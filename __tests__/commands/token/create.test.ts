import { alice, bob, baseState } from '../../helpers/state';
import { Command } from 'commander';
import commands from '../../../src/commands';
import { saveState as storeSaveState, get as storeGet } from '../../../src/state/store';
import * as mutations from '../../../src/state/mutations';

import { TokenId } from '@hashgraph/sdk';
import { Token } from '../../../types';

let tokenId = '0.0.1234';
jest.mock('../../../src/state/state'); // Mock the original module -> looks for __mocks__/state.ts in same directory
jest.mock('@hashgraph/sdk', () => {
  const originalModule = jest.requireActual('@hashgraph/sdk');

  return {
    ...originalModule,
    TokenCreateTransaction: jest.fn().mockImplementation(() => ({
      setTokenName: jest.fn().mockReturnThis(),
      setTokenSymbol: jest.fn().mockReturnThis(),
      setDecimals: jest.fn().mockReturnThis(),
      setInitialSupply: jest.fn().mockReturnThis(),
      setTokenType: jest.fn().mockReturnThis(),
      setSupplyType: jest.fn().mockReturnThis(),
      setTreasuryAccountId: jest.fn().mockReturnThis(),
      setAdminKey: jest.fn().mockReturnThis(),
      sign: jest.fn().mockReturnThis(),
      freezeWith: jest.fn().mockReturnThis(),
      execute: jest.fn().mockResolvedValue({
        getReceipt: jest.fn().mockResolvedValue({
          tokenId: TokenId.fromString(tokenId),
        }),
      }),
    })),
  };
});

describe('token create command', () => {
  const mockProcessExit = jest
    .spyOn(process, 'exit')
    .mockImplementation((code) => {
      throw new Error(`Process.exit(${code})`); // Forces the code to throw instead of exit
    });

  const addTokenSpy = jest.spyOn(mutations, 'addToken');

  beforeEach(() => {
  storeSaveState(baseState as any);
  });

  afterEach(() => {
    // Spy cleanup
    mockProcessExit.mockClear();
  addTokenSpy.mockClear();
  });

  describe('token create - success path', () => {
    test('✅ ', async () => {
      // Arrange
      const program = new Command();
      commands.tokenCommands(program);
      const tokenName = 'test-token';
      const tokenSymbol = 'TST';
      const tokenSupplyType = 'infinite';
      const totalSupply = 1000;
      const decimals = 2;

      // Act
      await program.parseAsync([
        'node',
        'hedera-cli.ts',
        'token',
        'create',
        '-t',
        alice.accountId,
        '-k',
        alice.privateKey,
        '-n',
        tokenName,
        '-s',
        tokenSymbol,
        '-d',
        decimals.toString(),
        '-i',
        totalSupply.toString(),
        '--supply-type',
        tokenSupplyType,
        '-a',
        bob.privateKey,
      ]);

      // Assert
  const tokens = storeGet('tokens' as any);
      expect(Object.keys(tokens).length).toEqual(1);
      expect(tokens[tokenId]).toEqual({
        tokenId: tokenId,
        name: tokenName,
        symbol: tokenSymbol,
        decimals: decimals,
        initialSupply: totalSupply,
        supplyType: tokenSupplyType.toUpperCase(),
        treasuryId: alice.accountId,
        associations: [],
        maxSupply: tokenSupplyType.toUpperCase() === 'FINITE' ? totalSupply : 0,
        keys: {
          treasuryKey: alice.privateKey,
          adminKey: bob.privateKey,
          supplyKey: '',
          wipeKey: '',
          kycKey: '',
          freezeKey: '',
          pauseKey: '',
          feeScheduleKey: '',
        },
        network: 'localnet',
        customFees: [],
      } as Token);
  expect(addTokenSpy).toHaveBeenCalled();
    });
  });
});
