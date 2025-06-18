import {
  TokenSupplyType,
} from "@hashgraph/sdk";

export type Topic = {
  network: string;
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

interface Fee {
  collectorId?: string;
  exempt?: boolean;
}

export interface FixedFee extends Fee {
  type: string;
  unitType: string;
  amount: number;
  denom?: string;
}

export interface FractionalFee extends Fee {
  type: string;
  numerator: number;
  denominator: number;
  min?: number;
  max?: number;
}

export type CustomFeeInput = FixedFee | FractionalFee;

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
  customFees: CustomFeeInput[];
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
  mirrorNodeLocalnet: string;
  mirrorNodePreviewnet: string;
  mirrorNodeTestnet: string;
  mirrorNodeMainnet: string;
  rpcUrlMainnet: string;
  rpcUrlTestnet: string;
  rpcUrlPreviewnet: string;
  rpcUrlLocalnet: string;
  telemetryServer: string;
  telemetry: number;
  scriptExecution: number;
  scriptExecutionName: string;
  accounts: Record<string, Account>;
  scripts: Record<string, Script>;
  tokens: Record<string, Token>;
  topics: Record<string, Topic>;
  localnetOperatorKey: string;
  localnetOperatorKeyHex: string;
  localnetOperatorId: string;
  localNodeAddress: string;
  localNodeAccountId: string;
  localNodeMirrorAddressGRPC: string;
  previewnetOperatorKey: string;
  previewnetOperatorKeyHex: string;
  previewnetOperatorId: string;
  testnetOperatorKey: string;
  testnetOperatorKeyHex: string;
  testnetOperatorId: string;
  mainnetOperatorKey: string;
  mainnetOperatorKeyHex: string;
  mainnetOperatorId: string;
  uuid: string;
  memory: Record<string, any>;
}

export interface DownloadState {
  accounts: Record<string, Account>, 
  scripts: Record<string, Script>, 
  tokens: Record<string, Token>, 
  topics: Record<string, Topic>
}
