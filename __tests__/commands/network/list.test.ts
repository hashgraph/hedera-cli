import { Command } from "commander";
import commands from "../../../src/commands";

describe("network list command", () => {
  const logSpy = jest.spyOn(console, 'log');

  describe("network list - success path", () => {
    afterEach(() => {
      // Spy cleanup
      logSpy.mockClear();
    });

    test("✅ list available networks", async () => {
        // Arrange  
        const program = new Command();
        commands.networkCommands(program);
  
        // Act
        await program.parse(["node", "hedera-cli.ts", "network", "list"]);
  
        // Assert
        expect(logSpy).toHaveBeenCalledWith(`Available networks: mainnet, testnet, previewnet`);
      });
  });
});
