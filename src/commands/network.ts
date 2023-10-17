import { recordCommand, switchNetwork } from "../state/stateService";

import type { Command } from "../../types";

export default (program: any) => {
    const network = program.command("network");

  network
    .command("use <name>")
    .hook("preAction", (thisCommand: Command) => {
      const command = [thisCommand.parent.action().name(), ...thisCommand.parent.args]
      recordCommand(command);
    })
    .description("Switch to a specific network")
    .action((name: string) => {
      switchNetwork(name);
    });

  network
    .command("ls")
    .hook("preAction", (thisCommand: Command) => {
      const command = [thisCommand.parent.action().name(), ...thisCommand.parent.args]
      recordCommand(command);
    })
    .description("List all available networks")
    .action(() => {
      console.log("Available networks: mainnet, testnet");
    });

  // Catch unknown commands
  network.command("*").action((options: any, command: Command) => {
    console.error(`Unknown command: network ${command.parent.args[0]}\n`);
    network.outputHelp(); // Display help for the 'network' command
  });
};
