import stateUtils from '../utils/state';
import telemetryUtils from '../utils/telemetry';
import stateController from '../state/stateController';
import enquirerUtils from '../utils/enquirer';
import dynamicVariablesUtils from '../utils/dynamicVariables';
import { Logger } from '../utils/logger';
import hbarUtils from '../utils/hbar';

import type { Account, Command } from '../../types';

const logger = Logger.getInstance();

export default (program: any) => {
  const hbar = program.command('hbar');

  hbar
    .command('transfer')
    .hook('preAction', async (thisCommand: Command) => {
      const command = [
        thisCommand.parent.action().name(),
        ...thisCommand.parent.args,
      ];
      if (stateUtils.isTelemetryEnabled()) {
        await telemetryUtils.recordCommand(command.join(' '));
      }
    })
    .description('Transfer tinybars between accounts')
    .requiredOption('-b, --balance <balance>', 'Amount of tinybars to transfer')
    .option('-t, --to <to>', 'Account ID to transfer tinybars to')
    .option('-f, --from <from>', 'Account ID to transfer tinybars from')
    .option('--memo <memo>', 'Memo for the transfer')
    .action(async (options: HbarTransferOptions) => {
      logger.verbose('Transferring tinybars');
      options = dynamicVariablesUtils.replaceOptions(options);

      let to = options.to;
      let from = options.from;
      const network = stateUtils.getNetwork();

      if (!options.from) {
        try {
          const accounts: Account[] = Object.values(
            stateController.getAll().accounts,
          );
          const filteredAccounts = accounts.filter(
            (account) => account.network === network,
          );
          if (filteredAccounts.length === 0) {
            logger.error(
              'No accounts found to transfer hbar from. Please create an account first.',
            );
            process.exit(1);
          }
          from = await enquirerUtils.createPrompt(
            filteredAccounts.map((account) => account.alias),
            'Choose account to transfer hbar from:',
          );
        } catch (error) {
          logger.error('Unable to get response:', error as object);
          process.exit(1);
        }
      }

      if (!options.to) {
        try {
          const accounts: Account[] = Object.values(
            stateController.getAll().accounts,
          );
          const filteredAccounts = accounts.filter(
            (account) => account.network === network,
          );
          if (filteredAccounts.length === 0) {
            logger.error(
              'No accounts found to transfer hbar from. Please create an account first.',
            );
            process.exit(1);
          }
          to = await enquirerUtils.createPrompt(
            filteredAccounts.map((account) => account.alias),
            'Choose account to transfer hbar to:',
          );
        } catch (error) {
          logger.error('Unable to get response:', error as object);
          process.exit(1);
        }
      }

      await hbarUtils.transfer(Number(options.balance), from, to, options.memo);
    });
};

interface HbarTransferOptions {
  balance: number;
  to: string;
  from: string;
  memo: string;
}
