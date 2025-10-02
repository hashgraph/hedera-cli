import { Command } from 'commander';
import { DomainError } from '../../utils/errors';
import { isJsonOutput, printOutput } from '../../utils/output';
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
          if (isJsonOutput()) {
            printOutput('stateDownload', {
              url: options.url,
              merge: options.merge || false,
              overwrite: options.overwrite || false,
            });
          }
        },
        { log: (o) => `Downloading state from ${o.url}` },
      ),
    );
  program.addHelpText(
    'afterAll',
    '\nExamples:\n  $ hedera state download --url https://example.com/state.json\n  $ hedera state download --url https://example.com/state.json --merge --json',
  );
};

interface DownloadStateOptions {
  url: string;
  merge: boolean;
  overwrite: boolean;
}
