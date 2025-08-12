import { baseState, scriptState, script_basic } from '../../helpers/state';
import { Command } from 'commander';
import commands from '../../../src/commands';
import {
  saveState as storeSaveState,
  saveKey as storeSaveKey,
} from '../../../src/state/store';

describe('script list command', () => {
  const logSpy = jest.spyOn(console, 'log');

  beforeEach(() => {
    storeSaveState(baseState as any);
  });

  describe('script list - success path', () => {
    afterEach(() => {
      // Spy cleanup
      logSpy.mockClear();
    });

    test('✅ should list all scripts from state', async () => {
      // Arrange
      const program = new Command();
      commands.scriptCommands(program);
      storeSaveState(scriptState as any);

      // Act
      await program.parse(['node', 'hedera-cli.ts', 'script', 'list']);

      // Assert
      expect(logSpy).toHaveBeenCalledWith(`\tscript-${script_basic.name}`);
      script_basic.commands.forEach((command) => {
        expect(logSpy).toHaveBeenCalledWith(`\t\t${command}`);
      });
    });
  });
});
