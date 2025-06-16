import stateUtils from '../../utils/state';
import { Logger } from '../../utils/logger';
import telemetryUtils from '../../utils/telemetry';

import type { Command } from '../../../types';

const logger = Logger.getInstance();

export default (program: any) => {
  program
    .command('compile')
    .hook('preAction', async (thisCommand: Command) => {
      const command = [
        thisCommand.parent.action().name(),
        ...thisCommand.parent.args,
      ];
      if (stateUtils.isTelemetryEnabled()) {
        await telemetryUtils.recordCommand(command.join(' '));
      }
    })
    .description('Compile smart contracts in the "contracts" directory')
    .action(async () => {
      logger.verbose('Compiling smart contracts');

      try {
        const hre = require('hardhat');
        await hre.run('compile');
        logger.log('Contracts compiled successfully.');
      } catch (error) {
        logger.error('Failed to compile contracts:', error as object);
      }
    });
};
