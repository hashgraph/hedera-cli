const { recordCommand } = require("../utils/configManager");
const switchNetworkUtils = require("../utils/switchNetwork");

module.exports = (program) => {
  const network = program.command("network");

  network
    .command("use <name>")
    .hook("preAction", (thisCommand) => {
      const command = [thisCommand.parent.action().name(), ...thisCommand.parent.args]
      recordCommand(command);
    })
    .description("Switch to a specific network")
    .action((name) => {
      switchNetworkUtils.switchNetwork(name);
    });

  network
    .command("ls")
    .hook("preAction", (thisCommand) => {
      const command = [thisCommand.parent.action().name(), ...thisCommand.parent.args]
      recordCommand(command);
    })
    .description("List all available networks")
    .action(() => {
      console.log("Available networks: mainnet, testnet");
    });

  // Catch unknown commands
  network.command("*").action((options, command) => {
    console.error(`Unknown command: network ${command.args[0]}\n`);
    network.outputHelp(); // Display help for the 'network' command
  });
};
