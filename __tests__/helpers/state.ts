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
  networks: {
    localnet: {
      mirrorNodeUrl: 'http://localhost:5551/api/v1',
      rpcUrl: 'http://localhost:7546',
      operatorKey:
        '302e020100300506032b65700422042091132178e72057a1d7528025956fe39b0b847f200ab59b2fdd367017f3087137',
      operatorId: '0.0.2',
      hexKey: '0x87592ee314bd0f42c4cf9f82b494481a2bb77bab0dc4454eedfe00f60168646f',
    },
    previewnet: {
      mirrorNodeUrl: 'https://previewnet.mirrornode.hedera.com/api/v1',
      rpcUrl: 'https://previewnet.hashio.io/api',
      operatorKey: '',
      operatorId: '',
      hexKey: '',
    },
    testnet: {
      mirrorNodeUrl: 'https://testnet.mirrornode.hedera.com/api/v1',
      rpcUrl: 'https://testnet.hashio.io/api',
      operatorKey: '',
      operatorId: '',
      hexKey: '',
    },
    mainnet: {
      mirrorNodeUrl: 'https://mainnet.mirrornode.hedera.com/api/v1',
      rpcUrl: 'https://mainnet.hashio.io/api',
      operatorKey: '',
      operatorId: '',
      hexKey: '',
    },
  },
  telemetryServer: 'https://hedera-cli-telemetry.onrender.com/track',
  telemetry: 0,
  scriptExecution: { active: false, name: '' },
  accounts: {
  },
  scripts: {},
  tokens: {},
  topics: {},

  localNodeAddress: '127.0.0.1:50211',
  localNodeAccountId: '0.0.3',
  localNodeMirrorAddressGRPC: '127.0.0.1:5600',
  uuid: '',
};

/* accounts */
export const alice: Account = {
  network: 'localnet',
  name: 'alice',
  accountId: '0.0.6366720',
  type: 'ECDSA',
  publicKey:
    '302d300706052b8104000a032200026db3b60d397b16a4adc9a3eed28c0a22643317ca242620ceadff7c141d01e121',
  evmAddress: '7424aa05e92717bf7523e71bb28465cace4dbef6',
  solidityAddress: '0000000000000000000000000000000000612600',
  solidityAddressFull: '0x0000000000000000000000000000000000612600',
  privateKey:
    '3030020100300706052b8104000a04220420e58de2c47dee0d68b1b44fff30447c64be005dd8153fa2bc76d8c770051e5ed7',
};

export const bob: Account = {
  network: 'localnet',
  name: 'bob',
  accountId: '0.0.6025066',
  type: 'ECDSA',
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
    'account create -n random',
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
    [alice.name]: alice,
    [bob.name]: bob,
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
    [alice.name]: alice,
    [bob.name]: bob,
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
    [alice.name]: alice,
    [bob.name]: bob,
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
    accountId: testnetOperatorId,
    name: 'testnet-operator',
    evmAddress: 'a943d99f6b909dca4fce25268cb8dd3c8ef55455',
    network: 'testnet',
    privateKey: testnetOperatorKey,
    publicKey: '302d300706052b8104000a0322000369a94f7e9c67faa92b8c4357a32b8c6ac90914fdf36bf5be3713f2acbbd856e1',
    solidityAddress: '0000000000000000000000000000000000757d7c',
    solidityAddressFull: '0x0000000000000000000000000000000000757d7c',
    type: 'ECDSA',
  },
};
