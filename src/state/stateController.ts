import type { State } from "../../types";
import { state } from "./state";


const getState = (key: string) => {
  return state.get(key);
}

const getAllState = () => {
  return state.getAll();
}

const saveState = (config: State) => {
    state.setAll(config);
}

const saveStateAttribute = (key: string, value: any) => {
    state.set(key, value);
}

export {
  getAllState,
  saveState,
  saveStateAttribute,
  getState,
};
