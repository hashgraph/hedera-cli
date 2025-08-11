import { fullState  } from "../../helpers/state";
import { Command } from "commander";
import commands from "../../../src/commands";
import accountUtils from "../../../src/utils/account";
import { saveState as storeSaveState, get as storeGet } from "../../../src/state/store";

jest.mock('../../../src/state/state'); // Mock the original module -> looks for __mocks__/state.ts in same directory

describe("account clear command", () => {
  beforeEach(() => {
  storeSaveState(fullState as any); // initialize state for each test
  });

  describe("account clear - success path", () => {
    test("✅ should clear accounts from state", async () => {
      // Arrange
      const clearAddressBookSpy = jest.spyOn(accountUtils, "clearAddressBook");

      const program = new Command();
      commands.accountCommands(program);

      // Act
      await program.parse(["node", "hedera-cli.ts", "account", "clear"]);

      // Assert
      expect(clearAddressBookSpy).toHaveBeenCalled();
  expect(storeGet('accounts' as any)).toEqual({});
    });
  });
});
