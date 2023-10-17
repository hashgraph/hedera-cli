import { saveState, saveStateAttribute, getAllState } from "../state/stateController.js";

export default (program: any) => {
    program
    .command("record <action> [name]")
    .description("Manage recording of a script")
    .action((action: string, name: string) => {
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

function startRecording(scriptName: string) {
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

function stopRecording(): void {
  saveStateAttribute('recording', 0);
  saveStateAttribute('recordingScriptName', "");
}
