import { Command } from 'commander';
import scriptUtils from '../../utils/script';
import { telemetryPreAction } from '../shared/telemetryHook';
import { Logger } from '../../utils/logger';

const logger = Logger.getInstance();

export default (program: Command) => {
  program
    .command('list')
    .hook('preAction', telemetryPreAction)
    .description('List all scripts')
    .action(() => {
      logger.verbose(`Listing all script names`);
      scriptUtils.listScripts();
    });
};
