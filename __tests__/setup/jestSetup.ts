// Unified Jest setup file.
// Responsibilities (merged from previous separate files):
// 1. Silence CLI logger output by default to keep test output clean (can override before logger import if needed).
// 2. Point the CLI to the test user config fixture and create an isolated ephemeral state file per Jest worker.
// 3. Expose a marker env var (HCLI_TEST_USER_CONFIG) so tests can assert the fixture layering occurred.

import * as fs from 'fs';
import * as os from 'os';
import * as path from 'path';

// Silence logs unless a test explicitly changes this before importing the logger.
if (!process.env.HCLI_LOG_MODE) {
  process.env.HCLI_LOG_MODE = 'silent';
}

// Resolve test user config fixture.
const fixturePath = path.resolve(
  __dirname,
  '..',
  'fixtures',
  'hedera-cli.config.test.json',
);

if (fs.existsSync(fixturePath)) {
  process.env.HCLI_CONFIG_FILE = fixturePath;
  process.env.HCLI_TEST_USER_CONFIG = '1';

  // Create unique temp dir per Jest worker for state isolation.
  const worker = process.env.JEST_WORKER_ID || '0';
  const tmpDir = fs.mkdtempSync(
    path.join(os.tmpdir(), `hcli-state-${worker}-`),
  );
  const stateFile = path.join(tmpDir, 'state.json');
  process.env.HCLI_STATE_FILE = stateFile;
} else {
  // eslint-disable-next-line no-console
  console.error('[TEST SETUP] Missing test user config fixture:', fixturePath);
}
