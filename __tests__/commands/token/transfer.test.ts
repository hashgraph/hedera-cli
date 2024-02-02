import { alice, tokenState } from '../../helpers/state';
import { Command } from 'commander';
import commands from '../../../src/commands';
import stateController from '../../../src/state/stateController';

import { TokenId } from '@hashgraph/sdk';
import { Token, Association } from '../../../types';

let tokenId = Object.keys(tokenState.tokens)[0];
jest.mock('../../../src/state/state'); // Mock the original module -> looks for __mocks__/state.ts in same directory
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
  const saveKeyStateControllerSpy = jest.spyOn(stateController, 'saveKey');

  beforeEach(() => {
    const tokenStateWithAlice = {
        ...tokenState,
        accounts: {
            [alice.alias]: alice,
        },
    };
    stateController.saveState(tokenStateWithAlice);
  });

  afterEach(() => {
    // Spy cleanup
    saveKeyStateControllerSpy.mockClear();
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
      const tokens = stateController.get('tokens');
      expect(tokens[tokenId].associations).toEqual([
        {
          alias: alice.alias,
          accountId: alice.accountId,
        },
      ]);
    });
  });
});
