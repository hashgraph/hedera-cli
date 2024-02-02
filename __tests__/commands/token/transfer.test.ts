import { alice, bob, tokenState } from '../../helpers/state';
import { Command } from 'commander';
import commands from '../../../src/commands';
import stateController from '../../../src/state/stateController';
import { TransactionId } from '@hashgraph/sdk';
import { Logger } from "../../../src/utils/logger";

const logger = Logger.getInstance();

let tokenId = Object.keys(tokenState.tokens)[0];
const txId = "0.0.14288@1706880903.830877722";
jest.mock('../../../src/state/state'); // Mock the original module -> looks for __mocks__/state.ts in same directory
jest.mock('@hashgraph/sdk', () => {
  const originalModule = jest.requireActual('@hashgraph/sdk');

  return {
    ...originalModule,
    TransferTransaction: jest.fn().mockImplementation(() => ({
      addTokenTransfer: jest.fn().mockReturnThis(),
      sign: jest.fn().mockReturnThis(),
      freezeWith: jest.fn().mockReturnThis(),
      execute: jest.fn().mockResolvedValue({
        transactionId: TransactionId.fromString(txId),
        getReceipt: jest.fn().mockResolvedValue({
            status: {
                _code: 22,
                message: 'Success',
            },
            
        }),
      }),
    })),
  };
});

describe('token transfer command', () => {
  const logSpy = jest.spyOn(logger, 'log');

  beforeEach(() => {
    const tokenStateWithAlice = {
        ...tokenState,
        accounts: {
            [alice.alias]: alice,
            [bob.alias]: bob,
        },
    };
    stateController.saveState(tokenStateWithAlice);
  });

  afterEach(() => {
    // Spy cleanup
    logSpy.mockClear();
  });

  describe('token transfer - success path', () => {
    test('✅ ', async () => {
      // Arrange
      const program = new Command();
      commands.tokenCommands(program);
      const balance = 10;

      // Act
      await program.parseAsync([
        'node',
        'hedera-cli.ts',
        'token',
        'transfer',
        '-t',
        tokenId,
        '--to',
        bob.alias,
        '--from',
        alice.alias,
        '-b',
        balance.toString()
      ]);

      // Assert
      expect(logSpy).toHaveBeenCalledWith(`Transfer successful with tx ID: ${txId}`);
    });
  });
});
