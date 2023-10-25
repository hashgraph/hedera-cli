import {
  Client,
  AccountId,
  PrivateKey,
} from "@hashgraph/sdk";

import {
  getState,
  getAllState,
  saveState,
  saveStateAttribute,
} from "./stateController";

import type { Account } from "../../types";

/** hook (middleware)
 * @example command ['account', 'create', '-b', '1000', '-t', 'ed25519']
 */
function recordCommand(command: string[]): void {
  const state = getAllState();
  if (state.recording === 1) {
    state.scripts[state.recordingScriptName].commands.push(command.join(" "));

    saveState(state);
  }
}

function getMirrorNodeURL(): string {
  const network = getState("network");
  const mirrorNodeURL =
    network === "testnet"
      ? getState("mirrorNodeTestnet")
      : getState("mirrorNodeMainnet");
  return mirrorNodeURL;
}

function getHederaClient(): Client {
  const state = getAllState();
  let client: Client;

  switch (state.network) {
    case "mainnet":
      client = Client.forMainnet();
      break;
    case "testnet":
      client = Client.forTestnet();
      break;
    default:
      throw new Error(`Unsupported network: ${state.network}`);
  }

  return client.setOperator(
    AccountId.fromString(state.operatorId),
    PrivateKey.fromString(state.operatorKey)
  );
}

function switchNetwork(name: string) {
  if (!["mainnet", "testnet"].includes(name)) {
    console.error("Invalid network name. Available networks: mainnet, testnet");
    return;
  }

  saveStateAttribute("network", name);
}

/* Accounts */
function getAccountById(accountId: string): (Account|undefined) {
  const accounts: Record<string, Account> = getState("accounts");
  const account = Object.values(accounts).find((account: Account) => account.accountId === accountId);
  return account;
}

function getAccountByAlias(alias: string): (Account|undefined) {
  const accounts: Record<string, Account> = getState("accounts");
  return accounts[alias];
}

export {
  getMirrorNodeURL,
  getHederaClient,
  recordCommand,
  switchNetwork,

  getAccountById,
  getAccountByAlias,
};
