import * as dotenv from 'dotenv';

import stateUtils from '../utils/state';
import telemetryUtils from '../utils/telemetry';
import config from '../state/config';
import { Logger } from '../utils/logger';
import accountUtils from '../utils/account';
import { setupOperatorAccount } from '../utils/setup';
import stateController from '../state/stateController';

import type { Command } from '../../types';

const logger = Logger.getInstance();

interface SetupOptions {
  telemetry: boolean;
}

interface ReloadOptions {
  telemetry: boolean;
}

/**
 * @description Setup the state file with the init config
 */
function setupState(): void {
  const newState = {
    ...config,
  };

  stateController.saveState(newState);
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
): Promise<void> {
  // Load environment variables from .env file
  const envConfig = dotenv.config();

  // Check for errors in loading .env file
  if (envConfig.error) {
    logger.error(`Can't load .env file: ${envConfig.error.message}`);
    process.exit(1);
  }

  // rework this to use the networks object from config
  // This will allow us to add more networks in the future without changing this code
  if (!config.networks || Object.keys(config.networks).length === 0) {
    logger.error(
      'No networks found in the config. Please check your config file.',
    );
    process.exit(1);
  }
  // check against the know list of networks
  const knownNetworks = ['mainnet', 'testnet', 'previewnet', 'localnet'];
  const networkNames = Object.keys(config.networks);
  // report if any network is missing from the known list
  const missingNetworks = networkNames.filter(
    (networkName) => !knownNetworks.includes(networkName),
  );
  //report if any network is missing from the known list
  if (missingNetworks.length > 0) {
    logger.error(
      `The following networks are required: [${missingNetworks.join(', ')}]. Please check your config file.`,
    );
    process.exit(1);
  }

  // Lets check each network in the config based on the network name
  for (const networkName of networkNames) {
    const network = config.networks[networkName];
    if (!network.operatorId || !network.operatorKey) {
      logger.error(
        `Operator ID and Key for ${networkName} are not defined in the config. Please check your config file.`,
      );
      process.exit(1);
    }
    setupOperatorAccount(network.operatorId, network.operatorKey, networkName);

    // Check if the operator account has enough balance to pay for transactions
    await verifyOperatorBalance(network.operatorId, networkName);
  }

  // // Extract operator key and ID from environment variables
  const { TELEMETRY_URL } = process.env;

  // Only write a fresh state file if the user is running the init command
  if (action === 'init') {
    setupState();
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
    .action(async (options: SetupOptions) => {
      logger.verbose(
        'Initializing the CLI tool with the config and operator key and ID for different networks',
      );
      if (!options.telemetry) {
        logger.log(
          'You don\'t have telmetry enabled. You can enable it by running "hcli setup init --telemetry". This helps us improve the CLI tool by collecting anonymous usage data.',
        );
      }
      await setupCLI('init', options.telemetry);
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
    .action(async (options: ReloadOptions) => {
      logger.verbose(
        'Reloading the CLI tool with operator key and ID for different networks',
      );
      if (!options.telemetry) {
        logger.log(
          'You don\'t have telmetry enabled. You can enable it by running "hcli setup reload --telemetry". This helps us improve the CLI tool by collecting anonymous usage data.',
        );
      }
      await setupCLI('reload', options.telemetry);
    });
};
