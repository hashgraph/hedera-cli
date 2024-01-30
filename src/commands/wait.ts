import stateUtils from '../utils/state';
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
      stateUtils.recordCommand(command);
    })
    .description('Wait for a specified number of seconds')
    .action(async (seconds: string) => {
      logger.verbose(`Waiting for ${seconds} seconds`);
      await wait(Number(seconds));
    });
};

async function wait(seconds: number) {
  await new Promise((resolve) => setTimeout(resolve, (seconds * 1000)));
}
