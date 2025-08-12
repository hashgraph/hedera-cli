/**
 * Configuration & state path resolution utilities.
 *
 * The CLI supports three layers of configuration/state data:
 * 1. Base defaults (TypeScript object in src/state/config.ts)
 * 2. Optional user overrides discovered via cosmiconfig (this file)
 * 3. Mutable runtime state persisted as JSON (accounts, tokens, scripts, etc.)
 *
 * This loader only concerns itself with (2) and the physical location of (3).
 *
 * Environment variables:
 *  - HCLI_CONFIG_FILE : absolute/relative path to an explicit user config file
 *  - HCLI_STATE_FILE  : absolute/relative path to override the state.json location
 *  - XDG_CONFIG_HOME  : respected (Linux/macOS) for default state location
 *
 * State default locations (when HCLI_STATE_FILE not set):
 *  - Linux/macOS: ~/.config/hedera-cli/state.json (or $XDG_CONFIG_HOME/hedera-cli/state.json)
 *  - Windows: %APPDATA%/hedera-cli/state.json (indirectly via os.homedir + .config if XDG not set)
 */
import { cosmiconfigSync } from 'cosmiconfig';
import * as fs from 'fs';
import * as os from 'os';
import * as path from 'path';
import type { State } from '../../types';
import { validateUserConfig } from './schema';

const MODULE_NAME = 'hedera-cli';

export interface LoadedConfig {
  user: Partial<State>;
  source?: string;
}

/**
 * Attempt to load user configuration overrides.
 *
 * Precedence:
 *  1. HCLI_CONFIG_FILE (if present and loadable)
 *  2. First cosmiconfig match for module name `hedera-cli`
 *  3. Fallback to empty object
 */
export const loadUserConfig = (): LoadedConfig => {
  const direct = process.env.HCLI_CONFIG_FILE;
  if (direct) {
    try {
      const full = path.resolve(direct);
      if (!fs.existsSync(full)) return { user: {}, source: undefined };
      const ext = path.extname(full).toLowerCase();
      if (ext === '.json' || ext === '') {
        try {
          const raw = fs.readFileSync(full, 'utf-8');
          const parsedRaw = JSON.parse(raw) as unknown;
          const validated = validateUserConfig(parsedRaw);
          if (!validated.valid) {
            // Surface validation errors but continue with empty overlay
            // (avoid throwing to keep CLI resilient)
            // eslint-disable-next-line no-console
            console.warn(
              `Invalid user config at ${full}:\n${validated.errors?.join('\n')}`,
            );
            return { user: {}, source: full };
          }
          return { user: validated.config as Partial<State>, source: full };
        } catch {
          return { user: {}, source: full };
        }
      }
      // Fallback to require for JS/TS transpiled configs
      // eslint-disable-next-line @typescript-eslint/no-var-requires
      const cfgRaw = require(full) as unknown;
      const validated = validateUserConfig(cfgRaw);
      if (!validated.valid) {
        // eslint-disable-next-line no-console
        console.warn(
          `Invalid user config at ${full}:\n${validated.errors?.join('\n')}`,
        );
        return { user: {}, source: full };
      }
      return { user: validated.config as Partial<State>, source: full };
    } catch {
      return { user: {}, source: direct };
    }
  }
  try {
    const explorer = cosmiconfigSync(MODULE_NAME);
    const result = explorer.search();
    if (result && !result.isEmpty) {
      const validated = validateUserConfig(result.config as unknown);
      if (!validated.valid) {
        // eslint-disable-next-line no-console
        console.warn(
          `Invalid user config at ${result.filepath}:\n${validated.errors?.join('\n')}`,
        );
        return { user: {}, source: result.filepath };
      }
      return {
        user: validated.config as Partial<State>,
        source: result.filepath,
      };
    }
  } catch {
    /* ignore */
  }
  return { user: {}, source: undefined };
};

/**
 * Resolve the path where mutable runtime state should be stored.
 * Creates the parent directory if it does not exist (best-effort / silent on failure).
 */
export const resolveStateFilePath = (): string => {
  if (process.env.HCLI_STATE_FILE)
    return path.resolve(process.env.HCLI_STATE_FILE);
  const home = os.homedir && os.homedir();
  const base =
    process.env.XDG_CONFIG_HOME ||
    (home ? path.join(home, '.config') : process.cwd());
  const dir = path.join(base, 'hedera-cli');
  if (!fs.existsSync(dir)) {
    try {
      fs.mkdirSync(dir, { recursive: true });
    } catch {
      /* ignore */
    }
  }
  return path.join(dir, 'state.json');
};
