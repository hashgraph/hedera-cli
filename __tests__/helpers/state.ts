import { Account, State, Script } from "../../types";

export const baseState: State = {
  network: "testnet",
  mirrorNodeTestnet: "https://testnet.mirrornode.hedera.com/api/v1",
  mirrorNodeMainnet: "https://mainnet.mirrornode.hedera.com/api/v1",
  recording: 0,
  recordingScriptName: "",
  accounts: {},
  scripts: {},
  testnetOperatorKey:
    "302e020100300506032b65700422042087592ee314bd0f42c4cf9f82b494481a2bb77bab0dc4454eedfe00f60168646f",
  testnetOperatorId: "0.0.458179",
  mainnetOperatorKey: "",
  mainnetOperatorId: "",
  tokens: {},
};

/* accounts */
export const alice: Account = {
  alias: "alice",
  accountId: "0.0.6025067",
  type: "ED25519",
  publicKey:
    "302a300506032b6570032100052ff6e06c1610e33c1c631fa44c259ab62c7becb7a97932b3d60094d0a2f8ba",
  evmAddress: "",
  solidityAddress: "00000000000000000000000000000000005bef6b",
  solidityAddressFull: "0x00000000000000000000000000000000005bef6b",
  privateKey:
    "302e020100300506032b657004220420ece0b15b20e555f66d5f4cd83187567af9613276629d7e15161b0c929ea07697",
};

export const bob: Account = {
  alias: "bob",
  accountId: "0.0.6025066",
  type: "ED25519",
  publicKey:
    "302a300506032b65700321009c7c0a15424226860552f0fd859f3995c55ebf64088214b692f87528f2e3d7e6",
  evmAddress: "",
  solidityAddress: "00000000000000000000000000000000005bef6a",
  solidityAddressFull: "0x00000000000000000000000000000000005bef6a",
  privateKey:
    "302e020100300506032b657004220420b4a0c427a47602aad6ad447dd3a0dc1cd482da23511e08a960c20bcaa77748fa",
};

export const script_basic: Script = {
  name: "basic",
  creation: 1697103669402,
  commands: [
    "network use testnet",
    "account create -a random",
    "token create-ft -n m -s mm -d 2 -i 1000 -a 302e020100300506032b6570042204202a6568253a539643468dda3128a734c9fcb07a927b3f742719a869db731f9f50 -t 0.0.4536940 -k 302e020100300506032b6570042204202a6568253a539643468dda3128a734c9fcb07a927b3f742719a869db731f9f50",
  ],
};

const token = {
  associations: [],
  tokenId: "0.0.6025124",
  name: "myToken",
  symbol: "MTK",
  treasuryId: "0.0.6025067",
  decimals: 2,
  initialSupply: 1000,
  supplyType: "finite",
  maxSupply: 1000000,
  keys: {
    adminKey:
      "3030020100300706052b8104000a0422042056ba50eb37387c7d523587652ddd5f1783965277ba781dbddeeb28f1d1a0d946",
    pauseKey: "",
    kycKey: "",
    wipeKey: "",
    freezeKey: "",
    supplyKey:
      "302e020100300506032b657004220420b4a0c427a47602aad6ad447dd3a0dc1cd482da23511e08a960c20bcaa77748fa",
    feeScheduleKey: "",
    treasuryKey:
      "302e020100300506032b657004220420ece0b15b20e555f66d5f4cd83187567af9613276629d7e15161b0c929ea07697",
  },
};

export const scriptState: State = {
  ...baseState,
  scripts: {
    [`script-${script_basic.name}`]: script_basic,
  },
};

export const tokenState: State = {
  ...baseState,
  tokens: {
    [token.tokenId]: token,
  },
};

export const fullState: State = {
  ...baseState,
  accounts: {
    [alice.alias]: alice,
    [bob.alias]: bob,
  },
  scripts: {
    [`script-${script_basic.name}`]: script_basic,
  },
  tokens: {
    [token.tokenId]: token,
  },
};
