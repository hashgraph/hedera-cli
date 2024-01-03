import { recordCommand } from '../../state/stateService';
import type { Command } from '../../../types';
import { Logger } from '../../utils/logger';
import stateController from '../../state/stateController';
import { clearState } from '../../state/stateService';

const logger = Logger.getInstance();

export default (program: any) => {
  program
    .command('clear')
    .hook('preAction', (thisCommand: Command) => {
      const command = [
        thisCommand.parent.action().name(),
        ...thisCommand.parent.args,
      ];
      recordCommand(command);
    })
    .description('Clear all state and reset to default')
    .option('-a, --skip-accounts', 'Skip resetting accounts', false)
    .option('-t, --skip-tokens', 'Skip resetting tokens', false)
    .option('-s, --skip-scripts', 'Skip resetting scripts', false)
    .action((options: ResetOptions) => {
      logger.verbose('Clearing state');
      clear(options.skipAccounts, options.skipTokens, options.skipScripts);
    });
};

function clear(
  skipAccounts: boolean,
  skipTokens: boolean,
  skipScripts: boolean,
): void {
  if (!skipAccounts && !skipTokens && !skipScripts) {
    clearState();
    process.exit(0);
  }

  if (!skipAccounts) stateController.saveKey('accounts', {});
  if (!skipTokens) stateController.saveKey('tokens', {});
  if (!skipScripts) stateController.saveKey('scripts', {});
  logger.log('State cleared successfully');
}

interface ResetOptions {
  skipAccounts: boolean;
  skipTokens: boolean;
  skipScripts: boolean;
}
