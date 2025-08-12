import { testnetOperatorId, testnetOperatorKey } from '../helpers/state';
import { Command } from 'commander';
import commands from '../../src/commands';

const os = require('os');
const dotenv = require('dotenv');
import accountUtils from '../../src/utils/account';
import { saveState as storeSaveState, getState as storeGetAll } from '../../src/state/store';
import setupUtils from '../../src/utils/setup';

jest.mock('os');
jest.mock('dotenv');

describe('setup init command', () => {
  describe('setup init - success path', () => {
    let originalEnv: any;
    const logSpy = jest.spyOn(console, 'log');
  const setupOperatorAccountSpy = jest.spyOn(setupUtils, 'setupOperatorAccount');

    beforeEach(() => {
      // Save the original process.env
      originalEnv = { ...process.env };
    });

    afterEach(() => {
      // Reset process.env to its original state
      process.env = originalEnv;
  jest.clearAllMocks();
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
      // Assert we invoked setupOperatorAccount for testnet with expected args
      const calls = setupOperatorAccountSpy.mock.calls;
      const hasExpectedCall = calls.some(
        (c) => c[0] === testnetOperatorId && c[1] === testnetOperatorKey && c[2] === 'testnet',
      );
      expect(hasExpectedCall).toBe(true);

  const finalState = storeGetAll();
      const opAcct = finalState.accounts?.['testnet-operator'];
      expect(opAcct).toBeDefined();
      expect(opAcct).toMatchObject({
        accountId: testnetOperatorId,
        privateKey: testnetOperatorKey,
        network: 'testnet',
        name: 'testnet-operator',
        type: 'ECDSA',
      });
    });
  });
});
