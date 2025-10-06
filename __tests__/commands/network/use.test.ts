import { Command } from 'commander';
import commands from '../../../src/commands';
import {
  get as storeGet,
  saveState as storeSaveState,
} from '../../../src/state/store';
import { baseState } from '../../helpers/state';

// TODO: Re-enable this suite. It makes CI exit with code 1 (global exitCode/listener leakage). Tracked in #827.
// https://github.com/hashgraph/hedera-cli/issues/827
describe.skip('network use command', () => {
  beforeEach(() => {
    const stateCopy = {
      ...baseState,
      // Provide a bogus mainnet operator ID and key
      mainnetOperatorId: '0.0.1001',
      mainnetOperatorKey:
        '302e020100300506032b65700422042091132178e72057a1d7528025956fe39b0b847f200ab59b2fdd367017f3087137',
    };

    storeSaveState(stateCopy as any);
  });

  describe('network use - success path', () => {
    afterEach(() => {});

    test('✅ switch to mainnet', async () => {
      // Assert
      expect(storeGet('network' as any)).toEqual('localnet');

      // Arrange
      const program = new Command();
      commands.networkCommands(program);

      // Act
      await program.parseAsync([
        'node',
        'hedera-cli.ts',
        'network',
        'use',
        'mainnet',
      ]);

      // Assert
      expect(storeGet('network' as any)).toEqual('mainnet');
    });

    test('✅ switch to custom network from config', async () => {
      // Arrange - Add a custom network to the state
      const stateCopy = {
        ...baseState,
        networks: {
          ...baseState.networks,
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
        mainnetOperatorId: '0.0.1001',
        mainnetOperatorKey:
          '302e020100300506032b65700422042091132178e72057a1d7528025956fe39b0b847f200ab59b2fdd367017f3087137',
      };

      storeSaveState(stateCopy as any);

      // Assert initial state
      expect(storeGet('network' as any)).toEqual('localnet');

      // Arrange
      const program = new Command();
      commands.networkCommands(program);

      // Act
      await program.parseAsync([
        'node',
        'hedera-cli.ts',
        'network',
        'use',
        'custom-network',
      ]);

      // Assert
      expect(storeGet('network' as any)).toEqual('custom-network');
    });

    test('✅ switch to solo-local network from config', async () => {
      // Arrange - Add solo-local network to the state
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
        mainnetOperatorId: '0.0.1001',
        mainnetOperatorKey:
          '302e020100300506032b65700422042091132178e72057a1d7528025956fe39b0b847f200ab59b2fdd367017f3087137',
      };

      storeSaveState(stateCopy as any);

      // Assert initial state
      expect(storeGet('network' as any)).toEqual('localnet');

      // Arrange
      const program = new Command();
      commands.networkCommands(program);

      // Act
      await program.parseAsync([
        'node',
        'hedera-cli.ts',
        'network',
        'use',
        'solo-local',
      ]);

      // Assert
      expect(storeGet('network' as any)).toEqual('solo-local');
    });
  });

  describe('network use - error cases', () => {
    test('❌ switch to non-existent network', async () => {
      // Arrange
      const program = new Command();
      commands.networkCommands(program);

      // Act
      await program.parseAsync([
        'node',
        'hedera-cli.ts',
        'network',
        'use',
        'non-existent-network',
      ]);

      // Assert - exitOnError should set process.exitCode for DomainError
      expect(process.exitCode).toBeDefined();
      expect(process.exitCode).toBeGreaterThan(0);
    });

    test('❌ switch to network without operator credentials', async () => {
      // Arrange - Add a network without operator credentials
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

      // Arrange
      const program = new Command();
      commands.networkCommands(program);

      // Act
      await program.parseAsync([
        'node',
        'hedera-cli.ts',
        'network',
        'use',
        'no-operator-network',
      ]);

      // Assert - exitOnError should set process.exitCode for DomainError
      expect(process.exitCode).toBeDefined();
      expect(process.exitCode).toBeGreaterThan(0);
    });
  });

  describe('network use - edge cases', () => {
    test('✅ switch to network with special characters in name', async () => {
      // Arrange - Add a network with special characters
      const stateCopy = {
        ...baseState,
        networks: {
          ...baseState.networks,
          'test-network-123': {
            mirrorNodeUrl: 'https://test-123.mirror.hedera.com/api/v1',
            rpcUrl: 'https://test-123.rpc.hedera.com',
            operatorKey:
              '302e020100300506032b65700422042091132178e72057a1d7528025956fe39b0b847f200ab59b2fdd367017f3087137',
            operatorId: '0.0.1001',
            hexKey:
              '0x87592ee314bd0f42c4cf9f82b494481a2bb77bab0dc4454eedfe00f60168646f',
          },
        },
        mainnetOperatorId: '0.0.1001',
        mainnetOperatorKey:
          '302e020100300506032b65700422042091132178e72057a1d7528025956fe39b0b847f200ab59b2fdd367017f3087137',
      };

      storeSaveState(stateCopy as any);

      // Arrange
      const program = new Command();
      commands.networkCommands(program);

      // Act
      await program.parseAsync([
        'node',
        'hedera-cli.ts',
        'network',
        'use',
        'test-network-123',
      ]);

      // Assert
      expect(storeGet('network' as any)).toEqual('test-network-123');
    });

    test('✅ switch to network with uppercase name', async () => {
      // Arrange - Add a network with uppercase name
      const stateCopy = {
        ...baseState,
        networks: {
          ...baseState.networks,
          UPPERCASE_NETWORK: {
            mirrorNodeUrl: 'https://uppercase.mirror.hedera.com/api/v1',
            rpcUrl: 'https://uppercase.rpc.hedera.com',
            operatorKey:
              '302e020100300506032b65700422042091132178e72057a1d7528025956fe39b0b847f200ab59b2fdd367017f3087137',
            operatorId: '0.0.1001',
            hexKey:
              '0x87592ee314bd0f42c4cf9f82b494481a2bb77bab0dc4454eedfe00f60168646f',
          },
        },
        mainnetOperatorId: '0.0.1001',
        mainnetOperatorKey:
          '302e020100300506032b65700422042091132178e72057a1d7528025956fe39b0b847f200ab59b2fdd367017f3087137',
      };

      storeSaveState(stateCopy as any);

      // Arrange
      const program = new Command();
      commands.networkCommands(program);

      // Act
      await program.parseAsync([
        'node',
        'hedera-cli.ts',
        'network',
        'use',
        'UPPERCASE_NETWORK',
      ]);

      // Assert
      expect(storeGet('network' as any)).toEqual('UPPERCASE_NETWORK');
    });
  });
});
