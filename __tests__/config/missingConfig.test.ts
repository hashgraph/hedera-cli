(() => {
  // Use CommonJS requires with namespace object to avoid duplicate identifiers
  const cjsPath = require('path');
  const cjsOs = require('os');
  const store = require('../../src/state/store');

  // Edge case: HCLI_CONFIG_FILE points to a missing file -> should gracefully fallback to defaults (no fixture-added network, telemetry 0)

  describe('config edge case: missing user config file', () => {
    test('falls back to base defaults', () => {
      const missingPath = cjsPath.join(
        cjsOs.tmpdir(),
        `hcli-missing-${Date.now()}.json`,
      ); // not created
      process.env.HCLI_CONFIG_FILE = missingPath;
      store.resetStore({
        stateFile: cjsPath.join(
          cjsOs.tmpdir(),
          `hcli-state-${Date.now()}.json`,
        ),
      });
      const state = store.getState();
      expect(state.telemetry).toBe(0);
      expect(state.networks['fixture-extra']).toBeUndefined();
    });
  });
})();
