import * as fs from 'fs';
import * as path from 'path';
import type { State as StateInterface } from '../../types';

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
      console.log('Config saved.');
    } catch (error: any) {
      console.error('Error saving config:', error.message);
    }
  }

  setAll(data: StateInterface): void {
    try {
      fs.writeFileSync(this._statePath, JSON.stringify(data, null, 2), 'utf-8');
      console.log('Config saved.');
    } catch (error: any) {
      console.error('Error saving config:', error.message);
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
