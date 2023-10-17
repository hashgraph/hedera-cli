export type Account = {
  accountId: string;
  type: string;
  publickey: string;
  evmAddress: string;
  solidityAddress: string;
  solidityAddressFull: string;
  privatekey: string;
}

type Script = {
  name: string;
  creation: number;
  commands: string[];
}

type Token = {
  tokenId: string;
  name: string;
  symbol: string;
  treasuryId: string;
  treasuryKey: string;
  decimals: number;
  initialSupply: number;
  adminKey: string;
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
