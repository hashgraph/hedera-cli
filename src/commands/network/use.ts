import { recordCommand, switchNetwork } from "../../state/stateService";

import type { Command } from "../../../types";

export default (program: any) => {
  program
    .command("use <name>")
    .hook("preAction", (thisCommand: Command) => {
      const command = [
        thisCommand.parent.action().name(),
        ...thisCommand.parent.args,
      ];
      recordCommand(command);
    })
    .description("Switch to a specific network")
    .action((name: string) => {
      switchNetwork(name);
    });
};
