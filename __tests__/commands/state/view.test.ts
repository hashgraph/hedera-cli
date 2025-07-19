import { fullState, alice } from "../../helpers/state";
import { Command } from "commander";
import commands from "../../../src/commands";
import stateController from "../../../src/state/stateController";
import { Logger } from "../../../src/utils/logger";

const logger = Logger.getInstance();

jest.mock("../../../src/state/state"); // Mock the original module -> looks for __mocks__/state.ts in same directory

describe("state view command", () => {
  const logSpy = jest.spyOn(logger, 'log');

  beforeEach(() => {
    stateController.saveState(fullState);
  });

  describe("state view - success path", () => {
    afterEach(() => {
      // Spy cleanup
      logSpy.mockClear();
    });

    test("✅ view entire state", async () => {
        // Arrange  
        const program = new Command();
        commands.stateCommands(program);
  
        // Act
        await program.parse(["node", "hedera-cli.ts", "state", "view"]);
  
        // Assert
        expect(logSpy).toHaveBeenCalledWith("\nState:");
        expect(logSpy).toHaveBeenCalledWith(fullState);
    });

    test("✅ view specific account with name", async () => {
        // Arrange  
        const program = new Command();
        commands.stateCommands(program);
  
        // Act
        await program.parse(["node", "hedera-cli.ts", "state", "view", "--account-name", alice.name]);
  
        // Assert
        expect(logSpy).toHaveBeenCalledWith("\nAccount:");
        expect(logSpy).toHaveBeenCalledWith(alice);
    });

    test("✅ view specific account with account ID", async () => {
        // Arrange  
        const program = new Command();
        commands.stateCommands(program);
  
        // Act
        await program.parse(["node", "hedera-cli.ts", "state", "view", "--account-id", alice.accountId]);
  
        // Assert
        expect(logSpy).toHaveBeenCalledWith(`\nAccount ${alice.accountId}:`);
        expect(logSpy).toHaveBeenCalledWith(alice);
    });
  });
});
