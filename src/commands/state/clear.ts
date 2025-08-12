import { Command } from 'commander';
import stateUtils from '../../utils/state';
import telemetryUtils from '../../utils/telemetry';
import { Logger } from '../../utils/logger';
import { updateState as storeUpdateState } from '../../state/store';

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
    .hook('preAction', async (thisCommand: Command) => {
      const parentName = thisCommand.parent?.name() || 'unknown';
      const command = [parentName, ...(thisCommand.parent?.args ?? [])];
      if (stateUtils.isTelemetryEnabled()) {
        await telemetryUtils.recordCommand(command.join(' '));
      }
    })
    .description('Clear all state and reset to default')
    .option('-a, --skip-accounts', 'Skip resetting accounts', false)
    .option('-t, --skip-tokens', 'Skip resetting tokens', false)
    .option('-s, --skip-scripts', 'Skip resetting scripts', false)
    .option('-o, --skip-topics', 'Skip resetting topics', false)
    .action((options: ResetOptions) => {
      logger.verbose('Clearing state');
      clear(
        options.skipAccounts,
        options.skipTokens,
        options.skipScripts,
        options.skipTopics,
      );
    });
};
