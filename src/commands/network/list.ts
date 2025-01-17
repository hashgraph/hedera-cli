import stateUtils from '../../utils/state';
import { Logger } from '../../utils/logger';

import type { Command } from '../../../types';

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
    .description('List all available networks')
    .action(() => {
      logger.verbose('Listing networks');
      logger.log('Available networks:');
      stateUtils.getAvailableNetworks().forEach((network) => {
        logger.log(`- ${network}`);
      });
    });
};
