import { recordCommand } from "../../state/stateService";
import { importAccount } from "../../utils/account";

import type { Command } from "../../../types";

export default (program: any) => {
  program
    .command("import")
    .hook("preAction", (thisCommand: Command) => {
      const command = [
        thisCommand.parent.action().name(),
        ...thisCommand.parent.args,
      ];
      recordCommand(command);
    })
    .description(
      "Import an existing account using a private key, type, account ID, and alias"
    )
    .requiredOption("-a, --alias <alias>", "account must have an alias")
    .requiredOption("-i, --id <id>", "Account ID")
    .requiredOption("-k, --key <key>", "Private key")
    .action((options: ImportAccountOptions) => {
      importAccount(options.id, options.key, options.alias);
    });
};

interface ImportAccountOptions {
  alias: string;
  id: string;
  key: string;
}
