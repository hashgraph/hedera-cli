import { Command } from 'commander';
import stateUtils from '../../utils/state';
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
    .description('List all available networks')
    .action(() => {
      logger.verbose('Listing networks');
      logger.log('Available networks:');
      stateUtils.getAvailableNetworks().forEach((network) => {
        logger.log(`- ${network}`);
      });
    });
};
