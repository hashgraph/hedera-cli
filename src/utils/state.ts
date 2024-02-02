import { Client, AccountId, PrivateKey } from '@hashgraph/sdk';
import axios from 'axios';

import { Logger } from '../utils/logger';
import stateController from '../state/stateController';

import type { Account, Script, Token, Topic } from '../../types';

const logger = Logger.getInstance();

/** hook (middleware)
 * @example command ['account', 'create', '-b', '1000', '-t', 'ed25519']
 */
function recordCommand(command: string[]): void {
  const state = stateController.getAll();
  if (state.recording === 1) {
    state.scripts[state.recordingScriptName].commands.push(command.join(' '));

    stateController.saveState(state);
  }
}

function getMirrorNodeURL(): string {
  const network = stateController.get('network');
  const mirrorNodeURL =
    network === 'testnet'
      ? stateController.get('mirrorNodeTestnet')
      : stateController.get('mirrorNodeMainnet');
  return mirrorNodeURL;
}

function getHederaClient(): Client {
  const state = stateController.getAll();
  let client: Client;
  let operatorId, operatorKey;

  switch (state.network) {
    case 'mainnet':
      client = Client.forMainnet();
      operatorId = state.mainnetOperatorId;
      operatorKey = state.mainnetOperatorKey;
      break;
    case 'testnet':
      client = Client.forTestnet();
      operatorId = state.testnetOperatorId;
      operatorKey = state.testnetOperatorKey;
      break;
    case 'previewnet':
      client = Client.forPreviewnet();
      operatorId = state.previewnetOperatorId;
      operatorKey = state.previewnetOperatorKey;
      break;
    default:
      logger.error('Invalid network name');
      process.exit(1);
  }

  if (operatorId === '' || operatorKey === '') {
    logger.error(`operator key and ID not set for ${state.network}`);
    process.exit(1);
  }

  return client.setOperator(
    AccountId.fromString(operatorId),
    PrivateKey.fromStringDer(operatorKey),
  );
}

/**
 * @returns {string} network name
 */
function getNetwork() {
  const state = stateController.getAll();
  return state.network;
}

function getOperator(): { operatorId: string; operatorKey: string } {
  const state = stateController.getAll();
  let operatorId, operatorKey;

  switch (state.network) {
    case 'mainnet':
      operatorId = state.mainnetOperatorId;
      operatorKey = state.mainnetOperatorKey;
      break;
    case 'testnet':
      operatorId = state.testnetOperatorId;
      operatorKey = state.testnetOperatorKey;
      break;
    case 'previewnet':
      operatorId = state.previewnetOperatorId;
      operatorKey = state.previewnetOperatorKey;
      break;
    default:
      logger.error('Invalid network name');
      process.exit(1);
  }

  if (operatorId === '' || operatorKey === '') {
    logger.error(`operator key and ID not set for ${state.network}`);
    process.exit(1);
  }

  return {
    operatorId,
    operatorKey,
  };
}

function switchNetwork(name: string) {
  if (!['mainnet', 'testnet', 'previewnet'].includes(name)) {
    logger.error(
      'Invalid network name. Available networks: mainnet, testnet, previewnet',
    );
    process.exit(1);
  }

  const state = stateController.getAll();
  let operatorId, operatorKey;
  switch (state.network) {
    case 'mainnet':
      operatorId = state.mainnetOperatorId;
      operatorKey = state.mainnetOperatorKey;
      break;
    case 'testnet':
      operatorId = state.testnetOperatorId;
      operatorKey = state.testnetOperatorKey;
      break;
    case 'previewnet':
      operatorId = state.previewnetOperatorId;
      operatorKey = state.previewnetOperatorKey;
      break;
  }

  if (operatorId === '' || operatorKey === '') {
    logger.error(`operator key and ID not set for ${state.network}`);
    process.exit(1);
  }

  stateController.saveKey('network', name);
}

function addTokenAssociation(
  tokenId: string,
  accountId: string,
  alias: string,
) {
  const tokens = stateController.get('tokens');
  const token: Token = tokens[tokenId];
  token.associations.push({ alias, accountId });
  tokens[tokenId] = token;
  stateController.saveKey('tokens', tokens);
}

/* Accounts */
function getAccountById(accountId: string): Account | undefined {
  const accounts: Record<string, Account> = stateController.get('accounts');
  const account = Object.values(accounts).find(
    (account: Account) => account.accountId === accountId,
  );
  return account;
}

function getAccountByAlias(alias: string): Account | undefined {
  const accounts: Record<string, Account> = stateController.get('accounts');
  return accounts[alias];
}

function getAccountByIdOrAlias(accountIdOrAlias: string): Account {
  const accountIdPattern = /^0\.0\.\d+$/;
  const match = accountIdOrAlias.match(accountIdPattern);
  let account;
  if (match) {
    account = getAccountById(accountIdOrAlias);
  } else {
    account = getAccountByAlias(accountIdOrAlias);
  }

  if (!account) {
    logger.error(`Account not found: ${accountIdOrAlias}`);
    process.exit(1);
  }

  return account;
}

function startScriptExecution(name: string): void {
  const state = stateController.getAll();
  state.scriptExecutionName = name;
  state.scriptExecution = 1;
  stateController.saveState(state);
}

function stopScriptExecution(): void {
  const state = stateController.getAll();
  state.scripts[`script-${state.scriptExecutionName}`].args = {};
  state.scriptExecutionName = '';
  state.scriptExecution = 0;
  stateController.saveState(state);
}

function clearState(): void {
  const state = stateController.getAll();
  state.accounts = {};
  state.tokens = {};
  state.scripts = {};
  state.topics = {};
  state.scriptExecution = 0;
  state.scriptExecutionName = '';
  state.recording = 0;
  state.recordingScriptName = '';

  stateController.saveState(state);
}

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
    stateController.saveKey('topics', data.topics || {});
    logger.log('State overwritten successfully');
    process.exit(0);
  }

  if (data.accounts && Object.entries(data.accounts).length > 0) {
    addAccounts(data.accounts, merge);
  }

  if (data.tokens && Object.entries(data.tokens).length > 0) {
    addTokens(data.tokens, merge);
  }

  if (data.scripts && Object.entries(data.scripts).length > 0) {
    addScripts(data.scripts, merge);
  }

  if (data.topics && Object.entries(data.topics).length > 0) {
    addTopics(data.topics, merge);
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

function addTopics(importedTopics: Topic[], merge: boolean) {
  const topics: Record<string, Topic> = stateController.get('topics');
  Object.values(importedTopics).forEach((topic: Topic) => {
    const existingTopic = topics[topic.topicId];

    if (!merge && existingTopic) {
      logger.error(`Topic with ID ${topic.topicId} already exists`);
      process.exit(1);
    }

    if (merge && existingTopic) {
      logger.log(`Topic ${topic.topicId} already exists, overwriting it`);
    }

    topics[topic.topicId] = topic;
    stateController.saveKey('topics', topics);
    logger.log(`Topic ${topic.topicId} added successfully`);
  });
}

const stateUtils = {
  getMirrorNodeURL,
  getHederaClient,
  getOperator,
  recordCommand,
  switchNetwork,
  getNetwork,
  addTokenAssociation,
  getAccountById,
  getAccountByAlias,
  getAccountByIdOrAlias,
  startScriptExecution,
  stopScriptExecution,
  clearState,
  downloadState,
  importState,
};

export default stateUtils;
