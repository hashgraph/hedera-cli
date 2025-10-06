import * as fs from 'fs';
import * as path from 'path';
import type { StoreState } from '../../src/state/store';

// Utility to force a fresh require of a module (bypass Jest/Node cache)
const fresh = <T>(mod: string): T => {
  // eslint-disable-next-line @typescript-eslint/no-var-requires
  delete require.cache[require.resolve(mod)];
  // eslint-disable-next-line @typescript-eslint/no-var-requires
  const loaded: unknown = require(mod);
  return loaded as T;
};

describe('store layering', () => {
  const root = process.cwd();
  const tmpDir = fs.mkdtempSync(path.join(root, 'store-test-'));
  const stateFile = path.join(tmpDir, 'state.json');
  const userConfigFile = path.join(tmpDir, '.hedera-clirc.json');

  let originalCwd = root;
  beforeEach(() => {
    process.env.HCLI_STATE_FILE = stateFile; // force custom path
    process.env.HCLI_CONFIG_FILE = userConfigFile; // force explicit config loading (avoid cosmiconfig caching nuances)
    fs.writeFileSync(
      userConfigFile,
      JSON.stringify({ telemetry: 1, network: 'previewnet' }),
      'utf-8',
    );
    originalCwd = process.cwd();
    process.chdir(tmpDir); // allow cosmiconfig to discover file
  });

  afterEach(() => {
    delete process.env.HCLI_STATE_FILE;
    delete process.env.HCLI_CONFIG_FILE;
    try {
      fs.rmSync(stateFile, { force: true });
    } catch {
      /* ignore */
    }
    try {
      process.chdir(originalCwd);
    } catch {
      /* ignore */
    }
  });

  afterAll(() => {
    try {
      fs.rmSync(tmpDir, { recursive: true, force: true });
    } catch {
      /* ignore */
    }
  });

  test('user config overrides base defaults on first load', () => {
    // Use absolute path so jest can resolve without relying on cwd
    const storePath = path
      .join(root, 'src/state/store.ts')
      .replace(/\.ts$/, '');
    const { getState } = fresh<{ getState: () => StoreState }>(storePath);
    const s = getState();
    expect(s.telemetry).toBe(1);
    expect(s.network).toBe('previewnet');
  });

  test('persisted runtime modifications survive reload while new defaults layer in', () => {
    // First load mutate state
    const storePath = path
      .join(root, 'src/state/store.ts')
      .replace(/\.ts$/, '');
    let storeMod = fresh<{
      saveKey?: <K extends keyof StoreState>(k: K, v: StoreState[K]) => void;
      getState: () => StoreState;
    }>(storePath);
    storeMod.saveKey?.('telemetry', 1);
    storeMod.saveKey?.('network', 'previewnet');
    const { actions } = storeMod.getState();
    actions.addAccount({
      network: 'previewnet',
      name: 'alice',
      accountId: '0.0.123',
      type: 'ECDSA',
      publicKey: 'pk',
      evmAddress: '',
      solidityAddress: '',
      solidityAddressFull: '',
      privateKey: 'sk',
    });

    // Simulate new CLI version adding a new network default by editing config file
    fs.writeFileSync(
      userConfigFile,
      JSON.stringify({
        telemetry: 1,
        network: 'previewnet',
        networks: {
          custom: {
            // Provide valid URLs so schema validation (z.string().url()) passes
            mirrorNodeUrl: 'https://customnet.mirrornode.local/api/v1',
            rpcUrl: 'https://customnet.rpc.local/api',
            operatorKey: '',
            operatorId: '',
            hexKey: '',
          },
        },
      }),
      'utf-8',
    );

    // Clear caches for config and loader to ensure fresh read on next require
    try {
      delete require.cache[require.resolve(userConfigFile)];
    } catch {
      /* ignore */
    }
    try {
      delete require.cache[
        require.resolve(path.join(root, 'src/config/loader.ts'))
      ];
    } catch {
      /* ignore */
    }
    // Reset Jest module registry to force ts-jest to re-evaluate transformed modules
    // (fresh() alone sometimes insufficient due to internal transform caches)
    // eslint-disable-next-line no-undef
    jest.resetModules();

    // Reload store (fresh require)
    storeMod = fresh<{
      getState: () => StoreState;
      // saveKey not needed on reload for this assertion path
    }>(storePath);
    const stateReloaded = storeMod.getState();
    expect(stateReloaded.accounts.alice).toBeDefined();
    expect(stateReloaded.networks.custom).toBeDefined();
  });
});
