import { Command } from 'commander';
import stateUtils from '../../utils/state';
import { telemetryPreAction } from '../shared/telemetryHook';
import { Logger } from '../../utils/logger';

const logger = Logger.getInstance();

export default (program: Command) => {
  program
    .command('use <name>')
    .hook('preAction', telemetryPreAction)
    .description('Switch to a specific network')
    .action((name: string) => {
      logger.verbose(`Switching to network: ${name}`);
      stateUtils.switchNetwork(name);
    });
};
