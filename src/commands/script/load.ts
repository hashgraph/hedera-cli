const axios = require('axios');

import { recordCommand } from '../../state/stateService';
import stateController from '../../state/stateController';
import {
  startScriptExecution,
  stopScriptExecution,
} from '../../state/stateService';
import { execSync } from 'child_process';
import { Logger } from '../../utils/logger';

import type { Command, Script } from '../../../types';

const logger = Logger.getInstance();

export default (program: any) => {
  program
    .command('load')
    .hook('preAction', (thisCommand: Command) => {
      const command = [
        thisCommand.parent.action().name(),
        ...thisCommand.parent.args,
      ];
      recordCommand(command);
    })
    .description('Load and execute a script')
    .requiredOption('-n, --name <name>', 'Name of script to load and execute')
    .action((options: ScriptLoadOptions) => {
      logger.verbose(`Loading script ${options.name}`);
      loadScript(options.name);
    });
};

function loadScript(name: string) {
  startScriptExecution(name);

  const state = stateController.getAll();
  const scripts: Record<string, Script> = state.scripts;
  const scriptName = `script-${name}`;
  const script = scripts[scriptName];

  if (!script) {
    logger.error(`No script found with name: ${scriptName}`);
    stopScriptExecution();
    process.exit(1);
  }

  logger.log(`Executing script: ${script.name}\n`);

  script.commands.forEach((command) => {
    logger.log(`\nExecuting command: \t${command}`);

    try {
      execSync(`node dist/hedera-cli.js ${command}`, { stdio: 'inherit' });
    } catch (error: any) {
      logger.error('Unable to execute command', error.message || error);
      stopScriptExecution();
      process.exit(1);
    }
  });

  stopScriptExecution();
  logger.log(`\nScript ${script.name} executed successfully`);
}

interface ScriptLoadOptions {
  name: string;
}
