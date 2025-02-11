import stateUtils from '../../utils/state';
import telemetryUtils from '../../utils/telemetry';
import { Logger } from '../../utils/logger';

import type { Command } from '../../../types';

const logger = Logger.getInstance();

export default (program: any) => {
  program
    .command('download')
    .hook('preAction', async (thisCommand: Command) => {
      const command = [
        thisCommand.parent.action().name(),
        ...thisCommand.parent.args,
      ];
      if (stateUtils.isTelemetryEnabled()) {
        await telemetryUtils.recordCommand(command.join(' '));
      }
      stateUtils.recordCommand(command);
    })
    .description(
      'Download state from a URL and merge it with the current state',
    )
    .requiredOption('--url <url>', 'URL of script to download')
    .option('--merge', 'Merge state with downloaded state', false)
    .option('--overwrite', 'Overwrite state with downloaded state', false)
    .action(async (options: DownloadStateOptions) => {
      logger.verbose(`Downloading state from ${options.url}`);

      if (options.merge && options.overwrite) {
        logger.error('Cannot use both --merge and --overwrite');
        process.exit(1);
      }

      const data = await stateUtils.downloadState(options.url);
      stateUtils.importState(data, options.overwrite, options.merge);
    });
};

interface DownloadStateOptions {
  url: string;
  merge: boolean;
  overwrite: boolean;
}
