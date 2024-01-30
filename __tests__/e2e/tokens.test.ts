import * as fs from 'fs';
import * as path from 'path';

import { baseState } from '../helpers/state';
import { program } from 'commander';
import commands from '../../src/commands';
import stateController from '../../src/state/stateController';
import { Token } from '../../types';
import api from '../../src/api';

/**
 * E2E testing flow for tokens:
 * - Create 3 accounts
 * - Create a token (account 1 is the treasury and account 2 is the admin key)
 * - Associate the token with account 3
 * - Transfer 1 unit of token from treasury to account 3
 * - Verify balance by looking up the token balance of account 3 via API
 */
describe('End to end: Token features', () => {
  beforeEach(() => {
    stateController.saveState(baseState); // reset state to base state for each test
  });

  test('✅ Token features', async () => {
    // Arrange: Setup init
    commands.setupCommands(program);

    // Act
    await program.parseAsync(['node', 'hedera-cli.ts', 'setup', 'init']);

    // Assert
    let accounts = stateController.get('accounts');
    expect(accounts['testnet-operator']).toBeDefined();

    // Arrange: Create 3 accounts
    commands.accountCommands(program);
    const accountAliasTreasury = 'treasury';
    const accountAliasAdmin = 'admin';
    const accountAliasUser = 'user';

    // Act
    await program.parseAsync(['node', 'hedera-cli.ts', 'account', 'create', '-a', accountAliasTreasury, '-b', '300000000']);
    await program.parseAsync(['node', 'hedera-cli.ts', 'account', 'create', '-a', accountAliasAdmin, '-b', '300000000']);
    await program.parseAsync(['node', 'hedera-cli.ts', 'account', 'create', '-a', accountAliasUser, '-b', '300000000']);

    // Assert
    accounts = stateController.get('accounts');
    expect(accounts[accountAliasTreasury]).toBeDefined();
    expect(accounts[accountAliasAdmin]).toBeDefined();
    expect(accounts[accountAliasUser]).toBeDefined();

    // Arrange: Create a token (account 1 is the treasury and account 2 is the admin key)
    commands.tokenCommands(program);
    const tokenName = 'test-token';

    // Act
    await program.parseAsync(['node', 'hedera-cli.ts', 'token', 'create', '-a', accounts[accountAliasAdmin].privateKey, '-t', accounts[accountAliasTreasury].accountId, '-k', accounts[accountAliasTreasury].privateKey, '-n', tokenName, '-s', 'TT', '-i', '1000', '-d', '2', '--supply-type', 'infinite']);

    // Assert
    let tokens = stateController.get('tokens') as Token[];
    let token = Object.values(tokens).find((token: Token) => token.name === tokenName);
    expect(token).toBeDefined();


    // TypeScript still sees `token` as possibly undefined. You need to assert it's not.
    if (!token) {
      throw new Error("Token not found");
    }

    // Arrange: Associate the token with account 3 (user)
    // Act
    await program.parseAsync(['node', 'hedera-cli.ts', 'token', 'associate', '--account-id', accounts[accountAliasUser].accountId, '-t', token.tokenId]);

    // Assert
    tokens = stateController.get('tokens') as Token[];
    token = Object.values(tokens).find((token: Token) => token.name === tokenName);
    expect(token?.associations).toEqual([{ accountId: accounts[accountAliasUser].accountId, alias: accountAliasUser }]);

    if (!token) {
      throw new Error("Token not found");
    }

    // Arrange: Transfer 1 unit of token from treasury to account 3 (user)
    // Act
    await program.parseAsync(['node', 'hedera-cli.ts', 'token', 'transfer', '-t', token.tokenId, '-b', '1', '--from', accountAliasTreasury, '--to', accountAliasUser]);
    await new Promise((resolve) => setTimeout(resolve, 5000));

    // Assert
    const data = await api.token.getTokenBalance(token.tokenId, accounts[accountAliasUser].accountId);
    expect(data.data.balances).toEqual([{ account: accounts[accountAliasUser].accountId, balance: 1 }]);
  });
});
