import { baseState, fullState, script_basic } from "../../helpers/state";
import { Command } from "commander";
import commands from "../../../src/commands";
import scriptUtils from "../../../src/utils/script";
import stateController from "../../../src/state/stateController";

jest.mock("../../../src/state/state"); // Mock the original module -> looks for __mocks__/state.ts in same directory

describe("script delete command", () => {
  beforeEach(() => {
    stateController.saveState(baseState);
  });

  describe("script delete - success path", () => {
    test("✅ should delete account by account ID", async () => {
      // Arrange
      const deleteScriptSpy = jest.spyOn(scriptUtils, "deleteScript");
      stateController.saveState(fullState);

      const program = new Command();
      commands.scriptCommands(program);

      // Act
      await program.parse([
        "node",
        "hedera-cli.ts",
        "script",
        "delete",
        "-n",
        script_basic.name,
      ]);

      // Assert
      expect(deleteScriptSpy).toHaveBeenCalledWith(script_basic.name);
      expect(stateController.get("scripts")).toEqual({});
    });
  });
});
