import { Command } from 'commander';
import type { Account } from '../../types';
import { getState } from '../state/store';
import enquirerUtils from '../utils/enquirer';
import { DomainError } from '../utils/errors';
import hbarUtils from '../utils/hbar';
import stateUtils from '../utils/state';
import { telemetryPreAction } from './shared/telemetryHook';
import { wrapAction } from './shared/wrapAction';
export default (program: Command) => {
  const hbar = program.command('hbar');

  hbar
    .command('transfer')
    .hook('preAction', telemetryPreAction)
    .description('Transfer tinybars between accounts')
    .requiredOption('-b, --balance <balance>', 'Amount of tinybars to transfer')
    .option('-t, --to <to>', 'Account ID to transfer tinybars to')
    .option('-f, --from <from>', 'Account ID to transfer tinybars from')
    .option('--memo <memo>', 'Memo for the transfer')
    .action(
      wrapAction<HbarTransferOptions>(
        async (options) => {
          let to = options.to;
          let from = options.from;
          const network = stateUtils.getNetwork();

          if (!options.from) {
            try {
              const accounts: Account[] = Object.values(getState().accounts);
              const filteredAccounts = accounts.filter(
                (account) => account.network === network,
              );
              if (filteredAccounts.length === 0) {
                throw new DomainError(
                  'No accounts found to transfer hbar from. Please create an account first.',
                );
              }
              from = await enquirerUtils.createPrompt(
                filteredAccounts.map((account) => account.name),
                'Choose account to transfer hbar from:',
              );
            } catch (error) {
              throw new DomainError('Unable to get response');
            }
          }

          if (!options.to) {
            try {
              const accounts: Account[] = Object.values(getState().accounts);
              const filteredAccounts = accounts.filter(
                (account) => account.network === network,
              );
              if (filteredAccounts.length === 0) {
                throw new DomainError(
                  'No accounts found to transfer hbar from. Please create an account first.',
                );
              }
              to = await enquirerUtils.createPrompt(
                filteredAccounts.map((account) => account.name),
                'Choose account to transfer hbar to:',
              );
            } catch (error) {
              throw new DomainError('Unable to get response');
            }
          }

          await hbarUtils.transfer(
            Number(options.balance),
            from,
            to,
            options.memo,
          );
        },
        { log: 'Transferring tinybars' },
      ),
    );
};

interface HbarTransferOptions {
  balance: number;
  to: string;
  from: string;
  memo: string;
}
