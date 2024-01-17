import { myParseInt } from '../../utils/verification';
import stateUtils from '../../utils/state';
import dynamicVariablesUtils from '../../utils/dynamicVariables';
import { Logger } from '../../utils/logger';

import type { Command } from '../../../types';
import tokenUtils from '../../utils/token';

const logger = Logger.getInstance();

export default (program: any) => {
  program
    .command('transfer')
    .hook('preAction', (thisCommand: Command) => {
      const command = [
        thisCommand.parent.action().name(),
        ...thisCommand.parent.args,
      ];
      stateUtils.recordCommand(command);
    })
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
      const toIdOrAlias = options.to;
      const fromIdOrAlias = options.from;
      const balance = options.balance;

      // Find sender account
      let fromAccount = stateUtils.getAccountByIdOrAlias(fromIdOrAlias);
      let fromId = fromAccount.accountId;

      // Find receiver account
      let toAccount = stateUtils.getAccountByIdOrAlias(toIdOrAlias);
      let toId = toAccount.accountId;

      await tokenUtils.transfer(tokenId, fromId, fromAccount.privateKey, toId, Number(balance))
    });
};

interface TransferTokenOptions {
  tokenId: string;
  to: string;
  from: string;
  balance: number;
}
