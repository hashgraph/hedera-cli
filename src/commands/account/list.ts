import accountUtils from '../../utils/account';
import { Logger } from '../../utils/logger';
import { telemetryPreAction } from '../shared/telemetryHook';
import { Command } from 'commander';

const logger = Logger.getInstance();

export default (program: Command) => {
  program
    .command('list')
    .hook('preAction', telemetryPreAction)
    .description('List all accounts in the address book')
    .option('-p, --private', 'Show private keys')
    .action((options: ListAccountsOptions) => {
      logger.verbose('Listing accounts');
      accountUtils.listAccounts(options.private);
    });
};

interface ListAccountsOptions {
  private: boolean;
}
