import stateUtils from '../../utils/state';
import telemetryUtils from '../../utils/telemetry';
import type { Command } from '../../../types';
import { Logger } from '../../utils/logger';
import { getState } from '../../state/store';
import dynamicVariablesUtils from '../../utils/dynamicVariables';

const logger = Logger.getInstance();

export default (program: any) => {
  program
    .command('view')
    .hook('preAction', async (thisCommand: Command) => {
      const command = [
        thisCommand.parent.action().name(),
        ...thisCommand.parent.args,
      ];
      if (stateUtils.isTelemetryEnabled()) {
        await telemetryUtils.recordCommand(command.join(' '));
      }
    })
    .description('View state')
    .option('--accounts', 'View accounts', false)
    .option('--account-name <account-name>', 'View account by name')
    .option('--account-id <account-id>', 'View account by ID')
    .option('--tokens', 'View tokens', false)
    .option('--token-id <token-id>', 'View token by ID')
    .option('--scripts', 'View scripts', false)
    .action((options: ViewStateOptions) => {
      options = dynamicVariablesUtils.replaceOptions(options); // allow dynamic vars for account-name, account-id, and token-id
      logger.verbose('Viewing state');

      const state = getState() as any;

      if (
        !options.accounts &&
        !options.tokens &&
        !options.scripts &&
        !options.accountName &&
        !options.accountId &&
        !options.tokenId
      ) {
        logger.log('\nState:');
        logger.log(state);
        return;
      }

      if (options.accountId) {
        logger.log(`\nAccount ${options.accountId}:`);
        logger.log(
          stateUtils.getAccountById(options.accountId) || 'Account not found',
        );
      }

      if (options.accountName) {
        logger.log('\nAccount:');
        logger.log(state.accounts[options.accountName] || 'Account not found');
      }

      if (options.tokenId) {
        logger.log(`\nToken ${options.tokenId}:`);
        logger.log(state.tokens[options.tokenId] || 'Token not found');
      }

      if (options.accounts) {
        logger.log('\nAccounts:');
        logger.log(state.accounts);
      }

      if (options.tokens) {
        logger.log('\nTokens:');
        logger.log(state.tokens);
      }

      if (options.scripts) {
        logger.log('\nScripts:');
        logger.log(state.scripts);
      }
    });
};

interface ViewStateOptions {
  accounts: boolean;
  tokens: boolean;
  scripts: boolean;
  accountName: string;
  accountId: string;
  tokenId: string;
}
