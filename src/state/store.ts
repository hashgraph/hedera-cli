// Primary Zustand store implementation.
//
// Layering strategy when hydrating:
//   baseConfig (static defaults)
//     + userConfig (cosmiconfig overrides)
//       + persisted runtime slice (accounts/tokens/scripts/etc.)
//
// The merge order ensures new default fields added in later versions appear
// without deleting user data, and user-provided overrides always take
// precedence over shipped defaults but can still be superseded by explicit
// runtime modifications where intended.
import { createStore, type StoreApi } from 'zustand/vanilla';
import { persist } from 'zustand/middleware';
import { immer } from 'zustand/middleware/immer';
import * as fs from 'fs';
// (path import removed – unused)
import type {
  State,
  Account,
  Token,
  Topic,
  Script,
  NetworkConfig,
} from '../../types';
import baseConfig from './config';
import { loadUserConfig, resolveStateFilePath } from '../config/loader';

export type CliState = State;

export interface Actions {
  setNetwork: (network: string) => void;
  ensureUUID: () => void;
  addAccount: (account: Account, overwrite?: boolean) => void;
  removeAccount: (name: string) => void;
  addToken: (token: Token, overwrite?: boolean) => void;
  associateToken: (
    tokenId: string,
    assoc: { name: string; accountId: string },
  ) => void;
  addTopic: (topic: Topic, overwrite?: boolean) => void;
  addScript: (script: Script, overwrite?: boolean) => void;
  startScript: (name: string) => void;
  stopScript: () => void;
  clearRuntime: () => void;
}

export type StoreState = CliState & { actions: Actions };

// Externalized user/runtime persistence path (XDG config or override)
let stateFile = resolveStateFilePath();

// Read persisted runtime state (if file missing / unreadable return empty object)
const loadFile = (): Partial<State> => {
  try {
    const raw = fs.readFileSync(stateFile, 'utf-8');
    const parsed: unknown = JSON.parse(raw);
    if (parsed && typeof parsed === 'object') return parsed as Partial<State>;
    return {};
  } catch {
    return {};
  }
};

// Persist runtime slice (best-effort; failures are silent to avoid crashing CLI)
const writeFile = (data: unknown) => {
  try {
    fs.writeFileSync(stateFile, JSON.stringify(data, null, 2), 'utf-8');
  } catch {
    /* ignore */
  }
};

// Merge helper combining a partial state (file or user overrides) with a base state.
// Performs deep merge only for `networks` so individual network overrides don't
// obliterate the entire networks map.
const mergeInitial = (file: Partial<State>, base: State): CliState => {
  const networks: Record<string, NetworkConfig> = { ...base.networks };
  if (file.networks) {
    for (const k of Object.keys(file.networks)) {
      const incoming = file.networks[k];
      if (incoming) networks[k] = { ...(networks[k] || {}), ...incoming };
    }
  }
  return {
    ...base,
    ...file,
    network: file.network || base.network,
    networks,
    uuid: file.uuid || '',
    accounts: file.accounts || {},
    tokens: file.tokens || {},
    topics: file.topics || {},
    scripts: file.scripts || {},
    scriptExecution: file.scriptExecution || base.scriptExecution,
  };
};

// Load user overrides and precompute layered base prior to reading persisted state.
// See note in buildStore regarding dynamic re-load on hydration.
let { user: userConfig } = loadUserConfig();
let basePlusUser = mergeInitial(userConfig, baseConfig);
let initialState = mergeInitial(loadFile(), basePlusUser);

interface PersistedSlice {
  network: StoreState['network'];
  networks: StoreState['networks'];
  telemetry: StoreState['telemetry'];
  telemetryServer: StoreState['telemetryServer'];
  accounts: StoreState['accounts'];
  tokens: StoreState['tokens'];
  topics: StoreState['topics'];
  scripts: StoreState['scripts'];
  uuid: StoreState['uuid'];
  localNodeAddress: StoreState['localNodeAddress'];
  localNodeAccountId: StoreState['localNodeAccountId'];
  localNodeMirrorAddressGRPC: StoreState['localNodeMirrorAddressGRPC'];
  scriptExecution: StoreState['scriptExecution'];
}

// Determine which fields are persisted (avoid writing derived/action props)
const partialize = (s: StoreState): PersistedSlice => ({
  network: s.network,
  networks: s.networks,
  telemetry: s.telemetry,
  telemetryServer: s.telemetryServer,
  accounts: s.accounts,
  tokens: s.tokens,
  topics: s.topics,
  scripts: s.scripts,
  uuid: s.uuid,
  localNodeAddress: s.localNodeAddress,
  localNodeAccountId: s.localNodeAccountId,
  localNodeMirrorAddressGRPC: s.localNodeMirrorAddressGRPC,
  scriptExecution: s.scriptExecution,
});

// Shared actions builder so we don't duplicate in reset path.
type ImmerSetter<T> = (fn: (draft: T) => void) => void;

const buildActions = (
  set: ImmerSetter<StoreState>,
  get: () => StoreState,
): Actions => ({
  setNetwork: (network) => {
    if (!get().networks[network])
      throw new Error(`Unknown network: ${network}`);
    set((d: StoreState) => {
      d.network = network;
    });
  },
  ensureUUID: () => {
    if (get().uuid) return;
    set((d: StoreState) => {
      d.uuid = crypto.randomUUID();
    });
  },
  addAccount: (account, overwrite = false) =>
    set((d: StoreState) => {
      if (!overwrite && d.accounts[account.name])
        throw new Error(`Account with name ${account.name} already exists`);
      d.accounts[account.name] = account;
    }),
  removeAccount: (name) =>
    set((d: StoreState) => {
      if (!d.accounts[name])
        throw new Error(`Account with name ${name} not found`);
      delete d.accounts[name];
    }),
  addToken: (token, overwrite = false) =>
    set((d: StoreState) => {
      if (!overwrite && d.tokens[token.tokenId])
        throw new Error(`Token with ID ${token.tokenId} already exists`);
      d.tokens[token.tokenId] = token;
    }),
  associateToken: (tokenId, assoc) =>
    set((d: StoreState) => {
      const t = d.tokens[tokenId];
      if (t) t.associations = [...t.associations, assoc];
    }),
  addTopic: (topic, overwrite = false) =>
    set((d: StoreState) => {
      if (!overwrite && d.topics[topic.topicId])
        throw new Error(`Topic with ID ${topic.topicId} already exists`);
      d.topics[topic.topicId] = topic;
    }),
  addScript: (script, overwrite = false) =>
    set((d: StoreState) => {
      const key = `script-${script.name}`;
      if (!overwrite && d.scripts[key])
        throw new Error(`Script with name ${script.name} already exists`);
      d.scripts[key] = { ...script, args: {}, creation: Date.now() };
    }),
  startScript: (name) =>
    set((d: StoreState) => {
      d.scriptExecution.active = true;
      d.scriptExecution.name = name;
    }),
  stopScript: () =>
    set((d: StoreState) => {
      const key = `script-${d.scriptExecution.name}`;
      if (d.scripts[key]) d.scripts[key].args = {};
      d.scriptExecution = { active: false, name: '' };
    }),
  clearRuntime: () =>
    set((d: StoreState) => {
      d.accounts = {};
      d.tokens = {};
      d.topics = {};
      d.scripts = {};
      d.scriptExecution = { active: false, name: '' };
    }),
});

// Build a persisted store instance given an initial state.
const buildStore = (state: CliState): StoreApi<StoreState> =>
  createStore<StoreState>()(
    persist(
      immer<StoreState>((set, get) => ({
        ...state,
        actions: buildActions(set as ImmerSetter<StoreState>, get),
      })),
      {
        name: 'hedera-cli-state',
        version: 1,
        partialize,
        migrate: (persisted: unknown) => {
          if (
            typeof persisted === 'object' &&
            persisted !== null &&
            'scriptExecution' in persisted &&
            typeof (persisted as { scriptExecution: unknown })
              .scriptExecution === 'number'
          ) {
            const p = persisted as {
              scriptExecution: number;
              scriptExecutionName?: string;
            } & Record<string, unknown>;
            const migrated = {
              ...p,
              scriptExecution: {
                active: p.scriptExecution === 1,
                name: p.scriptExecutionName || '',
              },
            } as PersistedSlice & Record<string, unknown>;
            delete migrated.scriptExecutionName;
            return migrated as PersistedSlice;
          }
          return persisted as PersistedSlice;
        },
        storage: {
          getItem: () => {
            const data = loadFile();
            const { user: dynamicUser } = loadUserConfig();
            const dynamicBase = mergeInitial(dynamicUser, baseConfig);
            const merged = mergeInitial(data, dynamicBase);
            return { state: merged } as { state: CliState };
          },
          setItem: (_key: string, value: { state?: CliState }) => {
            try {
              if (value?.state) writeFile(value.state);
            } catch {
              /* ignore */
            }
          },
          removeItem: () => {
            /* noop */
          },
        },
      },
    ),
  );

export let cliStore = buildStore(initialState);

export const getState = (): StoreState => cliStore.getState();
export const actions = (): Actions => cliStore.getState().actions;

// Internal helper using vanilla setState to apply an immer-style mutation function
const mutate = (fn: (draft: StoreState) => void) =>
  cliStore.setState((d) => {
    fn(d);
    return d; // ensure signature returns state/partial for TypeScript
  });

export const get = <K extends keyof StoreState>(k: K): StoreState[K] =>
  getState()[k];
export const saveKey = <K extends keyof StoreState>(
  k: K,
  v: StoreState[K],
): void =>
  mutate((d) => {
    d[k] = v;
  });
export const saveState = (partial: Partial<StoreState>): void =>
  mutate((d) => {
    Object.assign(d, partial);
  });
export const updateState = (mutator: (draft: StoreState) => void): void =>
  mutate(mutator);
export const saveScriptArgument = (name: string, value: string): void =>
  mutate((d) => {
    if (!d.scriptExecution.active) return;
    const script = d.scripts[`script-${d.scriptExecution.name}`];
    if (!script) return;
    script.args = { ...script.args, [name]: value };
  });
export const getScriptArgument = (arg: string): string | undefined => {
  const s = getState();
  if (!s.scriptExecution.active) return undefined;
  return s.scripts[`script-${s.scriptExecution.name}`]?.args?.[arg];
};

// Test-only: reset the store by re-resolving paths and re-layering configuration.
// Safe no-op in production unless explicitly invoked.
export const resetStore = (opts?: {
  stateFile?: string;
  userConfigPath?: string;
}) => {
  if (opts?.stateFile) process.env.HCLI_STATE_FILE = opts.stateFile;
  if (opts?.userConfigPath) process.env.HCLI_CONFIG_FILE = opts.userConfigPath;
  stateFile = resolveStateFilePath();
  ({ user: userConfig } = loadUserConfig());
  basePlusUser = mergeInitial(userConfig, baseConfig);
  initialState = mergeInitial(loadFile(), basePlusUser);
  cliStore = buildStore(initialState);
};
