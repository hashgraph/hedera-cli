import {
  AccountCreateTransaction,
  AccountId,
  Hbar,
  PrivateKey,
} from '@hashgraph/sdk';

import { updateState as storeUpdateState } from '../state/store';
import { actions } from '../state/store';
import { selectAccounts } from '../state/selectors';
import { addAccount } from '../state/mutations';
import stateUtils from '../utils/state';
import { display } from '../utils/display';
import { Logger } from '../utils/logger';
import { DomainError } from './errors';
import api from '../api';

import type { Account } from '../../types';

const logger = Logger.getInstance();

function clearAddressBook(): void {
  storeUpdateState((s: any) => {
    s.accounts = {};
  });
}

function deleteAccount(accountIdOrName: string): void {
  const account = stateUtils.getAccountByIdOrName(accountIdOrName);

  if (!account) {
    logger.error('Account not found');
    throw new DomainError('Account not found');
  }

  storeUpdateState((s: any) => {
    delete s.accounts[account.name];
  });
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
    throw new DomainError(
      'Invalid balance. Balance must be a positive number.',
    );
  }

  // Validate type
  if (type.toLowerCase() !== 'ecdsa') {
    throw new DomainError('Invalid type. Only "ecdsa" is supported.');
  }

  // Validate name: Not allowed to use "operator" as a name or part of a name
  if (name.toLowerCase().includes('operator')) {
    throw new DomainError(
      'Invalid name. Name cannot contain the word "operator".',
    );
  }

  // Get client from config
  const accounts: Record<string, Account> = selectAccounts();
  const client = stateUtils.getHederaClient();

  // Generate random name if "random" is provided
  let isRandomName = false;
  if (name.toLowerCase() === 'random') {
    isRandomName = true;
    name = generateRandomName();
  }

  // Check if name is unique
  if (!isRandomName && accounts && accounts[name]) {
    client.close();
    throw new DomainError('An account with this name already exists.');
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
    client.close();
    throw new DomainError('Error creating new account');
  }

  if (newAccountId == null) {
    client.close();
    throw new DomainError('Account was not created');
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
  // Transactional storage via helper (overwrite false to keep safety)
  addAccount(newAccountDetails, false);

  // Log the account ID
  logger.log(`The new account ID is: ${newAccountId}, with name: ${name}`);
  client.close();

  return newAccountDetails;
}

function listAccounts(showPrivateKeys: boolean = false): void {
  const accounts: Record<string, Account> = selectAccounts();

  // Check if there are any accounts in the config
  if (!accounts || Object.keys(accounts).length === 0) {
    logger.log('No accounts found.');
    throw new DomainError('No accounts found.', 0);
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
  const accounts = selectAccounts();

  // Check if name is unique
  if (accounts && accounts[name]) {
    logger.error('An account with this name already exists.');
    throw new DomainError('An account with this name already exists.');
  }

  let privateKey, type;
  const accountId = AccountId.fromString(id);

  if (isValidECDSAPrivateKey(key)) {
    type = 'ECDSA';
    privateKey = PrivateKey.fromStringECDSA(key);
  } else {
    logger.error('Invalid key type. Only ECDSA keys are supported.');
    throw new DomainError('Invalid key type. Only ECDSA keys are supported.');
  }

  let created: Account | undefined;
  // Primary write via new store actions (validation already done above)
  const account: Account = {
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
  actions().addAccount(account, true);
  // Dual write legacy
  storeUpdateState((s: any) => {
    s.accounts[name] = account;
  });
  created = account;
  return created!;
}

function importAccountId(id: string, name: string): Account {
  const accounts = selectAccounts();

  // Check if name is unique
  if (accounts && accounts[name]) {
    logger.error('An account with this name already exists.');
    throw new DomainError('An account with this name already exists.');
  }

  const accountId = AccountId.fromString(id);
  let created: Account | undefined;
  const account: Account = {
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
  actions().addAccount(account, true);
  storeUpdateState((s: any) => {
    s.accounts[name] = account;
  });
  created = account;
  return created!;
}

async function getAccountBalance(
  accountIdOrName: string,
  onlyHbar: boolean = false,
  tokenId?: string,
): Promise<void> {
  const accounts = selectAccounts();
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
    throw new DomainError(
      'Invalid account ID or name not found in address book.',
    );
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
    throw new DomainError('Error getting account balance');
  }

  return response.data.balance.balance;
}

async function getAccountHbarBalance(accountId: string): Promise<number> {
  const response = await api.account.getAccountInfo(accountId);

  if (!response) {
    logger.error('Error getting account balance');
    throw new DomainError('Error getting account balance');
  }

  return response.data.balance.balance;
}

function findAccountByPrivateKey(privateKey: string): Account {
  const accounts: Record<string, Account> = selectAccounts();
  if (!accounts) {
    logger.error('No accounts found in state');
    throw new DomainError('No accounts found in state');
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
    throw new DomainError('No matching account found for private key');
  }

  return matchingAccount;
}

function findAccountByName(inputName: string): Account {
  const accounts: Record<string, Account> = selectAccounts();
  if (!accounts) {
    logger.error('No accounts found in state');
    throw new DomainError('No accounts found in state');
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
    throw new DomainError('No matching account found for name');
  }

  return matchingAccount;
}

function getPublicKeyFromPrivateKey(privateKey: string): string {
  if (isValidECDSAPrivateKey(privateKey)) {
    return PrivateKey.fromStringECDSA(privateKey).publicKey.toString();
  }
  logger.error('Invalid private key');
  throw new DomainError('Invalid private key');
}

function getPrivateKeyObject(privateKey: string): PrivateKey {
  if (isValidECDSAPrivateKey(privateKey)) {
    return PrivateKey.fromStringECDSA(privateKey);
  }
  logger.error('Invalid private key');
  throw new DomainError('Invalid private key');
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
