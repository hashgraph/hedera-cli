import * as path from 'path';
import {
  TokenCreateTransaction,
  TokenType,
  PrivateKey,
  CustomFee,
} from '@hashgraph/sdk';

import accountUtils from '../../utils/account';
import tokenUtils from '../../utils/token';
import stateUtils from '../../utils/state';
import telemetryUtils from '../../utils/telemetry';
import feeUtils from '../../utils/fees';
import { Logger } from '../../utils/logger';
import stateController from '../../state/stateController';
import dynamicVariablesUtils from '../../utils/dynamicVariables';

import type {
  Account,
  Command,
  Token,
  Keys,
  CustomFeeInput,
  FixedFee,
  FractionalFee,
} from '../../../types';
import signUtils from '../../utils/sign';

const logger = Logger.getInstance();

interface CreateTokenFromFileOptions {
  file: string;
  args: string[];
}

interface TokenInput {
  name: string;
  symbol: string;
  decimals: number;
  supplyType: 'finite' | 'infinite';
  initialSupply: number;
  keys: Keys;
  maxSupply: number;
  treasuryId?: string;
  treasuryKey: string;
  customFees: CustomFeeInput[];
  memo: string;
}

function getTreasuryIdByTreasuryKey(treasuryKey: string): string {
  const account = accountUtils.findAccountByPrivateKey(treasuryKey);
  if (!account) {
    logger.error('Treasury account not found');
    process.exit(1);
  }
  return account.accountId;
}

async function createAccountForToken(
  key: string,
  initialBalance: number,
  type: string,
  alias: string,
): Promise<{ key: string; account: Account }> {
  const account = await accountUtils.createAccount(initialBalance, type, alias);
  return { key, account };
}

function resolveTokenFilePath(filename: string): string {
  return path.join(__dirname, '../..', 'input', `token.${filename}.json`);
}

function initializeToken(tokenInput: TokenInput): Token {
  const token: Token = {
    network: stateUtils.getNetwork(),
    associations: [],
    tokenId: '',
    name: tokenInput.name,
    symbol: tokenInput.symbol,
    treasuryId: tokenInput.treasuryId || '',
    decimals: tokenInput.decimals,
    initialSupply: tokenInput.initialSupply,
    supplyType: 'infinite',
    maxSupply: tokenInput.maxSupply || 0,
    keys: {
      adminKey: tokenInput.keys.adminKey,
      pauseKey: tokenInput.keys.pauseKey,
      kycKey: tokenInput.keys.kycKey,
      wipeKey: tokenInput.keys.wipeKey,
      freezeKey: tokenInput.keys.freezeKey,
      supplyKey: tokenInput.keys.supplyKey,
      feeScheduleKey: tokenInput.keys.feeScheduleKey,
      treasuryKey: tokenInput.keys.treasuryKey,
    },
    customFees: tokenInput.customFees,
  };

  return token;
}

/**
 * Find alias pattern in keys on token and replace with private key
 * @param keys
 * @return updated keys
 */
function replaceAliasPattern(keys: Keys): Keys {
  const accounts = stateController.get('accounts');
  const aliasPattern = /<alias:([a-zA-Z0-9_-]+)>/;
  let newKeys = { ...keys };

  Object.keys(newKeys).forEach((key) => {
    const match = newKeys[key as keyof typeof newKeys].match(aliasPattern);

    if (match) {
      const alias = match[1];
      if (accounts[alias]) {
        newKeys[key as keyof typeof newKeys] = accounts[alias].privateKey;
      }
    }
  });

  return newKeys;
}

/**
 * Create new accounts for keys that match the `newkey` pattern
 * and replace the pattern with the new private key
 *
 * @param keys
 * @returns promise array of new accounts
 */
function findNewKeyPattern(
  keys: Keys,
): Promise<{ key: string; account: Account }>[] {
  let newAccountPromises: Promise<{ key: string; account: Account }>[] = [];

  // Only allow ECDSA
  const newKeyPattern = /<newkey:(ecdsa|ECDSA):(\d+)>/;
  Object.keys(keys).forEach((key) => {
    const match = keys[key as keyof typeof keys].match(newKeyPattern);

    if (match) {
      const initialBalance = Number(match[2]); // Initial balance in tinybars
      newAccountPromises.push(
        createAccountForToken(key, initialBalance, 'ECDSA', 'random'), // Random alias because you can create an account upfront in scripts and give it an alias to be used in the template
      );
    } else if (
      /<newkey:(ed25519|ED25519):(\d+)>/.test(keys[key as keyof typeof keys])
    ) {
      logger.error(
        'ED25519 keys are no longer supported. Only ECDSA is allowed.',
      );
      process.exit(1);
    }
  });

  return newAccountPromises;
}

/**
 * Replace keys that match the `newkey` pattern with the new private key
 * @param keys
 * @returns updated keys
 */
async function handleNewKeyPattern(keys: Keys): Promise<Keys> {
  let newAccountPromises: Promise<{ key: string; account: Account }>[] =
    findNewKeyPattern(keys);

  // Create new accounts if pattern is detected
  let newKeys = { ...keys };
  if (newAccountPromises.length > 0) {
    try {
      const newAccounts = await Promise.all(newAccountPromises);
      newAccounts.forEach((newAccount) => {
        newKeys[newAccount.key as keyof typeof newKeys] =
          newAccount.account.privateKey;
      });
    } catch (error) {
      logger.error(
        'Failed to create new account(s) for token',
        error as object,
      );
      process.exit(1);
    }
  }

  return newKeys;
}

async function replaceKeysForToken(token: Token): Promise<Token> {
  let newToken = { ...token };

  // Look for alias pattern in keys on token
  newToken.keys = replaceAliasPattern(newToken.keys);

  // Look for `newkey` pattern in keys on token
  newToken.keys = await handleNewKeyPattern(newToken.keys);

  return newToken;
}

async function prepareTokenCreation(
  token: Token,
  tokenInput: TokenInput,
): Promise<Token> {
  token.supplyType = tokenInput.supplyType;
  token = await replaceKeysForToken(token);

  if (token.treasuryId === '') {
    token.treasuryId = getTreasuryIdByTreasuryKey(token.keys.treasuryKey);
  }

  return token;
}

function addKeysToTokenCreateTx(
  tokenCreateTx: TokenCreateTransaction,
  token: Token,
) {
  // Mapping key names to their corresponding setter methods
  const keySetters = {
    adminKey: tokenCreateTx.setAdminKey,
    pauseKey: tokenCreateTx.setPauseKey,
    kycKey: tokenCreateTx.setKycKey,
    wipeKey: tokenCreateTx.setWipeKey,
    freezeKey: tokenCreateTx.setFreezeKey,
    supplyKey: tokenCreateTx.setSupplyKey,
    feeScheduleKey: tokenCreateTx.setFeeScheduleKey,
  };

  Object.entries(keySetters).forEach(([key, setter]) => {
    const keyValue = token.keys[key as keyof typeof token.keys];
    if (keyValue && keyValue !== '') {
      setter.call(tokenCreateTx, PrivateKey.fromStringDer(keyValue).publicKey);
    }
  });
}

async function createTokenOnNetwork(token: Token) {
  const client = stateUtils.getHederaClient();

  try {
    const tokenCreateTx = new TokenCreateTransaction()
      .setTokenName(token.name)
      .setTokenSymbol(token.symbol)
      .setDecimals(token.decimals)
      .setInitialSupply(token.initialSupply)
      .setTokenType(TokenType.FungibleCommon)
      .setSupplyType(tokenUtils.getSupplyType(token.supplyType))
      .setTreasuryAccountId(token.treasuryId);

    if (token.supplyType === 'finite') {
      tokenCreateTx.setMaxSupply(token.maxSupply);
    }

    // Add keys
    addKeysToTokenCreateTx(tokenCreateTx, token);

    // Add custom fees
    let fees: CustomFee[] = token.customFees.map((fee) => {
      switch (fee.type) {
        case 'fixed':
          return feeUtils.createCustomFixedFee(fee as FixedFee);
        case 'fractional':
          return feeUtils.createCustomFractionalFee(fee as FractionalFee);
        default:
          logger.error(`Unsupported fee type: ${fee.type}`);
          client.close();
          process.exit(1);
      }
    });
    tokenCreateTx.setCustomFees(fees);

    // Signing
    tokenCreateTx.freezeWith(client);
    const signedTokenCreateTx = await signUtils.signByType(
      tokenCreateTx,
      'tokenCreate',
      {
        adminKey: token.keys.adminKey,
        treasuryKey: token.keys.treasuryKey,
      },
    );

    // Execute
    let tokenCreateSubmit = await signedTokenCreateTx.execute(client);
    let tokenCreateRx = await tokenCreateSubmit.getReceipt(client);

    if (tokenCreateRx.tokenId == null) {
      logger.error('Token was not created');
      client.close();
      process.exit(1);
    }

    token.tokenId = tokenCreateRx.tokenId.toString();
    logger.log(`Token ID: ${token.tokenId}`);
    client.close();
  } catch (error) {
    logger.error(error as object);
    client.close();
    process.exit(1);
  }
}

function updateTokenState(token: Token) {
  const tokens: Record<string, Token> = stateController.get('tokens');
  const updatedTokens = {
    ...tokens,
    [token.tokenId]: token,
  };

  stateController.saveKey('tokens', updatedTokens);
  stateUtils.getHederaClient().close();
}

async function createTokenFromFile(tokenInput: TokenInput): Promise<Token> {
  try {
    let token = initializeToken(tokenInput);
    token = await prepareTokenCreation(token, tokenInput);
    await createTokenOnNetwork(token);
    updateTokenState(token);
    return token;
  } catch (error) {
    logger.error(error as object);
    stateUtils.getHederaClient().close();
    process.exit(1);
  }
}

async function createToken(options: CreateTokenFromFileOptions) {
  logger.verbose(`Creating token from template with name: ${options.file}`);
  options = dynamicVariablesUtils.replaceOptions(options);

  const filepath = resolveTokenFilePath(options.file);
  const tokenDefinition = require(filepath);
  const token = await createTokenFromFile(tokenDefinition);

  // Store dynamic script variables
  dynamicVariablesUtils.storeArgs(
    options.args,
    dynamicVariablesUtils.commandActions.token.createFromFile.action,
    {
      tokenId: token.tokenId,
      name: token.name,
      symbol: token.symbol,
      treasuryId: token.treasuryId,
      adminKey: token.keys.adminKey,
      pauseKey: token.keys.pauseKey,
      kycKey: token.keys.kycKey,
      wipeKey: token.keys.wipeKey,
      freezeKey: token.keys.freezeKey,
      supplyKey: token.keys.supplyKey,
      feeScheduleKey: token.keys.feeScheduleKey,
      treasuryKey: token.keys.treasuryKey,
    },
  );
}

export default (program: any) => {
  program
    .command('create-from-file')
    .hook('preAction', async (thisCommand: Command) => {
      const command = [
        thisCommand.parent.action().name(),
        ...thisCommand.parent.args,
      ];
      if (stateUtils.isTelemetryEnabled()) {
        await telemetryUtils.recordCommand(command.join(' '));
      }
    })
    .description('Create a new token from a file')
    .requiredOption(
      '-f, --file <filename>',
      'Filename containing the token information',
    )
    .option(
      '--args <args>',
      'Store arguments for scripts',
      (value: string, previous: string) =>
        previous ? previous.concat(value) : [value],
      [],
    )
    .action(createToken);
};
