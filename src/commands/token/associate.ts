import { Command } from 'commander';
import tokenUtils from '../../utils/token';
import { telemetryPreAction } from '../shared/telemetryHook';
import { wrapAction } from '../shared/wrapAction';

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
    .action(
      wrapAction<AssociateTokenOptions>(
        async (options) => {
          await tokenUtils.associateToken(options.tokenId, options.accountId);
        },
        { log: (o) => `Associating token ${o.tokenId} with ${o.accountId}` },
      ),
    );
};

interface AssociateTokenOptions {
  tokenId: string;
  accountId: string;
}
