import stateUtils from '../../utils/state';
import accountUtils from '../../utils/account';
import telemetryUtils from '../../utils/telemetry';
import dynamicVariablesUtils from '../../utils/dynamicVariables';
import { Logger } from '../../utils/logger';
import { Command } from 'commander';

const logger = Logger.getInstance();

export default (program: Command) => {
  program
    .command('import')
    .hook('preAction', async (thisCommand: Command) => {
      const parentName = thisCommand.parent?.name() || 'unknown';
      const command = [parentName, ...(thisCommand.parent?.args ?? [])];
      if (stateUtils.isTelemetryEnabled()) {
        await telemetryUtils.recordCommand(command.join(' '));
      }
    })
    .description(
      'Import an existing account using an account ID, name, type, and optional private key.',
    )
    .requiredOption('-n, --name <name>', 'account must have a name')
    .requiredOption('-i, --id <id>', 'Account ID')
    .option('-k, --key <key>', 'Private key')
    .option(
      '--args <args>',
      'Store arguments for scripts',
      (value: string, previous: string[]) =>
        previous ? previous.concat(value) : [value],
      [] as string[],
    )
    .action((options: ImportAccountOptions) => {
      options = dynamicVariablesUtils.replaceOptions(options);
      logger.verbose(`Importing account with name: ${options.name}`);

      let accountDetails;
      if (options.key) {
        accountDetails = accountUtils.importAccount(
          options.id,
          options.key,
          options.name,
        );
      } else {
        accountDetails = accountUtils.importAccountId(options.id, options.name);
      }

      dynamicVariablesUtils.storeArgs(
        options.args,
        dynamicVariablesUtils.commandActions.account.import.action,
        accountDetails,
      );
    });
};

interface ImportAccountOptions {
  name: string;
  id: string;
  key: string;
  args: string[];
}
