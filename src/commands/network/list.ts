import { Command } from 'commander';
import { color, heading } from '../../utils/color';
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
        logger.log(heading('Available networks:'));
        networkNames.forEach((name) =>
          logger.log(`${color.green('-')} ${color.magenta(name)}`),
        );
      }),
    );

  cmd.addHelpText(
    'after',
    `\nExamples:\n  $ hedera-cli network list\n  $ hedera-cli --json network list\n`,
  );
};
