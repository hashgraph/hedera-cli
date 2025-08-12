## Contributing

### Development Setup

Install dependencies:

```
npm install
```

Run unit tests:

```
npm run test:unit
```

### Configuration & State Layering

The CLI layers state in three tiers (lowest precedence first):

1. Base defaults (`src/state/config.ts`)
2. Optional user overrides (cosmiconfig – `hedera-cli.config.*` or `HCLI_CONFIG_FILE`)
3. Mutable runtime state persisted to JSON (accounts, tokens, scripts, topics, etc.)

`src/state/store.ts` merges these using a deep merge only for the `networks` map to preserve shipped networks while allowing partial overrides.

### Test Environment Strategy

To keep tests deterministic and isolated while still exercising the real layering logic:

- A global Jest setup file (`__tests__/setup/setTestUserConfig.js`) sets `HCLI_CONFIG_FILE` to the fixture `__tests__/fixtures/hedera-cli.config.test.json` for every worker process.
- Each Jest worker is assigned a unique temporary state file via `HCLI_STATE_FILE` (per-worker path in the OS temp directory). This prevents cross-test interference when tests run in parallel and mutate runtime state.
- The store dynamically re-loads user config on hydration, so changes to the user config file appear without restarting the process.

### resetStore Helper

`resetStore(opts?)` (exported from `src/state/store.ts`) rebuilds the underlying Zustand store and re-layers configuration. Use it when you need to:

- Override `HCLI_CONFIG_FILE` within a specific test (e.g. simulate missing / malformed / empty config).
- Point to a custom temporary state file (`opts.stateFile`) for fine‑grained isolation inside a single test file.

Example:

```ts
import { resetStore, getState } from '../../src/state/store';
import * as os from 'os';
import * as path from 'path';

test('missing user config falls back to defaults', () => {
  const tmpConfigPath = path.join(
    os.tmpdir(),
    `hcli-missing-${Date.now()}.json`,
  );
  process.env.HCLI_CONFIG_FILE = tmpConfigPath; // no file created
  resetStore({
    stateFile: path.join(os.tmpdir(), `hcli-state-${Date.now()}.json`),
  });
  const state = getState();
  expect(state.telemetry).toBe(0);
  expect(state.networks['fixture-extra']).toBeUndefined();
});
```

### Logging & Silencing Strategy in Tests

Unit tests load `__tests__/setup/silenceLogs.ts` which sets `HCLI_LOG_MODE=silent`. The `Logger` will not emit user-facing output while still allowing Jest spies on `console.log` / `console.error` to observe calls (the silent transport routes through console for mocks).

Per‑test scoped control helpers live in `__tests__/helpers/loggerHelper.ts`:

```ts
import {
  withSilencedLogs,
  withVerboseLogs,
  withNormalLogs,
} from '../helpers/loggerHelper';

test('quiet block', async () => {
  await withSilencedLogs(async () => {
    // code that would normally log
  });
});

test('verbose tracing', async () => {
  await withVerboseLogs(async () => {
    // code executed with level = verbose
  });
});
```

Prefer these scoped helpers over imperative setters; they automatically restore the previous level.

If you need raw console output in a specific test file, set the mode early (before any logger import):

```ts
process.env.HCLI_LOG_MODE = 'normal'; // or 'verbose'
```

### Adding New Config Edge-Case Tests

Patterns:

- Missing config: point `HCLI_CONFIG_FILE` to a non-existent path then `resetStore()`.
- Empty config: create a temp file with empty contents.
- Malformed config: already covered by `invalidConfig.test.ts` (bad JSON falls back gracefully).

Always restore or reassign `HCLI_CONFIG_FILE` if the test file later relies on the standard fixture.

### Commit Guidelines

- Keep patches focused; unrelated formatting churn makes review harder.
- Prefer small, composable utility helpers when test logic starts repeating (e.g., temp file creation, logger control).
- Run the full unit suite before opening a PR.

### Questions / Improvements

Open an issue or PR if you see an opportunity to simplify store layering, reduce I/O in tests, or extend configuration validation.
