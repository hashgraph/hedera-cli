import * as fs from 'fs';
import * as path from 'path';

import stateUtils from '../utils/state';
import telemetryUtils from '../utils/telemetry';
import enquirerUtils from '../utils/enquirer';
import {
  saveKey as storeSaveKey,
  updateState as storeUpdateState,
} from '../state/store';
import { addAccount, addToken, addScript, addTopic } from '../state/mutations';
import { Logger } from '../utils/logger';
import { DomainError, exitOnError } from '../utils/errors';

import type { Command, State, Account } from '../../types';

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
  Object.keys(filteredState.accounts).forEach((name) => {
    filteredState.accounts[name].privateKey = '';
  });

  // Remove private keys from topics
  Object.keys(filteredState.topics).forEach((topicId) => {
    filteredState.topics[topicId].adminKey = '';
    filteredState.topics[topicId].submitKey = '';
  });

  return filteredState;
}

/**
 * Create a backup of the state file
 *
 * @parm name Name of the backup file
 * @param backupAccounts Only backup the accounts from state
 * @param safe Remove the private keys from the backup file
 * @param storagePath Custom path to store the backup (useful for adding it to a testing suite)
 */
function backupState(
  name: string,
  backupAccounts: boolean,
  safe: boolean,
  storagePath: string,
) {
  // Can be full state or subset (accounts object) when --accounts flag used
  let data: State | Record<string, Account>;

  try {
    const statePath = path.join(__dirname, '..', 'state', 'state.json');
    data = JSON.parse(fs.readFileSync(statePath, 'utf8')) as State;
  } catch (error) {
    throw new DomainError('Unable to read state file');
  }

  // Create backup filename
  const timestamp = Date.now(); // UNIX timestamp in milliseconds
  let backupFilename = `state.backup.${timestamp}.json`;
  if (name) {
    backupFilename = `state.backup.${name}.json`;
  }

  if (safe && 'network' in data) {
    data = filterState(data);
  }

  // Only backup accounts if the user specified the --accounts flag
  if (backupAccounts) {
    backupFilename = `accounts.backup.${timestamp}.json`;
    if (name) {
      backupFilename = `accounts.backup.${name}.json`;
    }
    if ('accounts' in data) {
      data = data.accounts;
    }
  }

  if (storagePath !== '' && !path.isAbsolute(storagePath)) {
    throw new Error('Invalid storage path: Must be an absolute path');
  }

  const backupPath =
    storagePath !== ''
      ? path.join(storagePath, backupFilename) // custom path
      : path.join(__dirname, '..', 'state', backupFilename); // default path

  try {
    fs.writeFileSync(backupPath, JSON.stringify(data, null, 2), 'utf8');
    logger.log(`Backup created with filename: ${backupFilename}`);
  } catch (error) {
    throw new DomainError('Unable to create backup file');
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
  let raw: unknown;
  try {
    const backupPath = path.join(__dirname, '..', 'state', filename);
    raw = JSON.parse(fs.readFileSync(backupPath, 'utf8'));
  } catch (error) {
    throw new DomainError('Unable to read backup file');
  }

  // Distinguish between full state and accounts-only backup
  const isFullState =
    typeof raw === 'object' && raw !== null && 'network' in (raw as any);

  if (!isFullState) {
    logger.log('Importing account backup');
    storeSaveKey(
      'accounts' as any,
      (raw as Record<string, Account>) || ({} as any),
    );
    logger.log('Account backup restored successfully');
    return;
  }

  const data = raw as State;

  if (!restoreAccounts && !restoreTokens && !restoreScripts) {
    // full overwrite
    storeUpdateState((draft: any) => {
      draft.accounts = data.accounts || {};
      draft.tokens = data.tokens || {};
      draft.scripts = data.scripts || {};
      draft.topics = data.topics || {};
    });
    logger.log('Backup restored successfully');
    return;
  }

  // granular restore merges into existing using helpers
  if (restoreAccounts && data.accounts) {
    Object.values(data.accounts).forEach((acc: any) => addAccount(acc, true));
  }
  if (restoreTokens && data.tokens) {
    Object.values(data.tokens).forEach((tok: any) => addToken(tok, true));
  }
  if (restoreScripts && data.scripts) {
    Object.values(data.scripts).forEach((scr: any) => {
      // scr already internal name keyed when coming from backup? If using same shape as state.scripts values we keep name field
      addScript(scr, true);
    });
  }
  logger.log('Backup restored successfully');
}

export default (program: any) => {
  const network = program.command('backup');

  network
    .command('create')
    .hook('preAction', async (thisCommand: Command) => {
      const command = [
        thisCommand.parent.action().name(),
        ...thisCommand.parent.args,
      ];
      if (stateUtils.isTelemetryEnabled()) {
        await telemetryUtils.recordCommand(command.join(' '));
      }
    })
    .description('Create a backup of the state.json file')
    .option('--accounts', 'Backup the accounts')
    .option('--safe', 'Remove the private keys from the backup')
    .option('--name <name>', 'Name of the backup file')
    .option('--path <path>', 'Specify a custom path to store the backup')
    .action(
      exitOnError((options: BackupOptions) => {
        logger.verbose('Creating backup of state');
        backupState(
          options.name,
          options.accounts,
          options.safe,
          options.path || '',
        );
      }),
    );

  network
    .command('restore')
    .hook('preAction', async (thisCommand: Command) => {
      const command = [
        thisCommand.parent.action().name(),
        ...thisCommand.parent.args,
      ];
      if (stateUtils.isTelemetryEnabled()) {
        await telemetryUtils.recordCommand(command.join(' '));
      }
    })
    .description('Restore a backup of the full state')
    .option('-f, --file <filename>', 'Filename containing the state backup')
    .option('--restore-accounts', 'Restore the accounts', false)
    .option('--restore-tokens', 'Restore the tokens', false)
    .option('--restore-scripts', 'Restore the scripts', false)
    .action(
      exitOnError(async (options: RestoreOptions) => {
        logger.verbose('Restoring backup of state');

        let filename = options.file;
        if (!options.file) {
          const files = fs.readdirSync(path.join(__dirname, '..', 'state'));

          // filter out the pattern *.backup.*.json like accounts.backup.7-nov-2024.json
          const pattern = /^.*\.backup\..*\.json$/;
          const backups = files.filter((file) => pattern.test(file));

          if (backups.length === 0) {
            throw new DomainError('No backup files found');
          }

          try {
            filename = await enquirerUtils.createPrompt(
              backups,
              'Choose a backup:',
            );
          } catch (error) {
            throw new DomainError('Unable to read backup file');
          }
        }

        restoreState(
          filename,
          options.restoreAccounts,
          options.restoreTokens,
          options.restoreScripts,
        );
      }),
    );
};

interface BackupOptions {
  accounts: boolean;
  safe: boolean;
  name: string;
  path: string;
}

interface RestoreOptions {
  file: string;
  restoreAccounts: boolean;
  restoreTokens: boolean;
  restoreScripts: boolean;
}
