import { Command } from 'commander';
import { TokenCreateTransaction, TokenType, PrivateKey } from '@hashgraph/sdk';
import { myParseInt } from '../../utils/verification';
import tokenUtils from '../../utils/token';
import stateUtils from '../../utils/state';
import telemetryUtils from '../../utils/telemetry';
import { Logger } from '../../utils/logger';
import { DomainError, exitOnError } from '../../utils/errors';
import { addToken } from '../../state/mutations';
import type { Token } from '../../../types/state';
import dynamicVariablesUtils from '../../utils/dynamicVariables';
import signUtils from '../../utils/sign';

const logger = Logger.getInstance();

interface CreateOptions {
  name: string;
  symbol: string;
  treasuryId: string;
  treasuryKey: string;
  decimals: number;
  initialSupply: number;
  supplyType: 'finite' | 'infinite';
  adminKey: string;
  args: string[];
}

async function createFungibleToken(
  name: string,
  symbol: string,
  treasuryId: string,
  treasuryKey: string,
  decimals: number,
  initialSupply: number,
  supplyType: string,
  adminKey: string,
): Promise<string> {
  const client = stateUtils.getHederaClient();

  let tokenId: string | undefined;
  try {
    const tokenCreateTx = new TokenCreateTransaction()
      .setTokenName(name)
      .setTokenSymbol(symbol)
      .setDecimals(decimals)
      .setInitialSupply(initialSupply)
      .setTokenType(TokenType.FungibleCommon)
      .setSupplyType(tokenUtils.getSupplyType(supplyType))
      .setTreasuryAccountId(treasuryId)
      .setAdminKey(PrivateKey.fromStringDer(adminKey).publicKey)
      .freezeWith(client);
    const tokenCreateTxSigned = await signUtils.signByType(
      tokenCreateTx,
      'tokenCreate',
      {
        adminKey: adminKey,
        treasuryKey: treasuryKey,
      },
    );
    const tokenCreateSubmit = await tokenCreateTxSigned.execute(client);
    const tokenCreateRx = await tokenCreateSubmit.getReceipt(client);
    tokenId = tokenCreateRx.tokenId?.toString();

    if (tokenId == null) {
      throw new DomainError('Token was not created');
    }

    logger.log(`Token ID: ${tokenId}`);
  } catch (error) {
    client.close();
    throw new DomainError('Failed to create token');
  }

  // Store new token in state
  logger.verbose(`Storing new token with ID ${tokenId} in state`);
  const newToken: Token = {
    tokenId: tokenId,
    associations: [],
    name,
    symbol,
    treasuryId,
    decimals,
    supplyType: supplyType.toUpperCase(),
    maxSupply: supplyType.toUpperCase() === 'FINITE' ? initialSupply : 0,
    initialSupply,
    keys: {
      treasuryKey,
      adminKey,
      supplyKey: '',
      wipeKey: '',
      kycKey: '',
      freezeKey: '',
      pauseKey: '',
      feeScheduleKey: '',
    },
    network: stateUtils.getNetwork(),
    customFees: [],
  };
  addToken(newToken, false);

  client.close();
  return tokenId;
}

export default (program: Command) => {
  program
    .command('create')
    .hook('preAction', async (thisCommand: Command) => {
      const parentName = thisCommand.parent?.name() || 'unknown';
      const command = [parentName, ...(thisCommand.parent?.args ?? [])];
      if (stateUtils.isTelemetryEnabled()) {
        await telemetryUtils.recordCommand(command.join(' '));
      }
    })
    .description('Create a new fungible token')
    .requiredOption(
      '-t, --treasury-id <treasuryId>',
      'Treasury of the fungible token',
    )
    .requiredOption(
      '-k, --treasury-key <treasuryKey>',
      'Treasury of the fungible token',
    )
    .requiredOption('-n, --name <name>', 'Name of the fungible token')
    .requiredOption('-s, --symbol <symbol>', 'Symbol of the fungible token')
    .requiredOption(
      '-d, --decimals <decimals>',
      'Decimals of the fungible token',
      myParseInt,
    )
    .requiredOption(
      '-i, --initial-supply <initialSupply>',
      'Initial supply of the fungible token',
      myParseInt,
    )
    .requiredOption(
      '--supply-type <supplyType>',
      'Supply type of the token: finite or infinite',
    )
    .requiredOption(
      '-a, --admin-key <adminKey>',
      'Admin key of the fungible token',
    )
    .option(
      '--args <args>',
      'Store arguments for scripts',
      (value: string, previous: string[]) =>
        previous ? [...previous, value] : [value],
      [] as string[],
    )
    .action(
      exitOnError(async (options: CreateOptions) => {
        logger.verbose('Creating new token');
        options = dynamicVariablesUtils.replaceOptions(options);
        const tokenId = await createFungibleToken(
          options.name,
          options.symbol,
          options.treasuryId,
          options.treasuryKey,
          options.decimals,
          options.initialSupply,
          options.supplyType,
          options.adminKey,
        );

        dynamicVariablesUtils.storeArgs(
          options.args,
          dynamicVariablesUtils.commandActions.token.create.action,
          {
            tokenId: tokenId,
            name: options.name,
            symbol: options.symbol,
            treasuryId: options.treasuryId,
            adminKey: options.adminKey,
          },
        );
      }),
    );
};
