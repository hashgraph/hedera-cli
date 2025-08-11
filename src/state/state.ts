import * as fs from 'fs';
import * as path from 'path';
import type { State as StateInterface, NetworkConfig } from '../../types';
import { Logger } from '../utils/logger';
import { createStore } from 'zustand/vanilla';
import type { StoreApi } from 'zustand/vanilla';
import config from './config';

const logger = Logger.getInstance();

// Single source of truth for initial defaults imported from config.ts
const defaultState: StateInterface = config;

class State {
  private _statePath: string;
  private _store: StoreApi<StateInterface> | null = null;

  constructor() {
    this._statePath = path.join(__dirname, 'state.json');
    this.initStore();
  }

  private readFileState(): StateInterface | Partial<StateInterface> {
    try {
      return JSON.parse(fs.readFileSync(this._statePath, 'utf-8'));
    } catch {
      return {}; // fallback (tests expect file to exist in build)
    }
  }

  private writeFileState(data: StateInterface) {
    try {
      fs.writeFileSync(this._statePath, JSON.stringify(data, null, 2), 'utf-8');
    } catch (error: any) {
      logger.error('Unable to persist state file:', error.message);
    }
  }

  private initStore() {
    const fileData = this.readFileState();
    const initial = this.mergeDefaults(fileData);
    if (JSON.stringify(fileData) !== JSON.stringify(initial)) {
      this.writeFileState(initial);
    }
    this._store = createStore<StateInterface>(() => ({ ...initial }));
  }

  private ensureStore() {
    if (!this._store) this.initStore();
    return this._store!;
  }

  set(key: string, value: any): void {
    try {
      const store = this.ensureStore();
      const current = store.getState();
      const updated: StateInterface = { ...current, [key]: value };
      store.setState(updated, true);
      this.writeFileState(updated);
      logger.verbose(`State saved for key: ${key}`);
    } catch (error: any) {
      logger.error('Unable to save state:', error.message);
    }
  }

  setAll(data: StateInterface): void {
    try {
      // Assume callers provide full shape after initialization
      this.ensureStore().setState({ ...data }, true);
      this.writeFileState(data);
      logger.verbose('Full state saved');
    } catch (error: any) {
      logger.error('Unable to save state:', error.message);
    }
  }

  get<K extends keyof StateInterface>(key: K): StateInterface[K] {
    return this.ensureStore().getState()[key];
  }

  getAll(): StateInterface {
    return this.ensureStore().getState();
  }

  // One-time deep-ish merge: preserve unknown keys from file, fill known defaults
  private mergeDefaults(file: Partial<StateInterface>): StateInterface {
    const mergedNetworks: Record<string, NetworkConfig> = {};
    const fileNetworks = file.networks || {};
    // Merge known networks
    for (const name of Object.keys(defaultState.networks)) {
      mergedNetworks[name] = {
        ...defaultState.networks[name],
        ...(fileNetworks[name] || {}),
      };
    }
    // Include any extra custom networks from file
    for (const extra of Object.keys(fileNetworks)) {
      if (!mergedNetworks[extra]) {
        mergedNetworks[extra] = fileNetworks[extra];
      }
    }

    const merged: StateInterface = {
      ...defaultState,
      ...file,
      network: file.network || defaultState.network,
      networks: mergedNetworks,
      accounts: file.accounts || {},
      scripts: file.scripts || {},
      tokens: file.tokens || {},
      topics: file.topics || {},
    };
    return merged;
  }
}

const state = new State();
export { state };
