import stateUtils from '../../utils/state';
import accountUtils from '../../utils/account';
import telemetryUtils from '../../utils/telemetry';
import dynamicVariablesUtils from '../../utils/dynamicVariables';
import { Logger } from '../../utils/logger';

import type { Command } from '../../../types';

const logger = Logger.getInstance();

export default (program: any) => {
  program
    .command('import')
    .hook('preAction', async (thisCommand: Command) => {
      const command = [
        thisCommand.parent.action().name(),
        ...thisCommand.parent.args,
      ];
      if (stateUtils.isTelemetryEnabled()) {
        await telemetryUtils.recordCommand(command.join(' '));
      }
      stateUtils.recordCommand(command);
    })
    .description(
      'Import an existing account using an account ID, alias, type, and optional private key.',
    )
    .requiredOption('-a, --alias <alias>', 'account must have an alias')
    .requiredOption('-i, --id <id>', 'Account ID')
    .option('-k, --key <key>', 'Private key')
    .option(
      '--args <args>',
      'Store arguments for scripts',
      (value: string, previous: string) =>
        previous ? previous.concat(value) : [value],
      [],
    )
    .action((options: ImportAccountOptions) => {
      options = dynamicVariablesUtils.replaceOptions(options);
      logger.verbose(`Importing account with alias: ${options.alias}`);

      let accountDetails;
      if (options.key) {
        accountDetails = accountUtils.importAccount(
          options.id,
          options.key,
          options.alias,
        );
      } else {
        accountDetails = accountUtils.importAccountId(
          options.id,
          options.alias,
        );
      }

      dynamicVariablesUtils.storeArgs(
        options.args,
        dynamicVariablesUtils.commandActions.account.import.action,
        accountDetails,
      );
    });
};

interface ImportAccountOptions {
  alias: string;
  id: string;
  key: string;
  args: string[];
}
