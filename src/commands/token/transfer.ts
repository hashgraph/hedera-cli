import { myParseInt } from '../../utils/verification';
import stateUtils from '../../utils/state';
import { telemetryPreAction } from '../shared/telemetryHook';
import { wrapAction } from '../shared/wrapAction';

import { Command } from 'commander';
import tokenUtils from '../../utils/token';

// logging handled via wrapAction configuration

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
    .action(
      wrapAction<TransferTokenOptions>(
        async (options) => {
          const tokenId = options.tokenId;
          const toIdOrName = options.to;
          const fromIdOrName = options.from;
          const balance = options.balance;

          const fromAccount = stateUtils.getAccountByIdOrName(fromIdOrName);
          const fromId = fromAccount.accountId;

          const toAccount = stateUtils.getAccountByIdOrName(toIdOrName);
          const toId = toAccount.accountId;

          await tokenUtils.transfer(
            tokenId,
            fromId,
            fromAccount.privateKey,
            toId,
            Number(balance),
          );
        },
        { log: (o) => `Transfering tokens from ${o.from} to ${o.to}` },
      ),
    );
};

interface TransferTokenOptions {
  tokenId: string;
  to: string;
  from: string;
  balance: number;
}
