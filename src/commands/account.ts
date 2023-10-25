import { myParseInt } from "../utils/verification";
import { recordCommand }  from "../state/stateService";
import { Logger } from "../utils/logger";

import { createAccount, getAccountBalance, listAccounts, importAccount, clearAddressBook } from "../utils/account";

import type { Command } from "../../types";

const logger = Logger.getInstance();

export default (program: any) => {
  const account = program.command("account");

  account
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
        await createAccount(options.balance, options.type, options.alias);
      } catch (error) {
        logger.error(error as object);
      }
    });

  account
    .command("balance <accountIdOrAlias>")
    .hook("preAction", (thisCommand: Command) => {
      const command = [
        thisCommand.parent.action().name(),
        ...thisCommand.parent.args,
      ];
      recordCommand(command);
    })
    .description("Retrieve the balance for an account ID or alias")
    .option("-h, --only-hbar", "Show only Hbar balance")
    .option("-t, --token-id <tokenId>", "Show balance for a specific token ID")
    .action(async (accountIdOrAlias: string, options: GetAccountBalanceOptions) => {
      if (options.onlyHbar && options.tokenId) {
        logger.error(
          "Error: You cannot use both --only-hbar and --token-id options at the same time."
        );
        return;
      }

      try {
        await getAccountBalance(accountIdOrAlias, options.onlyHbar, options.tokenId);
      } catch (error) {
        logger.error(error as object);
      }
    });

  account
    .command("ls")
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

  account
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

  account
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
      clearAddressBook();
    });
};

interface CreateAccountOptions {
  alias: string;
  balance: number;
  type: "ECDSA" | "ED25519";
}

interface GetAccountBalanceOptions {
  onlyHbar: boolean;
  tokenId: string;
}

interface ListAccountsOptions {
  private: boolean;
}

interface ImportAccountOptions {
  alias: string;
  id: string;
  key: string;
}