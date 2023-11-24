import { recordCommand } from "../../state/stateService";

import type { Command } from "../../../types";

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
    .description("List all available networks")
    .action(() => {
      console.log("Available networks: mainnet, testnet");
    });
};
