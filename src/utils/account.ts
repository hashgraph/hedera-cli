import {
  PrivateKey,
  AccountCreateTransaction,
  Hbar,
  AccountId,
} from '@hashgraph/sdk';

import stateController from '../state/stateController';
import stateUtils from '../utils/state';
import { display } from '../utils/display';
import { Logger } from '../utils/logger';
import api from '../api';

import type { Account } from '../../types';

const logger = Logger.getInstance();

function clearAddressBook(): void {
  stateController.saveKey('accounts', {});
}

function deleteAccount(accountIdOrAlias: string): void {
  const account = stateUtils.getAccountByIdOrAlias(accountIdOrAlias);

  if (!account) {
    logger.error('Account not found');
    process.exit(1);
  }

  const accounts = stateController.get('accounts');
  delete accounts[account.alias];

  stateController.saveKey('accounts', accounts);
}

function generateRandomAlias(): string {
  const length = 20; // Define the length of the random string
  const characters =
    'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
  let result = '';
  for (let i = 0; i < length; i++) {
    result += characters.charAt(Math.floor(Math.random() * characters.length));
  }
  return result;
}

async function createAccount(
  balance: number,
  type: string,
  alias: string,
  setMaxAutomaticTokenAssociations: number = 0,
): Promise<Account> {
  // Validate balance
  if (isNaN(balance) || balance <= 0) {
    logger.error('Invalid balance. Balance must be a positive number.');
    process.exit(1);
  }

  // Validate type
  if (!['ecdsa', 'ed25519'].includes(type.toLowerCase())) {
    logger.error('Invalid type. Type must be either "ecdsa" or "ed25519".');
    process.exit(1);
  }

  // Validate alias: Not allowed to use "operator" as an alias or part of an alias
  if (alias.toLowerCase().includes('operator')) {
    logger.error('Invalid alias. Alias cannot contain the word "operator".');
    process.exit(1);
  }

  // Get client from config
  const accounts: Record<string, Account> = stateController.get('accounts');
  const client = stateUtils.getHederaClient();

  // Generate random alias if "random" is provided
  let isRandomAlias = false;
  if (alias.toLowerCase() === 'random') {
    isRandomAlias = true;
    let newAlias = generateRandomAlias();
    alias = newAlias; // Implement this function to generate a random string
  }

  // Check if name is unique
  if (!isRandomAlias && accounts && accounts[alias]) {
    logger.error('An account with this alias already exists.');
    client.close();
    process.exit(1);
  }

  // Handle different types of account creation
  let newAccountPrivateKey, newAccountPublicKey;
  if (type.toLowerCase() === 'ed25519') {
    newAccountPrivateKey = PrivateKey.generateED25519();
    newAccountPublicKey = newAccountPrivateKey.publicKey;
  } else {
    newAccountPrivateKey = PrivateKey.generateECDSA();
    newAccountPublicKey = newAccountPrivateKey.publicKey;
  }

  let newAccountId;
  try {
    const newAccount = await new AccountCreateTransaction()
      .setKey(newAccountPublicKey)
      .setInitialBalance(Hbar.fromTinybars(balance))
      .setMaxAutomaticTokenAssociations(setMaxAutomaticTokenAssociations)
      .execute(client);

    // Get the new account ID
    const getReceipt = await newAccount.getReceipt(client);
    newAccountId = getReceipt.accountId;
  } catch (error) {
    logger.error('Error creating new account:', error as object);
    client.close();
    process.exit(1);
  }

  if (newAccountId == null) {
    logger.error('Account was not created');
    client.close();
    process.exit(1);
  }

  // Store the new account in the config
  const newAccountDetails = {
    network: stateUtils.getNetwork(),
    alias,
    accountId: newAccountId.toString(),
    type: type.toUpperCase(),
    publicKey: newAccountPrivateKey.publicKey.toString(),
    evmAddress:
      type.toLowerCase() === 'ed25519'
        ? ''
        : newAccountPrivateKey.publicKey.toEvmAddress(),
    solidityAddress: `${newAccountId.toSolidityAddress()}`,
    solidityAddressFull: `0x${newAccountId.toSolidityAddress()}`,
    privateKey: newAccountPrivateKey.toString(),
  };

  // Add the new account to the accounts object in the config
  const updatedAccounts = { ...accounts, [alias]: newAccountDetails };
  stateController.saveKey('accounts', updatedAccounts);

  // Log the account ID
  logger.log(`The new account ID is: ${newAccountId}, with alias: ${alias}`);
  client.close();

  return newAccountDetails;
}

function listAccounts(showPrivateKeys: boolean = false): void {
  const accounts: Record<string, Account> = stateController.get('accounts');

  // Check if there are any accounts in the config
  if (!accounts || Object.keys(accounts).length === 0) {
    logger.log('No accounts found.');
    process.exit(0);
  }

  // Log details for each account
  for (const [alias, account] of Object.entries(accounts)) {
    if (showPrivateKeys) {
      logger.log('Alias, account ID, type, private key (DER)\n');
      logger.log(
        `${alias}, ${account.accountId}, ${account.type.toUpperCase()}, ${
          account.privateKey
        }`,
      );
    } else {
      logger.log('Alias, account ID, type\n');
      logger.log(
        `${alias}, ${account.accountId}, ${account.type.toUpperCase()}`,
      );
    }
  }
}

/**
 * @description Returns the type of a private key
 * @param privateKey Input private key
 * @returns {string} key type {ed25519, ecdsa, Unknown key type}
 */
function getKeyType(privateKey: string): string {
  try {
    PrivateKey.fromStringED25519(privateKey);
    return 'ed25519';
  } catch (e) {
    // Not an Ed25519 private key
  }

  try {
    PrivateKey.fromStringECDSA(privateKey);
    return 'ecdsa';
  } catch (e) {
    // Not an ECDSA private key
  }

  return 'Unknown key type';
}

function importAccount(id: string, key: string, alias: string): Account {
  const accounts = stateController.get('accounts');

  // Check if name is unique
  if (accounts && accounts[alias]) {
    logger.error('An account with this alias already exists.');
    process.exit(1);
  }

  let privateKey, type;
  const accountId = AccountId.fromString(id);
  switch (getKeyType(key)) {
    case 'ecdsa':
      type = 'ECDSA';
      privateKey = PrivateKey.fromStringECDSA(key);
      break;
    case 'ed25519':
      type = 'ED25519';
      privateKey = PrivateKey.fromStringED25519(key);
      break;
    default:
      logger.error(
        'Invalid key type. Only ECDSA and ED25519 keys are supported.',
      );
      process.exit(1);
  }

  // No Solidity and EVM address for ED25519 keys
  const updatedAccounts = { ...accounts };
  updatedAccounts[alias] = {
    network: stateUtils.getNetwork(),
    alias,
    accountId: id,
    type,
    publicKey: privateKey.publicKey.toString(),
    evmAddress:
      type.toLowerCase() === 'ed25519'
        ? ''
        : privateKey.publicKey.toEvmAddress(),
    solidityAddress: `${accountId.toSolidityAddress()}`,
    solidityAddressFull: `0x${accountId.toSolidityAddress()}`,
    privateKey: key,
  };

  stateController.saveKey('accounts', updatedAccounts);
  return updatedAccounts[alias];
}

function importAccountId(id: string, alias: string): Account {
  const accounts = stateController.get('accounts');

  // Check if name is unique
  if (accounts && accounts[alias]) {
    logger.error('An account with this alias already exists.');
    process.exit(1);
  }

  const accountId = AccountId.fromString(id);
  const updatedAccounts = { ...accounts };
  updatedAccounts[alias] = {
    network: stateUtils.getNetwork(),
    alias,
    accountId: id,
    type: '',
    publicKey: '',
    evmAddress: '',
    solidityAddress: `${accountId.toSolidityAddress()}`,
    solidityAddressFull: `0x${accountId.toSolidityAddress()}`,
    privateKey: '',
  };

  stateController.saveKey('accounts', updatedAccounts);
  return updatedAccounts[alias];
}

async function getAccountBalance(
  accountIdOrAlias: string,
  onlyHbar: boolean = false,
  tokenId?: string,
): Promise<void> {
  const accounts = stateController.get('accounts');
  const client = stateUtils.getHederaClient();

  let accountId;

  // Check if input is an alias or an account ID
  const accountIdPattern = /^0\.0\.\d+$/;
  if (accountIdPattern.test(accountIdOrAlias)) {
    accountId = accountIdOrAlias;
  } else if (accounts && accounts[accountIdOrAlias]) {
    accountId = accounts[accountIdOrAlias].accountId;
  } else {
    logger.error('Invalid account ID or alias not found in address book.');
    client.close();
    process.exit(1);
  }

  const response = await api.account.getAccountInfo(accountId);
  display('displayBalance', response, { onlyHbar, tokenId });

  client.close();
  return;
}

async function getAccountHbarBalanceByNetwork(
  accountId: string,
  network: string,
): Promise<number> {
  const response = await api.account.getAccountInfoByNetwork(
    accountId,
    network,
  );

  if (!response) {
    logger.error('Error getting account balance');
    process.exit(1);
  }

  return response.data.balance.balance;
}

async function getAccountHbarBalance(accountId: string): Promise<number> {
  const response = await api.account.getAccountInfo(accountId);

  if (!response) {
    logger.error('Error getting account balance');
    process.exit(1);
  }

  return response.data.balance.balance;
}

function findAccountByPrivateKey(privateKey: string): Account {
  const accounts: Record<string, Account> = stateController.get('accounts');
  if (!accounts) {
    logger.error('No accounts found in state');
    process.exit(1);
  }

  let matchingAccount: Account | null = null;
  for (const [, account] of Object.entries(accounts)) {
    if (account.privateKey === privateKey) {
      matchingAccount = account;
      break; // Exit the loop once a matching account is found
    }
  }

  if (!matchingAccount) {
    logger.error('No matching account found for private key');
    process.exit(1);
  }

  return matchingAccount;
}

function findAccountByAlias(inputAlias: string): Account {
  const accounts: Record<string, Account> = stateController.get('accounts');
  if (!accounts) {
    logger.error('No accounts found in state');
    process.exit(1);
  }

  let matchingAccount: Account | null = null;
  for (const [, account] of Object.entries(accounts)) {
    if (account.alias === inputAlias) {
      matchingAccount = account;
      break; // Exit the loop once a matching account is found
    }
  }

  if (!matchingAccount) {
    logger.error('No matching account found for alias');
    process.exit(1);
  }

  return matchingAccount;
}

function getPublicKeyFromPrivateKey(privateKey: string): string {
  const keyType = getKeyType(privateKey);

  if (keyType === 'ed25519') {
    return PrivateKey.fromStringED25519(privateKey).publicKey.toString();
  }

  if (keyType === 'ecdsa') {
    return PrivateKey.fromStringECDSA(privateKey).publicKey.toString();
  }

  logger.error('Invalid private key');
  process.exit(1);
}

function getPrivateKeyObject(privateKey: string): PrivateKey {
  const keyType = getKeyType(privateKey);

  if (keyType === 'ed25519') {
    return PrivateKey.fromStringED25519(privateKey);
  }

  if (keyType === 'ecdsa') {
    return PrivateKey.fromStringECDSA(privateKey);
  }

  logger.error('Invalid private key');
  process.exit(1);
}

const accountUtils = {
  createAccount,
  listAccounts,
  importAccount,
  importAccountId,
  getAccountBalance,
  getAccountHbarBalance,
  getAccountHbarBalanceByNetwork,
  getKeyType,
  getPublicKeyFromPrivateKey,
  getPrivateKeyObject,
  generateRandomAlias,
  clearAddressBook,
  deleteAccount,

  findAccountByPrivateKey,
  findAccountByAlias,
};

export default accountUtils;
