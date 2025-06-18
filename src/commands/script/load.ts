import stateController from '../../state/stateController';
import stateUtils from '../../utils/state';
import telemetryUtils from '../../utils/telemetry';
import { execSync } from 'child_process';
import { Logger } from '../../utils/logger';

import type { Command, Script } from '../../../types';

const logger = Logger.getInstance();

interface ScriptLoadOptions {
  name: string;
}

function loadScript(name: string) {
  stateUtils.startScriptExecution(name);

  const state = stateController.getAll();
  const scripts: Record<string, Script> = state.scripts;
  const scriptName = `script-${name}`;
  const script = scripts[scriptName];

  if (!script) {
    logger.error(`No script found with name: ${scriptName}`);
    stateUtils.stopScriptExecution();
    process.exit(1);
  }

  logger.log(`Executing script: ${script.name}\n`);

  script.commands.forEach((command) => {
    logger.log(`\nExecuting command: \t${command}`);

    if (command.startsWith('npx')) {
      // If the command starts with 'npx', we can execute it directly
      // Verify that the command is safe to execute
      if (command.includes('&&') || command.includes(';')) {
        logger.error('Unsafe command detected. Please check the script.');
        stateUtils.stopScriptExecution();
        process.exit(1);
      }

      try {
        execSync(`npx ${command}`, { stdio: 'inherit' });
      } catch (error: any) {
        logger.error('Unable to execute command', error.message || error);
        stateUtils.stopScriptExecution();
        process.exit(1);
      }
      return;
    }

    // For other commands, we need to run the hedera-cli.js script
    try {
      execSync(`node dist/hedera-cli.js ${command}`, { stdio: 'inherit' });
    } catch (error: any) {
      logger.error('Unable to execute command', error.message || error);
      stateUtils.stopScriptExecution();
      process.exit(1);
    }
  });

  stateUtils.stopScriptExecution();
  logger.log(`\nScript ${script.name} executed successfully`);
}

export default (program: any) => {
  program
    .command('load')
    .hook('preAction', async (thisCommand: Command) => {
      const command = [
        thisCommand.parent.action().name(),
        ...thisCommand.parent.args,
      ];
      if (stateUtils.isTelemetryEnabled()) {
        await telemetryUtils.recordCommand(command.join(' '));
      }
    })
    .description('Load and execute a script')
    .requiredOption('-n, --name <name>', 'Name of script to load and execute')
    .action((options: ScriptLoadOptions) => {
      logger.verbose(`Loading script ${options.name}`);
      loadScript(options.name);
    });
};
