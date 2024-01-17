import { baseState } from "../../helpers/state";
import { Command } from "commander";
import commands from "../../../src/commands";
import stateController from "../../../src/state/stateController";

jest.mock("../../../src/state/state"); // Mock the original module -> looks for __mocks__/state.ts in same directory

describe("network use command", () => {
  const stateControllerSpy = jest.spyOn(stateController, 'saveKey');

  beforeEach(() => {
    stateController.saveState(baseState);
  });

  describe("network use - success path", () => {
    afterEach(() => {
      // Spy cleanup
      stateControllerSpy.mockClear();
    });

    test("✅ switch to mainnet", async () => {
        // Arrange  
        const program = new Command();
        commands.networkCommands(program);
  
        // Act
        await program.parse(["node", "hedera-cli.ts", "network", "use", "mainnet"]);
  
        // Assert
        expect(stateControllerSpy).toHaveBeenCalledWith('network', 'mainnet');
      });
  });
});
