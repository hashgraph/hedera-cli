import { Logger } from '../../utils/logger';
import accountUtils from '../../utils/account';
import { DomainError, exitOnError } from '../../utils/errors';
import dynamicVariablesUtils from '../../utils/dynamicVariables';
import { telemetryPreAction } from '../shared/telemetryHook';

import { Command } from 'commander';

const logger = Logger.getInstance();

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
      exitOnError(async (options: GetAccountBalanceOptions) => {
        logger.verbose(`Getting balance for ${options.accountIdOrName}`);
        options = dynamicVariablesUtils.replaceOptions(options);

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
      }),
    );
};

interface GetAccountBalanceOptions {
  onlyHbar: boolean;
  tokenId: string;
  accountIdOrName: string;
}
