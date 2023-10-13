const state = require("./state");

function getState(key) {
  return state.get(key);
}

function getAllState() {
  return state.getAll();
}

function saveState(config) {
    state.setAll(config);
}

function saveStateAttribute(key, value) {
    state.set(key, value);
}

module.exports = {
  getAllState,
  saveState,
  saveStateAttribute,
  getState,
};
