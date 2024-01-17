import { baseState, fullState, accountState, tokenState, scriptState } from "../../helpers/state";
import { Command } from "commander";
import commands from "../../../src/commands";
import stateController from "../../../src/state/stateController";

jest.mock("../../../src/state/state"); // Mock the original module -> looks for __mocks__/state.ts in same directory

describe("state clear command", () => {
  const saveStateControllerSpy = jest.spyOn(stateController, 'saveState');
  const saveKeyStateControllerSpy = jest.spyOn(stateController, 'saveKey');

  beforeEach(() => {
    stateController.saveState(fullState);
  });

  describe("state clear - success path", () => {
    afterEach(() => {
      // Spy cleanup
      saveStateControllerSpy.mockClear();
      saveKeyStateControllerSpy.mockClear();
    });

    test("✅ clear entire CLI state", async () => {
        // Arrange  
        const program = new Command();
        commands.stateCommands(program);
  
        // Act
        await program.parse(["node", "hedera-cli.ts", "state", "clear"]);
  
        // Assert
        expect(saveStateControllerSpy).toHaveBeenCalledWith(baseState);
    });

    test("✅ clear state skip accounts", async () => {
        // Arrange  
        const program = new Command();
        commands.stateCommands(program);
  
        // Act
        await program.parse(["node", "hedera-cli.ts", "state", "clear", "--skip-accounts"]);
  
        // Assert
        expect(saveKeyStateControllerSpy).toHaveBeenCalledWith('tokens', {});
        expect(saveKeyStateControllerSpy).toHaveBeenCalledWith('scripts', {});
        expect(stateController.getAll()).toEqual(accountState);
    });

    test("✅ clear state skip tokens", async () => {
        // Arrange  
        const program = new Command();
        commands.stateCommands(program);
  
        // Act
        await program.parse(["node", "hedera-cli.ts", "state", "clear", "--skip-tokens"]);
  
        // Assert
        expect(saveKeyStateControllerSpy).toHaveBeenCalledWith('accounts', {});
        expect(saveKeyStateControllerSpy).toHaveBeenCalledWith('scripts', {});
        expect(stateController.getAll()).toEqual(tokenState);
    });

    test("✅ clear state skip scripts", async () => {
        // Arrange  
        const program = new Command();
        commands.stateCommands(program);
  
        // Act
        await program.parse(["node", "hedera-cli.ts", "state", "clear", "--skip-scripts"]);
  
        // Assert
        expect(saveKeyStateControllerSpy).toHaveBeenCalledWith('accounts', {});
        expect(saveKeyStateControllerSpy).toHaveBeenCalledWith('tokens', {});
        expect(stateController.getAll()).toEqual(scriptState);
    });

    test("✅ clear state skip all (tokens, scripts, and accounts)", async () => {
        // Arrange  
        const program = new Command();
        commands.stateCommands(program);
  
        // Act
        await program.parse(["node", "hedera-cli.ts", "state", "clear", "--skip-scripts", "--skip-tokens", "--skip-accounts"]);
  
        // Assert
        expect(saveStateControllerSpy).toHaveBeenCalledWith(fullState);
    });
  });
});
