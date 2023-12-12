import { recordCommand } from '../../state/stateService';
import accountUtils from '../../utils/account';

import type { Command } from '../../../types';

export default (program: any) => {
  program
    .command('import')
    .hook('preAction', (thisCommand: Command) => {
      const command = [
        thisCommand.parent.action().name(),
        ...thisCommand.parent.args,
      ];
      recordCommand(command);
    })
    .description(
      'Import an existing account using an account ID, alias, type, and optional private key.',
    )
    .requiredOption('-a, --alias <alias>', 'account must have an alias')
    .requiredOption('-i, --id <id>', 'Account ID')
    .option('-k, --key <key>', 'Private key')
    .action((options: ImportAccountOptions) => {
      if (options.key) {
        accountUtils.importAccount(options.id, options.key, options.alias);
      } else {
        accountUtils.importAccountId(options.id, options.alias);
      }
    });
};

interface ImportAccountOptions {
  alias: string;
  id: string;
  key: string;
}
