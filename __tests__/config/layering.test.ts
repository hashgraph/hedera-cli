const { resetStore, getState } = require('../../src/state/store');
describe('config layering with test user config', () => {
  test('merges test user config with base defaults', () => {
    // Sanity: marker env set
    expect(process.env.HCLI_TEST_USER_CONFIG).toBe('1');
  // Reset store to ensure fresh layering (uses per-worker temp state file from setup)
  resetStore();
  const state = getState();

    // Default network preserved as specified in fixture
    expect(state.network).toBe('localnet');

  // Telemetry overridden to 1 by fixture (runtime file removed so user override surfaces)
  expect(state.telemetry).toBe(1);

    // Base networks still present
    expect(state.networks.testnet).toBeDefined();
    expect(state.networks.mainnet).toBeDefined();

    // Fixture-added network present
    expect(state.networks['fixture-extra']).toBeDefined();
    expect(state.networks['fixture-extra'].rpcUrl).toMatch(/fixture\.extra/);

    // Partial override: we only set rpcUrl/mirrorNodeUrl for localnet in fixture; operator fields from base remain
    expect(state.networks.localnet.operatorId).toBe('0.0.2');

    // Ensure no accidental mutation of base defaults shape
    const networkKeys = Object.keys(state.networks).sort();
    expect(networkKeys).toEqual(expect.arrayContaining(['localnet','testnet','previewnet','mainnet','fixture-extra']));
  });
});
