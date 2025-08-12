import { Command } from 'commander';
import scriptUtils from '../../utils/script';
import { telemetryPreAction } from '../shared/telemetryHook';
import { Logger } from '../../utils/logger';
import dynamicVariablesUtils from '../../utils/dynamicVariables';

const logger = Logger.getInstance();

export default (program: Command) => {
  program
    .command('delete')
    .hook('preAction', telemetryPreAction)
    .description('Delete a script')
    .requiredOption('-n, --name <name>', 'Name of script to delete')
    .action((options: ScriptDeleteOptions) => {
      options = dynamicVariablesUtils.replaceOptions(options);
      logger.verbose(`Deleting script: ${options.name}`);

      scriptUtils.deleteScript(options.name);
    });
};

interface ScriptDeleteOptions {
  name: string;
}
