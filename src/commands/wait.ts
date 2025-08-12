import { telemetryPreAction } from './shared/telemetryHook';
import { Logger } from '../utils/logger';
import { exitOnError } from '../utils/errors';

import { Command } from 'commander';

const logger = Logger.getInstance();

async function wait(seconds: number) {
  await new Promise((resolve) => setTimeout(resolve, seconds * 1000));
}

export default (program: Command) => {
  program
    .command('wait <seconds>')
    .hook('preAction', telemetryPreAction)
    .description('Wait for a specified number of seconds')
    .action(
      exitOnError(async (seconds: string) => {
        logger.verbose(`Waiting for ${seconds} seconds`);
        await wait(Number(seconds));
      }),
    );
};
