import stateUtils from '../../utils/state';
import scriptUtils from '../../utils/script';
import type { Command } from '../../../types';
import { Logger } from '../../utils/logger';

const logger = Logger.getInstance();

export default (program: any) => {
  program
    .command('list')
    .hook('preAction', (thisCommand: Command) => {
      const command = [
        thisCommand.parent.action().name(),
        ...thisCommand.parent.args,
      ];
      stateUtils.recordCommand(command);
    })
    .description('List all scripts')
    .action(() => {
      logger.verbose(`Listing all script names`);
      scriptUtils.listScripts();
    });
};
