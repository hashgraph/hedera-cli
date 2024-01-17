import { alice, bob, baseState } from "../../helpers/state";
import { Command } from "commander";
import commands from "../../../src/commands";
import stateController from "../../../src/state/stateController";

jest.mock("../../../src/state/state"); // Mock the original module -> looks for __mocks__/state.ts in same directory

describe("token create command", () => {
  const mockProcessExit = jest.spyOn(process, 'exit').mockImplementation(((code) => { 
    throw new Error(`Process.exit(${code})`); // Forces the code to throw instead of exit
  }));

  const saveKeyStateControllerSpy = jest.spyOn(stateController, 'saveKey');

  beforeEach(() => {
    stateController.saveState(baseState);
  });

  afterEach(() => {
    // Spy cleanup
    mockProcessExit.mockClear();
    saveKeyStateControllerSpy.mockClear();
  });

  describe("token create - success path", () => {
    test("✅ ", async () => {
      // Arrange
      const program = new Command();
      commands.tokenCommands(program);

      // Act
      try {
        await program.parseAsync([
            "node",
            "hedera-cli.ts",
            "token",
            "create",
            "-t",
            alice.accountId,
            "-k",
            alice.privateKey,
            "-n",
            "test-token",
            "-s",
            "TST",
            "-d",
            "2",
            "-i",
            "1000",
            "--supply-type",
            "infinite",
            "-a",
            bob.privateKey
        ]);
      } catch (error) {
        expect(error).toEqual(Error(`Process.exit(1)`));
      }

      // Assert
      expect(Object.keys(stateController.get('tokens')).length).toEqual(1);
      expect(saveKeyStateControllerSpy).toHaveBeenCalledWith('tokens', expect.any(Object));
    });
  });
});
