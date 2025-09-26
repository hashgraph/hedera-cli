import { saveState as storeSaveState } from '../../src/state/store';
import stateUtils from '../../src/utils/state';
import { baseState } from '../helpers/state';

// Mock the Client constructor and methods
jest.mock('@hashgraph/sdk', () => ({
  Client: {
    forMainnet: jest.fn(() => ({
      setOperator: jest.fn().mockReturnThis(),
      setMirrorNetwork: jest.fn().mockReturnThis(),
    })),
    forTestnet: jest.fn(() => ({
      setOperator: jest.fn().mockReturnThis(),
      setMirrorNetwork: jest.fn().mockReturnThis(),
    })),
    forPreviewnet: jest.fn(() => ({
      setOperator: jest.fn().mockReturnThis(),
      setMirrorNetwork: jest.fn().mockReturnThis(),
    })),
    forNetwork: jest.fn(() => ({
      setOperator: jest.fn().mockReturnThis(),
      setMirrorNetwork: jest.fn().mockReturnThis(),
    })),
  },
  AccountId: {
    fromString: jest.fn((id) => ({ toString: () => id })),
  },
  PrivateKey: {
    fromStringDer: jest.fn((key) => ({ toString: () => key })),
  },
}));

describe('state utilities', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('getAvailableNetworks', () => {
    test('✅ returns all networks from state including custom ones', () => {
      // Arrange - Add custom networks to the state
      const stateCopy = {
        ...baseState,
        networks: {
          ...baseState.networks,
          'solo-local': {
            mirrorNodeUrl: 'http://localhost:8081/api/v1',
            rpcUrl: 'http://localhost:8080',
            operatorKey:
              '302e020100300506032b65700422042091132178e72057a1d7528025956fe39b0b847f200ab59b2fdd367017f3087137',
            operatorId: '0.0.1001',
            hexKey:
              '0x87592ee314bd0f42c4cf9f82b494481a2bb77bab0dc4454eedfe00f60168646f',
          },
          'custom-network': {
            mirrorNodeUrl: 'https://custom.mirror.hedera.com/api/v1',
            rpcUrl: 'https://custom.rpc.hedera.com',
            operatorKey:
              '302e020100300506032b65700422042091132178e72057a1d7528025956fe39b0b847f200ab59b2fdd367017f3087137',
            operatorId: '0.0.1001',
            hexKey:
              '0x87592ee314bd0f42c4cf9f82b494481a2bb77bab0dc4454eedfe00f60168646f',
          },
        },
      };

      storeSaveState(stateCopy as any);

      // Act
      const networks = stateUtils.getAvailableNetworks();

      // Assert
      expect(networks).toEqual(
        expect.arrayContaining([
          'localnet',
          'testnet',
          'previewnet',
          'mainnet',
          'solo-local',
          'custom-network',
        ]),
      );
    });
  });

  describe('switchNetwork', () => {
    test('✅ switches to custom network successfully', () => {
      // Arrange - Add custom network to the state
      const stateCopy = {
        ...baseState,
        networks: {
          ...baseState.networks,
          'solo-local': {
            mirrorNodeUrl: 'http://localhost:8081/api/v1',
            rpcUrl: 'http://localhost:8080',
            operatorKey:
              '302e020100300506032b65700422042091132178e72057a1d7528025956fe39b0b847f200ab59b2fdd367017f3087137',
            operatorId: '0.0.1001',
            hexKey:
              '0x87592ee314bd0f42c4cf9f82b494481a2bb77bab0dc4454eedfe00f60168646f',
          },
        },
      };

      storeSaveState(stateCopy as any);

      // Act
      stateUtils.switchNetwork('solo-local');

      // Assert
      expect(stateUtils.getNetwork()).toBe('solo-local');
    });

    test('❌ throws error for non-existent network', () => {
      // Arrange
      const stateCopy = { ...baseState };
      storeSaveState(stateCopy as any);

      // Act & Assert
      expect(() => {
        stateUtils.switchNetwork('non-existent-network');
      }).toThrow('Invalid network name');
    });

    test('❌ throws error for network without operator credentials', () => {
      // Arrange - Add network without operator credentials
      const stateCopy = {
        ...baseState,
        networks: {
          ...baseState.networks,
          'no-operator-network': {
            mirrorNodeUrl: 'https://no-operator.mirror.hedera.com/api/v1',
            rpcUrl: 'https://no-operator.rpc.hedera.com',
            operatorKey: '', // Empty operator key
            operatorId: '', // Empty operator ID
            hexKey: '',
          },
        },
      };

      storeSaveState(stateCopy as any);

      // Act & Assert
      expect(() => {
        stateUtils.switchNetwork('no-operator-network');
      }).toThrow('operator key and ID not set');
    });
  });

  describe('getHederaClient with custom networks', () => {
    test('✅ creates client for custom network with RPC URL', () => {
      // Arrange - Add custom network to the state and switch to it
      const stateCopy = {
        ...baseState,
        networks: {
          ...baseState.networks,
          'solo-local': {
            mirrorNodeUrl: 'http://localhost:8081/api/v1',
            rpcUrl: 'http://localhost:8080',
            operatorKey:
              '302e020100300506032b65700422042091132178e72057a1d7528025956fe39b0b847f200ab59b2fdd367017f3087137',
            operatorId: '0.0.1001',
            hexKey:
              '0x87592ee314bd0f42c4cf9f82b494481a2bb77bab0dc4454eedfe00f60168646f',
          },
        },
        network: 'solo-local', // Set as active network
      };

      storeSaveState(stateCopy as any);

      // Act
      const client = stateUtils.getHederaClient();

      // Assert
      expect(client).toBeDefined();
      // Note: We can't easily test the internal client configuration due to mocking,
      // but we can verify the function doesn't throw an error
    });

    test('❌ throws error for custom network without RPC URL', () => {
      // Arrange - Add custom network without RPC URL
      const stateCopy = {
        ...baseState,
        networks: {
          ...baseState.networks,
          'no-rpc-network': {
            mirrorNodeUrl: 'http://localhost:8081/api/v1',
            rpcUrl: '', // Empty RPC URL
            operatorKey:
              '302e020100300506032b65700422042091132178e72057a1d7528025956fe39b0b847f200ab59b2fdd367017f3087137',
            operatorId: '0.0.1001',
            hexKey:
              '0x87592ee314bd0f42c4cf9f82b494481a2bb77bab0dc4454eedfe00f60168646f',
          },
        },
        network: 'no-rpc-network', // Set as active network
      };

      storeSaveState(stateCopy as any);

      // Act & Assert
      expect(() => {
        stateUtils.getHederaClient();
      }).toThrow('RPC URL not configured for network');
    });
  });
});
