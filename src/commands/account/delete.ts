import stateUtils from '../../utils/state';
import { Logger } from '../../utils/logger';

import accountUtils from '../../utils/account';
import { DomainError, exitOnError } from '../../utils/errors';
import telemetryUtils from '../../utils/telemetry';
import { getState } from '../../state/store';
import enquirerUtils from '../../utils/enquirer';
import dynamicVariablesUtils from '../../utils/dynamicVariables';

import type { Command, Account } from '../../../types';

const logger = Logger.getInstance();

export default (program: any) => {
  program
    .command('delete')
    .hook('preAction', async (thisCommand: Command) => {
      const command = [
        thisCommand.parent.action().name(),
        ...thisCommand.parent.args,
      ];
      if (stateUtils.isTelemetryEnabled()) {
        await telemetryUtils.recordCommand(command.join(' '));
      }
    })
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
        let accountIdOrName;
        const network = stateUtils.getNetwork();
        if (!options.id && !options.name) {
          try {
            const accounts: Account[] = Object.values(
              (getState() as any).accounts,
            );
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
