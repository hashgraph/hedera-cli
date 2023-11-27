import { recordCommand } from "../../state/stateService";

import accountUtils from "../../utils/account";
import type { Command } from "../../../types";

export default (program: any) => {
  program
    .command("clear")
    .hook("preAction", (thisCommand: Command) => {
      const command = [
        thisCommand.parent.action().name(),
        ...thisCommand.parent.args,
      ];
      recordCommand(command);
    })
    .description("Clear all accounts from the address book")
    .action(() => {
      accountUtils.clearAddressBook();
    });
};
