import { Command } from 'commander';
import { updateState as storeUpdateState } from '../../state/store';
import { exitOnError } from '../../utils/errors';
import { Logger } from '../../utils/logger';
import { isJsonOutput, printOutput } from '../../utils/output';
import stateUtils from '../../utils/state';
import { telemetryPreAction } from '../shared/telemetryHook';

const logger = Logger.getInstance();

interface ResetOptions {
  skipAccounts: boolean;
  skipTokens: boolean;
  skipScripts: boolean;
  skipTopics: boolean;
}

function clear(
  skipAccounts: boolean,
  skipTokens: boolean,
  skipScripts: boolean,
  skipTopics: boolean,
): void {
  if (!skipAccounts && !skipTokens && !skipScripts && !skipTopics) {
    stateUtils.clearState();
    return;
  }

  storeUpdateState((draft) => {
    if (!skipAccounts) draft.accounts = {};
    if (!skipTokens) draft.tokens = {};
    if (!skipScripts) draft.scripts = {};
    if (!skipTopics) draft.topics = {};
  });
  logger.log('State cleared successfully');
}

export default (program: Command) => {
  program
    .command('clear')
    .hook('preAction', telemetryPreAction)
    .description('Clear all state and reset to default')
    .option('-a, --skip-accounts', 'Skip resetting accounts', false)
    .option('-t, --skip-tokens', 'Skip resetting tokens', false)
    .option('-s, --skip-scripts', 'Skip resetting scripts', false)
    .option('-o, --skip-topics', 'Skip resetting topics', false)
    .action(
      exitOnError((options: ResetOptions) => {
        logger.verbose('Clearing state');
        clear(
          options.skipAccounts,
          options.skipTokens,
          options.skipScripts,
          options.skipTopics,
        );
        if (isJsonOutput()) {
          printOutput('stateClear', {
            skipped: {
              accounts: options.skipAccounts || false,
              tokens: options.skipTokens || false,
              scripts: options.skipScripts || false,
              topics: options.skipTopics || false,
            },
          });
        }
      }),
    );
  program.addHelpText(
    'afterAll',
    '\nExamples:\n  $ hedera state clear\n  $ hedera state clear -a -t --json',
  );
};
