import stateUtils from '../../utils/state';

import accountUtils from '../../utils/account';
import type { Command } from '../../../types';
import { Logger } from '../../utils/logger';

const logger = Logger.getInstance();

export default (program: any) => {
  program
    .command('clear')
    .hook('preAction', (thisCommand: Command) => {
      const command = [
        thisCommand.parent.action().name(),
        ...thisCommand.parent.args,
      ];
      stateUtils.recordCommand(command);
    })
    .description('Clear all accounts from the address book')
    .action(() => {
      logger.verbose('Clearing address book');
      accountUtils.clearAddressBook();
    });
};
