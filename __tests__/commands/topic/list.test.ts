import { topicState, topic } from "../../helpers/state";
import commands from "../../../src/commands";
import stateController from "../../../src/state/stateController";
import { Command } from "commander";

jest.mock("../../../src/state/state"); // Mock the original module -> looks for __mocks__/state.ts in same directory

describe("topic list command", () => {
  const logSpy = jest.spyOn(console, 'log');

  beforeEach(() => {
    stateController.saveState(topicState);
  });

  describe("topic message submit - success path", () => {
    afterEach(() => {
        // Spy cleanup
        logSpy.mockClear();
      });

    test("✅ List all topics", async () => {
        // Arrange
        const program = new Command();
        commands.topicCommands(program);

        // Act
        await program.parseAsync(["node", "hedera-cli.ts", "topic", "list"]);

        // Assert
        expect(logSpy).toHaveBeenCalledWith(`Topics:`);
        expect(logSpy).toHaveBeenCalledWith(`\tTopic ID: ${topic.topicId}`);
        expect(logSpy).toHaveBeenCalledWith(`\t\t- Submit key: No`);
        expect(logSpy).toHaveBeenCalledWith(`\t\t- Admin key: No`);
    });
  });
});
