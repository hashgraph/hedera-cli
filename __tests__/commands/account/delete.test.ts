import { baseState, bob } from "../../helpers/state";
import { Command } from "commander";
import commands from "../../../src/commands";
import accountUtils from "../../../src/utils/account";
import stateController from "../../../src/state/stateController";

jest.mock("../../../src/state/state"); // Mock the original module -> looks for __mocks__/state.ts in same directory

describe("account delete command", () => {
  beforeEach(() => {
    stateController.saveState(baseState);
  });

  describe("account delete - success path", () => {
    test("✅ should delete account by account ID", async () => {
      // Arrange
      const deleteAccountSpy = jest.spyOn(accountUtils, "deleteAccount");
      stateController.saveKey("accounts", { [bob.alias]: bob });

      const program = new Command();
      commands.accountCommands(program);

      // Act
      await program.parse([
        "node",
        "hedera-cli.ts",
        "account",
        "delete",
        "-i",
        bob.accountId,
      ]);

      // Assert
      expect(deleteAccountSpy).toHaveBeenCalledWith(bob.accountId);
      expect(stateController.get("accounts")).toEqual({});
    });

    test("✅ should delete account by alias", async () => {
      // Arrange
      const deleteAccountSpy = jest.spyOn(accountUtils, "deleteAccount");
      stateController.saveKey("accounts", { [bob.alias]: bob });

      const program = new Command();
      commands.accountCommands(program);

      // Act
      await program.parse([
        "node",
        "hedera-cli.ts",
        "account",
        "delete",
        "-a",
        bob.alias,
      ]);

      // Assert
      expect(deleteAccountSpy).toHaveBeenCalledWith(bob.alias);
      expect(stateController.get("accounts")).toEqual({});
    });
  });
});
