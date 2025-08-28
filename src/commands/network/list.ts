import axios from 'axios';
import { Command } from 'commander';
import { selectNetworks } from '../../state/selectors';
import { color, heading } from '../../utils/color';
import { exitOnError } from '../../utils/errors';
import { Logger } from '../../utils/logger';
import { isJsonOutput, printOutput } from '../../utils/output';
import stateUtils from '../../utils/state';
import { telemetryPreAction } from '../shared/telemetryHook';

const logger = Logger.getInstance();

// Test mirror node by making a real API call
async function checkMirrorNodeHealth(
  mirrorNodeUrl: string,
): Promise<{ status: string; code?: number }> {
  try {
    // Test with a simple accounts call (0.0.2 is a common test account)
    const testUrl = `${mirrorNodeUrl}/accounts/0.0.2`;
    const response = await axios.get(testUrl, { timeout: 3000 });
    return { status: '✅', code: response.status };
  } catch (error: unknown) {
    if (error && typeof error === 'object' && 'response' in error) {
      const axiosError = error as { response: { status: number } };
      // 404 for a specific account is still OK (account might not exist)
      if (axiosError.response.status === 404) {
        return { status: '✅', code: axiosError.response.status };
      }
      // 400/401/403 are also OK (API is working, just auth/permission issues)
      if (
        axiosError.response.status >= 400 &&
        axiosError.response.status < 500
      ) {
        return { status: '✅', code: axiosError.response.status };
      }
      return { status: '❌', code: axiosError.response.status };
    }
    return { status: '❌' };
  }
}

// Test RPC endpoint using JSON-RPC call
async function checkRpcHealth(
  rpcUrl: string,
): Promise<{ status: string; code?: number }> {
  try {
    const response = await axios.post(
      rpcUrl,
      {
        jsonrpc: '2.0',
        id: 1,
        method: 'web3_clientVersion',
        params: [],
      },
      {
        headers: { 'Content-Type': 'application/json' },
        timeout: 3000,
      },
    );
    return { status: '✅', code: response.status };
  } catch (error: unknown) {
    if (error && typeof error === 'object' && 'response' in error) {
      const axiosError = error as { response: { status: number } };
      return { status: '❌', code: axiosError.response.status };
    }
    return { status: '❌' };
  }
}

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
