import { Command } from 'commander';
import accountUtils from '../../utils/account';
import { exitOnError } from '../../utils/errors';
import { Logger } from '../../utils/logger';
import { isJsonOutput, printOutput } from '../../utils/output';
import { telemetryPreAction } from '../shared/telemetryHook';

const logger = Logger.getInstance();

export default (program: Command) => {
  program
    .command('clear')
    .hook('preAction', telemetryPreAction)
    .description('Clear all accounts from the address book')
    .action(
      exitOnError(() => {
        logger.verbose('Clearing address book');
        accountUtils.clearAddressBook();
        if (isJsonOutput()) {
          printOutput('accountClear', { cleared: true });
        }
      }),
    );
  program.addHelpText(
    'afterAll',
    '\nExamples:\n  $ hedera account clear\n  $ hedera account clear --json',
  );
};
