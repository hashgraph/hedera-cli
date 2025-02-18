import stateUtils from '../../utils/state';
import { Logger } from '../../utils/logger';

import accountUtils from '../../utils/account';
import telemetryUtils from '../../utils/telemetry';
import dynamicVariablesUtils from '../../utils/dynamicVariables';

import type { Command } from '../../../types';

const logger = Logger.getInstance();

export default (program: any) => {
  program
    .command('delete')
    .hook('preAction', async (thisCommand: Command) => {
      const command = [
        thisCommand.parent.action().name(),
        ...thisCommand.parent.args,
      ];
      if (stateUtils.isTelemetryEnabled()) {
        await telemetryUtils.recordCommand(command.join(' '));
      }
    })
    .description('Delete an account from the address book')
    .option('-a, --alias <alias>', 'account must have an alias')
    .option('-i, --id <id>', 'Account ID')
    .action((options: AccountDeleteOptions) => {
      options = dynamicVariablesUtils.replaceOptions(options);

      const accountIdOrAlias = options.id || options.alias;
      logger.verbose(`Deleting account with alias or ID: ${accountIdOrAlias}`);
      if (!accountIdOrAlias) {
        logger.error('You must provide either an account ID or an alias.');
        process.exit(1);
      }

      accountUtils.deleteAccount(accountIdOrAlias);
    });
};

interface AccountDeleteOptions {
  alias?: string;
  id?: string;
}
