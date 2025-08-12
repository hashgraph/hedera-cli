import accountUtils from '../../utils/account';
import { DomainError } from '../../utils/errors';
import { telemetryPreAction } from '../shared/telemetryHook';
import { wrapAction } from '../shared/wrapAction';

import { Command } from 'commander';

// logging handled via wrapAction config

export default (program: Command) => {
  program
    .command('balance')
    .hook('preAction', telemetryPreAction)
    .description('Retrieve the balance for an account ID or name')
    .requiredOption(
      '-a, --account-id-or-name <accountIdOrName>',
      '(Required) Account ID or account name to retrieve balance for',
    )
    .option('-h, --only-hbar', 'Show only Hbar balance')
    .option('-t, --token-id <tokenId>', 'Show balance for a specific token ID')
    .action(
      wrapAction<GetAccountBalanceOptions>(
        async (options) => {
          if (options.onlyHbar && options.tokenId) {
            throw new DomainError(
              'You cannot use both --only-hbar and --token-id options at the same time.',
            );
          }
          await accountUtils.getAccountBalance(
            options.accountIdOrName,
            options.onlyHbar,
            options.tokenId,
          );
        },
        { log: (o) => `Getting balance for ${o.accountIdOrName}` },
      ),
    );
};

interface GetAccountBalanceOptions {
  onlyHbar: boolean;
  tokenId: string;
  accountIdOrName: string;
}
