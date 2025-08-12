import { Command } from 'commander';
import stateUtils from '../../utils/state';
import scriptUtils from '../../utils/script';
import telemetryUtils from '../../utils/telemetry';
import { Logger } from '../../utils/logger';
import dynamicVariablesUtils from '../../utils/dynamicVariables';

const logger = Logger.getInstance();

export default (program: Command) => {
  program
    .command('delete')
    .hook('preAction', async (thisCommand: Command) => {
      const parentName = thisCommand.parent?.name() || 'unknown';
      const command = [parentName, ...(thisCommand.parent?.args ?? [])];
      if (stateUtils.isTelemetryEnabled()) {
        await telemetryUtils.recordCommand(command.join(' '));
      }
    })
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
