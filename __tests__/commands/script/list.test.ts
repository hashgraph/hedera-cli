import { baseState, scriptState, script_basic } from "../../helpers/state";
import { Command } from "commander";
import commands from "../../../src/commands";
import scriptUtils from "../../../src/utils/script";
import stateController from "../../../src/state/stateController";

jest.mock("../../../src/state/state"); // Mock the original module -> looks for __mocks__/state.ts in same directory

describe("script list command", () => {
  const logSpy = jest.spyOn(console, 'log');

  beforeEach(() => {
    stateController.saveState(baseState);
  });

  describe("script list - success path", () => {
    afterEach(() => {
        // Spy cleanup
        logSpy.mockClear();
    });

    test("✅ should list all scripts from state", async () => {
      // Arrange
      const program = new Command();
      commands.scriptCommands(program);
      stateController.saveState(scriptState)

      // Act
      await program.parse([
        "node",
        "hedera-cli.ts",
        "script",
        "list"
      ]);

      // Assert
      expect(logSpy).toHaveBeenCalledWith(`\tscript-${script_basic.name}`)
      script_basic.commands.forEach(command => {
        expect(logSpy).toHaveBeenCalledWith(`\t\t${command}`);      
      });
    });
  });
});
