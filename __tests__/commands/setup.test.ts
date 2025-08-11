import {
  baseState,
  testnetOperatorAccount,
  testnetOperatorId,
  testnetOperatorKey,
} from '../helpers/state';
import { Command } from 'commander';
import commands from '../../src/commands';

const os = require('os');
const dotenv = require('dotenv');
import accountUtils from '../../src/utils/account';
import stateController from '../../src/state/stateController';
import setupUtils from '../../src/utils/setup';

jest.mock('os');
jest.mock('dotenv');
jest.mock('../../src/state/state'); // Mock the original module -> looks for __mocks__/state.ts in same directory

describe('setup init command', () => {
  describe('setup init - success path', () => {
    let originalEnv: any;
    const logSpy = jest.spyOn(console, 'log');
    const setupOperatorAccountSpy = jest.spyOn(
      setupUtils,
      'setupOperatorAccount',
    );
    const saveStateControllerSpy = jest.spyOn(stateController, 'saveState');

    beforeEach(() => {
      // Save the original process.env
      originalEnv = { ...process.env };
    });

    afterEach(() => {
      // Reset process.env to its original state
      process.env = originalEnv;
      logSpy.mockClear();
      setupOperatorAccountSpy.mockClear();
      saveStateControllerSpy.mockClear();
    });

    test('✅ should set up state with environment variables with custom path', async () => {
      // Arrange
      const program = new Command();
      commands.setupCommands(program);

      // Set up mock environment variables
      process.env.TESTNET_OPERATOR_KEY = testnetOperatorKey;
      process.env.TESTNET_OPERATOR_ID = testnetOperatorId;

      const mockEnvPath = '/some/path/.env';
      dotenv.config.mockReturnValue({ error: null }); // Mock dotenv to succeed - if error path doesn't exist
      accountUtils.getAccountHbarBalance = jest
        .fn()
        .mockResolvedValue(1000000000); // Mock accountUtils to succeed
      accountUtils.getAccountHbarBalanceByNetwork = jest
        .fn()
        .mockResolvedValue(1000000000);

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
      expect(setupOperatorAccountSpy).toHaveBeenCalledTimes(1);
      expect(setupOperatorAccountSpy).toHaveBeenCalledWith(
        testnetOperatorId,
        testnetOperatorKey,
        'testnet',
      );
      expect(saveStateControllerSpy).toHaveBeenCalledWith({
        ...baseState,
        accounts: testnetOperatorAccount,
      });

      const updated = stateController.getAll();
      expect(updated.accounts['testnet-operator']).toMatchObject(
        testnetOperatorAccount['testnet-operator'],
      );
    });
  });
});
