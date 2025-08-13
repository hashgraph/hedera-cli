import { program } from 'commander';
import api from '../src/api';
import commands from '../src/commands';
import {
  get as storeGet,
  saveState as storeSaveState,
  type StoreState,
} from '../src/state/store';
import { waitFor } from './helpers/poll';
import { baseState } from './helpers/state';

/**
 * Minimal E2E sanity: verifies localnet reachable and setup init creates operator account.
 * Extend iteratively to isolate failing steps from full flow in e2e.test.ts.
 */
describe('minimal e2e sanity', () => {
  beforeEach(() => {
    storeSaveState(baseState as Partial<StoreState>);
  });

  test('setup init -> operator account present & mirror responds', async () => {
    commands.setupCommands(program);

    await program.parseAsync(['node', 'hedera-cli.ts', 'setup', 'init']);

    const accounts = storeGet('accounts');
    expect(accounts['localnet-operator']).toBeDefined();

    // Mirror availability quick check
    const ok = await waitFor(
      async () => {
        try {
          const res = await api.account.getAccountInfo('0.0.2');
          return !!res?.data;
        } catch {
          return false;
        }
      },
      {
        timeout: 5000,
        interval: 500,
        description: 'mirror node operator account info',
      },
    );
    expect(ok).toBe(true);
  });
});
