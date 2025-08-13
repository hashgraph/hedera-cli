import { Command } from 'commander';
import { exitOnError } from '../../utils/errors';
import { Logger } from '../../utils/logger';
import stateUtils from '../../utils/state';
import { telemetryPreAction } from '../shared/telemetryHook';
import { isJsonOutput, printOutput } from '../../utils/output';

const logger = Logger.getInstance();

export default (program: Command) => {
  program
    .command('use <name>')
    .hook('preAction', telemetryPreAction)
    .description('Switch to a specific network')
    .action(
      exitOnError((name: string) => {
        logger.verbose(`Switching to network: ${name}`);
        stateUtils.switchNetwork(name);
        if (isJsonOutput()) {
          printOutput('network', { activeNetwork: name });
          return;
        }
        logger.log(`Active network set to: ${name}`);
      }),
    )
    .addHelpText(
      'after',
      `\nExamples:\n  $ hedera-cli network use testnet\n  $ hedera-cli --json network use previewnet\n`,
    );
};
