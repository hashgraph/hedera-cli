import { fullState, alice } from '../../helpers/state';
import { Command } from 'commander';
import commands from '../../../src/commands';
import { saveState as storeSaveState } from '../../../src/state/store';
import { Logger } from '../../../src/utils/logger';

const logger = Logger.getInstance();

describe('state view command', () => {
  const logSpy = jest.spyOn(logger, 'log');

  beforeEach(() => {
    storeSaveState(fullState as any);
  });

  describe('state view - success path', () => {
    afterEach(() => {
      // Spy cleanup
      logSpy.mockClear();
    });

    test('✅ view entire state', async () => {
      // Arrange
      const program = new Command();
      commands.stateCommands(program);

      // Act
      await program.parse(['node', 'hedera-cli.ts', 'state', 'view']);

      // Assert
      expect(logSpy).toHaveBeenCalledWith('\nState:');
      const calls = logSpy.mock.calls.map((c) => c[0]);
      // Find the logged state object and compare without actions field
      const loggedState = calls.find(
        (v) => v && typeof v === 'object' && (v as any).accounts,
      );
      const {
        actions,
        scriptExecutionName: _legacyName,
        ...restLogged
      } = loggedState as any;
      expect(restLogged).toEqual(fullState);
    });

    test('✅ view specific account with name', async () => {
      // Arrange
      const program = new Command();
      commands.stateCommands(program);

      // Act
      await program.parse([
        'node',
        'hedera-cli.ts',
        'state',
        'view',
        '--account-name',
        alice.name,
      ]);

      // Assert
      expect(logSpy).toHaveBeenCalledWith('\nAccount:');
      expect(logSpy).toHaveBeenCalledWith(alice);
    });

    test('✅ view specific account with account ID', async () => {
      // Arrange
      const program = new Command();
      commands.stateCommands(program);

      // Act
      await program.parse([
        'node',
        'hedera-cli.ts',
        'state',
        'view',
        '--account-id',
        alice.accountId,
      ]);

      // Assert
      expect(logSpy).toHaveBeenCalledWith(`\nAccount ${alice.accountId}:`);
      expect(logSpy).toHaveBeenCalledWith(alice);
    });
  });
});
