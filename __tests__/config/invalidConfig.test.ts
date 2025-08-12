// Negative test: invalid user config file should be ignored and fallback to base defaults
// We simulate this by pointing HCLI_CONFIG_FILE at an invalid JSON file and clearing module caches.

const fs = require('fs');
const path = require('path');
const os = require('os');

describe('invalid user config handling', () => {
  test('falls back to base defaults when user config is unreadable JSON', () => {
    // Create a temporary invalid JSON file
    const tmpDir = fs.mkdtempSync(path.join(os.tmpdir(), 'hcli-invalid-'));
    const invalidPath = path.join(tmpDir, 'hedera-cli.invalid.config.json');
    fs.writeFileSync(invalidPath, '{"network": "testnet",  BROKEN', 'utf-8');

    // Point env to invalid file BEFORE loading store
    process.env.HCLI_CONFIG_FILE = invalidPath;

    let state: any;
    jest.isolateModules(() => {
      const { getState } = require('../../src/state/store');
      state = getState();
    });

    // Because file is invalid we should still have default base network (localnet) from baseConfig
    expect(state.network).toBe('localnet');
    // Telemetry default is 0 from base config
    expect(state.telemetry).toBe(0);
    // Ensure base networks present
    expect(state.networks.localnet).toBeDefined();
    expect(state.networks.testnet).toBeDefined();

    // Clean up temp file
    try {
      fs.unlinkSync(invalidPath);
      fs.rmdirSync(tmpDir);
    } catch {}
  });
});
