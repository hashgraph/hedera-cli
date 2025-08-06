import stateUtils from '../../utils/state';
import telemetryUtils from '../../utils/telemetry';
import type { Command } from '../../../types';
import { Logger } from '../../utils/logger';
import stateController from '../../state/stateController';

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

  if (!skipAccounts) stateController.saveKey('accounts', {});
  if (!skipTokens) stateController.saveKey('tokens', {});
  if (!skipScripts) stateController.saveKey('scripts', {});
  if (!skipTopics) stateController.saveKey('topics', {});
  logger.log('State cleared successfully');
}

export default (program: any) => {
  program
    .command('clear')
    .hook('preAction', async (thisCommand: Command) => {
      const command = [
        thisCommand.parent.action().name(),
        ...thisCommand.parent.args,
      ];
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
