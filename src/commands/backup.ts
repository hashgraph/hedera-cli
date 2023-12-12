import * as fs from 'fs';
import * as path from 'path';

import { recordCommand } from '../state/stateService';

import type { Command, State } from '../../types';

export default (program: any) => {
  const network = program.command('backup');

  network
    .command('create')
    .hook('preAction', (thisCommand: Command) => {
      recordCommand(thisCommand.parent.args);
    })
    .description('Create a backup of the config.json file')
    .option('--accounts', 'Backup the accounts')
    .option('--safe', 'Remove the private keys from the backup')
    .action((options: BackupOptions) => {
      backupState(options.accounts, options.safe);
    });
};

/**
 * Create a backup of the state file
 * 
 * @param backupAccounts Only backup the accounts from state
 * @param safe Remove the private keys from the backup file 
 */
function backupState(backupAccounts: boolean, safe: boolean) {
  const timestamp = Date.now(); // UNIX timestamp in milliseconds

  let data;
  try {
    const statePath = path.join(__dirname, '..', 'state', 'state.json');
    data = JSON.parse(fs.readFileSync(statePath, 'utf8')) as State;
  } catch (error) {
    console.error('Error reading the state file:', error);
    return;
  }

  // Create backup filename
  let backupFilename = `state.backup.${timestamp}.json`;

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
  } catch (error) {
    console.error('Error creating the backup file:', error);
    return;
  }

  console.log(`Backup created successfully: ${backupFilename}`);
}

/**
 * Remove the private keys and other sensitive info from the state object
 * Warning: It does not remove the private keys from scripts
 * 
 * @param data Modify the state object to remove private keys and other sensitive info
 * @returns @type {State}
 */
function filterState(data: State) {
  const filteredState = { ...data };

  filteredState.operatorId = '';
  filteredState.operatorKey = '';

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

  console.log('Warning: The private keys were not removed from scripts');

  return filteredState;
}

interface BackupOptions {
  accounts: boolean;
  safe: boolean;
}