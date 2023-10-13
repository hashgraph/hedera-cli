const fs = require('fs');
const path = require('path');

class State {
  constructor() {
    this._statePath = path.join(__dirname, "state.json");
  }

  set(key, value) {
    try {
      const updatedData = { ...this.getAll(), [key]: value };
      fs.writeFileSync(this._statePath, JSON.stringify(updatedData, null, 2), "utf-8");
      console.log("Config saved.");
    } catch (error) {
      console.error("Error saving config:", error.message);
    }
  }

  setAll(data) {
    try {
      fs.writeFileSync(this._statePath, JSON.stringify(data, null, 2), "utf-8");
      console.log("Config saved.");
    } catch (error) {
      console.error("Error saving config:", error.message);
    }
  }

  get(key) {
    const state = JSON.parse(fs.readFileSync(this._statePath, "utf-8"));
    return state[key];
  }

  getAll() {
    const state = JSON.parse(fs.readFileSync(this._statePath, "utf-8"));
    return state;
  }
}

const state = new State();
module.exports = state;