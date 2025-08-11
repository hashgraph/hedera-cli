import * as dotenv from 'dotenv';

import stateUtils from '../utils/state';
import telemetryUtils from '../utils/telemetry';
import config from '../state/config';
import type { State } from '../../types';
import { Logger } from '../utils/logger';
import accountUtils from '../utils/account';
import setupUtils from '../utils/setup';
import stateController from '../state/stateController';

import type { Command } from '../../types';

const logger = Logger.getInstance();

interface SetupOptions {
  telemetry: boolean;
  path?: string;
}

interface ReloadOptions {
  telemetry: boolean;
}

/**
 * @description Setup the state file with the init config
 */
function setupState(): void {
  stateController.saveState(config as State);
}

/**
 * @description Verify that the operator account has enough balance to pay for transactions (at least 1 Hbar)
 * @param operatorId Operator ID to check balance for
 * @param network
 */
async function verifyOperatorBalance(
  operatorId: string,
  network: string,
): Promise<void> {
  // Skip if operator ID is not defined
  if (operatorId) {
    const balance = await accountUtils.getAccountHbarBalanceByNetwork(
      operatorId,
      network,
    );
    if (balance < 100000000) {
      logger.error(
        `The operator account ${operatorId} does not have enough balance to pay for transactions (less than 1 Hbar). Please add more balance to the account.`,
      );
      process.exit(1);
    }
  }
}

/**
 * @description Setup the CLI with operator key and ID for different networks
 * @param action Action to perform (init or reload)
 * @param telemetry Flag to enable telemetry
 */
async function setupCLI(
  action: string,
  telemetry: boolean = false,
  envPath?: string,
): Promise<void> {
  // Load environment variables from .env file (optional custom path)
  const envConfig = dotenv.config(envPath ? { path: envPath } : undefined);
  if (envConfig.error) {
    logger.error(`Can't load .env file: ${envConfig.error.message}`);
    process.exit(1);
  }

  if (!config.networks || Object.keys(config.networks).length === 0) {
    logger.error(
      'No networks found in the config. Please check your config file.',
    );
    process.exit(1);
  }

  const networkNames = Object.keys(config.networks);

  // // Extract operator key and ID from environment variables
  const { TELEMETRY_URL } = process.env;

  // Only write a fresh state file if the user is running the init command
  if (action === 'init') {
    setupState();
  }

  // For each supported network allow env overrides: TESTNET_OPERATOR_ID / KEY etc.
  for (const networkName of networkNames) {
    const upper = networkName.toUpperCase();
    const envId = (process.env[`${upper}_OPERATOR_ID`] || '').trim();
    const envKey = (process.env[`${upper}_OPERATOR_KEY`] || '').trim();
    if (envId && envKey) {
      setupUtils.setupOperatorAccount(envId, envKey, networkName);
      await verifyOperatorBalance(envId, networkName);
    }
  }

  // Set telemetry server URL
  let telemetryServer =
    TELEMETRY_URL || 'https://hedera-cli-telemetry.onrender.com/track';
  stateController.saveKey('telemetryServer', telemetryServer);
  if (telemetry) {
    stateController.saveKey('telemetry', 1);
  }
}

export default (program: any) => {
  const setup = program.command('setup').description('Setup Hedera CLI');

  setup
    .command('init')
    .hook('preAction', async (thisCommand: Command) => {
      const command = [
        thisCommand.parent.action().name(),
        ...thisCommand.parent.args,
      ];
      if (stateUtils.isTelemetryEnabled()) {
        await telemetryUtils.recordCommand(command.join(' '));
      }
    })
    .description('Setup the CLI with operator key and ID')
    .option(
      '--telemetry',
      'Enable telemetry for Hedera to process anonymous usage data, disabled by default',
    )
    .option('--path <path>', 'Specify a custom path for the .env file')
    .action(async (options: SetupOptions) => {
      logger.verbose(
        'Initializing the CLI tool with the config and operator key and ID for different networks',
      );
      if (!options.telemetry) {
        logger.log(
          'You don\'t have telmetry enabled. You can enable it by running "hcli setup init --telemetry". This helps us improve the CLI tool by collecting anonymous usage data.',
        );
      }
      await setupCLI('init', options.telemetry, options.path);
      stateUtils.createUUID(); // Create a new UUID for the user if doesn't exist
    });

  setup
    .command('reload')
    .hook('preAction', async (thisCommand: Command) => {
      const command = [
        thisCommand.parent.action().name(),
        ...thisCommand.parent.args,
      ];
      if (stateUtils.isTelemetryEnabled()) {
        await telemetryUtils.recordCommand(command.join(' '));
      }
    })
    .description('Reload the CLI with operator key and ID')
    .option('--path <path>', 'Specify a custom path for the .env file')
    .option(
      '--telemetry',
      'Enable telemetry for Hedera to process anonymous usage data, disabled by default',
    )
    .action(async (options: ReloadOptions & { path?: string }) => {
      logger.verbose(
        'Reloading the CLI tool with operator key and ID for different networks',
      );
      if (!options.telemetry) {
        logger.log(
          'You don\'t have telmetry enabled. You can enable it by running "hcli setup reload --telemetry". This helps us improve the CLI tool by collecting anonymous usage data.',
        );
      }
      await setupCLI('reload', options.telemetry, options.path);
    });
};
