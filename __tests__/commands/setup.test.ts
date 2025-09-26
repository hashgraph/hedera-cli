import { Command } from 'commander';
import * as dotenv from 'dotenv';
import commands from '../../src/commands';
import { getState as storeGetAll } from '../../src/state/store';
import accountUtils from '../../src/utils/account';
import setupUtils from '../../src/utils/setup';
import { testnetOperatorId, testnetOperatorKey } from '../helpers/state';

jest.mock('dotenv', () => ({
  __esModule: true,
  config: jest.fn().mockReturnValue({ error: null }),
}));

describe('setup init command', () => {
  describe('setup init - success path', () => {
    let originalEnv: any;
    const setupOperatorAccountSpy = jest.spyOn(
      setupUtils,
      'setupOperatorAccount',
    );

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
      // ensure dotenv.config mock returns success (already set in jest.mock but reaffirm for clarity)
      (dotenv.config as unknown as jest.Mock).mockReturnValue({ error: null });
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
        (c) =>
          c[0] === testnetOperatorId &&
          c[1] === testnetOperatorKey &&
          c[2] === 'testnet',
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
