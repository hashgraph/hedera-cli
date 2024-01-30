import * as fs from 'fs';
import * as path from 'path';

import { baseState } from './helpers/state';
import { program } from 'commander';
import commands from '../src/commands';
import stateController from '../src/state/stateController';
import api from '../src/api';

import { Token } from '../types';

describe('End to end tests', () => {
  beforeEach(() => {
    stateController.saveState(baseState); // reset state to base state for each test
  });

  afterEach(async () => {
    // Reset state to base state
    const distStatePath = path.join(
      __dirname,
      '..',
      'dist',
      'state',
      'state.json',
    );
    await fs.writeFileSync(
      distStatePath,
      JSON.stringify(baseState, null, 2),
      'utf8',
    );
  });

  afterAll(() => {
    stateController.saveState(baseState);
  });

  /**
   * E2E testing flow:
   * - Setup init
   * - Switch to testnet
   * - Create a new account with specific balance, type, and network and verify it is created
   * - Transfer part of the balance back to the operator account and verify the balance is correct
   * - Create a backup of the state file and verify it is created
   * - Delete the account and verify it is deleted
   * - Restore the state file from backup and verify the account and operator details are restored
   */
  test('✅ Flow 1', async () => {
    // Arrange: Setup init
    commands.setupCommands(program);

    // Act
    await program.parseAsync(['node', 'hedera-cli.ts', 'setup', 'init']);

    // Assert
    const accounts = stateController.get('accounts');
    expect(accounts['testnet-operator']).toBeDefined();

    // Arrange: Change network to testnet
    commands.networkCommands(program);

    // Act
    program.parse(['node', 'hedera-cli.ts', 'network', 'use', 'testnet']);

    // Assert
    const network = stateController.get('network');
    expect(network).toEqual('testnet');

    // Arrange: Create a new account with specific balance, type, and network and verify it is created
    commands.accountCommands(program);
    commands.waitCommands(program);
    const accountAlias = 'greg';

    // Act
    await program.parseAsync([
      'node',
      'hedera-cli.ts',
      'account',
      'create',
      '-a',
      accountAlias,
      '-t',
      'ECdsA',
      '--auto-associations',
      '1',
    ]);
    await new Promise((resolve) => setTimeout(resolve, 5000));

    // Assert
    let state = stateController.get('accounts');
    expect(state[accountAlias]).toBeDefined();
    expect(state[accountAlias].type).toEqual('ECDSA');

    let data = await api.account.getAccountInfo(state[accountAlias].accountId);
    expect(data.data.balance.balance).toEqual(10000); // default value if no balance is specified

    // Arrange: Transfer part of the balance back to the operator account and verify the balance is correct
    commands.hbarCommands(program);
    const transferAmount = 0.00001;

    // Act
    await program.parseAsync([
      'node',
      'hedera-cli.ts',
      'hbar',
      'transfer',
      '-b',
      transferAmount.toString(),
      '-f',
      accountAlias,
      '-t',
      'testnet-operator',
    ]);
    await new Promise((resolve) => setTimeout(resolve, 5000));

    // Assert
    data = await api.account.getAccountInfo(state[accountAlias].accountId);
    expect(data.data.balance.balance).toEqual(9000);

    // Arrange: Create a backup of the state file and verify it is created
    commands.backupCommands(program);
    const backupName = 'e2e';

    // Act
    program.parse([
      'node',
      'hedera-cli.ts',
      'backup',
      'create',
      '--name',
      backupName,
    ]);

    // Assert
    let files = fs.readdirSync(path.join(__dirname, '..', 'src', 'state'));
    expect(files).toContain(`state.backup.${backupName}.json`);

    // Arrange: Delete the account and verify it is deleted in state
    commands.accountCommands(program);

    // Act
    program.parse([
      'node',
      'hedera-cli.ts',
      'account',
      'delete',
      '-a',
      accountAlias,
    ]);

    // Assert
    state = stateController.get('accounts');
    expect(state[accountAlias]).toBeUndefined();

    // Arrange: Restore the state file from backup and verify the account and operator details are restored
    commands.backupCommands(program);

    // Act
    program.parse([
      'node',
      'hedera-cli.ts',
      'backup',
      'restore',
      '-f',
      `state.backup.${backupName}.json`,
    ]);

    // Assert
    state = stateController.get('accounts');
    expect(state[accountAlias]).toBeDefined();

    // Cleanup
    files = fs.readdirSync(path.join(__dirname, '..', 'src', 'state'));
    const pattern = /^state\.backup\.[a-zA-Z0-9]+\.json$/;
    const backups = files.filter((file) => pattern.test(file));

    for (const backup of backups) {
      fs.unlinkSync(path.join(__dirname, '..', 'src', 'state', backup));
    }
  });

  /**
   * E2E testing flow for scripts:
   * - Download a script from the internet
   * - Load and execute the script (list all scripts and spy on logger function)
   * - Delete script and verify it is deleted in state file
   */
  test('✅ Script features', async () => {
    // Arrange: Setup init
    commands.setupCommands(program);

    // Act
    await program.parseAsync(['node', 'hedera-cli.ts', 'setup', 'init']);

    // Assert
    let accounts = stateController.get('accounts');
    expect(accounts['testnet-operator']).toBeDefined();

    // Arrange: Download a script from the internet
    commands.stateCommands(program);
    const scriptURL =
      'https://raw.githubusercontent.com/hashgraph/hedera-cli/main/src/commands/script/examples.json';

    // Act
    await program.parseAsync([
      'node',
      'hedera-cli.ts',
      'state',
      'download',
      '--url',
      scriptURL,
    ]);

    // Assert
    let scripts = stateController.get('scripts');
    expect(scripts['script-token']).toBeDefined();
    expect(scripts['script-account-create']).toBeDefined();

    // Arrange: Load and execute the script (list all scripts and spy on logger function)
    commands.scriptCommands(program);

    // Act
    await program.parseAsync([
      'node',
      'hedera-cli.ts',
      'script',
      'load',
      '-n',
      'account-create-simple',
    ]);

    // Assert
    const distStatePath = path.join(
      __dirname,
      '..',
      'dist',
      'state',
      'state.json',
    ); // read state from dist/state/state.json because script load uses dist/ logic
    const distState = await fs.readFileSync(distStatePath, 'utf8');
    expect(Object.keys(JSON.parse(distState).accounts).length).toBe(1); // 1 random account created

    // Reset state to base state
    await fs.writeFileSync(
      distStatePath,
      JSON.stringify(baseState, null, 2),
      'utf8',
    );

    // Arrange: Delete script and verify it is deleted in state file
    // Act
    program.parse([
      'node',
      'hedera-cli.ts',
      'script',
      'delete',
      '-n',
      'account-create-simple',
    ]);

    // Assert
    scripts = stateController.get('scripts');
    expect(scripts['script-account-create-simple']).toBeUndefined();
    expect(scripts['script-account-create']).toBeDefined();
  });

  /**
   * E2E testing flow for tokens:
   * - Create 3 accounts
   * - Create a token (account 1 is the treasury and account 2 is the admin key)
   * - Associate the token with account 3
   * - Transfer 1 unit of token from treasury to account 3
   * - Verify balance by looking up the token balance of account 3 via API
   */
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
    await program.parseAsync([
      'node',
      'hedera-cli.ts',
      'account',
      'create',
      '-a',
      accountAliasTreasury,
      '-b',
      '300000000',
    ]);
    await program.parseAsync([
      'node',
      'hedera-cli.ts',
      'account',
      'create',
      '-a',
      accountAliasAdmin,
      '-b',
      '300000000',
    ]);
    await program.parseAsync([
      'node',
      'hedera-cli.ts',
      'account',
      'create',
      '-a',
      accountAliasUser,
      '-b',
      '300000000',
    ]);

    // Assert
    accounts = stateController.get('accounts');
    expect(accounts[accountAliasTreasury]).toBeDefined();
    expect(accounts[accountAliasAdmin]).toBeDefined();
    expect(accounts[accountAliasUser]).toBeDefined();

    // Arrange: Create a token (account 1 is the treasury and account 2 is the admin key)
    commands.tokenCommands(program);
    const tokenName = 'test-token';

    // Act
    await program.parseAsync([
      'node',
      'hedera-cli.ts',
      'token',
      'create',
      '-a',
      accounts[accountAliasAdmin].privateKey,
      '-t',
      accounts[accountAliasTreasury].accountId,
      '-k',
      accounts[accountAliasTreasury].privateKey,
      '-n',
      tokenName,
      '-s',
      'TT',
      '-i',
      '1000',
      '-d',
      '2',
      '--supply-type',
      'infinite',
    ]);

    // Assert
    let tokens = stateController.get('tokens') as Token[];
    let token = Object.values(tokens).find(
      (token: Token) => token.name === tokenName,
    );
    expect(token).toBeDefined();

    // TypeScript still sees `token` as possibly undefined. You need to assert it's not.
    if (!token) {
      throw new Error('Token not found');
    }

    // Arrange: Associate the token with account 3 (user)
    // Act
    await program.parseAsync([
      'node',
      'hedera-cli.ts',
      'token',
      'associate',
      '--account-id',
      accounts[accountAliasUser].accountId,
      '-t',
      token.tokenId,
    ]);

    // Assert
    tokens = stateController.get('tokens') as Token[];
    token = Object.values(tokens).find(
      (token: Token) => token.name === tokenName,
    );
    expect(token?.associations).toEqual([
      {
        accountId: accounts[accountAliasUser].accountId,
        alias: accountAliasUser,
      },
    ]);

    if (!token) {
      throw new Error('Token not found');
    }

    // Arrange: Transfer 1 unit of token from treasury to account 3 (user)
    // Act
    await program.parseAsync([
      'node',
      'hedera-cli.ts',
      'token',
      'transfer',
      '-t',
      token.tokenId,
      '-b',
      '1',
      '--from',
      accountAliasTreasury,
      '--to',
      accountAliasUser,
    ]);
    await new Promise((resolve) => setTimeout(resolve, 5000));

    // Assert
    const data = await api.token.getTokenBalance(
      token.tokenId,
      accounts[accountAliasUser].accountId,
    );
    expect(data.data.balances).toEqual([
      { account: accounts[accountAliasUser].accountId, balance: 1 },
    ]);
  });
});
