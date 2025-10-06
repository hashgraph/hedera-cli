import * as fs from 'fs';
import * as path from 'path';

import { telemetryPreAction } from './shared/telemetryHook';
import enquirerUtils from '../utils/enquirer';
import {
  saveKey as storeSaveKey,
  updateState as storeUpdateState,
} from '../state/store';
import { addAccount, addToken, addScript } from '../state/mutations';
import { Logger } from '../utils/logger';
import { DomainError } from '../utils/errors';
import { wrapAction } from './shared/wrapAction';

import type { State, Account, Token, Script } from '../../types';
import type { StoreState } from '../state/store';
import type { Command as CommanderCommand } from 'commander';

// Local narrowed shapes for partial restores
type AccountsOnly = Record<string, Account>;
interface FullStateLike extends State {}

function isFullStateLike(v: unknown): v is FullStateLike {
  if (!v || typeof v !== 'object') return false;
  return 'network' in v && 'accounts' in v; // light heuristic
}

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
): void {
  // Can be full state or subset (accounts object) when --accounts flag used
  let data: State | AccountsOnly;

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

  if (safe && isFullStateLike(data)) data = filterState(data);

  // Only backup accounts if the user specified the --accounts flag
  if (backupAccounts) {
    backupFilename = `accounts.backup.${timestamp}.json`;
    if (name) {
      backupFilename = `accounts.backup.${name}.json`;
    }
    if (isFullStateLike(data)) data = data.accounts;
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
): void {
  let raw: unknown;
  try {
    const backupPath = path.join(__dirname, '..', 'state', filename);
    raw = JSON.parse(fs.readFileSync(backupPath, 'utf8')) as unknown;
  } catch {
    throw new DomainError('Unable to read backup file');
  }

  if (!isFullStateLike(raw)) {
    // Treat as accounts-only backup
    logger.log('Importing account backup');
    storeSaveKey('accounts', (raw as AccountsOnly) || {});
    logger.log('Account backup restored successfully');
    return;
  }

  const data: State = raw;

  if (!restoreAccounts && !restoreTokens && !restoreScripts) {
    // Full overwrite
    storeUpdateState((draft: StoreState) => {
      draft.accounts = data.accounts || {};
      draft.tokens = data.tokens || {};
      draft.scripts = data.scripts || {};
      draft.topics = data.topics || {};
    });
    logger.log('Backup restored successfully');
    return;
  }

  if (restoreAccounts && data.accounts) {
    Object.values(data.accounts).forEach((acc: Account) =>
      addAccount(acc, true),
    );
  }
  if (restoreTokens && data.tokens) {
    Object.values(data.tokens).forEach((tok: Token) => addToken(tok, true));
  }
  if (restoreScripts && data.scripts) {
    Object.values(data.scripts).forEach((scr: Script) => addScript(scr, true));
  }
  logger.log('Backup restored successfully');
}

export default (program: CommanderCommand) => {
  const backup = program.command('backup');

  backup
    .command('create')
    .hook('preAction', telemetryPreAction)
    .description('Create a backup of the state.json file')
    .option('--accounts', 'Backup the accounts')
    .option('--safe', 'Remove the private keys from the backup')
    .option('--name <name>', 'Name of the backup file')
    .option('--path <path>', 'Specify a custom path to store the backup')
    .action(
      wrapAction<BackupOptions>(
        (options) => {
          backupState(
            options.name,
            options.accounts,
            options.safe,
            options.path || '',
          );
        },
        { log: 'Creating backup of state' },
      ),
    );

  backup
    .command('restore')
    .hook('preAction', telemetryPreAction)
    .description('Restore a backup of the full state')
    .option('-f, --file <filename>', 'Filename containing the state backup')
    .option('--restore-accounts', 'Restore the accounts', false)
    .option('--restore-tokens', 'Restore the tokens', false)
    .option('--restore-scripts', 'Restore the scripts', false)
    .action(
      wrapAction<RestoreOptions>(
        async (options) => {
          let filename = options.file;
          if (!options.file) {
            const files = fs.readdirSync(path.join(__dirname, '..', 'state'));
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
        },
        { log: 'Restoring backup of state' },
      ),
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
