import stateUtils from '../../utils/state';
import scriptUtils from '../../utils/script';
import telemetryUtils from '../../utils/telemetry';
import { Logger } from '../../utils/logger';
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
