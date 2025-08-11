import { State } from '../../types';
/**
 * Base config used in application startup/testing etc. Used to help us populate a base state model.
 */
const defaultConfig: State = {
  network: 'localnet',
  networks: {
    localnet: {
      mirrorNodeUrl: 'http://localhost:5551/api/v1',
      rpcUrl: 'http://localhost:7546',
      operatorKey:
        '302e020100300506032b65700422042091132178e72057a1d7528025956fe39b0b847f200ab59b2fdd367017f3087137',
      operatorId: '0.0.2',
      hexKey:
        '0x87592ee314bd0f42c4cf9f82b494481a2bb77bab0dc4454eedfe00f60168646f',
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
  accounts: {},
  tokens: {},
  scripts: {},
  topics: {},
  localNodeAddress: '127.0.0.1:50211',
  localNodeAccountId: '0.0.3',
  localNodeMirrorAddressGRPC: '127.0.0.1:5600',
  uuid: '',
};

export default defaultConfig;
