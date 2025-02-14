import {
  Account,
  State,
  Script,
  Topic,
  Token,
  DownloadState,
} from '../../types';

export const baseState: State = {
  network: 'localnet',
  mirrorNodeLocalnet: 'http://localhost:5551/api/v1',
  mirrorNodePreviewnet: 'https://previewnet.mirrornode.hedera.com/api/v1',
  mirrorNodeTestnet: 'https://testnet.mirrornode.hedera.com/api/v1',
  mirrorNodeMainnet: 'https://mainnet.mirrornode.hedera.com/api/v1',
  telemetryServer: "https://hedera-cli-telemetry.onrender.com/track",
  telemetry: 0,
  recording: 0,
  recordingScriptName: '',
  scriptExecution: 0,
  scriptExecutionName: '',
  accounts: {},
  scripts: {},
  tokens: {},
  topics: {},
  testnetOperatorKey: '',
  testnetOperatorId: '',
  mainnetOperatorKey: '',
  mainnetOperatorId: '',
  previewnetOperatorKey: '',
  previewnetOperatorId: '',
  localnetOperatorKey:
    '302e020100300506032b65700422042091132178e72057a1d7528025956fe39b0b847f200ab59b2fdd367017f3087137',
  localnetOperatorId: '0.0.2',
  localNodeAddress: '127.0.0.1:50211',
  localNodeAccountId: '0.0.3',
  localNodeMirrorAddressGRPC: '127.0.0.1:5600',
  uuid: '',
};

/* accounts */
export const alice: Account = {
  network: 'localnet',
  alias: 'alice',
  accountId: '0.0.6025067',
  type: 'ED25519',
  publicKey:
    '302a300506032b6570032100052ff6e06c1610e33c1c631fa44c259ab62c7becb7a97932b3d60094d0a2f8ba',
  evmAddress: '',
  solidityAddress: '00000000000000000000000000000000005bef6b',
  solidityAddressFull: '0x00000000000000000000000000000000005bef6b',
  privateKey:
    '302e020100300506032b657004220420ece0b15b20e555f66d5f4cd83187567af9613276629d7e15161b0c929ea07697',
};

export const bob: Account = {
  network: 'localnet',
  alias: 'bob',
  accountId: '0.0.6025066',
  type: 'ED25519',
  publicKey:
    '302a300506032b65700321009c7c0a15424226860552f0fd859f3995c55ebf64088214b692f87528f2e3d7e6',
  evmAddress: '',
  solidityAddress: '00000000000000000000000000000000005bef6a',
  solidityAddressFull: '0x00000000000000000000000000000000005bef6a',
  privateKey:
    '302e020100300506032b657004220420b4a0c427a47602aad6ad447dd3a0dc1cd482da23511e08a960c20bcaa77748fa',
};

export const script_basic: Script = {
  name: 'basic',
  creation: 1697103669402,
  commands: [
    'network use testnet',
    'account create -a random',
    'token create-ft -n m -s mm -d 2 -i 1000 -a 302e020100300506032b6570042204202a6568253a539643468dda3128a734c9fcb07a927b3f742719a869db731f9f50 -t 0.0.4536940 -k 302e020100300506032b6570042204202a6568253a539643468dda3128a734c9fcb07a927b3f742719a869db731f9f50',
  ],
  args: {},
};

export const token: Token = {
  network: 'localnet',
  associations: [],
  tokenId: '0.0.6025124',
  name: 'myToken',
  symbol: 'MTK',
  treasuryId: '0.0.6025067',
  decimals: 2,
  initialSupply: 1000,
  supplyType: 'finite',
  maxSupply: 1000000,
  keys: {
    adminKey:
      '3030020100300706052b8104000a0422042056ba50eb37387c7d523587652ddd5f1783965277ba781dbddeeb28f1d1a0d946',
    pauseKey: '',
    kycKey: '',
    wipeKey: '',
    freezeKey: '',
    supplyKey:
      '302e020100300506032b657004220420b4a0c427a47602aad6ad447dd3a0dc1cd482da23511e08a960c20bcaa77748fa',
    feeScheduleKey: '',
    treasuryKey:
      '302e020100300506032b657004220420ece0b15b20e555f66d5f4cd83187567af9613276629d7e15161b0c929ea07697',
  },
  customFees: [],
};

export const topic: Topic = {
  network: 'localnet',
  topicId: '0.0.123',
  memo: 'test',
  adminKey: '',
  submitKey: '',
};

export const accountState: State = {
  ...baseState,
  accounts: {
    [alice.alias]: alice,
    [bob.alias]: bob,
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

export const topicState: State = {
  ...baseState,
  topics: {
    [topic.topicId]: topic,
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
  topics: {
    [topic.topicId]: topic,
  },
};

export const downloadState: DownloadState = {
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
  topics: {
    [topic.topicId]: topic,
  },
};

export const testnetOperatorKey =
  '302e020100300506032b6570042204202ef1cb430150535aa15bdcc6609ff2ef4ec843eb35f1d0cc655a4cad2130b796'; // dummy account
export const testnetOperatorId = '0.0.7699836';

export const testnetOperatorAccount: Record<string, Account> = {
  'testnet-operator': {
    accountId: '0.0.7699836',
    alias: 'testnet-operator',
    evmAddress: '',
    network: 'testnet',
    privateKey:
      '302e020100300506032b6570042204202ef1cb430150535aa15bdcc6609ff2ef4ec843eb35f1d0cc655a4cad2130b796',
    publicKey:
      '302a300506032b6570032100b5416f8c0c2836904c58082e4e4a4e923db30bcf85aa189b41fa91062eb8e98b',
    solidityAddress: '0000000000000000000000000000000000757d7c',
    solidityAddressFull: '0x0000000000000000000000000000000000757d7c',
    type: 'ed25519',
  },
};
