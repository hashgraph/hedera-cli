import {
  baseState,
  fullState,
  downloadState,
  script_basic,
  accountState,
} from '../../helpers/state';
import { Command } from 'commander';
import commands from '../../../src/commands';
import stateController from '../../../src/state/stateController';
import stateUtils from '../../../src/utils/state';

jest.mock('../../../src/state/state'); // Mock the original module -> looks for __mocks__/state.ts in same directory

describe('state download command', () => {
  const stateUtilsDownloadStateSpy = jest
    .spyOn(stateUtils, 'downloadState')
    .mockResolvedValue(downloadState);

  const consoleErrorSpy = jest.spyOn(console, 'error');

  const mockProcessExit = jest.spyOn(process, 'exit').mockImplementation(((code) => { 
    throw new Error(`Process.exit(${code})`); // Forces the code to throw instead of exit
  }));

  describe('state download - success path', () => {
    beforeAll(() => {
      stateController.saveState(baseState);
    });

    afterEach(() => {
      // Spy cleanup
      stateUtilsDownloadStateSpy.mockClear();
      consoleErrorSpy.mockClear();
      mockProcessExit.mockClear();
    });

    test('✅ download state and merge with base state', async () => {
      // Arrange
      stateController.saveState(baseState);
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
      expect(stateController.getAll()).toEqual({
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
      stateController.saveState(accountState);
      const program = new Command();
      commands.stateCommands(program);
      const url = 'https://dummy.url/state.json';

      // Act
      try {
        await program.parseAsync([
            'node',
            'hedera-cli.ts',
            'state',
            'download',
            '--url',
            url,
            '--overwrite',
          ]);
      } catch (error) {
        expect(error).toEqual(Error(`Process.exit(0)`));
      }

      // Assert
      expect(stateUtilsDownloadStateSpy).toHaveBeenCalledWith(url);
      expect(stateController.getAll()).toEqual({
        ...fullState,
        scripts: {
          [`script-${script_basic.name}`]: {
            ...script_basic,
            creation: expect.any(Number),
          },
        },
      });
      expect(consoleErrorSpy).not.toHaveBeenCalled(); // because we are overwriting the state
    });
  });
});
