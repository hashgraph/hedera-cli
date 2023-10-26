export type Account = {
  alias: string;
  accountId: string;
  type: string;
  publicKey: string;
  evmAddress: string;
  solidityAddress: string;
  solidityAddressFull: string;
  privateKey: string;
}

type Script = {
  name: string;
  creation: number;
  commands: string[];
}

export type Token = {
  associations: Association[];
  tokenId: string;
  name: string;
  symbol: string;
  treasuryId: string;
  treasuryKey: string;
  decimals: number;
  initialSupply: number;
  adminKey?: string;
  pauseKey?: string;
  kycKey?: string;
  wipeKey?: string;
  freezeKey?: string;
  supplyKey?: string;
  feeScheduleKey?: string;
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
  accounts: Record<string, Account>;
  scripts: Record<string, Script>;
  operatorKey: string;
  operatorId: string;
  tokens: Record<string, Token>;
}
