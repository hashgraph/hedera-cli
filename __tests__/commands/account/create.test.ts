import { baseState } from "../../helpers/state";
import { Command } from "commander";
import commands from "../../../src/commands";
import accountUtils from "../../../src/utils/account";
import stateController from "../../../src/state/stateController";
import { AccountId } from "@hashgraph/sdk";

jest.mock("../../../src/state/state"); // Mock the original module -> looks for __mocks__/state.ts in same directory
jest.mock('@hashgraph/sdk', () => {
  const originalModule = jest.requireActual('@hashgraph/sdk');

  return {
    ...originalModule,
    AccountCreateTransaction: jest.fn().mockImplementation(() => ({
      setKey: jest.fn().mockReturnThis(),
      setInitialBalance: jest.fn().mockReturnThis(),
      setMaxAutomaticTokenAssociations: jest.fn().mockReturnThis(),
      execute: jest.fn().mockResolvedValue({
        getReceipt: jest.fn().mockResolvedValue({
          accountId: AccountId.fromString('0.0.1234'),
        })
      }),
    })),
  };
});

describe("account create command", () => {
  beforeEach(() => {
    stateController.saveState(baseState);
  });

  describe("account create - success path", () => {
    test("✅ should create ED25519 account in state", async () => {
      // Arrange
      const balance = 5000;
      const newAccountAlias = "greg";
      const type = "ED25519";
      const createAccountSpy = jest.spyOn(accountUtils, "createAccount");

      const program = new Command();
      commands.accountCommands(program);

      // Act
      await program.parseAsync([
        "node",
        "hedera-cli.ts",
        "account",
        "create",
        "-a",
        newAccountAlias,
        "-b",
        balance.toString(),
        "-t",
        type
      ]);

      // Assert
      const greg = accountUtils.findAccountByAlias(newAccountAlias);
      expect(createAccountSpy).toHaveBeenCalledWith(balance, type, newAccountAlias, 0);
      expect(greg.alias).toBe(newAccountAlias);
      expect(greg.type).toBe(type);
    });

    test("✅ should create ECDSA account in state", async () => {
        // Arrange
        const balance = 5000;
        const newAccountAlias = "greg";
        const type = "ECDSA"
        const createAccountSpy = jest.spyOn(accountUtils, "createAccount");
  
        const program = new Command();
        commands.accountCommands(program);
  
        // Act
        await program.parseAsync([
          "node",
          "hedera-cli.ts",
          "account",
          "create",
          "-a",
          newAccountAlias,
          "-b",
          balance.toString(),
          "-t",
          type
        ]);
  
        // Assert
        const greg = accountUtils.findAccountByAlias(newAccountAlias);
        expect(createAccountSpy).toHaveBeenCalledWith(balance, type, newAccountAlias, 0);
        expect(greg.alias).toBe(newAccountAlias);
        expect(greg.type).toBe(type);
      });
  });
});
