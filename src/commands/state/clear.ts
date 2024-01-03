import { recordCommand } from '../../state/stateService';
import type { Command } from '../../../types';
import { Logger } from '../../utils/logger';
import stateController from '../../state/stateController';

const logger = Logger.getInstance();

export default (program: any) => {
  program
    .command('clear')   
    .hook('preAction', (thisCommand: Command) => {
      const command = [
        thisCommand.parent.action().name(),
        ...thisCommand.parent.args,
      ];
      recordCommand(command);
    })
    .description('Clear all state and reset to default')
    .action(() => {
      logger.verbose('Clearing state');
      
      //stateController.clearState();
    });
};
