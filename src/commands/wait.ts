import { recordCommand } from '../state/stateService';
import { Logger } from '../utils/logger';

import type { Command } from '../../types';

const logger = Logger.getInstance();

export default (program: any) => {
  program
    .command('wait <seconds>')
    .hook('preAction', (thisCommand: Command) => {
      const command = [
        thisCommand.parent.action().name(),
        ...thisCommand.parent.args,
      ];
      recordCommand(command);
    })
    .description('Wait for a specified number of seconds')
    .action((seconds: string) => {
      logger.verbose(`Waiting for ${seconds} seconds`);
      wait(seconds);
    });
};

function wait(seconds: string) {
  const ms = Number(seconds) * 1000;
  setTimeout(() => {}, ms);
}
