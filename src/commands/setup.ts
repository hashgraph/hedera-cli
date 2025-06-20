import * as dotenv from 'dotenv';

import stateUtils from '../utils/state';
import telemetryUtils from '../utils/telemetry';
import config from '../state/config';
import { Logger } from '../utils/logger';
import accountUtils from '../utils/account';
import setupUtils from '../utils/setup';
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
 * @param envPath Path to the .env file
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

  // Extract operator key and ID from environment variables
  const {
    TESTNET_OPERATOR_KEY,
    TESTNET_OPERATOR_ID,
    MAINNET_OPERATOR_KEY,
    MAINNET_OPERATOR_ID,
    PREVIEWNET_OPERATOR_ID,
    PREVIEWNET_OPERATOR_KEY,
    LOCALNET_OPERATOR_ID,
    LOCALNET_OPERATOR_KEY,
    TELEMETRY_URL,
  } = process.env;

  let mainnetOperatorId = MAINNET_OPERATOR_ID || '';
  let mainnetOperatorKey = MAINNET_OPERATOR_KEY || '';
  let testnetOperatorId = TESTNET_OPERATOR_ID || '';
  let testnetOperatorKey = TESTNET_OPERATOR_KEY || '';
  let previewnetOperatorId = PREVIEWNET_OPERATOR_ID || '';
  let previewnetOperatorKey = PREVIEWNET_OPERATOR_KEY || '';
  let localnetOperatorId = LOCALNET_OPERATOR_ID || '';
  let localnetOperatorKey = LOCALNET_OPERATOR_KEY || '';

  // Validate operator key and ID pairs for previewnet, testnet, and mainnet
  if (
    (PREVIEWNET_OPERATOR_KEY && !PREVIEWNET_OPERATOR_ID) ||
    (!PREVIEWNET_OPERATOR_KEY && PREVIEWNET_OPERATOR_ID)
  ) {
    logger.error(
      'Both PREVIEWNET_OPERATOR_KEY and PREVIEWNET_OPERATOR_ID must be defined together in the .env file.',
    );
    process.exit(1);
  }

  if (
    (TESTNET_OPERATOR_KEY && !TESTNET_OPERATOR_ID) ||
    (!TESTNET_OPERATOR_KEY && TESTNET_OPERATOR_ID)
  ) {
    logger.error(
      'Both TESTNET_OPERATOR_KEY and TESTNET_OPERATOR_ID must be defined together in the .env file.',
    );
    process.exit(1);
  }

  if (
    (MAINNET_OPERATOR_KEY && !MAINNET_OPERATOR_ID) ||
    (!MAINNET_OPERATOR_KEY && MAINNET_OPERATOR_ID)
  ) {
    logger.error(
      'Both MAINNET_OPERATOR_KEY and MAINNET_OPERATOR_ID must be defined together in the .env file.',
    );
    process.exit(1);
  }

  if (
    (LOCALNET_OPERATOR_KEY && !LOCALNET_OPERATOR_ID) ||
    (!LOCALNET_OPERATOR_KEY && LOCALNET_OPERATOR_ID)
  ) {
    logger.error(
      'Both LOCALNET_OPERATOR_KEY and LOCALNET_OPERATOR_ID must be defined together in the .env file.',
    );
    process.exit(1);
  }

  // Only write a fresh state file if the user is running the init command
  if (action === 'init') {
    setupState();
  }

  await verifyOperatorBalance(localnetOperatorId, 'localnet');
  await verifyOperatorBalance(previewnetOperatorId, 'previewnet');
  await verifyOperatorBalance(testnetOperatorId, 'testnet');
  await verifyOperatorBalance(mainnetOperatorId, 'mainnet');

  setupUtils.setupOperatorAccounts(
    testnetOperatorId,
    testnetOperatorKey,
    mainnetOperatorId,
    mainnetOperatorKey,
    previewnetOperatorId,
    previewnetOperatorKey,
    localnetOperatorId,
    localnetOperatorKey,
  );

  // Set telemetry server URL
  let telemetryServer =
    TELEMETRY_URL || 'https://hedera-cli-telemetry.onrender.com/track';
  stateController.saveKey('telemetryServer', telemetryServer);
  if (telemetry === true) {
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
