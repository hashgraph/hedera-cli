import { telemetryPreAction } from '../shared/telemetryHook';
import accountUtils from '../../utils/account';
import { Command } from 'commander';
import { Logger } from '../../utils/logger';

const logger = Logger.getInstance();

export default (program: Command) => {
  program
    .command('clear')
    .hook('preAction', telemetryPreAction)
    .description('Clear all accounts from the address book')
    .action(() => {
      logger.verbose('Clearing address book');
      accountUtils.clearAddressBook();
    });
};
