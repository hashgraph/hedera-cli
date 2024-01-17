
import { baseState, scriptState, script_basic } from '../helpers/state';
import { Command } from 'commander';
import commands from '../../src/commands';

const os = require('os');
const dotenv = require('dotenv');
import accountUtils from '../../src/utils/account';
import stateController from '../../src/state/stateController';
import setupUtils from '../../src/utils/setup';

jest.mock('os');
jest.mock('dotenv');
jest.mock('../../src/utils/account');
jest.mock('../../src/state/state'); // Mock the original module -> looks for __mocks__/state.ts in same directory

describe('setup init command', () => {
  describe('setup init - success path', () => {
    let originalEnv: any;
    const logSpy = jest.spyOn(console, 'log');
    const setupOperatorAccountsSpy = jest.spyOn(setupUtils, 'setupOperatorAccounts');
    const saveStateControllerSpy = jest.spyOn(stateController, 'saveState');

    beforeEach(() => {
      // Save the original process.env
      originalEnv = { ...process.env };
    });

    afterEach(() => {
      // Reset process.env to its original state
      process.env = originalEnv;
      logSpy.mockClear();
      setupOperatorAccountsSpy.mockClear();
      saveStateControllerSpy.mockClear();
    });

    test('✅ should set up state with environment variables with custom path', async () => {
      // Arrange
      const program = new Command();
      commands.setupCommands(program);

      // Set up mock environment variables
      const testnetOperatorKey = 'mockTestnetOperatorKey';
      const testnetOperatorId = 'mockTestnetOperatorId';
      process.env.TESTNET_OPERATOR_KEY = testnetOperatorKey;
      process.env.TESTNET_OPERATOR_ID = testnetOperatorId;
      
      const mockEnvPath = '/some/path/.env';
      dotenv.config.mockReturnValue({ error: null }); // Mock dotenv to succeed - if error path doesn't exist
      accountUtils.getAccountHbarBalance = jest.fn().mockResolvedValue(1000000000); // Mock accountUtils to succeed

      // Act
      await program.parseAsync([
        'node',
        'hedera-cli.ts',
        'setup',
        'init',
        '--path',
        mockEnvPath,
      ]);

      // Assert
      expect(dotenv.config).toHaveBeenCalledWith({ path: mockEnvPath });
      expect(setupOperatorAccountsSpy).toHaveBeenCalledWith(testnetOperatorId, testnetOperatorKey, '', '', '', '');
      expect(saveStateControllerSpy).toHaveBeenCalledWith({
        ...baseState,
        testnetOperatorId,
        testnetOperatorKey,
      });
    });
  });
});
