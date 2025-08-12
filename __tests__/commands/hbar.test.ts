import { baseState, fullState, bob, alice } from '../helpers/state';
import { Command } from 'commander';
import commands from '../../src/commands';
import { saveState as storeSaveState } from '../../src/state/store';
import hbarUtils from '../../src/utils/hbar';

describe('hbar transfer command', () => {
  const hbarUtilsSpy = jest.spyOn(hbarUtils, 'transfer').mockResolvedValue();

  beforeEach(() => {
    storeSaveState(fullState as any);
  });

  describe('hbar transfer - success path', () => {
    afterEach(() => {
      // Spy cleanup
      hbarUtilsSpy.mockClear();
    });

    test('✅ transfer hbar from alice to bob account IDs', async () => {
      // Arrange
      const program = new Command();
      commands.hbarCommands(program);
      const amount = '10';

      // Act
      await program.parseAsync([
        'node',
        'hedera-cli.ts',
        'hbar',
        'transfer',
        '-f',
        alice.accountId,
        '-t',
        bob.accountId,
        '-b',
        amount,
      ]);

      // Assert
      expect(hbarUtilsSpy).toHaveBeenCalledWith(
        Number(amount),
        alice.accountId,
        bob.accountId,
        undefined,
      );
    });
  });
});
