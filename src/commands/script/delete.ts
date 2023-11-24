import { recordCommand } from "../../state/stateService";

import { getState, saveStateAttribute } from "../../state/stateController";

import type { Command, Script } from "../../../types";

export default (program: any) => {
  program
    .command("delete")
    .hook("preAction", (thisCommand: Command) => {
      const command = [
        thisCommand.parent.action().name(),
        ...thisCommand.parent.args,
      ];
      recordCommand(command);
    })
    .description("Delete a script")
    .requiredOption("-n, --name <name>", "Name of script to delete")
    .action((options: ScriptDeleteOptions) => {
      deleteScript(options.name);
    });
};

function deleteScript(name: string) {
  const scripts: Record<string, Script> = getState("scripts");
  const scriptName = `script-${name}`;
  const script = scripts[scriptName];

  if (!script) {
    console.error(`No script found with name: ${scriptName}`);
    return;
  }

  delete scripts[scriptName];
  saveStateAttribute("scripts", scripts);
  console.log(`Script ${scriptName} deleted successfully`);
}

interface ScriptDeleteOptions {
  name: string;
}
