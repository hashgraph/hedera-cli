import { Command } from 'commander';
import stateUtils from '../../utils/state';
import telemetryUtils from '../../utils/telemetry';
import { Logger } from '../../utils/logger';

const logger = Logger.getInstance();

export default (program: Command) => {
  program
    .command('use <name>')
    .hook('preAction', async (thisCommand: Command) => {
      const parentName = thisCommand.parent?.name() || 'unknown';
      const command = [parentName, ...(thisCommand.parent?.args ?? [])];
      if (stateUtils.isTelemetryEnabled()) {
        await telemetryUtils.recordCommand(command.join(' '));
      }
    })
    .description('Switch to a specific network')
    .action((name: string) => {
      logger.verbose(`Switching to network: ${name}`);
      stateUtils.switchNetwork(name);
    });
};
