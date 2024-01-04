import axios from 'axios';

import { recordCommand } from '../../state/stateService';
import { Logger } from '../../utils/logger';
import stateController from '../../state/stateController';

import type { Account, Command, Script, Token } from '../../../types';

const logger = Logger.getInstance();

export default (program: any) => {
  program
    .command('download')
    .hook('preAction', (thisCommand: Command) => {
      const command = [
        thisCommand.parent.action().name(),
        ...thisCommand.parent.args,
      ];
      recordCommand(command);
    })
    .description(
      'Download state from a URL and merge it with the current state',
    )
    .requiredOption('--url <url>', 'URL of script to download')
    .option('--merge', 'Merge state with downloaded state', false)
    .option('--overwrite', 'Overwrite state with downloaded state', false)
    .action(async (options: DownloadStateOptions) => {
      logger.verbose(`Downloading state from ${options.url}`);

      if (options.merge && options.overwrite) {
        logger.error('Cannot use both --merge and --overwrite');
        process.exit(1);
      }

      const data = await downloadState(options.url);
      importState(data, options.overwrite, options.merge);
    });
};

async function downloadState(url: string): Promise<any> {
  let data;
  try {
    const response = await axios.get(url);
    data = response.data;
  } catch (error) {
    if (axios.isAxiosError(error)) {
      logger.error(error.message);
    } else {
      logger.error('Unexpected error downloading file', error as object);
    }
    process.exit(1);
  }

  return data;
}

function importState(data: any, overwrite: boolean, merge: boolean) {
  if (overwrite) {
    stateController.saveKey('accounts', data.accounts || {});
    stateController.saveKey('tokens', data.tokens || {});
    stateController.saveKey('scripts', data.scripts || {});
    logger.log('State overwritten successfully');
    process.exit(0);
  }

  if (data.scripts && Object.entries(data.scripts).length > 0) {
    addScripts(data.scripts, merge);
  }

  if (data.accounts && Object.entries(data.accounts).length > 0) {
    addAccounts(data.accounts, merge);
  }

  if (data.tokens && Object.entries(data.tokens).length > 0) {
    addTokens(data.tokens, merge);
  }
}

function addScripts(importedScripts: Script[], merge: boolean) {
  const scripts: Record<string, Script> = stateController.get('scripts');
  Object.values(importedScripts).forEach((script: Script) => {
    const scriptName = `script-${script.name}`;
    const existingScript = scripts[scriptName];

    if (!merge && existingScript) {
      logger.error(`Script with name ${scriptName} already exists`);
      process.exit(1);
    }

    if (merge && existingScript) {
      // continue to add values to existing state (merging)
      logger.log(`Script "${script.name}" already exists, overwriting it`);
    }

    scripts[scriptName] = {
      name: script.name,
      creation: Date.now(),
      commands: script.commands,
      args: {},
    };
    stateController.saveKey('scripts', scripts);
    logger.log(`Script "${script.name}" added successfully`);
  });
}

function addAccounts(importedAccounts: Account[], merge: boolean) {
  const accounts: Record<string, Account> = stateController.get('accounts');
  Object.values(importedAccounts).forEach((account: Account) => {
    const existingAccount = accounts[account.alias];

    if (!merge && existingAccount) {
      logger.error(`Account with name ${account} already exists`);
      process.exit(1);
    }

    if (merge && existingAccount) {
      logger.log(
        `Account "${account.alias}" already exists, merging it with the new account details`,
      );
    }

    accounts[account.alias] = account;
    stateController.saveKey('accounts', accounts);
    logger.log(
      `Account "${account.alias}" with ID ${account.accountId} added successfully`,
    );
  });
}

function addTokens(importedTokens: Token[], merge: boolean) {
  const tokens: Record<string, Token> = stateController.get('tokens');
  Object.values(importedTokens).forEach((token: Token) => {
    const existingToken = tokens[token.tokenId];

    if (!merge && existingToken) {
      logger.error(`Token with ID ${token.tokenId} already exists`);
      process.exit(1);
    }

    if (merge && existingToken) {
      logger.log(`Token ${token.tokenId} already exists, overwriting it`);
    }

    tokens[token.tokenId] = token;
    stateController.saveKey('tokens', tokens);
    logger.log(
      `Token ${token.tokenId} with name "${token.name}" added successfully`,
    );
  });
}

interface DownloadStateOptions {
  url: string;
  merge: boolean;
  overwrite: boolean;
}
