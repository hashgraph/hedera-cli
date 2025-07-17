import { alice, baseState } from "../../helpers/state";
import { Command } from "commander";
import commands from "../../../src/commands";
import accountUtils from "../../../src/utils/account";
import stateController from "../../../src/state/stateController";

jest.mock("../../../src/state/state"); // Mock the original module -> looks for __mocks__/state.ts in same directory

describe("account import command", () => {
  beforeEach(() => {
    stateController.saveState(baseState);
  });

  describe("account import - success path", () => {
    test("✅ should import account into state", async () => {
      // Arrange
      const importAccountSpy = jest.spyOn(accountUtils, "importAccount");

      const program = new Command();
      commands.accountCommands(program);

      // Act
      await program.parse([
        "node",
        "hedera-cli.ts",
        "account",
        "import",
        "-n",
        alice.name,
        "-i",
        alice.accountId,
        "-k",
        alice.privateKey
      ]);

      // Assert
      expect(importAccountSpy).toHaveBeenCalledWith(alice.accountId, alice.privateKey, alice.name);
      expect(stateController.get("accounts")).toEqual({ [alice.name]: alice });
    });
  });
});
