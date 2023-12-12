import type { State } from '../../types';
import { state } from './state';

const get = (key: string) => {
  return state.get(key);
};

const getAll = () => {
  return state.getAll();
};

const saveState = (config: State) => {
  state.setAll(config);
};

const saveKey = (key: string, value: any) => {
  state.set(key, value);
};

const stateController = {
  getAll,
  saveState,
  saveKey,
  get,
};

export default stateController;
