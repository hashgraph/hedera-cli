import stateUtils from '../../utils/state';
import { Logger } from '../../utils/logger';

import type { Command } from '../../../types';

const logger = Logger.getInstance();

export default (program: any) => {
  program
    .command('use <name>')
    .hook('preAction', (thisCommand: Command) => {
      const command = [
        thisCommand.parent.action().name(),
        ...thisCommand.parent.args,
      ];
      stateUtils.recordCommand(command);
    })
    .description('Switch to a specific network')
    .action((name: string) => {
      logger.verbose(`Switching to network: ${name}`);
      stateUtils.switchNetwork(name);
    });
};
