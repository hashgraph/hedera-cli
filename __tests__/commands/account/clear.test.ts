import { Command } from "commander";
import commands from "../../../src/commands";
import accountUtils from "../../../src/utils/account";
import { state as stateInstance } from "../../../src/state/state";
import * as fs from 'fs';

describe("account clear command", () => {
  describe("account clear - success path", () => {
    test("✅ retrieve hbar balance", async () => {
      // Arrange
      const clearAddressBookSpy = jest.spyOn(accountUtils, "clearAddressBook");
      const saveStateAttribute = jest.fn();

      const program = new Command();
      commands.accountCommands(program);

      // Act
      await program.parse(["node", "hedera-cli.ts", "account", "clear"]);

      // Assert
      expect(clearAddressBookSpy).toHaveBeenCalled();
      console.log(saveStateAttribute.caller)      
    });

  });
});
