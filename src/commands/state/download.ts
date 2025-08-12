import { Command } from 'commander';
import stateUtils from '../../utils/state';
import { telemetryPreAction } from '../shared/telemetryHook';
import { Logger } from '../../utils/logger';
import { DomainError, exitOnError } from '../../utils/errors';

const logger = Logger.getInstance();

export default (program: Command) => {
  program
    .command('download')
    .hook('preAction', telemetryPreAction)
    .description(
      'Download state from a URL and merge it with the current state',
    )
    .requiredOption('--url <url>', 'URL of script to download')
    .option('--merge', 'Merge state with downloaded state', false)
    .option('--overwrite', 'Overwrite state with downloaded state', false)
    .action(
      exitOnError(async (options: DownloadStateOptions) => {
        logger.verbose(`Downloading state from ${options.url}`);

        if (options.merge && options.overwrite) {
          throw new DomainError('Cannot use both --merge and --overwrite');
        }

        const data = await stateUtils.downloadState(options.url);
        stateUtils.importState(data, options.overwrite, options.merge);
      }),
    );
};

interface DownloadStateOptions {
  url: string;
  merge: boolean;
  overwrite: boolean;
}
