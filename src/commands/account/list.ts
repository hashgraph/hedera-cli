import { recordCommand } from "../../state/stateService";
import { Logger } from "../../utils/logger";

import { listAccounts } from "../../utils/account";

import type { Command } from "../../../types";

const logger = Logger.getInstance();

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
    .description("List all accounts in the address book")
    .option("-p, --private", "Show private keys")
    .action((options: ListAccountsOptions) => {
      listAccounts(options.private);
    });
};

interface ListAccountsOptions {
  private: boolean;
}
