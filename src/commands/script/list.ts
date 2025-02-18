import stateUtils from '../../utils/state';
import scriptUtils from '../../utils/script';
import telemetryUtils from '../../utils/telemetry';
import type { Command } from '../../../types';
import { Logger } from '../../utils/logger';

const logger = Logger.getInstance();

export default (program: any) => {
  program
    .command('list')
    .hook('preAction', async (thisCommand: Command) => {
      const command = [
        thisCommand.parent.action().name(),
        ...thisCommand.parent.args,
      ];
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
