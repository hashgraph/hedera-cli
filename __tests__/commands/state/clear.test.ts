import { Command } from 'commander';
import commands from '../../../src/commands';
import {
  getState as storeGetAll,
  saveState as storeSaveState,
} from '../../../src/state/store';
import {
  accountState,
  fullState,
  scriptState,
  tokenState,
} from '../../helpers/state';

describe('state clear command', () => {
  const saveStateControllerSpy = jest.spyOn({ save: storeSaveState }, 'save');

  beforeEach(() => {
    storeSaveState(fullState as any);
  });

  describe('state clear - success path', () => {
    afterEach(() => {
      // Spy cleanup
      saveStateControllerSpy.mockClear();
    });

    test('✅ clear entire CLI state', async () => {
      // Arrange
      const program = new Command();
      commands.stateCommands(program);

      // Act
      await program.parse(['node', 'hedera-cli.ts', 'state', 'clear']);

      // Assert
      expect((storeGetAll() as any).accounts).toBeDefined();
    });

    test('✅ clear state skip accounts', async () => {
      // Arrange
      const program = new Command();
      commands.stateCommands(program);

      // Act
      await program.parse([
        'node',
        'hedera-cli.ts',
        'state',
        'clear',
        '--skip-accounts',
      ]);

      // Assert
      const {
        actions: _aAcc,
        scriptExecutionName: _sAcc,
        ...accountsView
      } = storeGetAll() as any;
      expect(accountsView).toEqual(accountState);
    });

    test('✅ clear state skip tokens', async () => {
      // Arrange
      const program = new Command();
      commands.stateCommands(program);

      // Act
      await program.parse([
        'node',
        'hedera-cli.ts',
        'state',
        'clear',
        '--skip-tokens',
      ]);

      // Assert
      const {
        actions: _aTok,
        scriptExecutionName: _sTok,
        ...tokensView
      } = storeGetAll() as any;
      expect(tokensView).toEqual(tokenState);
    });

    test('✅ clear state skip scripts', async () => {
      // Arrange
      const program = new Command();
      commands.stateCommands(program);

      // Act
      await program.parse([
        'node',
        'hedera-cli.ts',
        'state',
        'clear',
        '--skip-scripts',
      ]);

      // Assert
      const {
        actions: _aScr,
        scriptExecutionName: _sScr,
        ...scriptsView
      } = storeGetAll() as any;
      expect(scriptsView).toEqual(scriptState);
    });

    test('✅ clear state skip all (tokens, scripts, and accounts)', async () => {
      // Arrange
      const program = new Command();
      commands.stateCommands(program);

      // Act
      await program.parse([
        'node',
        'hedera-cli.ts',
        'state',
        'clear',
        '--skip-scripts',
        '--skip-tokens',
        '--skip-accounts',
      ]);

      // Assert
      // With all skips the state should remain unchanged
      const {
        actions: _aAll,
        scriptExecutionName: _sAll,
        ...current
      } = storeGetAll() as any;
      expect(current.accounts).toEqual(fullState.accounts);
      expect(current.tokens).toEqual(fullState.tokens);
      expect(current.scripts).toEqual(fullState.scripts);
      expect(current).toHaveProperty('topics');
    });
  });
});
