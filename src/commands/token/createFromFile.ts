import * as path from "path";
import {
  TokenCreateTransaction,
  TokenType,
  PrivateKey,
  TokenSupplyType,
} from "@hashgraph/sdk";

import accountUtils from "../../utils/account";
import { getSupplyType } from "../../utils/token";
import { recordCommand, getHederaClient } from "../../state/stateService";
import { Logger } from "../../utils/logger";
import { saveStateAttribute, getState } from "../../state/stateController";

import type { Account, Command, Token, Keys } from "../../../types";

const logger = Logger.getInstance();

export default (program: any) => {
  program
    .command("create-from-file")
    .hook("preAction", (thisCommand: Command) => {
      const command = [
        thisCommand.parent.action().name(),
        ...thisCommand.parent.args,
      ];
      recordCommand(command);
    })
    .description("Create a new token from a file")
    .requiredOption(
      "-f, --file <filename>",
      "Filename containing the token information"
    )
    .action(createTokenFromCLI);
};

async function createTokenFromCLI(options: CreateTokenFromFileOptions) {
  try {
    const filepath = resolveTokenFilePath(options.file);
    const tokenDefinition = require(filepath);
    await createTokenFromFile(tokenDefinition);
  } catch (error) {
    logger.error(error as object);
  }
}

function resolveTokenFilePath(filename: string): string {
  return path.join(__dirname, "../..", "input", `token.${filename}.json`);
}

function initializeToken(tokenInput: TokenInput): Token {
  const token: Token = {
    associations: [],
    tokenId: "",
    name: tokenInput.name,
    symbol: tokenInput.symbol,
    treasuryId: tokenInput.treasuryId || "",
    decimals: tokenInput.decimals,
    initialSupply: tokenInput.initialSupply,
    supplyType: TokenSupplyType.Infinite,
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
  };

  return token;
}

async function prepareTokenCreation(
  token: Token,
  tokenInput: TokenInput
): Promise<Token> {
  token.supplyType = getSupplyType(tokenInput.supplyType);
  token = await replaceKeysForToken(token);

  if (token.treasuryId === "") {
    token.treasuryId = getTreasuryIdByTreasuryKey(token.keys.treasuryKey);
  }

  return token;
}

async function createTokenOnNetwork(token: Token) {
  const client = getHederaClient();
  try {
    const tokenCreateTx = new TokenCreateTransaction()
      .setTokenName(token.name)
      .setTokenSymbol(token.symbol)
      .setDecimals(token.decimals)
      .setInitialSupply(token.initialSupply)
      .setTokenType(TokenType.FungibleCommon)
      .setSupplyType(token.supplyType)
      .setTreasuryAccountId(token.treasuryId);

    if (token.supplyType === TokenSupplyType.Finite) {
      tokenCreateTx.setMaxSupply(token.maxSupply);
    }

    // Add keys
    addKeysToTokenCreateTx(tokenCreateTx, token);
  
    // Signing
    tokenCreateTx
      .freezeWith(client)
      .sign(PrivateKey.fromString(token.keys.treasuryKey));

    if (token.keys.adminKey !== "") {
      tokenCreateTx.sign(PrivateKey.fromString(token.keys.adminKey));
    }

    // Execute
    let tokenCreateSubmit = await tokenCreateTx.execute(client);
    let tokenCreateRx = await tokenCreateSubmit.getReceipt(client);

    if (tokenCreateRx.tokenId == null) throw new Error("Token was not created");

    token.tokenId = tokenCreateRx.tokenId.toString();
    console.log("Token ID:", token.tokenId);
    client.close();
  } catch (error) {
    logger.error(error as object);
    client.close();
    return;
  }
}

function addKeysToTokenCreateTx(tokenCreateTx: TokenCreateTransaction, token: Token) {
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
    if (keyValue && keyValue !== "") {
      setter.call(tokenCreateTx, PrivateKey.fromString(keyValue).publicKey);
    }
  }); 
}

async function createTokenFromFile(tokenInput: TokenInput) {
  try {
    let token = initializeToken(tokenInput);
    token = await prepareTokenCreation(token, tokenInput);
    await createTokenOnNetwork(token);
    updateTokenState(token);
  } catch (error) {
    logger.error(error as object);
    getHederaClient().close();
  }
}

function updateTokenState(token: Token) {
  const tokens: Record<string, Token> = getState("tokens");
  const updatedTokens = {
    ...tokens,
    [token.tokenId]: token,
  };

  saveStateAttribute("tokens", updatedTokens);
  getHederaClient().close();
}

async function replaceKeysForToken(token: Token): Promise<Token> {
  let newToken = { ...token };

  // Look for alias pattern in keys on token
  newToken.keys = replaceAliasPattern(newToken.keys);

  // Look for `newkey` pattern in keys on token
  newToken.keys = await handleNewKeyPattern(newToken.keys);

  return newToken;
}

/**
 * Find alias pattern in keys on token and replace with private key
 * @param keys
 * @return updated keys
 */
function replaceAliasPattern(keys: Keys): Keys {
  const accounts = getState("accounts");
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
  keys: Keys
): Promise<{ key: string; account: Account }>[] {
  let newAccountPromises: Promise<{ key: string; account: Account }>[] = [];

  const newKeyPattern = /<newkey:(ecdsa|ECDSA|ed25519|ED25519):(\d+)>/;
  Object.keys(keys).forEach(key => {
    const match = keys[key as keyof typeof keys].match(newKeyPattern);

    if (match) {
      const keyType = match[1]; // 'ecdsa' or 'ed25519' (can be capitals)
      const initialBalance = Number(match[2]); // Initial balance in tinybars
      newAccountPromises.push(
        createAccountForToken(key, initialBalance, keyType, "random") // Random alias because you can create an account upfront in scripts and give it an alias to be used in the template
      );
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
      logger.error(error as object);
      throw new Error(
        `Failed to create new accounts for token`
      );
    }
  }

  return newKeys;
}

function getTreasuryIdByTreasuryKey(treasuryKey: string): string {
  const account = accountUtils.findAccountByPrivateKey(treasuryKey);
  if (!account) {
    throw new Error("Treasury account not found");
  }
  return account.accountId;
}

async function createAccountForToken(
  key: string,
  initialBalance: number,
  type: string,
  alias: string
): Promise<{ key: string; account: Account }> {
  const account = await accountUtils.createAccount(initialBalance, type, alias);
  return { key, account };
}

interface CreateTokenFromFileOptions {
  file: string;
}

interface TokenInput {
  name: string;
  symbol: string;
  decimals: number;
  supplyType: "finite" | "infinite";
  initialSupply: number;
  keys: Keys;
  maxSupply: number;
  treasuryId?: string;
  treasuryKey: string;
  customFees: [];
  memo: string;
}
