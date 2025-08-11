import { baseState, fullState, bob, alice } from "../helpers/state";
import { Command } from "commander";
import commands from "../../src/commands";
import stateController from "../../src/state/stateController";
import hbarUtils from "../../src/utils/hbar";

jest.mock("../../src/state/state"); // Mock the original module -> looks for __mocks__/state.ts in same directory

describe("hbar transfer command", () => {
  const hbarUtilsSpy = jest.spyOn(hbarUtils, 'transfer').mockResolvedValue()

  beforeEach(() => {
    stateController.saveState(fullState);
  });

  describe("hbar transfer - success path", () => {
    afterEach(() => {
      // Spy cleanup
      hbarUtilsSpy.mockClear();
    });

    test("✅ transfer hbar from alice to bob account IDs", async () => {
        // Arrange  
        const program = new Command();
        commands.hbarCommands(program);
        const amount = "10";
  
        // Act
        await program.parseAsync(["node", "hedera-cli.ts", "hbar", "transfer", "-f", alice.accountId, "-t", bob.accountId, "-b", amount]);
  
        // Assert
        expect(hbarUtilsSpy).toHaveBeenCalledWith(Number(amount), alice.accountId, bob.accountId, undefined);
    });
  });
});
