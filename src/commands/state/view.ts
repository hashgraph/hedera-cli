import { Command } from 'commander';
import { getState } from '../../state/store';
import { Logger } from '../../utils/logger';
import { isJsonOutput } from '../../utils/output';
import stateUtils from '../../utils/state';
import { outputState } from '../../utils/stateOutput';
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

          const noFilters =
            !options.accounts &&
            !options.tokens &&
            !options.scripts &&
            !options.accountName &&
            !options.accountId &&
            !options.tokenId;
          if (noFilters) {
            if (isJsonOutput()) {
              outputState('stateView', { all: true });
            } else {
              logger.log('\nState:');
              logger.log(state);
            }
            return;
          }

          if (options.accountId) {
            const account =
              stateUtils.getAccountById(options.accountId) ||
              'Account not found';
            if (isJsonOutput()) {
              outputState('stateAccountView', { accountId: options.accountId });
            } else {
              logger.log(`\nAccount ${options.accountId}:`);
              logger.log(account);
            }
          }

          if (options.accountName) {
            const account =
              state.accounts[options.accountName] || 'Account not found';
            if (isJsonOutput()) {
              outputState('stateAccountView', {
                accountName: options.accountName,
              });
            } else {
              logger.log('\nAccount:');
              logger.log(account);
            }
          }

          if (options.tokenId) {
            const token = state.tokens[options.tokenId] || 'Token not found';
            if (isJsonOutput()) {
              outputState('stateTokenView', { tokenId: options.tokenId });
            } else {
              logger.log(`\nToken ${options.tokenId}:`);
              logger.log(token);
            }
          }

          if (options.accounts) {
            if (isJsonOutput()) {
              outputState('stateAccounts', { accounts: true });
            } else {
              logger.log('\nAccounts:');
              logger.log(state.accounts);
            }
          }

          if (options.tokens) {
            if (isJsonOutput()) {
              outputState('stateTokens', { tokens: true });
            } else {
              logger.log('\nTokens:');
              logger.log(state.tokens);
            }
          }

          if (options.scripts) {
            if (isJsonOutput()) {
              outputState('stateScripts', { scripts: true });
            } else {
              logger.log('\nScripts:');
              logger.log(state.scripts);
            }
          }
        },
        { log: 'View state' },
      ),
    );
  program.addHelpText(
    'afterAll',
    '\nExamples:\n  $ hedera state view\n  $ hedera state view --accounts --json',
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
