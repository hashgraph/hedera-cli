import stateController from "../state/stateController";

import type { Script } from "../../types";

function listScripts() {
  const scripts: Record<string, Script> = stateController.get("scripts");
  const scriptNames = Object.keys(scripts);

  if (scriptNames.length === 0) {
    console.log("No scripts found");
    return;
  }

  console.log("Scripts:");
  scriptNames.forEach((scriptName) => {
    console.log(`\t${scriptName}`);
    console.log(`\t- Commands:`);
    scripts[scriptName].commands.forEach((command) => {
      console.log(`\t\t${command}`);
    });
  });
}

function deleteScript(name: string) {
  const scripts: Record<string, Script> = stateController.get("scripts");
  const scriptName = `script-${name}`;
  const script = scripts[scriptName];

  if (!script) {
    console.error(`No script found with name: ${scriptName}`);
    return;
  }

  delete scripts[scriptName];
  stateController.saveKey("scripts", scripts);
  console.log(`Script ${scriptName} deleted successfully`);
}

const scriptUtils = {
  listScripts,
  deleteScript,
};

export default scriptUtils;
