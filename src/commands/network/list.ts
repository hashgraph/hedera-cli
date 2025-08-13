import { Command } from 'commander';
import { exitOnError } from '../../utils/errors';
import { Logger } from '../../utils/logger';
import { isJsonOutput, printOutput } from '../../utils/output';
import stateUtils from '../../utils/state';
import { telemetryPreAction } from '../shared/telemetryHook';

const logger = Logger.getInstance();

export default (program: Command) => {
  const cmd = program
    .command('list')
    .hook('preAction', telemetryPreAction)
    .description('List all available networks')
    .action(
      exitOnError(() => {
        const networkNames = stateUtils.getAvailableNetworks();
        if (isJsonOutput()) {
          printOutput('networks', { networks: networkNames });
          return;
        }
        logger.log('Available networks:');
        networkNames.forEach((name) => logger.log(`- ${name}`));
      }),
    );

  cmd.addHelpText(
    'after',
    `\nExamples:\n  $ hedera-cli network list\n  $ hedera-cli --json network list\n`,
  );
};
