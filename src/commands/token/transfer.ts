import { myParseInt } from '../../utils/verification';
import stateUtils from '../../utils/state';
import { telemetryPreAction } from '../shared/telemetryHook';
import dynamicVariablesUtils from '../../utils/dynamicVariables';
import { Logger } from '../../utils/logger';

import { Command } from 'commander';
import tokenUtils from '../../utils/token';

const logger = Logger.getInstance();

export default (program: Command) => {
  program
    .command('transfer')
    .hook('preAction', telemetryPreAction)
    .description('Transfer a fungible token')
    .requiredOption('-t, --token-id <tokenId>', 'Token ID to transfer')
    .requiredOption('--to <to>', 'Account ID to transfer token to')
    .requiredOption('--from <from>', 'Account ID to transfer token from')
    .requiredOption(
      '-b, --balance <balance>',
      'Amount of token to transfer',
      myParseInt,
    )
    .action(async (options: TransferTokenOptions) => {
      options = dynamicVariablesUtils.replaceOptions(options);
      logger.verbose(
        `Transfering tokens from ${options.from} to ${options.to}`,
      );

      const tokenId = options.tokenId;
      const toIdOrName = options.to;
      const fromIdOrName = options.from;
      const balance = options.balance;

      // Find sender account
      const fromAccount = stateUtils.getAccountByIdOrName(fromIdOrName);
      const fromId = fromAccount.accountId;

      // Find receiver account
      const toAccount = stateUtils.getAccountByIdOrName(toIdOrName);
      const toId = toAccount.accountId;

      await tokenUtils.transfer(
        tokenId,
        fromId,
        fromAccount.privateKey,
        toId,
        Number(balance),
      );
    });
};

interface TransferTokenOptions {
  tokenId: string;
  to: string;
  from: string;
  balance: number;
}
