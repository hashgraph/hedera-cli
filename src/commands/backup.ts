import * as fs from 'fs';
import * as path from 'path';

import { recordCommand } from '../state/stateService';

import type { Command } from '../../types';

export default (program: any) => {
  const network = program.command('backup');

  network
    .command('create')
    .hook('preAction', (thisCommand: Command) => {
      recordCommand(thisCommand.parent.args);
    })
    .description('Create a backup of the config.json file')
    .option('--accounts', 'Backup the accounts')
    .action((options: BackupOptions) => {
      backupState(options.accounts);
    });
};

function backupState(backupAccounts: boolean) {
  const timestamp = Date.now(); // UNIX timestamp in milliseconds

  let data;
  try {
    const statePath = path.join(__dirname, '..', 'state', 'state.json');
    data = fs.readFileSync(statePath, 'utf8');
  } catch (error) {
    console.error('Error reading the state file:', error);
    return;
  }

  // Create backup filename
  let backupFilename = `state.backup.${timestamp}.json`;
  
  // Only backup accounts if the user specified the --accounts flag
  if (backupAccounts) {
    backupFilename = `accounts.backup.${timestamp}.json`;
    data = JSON.stringify(JSON.parse(data).accounts, null, 2);
  }
  const backupPath = path.join(__dirname, '..', 'state', backupFilename);


  try {
    fs.writeFileSync(backupPath, data, 'utf8');
  } catch (error) {
    console.error('Error creating the backup file:', error);
    return;
  }

  console.log(`Backup created successfully: ${backupFilename}`);
}

interface BackupOptions {
  accounts: boolean;
}