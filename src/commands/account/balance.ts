import stateUtils from '../../utils/state';
import { Logger } from '../../utils/logger';
import accountUtils from '../../utils/account';
import dynamicVariablesUtils from '../../utils/dynamicVariables';
import telemetryUtils from '../../utils/telemetry';

import type { Command } from '../../../types';

const logger = Logger.getInstance();

export default (program: any) => {
  program
    .command('balance')
    .hook('preAction', async (thisCommand: Command) => {
      const command = [
        thisCommand.parent.action().name(),
        ...thisCommand.parent.args,
      ];
      if (stateUtils.isTelemetryEnabled()) {
        await telemetryUtils.recordCommand(command.join(' '));
      }
    })
    .description('Retrieve the balance for an account ID or name')
    .requiredOption(
      '-a, --account-id-or-name <accountIdOrName>',
      '(Required) Account ID or account name to retrieve balance for',
    )
    .option('-h, --only-hbar', 'Show only Hbar balance')
    .option('-t, --token-id <tokenId>', 'Show balance for a specific token ID')
    .action(async (options: GetAccountBalanceOptions) => {
      logger.verbose(`Getting balance for ${options.accountIdOrName}`);
      options = dynamicVariablesUtils.replaceOptions(options);

      if (options.onlyHbar && options.tokenId) {
        logger.error(
          'You cannot use both --only-hbar and --token-id options at the same time.',
        );
        process.exit(1);
      }

      await accountUtils.getAccountBalance(
        options.accountIdOrName,
        options.onlyHbar,
        options.tokenId,
      );
    });
};

interface GetAccountBalanceOptions {
  onlyHbar: boolean;
  tokenId: string;
  accountIdOrName: string;
}
