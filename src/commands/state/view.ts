import { Command } from 'commander';
import { getState } from '../../state/store';
import { Logger } from '../../utils/logger';
import stateUtils from '../../utils/state';
import { telemetryPreAction } from '../shared/telemetryHook';
import { wrapAction } from '../shared/wrapAction';

const logger = Logger.getInstance();

export default (program: Command) => {
  program
    .command('view')
    .hook('preAction', telemetryPreAction)
    .description('View state')
    .option('--accounts', 'View accounts', false)
    .option('--account-name <account-name>', 'View account by name')
    .option('--account-id <account-id>', 'View account by ID')
    .option('--tokens', 'View tokens', false)
    .option('--token-id <token-id>', 'View token by ID')
    .option('--scripts', 'View scripts', false)
    .action(
      wrapAction<ViewStateOptions>(
        (options) => {
          logger.verbose('Viewing state');

          const state = getState();

          if (
            !options.accounts &&
            !options.tokens &&
            !options.scripts &&
            !options.accountName &&
            !options.accountId &&
            !options.tokenId
          ) {
            logger.log('\nState:');
            logger.log(state); // logger handles object formatting
            return;
          }

          if (options.accountId) {
            logger.log(`\nAccount ${options.accountId}:`);
            logger.log(
              stateUtils.getAccountById(options.accountId) ||
                'Account not found',
            );
          }

          if (options.accountName) {
            logger.log('\nAccount:');
            logger.log(
              state.accounts[options.accountName] || 'Account not found',
            );
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
        },
        { log: 'View state' },
      ),
    );
};

interface ViewStateOptions {
  accounts: boolean;
  tokens: boolean;
  scripts: boolean;
  accountName: string;
  accountId: string;
  tokenId: string;
}
