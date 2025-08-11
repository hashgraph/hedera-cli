// Primary Zustand store implementation (formerly in newStore.ts)
import { createStore } from 'zustand/vanilla';
import { persist } from 'zustand/middleware';
import { immer } from 'zustand/middleware/immer';
import * as fs from 'fs';
import * as path from 'path';
import type {
  State,
  Account,
  Token,
  Topic,
  Script,
  NetworkConfig,
} from '../../types';
import baseConfig from './config';

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

const stateFile = path.join(__dirname, 'state.json');

const loadFile = (): Partial<State> => {
  try {
    return JSON.parse(fs.readFileSync(stateFile, 'utf-8'));
  } catch {
    return {};
  }
};

const writeFile = (data: unknown) => {
  try {
    fs.writeFileSync(stateFile, JSON.stringify(data, null, 2), 'utf-8');
  } catch {
    /* ignore */
  }
};

const mergeInitial = (file: Partial<State>, base: State): CliState => {
  const networks: Record<string, NetworkConfig> = { ...base.networks };
  if (file.networks) {
    for (const k of Object.keys(file.networks)) {
      networks[k] = { ...(networks[k] || {}), ...file.networks[k]! };
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

const initialState = mergeInitial(loadFile(), baseConfig);

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

export const cliStore = createStore<StoreState>()(
  persist(
    immer<StoreState>((set, get) => ({
      ...initialState,
      actions: {
        setNetwork: (network) => {
          if (!get().networks[network])
            throw new Error(`Unknown network: ${network}`);
          set((d) => {
            d.network = network;
          });
        },
        ensureUUID: () => {
          if (!get().uuid)
            set((d) => {
              d.uuid = crypto.randomUUID();
            });
        },
        addAccount: (account, overwrite = false) =>
          set((d) => {
            if (!overwrite && d.accounts[account.name])
              throw new Error(
                `Account with name ${account.name} already exists`,
              );
            d.accounts[account.name] = account;
          }),
        removeAccount: (name) =>
          set((d) => {
            if (!d.accounts[name])
              throw new Error(`Account with name ${name} not found`);
            delete d.accounts[name];
          }),
        addToken: (token, overwrite = false) =>
          set((d) => {
            if (!overwrite && d.tokens[token.tokenId])
              throw new Error(`Token with ID ${token.tokenId} already exists`);
            d.tokens[token.tokenId] = token;
          }),
        associateToken: (tokenId, assoc) =>
          set((d) => {
            const t = d.tokens[tokenId];
            if (t) t.associations = [...t.associations, assoc];
          }),
        addTopic: (topic, overwrite = false) =>
          set((d) => {
            if (!overwrite && d.topics[topic.topicId])
              throw new Error(`Topic with ID ${topic.topicId} already exists`);
            d.topics[topic.topicId] = topic;
          }),
        addScript: (script, overwrite = false) =>
          set((d) => {
            const key = `script-${script.name}`;
            if (!overwrite && d.scripts[key])
              throw new Error(`Script with name ${script.name} already exists`);
            d.scripts[key] = { ...script, args: {}, creation: Date.now() };
          }),
        startScript: (name) =>
          set((d) => {
            d.scriptExecution.active = true;
            d.scriptExecution.name = name;
          }),
        stopScript: () =>
          set((d) => {
            const key = `script-${d.scriptExecution.name}`;
            if (d.scripts[key]) d.scripts[key].args = {};
            d.scriptExecution = { active: false, name: '' };
          }),
        clearRuntime: () =>
          set((d) => {
            d.accounts = {};
            d.tokens = {};
            d.topics = {};
            d.scripts = {};
            d.scriptExecution = { active: false, name: '' };
          }),
      },
    })),
    {
      name: 'hedera-cli-state',
      version: 1,
      partialize,
      migrate: (persisted: any) => {
        if (persisted && typeof persisted.scriptExecution === 'number') {
          persisted.scriptExecution = {
            active: persisted.scriptExecution === 1,
            name: persisted.scriptExecutionName || '',
          };
          delete persisted.scriptExecutionName;
        }
        return persisted;
      },
      storage: {
        getItem: (_name: string) => {
          const data = loadFile();
          const merged = mergeInitial(data, baseConfig);
          return { state: { ...initialState, ...merged } } as any;
        },
        setItem: (_key: string, value: any) => {
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

export const getState = (): StoreState => cliStore.getState();
export const actions = (): Actions => cliStore.getState().actions;

const mutate = (fn: (draft: StoreState) => void) =>
  cliStore.setState((d) => {
    fn(d);
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
