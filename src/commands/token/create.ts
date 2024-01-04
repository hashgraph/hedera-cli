import { TokenCreateTransaction, TokenType, PrivateKey } from '@hashgraph/sdk';

import { myParseInt } from '../../utils/verification';
import { getSupplyType } from '../../utils/token';
import {
  recordCommand,
  getHederaClient,
  getNetwork,
} from '../../state/stateService';
import { Logger } from '../../utils/logger';
import stateController from '../../state/stateController';

import type { Command, Token } from '../../../types';
import dynamicVariablesUtils from '../../utils/dynamicVariables';

const logger = Logger.getInstance();

export default (program: any) => {
  program
    .command('create')
    .hook('preAction', (thisCommand: Command) => {
      const command = [
        thisCommand.parent.action().name(),
        ...thisCommand.parent.args,
      ];
      recordCommand(command);
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
      (value: string, previous: string) =>
        previous ? previous.concat(value) : [value],
      [],
    )
    .action(async (options: CreateOptions) => {
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
          tokenId: tokenId.toString(),
          name: options.name,
          symbol: options.symbol,
          treasuryId: options.treasuryId,
          adminKey: options.adminKey,
        },
      );
    });
};

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
  const client = getHederaClient();

  let tokenId;
  try {
    let tokenCreateTx = await new TokenCreateTransaction()
      .setTokenName(name)
      .setTokenSymbol(symbol)
      .setDecimals(decimals)
      .setInitialSupply(initialSupply)
      .setTokenType(TokenType.FungibleCommon)
      .setSupplyType(getSupplyType(supplyType))
      .setTreasuryAccountId(treasuryId)
      .setAdminKey(PrivateKey.fromStringDer(adminKey).publicKey)
      .freezeWith(client)
      .sign(PrivateKey.fromStringDer(treasuryKey));

    let tokenCreateTxSigned = await tokenCreateTx.sign(
      PrivateKey.fromStringDer(adminKey),
    );
    let tokenCreateSubmit = await tokenCreateTxSigned.execute(client);
    let tokenCreateRx = await tokenCreateSubmit.getReceipt(client);
    tokenId = tokenCreateRx.tokenId;

    if (tokenId == null) {
      logger.error('Token was not created');
      process.exit(1);
    }

    logger.log(`Token ID: ${tokenId.toString()}`);
  } catch (error) {
    logger.error(error as object);
    client.close();
    process.exit(1);
  }

  // Store new token in state
  logger.verbose(`Storing new token with ID ${tokenId} in state`);
  const tokens: Record<string, Token> = stateController.get('tokens');
  const updatedTokens = {
    ...tokens,
    [tokenId.toString()]: {
      tokenId: tokenId.toString(),
      associations: [],
      name,
      symbol,
      treasuryId,
      treasuryKey,
      decimals,
      initialSupply,
      adminKey,
      network: getNetwork(),
    },
  };

  stateController.saveKey('tokens', updatedTokens);

  client.close();
  return tokenId.toString();
}

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
