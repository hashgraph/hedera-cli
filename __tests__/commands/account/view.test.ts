import { alice, baseState } from "../../helpers/state";
import { Command } from "commander";
import commands from "../../../src/commands";
import api from "../../../src/api/";
import stateController from "../../../src/state/stateController";
import { accountResponse } from "../../helpers/api/apiAccountHelper";
import { Logger } from "../../../src/utils/logger";

const logger = Logger.getInstance();

jest.mock("../../../src/state/state"); // Mock the original module -> looks for __mocks__/state.ts in same directory

describe("account view command", () => {
  const getAccountInfoSpy = jest.spyOn(api.account, "getAccountInfo").mockResolvedValue({
    data: accountResponse,
  });
  const logSpy = jest.spyOn(logger, "log").mockImplementation();

  beforeEach(() => {
    stateController.saveState(baseState);
  });

  afterEach(() => {
    getAccountInfoSpy.mockClear();
    logSpy.mockClear();
  });

  describe("account view - success path", () => {
    test("✅ should print account details for requested account ID", async () => {
      // Arrange
      const program = new Command();
      commands.accountCommands(program);

      // Act
      await program.parse([
        "node",
        "hedera-cli.ts",
        "account",
        "view",
        "-i",
        accountResponse.account,
      ]);

      // Assert
      expect(getAccountInfoSpy).toHaveBeenCalledWith(accountResponse.account);
      expect(logSpy).toHaveBeenCalledWith(`Account: ${accountResponse.account}`);
      expect(logSpy).toHaveBeenCalledWith(`Balance Tinybars: ${accountResponse.balance.balance}`);
      expect(logSpy).toHaveBeenCalledWith(`Deleted: ${accountResponse.deleted}`);
      expect(logSpy).toHaveBeenCalledWith(`EVM Address: ${accountResponse.evm_address}`);
      expect(logSpy).toHaveBeenCalledWith(`Key type: ${accountResponse.key._type} - Key: ${accountResponse.key.key}`);
      expect(logSpy).toHaveBeenCalledWith(`Max automatic token associations: ${accountResponse.max_automatic_token_associations}`);
    });
  });
});
