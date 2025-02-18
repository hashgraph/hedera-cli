import stateUtils from '../../utils/state';
import accountUtils from '../../utils/account';
import { Logger } from '../../utils/logger';
import telemetryUtils from '../../utils/telemetry';

import type { Command } from '../../../types';

const logger = Logger.getInstance();

export default (program: any) => {
  program
    .command('list')
    .hook('preAction', async (thisCommand: Command) => {
      const command = [
        thisCommand.parent.action().name(),
        ...thisCommand.parent.args,
      ];
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
