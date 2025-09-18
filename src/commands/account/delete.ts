import { Command } from 'commander';
import type { Account } from '../../../types/state';
import { getState } from '../../state/store';
import accountUtils from '../../utils/account';
import enquirerUtils from '../../utils/enquirer';
import { DomainError } from '../../utils/errors';
import { isJsonOutput, printOutput } from '../../utils/output';
import stateUtils from '../../utils/state';
import { telemetryPreAction } from '../shared/telemetryHook';
import { wrapAction } from '../shared/wrapAction';

export default (program: Command) => {
  program
    .command('delete')
    .hook('preAction', telemetryPreAction)
    .description('Delete an account from the address book')
    .option('-n, --name <name>', 'account must have a name')
    .option('-i, --id <id>', 'Account ID')
    .action(
      wrapAction<AccountDeleteOptions>(
        async (options) => {
          if (options.id && options.name) {
            throw new DomainError(
              'You must provide either an account ID or a name, not both.',
            );
          }
          let accountIdOrName: string | undefined;
          const network = stateUtils.getNetwork();
          if (!options.id && !options.name) {
            try {
              const state = getState();
              const accounts: Account[] = Object.values(state.accounts);
              const filteredAccounts = accounts.filter(
                (account) => account.network === network,
              );
              if (filteredAccounts.length === 0) {
                throw new DomainError('No accounts found to delete.');
              }
              accountIdOrName = await enquirerUtils.createPrompt(
                filteredAccounts.map((account) => account.name),
                'Choose account to delete:',
              );
            } catch {
              throw new DomainError('Unable to get response');
            }
          } else {
            accountIdOrName = options.id || options.name;
          }
          if (!accountIdOrName) {
            throw new DomainError(
              'You must provide either an account ID or a name.',
            );
          }
          accountUtils.deleteAccount(accountIdOrName);
          if (isJsonOutput()) {
            printOutput('accountDelete', { target: accountIdOrName });
          }
        },
        { log: (o) => `Deleting account with name or ID: ${o.id || o.name}` },
      ),
    );
  program.addHelpText(
    'afterAll',
    '\nExamples:\n  $ hedera account delete -n alice\n  $ hedera account delete -i 0.0.1234 --json',
  );
};

interface AccountDeleteOptions {
  name?: string;
  id?: string;
}
