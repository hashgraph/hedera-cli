import tokenUtils from '../../utils/token';
import stateUtils from '../../utils/state';
import telemetryUtils from '../../utils/telemetry';
import { Logger } from '../../utils/logger';

import type { Command } from '../../../types';
import dynamicVariablesUtils from '../../utils/dynamicVariables';

const logger = Logger.getInstance();

export default (program: any) => {
  program
    .command('associate')
    .hook('preAction', async (thisCommand: Command) => {
      const command = [
        thisCommand.parent.action().name(),
        ...thisCommand.parent.args,
      ];
      if (stateUtils.isTelemetryEnabled()) {
        await telemetryUtils.recordCommand(command.join(' '));
      }
      stateUtils.recordCommand(command);
    })
    .description('Associate a token with an account')
    .requiredOption(
      '-a, --account-id <accountId>', // alias is also possible for --acount-id
      'Account ID or account alias to associate with token',
    )
    .requiredOption(
      '-t, --token-id <tokenId>',
      'Token ID to associate with account',
    )
    .action(async (options: AssociateTokenOptions) => {
      logger.verbose(
        `Associating token ${options.tokenId} with ${options.accountId}`,
      );
      options = dynamicVariablesUtils.replaceOptions(options);
      await tokenUtils.associateToken(options.tokenId, options.accountId);
    });
};

interface AssociateTokenOptions {
  tokenId: string;
  accountId: string;
}
