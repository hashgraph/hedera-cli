import * as fs from 'fs';
import * as path from 'path';

import { baseState } from '../helpers/state';
import { program } from 'commander';
import commands from '../../src/commands';
import stateController from '../../src/state/stateController';

/**
 * E2E testing flow for scripts:
 * - Download a script from the internet
 * - Load and execute the script (list all scripts and spy on logger function)
 * - Delete script and verify it is deleted in state file
 */
describe('End to end: Script features', () => {
  beforeEach(() => {
    stateController.saveState(baseState); // reset state to base state for each test
  });

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
    const scriptURL = 'https://raw.githubusercontent.com/hashgraph/hedera-cli/main/src/commands/script/examples.json';

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
    const distStatePath = path.join(__dirname, '..', '..', 'dist', 'state', 'state.json'); // read state from dist/state/state.json because script load uses dist/ logic
    const distState = await fs.readFileSync(distStatePath, 'utf8')
    expect(Object.keys((JSON.parse(distState)).accounts).length).toBe(1); // 1 random account created

    // Reset state to base state
    await fs.writeFileSync(distStatePath, JSON.stringify(baseState, null, 2), 'utf8');

    // Arrange: Delete script and verify it is deleted in state file
    // Act
    program.parse(['node', 'hedera-cli.ts', 'script', 'delete', '-n', 'account-create-simple']);

    // Assert
    scripts = stateController.get('scripts');
    expect(scripts['script-account-create-simple']).toBeUndefined();
    expect(scripts['script-account-create']).toBeDefined();
  });
});
