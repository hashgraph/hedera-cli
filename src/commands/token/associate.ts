import { Command } from 'commander';
import tokenUtils from '../../utils/token';
import { telemetryPreAction } from '../shared/telemetryHook';
import { Logger } from '../../utils/logger';
import dynamicVariablesUtils from '../../utils/dynamicVariables';

const logger = Logger.getInstance();

export default (program: Command) => {
  program
    .command('associate')
    .hook('preAction', telemetryPreAction)
    .description('Associate a token with an account')
    .requiredOption(
      '-a, --account-id <accountId>', // name is also possible for --account-id
      'Account ID or account name to associate with token',
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
