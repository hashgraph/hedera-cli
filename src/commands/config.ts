import { getState } from '../state/store';
import { loadUserConfig, resolveStateFilePath } from '../config/loader';
import { Logger } from '../utils/logger';
import telemetryUtils from '../utils/telemetry';
import stateUtils from '../utils/state';
import type { Command } from '../../types';

const logger = Logger.getInstance();

export default (program: any) => {
  const cfg = program
    .command('config')
    .description('Inspect configuration layers');

  cfg
    .command('view')
    .option('--active', 'Show only the effective active network config')
    .option('--json', 'Output raw JSON (machine readable)')
    .hook('preAction', async (thisCommand: Command) => {
      const command = [
        thisCommand.parent.action().name(),
        ...thisCommand.parent.args,
      ];
      if (stateUtils.isTelemetryEnabled()) {
        await telemetryUtils.recordCommand(command.join(' '));
      }
    })
    .description('View merged configuration (base + user overrides + runtime)')
    .action((options: { active?: boolean; json?: boolean }) => {
      const state = getState();
      const { user, source } = loadUserConfig();
      const activeNetwork = state.network;
      const activeNetworkConfig = state.networks[activeNetwork];
      if (options.active) {
        const output = { network: activeNetwork, config: activeNetworkConfig };
        if (options.json) {
          logger.log(JSON.stringify(output, null, 2));
        } else {
          logger.log(`Active network: ${activeNetwork}`);
          logger.log('Config:');
          logger.log(JSON.stringify(activeNetworkConfig, null, 2));
        }
        return;
      }
      const mergedInfo = {
        sourceUserConfig: source || null,
        stateFile: resolveStateFilePath(),
        activeNetwork,
        networks: state.networks,
        telemetry: state.telemetry,
        telemetryServer: state.telemetryServer,
        userOverridesKeys: Object.keys(user || {}),
      };
      if (options.json) {
        logger.log(JSON.stringify(mergedInfo, null, 2));
      } else {
        logger.log('Merged configuration:');
        logger.log(JSON.stringify(mergedInfo, null, 2));
      }
    });
};
