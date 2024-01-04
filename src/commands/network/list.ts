import { recordCommand } from '../../state/stateService';
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
      recordCommand(command);
    })
    .description('List all available networks')
    .action(() => {
      logger.verbose('Listing networks');
      logger.log('Available networks: mainnet, testnet, previewnet');
    });
};
