import * as fs from 'fs';
import * as path from 'path';

import { baseState } from '../helpers/state';
import { program } from 'commander';
import commands from '../../src/commands';
import stateController from '../../src/state/stateController';
import api from '../../src/api';

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
describe('End to end: Flow 1', () => {
  beforeEach(() => {
    stateController.saveState(baseState); // reset state to base state for each test
  });

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
    const backupName = 'flow1';

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
    let files = fs.readdirSync(path.join(__dirname, '../..', 'src', 'state'));
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
    files = fs.readdirSync(path.join(__dirname, '../..', 'src', 'state'));
    const pattern = /^state\.backup\.[a-zA-Z0-9]+\.json$/;
    const backups = files.filter((file) => pattern.test(file));

    for (const backup of backups) {
      fs.unlinkSync(path.join(__dirname, '../..', 'src', 'state', backup));
    }
  });
});
