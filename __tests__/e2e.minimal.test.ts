import { Command } from 'commander';
import commands from '../src/commands';
import {
  get as storeGet,
  saveState as storeSaveState,
  type StoreState,
} from '../src/state/store';
import { initLocalnetFlag, localnetTest } from './helpers/localnet';
import { baseState } from './helpers/state';

/**
 * Minimal E2E sanity: verifies localnet reachable and setup init creates operator account.
 * Extend iteratively to isolate failing steps from full flow in e2e.test.ts.
 */
describe('minimal e2e sanity', () => {
  beforeEach(() => {
    storeSaveState(baseState as Partial<StoreState>);
  });

  beforeAll(async () => {
    await initLocalnetFlag();
  });

  localnetTest(
    'setup init -> operator account present (soft-skip if localnet down)',
    async () => {
      const program = new Command();
      commands.setupCommands(program);
      await program.parseAsync(['node', 'hedera-cli.ts', 'setup', 'init']);
      const accounts = storeGet('accounts');
      expect(accounts['localnet-operator']).toBeDefined();
    },
  );
});
