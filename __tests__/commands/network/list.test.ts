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
          'localnet',
          'testnet',
          'previewnet',
          'mainnet',
        ]),
      );
    });
  });
});
