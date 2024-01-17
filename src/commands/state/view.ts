import stateUtils from '../../utils/state';
import type { Command } from '../../../types';
import { Logger } from '../../utils/logger';
import stateController from '../../state/stateController';
import dynamicVariablesUtils from '../../utils/dynamicVariables';

const logger = Logger.getInstance();

export default (program: any) => {
  program
    .command('view')
    .hook('preAction', (thisCommand: Command) => {
      const command = [
        thisCommand.parent.action().name(),
        ...thisCommand.parent.args,
      ];
      stateUtils.recordCommand(command);
    })
    .description('View state')
    .option('--accounts', 'View accounts', false)
    .option('--account-alias <account-alias>', 'View account by alias')
    .option('--account-id <account-id>', 'View account by ID')
    .option('--tokens', 'View tokens', false)
    .option('--token-id <token-id>', 'View token by ID')
    .option('--scripts', 'View scripts', false)
    .action((options: ViewStateOptions) => {
      options = dynamicVariablesUtils.replaceOptions(options); // allow dynamic vars for account-alias, account-id, and token-id
      logger.verbose('Viewing state');

      const state = stateController.getAll();

      if (
        !options.accounts &&
        !options.tokens &&
        !options.scripts &&
        !options.accountAlias &&
        !options.accountId &&
        !options.tokenId
      ) {
        logger.log('\nState:');
        logger.log(state);
        return;
      }

      if (options.accountId) {
        logger.log(`\nAccount ${options.accountId}:`);
        logger.log(stateUtils.getAccountById(options.accountId) || 'Account not found');
      }

      if (options.accountAlias) {
        logger.log('\nAccount:');
        logger.log(state.accounts[options.accountAlias] || 'Account not found');
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
  accountAlias: string;
  accountId: string;
  tokenId: string;
}
