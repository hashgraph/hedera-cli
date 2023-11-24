const axios = require("axios");

import { recordCommand } from "../../state/stateService";
import { getState } from "../../state/stateController";

import type { Command, Script } from "../../../types";


export default (program: any) => {
  program
    .command("list")
    .hook("preAction", (thisCommand: Command) => {
      const command = [
        thisCommand.parent.action().name(),
        ...thisCommand.parent.args,
      ];
      recordCommand(command);
    })
    .description("List all scripts")
    .action(() => {
      listScripts();
    });
};

function listScripts() {
  const scripts: Record<string, Script> = getState("scripts");
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
