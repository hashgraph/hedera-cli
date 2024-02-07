import * as fs from 'fs';
import * as path from 'path';

import stateUtils from '../utils/state';
import enquirerUtils from '../utils/enquirer';
import stateController from '../state/stateController';
import { Logger } from '../utils/logger';

import type { Command, State } from '../../types';

const logger = Logger.getInstance();

/**
 * Remove the private keys and other sensitive info from the state object
 * Warning: It does not remove the private keys from scripts
 *
 * @param data Modify the state object to remove private keys and other sensitive info
 * @returns @type {State}
 */
function filterState(data: State) {
  const filteredState = { ...data };

  filteredState.previewnetOperatorId = '';
  filteredState.previewnetOperatorKey = '';
  filteredState.testnetOperatorId = '';
  filteredState.testnetOperatorKey = '';
  filteredState.mainnetOperatorId = '';
  filteredState.mainnetOperatorKey = '';

  Object.keys(filteredState.tokens).forEach((key) => {
    filteredState.tokens[key].keys = {
      adminKey: '',
      pauseKey: '',
      supplyKey: '',
      wipeKey: '',
      feeScheduleKey: '',
      treasuryKey: '',
      freezeKey: '',
      kycKey: '',
    };
  });

  // Remove the private keys from the accounts
  Object.keys(filteredState.accounts).forEach((alias) => {
    filteredState.accounts[alias].privateKey = '';
  });

  logger.log('Warning: The private keys were not removed from scripts');

  return filteredState;
}

/**
 * Create a backup of the state file
 *
 * @param backupAccounts Only backup the accounts from state
 * @param safe Remove the private keys from the backup file
 */
function backupState(name: string, backupAccounts: boolean, safe: boolean) {
  let data;
  try {
    const statePath = path.join(__dirname, '..', 'state', 'state.json');
    data = JSON.parse(fs.readFileSync(statePath, 'utf8')) as State;
  } catch (error) {
    logger.error('Unable to read state file:', error as object);
    process.exit(1);
  }

  // Create backup filename
  const timestamp = Date.now(); // UNIX timestamp in milliseconds
  let backupFilename = `state.backup.${timestamp}.json`;
  if (name) {
    backupFilename = `state.backup.${name}.json`;
  }

  if (safe) {
    data = filterState(data);
  }

  // Only backup accounts if the user specified the --accounts flag
  if (backupAccounts) {
    backupFilename = `accounts.backup.${timestamp}.json`;
    data = data.accounts;
  }
  const backupPath = path.join(__dirname, '..', 'state', backupFilename);

  try {
    fs.writeFileSync(backupPath, JSON.stringify(data, null, 2), 'utf8');
    logger.log(`Backup created with filename: ${backupFilename}`);
  } catch (error) {
    logger.error('Unable to create backup file:', error as object);
    process.exit(1);
  }
}

/**
 * Restore a backup of the state file
 *
 * @param filename File containing the state backup
 */
function restoreState(
  filename: string,
  restoreAccounts: boolean,
  restoreTokens: boolean,
  restoreScripts: boolean,
) {
  let data;
  try {
    const backupPath = path.join(__dirname, '..', 'state', filename);
    data = JSON.parse(fs.readFileSync(backupPath, 'utf8')) as State;
  } catch (error) {
    logger.error('Unable to read backup file:', error as object);
    process.exit(1);
  }

  if (!restoreAccounts && !restoreTokens && !restoreScripts) {
    stateController.saveState(data);
    logger.log('Backup restored successfully');
    return;
  }

  if (restoreAccounts) {
    stateController.saveKey('accounts', data.accounts || {});
  }

  if (restoreTokens) {
    stateController.saveKey('tokens', data.tokens || {});
  }

  if (restoreScripts) {
    stateController.saveKey('scripts', data.scripts || {});
  }

  logger.log('Backup restored successfully');
}

export default (program: any) => {
  const network = program.command('backup');

  network
    .command('create')
    .hook('preAction', (thisCommand: Command) => {
      const command = [
        thisCommand.parent.action().name(),
        ...thisCommand.parent.args,
      ];
      stateUtils.recordCommand(command);
    })
    .description('Create a backup of the state.json file')
    .option('--accounts', 'Backup the accounts')
    .option('--safe', 'Remove the private keys from the backup')
    .option('--name <name>', 'Name of the backup file')
    .action((options: BackupOptions) => {
      logger.verbose('Creating backup of state');
      backupState(options.name, options.accounts, options.safe);
    });

  network
    .command('restore')
    .hook('preAction', (thisCommand: Command) => {
      const command = [
        thisCommand.parent.action().name(),
        ...thisCommand.parent.args,
      ];
      stateUtils.recordCommand(command);
    })
    .description('Restore a backup of the full state')
    .option('-f, --file <filename>', 'Filename containing the state backup')
    .option('--restore-accounts', 'Restore the accounts', false)
    .option('--restore-tokens', 'Restore the tokens', false)
    .option('--restore-scripts', 'Restore the scripts', false)
    .action(async (options: RestoreOptions) => {
      logger.verbose('Restoring backup of state');

      let filename = options.file;
      if (!options.file) {
        const files = fs.readdirSync(path.join(__dirname, '..', 'state'));

        // filter out the pattern state.backup.TIMESTAMP.json
        const pattern = /^state\.backup\.\d+\.json$/;
        const backups = files.filter((file) => pattern.test(file));

        if (backups.length === 0) {
          logger.error('No backup files found');
          process.exit(1);
        }

        try {
          filename = await enquirerUtils.createPrompt(
            backups,
            'Choose a backup:',
          );
        } catch (error) {
          logger.error('Unable to read backup file:', error as object);
          process.exit(1);
        }
      }

      restoreState(
        filename,
        options.restoreAccounts,
        options.restoreTokens,
        options.restoreScripts,
      );
    });
};

interface BackupOptions {
  accounts: boolean;
  safe: boolean;
  name: string;
}

interface RestoreOptions {
  file: string;
  restoreAccounts: boolean;
  restoreTokens: boolean;
  restoreScripts: boolean;
}
