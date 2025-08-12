import { baseState } from '../../helpers/state';
import { Command } from 'commander';
import commands from '../../../src/commands';
import { saveState as storeSaveState } from '../../../src/state/store';

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

    test('✅ list available networks', async () => {
      // Arrange
      const program = new Command();
      commands.networkCommands(program);

      // Act
      await program.parse(['node', 'hedera-cli.ts', 'network', 'list']);

      // Assert
      expect(logSpy).toHaveBeenCalledWith(`Available networks:`);
      expect(logSpy).toHaveBeenCalledWith(`- localnet`);
      expect(logSpy).toHaveBeenCalledWith(`- testnet`);
      expect(logSpy).toHaveBeenCalledTimes(5);
    });
  });
});
