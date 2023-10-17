import type { Key } from "./shared.d.ts";

export type TokenBalance = {
  token_id: string;
  balance: number;
};

export type Balance = {
  balance: number;
  timestamp: string;
  tokens: TokenBalance[];
};

type TransactionTransfer = {
  account: string;
  amount: number;
  is_approval: boolean;
};

type Transaction = {
  bytes: null | string;
  charged_tx_fee: number;
  consensus_timestamp: string;
  entity_id: null | string;
  max_fee: string;
  memo_base64: string;
  name: string;
  nft_transfers: any[]; // Todo
  node: string;
  nonce: number;
  parent_consensus_timestamp: null | string;
  result: string;
  scheduled: boolean;
  staking_reward_transfers: any[];
  token_transfers: any[]; // Todo
  transaction_hash: string;
  transaction_id: string;
  transfers: TransactionTransfer[];
  valid_duration_seconds: string;
  valid_start_timestamp: string;
};

export type AccountResponse = {
  account: string;
  alias: string;
  auto_renew_period: number;
  balance: Balance;
  created_timestamp: string;
  decline_reward: boolean;
  deleted: boolean;
  ethereum_nonce: number;
  evm_address: string;
  expiry_timestamp: string;
  key: Key;
  max_automatic_token_associations: number;
  memo: string;
  pending_reward: number;
  receiver_sig_required: boolean;
  staked_account_id: null | string;
  staked_node_id: null | string;
  stake_period_start: null | string;
  transactions: Transaction[];
};
