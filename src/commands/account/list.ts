import { Command } from 'commander';
import { selectAccounts } from '../../state/selectors';
import accountUtils from '../../utils/account';
import { exitOnError } from '../../utils/errors';
import { Logger } from '../../utils/logger';
import { isJsonOutput, printOutput } from '../../utils/output';
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
        if (isJsonOutput()) {
          const accounts = selectAccounts();
          printOutput('accounts', { accounts });
          return;
        }
        accountUtils.listAccounts(options.private);
      }),
    )
    .addHelpText(
      'after',
      `\nExamples:\n  $ hedera-cli account list\n  $ hedera-cli account list --private\n  $ hedera-cli --json account list\n`,
    );
};

interface ListAccountsOptions {
  private: boolean;
}
