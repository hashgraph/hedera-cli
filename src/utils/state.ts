import { AccountId, Client, PrivateKey } from '@hashgraph/sdk';
import axios from 'axios';
import { v4 as uuidv4 } from 'uuid';

import {
  selectAccounts,
  selectScripts,
  selectTokens,
  selectTopics,
} from '../state/selectors';
import {
  getState,
  get as storeGet,
  saveKey as storeSaveKey,
  saveState as storeSaveState,
  updateState as storeUpdateState,
} from '../state/store';
import { DomainError } from './errors';
import { Logger } from './logger';

import type {
  Account,
  DownloadState,
  NetworkConfig,
  Script,
  State,
  Token,
  Topic,
} from '../../types';

const logger = Logger.getInstance();

/**
 * Generates a UUID when it doesn't exist
 */
function createUUID(): void {
  const uuid = storeGet('uuid');
  if (!uuid) {
    storeSaveKey('uuid', uuidv4());
  }
}

/**
 * Returns the current telemetry setting
 * @returns {boolean} telemetry
 */
function isTelemetryEnabled(): boolean {
  return storeGet('telemetry') === 1;
}

function getNetworkFromState(network: string): NetworkConfig {
  const { networks } = getState();
  const cfg = networks[network];
  if (!cfg) {
    logger.error(`Network ${network} not found in state`);
    throw new DomainError(`Network ${network} not found in state`);
  }
  return cfg;
}

function getOperator(network?: string): {
  operatorId: string;
  operatorKey: string;
} {
  const state = getState() as unknown as State & Record<string, unknown>; // allow legacy dynamic keys
  const activeNetwork = network || state.network;
  const netConfig = getNetworkFromState(activeNetwork);
  let { operatorId, operatorKey } = netConfig;

  if (!operatorId || !operatorKey) {
    const legacyId = state[`${activeNetwork}OperatorId`];
    const legacyKey = state[`${activeNetwork}OperatorKey`];
    if (
      typeof legacyId === 'string' &&
      legacyId &&
      typeof legacyKey === 'string' &&
      legacyKey
    ) {
      operatorId = legacyId;
      operatorKey = legacyKey;
      // persist back
      storeSaveKey('networks', {
        ...state.networks,
        [activeNetwork]: { ...netConfig, operatorId, operatorKey },
      });
    }
  }

  if (!operatorId || !operatorKey) {
    logger.error(`operator key and ID not set for ${activeNetwork}`);
    throw new DomainError(`operator key and ID not set for ${activeNetwork}`);
  }
  return { operatorId, operatorKey };
}

// Get all the available networks from the state
function getAvailableNetworks(): string[] {
  return Object.keys(getState().networks);
}

const getMirrorNodeURL = (): string =>
  getNetworkFromState(getState().network).mirrorNodeUrl;

const getMirrorNodeURLByNetwork = (network: string): string =>
  getNetworkFromState(network).mirrorNodeUrl;

function getHederaClient(): Client {
  const state = getState();
  const { operatorId, operatorKey } = getOperator(state.network);
  let client: Client;

  // Handle predefined networks
  switch (state.network) {
    case 'mainnet':
      client = Client.forMainnet();
      break;
    case 'testnet':
      client = Client.forTestnet();
      break;
    case 'previewnet':
      client = Client.forPreviewnet();
      break;
    case 'localnet': {
      const node = {
        [state.localNodeAddress]: AccountId.fromString(
          state.localNodeAccountId,
        ),
      };
      client = Client.forNetwork(node).setMirrorNetwork(
        state.localNodeMirrorAddressGRPC,
      );
      break;
    }
    default: {
      // Handle custom networks from config
      const networkConfig = getNetworkFromState(state.network);
      if (!networkConfig.rpcUrl) {
        logger.error(`RPC URL not configured for network: ${state.network}`);
        throw new DomainError(
          `RPC URL not configured for network: ${state.network}`,
        );
      }

      // For custom networks, we need to create a client with custom endpoints
      // Use the same approach as localnet but with custom URLs
      const node = {
        [networkConfig.rpcUrl]: AccountId.fromString('0.0.3'), // Default node account
      };
      client = Client.forNetwork(node);

      // Set mirror network if configured
      if (networkConfig.mirrorNodeUrl) {
        client.setMirrorNetwork(networkConfig.mirrorNodeUrl);
      }
      break;
    }
  }

  return client.setOperator(
    AccountId.fromString(operatorId),
    PrivateKey.fromStringDer(operatorKey),
  );
}

/**
 * @returns {string} network name
 */
function getNetwork(): string {
  return getState().network;
}

function switchNetwork(name: string) {
  const networks = getAvailableNetworks();
  if (!networks.includes(name)) {
    logger.error(
      'Invalid network name. Available networks: ' + networks.join(', '),
    );
    throw new DomainError(
      'Invalid network name. Available networks: ' + networks.join(', '),
    );
  }
  // Validate operator exists for selected network
  getOperator(name);
  storeSaveKey('network', name);
}

function addTokenAssociation(tokenId: string, accountId: string, name: string) {
  storeUpdateState((draft) => {
    const token = draft.tokens[tokenId];
    if (!token) {
      logger.log(
        `Token ${tokenId} not found in state. Skipping storing the token associations.`,
      );
      return;
    }
    token.associations.push({ name, accountId });
  });
}

/* Accounts */
function getAccountById(accountId: string): Account | undefined {
  const accounts: Record<string, Account> = selectAccounts();
  return Object.values(accounts).find(
    (el: Account) => el.accountId === accountId,
  );
}

function getAccountByName(name: string): Account | undefined {
  const accounts: Record<string, Account> = selectAccounts();
  return accounts[name];
}

function getAccountByIdOrName(accountIdOrName: string): Account {
  const accountIdPattern = /^0\.0\.\d+$/;
  const match = accountIdOrName.match(accountIdPattern);
  let account;
  if (match) {
    account = getAccountById(accountIdOrName);
  } else {
    account = getAccountByName(accountIdOrName);
  }

  if (!account) {
    logger.error(`Account not found: ${accountIdOrName}`);
    throw new DomainError(`Account not found: ${accountIdOrName}`);
  }

  return account;
}

function startScriptExecution(name: string): void {
  storeUpdateState((draft) => {
    draft.scriptExecution.active = true;
    draft.scriptExecution.name = name;
  });
}

function stopScriptExecution(): void {
  const { scriptExecution } = getState();
  const active = scriptExecution.name;
  storeUpdateState((draft) => {
    if (active) {
      const key = `script-${active}`;
      const existing = draft.scripts[key];
      if (existing) existing.args = {};
    }
    draft.scriptExecution = { active: false, name: '' };
  });
}

function clearState(): void {
  const current = getState();
  storeSaveState({
    ...current,
    accounts: {},
    tokens: {},
    scripts: {},
    topics: {},
    scriptExecution: { active: false, name: '' },
  });
}

async function downloadState(url: string): Promise<DownloadState> {
  try {
    const response = await axios.get<DownloadState>(url);
    return response.data;
  } catch (error) {
    if (axios.isAxiosError(error)) logger.error(error.message);
    else logger.error('Unexpected error downloading file', error as object);
    throw new DomainError('Error downloading state');
  }
}

function addAccounts(importedAccounts: Account[], merge: boolean) {
  const additions = Object.values(importedAccounts);
  const currentAccounts: Record<string, Account> = selectAccounts();

  // Validate & log merge notices first (no mutations yet)
  additions.forEach((account) => {
    const existingAccount = currentAccounts[account.name];
    if (!merge && existingAccount) {
      logger.error(`Account with name ${account.name} already exists`);
      throw new DomainError(`Account with name ${account.name} already exists`);
    }
    if (merge && existingAccount) {
      logger.log(
        `Account "${account.name}" already exists, merging it with the new account details`,
      );
    }
  });

  // Single transactional mutation
  storeUpdateState((s) => {
    additions.forEach((account) => {
      s.accounts[account.name] = account;
    });
  });

  // Success logs (post mutation)
  additions.forEach((account) => {
    logger.log(
      `Account "${account.name}" with ID ${account.accountId} added successfully`,
    );
  });
}

function addTokens(importedTokens: Token[], merge: boolean) {
  const additions = Object.values(importedTokens);
  const currentTokens: Record<string, Token> = selectTokens();

  additions.forEach((token) => {
    const existingToken = currentTokens[token.tokenId];
    if (!merge && existingToken) {
      logger.error(`Token with ID ${token.tokenId} already exists`);
      throw new DomainError(`Token with ID ${token.tokenId} already exists`);
    }
    if (merge && existingToken) {
      logger.log(`Token ${token.tokenId} already exists, overwriting it`);
    }
  });

  storeUpdateState((s) => {
    additions.forEach((token) => {
      s.tokens[token.tokenId] = token;
    });
  });

  additions.forEach((token) => {
    logger.log(
      `Token ${token.tokenId} with name "${token.name}" added successfully`,
    );
  });
}

function addTopics(importedTopics: Topic[], merge: boolean) {
  const additions = Object.values(importedTopics);
  const currentTopics: Record<string, Topic> = selectTopics();

  additions.forEach((topic) => {
    const existingTopic = currentTopics[topic.topicId];
    if (!merge && existingTopic) {
      logger.error(`Topic with ID ${topic.topicId} already exists`);
      throw new DomainError(`Topic with ID ${topic.topicId} already exists`);
    }
    if (merge && existingTopic) {
      logger.log(`Topic ${topic.topicId} already exists, overwriting it`);
    }
  });

  storeUpdateState((s) => {
    additions.forEach((topic) => {
      s.topics[topic.topicId] = topic;
    });
  });

  additions.forEach((topic) => {
    logger.log(`Topic ${topic.topicId} added successfully`);
  });
}

function addScripts(importedScripts: Script[], merge: boolean) {
  const additions = Object.values(importedScripts);
  const currentScripts: Record<string, Script> = selectScripts();

  additions.forEach((script) => {
    const scriptName = `script-${script.name}`;
    const existingScript = currentScripts[scriptName];
    if (!merge && existingScript) {
      logger.error(`Script with name ${scriptName} already exists`);
      throw new DomainError(`Script with name ${scriptName} already exists`);
    }
    if (merge && existingScript) {
      logger.log(`Script "${script.name}" already exists, overwriting it`);
    }
  });

  storeUpdateState((s) => {
    additions.forEach((script) => {
      const scriptName = `script-${script.name}`;
      s.scripts[scriptName] = {
        name: script.name,
        creation: Date.now(),
        commands: script.commands,
        args: {},
      };
    });
  });

  additions.forEach((script) => {
    logger.log(`Script "${script.name}" added successfully`);
  });
}

type ImportableState = Partial<DownloadState> & {
  scripts?: Record<string, Script>;
};
function importState(
  data: ImportableState,
  overwrite: boolean,
  merge: boolean,
) {
  if (overwrite) {
    storeUpdateState((draft) => {
      draft.accounts = data.accounts || {};
      draft.tokens = data.tokens || {};
      draft.scripts = data.scripts || {};
      draft.topics = data.topics || {};
    });
    logger.log('State overwritten successfully');
    return;
  }
  if (data.accounts && Object.keys(data.accounts).length)
    addAccounts(Object.values(data.accounts), merge);
  if (data.tokens && Object.keys(data.tokens).length)
    addTokens(Object.values(data.tokens), merge);
  if (data.scripts && Object.keys(data.scripts).length)
    addScripts(Object.values(data.scripts), merge);
  if (data.topics && Object.keys(data.topics).length)
    addTopics(Object.values(data.topics), merge);
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
  getAccountByName,
  getAccountByIdOrName,
  startScriptExecution,
  stopScriptExecution,
  clearState,
  downloadState,
  importState,
  getAvailableNetworks,
};

export default stateUtils;
