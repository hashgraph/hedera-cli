import { Command } from 'commander';
import stateUtils from '../../utils/state';
import { Logger } from '../../utils/logger';
import accountUtils from '../../utils/account';
import { DomainError, exitOnError } from '../../utils/errors';
import { telemetryPreAction } from '../shared/telemetryHook';
import { getState } from '../../state/store';
import enquirerUtils from '../../utils/enquirer';
import dynamicVariablesUtils from '../../utils/dynamicVariables';
import type { Account } from '../../../types/state';

const logger = Logger.getInstance();

export default (program: Command) => {
  program
    .command('delete')
    .hook('preAction', telemetryPreAction)
    .description('Delete an account from the address book')
    .option('-n, --name <name>', 'account must have a name')
    .option('-i, --id <id>', 'Account ID')
    .action(
      exitOnError(async (options: AccountDeleteOptions) => {
        options = dynamicVariablesUtils.replaceOptions(options);

        if (options.id && options.name) {
          throw new DomainError(
            'You must provide either an account ID or a name, not both.',
          );
        }

        // Prompt for account ID or name if not provided
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
          } catch (error) {
            throw new DomainError('Unable to get response');
          }
        } else {
          accountIdOrName = options.id || options.name;
        }

        // options.id || options.name;
        logger.verbose(`Deleting account with name or ID: ${accountIdOrName}`);
        if (!accountIdOrName) {
          throw new DomainError(
            'You must provide either an account ID or a name.',
          );
        }

        accountUtils.deleteAccount(accountIdOrName);
      }),
    );
};

interface AccountDeleteOptions {
  name?: string;
  id?: string;
}
