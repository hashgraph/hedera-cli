// Ensures user config fixture is applied before any modules import the store/loader.
// This runs for ALL unit tests via jest.unit.config.js setupFiles.
const path = require('path');
const fs = require('fs');

const fixturePath = path.resolve(
  __dirname,
  '..',
  'fixtures',
  'hedera-cli.config.test.json',
);
if (fs.existsSync(fixturePath)) {
  process.env.HCLI_CONFIG_FILE = fixturePath;
  // Provide a marker so tests can assert they are running under test user config
  process.env.HCLI_TEST_USER_CONFIG = '1';
  // Use a unique ephemeral state file per Jest worker to avoid cross-test interference.
  // JEST_WORKER_ID is available in Jest >= 27.
  const worker = process.env.JEST_WORKER_ID || '0';
  const tmpDir = fs.mkdtempSync(
    path.join(require('os').tmpdir(), `hcli-state-${worker}-`),
  );
  const stateFile = path.join(tmpDir, 'state.json');
  process.env.HCLI_STATE_FILE = stateFile;
} else {
  // Fail loudly if expected fixture missing
  // eslint-disable-next-line no-console
  console.error('[TEST SETUP] Missing test user config fixture:', fixturePath);
}
