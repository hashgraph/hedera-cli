import { Command } from 'commander';
import { isJsonOutput, printOutput } from '../../utils/output';
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
          if (isJsonOutput()) {
            printOutput('tokenAssociate', {
              tokenId: options.tokenId,
              account: options.accountId,
            });
          }
        },
        { log: (o) => `Associating token ${o.tokenId} with ${o.accountId}` },
      ),
    );
  program.addHelpText(
    'afterAll',
    '\nExamples:\n  $ hedera token associate -a 0.0.1234 -t 0.0.5555\n  $ hedera token associate -a alice -t 0.0.5555 --json',
  );
};

interface AssociateTokenOptions {
  tokenId: string;
  accountId: string;
}
