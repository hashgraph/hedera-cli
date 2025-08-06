import {
  AccountCreateTransaction,
  AccountId,
  Hbar,
  PrivateKey,
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

function deleteAccount(accountIdOrName: string): void {
  const account = stateUtils.getAccountByIdOrName(accountIdOrName);

  if (!account) {
    logger.error('Account not found');
    process.exit(1);
  }

  const accounts = stateController.get('accounts');
  delete accounts[account.name];

  stateController.saveKey('accounts', accounts);
}

function generateRandomName(): string {
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
  name: string,
  setMaxAutomaticTokenAssociations: number = 0,
): Promise<Account> {
  // Validate balance
  if (isNaN(balance) || balance <= 0) {
    logger.error('Invalid balance. Balance must be a positive number.');
    process.exit(1);
  }

  // Validate type
  if (type.toLowerCase() !== 'ecdsa') {
    logger.error('Invalid type. Only "ecdsa" is supported.');
    process.exit(1);
  }

  // Validate name: Not allowed to use "operator" as a name or part of a name
  if (name.toLowerCase().includes('operator')) {
    logger.error('Invalid name. Name cannot contain the word "operator".');
    process.exit(1);
  }

  // Get client from config
  const accounts: Record<string, Account> = stateController.get('accounts');
  const client = stateUtils.getHederaClient();

  // Generate random name if "random" is provided
  let isRandomName = false;
  if (name.toLowerCase() === 'random') {
    isRandomName = true;
    name = generateRandomName();
  }

  // Check if name is unique
  if (!isRandomName && accounts && accounts[name]) {
    logger.error('An account with this name already exists.');
    client.close();
    process.exit(1);
  }

  // Only ECDSA supported
  let newAccountPrivateKey = PrivateKey.generateECDSA();
  let newAccountPublicKey = newAccountPrivateKey.publicKey;

  let newAccountId;
  try {
    const newAccount = await new AccountCreateTransaction()
      .setECDSAKeyWithAlias(newAccountPublicKey) // this makes it EVM compatible
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

  const newAccountDetails: Account = {
    network: stateUtils.getNetwork(),
    name,
    accountId: newAccountId.toString(),
    type: 'ECDSA',
    publicKey: newAccountPrivateKey.publicKey.toString(),
    evmAddress: newAccountPrivateKey.publicKey.toEvmAddress(),
    solidityAddress: `${newAccountId.toSolidityAddress()}`,
    solidityAddressFull: `0x${newAccountId.toSolidityAddress()}`,
    privateKey: newAccountPrivateKey.toString(),
  };
  // Store the new account in the config
  const updatedAccounts = { ...accounts, [name]: newAccountDetails };
  stateController.saveKey('accounts', updatedAccounts);

  // Log the account ID
  logger.log(`The new account ID is: ${newAccountId}, with name: ${name}`);
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
  for (const [name, account] of Object.entries(accounts)) {
    if (showPrivateKeys) {
      logger.log('Name, account ID, type, private key (DER)\n');
      logger.log(
        `${name}, ${account.accountId}, ${account.type.toUpperCase()}, ${
          account.privateKey
        }`,
      );
    } else {
      logger.log('Name, account ID, type\n');
      logger.log(
        `${name}, ${account.accountId}, ${account.type.toUpperCase()}`,
      );
    }
  }
}

/**
 * @description Checks if the private key is a valid ECDSA private key
 * @param privateKey Input private key
 * @returns {boolean} true if the private key is a valid ECDSA private key, false otherwise
 */
function isValidECDSAPrivateKey(privateKey: string): boolean {
  try {
    PrivateKey.fromStringECDSA(privateKey);
    return true;
  } catch (e) {
    return false;
  }
}

/**
 * @description Imports an account by ID, key, and name
 * @param id Account ID
 * @param key Private key
 * @param name Account name
 * @returns Account
 */
function importAccount(id: string, key: string, name: string): Account {
  const accounts = stateController.get('accounts');

  // Check if name is unique
  if (accounts && accounts[name]) {
    logger.error('An account with this name already exists.');
    process.exit(1);
  }

  let privateKey, type;
  const accountId = AccountId.fromString(id);

  if (isValidECDSAPrivateKey(key)) {
    type = 'ECDSA';
    privateKey = PrivateKey.fromStringECDSA(key);
  } else {
    logger.error('Invalid key type. Only ECDSA keys are supported.');
    process.exit(1);
  }

  const updatedAccounts = { ...accounts };
  updatedAccounts[name] = {
    network: stateUtils.getNetwork(),
    name,
    accountId: id,
    type,
    publicKey: privateKey.publicKey.toString(),
    evmAddress: privateKey.publicKey.toEvmAddress(),
    solidityAddress: `${accountId.toSolidityAddress()}`,
    solidityAddressFull: `0x${accountId.toSolidityAddress()}`,
    privateKey: key,
  } as Account;

  stateController.saveKey('accounts', updatedAccounts);
  return updatedAccounts[name];
}

function importAccountId(id: string, name: string): Account {
  const accounts = stateController.get('accounts');

  // Check if name is unique
  if (accounts && accounts[name]) {
    logger.error('An account with this name already exists.');
    process.exit(1);
  }

  const accountId = AccountId.fromString(id);
  const updatedAccounts = { ...accounts };
  updatedAccounts[name] = {
    network: stateUtils.getNetwork(),
    name,
    accountId: id,
    type: '',
    publicKey: '',
    evmAddress: '',
    solidityAddress: `${accountId.toSolidityAddress()}`,
    solidityAddressFull: `0x${accountId.toSolidityAddress()}`,
    privateKey: '',
  } as Account;

  stateController.saveKey('accounts', updatedAccounts);
  return updatedAccounts[name];
}

async function getAccountBalance(
  accountIdOrName: string,
  onlyHbar: boolean = false,
  tokenId?: string,
): Promise<void> {
  const accounts = stateController.get('accounts');
  const client = stateUtils.getHederaClient();

  let accountId;

  // Check if input is an name or an account ID
  const accountIdPattern = /^0\.0\.\d+$/;
  if (accountIdPattern.test(accountIdOrName)) {
    accountId = accountIdOrName;
  } else if (accounts && accounts[accountIdOrName]) {
    accountId = accounts[accountIdOrName].accountId;
  } else {
    logger.error('Invalid account ID or name not found in address book.');
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

function findAccountByName(inputName: string): Account {
  const accounts: Record<string, Account> = stateController.get('accounts');
  if (!accounts) {
    logger.error('No accounts found in state');
    process.exit(1);
  }

  let matchingAccount: Account | null = null;
  for (const [, account] of Object.entries(accounts)) {
    if (account.name === inputName) {
      matchingAccount = account;
      break; // Exit the loop once a matching account is found
    }
  }

  if (!matchingAccount) {
    logger.error('No matching account found for name');
    process.exit(1);
  }

  return matchingAccount;
}

function getPublicKeyFromPrivateKey(privateKey: string): string {
  if (isValidECDSAPrivateKey(privateKey)) {
    return PrivateKey.fromStringECDSA(privateKey).publicKey.toString();
  }
  logger.error('Invalid private key');
  process.exit(1);
}

function getPrivateKeyObject(privateKey: string): PrivateKey {
  if (isValidECDSAPrivateKey(privateKey)) {
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
  getPublicKeyFromPrivateKey,
  getPrivateKeyObject,
  generateRandomName,
  clearAddressBook,
  deleteAccount,
  findAccountByPrivateKey,
  findAccountByName,
};

export default accountUtils;
