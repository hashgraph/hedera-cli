import stateUtils from '../utils/state';
import telemetryUtils from '../utils/telemetry';
import { Logger } from '../utils/logger';

import { Command } from 'commander';

const logger = Logger.getInstance();

async function wait(seconds: number) {
  await new Promise((resolve) => setTimeout(resolve, seconds * 1000));
}

export default (program: Command) => {
  program
    .command('wait <seconds>')
    .hook('preAction', async (thisCommand: Command) => {
      const parentName = thisCommand.parent?.name() || 'unknown';
      const command = [parentName, ...(thisCommand.parent?.args ?? [])];
      if (stateUtils.isTelemetryEnabled()) {
        await telemetryUtils.recordCommand(command.join(' '));
      }
    })
    .description('Wait for a specified number of seconds')
    .action(async (seconds: string) => {
      logger.verbose(`Waiting for ${seconds} seconds`);
      await wait(Number(seconds));
    });
};
