import stateUtils from '../../utils/state';
import telemetryUtils from '../../utils/telemetry';
import accountUtils from '../../utils/account';
import { Command } from 'commander';
import { Logger } from '../../utils/logger';

const logger = Logger.getInstance();

export default (program: Command) => {
  program
    .command('clear')
    .hook('preAction', async (thisCommand: Command) => {
      const parentName = thisCommand.parent?.name() || 'unknown';
      const command = [parentName, ...(thisCommand.parent?.args ?? [])];
      if (stateUtils.isTelemetryEnabled()) {
        await telemetryUtils.recordCommand(command.join(' '));
      }
    })
    .description('Clear all accounts from the address book')
    .action(() => {
      logger.verbose('Clearing address book');
      accountUtils.clearAddressBook();
    });
};
