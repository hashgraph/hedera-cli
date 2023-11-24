import { associateToken } from "../../utils/token";
import {
  recordCommand,
} from "../../state/stateService";
import { Logger } from "../../utils/logger";

import type { Command } from "../../../types";

const logger = Logger.getInstance();

export default (program: any) => {
  program
    .command("associate")
    .hook("preAction", (thisCommand: Command) => {
      const command = [
        thisCommand.parent.action().name(),
        ...thisCommand.parent.args,
      ];
      recordCommand(command);
    })
    .description("Associate a token with an account")
    .requiredOption(
      "-a, --account-id <accountId>", // alias is also possible for --acount-id
      "Account ID or account alias to associate with token"
    )
    .requiredOption(
      "-t, --token-id <tokenId>",
      "Token ID to associate with account"
    )
    .action(async (options: AssociateTokenOptions) => {
      try {
        await associateToken(options.tokenId, options.accountId);
      } catch (error) {
        logger.error(error as object);
      }
    });
};

interface AssociateTokenOptions {
    tokenId: string;
    accountId: string;
}
