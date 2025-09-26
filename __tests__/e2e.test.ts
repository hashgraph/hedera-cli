import { Command } from 'commander';
import * as fs from 'fs';
import * as path from 'path';
import api from '../src/api';
import commands from '../src/commands';
import {
  get as storeGet,
  saveState as storeSaveState,
  type StoreState,
} from '../src/state/store';
import { Logger } from '../src/utils/logger';
import type { Token } from '../types';
import { initLocalnetFlag, localnetTest } from './helpers/localnet';
import { waitFor } from './helpers/poll';
import { baseState } from './helpers/state';

const logger = Logger.getInstance();

describe('End to end tests', () => {
  const logSpy = jest.spyOn(logger, 'log');
  beforeAll(async () => {
    // Debug: print active network configuration for visibility in e2e runs
    const activeNetwork = storeGet('network');
    const networks = storeGet('networks') as Record<
      string,
      { rpcUrl?: string; mirrorNodeUrl?: string }
    >;
    const cfg = networks[activeNetwork] || {};
    const localNodeAddress = storeGet('localNodeAddress');
    const localNodeAccountId = storeGet('localNodeAccountId');
    const localNodeMirrorAddressGRPC = storeGet('localNodeMirrorAddressGRPC');
    // eslint-disable-next-line no-console
    console.warn(
      `[e2e] Using network="${activeNetwork}" rpcUrl=${cfg.rpcUrl} mirrorNodeUrl=${cfg.mirrorNodeUrl} localNode=${localNodeAddress}(${localNodeAccountId}) mirrorGrpc=${localNodeMirrorAddressGRPC} configFile=${process.env.HCLI_CONFIG_FILE}`,
    );

    await initLocalnetFlag();
  });

  beforeEach(() => {
    storeSaveState(baseState as Partial<StoreState>);
  });

  afterEach(() => {
    const distStatePath = path.join(
      __dirname,
      '..',
      'dist',
      'state',
      'state.json',
    );
    fs.writeFileSync(distStatePath, JSON.stringify(baseState, null, 2), 'utf8');
  });

  afterAll(() => {
    storeSaveState(baseState as Partial<StoreState>);
    logSpy.mockClear();
  });

  /**
   * E2E testing flow:
   * - Setup init
   * - Switch to localnet
   * - Create a new account with specific balance, type, and network and verify it is created
   * - Transfer part of the balance back to the operator account and verify the balance is correct
   * - Create a backup of the state file and verify it is created
   * - Delete the account and verify it is deleted
   * - Restore the state file from backup and verify the account and operator details are restored
   */
  // @TODO Temporary omission of the test due to future reconstruction
  test.skip('✅ Flow 1', async () => {
    const program = new Command();
    // Extend timeout for this long flow (was formerly passed as 3rd arg)
    jest.setTimeout(45000);
    // Arrange: Setup init
    commands.setupCommands(program);

    // Act
    await program.parseAsync(['node', 'hedera-cli.ts', 'setup', 'init']);

    // Assert
    const accounts = storeGet('accounts');
    expect(accounts['localnet-operator']).toBeDefined();

    // Arrange: Change network to localnet
    commands.networkCommands(program);

    // Act
    await program.parseAsync([
      'node',
      'hedera-cli.ts',
      'network',
      'use',
      'localnet',
    ]);

    // Assert
    const network = storeGet('network');
    expect(network).toEqual('localnet');

    // Arrange: Create a new account with specific balance, type, and network and verify it is created
    commands.accountCommands(program);
    commands.waitCommands(program);
    const accountName = 'greg';

    // Act
    await program.parseAsync([
      'node',
      'hedera-cli.ts',
      'account',
      'create',
      '-n',
      accountName,
      '--auto-associations',
      '1',
    ]);
    // Capture state AFTER create before polling so we have accountId available
    let state = storeGet('accounts');
    // Wait for account to appear via API (faster than fixed 7s sleep)
    await waitFor(
      async () => {
        try {
          const info = await api.account.getAccountInfo(
            state[accountName].accountId,
          );
          return info?.data?.balance?.balance === 10000;
        } catch {
          return false;
        }
      },
      {
        timeout: 10000,
        interval: 500,
        description: 'new account ledger propagation',
      },
    );

    // Assert
    expect(state[accountName]).toBeDefined();
    expect(state[accountName].type).toEqual('ECDSA');

    let data = await api.account.getAccountInfo(state[accountName].accountId);
    expect(data.data.balance.balance).toEqual(10000); // default value if no balance is specified

    // Arrange: Transfer part of the balance back to the operator account and verify the balance is correct
    commands.hbarCommands(program);
    const transferAmount = 1000;

    // Act
    await program.parseAsync([
      'node',
      'hedera-cli.ts',
      'hbar',
      'transfer',
      '-b',
      transferAmount.toString(),
      '-f',
      accountName,
      '-t',
      'localnet-operator',
    ]);
    await waitFor(
      async () => {
        try {
          const info = await api.account.getAccountInfo(
            state[accountName].accountId,
          );
          return info?.data?.balance?.balance === 9000 ? true : false;
        } catch {
          return false;
        }
      },
      {
        timeout: 10000,
        interval: 500,
        description: 'post-transfer balance update',
      },
    );

    // Assert
    data = await api.account.getAccountInfo(state[accountName].accountId);
    expect(data.data.balance.balance).toEqual(9000);

    // Arrange: Create a backup of the state file and verify it is created
    commands.backupCommands(program);
    const backupName = 'e2e';

    // Act
    await program.parseAsync([
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
    await program.parseAsync([
      'node',
      'hedera-cli.ts',
      'account',
      'delete',
      '-n',
      accountName,
    ]);

    // Assert
    state = storeGet('accounts');
    expect(state[accountName]).toBeUndefined();

    // Arrange: Restore the state file from backup and verify the account and operator details are restored
    commands.backupCommands(program);

    // Act
    await program.parseAsync([
      'node',
      'hedera-cli.ts',
      'backup',
      'restore',
      '-f',
      `state.backup.${backupName}.json`,
    ]);

    // Assert
    state = storeGet('accounts');
    expect(state[accountName]).toBeDefined();

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
  localnetTest('✅ Script features', async () => {
    const program = new Command();
    // Arrange: Download a script from the internet
    commands.stateCommands(program);
    const scriptURL =
      'https://raw.githubusercontent.com/hashgraph/hedera-cli/78e4e0dd4eb1a6d9e0894ae970bf6706142e0252/src/commands/script/examples.json';

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
    let scripts = storeGet('scripts');
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
    const distState = fs.readFileSync(distStatePath, 'utf8');
    const parsed = JSON.parse(distState) as {
      accounts: Record<string, unknown>;
    };
    expect(Object.keys(parsed.accounts).length).toBe(1); // 1 random account created

    // Reset state to base state
    fs.writeFileSync(distStatePath, JSON.stringify(baseState, null, 2), 'utf8');

    // Arrange: Delete script and verify it is deleted in state file
    // Act
    await program.parseAsync([
      'node',
      'hedera-cli.ts',
      'script',
      'delete',
      '-n',
      'account-create-simple',
    ]);

    // Assert
    scripts = storeGet('scripts');
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
  localnetTest('✅ Token features', async () => {
    const program = new Command();
    // Arrange: Create 3 accounts
    commands.accountCommands(program);
    const accountNameTreasury = 'treasury';
    const accountNameAdmin = 'admin';
    const accountNameUser = 'user';

    // Act
    await program.parseAsync([
      'node',
      'hedera-cli.ts',
      'account',
      'create',
      '-n',
      accountNameTreasury,
      '-b',
      '300000000',
    ]);
    await program.parseAsync([
      'node',
      'hedera-cli.ts',
      'account',
      'create',
      '-n',
      accountNameAdmin,
      '-b',
      '300000000',
    ]);
    await program.parseAsync([
      'node',
      'hedera-cli.ts',
      'account',
      'create',
      '-n',
      accountNameUser,
      '-b',
      '300000000',
    ]);

    // Assert
    const accounts = storeGet('accounts');
    expect(accounts[accountNameTreasury]).toBeDefined();
    expect(accounts[accountNameAdmin]).toBeDefined();
    expect(accounts[accountNameUser]).toBeDefined();

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
      accounts[accountNameAdmin].privateKey,
      '-t',
      accounts[accountNameTreasury].accountId,
      '-k',
      accounts[accountNameTreasury].privateKey,
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
    let tokens = storeGet('tokens');
    let token = Object.values(tokens).find((t: Token) => t.name === tokenName);
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
      accounts[accountNameUser].accountId,
      '-t',
      token.tokenId,
    ]);

    // Assert
    tokens = storeGet('tokens');
    token = Object.values(tokens).find((t: Token) => t.name === tokenName);
    expect(token?.associations).toEqual([
      {
        accountId: accounts[accountNameUser].accountId,
        name: accountNameUser,
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
      accountNameTreasury,
      '--to',
      accountNameUser,
    ]);
    await waitFor(
      async () => {
        try {
          const data = await api.token.getTokenBalance(
            token.tokenId,
            accounts[accountNameUser].accountId,
          );
          return Array.isArray(data?.data?.balances);
        } catch {
          return false;
        }
      },
      {
        timeout: 10000,
        interval: 500,
        description: 'token association propagation',
      },
    );

    // Assert
    const data = await api.token.getTokenBalance(
      token.tokenId,
      accounts[accountNameUser].accountId,
    );
    expect(data.data.balances).toEqual([
      {
        account: accounts[accountNameUser].accountId,
        balance: 1,
        decimals: 2,
      },
    ]);
  });

  /**
   * E2E testing flow for topics:
   * - Create a topic with admin key and submit key
   * - Submit a message to topic (submit key should sign)
   * - Find the message and verify it is correct
   */
  localnetTest('✅ Topic features', async () => {
    const program = new Command();
    // Arrange: Setup init
    commands.setupCommands(program);

    // Act
    await program.parseAsync(['node', 'hedera-cli.ts', 'setup', 'init']);

    // Assert
    let accounts = storeGet('accounts');
    expect(accounts['localnet-operator']).toBeDefined();

    // Arrange: Change network to localnet
    commands.networkCommands(program);

    // Act
    await program.parseAsync([
      'node',
      'hedera-cli.ts',
      'network',
      'use',
      'localnet',
    ]);

    // Assert
    const network = storeGet('network');
    expect(network).toEqual('localnet');

    // Arrange: Create 2 accounts
    commands.accountCommands(program);
    const accountNameAdmin = 'admin';
    const accountNameSubmit = 'submit';

    // Act
    await program.parseAsync([
      'node',
      'hedera-cli.ts',
      'account',
      'create',
      '-n',
      accountNameAdmin,
      '-b',
      '300000000',
    ]);
    await program.parseAsync([
      'node',
      'hedera-cli.ts',
      'account',
      'create',
      '-n',
      accountNameSubmit,
      '-b',
      '300000000',
    ]);

    // Assert
    accounts = storeGet('accounts');
    expect(accounts[accountNameAdmin]).toBeDefined();
    expect(accounts[accountNameSubmit]).toBeDefined();

    // Arrange: Create a topic with admin key and submit key
    commands.topicCommands(program);
    const topicMemo = 'test-topic';

    // Act
    await program.parseAsync([
      'node',
      'hedera-cli.ts',
      'topic',
      'create',
      '--memo',
      topicMemo,
      '-a',
      accounts[accountNameAdmin].privateKey,
      '-s',
      accounts[accountNameSubmit].privateKey,
    ]);

    // Assert
    const topics = storeGet('topics');
    expect(Object.keys(topics).length).toBe(1);
    expect(topics[Object.keys(topics)[0]].memo).toEqual(topicMemo);

    // Arrange: Submit a message to topic (submit key should sign)
    const message = 'Hello world!';

    // Act
    await program.parseAsync([
      'node',
      'hedera-cli.ts',
      'topic',
      'message',
      'submit',
      '-m',
      message,
      '-t',
      Object.keys(topics)[0],
    ]);
    await waitFor(
      async () => {
        try {
          const resp = await api.topic.findMessage(Object.keys(topics)[0], 1);
          return !!resp?.data?.message;
        } catch {
          return false;
        }
      },
      {
        timeout: 10000,
        interval: 500,
        description: 'topic message availability',
      },
    );

    // Assert
    const response = await api.topic.findMessage(Object.keys(topics)[0], 1); // first message
    expect(
      Buffer.from(response.data.message, 'base64').toString('ascii'),
    ).toEqual(message); // decode buffer

    // Arrange: Find the message and verify it is correct
    // Act
    await program.parseAsync([
      'node',
      'hedera-cli.ts',
      'topic',
      'message',
      'find',
      '-t',
      Object.keys(topics)[0],
      '-s',
      '1',
    ]);

    // Assert
    expect(logSpy).toHaveBeenCalledWith(
      `Message found: "${Buffer.from(response.data.message, 'base64').toString(
        'ascii',
      )}"`,
    );
  });
});
