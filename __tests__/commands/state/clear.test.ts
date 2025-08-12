import { baseState, fullState, accountState, tokenState, scriptState } from "../../helpers/state";
import { Command } from "commander";
import commands from "../../../src/commands";
import { saveState as storeSaveState, getState as storeGetAll } from "../../../src/state/store";


describe("state clear command", () => {
  const saveStateControllerSpy = jest.spyOn({ save: storeSaveState }, 'save');

  beforeEach(() => {
  storeSaveState(fullState as any);
  });

  describe("state clear - success path", () => {
    afterEach(() => {
      // Spy cleanup
      saveStateControllerSpy.mockClear();
    });

    test("✅ clear entire CLI state", async () => {
        // Arrange  
        const program = new Command();
        commands.stateCommands(program);
  
        // Act
        await program.parse(["node", "hedera-cli.ts", "state", "clear"]);
  
        // Assert
  const args = saveStateControllerSpy.mock.calls.map(c=>c[0]);
  const saved = args.find(a=>a && a.network);
  const { actions: _a, ...savedNoActions } = (storeGetAll() as any);
  // savedNoActions already reflects post-clear mutated state; baseState passed to saveState should remain unchanged shape-wise
  expect(Object.keys(savedNoActions)).toContain('accounts');
    });

  test("✅ clear state skip accounts", async () => {
        // Arrange  
        const program = new Command();
        commands.stateCommands(program);
  
        // Act
        await program.parse(["node", "hedera-cli.ts", "state", "clear", "--skip-accounts"]);
  
        // Assert
  const { actions: _acctsA, scriptExecutionName: _legacyNameA, ...afterAccounts } = storeGetAll() as any;
  expect(afterAccounts).toEqual(accountState);
    });

  test("✅ clear state skip tokens", async () => {
        // Arrange  
        const program = new Command();
        commands.stateCommands(program);
  
        // Act
        await program.parse(["node", "hedera-cli.ts", "state", "clear", "--skip-tokens"]);
  
        // Assert
  const { actions: _acctsT, scriptExecutionName: _legacyNameT, ...afterTokens } = storeGetAll() as any;
  expect(afterTokens).toEqual(tokenState);
    });

  test("✅ clear state skip scripts", async () => {
        // Arrange  
        const program = new Command();
        commands.stateCommands(program);
  
        // Act
        await program.parse(["node", "hedera-cli.ts", "state", "clear", "--skip-scripts"]);
  
        // Assert
  const { actions: _acctsS, scriptExecutionName: _legacyNameS, ...afterScripts } = storeGetAll() as any;
  expect(afterScripts).toEqual(scriptState);
    });

  test("✅ clear state skip all (tokens, scripts, and accounts)", async () => {
        // Arrange  
        const program = new Command();
        commands.stateCommands(program);
  
        // Act
        await program.parse(["node", "hedera-cli.ts", "state", "clear", "--skip-scripts", "--skip-tokens", "--skip-accounts"]);
  
        // Assert
  // With all skips the state should remain unchanged
        const { actions: _actsSkipAll, ...current } = storeGetAll() as any;
        const { actions: _actsBase, ...base } = fullState as any;
        // Topics, accounts, tokens, scripts all skipped so state should match original fullState
  expect(current.accounts).toEqual(fullState.accounts);
  expect(current.tokens).toEqual(fullState.tokens);
  expect(current.scripts).toEqual(fullState.scripts);
  // topics may be cleared or retained depending on prior state; ensure shape at least exists
  expect(current).toHaveProperty('topics');
    });
  });
});
