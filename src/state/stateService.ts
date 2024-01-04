import { Client, AccountId, PrivateKey } from '@hashgraph/sdk';

import stateController from './stateController';
import { Logger } from '../utils/logger';

import type { Account, Token } from '../../types';

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
  state.scriptExecution = 0;
  state.scriptExecutionName = '';
  state.recording = 0;
  state.recordingScriptName = '';

  stateController.saveState(state);
}

export {
  getMirrorNodeURL,
  getHederaClient,
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
};
