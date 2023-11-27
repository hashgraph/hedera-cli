import { recordCommand }  from "../../state/stateService";
import { Logger } from "../../utils/logger";

import accountUtils from "../../utils/account";

import type { Command } from "../../../types";

const logger = Logger.getInstance();

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
    .description("Delete an account from the address book")
    .option("-a, --alias <alias>", "account must have an alias")
    .option("-i, --id <id>", "Account ID")
    .action((options: AccountDeleteOptions) => {
      const accountIdOrAlias = options.id || options.alias;
      if (!accountIdOrAlias) {
        logger.error(
          "Error: You must provide either an account ID or an alias."
        );
        return;
      }
      accountUtils.deleteAccount(accountIdOrAlias);
    });
};

interface AccountDeleteOptions {
  alias?: string;
  id?: string;
}
