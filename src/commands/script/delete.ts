import { recordCommand } from "../../state/stateService";

import scriptUtils from "../../utils/script";

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
      scriptUtils.deleteScript(options.name);
    });
};

interface ScriptDeleteOptions {
  name: string;
}
