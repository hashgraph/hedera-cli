import { baseState, fullState } from "../../helpers/state";
import { Command } from "commander";
import commands from "../../../src/commands";
import accountUtils from "../../../src/utils/account";
import stateController from "../../../src/state/stateController";

jest.mock("../../../src/state/state"); // Mock the original module -> looks for __mocks__/state.ts in same directory

describe("account list command", () => {
  beforeEach(() => {
    stateController.saveState(baseState);
  });

  describe("account list - success path", () => {
    test("✅ should list all accounts from state", async () => {
      // Arrange
      const listAccountSpy = jest.spyOn(accountUtils, "listAccounts");
      stateController.saveKey("accounts", fullState.accounts);

      const program = new Command();
      commands.accountCommands(program);

      // Act
      await program.parse([
        "node",
        "hedera-cli.ts",
        "account",
        "list"
      ]);

      // Assert
      expect(listAccountSpy).toHaveBeenCalledWith(undefined);
      expect(stateController.get("accounts")).toEqual(fullState.accounts);
    });

    test("✅ should list all accounts from state with private keys", async () => {
        // Arrange
        const listAccountSpy = jest.spyOn(accountUtils, "listAccounts");
        stateController.saveKey("accounts", fullState.accounts);
  
        const program = new Command();
        commands.accountCommands(program);
  
        // Act
        await program.parse([
          "node",
          "hedera-cli.ts",
          "account",
          "list",
          "-p"
        ]);
  
        // Assert
        expect(listAccountSpy).toHaveBeenCalledWith(true);
        expect(stateController.get("accounts")).toEqual(fullState.accounts);
      });
  });
});
