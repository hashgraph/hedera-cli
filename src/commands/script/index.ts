import deleteCommand from "./delete";
import listCommand from "./list";
import downloadCommand from "./download";
import loadCommand from "./load";

import type { Command } from "../../../types";

export default (program: any) => {
  const script = program
    .command("script")
    .description("Handle and execute scripts");

  deleteCommand(script);
  listCommand(script);
  downloadCommand(script);
  loadCommand(script);

  script.command("*").action((options: any, command: Command) => {
    console.error(`Unknown command: script ${command.parent.args[0]}\n`);
    script.outputHelp();
  });
};
