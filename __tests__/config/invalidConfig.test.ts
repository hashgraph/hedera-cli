// Negative test: invalid user config file should be ignored and fallback to base defaults
// We simulate this by pointing HCLI_CONFIG_FILE at an invalid JSON file and clearing module caches.

import * as fs from 'fs';
import * as os from 'os';
import * as path from 'path';

describe('invalid user config handling', () => {
  test('falls back to base defaults when user config is unreadable JSON', async () => {
    // Create a temporary invalid JSON file
    const tmpDir = fs.mkdtempSync(path.join(os.tmpdir(), 'hcli-invalid-'));
    const invalidPath = path.join(tmpDir, 'hedera-cli.invalid.config.json');
    fs.writeFileSync(invalidPath, '{"network": "testnet",  BROKEN', 'utf-8');

    // Point env to invalid file BEFORE loading store
    process.env.HCLI_CONFIG_FILE = invalidPath;

    let state: any;
    jest.isolateModules(() => {
      // eslint-disable-next-line @typescript-eslint/no-var-requires
      const mod = require('../../src/state/store');
      state = mod.getState();
    });

    // Because file is invalid we should still have default base network (localnet) from baseConfig
    expect(state.network).toBe('localnet');
    // Telemetry default is 0 from base config
    expect(state.telemetry).toBe(0);
    // Ensure base networks present
    expect(state.networks.localnet).toBeDefined();
    expect(state.networks.testnet).toBeDefined();

    // Clean up temp file
    if (fs.existsSync(invalidPath)) fs.unlinkSync(invalidPath);
    if (fs.existsSync(tmpDir)) fs.rmdirSync(tmpDir);
  });
});
