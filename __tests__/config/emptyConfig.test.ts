(() => {
  const fs = require('fs');
  const cjsPath = require('path');
  const cjsOs = require('os');
  const store = require('../../src/state/store');

// Edge case: empty file (zero bytes) should parse as error and fallback to defaults.

  describe('config edge case: empty user config file', () => {
    test('treats empty file as no overrides', () => {
      const emptyPath = cjsPath.join(cjsOs.tmpdir(), `hcli-empty-${Date.now()}.json`);
      fs.writeFileSync(emptyPath, '');
      process.env.HCLI_CONFIG_FILE = emptyPath;
      store.resetStore({ stateFile: cjsPath.join(cjsOs.tmpdir(), `hcli-state-${Date.now()}.json`) });
      const state = store.getState();
      expect(state.telemetry).toBe(0);
      expect(state.networks['fixture-extra']).toBeUndefined();
    });
  });
})();
