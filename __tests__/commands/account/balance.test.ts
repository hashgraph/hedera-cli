import { Command } from "commander";
import commands from "../../../src/commands";
import accountUtils from "../../../src/utils/account";
import api from "../../../src/api";

import { accountResponse, getAccountBalanceResponseMock } from "../../helpers/api/apiAccountHelper";

describe("account balance command", () => {
  describe("account balance - success path", () => {
    test("✅ retrieve hbar balance", async () => {
      // Arrange
      const logSpy = jest.spyOn(console, 'log');
      const getAccountBalanceSpy = jest.spyOn(accountUtils, "getAccountBalance");

      api.account.getAccountBalance = jest.fn().mockResolvedValue(getAccountBalanceResponseMock);

      const program = new Command();
      commands.accountCommands(program);

      // Act
      await program.parse(["node", "hedera-cli.ts", "account", "balance", accountResponse.account, "--only-hbar"]);

      // Assert
      expect(getAccountBalanceSpy).toHaveBeenCalledWith(accountResponse.account, true, undefined);
      expect(logSpy).toHaveBeenCalledWith(`${accountResponse.balance.balance} Hbars`);
    });

    // write test when calling switchNetwork throws an error
    /*test("❌ throw error when switching to incorrect network", () => {
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
    });*/
  });

  /*describe("network ls", () => {
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
  });*/
});
