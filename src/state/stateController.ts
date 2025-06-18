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

/**
 * Get data from memory
 * @param {string} key - The key to retrieve the data from
 * @returns {any} - The value stored under the key, or undefined if not found
 */
const getFromMemory = (key: string) => {
  const memory = state.get('memory');
  return memory[key];
};

const stateController = {
  getAll,
  saveState,
  saveKey,
  get,
  saveToMemory,
  getFromMemory,
};

export default stateController;
