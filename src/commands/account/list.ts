import stateUtils from '../../utils/state';
import accountUtils from '../../utils/account';
import { Logger } from '../../utils/logger';
import telemetryUtils from '../../utils/telemetry';
import { Command } from 'commander';

const logger = Logger.getInstance();

export default (program: Command) => {
  program
    .command('list')
    .hook('preAction', async (thisCommand: Command) => {
      const parentName = thisCommand.parent?.name() || 'unknown';
      const command = [parentName, ...(thisCommand.parent?.args ?? [])];
      if (stateUtils.isTelemetryEnabled()) {
        await telemetryUtils.recordCommand(command.join(' '));
      }
    })
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
