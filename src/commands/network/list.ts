import { Command } from 'commander';
import stateUtils from '../../utils/state';
import { telemetryPreAction } from '../shared/telemetryHook';
import { Logger } from '../../utils/logger';

const logger = Logger.getInstance();

export default (program: Command) => {
  program
    .command('list')
    .hook('preAction', telemetryPreAction)
    .description('List all available networks')
    .action(() => {
      logger.verbose('Listing networks');
      logger.log('Available networks:');
      stateUtils.getAvailableNetworks().forEach((network) => {
        logger.log(`- ${network}`);
      });
    });
};
