const { Command } = require("commander");
const networkCommands = require("../../src/commands");
const switchNetworkUtils = require("../../src/utils/switchNetwork");

const fs = require("fs");

describe("network commands", () => {
  describe("network use", () => {
    test("✅ switching networks successfully", () => {
      // Arrange
      const switchNetworkSpy = jest.spyOn(switchNetworkUtils, "switchNetwork");
      fs.readFileSync = jest.fn(() => JSON.stringify({ network: "mainnet" })); // Mock fs.readFileSync to return a sample config
      fs.writeFileSync = jest.fn(); // Mock fs.writeFileSync to do nothing
      console.log = jest.fn(); // Mock console.log to check the log messages

      const program = new Command();
      networkCommands(program);

      // Act
      program.parse(["node", "hedera-cli.js", "network", "use", "testnet"]);

      // Assert
      console.log(switchNetworkSpy.mock.calls);
      expect(switchNetworkSpy).toHaveBeenCalledWith("testnet");

      // Check that console.log was called with the correct message
      expect(console.log).toHaveBeenCalledWith("Switched to testnet");

      // Check that fs.writeFileSync was called with the updated config
      expect(fs.writeFileSync).toHaveBeenCalledWith(
        expect.any(String), // path
        JSON.stringify({ network: "testnet" }, null, 2),
        "utf-8"
      );
    });

    // write test when calling switchNetwork throws an error
    test("❌ throw error when switching to incorrect network", () => {
      // Arrange
      console.error = jest.fn(); // Mock console.log to check the log messages

      const program = new Command();
      networkCommands(program);

      // Act
      program.parse(["node", "hedera-cli.js", "network", "use", "notanetwork"]);

      // Assert
      expect(console.error).toHaveBeenCalledWith(
        "Invalid network name. Available networks: mainnet, testnet"
      );
    });
  });

  describe("network ls", () => {
    test("✅ list networks successfully", () => {
      // Arrange
      console.log = jest.fn(); // Mock console.log to check the log messages

      const program = new Command();
      networkCommands(program);

      // Act
      program.parse(["node", "hedera-cli.js", "network", "ls"]);

      // Assert
      expect(console.log).toHaveBeenCalledWith(
        "Available networks: mainnet, testnet"
      );
    });
  });

  describe("network unknown action", () => {
    test("❌ throw error for unknown action", () => {
      // Arrange
      console.error = jest.fn(); // Mock console.error to check the error messages

      const program = new Command();
      networkCommands(program);

      // Act
      program.parse(["node", "hedera-cli.js", "network", "unknown"]);

      // Assert
      expect(console.error).toHaveBeenCalledWith(
        "Unknown action. Available actions: use, ls"
      );
    });
  });
});
