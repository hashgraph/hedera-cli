import deleteCommand from "./delete";
import balanceCommand from "./balance";
import createCommand from "./create";
import importCommand from "./import";
import listCommand from "./list";
import clearCommand from "./clear";

import type { Command } from "../../../types";

export default (program: any) => {
  const account = program.command("account");

  deleteCommand(account);
  balanceCommand(account);
  createCommand(account);
  importCommand(account);
  listCommand(account);
  clearCommand(account);

  account.command("*").action((options: any, command: Command) => {
    console.error(`Unknown command: account ${command.parent.args[0]}\n`);
    account.outputHelp();
  });
};
