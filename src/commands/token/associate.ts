import { Command } from 'commander';
import tokenUtils from '../../utils/token';
import stateUtils from '../../utils/state';
import telemetryUtils from '../../utils/telemetry';
import { Logger } from '../../utils/logger';
import dynamicVariablesUtils from '../../utils/dynamicVariables';

const logger = Logger.getInstance();

export default (program: Command) => {
  program
    .command('associate')
    .hook('preAction', async (thisCommand: Command) => {
      const parentName = thisCommand.parent?.name() || 'unknown';
      const command = [parentName, ...(thisCommand.parent?.args ?? [])];
      if (stateUtils.isTelemetryEnabled()) {
        await telemetryUtils.recordCommand(command.join(' '));
      }
    })
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
