const { program } = require("commander");
const { saveState, saveStateAttribute, getAllState } = require("../state/stateController");

module.exports = () => {
  program
    .command("record <action> [name]")
    .description("Manage recording of a script")
    .action((action, name) => {
      switch (action) {
        case "start":
          if (!name) {
            console.error("Error: Script name is required for start action");
            process.exit(1);
          }
          startRecording(name);
          console.log(`Recording started for script: ${name}`);
          break;
        case "stop":
          stopRecording();
          console.log("Recording stopped");
          break;
        default:
          console.error(`Unknown action: ${action}`);
          process.exit(1);
      }
    });
};

function startRecording(scriptName) {
  const state = getAllState();
  state.recording = 1;
  state.recordingScriptName = `script-${scriptName}`;
  state.scripts[state.recordingScriptName] = {
    name: scriptName,
    creation:  Date.now(),
    commands: []
  };
  saveState(state);
}

function stopRecording() {
  saveStateAttribute('recording', 0);
  saveStateAttribute('recordingScriptName', "");
}
