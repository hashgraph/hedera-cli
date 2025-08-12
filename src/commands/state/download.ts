import { Command } from 'commander';
import { DomainError } from '../../utils/errors';
import stateUtils from '../../utils/state';
import { telemetryPreAction } from '../shared/telemetryHook';
import { wrapAction } from '../shared/wrapAction';

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
      wrapAction<DownloadStateOptions>(
        async (options) => {
          if (options.merge && options.overwrite) {
            throw new DomainError('Cannot use both --merge and --overwrite');
          }
          const data = await stateUtils.downloadState(options.url);
          stateUtils.importState(data, options.overwrite, options.merge);
        },
        { log: (o) => `Downloading state from ${o.url}` },
      ),
    );
};

interface DownloadStateOptions {
  url: string;
  merge: boolean;
  overwrite: boolean;
}
