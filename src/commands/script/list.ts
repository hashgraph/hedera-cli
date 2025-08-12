import { Command } from 'commander';
import stateUtils from '../../utils/state';
import scriptUtils from '../../utils/script';
import telemetryUtils from '../../utils/telemetry';
import { Logger } from '../../utils/logger';

const logger = Logger.getInstance();

export default (program: Command) => {
  program
    .command('list')
    .hook('preAction', async (thisCommand: Command) => {
      const parentName = thisCommand.parent?.name() || 'unknown';
      const command = [parentName, ...(thisCommand.parent?.args ?? [])];
      if (stateUtils.isTelemetryEnabled()) {
        await telemetryUtils.recordCommand(command.join(' '));
      }
    })
    .description('List all scripts')
    .action(() => {
      logger.verbose(`Listing all script names`);
      scriptUtils.listScripts();
    });
};
