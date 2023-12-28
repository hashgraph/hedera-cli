import { recordCommand } from '../../state/stateService';
import { Logger } from '../../utils/logger';

import accountUtils from '../../utils/account';

import type { Command } from '../../../types';

const logger = Logger.getInstance();

export default (program: any) => {
  program
    .command('balance <accountIdOrAlias>')
    .hook('preAction', (thisCommand: Command) => {
      const command = [
        thisCommand.parent.action().name(),
        ...thisCommand.parent.args,
      ];
      recordCommand(command);
    })
    .description('Retrieve the balance for an account ID or alias')
    .option('-h, --only-hbar', 'Show only Hbar balance')
    .option('-t, --token-id <tokenId>', 'Show balance for a specific token ID')
    .action(
      async (accountIdOrAlias: string, options: GetAccountBalanceOptions) => {
        logger.verbose(`Getting balance for ${accountIdOrAlias}`);
        
        if (options.onlyHbar && options.tokenId) {
          logger.error(
            'You cannot use both --only-hbar and --token-id options at the same time.',
          );
          process.exit(1);
        }

        await accountUtils.getAccountBalance(
          accountIdOrAlias,
          options.onlyHbar,
          options.tokenId,
        );
      },
    );
};

interface GetAccountBalanceOptions {
  onlyHbar: boolean;
  tokenId: string;
}
