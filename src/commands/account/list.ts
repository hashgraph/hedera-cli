import { Command } from 'commander';
import accountUtils from '../../utils/account';
import { exitOnError } from '../../utils/errors';
import { Logger } from '../../utils/logger';
import { telemetryPreAction } from '../shared/telemetryHook';

const logger = Logger.getInstance();

export default (program: Command) => {
  program
    .command('list')
    .hook('preAction', telemetryPreAction)
    .description('List all accounts in the address book')
    .option('-p, --private', 'Show private keys')
    .action(
      exitOnError((options: ListAccountsOptions) => {
        logger.verbose('Listing accounts');
        accountUtils.listAccounts(options.private);
      }),
    );
};

interface ListAccountsOptions {
  private: boolean;
}
