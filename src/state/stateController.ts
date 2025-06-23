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
 * Store an argument in the script execution state
 * @param {string} name - The argument name to store the value for
 * @param {string} value - The value to store
 */
const saveScriptArgument = (name: string, value: any) => {
  if (state.get('scriptExecution') === 0) {
    return; // If no script is currently being executed, skip saving the argument
  }
  const activeScript = state.get('scriptExecutionName');
  const scripts = state.get('scripts');
  const scriptName = `script-${activeScript}`;
  const newScripts = { ...scripts };
  newScripts[scriptName].args[name] = value;
  saveKey('scripts', newScripts);
};

/**
 * Get argument from the script execution state
 * @param {string} argument - The argument name to retrieve
 * @returns {any} - The value stored for the given argument name
 */
const getScriptArgument = (argument: string) => {
  if (state.get('scriptExecution') === 0) {
    return; // If no script is currently being executed, skip retrieving the argument
  }
  const activeScript = state.get('scriptExecutionName');
  const scripts = state.get('scripts');
  const scriptName = `script-${activeScript}`;
  return scripts[scriptName]?.args?.[argument];
};

const stateController = {
  getAll,
  saveState,
  saveKey,
  get,
  saveScriptArgument,
  getScriptArgument,
};

export default stateController;
