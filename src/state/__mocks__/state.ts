import { State as StateInterface } from "../../../types";

class StateMock {
  private state: Record<string, any> = {};

  set(key: string, value: any): void {
    this.state[key] = value;
  }

  setAll(data: StateInterface): void {
    this.state = { ...data };
  }

  get(key: string): any {
    return this.state[key];
  }

  getAll(): StateInterface {
    return this.state as StateInterface;
  }
}

const state = new StateMock();

export { state };
