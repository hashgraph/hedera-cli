import type { State } from '../../types';
import { state } from './state';

const get = (key: string) => {
  return state.get(key);
};

const getAll = () => {
  return state.getAll();
};

const saveState = (newState: State) => {
  state.setAll(newState);
};

const saveKey = (key: string, value: any) => {
  state.set(key, value);
};

/**
 * Store data in memory
 * @param {string} key - The key to store the data under
 * @param {any} value - The value to store
 */
const saveToMemory = (key: string, value: any) => {
  const memory = state.get('memory');
  memory[key] = value;
  saveKey('memory', memory);
};

const stateController = {
  getAll,
  saveState,
  saveKey,
  get,
  saveToMemory,
};

export default stateController;
