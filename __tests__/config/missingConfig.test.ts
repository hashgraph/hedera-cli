import * as os from 'os';
import * as path from 'path';
// eslint-disable-next-line @typescript-eslint/no-var-requires
const store = require('../../src/state/store');

(() => {
  // Edge case: HCLI_CONFIG_FILE points to a missing file -> should gracefully fallback to defaults (no fixture-added network, telemetry 0)

  describe('config edge case: missing user config file', () => {
    test('falls back to base defaults', () => {
      const missingPath = path.join(
        os.tmpdir(),
        `hcli-missing-${Date.now()}.json`,
      ); // not created
      process.env.HCLI_CONFIG_FILE = missingPath;
      store.resetStore({
        stateFile: path.join(os.tmpdir(), `hcli-state-${Date.now()}.json`),
      });
      const state = store.getState();
      expect(state.telemetry).toBe(0);
      expect(state.networks['fixture-extra']).toBeUndefined();
    });
  });
})();
