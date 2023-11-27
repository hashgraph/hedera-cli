import { recordCommand } from "../../state/stateService";
import { Logger } from "../../utils/logger";
import { myParseInt } from "../../utils/verification";

import accountUtils from "../../utils/account";

import type { Command } from "../../../types";

const logger = Logger.getInstance();

export default (program: any) => {
  program
    .command("create")
    .hook("preAction", (thisCommand: Command) => {
      const command = [
        thisCommand.parent.action().name(),
        ...thisCommand.parent.args,
      ];
      recordCommand(command);
    })
    .description(
      "Create a new Hedera account using NEW recovery words and keypair. This is default."
    )
    .requiredOption("-a, --alias <alias>", "account must have an alias")
    .option(
      "-b, --balance <balance>",
      "Initial balance in tinybars",
      myParseInt,
      1000
    )
    .option(
      "-t, --type <type>",
      "Type of account to create (ECDSA or ED25519)",
      "ED25519"
    )
    .action(async (options: CreateAccountOptions) => {
      try {
        await accountUtils.createAccount(options.balance, options.type, options.alias);
      } catch (error) {
        logger.error(error as object);
      }
    });
};

interface CreateAccountOptions {
    alias: string;
    balance: number;
    type: "ECDSA" | "ED25519";
  }
