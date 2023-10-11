const { program } = require("commander");
const { saveConfig, getConfig } = require("../utils/configManager");

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
  const config = getConfig();
  config.recording = 1;
  config.recordingScriptName = `script-${scriptName}`;
  config.scripts[config.recordingScriptName] = {
    name: scriptName,
    creation:  Date.now(),
    commands: []
  };
  saveConfig(config);
}

function stopRecording() {
  const config = getConfig();
  config.recording = 0;
  config.recordingScriptName = "";
  saveConfig(config);
}