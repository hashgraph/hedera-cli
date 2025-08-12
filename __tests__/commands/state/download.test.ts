import {
  baseState,
  fullState,
  downloadState,
  script_basic,
  accountState,
} from '../../helpers/state';
import { Command } from 'commander';
import commands from '../../../src/commands';
import {
  saveState as storeSaveState,
  getState as storeGetAll,
} from '../../../src/state/store';
import stateUtils from '../../../src/utils/state';

describe('state download command', () => {
  const stateUtilsDownloadStateSpy = jest
    .spyOn(stateUtils, 'downloadState')
    .mockResolvedValue(downloadState);

  const consoleErrorSpy = jest.spyOn(console, 'error');

  const mockProcessExit = jest
    .spyOn(process, 'exit')
    .mockImplementation((code) => {
      throw new Error(`Process.exit(${code})`); // Forces the code to throw instead of exit
    });

  describe('state download - success path', () => {
    beforeAll(() => {
      storeSaveState(baseState as any);
    });

    afterEach(() => {
      // Spy cleanup
      stateUtilsDownloadStateSpy.mockClear();
      consoleErrorSpy.mockClear();
      mockProcessExit.mockClear();
    });

    test('✅ download state and merge with base state', async () => {
      // Arrange
      storeSaveState(baseState as any);
      const program = new Command();
      commands.stateCommands(program);
      const url = 'https://dummy.url/state.json';

      // Act
      await program.parseAsync([
        'node',
        'hedera-cli.ts',
        'state',
        'download',
        '--url',
        url,
        '--merge',
      ]);

      // Assert
      expect(stateUtilsDownloadStateSpy).toHaveBeenCalledWith(url);
      const {
        actions: _a1,
        scriptExecutionName: _legacyName1,
        ...stateAfterMerge
      } = storeGetAll() as any;
      expect(stateAfterMerge).toEqual({
        ...fullState,
        scripts: {
          'script-basic': {
            ...script_basic,
            creation: expect.any(Number),
          },
        },
      });
      expect(consoleErrorSpy).not.toHaveBeenCalled();
    });

    test('✅ download and overwrite state', async () => {
      // Arrange
      storeSaveState(accountState as any);
      const program = new Command();
      commands.stateCommands(program);
      const url = 'https://dummy.url/state.json';

      // Act
      await program.parseAsync([
        'node',
        'hedera-cli.ts',
        'state',
        'download',
        '--url',
        url,
        '--overwrite',
      ]);

      // Assert
      expect(stateUtilsDownloadStateSpy).toHaveBeenCalledWith(url);
      const {
        actions: _a2,
        scriptExecutionName: _legacyName2,
        ...stateAfterOverwrite
      } = storeGetAll() as any;
      expect(stateAfterOverwrite).toEqual({
        ...fullState,
        scripts: {
          [`script-${script_basic.name}`]: {
            ...script_basic,
            creation: expect.any(Number),
          },
        },
      });
      expect(consoleErrorSpy).not.toHaveBeenCalled(); // overwrite should be silent on errors
    });
  });
});
