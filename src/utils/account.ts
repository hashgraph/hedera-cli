import {
  PrivateKey,
  AccountCreateTransaction,
  Hbar,
  AccountId,
} from "@hashgraph/sdk";

import { getState, saveStateAttribute } from "../state/stateController";
import { getHederaClient, getAccountByIdOrAlias } from "../state/stateService";
import { display } from "../utils/display";
import { Logger } from "../utils/logger";
import api from "../api";

import type { Account } from "../../types";

const logger = Logger.getInstance();

function clearAddressBook(): void {
  saveStateAttribute("accounts", {});
}

function deleteAccount(accountIdOrAlias: string): void {
  const account = getAccountByIdOrAlias(accountIdOrAlias);

  if (!account) {
    logger.error("Account not found");
    return;
  }

  const accounts = getState("accounts");  
  delete accounts[account.alias];

  saveStateAttribute("accounts", accounts);
}

async function createAccount(balance: number, type: string, alias: string): Promise<Account> {
  // Validate balance
  if (isNaN(balance) || balance <= 0) {
    logger.error("Invalid balance. Balance must be a positive number.");
    throw new Error("Invalid balance. Balance must be a positive number.");
  }

  // Validate type
  if (!["ecdsa", "ed25519"].includes(type.toLowerCase())) {
    logger.error('Invalid type. Type must be either "ecdsa" or "ed25519".');
    throw new Error('Invalid type. Type must be either "ecdsa" or "ed25519".')
  }

  // Get client from config
  const accounts: Record<string, Account> = getState("accounts");
  const client = getHederaClient();

  // Generate random alias if "random" is provided
  let isRandomAlias = false;
  if (alias.toLowerCase() === "random") {
    isRandomAlias = true;
    let newAlias = generateRandomAlias();
    alias = newAlias; // Implement this function to generate a random string
  }

  // Check if name is unique
  if (!isRandomAlias && accounts && accounts[alias]) {
    logger.error("An account with this alias already exists.");
    client.close();
    throw new Error("An account with this alias already exists.")
  }

  // Handle different types of account creation
  let newAccountPrivateKey, newAccountPublicKey;
  if (type.toLowerCase() === "ed25519") {
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
      .execute(client);

    // Get the new account ID
    const getReceipt = await newAccount.getReceipt(client);
    newAccountId = getReceipt.accountId;
  } catch (error) {
    logger.error("Error creating new account:", error as object);
    client.close();
  }

  if (newAccountId == null) {
    logger.error("Account was not created");
    client.close();
    throw new Error("Account was not created");
  }

  // Store the new account in the config
  const newAccountDetails = {
    alias,
    accountId: newAccountId.toString(),
    type,
    publicKey: newAccountPrivateKey.publicKey.toString(),
    evmAddress:
      type.toLowerCase() === "ed25519"
        ? ""
        : newAccountPrivateKey.publicKey.toEvmAddress(),
    solidityAddress:
      type.toLowerCase() === "ed25519" ? "" : newAccountId.toSolidityAddress(),
    solidityAddressFull:
      type.toLowerCase() === "ed25519"
        ? ""
        : `0x${newAccountId.toSolidityAddress()}`,
    privateKey: newAccountPrivateKey.toString(),
  };

  // Add the new account to the accounts object in the config
  const updatedAccounts = { ...accounts, [alias]: newAccountDetails };
  saveStateAttribute("accounts", updatedAccounts);

  // Log the account ID
  logger.log(`The new account ID is: ${newAccountId}, with alias: ${alias}`);

  client.close();

  return newAccountDetails;
}

function listAccounts(showPrivateKeys: boolean = false): void {
  const accounts: Record<string, Account> = getState("accounts");

  // Check if there are any accounts in the config
  if (!accounts || Object.keys(accounts).length === 0) {
    logger.log("No accounts found.");
    return;
  }

  // Log details for each account
  logger.log("Accounts:");
  for (const [alias, account] of Object.entries(accounts)) {
    logger.log(`- Alias: ${alias}`);
    logger.log(`  Account ID: ${account.accountId}`);
    logger.log(`  Type: ${account.type}`);
    if (showPrivateKeys) {
      logger.log(`  Private Key: ${account.privateKey}`);
    }
  }
}

// Write the importAccount function here
function importAccount(id: string, key: string, alias: string): void {
  const accounts = getState("accounts");

  // Check if name is unique
  if (accounts && accounts[alias]) {
    logger.error("An account with this alias already exists.");
    return;
  }

  let privateKey, type;
  const accountId = AccountId.fromString(id);
  switch (getKeyType(key)) {
    case "ecdsa":
      type = "ECDSA";
      privateKey = PrivateKey.fromStringECDSA(key);
      break;
    case "ed25519":
      type = "ED25519";
      privateKey = PrivateKey.fromStringED25519(key);
      break;
    default:
      logger.error(
        "Invalid key type. Only ECDSA and ED25519 keys are supported."
      );
      return;
  }

  // No Solidity and EVM address for ED25519 keys
  const updatedAccounts = { ...accounts };
  updatedAccounts[alias] = {
    accountId: id,
    type,
    publicKey: privateKey.publicKey.toString(),
    evmAddress:
      type.toLowerCase() === "ed25519"
        ? ""
        : privateKey.publicKey.toEvmAddress(),
    solidityAddress:
      type.toLowerCase() === "ed25519"
        ? ""
        : privateKey.publicKey.toEvmAddress(),
    solidityAddressFull: `0x${accountId.toSolidityAddress()}`,
    privateKey: key,
  };

  saveStateAttribute("accounts", updatedAccounts);
}

async function getAccountBalance(
  accountIdOrAlias: string,
  onlyHbar: boolean = false,
  tokenId?: string
) {
  const accounts = getState("accounts");
  const client = getHederaClient();

  let accountId;

  // Check if input is an alias or an account ID
  const accountIdPattern = /^0\.0\.\d+$/;
  if (accountIdPattern.test(accountIdOrAlias)) {
    accountId = accountIdOrAlias;
  } else if (accounts && accounts[accountIdOrAlias]) {
    accountId = accounts[accountIdOrAlias].accountId;
  } else {
    logger.error("Invalid account ID or alias not found in address book.");
    client.close();
    return;
  }

  try {
    logger.log("Getting API balance");
    const response = await api.account.getAccountBalance(accountId);
    display("displayBalance", response, { onlyHbar, tokenId });
  } catch (error) {
    logger.error("Error fetching account balance:", error as object);
  }

  client.close();
}

function findAccountByPrivateKey(privateKey: string): Account {
  const accounts: Record<string, Account> = getState("accounts");
  if (!accounts) throw new Error("No accounts found in state");

  let matchingAccount: Account | null = null;
  for (const [alias, account] of Object.entries(accounts)) {
    if (account.privateKey === privateKey) {
      matchingAccount = account;
      break; // Exit the loop once a matching account is found
    }
  }

  if (!matchingAccount) throw new Error("No matching account found for treasury key");

  return matchingAccount;
}

function getKeyType(keyString: string): string {
  try {
    PrivateKey.fromStringED25519(keyString);
    return "ed25519";
  } catch (e) {
    // Not an Ed25519 private key
  }

  try {
    PrivateKey.fromStringECDSA(keyString);
    return "ecdsa";
  } catch (e) {
    // Not an ECDSA private key
  }

  return "Unknown key type";
}

function generateRandomAlias(): string {
  const length = 20; // Define the length of the random string
  const characters =
    "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
  let result = "";
  for (let i = 0; i < length; i++) {
    result += characters.charAt(Math.floor(Math.random() * characters.length));
  }
  return result;
}

const accountUtils = {
  createAccount,
  listAccounts,
  importAccount,
  getAccountBalance,
  getKeyType,
  generateRandomAlias,
  clearAddressBook,
  deleteAccount,
  findAccountByPrivateKey,
};

export default accountUtils;