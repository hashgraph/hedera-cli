import * as path from 'path';
import * as dotenv from 'dotenv';
import * as os from 'os';

import { recordCommand } from '../state/stateService';
import stateController from '../state/stateController';
import config from '../state/config';
import { Logger } from '../utils/logger';
import accountUtils from '../utils/account';

import type { Command } from '../../types';

const logger = Logger.getInstance();

export default (program: any) => {
  const setup = program.command('setup').description('Setup Hedera CLI');

  setup
    .command('init')
    .hook('preAction', (thisCommand: Command) => {
      const command = [
        thisCommand.parent.action().name(),
        ...thisCommand.parent.args,
      ];
      recordCommand(command);
    })
    .description('Setup the CLI with operator key and ID')
    .option('--path <path>', 'Specify a custom path for the .env file')
    .action(async (options: SetupOptions) => {
      logger.verbose(
        'Initializing the CLI tool with the config and operator key and ID for different networks',
      );

      setupCLI('init', options.path);
    });

  setup
    .command('reload')
    .hook('preAction', (thisCommand: Command) => {
      const command = [
        thisCommand.parent.action().name(),
        ...thisCommand.parent.args,
      ];
      recordCommand(command);
    })
    .description('Reload the CLI with operator key and ID')
    .option('--path <path>', 'Specify a custom path for the .env file')
    .action(() => {
      logger.verbose(
        'Reloading the CLI tool with operator key and ID for different networks',
      );
      setupCLI('reload');
    });
};

/**
 * @description Setup the CLI with operator key and ID for different networks
 * @param action Action to perform (init or reload)
 * @param envPath Path to the .env file
 */
async function setupCLI(action: string, envPath: string = ''): Promise<void> {
  let finalPath = '';
  if (envPath !== '') {
    finalPath = path.normalize(envPath);
  } else {
    try {
      const homePath = os.homedir();
      if (!homePath) {
        logger.error('Can not find home directory');
        process.exit(1);
      }
      finalPath = path.join(homePath, '.hedera/.env');
    } catch (error) {
      logger.error('Failed to retrieve home directory');
      process.exit(1);
    }
 }

  // Path to the .env file in the .hedera directory in the user's home directory
  
  // Load environment variables from .env file
  const envConfig = dotenv.config({ path: finalPath });

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
  } = process.env;

  let mainnetOperatorId = MAINNET_OPERATOR_ID || '';
  let mainnetOperatorKey = MAINNET_OPERATOR_KEY || '';
  let testnetOperatorId = TESTNET_OPERATOR_ID || '';
  let testnetOperatorKey = TESTNET_OPERATOR_KEY || '';
  let previewnetOperatorId = PREVIEWNET_OPERATOR_ID || '';
  let previewnetOperatorKey = PREVIEWNET_OPERATOR_KEY || '';

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

  // Only write a fresh state file if the user is running the init command
  if (action === 'init') {
    setupState();
  }

  await verifyOperatorBalance(previewnetOperatorId);
  await verifyOperatorBalance(testnetOperatorId);
  await verifyOperatorBalance(mainnetOperatorId);

  setupOperatorAccounts(
    testnetOperatorId,
    testnetOperatorKey,
    mainnetOperatorId,
    mainnetOperatorKey,
    previewnetOperatorId,
    previewnetOperatorKey,
  );
}

/**
 * @description Verify that the operator account has enough balance to pay for transactions (at least 1 Hbar)
 * @param operatorId Operator ID to check balance for
 */
async function verifyOperatorBalance(operatorId: string): Promise<void> {
  // Skip if operator ID is not defined
  if (operatorId) {
    const balance = await accountUtils.getAccountHbarBalance(operatorId);
    if (balance < 100000000) {
      logger.error(
        `The operator account ${operatorId} does not have enough balance to pay for transactions (less than 1 Hbar). Please add more balance to the account.`,
      );
      process.exit(1);
    }
  }
}

/**
 * @description Setup operator accounts for previewnet, testnet, and mainnet in the state file
 */
function setupOperatorAccounts(
  testnetOperatorId: string,
  testnetOperatorKey: string,
  mainnetOperatorId: string,
  mainnetOperatorKey: string,
  previewnetOperatorId: string,
  previewnetOperatorKey: string,
): void {
  const state = stateController.getAll();
  let newState = { ...state };
  newState.testnetOperatorKey = testnetOperatorKey;
  newState.testnetOperatorId = testnetOperatorId;
  newState.mainnetOperatorKey = mainnetOperatorKey;
  newState.mainnetOperatorId = mainnetOperatorId;
  newState.previewnetOperatorId = previewnetOperatorId;
  newState.previewnetOperatorKey = previewnetOperatorKey;

  newState.network = 'testnet';

  stateController.saveState(newState);
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

interface SetupOptions {
  path: string;
}