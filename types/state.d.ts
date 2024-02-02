import {
  TokenSupplyType,
} from "@hashgraph/sdk";

export type Topic = {
  topicId: string;
  memo?: string;
  adminKey?: string;
  submitKey?: string;
}

export type Account = {
  network: string;
  alias: string;
  accountId: string;
  type: string;
  publicKey: string;
  evmAddress: string;
  solidityAddress: string;
  solidityAddressFull: string;
  privateKey: string;
}

export type Script = {
  name: string;
  creation: number;
  commands: string[];
  args: Record<string, string>;
}

export type Token = {
  associations: Association[];
  tokenId: string;
  name: string;
  symbol: string;
  treasuryId: string;
  decimals: number;
  initialSupply: number;
  supplyType: string;
  maxSupply: number;
  keys: Keys;
  network: string;
}

export interface Keys {
  adminKey: string;
  supplyKey: string;
  wipeKey: string;
  kycKey: string;
  freezeKey: string;
  pauseKey: string;
  feeScheduleKey: string;
  treasuryKey: string;
}

export interface Association {
  alias: string;
  accountId: string;
}

export interface State {
  network: string;
  mirrorNodeTestnet: string;
  mirrorNodeMainnet: string;
  recording: number;
  recordingScriptName: string;
  scriptExecution: number;
  scriptExecutionName: string;
  accounts: Record<string, Account>;
  scripts: Record<string, Script>;
  tokens: Record<string, Token>;
  topics: Record<string, Topic>;
  previewnetOperatorKey: string;
  previewnetOperatorId: string;
  testnetOperatorKey: string;
  testnetOperatorId: string;
  mainnetOperatorKey: string;
  mainnetOperatorId: string;
}
