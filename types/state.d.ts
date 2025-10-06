export type Topic = {
  network: string;
  topicId: string;
  memo?: string;
  adminKey?: string;
  submitKey?: string;
};

export type Account = {
  network: string;
  name: string;
  accountId: string;
  type: string;
  publicKey: string;
  evmAddress: string;
  solidityAddress: string;
  solidityAddressFull: string;
  privateKey: string;
};

export type Script = {
  name: string;
  creation: number;
  commands: string[];
  args: Record<string, string>;
};

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
};

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
  name: string;
  accountId: string;
}

export interface NetworkConfig {
  mirrorNodeUrl: string;
  rpcUrl: string;
  operatorKey: string;
  operatorId: string;
  hexKey: string;
}

export interface State {
  network: string;
  networks: Record<string, NetworkConfig>;
  telemetryServer: string;
  telemetry: number;
  debug?: boolean;
  // Script execution runtime status persisted as structured object
  scriptExecution: {
    active: boolean;
    name: string;
  };
  // this will hold all the accounts and operator keys etc
  accounts: Record<string, Account>;
  scripts: Record<string, Script>;
  tokens: Record<string, Token>;
  topics: Record<string, Topic>;
  localNodeAddress: string;
  localNodeAccountId: string;
  localNodeMirrorAddressGRPC: string;
  uuid: string;
}

export interface DownloadState {
  accounts: Record<string, Account>;
  scripts: Record<string, Script>;
  tokens: Record<string, Token>;
  topics: Record<string, Topic>;
}
