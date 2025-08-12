import { AccountId, Client, PrivateKey } from '@hashgraph/sdk';
import axios from 'axios';
import { v4 as uuidv4 } from 'uuid';

import { Logger } from './logger';
import { DomainError } from './errors';
import {
  get as storeGet,
  saveKey as storeSaveKey,
  updateState as storeUpdateState,
  saveState as storeSaveState,
  getState,
} from '../state/store';
import {
  selectAccounts,
  selectTokens,
  selectTopics,
  selectScripts,
} from '../state/selectors';

import type {
  Account,
  DownloadState,
  NetworkConfig,
  Script,
  Token,
  Topic,
} from '../../types';

const logger = Logger.getInstance();

/**
 * Generates a UUID when it doesn't exist
 */
function createUUID(): void {
  const uuid = storeGet('uuid' as any) as any;
  if (uuid === '' || !uuid) {
    const newUUID = uuidv4();
    storeSaveKey('uuid' as any, newUUID as any);
  }
}

/**
 * Returns the current telemetry setting
 * @returns {boolean} telemetry
 */
function isTelemetryEnabled(): boolean {
  const telemetry = storeGet('telemetry' as any) as any;
  return telemetry === 1;
}

function getNetworkFromState(network: string): NetworkConfig {
  const state = getState() as any;
  if (!state.networks[network]) {
    logger.error(`Network ${network} not found in state`);
    throw new DomainError(`Network ${network} not found in state`);
  }
  return state.networks[network];
}

function getOperator(network?: string): {
  operatorId: string;
  operatorKey: string;
} {
  const state = getState() as any;

  // set the network to the current state network if not provided
  if (!network) {
    network = state.network;
  }
  const netConfig = getNetworkFromState(network as string);
  // FIXME - I dont think we need this
  let operatorId = netConfig.operatorId;
  let operatorKey = netConfig.operatorKey;

  // Fallback support for legacy root-level operator fields used in tests (e.g. mainnetOperatorId)
  if (operatorId === '' || operatorKey === '') {
    const rootIdKey = `${network}OperatorId` as keyof typeof state;
    const rootKeyKey = `${network}OperatorKey` as keyof typeof state;
    // @ts-ignore dynamic legacy fields possibly present in test state
    const legacyId = state[rootIdKey];
    // @ts-ignore
    const legacyKey = state[rootKeyKey];
    if (
      typeof legacyId === 'string' &&
      legacyId !== '' &&
      typeof legacyKey === 'string' &&
      legacyKey !== ''
    ) {
      operatorId = legacyId;
      operatorKey = legacyKey;
      // Persist back into networks config so subsequent reads succeed
      const networks = {
        ...state.networks,
        [network as string]: {
          ...(state.networks as any)[network as string],
          operatorId,
          operatorKey,
        },
      } as any;
      storeSaveKey('networks' as any, networks as any);
    }
  }

  if (operatorId === '' || operatorKey === '') {
    logger.error(`operator key and ID not set for ${network}`);
    throw new DomainError(`operator key and ID not set for ${network}`);
  }

  return {
    operatorId,
    operatorKey,
  };
}

// Get all the available networks from the state
function getAvailableNetworks(): string[] {
  return Array.from(
    new Set<string>(Object.keys((getState() as any).networks).values()),
  );
}

const getMirrorNodeURL = (): string =>
  getNetworkFromState((getState() as any).network).mirrorNodeUrl;

const getMirrorNodeURLByNetwork = (network: string): string =>
  getNetworkFromState(network).mirrorNodeUrl;

function getHederaClient(): Client {
  const state = getState() as any;
  let client: Client;
  const { operatorId, operatorKey } = getOperator(state.network);

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
    case 'localnet':
      const node = {
        [state.localNodeAddress]: AccountId.fromString(
          state.localNodeAccountId,
        ),
      };
      client = Client.forNetwork(node).setMirrorNetwork(
        state.localNodeMirrorAddressGRPC,
      );
      break;
    default:
      // TODO: add in the ability to add custom networks here by name for sphere instances esp.
      logger.error('Invalid network name - FIXME');
      throw new DomainError('Invalid network name - FIXME');
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
  const state = getState() as any;
  return state.network;
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
  // check the operator exists.
  getOperator(name);

  storeSaveKey('network' as any, name as any);
}

function addTokenAssociation(tokenId: string, accountId: string, name: string) {
  storeUpdateState((draft: any) => {
    const token = draft.tokens[tokenId];
    if (!token) {
      logger.log(
        `Token ${tokenId} not found in state. Skipping storing the token associations.`,
      );
      return;
    }
    token.associations = [...token.associations, { name, accountId }];
    draft.tokens = { ...draft.tokens, [tokenId]: { ...token } } as Record<
      string,
      Token
    >;
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
  storeUpdateState((draft: any) => {
    draft.scriptExecution.active = true;
    draft.scriptExecution.name = name;
  });
}

function stopScriptExecution(): void {
  const s: any = getState();
  const active = s.scriptExecution?.name;
  storeUpdateState((draft: any) => {
    if (active) {
      const key = `script-${active}`;
      if (draft.scripts[key])
        draft.scripts[key] = { ...draft.scripts[key], args: {} };
    }
    draft.scriptExecution = { active: false, name: '' };
  });
}

function clearState(): void {
  const current = getState() as any;
  const cleared = {
    ...current,
    accounts: {},
    tokens: {},
    scripts: {},
    topics: {},
    scriptExecution: { active: false, name: '' },
  };
  storeSaveState(cleared as any);
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
    throw new DomainError('Error downloading state');
  }

  return data;
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
  storeUpdateState((s: any) => {
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

  storeUpdateState((s: any) => {
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

  storeUpdateState((s: any) => {
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

  storeUpdateState((s: any) => {
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

function importState(data: any, overwrite: boolean, merge: boolean) {
  if (overwrite) {
    storeUpdateState((draft: any) => {
      draft.accounts = data.accounts || {};
      draft.tokens = data.tokens || {};
      draft.scripts = data.scripts || {};
      draft.topics = data.topics || {};
    });
    logger.log('State overwritten successfully');
    return; // normal successful completion; no exception
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
