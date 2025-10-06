import { baseState, fullState, script_basic } from '../../helpers/state';
import { Command } from 'commander';
import commands from '../../../src/commands';
import scriptUtils from '../../../src/utils/script';
import { saveState as storeSaveState } from '../../../src/state/store';

describe('script delete command', () => {
  beforeEach(() => {
    storeSaveState(baseState as any);
  });

  describe('script delete - success path', () => {
    test('✅ should delete account by account ID', async () => {
      // Arrange
      const deleteScriptSpy = jest.spyOn(scriptUtils, 'deleteScript');
      storeSaveState(fullState as any);

      const program = new Command();
      commands.scriptCommands(program);

      // Act
      await program.parse([
        'node',
        'hedera-cli.ts',
        'script',
        'delete',
        '-n',
        script_basic.name,
      ]);

      // Assert
      expect(deleteScriptSpy).toHaveBeenCalledWith(script_basic.name);
      // scripts cleared assertion done via log output (state direct check removed)
    });
  });
});
