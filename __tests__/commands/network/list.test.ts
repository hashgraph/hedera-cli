import { Command } from 'commander';
import commands from '../../../src/commands';
import { saveState as storeSaveState } from '../../../src/state/store';
import { setGlobalOutputMode } from '../../../src/utils/output';
import { baseState } from '../../helpers/state';

describe('network list command', () => {
  const logSpy = jest.spyOn(console, 'log');

  describe('network list - success path', () => {
    beforeEach(() => {
      const stateCopy = {
        ...baseState,
        // Provide a bogus mainnet operator ID and key
        localnetOperatorKey: 'mykey',
      };

      storeSaveState(stateCopy as any);
    });

    afterEach(() => {
      // Spy cleanup
      logSpy.mockClear();
    });

    test('✅ list available networks (JSON)', async () => {
      // Arrange
      const program = new Command();
      commands.networkCommands(program);
      // Force JSON output mode directly (root --json flag lives in top-level CLI setup)
      setGlobalOutputMode({ json: true });

      // Act
      await program.parse(['node', 'hedera-cli.ts', 'network', 'list']);

      // Assert (single JSON call)
      expect(logSpy).toHaveBeenCalledTimes(1);
      const payload = JSON.parse(String(logSpy.mock.calls[0][0]));
      expect(payload.networks).toEqual(
        expect.arrayContaining([
          expect.objectContaining({ name: 'localnet' }),
          expect.objectContaining({ name: 'testnet' }),
          expect.objectContaining({ name: 'previewnet' }),
          expect.objectContaining({ name: 'mainnet' }),
        ]),
      );
      expect(payload.activeNetwork).toBe('localnet');
    });

    test('✅ list available networks including custom networks (JSON)', async () => {
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
        localnetOperatorKey: 'mykey',
      };

      storeSaveState(stateCopy as any);

      // Arrange
      const program = new Command();
      commands.networkCommands(program);
      // Force JSON output mode directly
      setGlobalOutputMode({ json: true });

      // Act
      await program.parse(['node', 'hedera-cli.ts', 'network', 'list']);

      // Assert (single JSON call)
      expect(logSpy).toHaveBeenCalledTimes(1);
      const payload = JSON.parse(String(logSpy.mock.calls[0][0]));
      expect(payload.networks).toEqual(
        expect.arrayContaining([
          expect.objectContaining({ name: 'localnet' }),
          expect.objectContaining({ name: 'testnet' }),
          expect.objectContaining({ name: 'previewnet' }),
          expect.objectContaining({ name: 'mainnet' }),
          expect.objectContaining({ name: 'solo-local' }),
          expect.objectContaining({ name: 'custom-network' }),
        ]),
      );
      expect(payload.activeNetwork).toBe('localnet');
    });
  });
});
