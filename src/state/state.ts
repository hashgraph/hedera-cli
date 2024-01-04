import * as fs from 'fs';
import * as path from 'path';
import type { State as StateInterface } from '../../types';
import { Logger } from '../utils/logger';

const logger = Logger.getInstance();

class State {
  private _statePath: string; // private class property

  constructor() {
    this._statePath = path.join(__dirname, 'state.json');
  }

  set(key: string, value: any): void {
    try {
      const updatedData = { ...this.getAll(), [key]: value };
      fs.writeFileSync(
        this._statePath,
        JSON.stringify(updatedData, null, 2),
        'utf-8',
      );
      logger.verbose(`State saved for key: ${key}`);
    } catch (error: any) {
      logger.error('Unable to save state:', error.message);
    }
  }

  setAll(data: StateInterface): void {
    try {
      fs.writeFileSync(this._statePath, JSON.stringify(data, null, 2), 'utf-8');
      logger.verbose('Full state saved');
    } catch (error: any) {
      logger.error('Unable to save state:', error.message);
    }
  }

  get(key: string): any {
    const state = JSON.parse(fs.readFileSync(this._statePath, 'utf-8'));
    return state[key];
  }

  getAll(): StateInterface {
    const state = JSON.parse(fs.readFileSync(this._statePath, 'utf-8'));
    return state;
  }
}

const state = new State();
export { state };
