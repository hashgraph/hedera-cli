import { Command } from 'commander';
import { selectNetworks } from '../../state/selectors';
import { color, heading } from '../../utils/color';
import { exitOnError } from '../../utils/errors';
import { Logger } from '../../utils/logger';
import {
  checkMirrorNodeHealth,
  checkRpcHealth,
} from '../../utils/networkHealth';
import { isJsonOutput, printOutput } from '../../utils/output';
import stateUtils from '../../utils/state';
import { telemetryPreAction } from '../shared/telemetryHook';

const logger = Logger.getInstance();

// health-checks moved to ../../utils/networkHealth

export default (program: Command) => {
  const cmd = program
    .command('list')
    .hook('preAction', telemetryPreAction)
    .description('List all available networks')
    .action(
      exitOnError(async () => {
        const networkNames = stateUtils.getAvailableNetworks();
        const currentNetwork = stateUtils.getNetwork();

        const networks = selectNetworks();

        if (isJsonOutput()) {
          const networksWithConfig = networkNames.map((name) => {
            const config = networks[name];
            return {
              name,
              isActive: name === currentNetwork,
              mirrorNodeUrl: config.mirrorNodeUrl,
              rpcUrl: config.rpcUrl,
              operatorId: config.operatorId,
            };
          });
          printOutput('networks', {
            networks: networksWithConfig,
            activeNetwork: currentNetwork,
          });
          return;
        }

        logger.log(heading('Available networks:'));
        for (const name of networkNames) {
          const isActive = name === currentNetwork;
          const config = networks[name];
          const networkLine = `${color.green('-')} ${color.magenta(name)}`;
          const activeIndicator = isActive
            ? ` ${color.yellow('(active)')}`
            : '';
          logger.log(`${networkLine}${activeIndicator}`);

          if (isActive) {
            const mirrorStatus = await checkMirrorNodeHealth(
              config.mirrorNodeUrl,
            );
            logger.log(
              `  Mirror Node: ${color.cyan(config.mirrorNodeUrl)} ${mirrorStatus.status} ${
                mirrorStatus.code ? `(${mirrorStatus.code})` : ''
              }`,
            );

            const rpcStatus = await checkRpcHealth(config.rpcUrl);
            logger.log(
              `  RPC URL: ${color.cyan(config.rpcUrl)} ${rpcStatus.status} ${
                rpcStatus.code ? `(${rpcStatus.code})` : ''
              }`,
            );

            if (config.operatorId) {
              logger.log(`  Operator ID: ${color.cyan(config.operatorId)}`);
            }
          }
        }
      }),
    );

  cmd.addHelpText(
    'after',
    `\nExamples:\n  $ hedera-cli network list\n  $ hedera-cli --json network list\n`,
  );
};
