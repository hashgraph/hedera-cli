import * as fs from 'fs';
import * as path from 'path';
import type { State as StateInterface } from '../../types';
import { Logger } from '../utils/logger';
import { createStore } from 'zustand/vanilla';
import type { StoreApi } from 'zustand/vanilla';

const logger = Logger.getInstance();

class State {
  private _statePath: string;
  private _store: StoreApi<StateInterface> | null = null;

  constructor() {
    this._statePath = path.join(__dirname, 'state.json');
    this.initStore();
  }

  private readFileState(): StateInterface {
    try {
      return JSON.parse(fs.readFileSync(this._statePath, 'utf-8'));
    } catch {
      return {} as StateInterface; // fallback (tests expect file to exist in build)
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
    const initialRaw = this.readFileState();
    const initial = this.normalize(initialRaw as Partial<StateInterface>);
    // Persist back if normalization added missing keys
    if (JSON.stringify(initialRaw) !== JSON.stringify(initial)) {
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
      const updated = this.normalize({ ...current, [key]: value });
      store.setState(updated, true);
      this.writeFileState(updated);
      logger.verbose(`State saved for key: ${key}`);
    } catch (error: any) {
      logger.error('Unable to save state:', error.message);
    }
  }

  setAll(data: StateInterface): void {
    try {
      const normalized = this.normalize(data);
      this.ensureStore().setState({ ...normalized } as StateInterface, true);
      this.writeFileState(normalized);
      logger.verbose('Full state saved');
    } catch (error: any) {
      logger.error('Unable to save state:', error.message);
    }
  }

  get(key: string): any {
    return (this.ensureStore().getState() as any)[key];
  }

  getAll(): StateInterface {
    return this.ensureStore().getState();
  }

  // Ensure all mandatory sections exist to prevent null/undefined errors downstream
  private normalize(data: Partial<StateInterface>): StateInterface {
    const networksDefault = data.networks || {};
    const ensureNet = (name: string) => {
      if (!networksDefault[name]) {
        networksDefault[name] = {
          mirrorNodeUrl: '',
          rpcUrl: '',
          operatorKey: '',
          operatorId: '',
          hexKey: '',
        };
      } else {
        // fill missing keys individually
        networksDefault[name].mirrorNodeUrl =
          networksDefault[name].mirrorNodeUrl || '';
        networksDefault[name].rpcUrl = networksDefault[name].rpcUrl || '';
        networksDefault[name].operatorKey =
          networksDefault[name].operatorKey || '';
        networksDefault[name].operatorId =
          networksDefault[name].operatorId || '';
        networksDefault[name].hexKey = networksDefault[name].hexKey || '';
      }
    };
    ['localnet', 'previewnet', 'testnet', 'mainnet'].forEach(ensureNet);

    // Provide sensible defaults aligning with original base_state.json for localnet if empty
    if (networksDefault['localnet'].mirrorNodeUrl === '') {
      networksDefault['localnet'].mirrorNodeUrl =
        'http://localhost:5551/api/v1';
    }
    if (networksDefault['localnet'].rpcUrl === '') {
      networksDefault['localnet'].rpcUrl = 'http://localhost:7546';
    }
    if (networksDefault['localnet'].operatorId === '') {
      networksDefault['localnet'].operatorId = '0.0.2';
    }
    if (networksDefault['localnet'].operatorKey === '') {
      networksDefault['localnet'].operatorKey =
        '302e020100300506032b65700422042087592ee314bd0f42c4cf9f82b494481a2bb77bab0dc4454eedfe00f60168646f';
    }
    if (networksDefault['localnet'].hexKey === '') {
      networksDefault['localnet'].hexKey =
        '0x87592ee314bd0f42c4cf9f82b494481a2bb77bab0dc4454eedfe00f60168646f';
    }

    const normalized: StateInterface = {
      network:
        typeof data.network === 'string' && data.network !== ''
          ? data.network
          : 'localnet',
      networks: networksDefault,
      telemetryServer:
        data.telemetryServer ||
        'https://hedera-cli-telemetry.onrender.com/track',
      telemetry: typeof data.telemetry === 'number' ? data.telemetry : 0,
      scriptExecution:
        typeof data.scriptExecution === 'number' ? data.scriptExecution : 0,
      scriptExecutionName: data.scriptExecutionName || '',
      accounts:
        data.accounts && typeof data.accounts === 'object'
          ? (data.accounts as any)
          : {},
      scripts:
        data.scripts && typeof data.scripts === 'object'
          ? (data.scripts as any)
          : {},
      tokens:
        data.tokens && typeof data.tokens === 'object'
          ? (data.tokens as any)
          : {},
      topics:
        data.topics && typeof data.topics === 'object'
          ? (data.topics as any)
          : {},
      localNodeAddress: data.localNodeAddress || '127.0.0.1:50211',
      localNodeAccountId: data.localNodeAccountId || '0.0.3',
      localNodeMirrorAddressGRPC:
        data.localNodeMirrorAddressGRPC || '127.0.0.1:5600',
      uuid: data.uuid || '',
    } as StateInterface;

    return normalized;
  }
}

const state = new State();
export { state };
