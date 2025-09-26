import { Command } from 'commander';
import commands from '../../../src/commands';
import {
  get as storeGet,
  saveState as storeSaveState,
} from '../../../src/state/store';
import { alice, tokenState } from '../../helpers/state';

const tokenId = Object.keys(tokenState.tokens)[0];
jest.mock('@hashgraph/sdk', () => {
  const originalModule = jest.requireActual('@hashgraph/sdk');

  return {
    ...originalModule,
    TokenAssociateTransaction: jest.fn().mockImplementation(() => ({
      setAccountId: jest.fn().mockReturnThis(),
      setTokenIds: jest.fn().mockReturnThis(),
      sign: jest.fn().mockReturnThis(),
      freezeWith: jest.fn().mockReturnThis(),
      execute: jest.fn().mockResolvedValue({
        getReceipt: jest.fn().mockResolvedValue({}),
      }),
    })),
  };
});

describe('token associate command', () => {
  beforeEach(() => {
    const tokenStateWithAlice = {
      ...tokenState,
      accounts: {
        [alice.name]: alice,
      },
    };
    storeSaveState(tokenStateWithAlice as any);
  });

  describe('token associate - success path', () => {
    test('✅ ', async () => {
      // Arrange
      const program = new Command();
      commands.tokenCommands(program);

      // Act
      await program.parseAsync([
        'node',
        'hedera-cli.ts',
        'token',
        'associate',
        '-a',
        alice.accountId,
        '-t',
        tokenId,
      ]);

      // Assert
      const tokens = storeGet('tokens' as any);
      expect(tokens[tokenId].associations).toEqual([
        {
          name: alice.name,
          accountId: alice.accountId,
        },
      ]);
    });
  });
});
