import { Command } from 'commander';
import { getState } from '../../state/store';
import stateUtils from '../../utils/state';
import { telemetryPreAction } from '../shared/telemetryHook';
import { wrapAction } from '../shared/wrapAction';
import { execSync } from 'child_process';
import { Logger } from '../../utils/logger';
import { DomainError } from '../../utils/errors';
import type { Script } from '../../../types';

const logger = Logger.getInstance();

interface ScriptLoadOptions {
  name: string;
}

function loadScript(name: string): void {
  stateUtils.startScriptExecution(name);

  const state = getState();
  const scripts: Record<string, Script> = state.scripts;
  const scriptName = `script-${name}`;
  const script = scripts[scriptName];

  if (!script) {
    stateUtils.stopScriptExecution();
    throw new DomainError(`No script found with name: ${scriptName}`);
  }

  logger.log(`Executing script: ${script.name}\n`);

  script.commands.forEach((command) => {
    logger.log(`\nExecuting command: \t${command}`);

    if (command.startsWith('hardhat')) {
      // If the command starts with 'hardhat', we can execute it directly by adding 'npx'
      // Verify that the command is safe to execute
      if (command.includes('&&') || command.includes(';')) {
        stateUtils.stopScriptExecution();
        throw new DomainError(
          'Unsafe command detected. Please check the script.',
        );
      }

      try {
        execSync(`npx ${command}`, { stdio: 'inherit' });
      } catch {
        stateUtils.stopScriptExecution();
        throw new DomainError('Unable to execute command');
      }
      return;
    }

    // For other commands, we need to run the hedera-cli.js script
    try {
      execSync(`node dist/hedera-cli.js ${command}`, { stdio: 'inherit' });
    } catch {
      stateUtils.stopScriptExecution();
      throw new DomainError('Unable to execute command');
    }
  });

  stateUtils.stopScriptExecution();
  logger.log(`\nScript ${script.name} executed successfully`);
}

export default (program: Command) => {
  program
    .command('load')
    .hook('preAction', telemetryPreAction)
    .description('Load and execute a script')
    .requiredOption('-n, --name <name>', 'Name of script to load and execute')
    .action(
      wrapAction<ScriptLoadOptions>(
        (options) => {
          loadScript(options.name);
        },
        { log: (o) => `Loading script ${o.name}` },
      ),
    );
};
