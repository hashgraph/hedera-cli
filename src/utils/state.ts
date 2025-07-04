import { Client, AccountId, PrivateKey } from '@hashgraph/sdk';
import axios from 'axios';
import { v4 as uuidv4 } from 'uuid';

import { Logger } from '../utils/logger';
import stateController from '../state/stateController';

import type { Account, DownloadState, Script, Token, Topic } from '../../types';

const logger = Logger.getInstance();

/**
 * Generates a UUID when it doesn't exist
 */
function createUUID(): void {
  const uuid = stateController.get('uuid');
  if (uuid === '' || !uuid) {
    const newUUID = uuidv4();
    stateController.saveKey('uuid', newUUID);
  }
}

/**
 * Returns the current telemetry setting
 * @returns {boolean} telemetry
 */
function isTelemetryEnabled(): boolean {
  const telemetry = stateController.get('telemetry');
  return telemetry === 1;
}

function getMirrorNodeURL(): string {
  const network = stateController.get('network');
  let mirrorNodeURL = stateController.get('mirrorNodeTestnet');
  switch (network) {
    case 'testnet':
      mirrorNodeURL = stateController.get('mirrorNodeTestnet');
      break;
    case 'mainnet':
      mirrorNodeURL = stateController.get('mirrorNodeMainnet');
      break;
    case 'previewnet':
      mirrorNodeURL = stateController.get('mirrorNodePreviewnet');
      break;
    case 'localnet':
      mirrorNodeURL = stateController.get('mirrorNodeLocalnet');
      break;
    default:
      logger.error('Invalid network name');
      process.exit(1);
  }
  return mirrorNodeURL;
}

function getAvailableNetworks(): string[] {
  const mainnet = stateController.get('mainnetOperatorKey');
  const testnet = stateController.get('testnetOperatorKey');
  const previewnet = stateController.get('previewnetOperatorKey');
  const localnet = stateController.get('localnetOperatorKey');

  const networks = [];
  if (mainnet) {
    networks.push('mainnet');
  }
  if (testnet) {
    networks.push('testnet');
  }
  if (previewnet) {
    networks.push('previewnet');
  }
  if (localnet) {
    networks.push('localnet');
  }

  return networks;
}

function getMirrorNodeURLByNetwork(network: string): string {
  let mirrorNodeURL = stateController.get('mirrorNodeTestnet');
  switch (network) {
    case 'testnet':
      mirrorNodeURL = stateController.get('mirrorNodeTestnet');
      break;
    case 'mainnet':
      mirrorNodeURL = stateController.get('mirrorNodeMainnet');
      break;
    case 'previewnet':
      mirrorNodeURL = stateController.get('mirrorNodePreviewnet');
      break;
    case 'localnet':
      mirrorNodeURL = stateController.get('mirrorNodeLocalnet');
      break;
    default:
      logger.error('Invalid network name');
      process.exit(1);
  }
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
    case 'localnet':
      const node = {
        [state.localNodeAddress]: AccountId.fromString(
          state.localNodeAccountId,
        ),
      };
      client = Client.forNetwork(node).setMirrorNetwork(
        state.localNodeMirrorAddressGRPC,
      );
      operatorId = state.localnetOperatorId;
      operatorKey = state.localnetOperatorKey;
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
    case 'localnet':
      operatorId = state.localnetOperatorId;
      operatorKey = state.localnetOperatorKey;
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
  const networks = getAvailableNetworks();
  if (!networks.includes(name)) {
    logger.error(
      'Invalid network name. Available networks: ' + networks.join(', '),
    );
    process.exit(1);
  }

  const state = stateController.getAll();
  let operatorId, operatorKey;
  switch (name) {
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
    case 'localnet':
      operatorId = state.localnetOperatorId;
      operatorKey = state.localnetOperatorKey;
      break;
  }

  if (operatorId === '' || operatorKey === '') {
    logger.error(`operator key and ID not set for ${name}`);
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

  if (!tokens[tokenId]) {
    logger.log(
      `Token ${tokenId} not found in state. Skipping storing the token associations.`,
    );
    return;
  }
  const token: Token = tokens[tokenId];
  token.associations.push({ alias, accountId });
  tokens[tokenId] = token;
  stateController.saveKey('tokens', tokens);
}

/* Accounts */
function getAccountById(accountId: string): Account | undefined {
  const accounts: Record<string, Account> = stateController.get('accounts');
  const account = Object.values(accounts).find(
    (el: Account) => el.accountId === accountId,
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

  stateController.saveState(state);
}

async function downloadState(url: string): Promise<DownloadState> {
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

const stateUtils = {
  createUUID,
  isTelemetryEnabled,
  getMirrorNodeURL,
  getMirrorNodeURLByNetwork,
  getHederaClient,
  getOperator,
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
  getAvailableNetworks,
};

export default stateUtils;
