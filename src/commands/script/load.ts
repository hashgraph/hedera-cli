const axios = require('axios');

import { recordCommand } from '../../state/stateService';
import stateController from '../../state/stateController';
import { execSync } from 'child_process';

import type { Command, Script } from '../../../types';

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
      loadScript(options.name);
    });
};

function loadScript(name: string) {
  const state = stateController.getAll();

  state.scriptExecutionName = name;
  state.scriptExecution = 1;
  stateController.saveState(state);

  const scripts: Record<string, Script> = state.scripts;
  const scriptName = `script-${name}`;
  const script = scripts[scriptName];

  if (!script) {
    console.error(`No script found with name: ${scriptName}`);
    state.scriptExecutionName = '';
    state.scriptExecution = 0;
    stateController.saveState(state);
    process.exit(1);
  }

  console.log(`Executing script: ${script.name}\n`);

  script.commands.forEach((command) => {
    console.log(`Executing command: \t${command}`);

    try {
      execSync(`node dist/hedera-cli.js ${command}`, { stdio: 'inherit' });
    } catch (error: any) {
      console.error(`Error executing command: ${command}`);
      console.error(error.message);
      /*state.scriptExecutionName = '';
      state.scriptExecution = 0;
      stateController.saveState(state);*/
      process.exit(1);
    }
  });

  console.log(`\nScript ${script.name} executed successfully`);
}

interface ScriptLoadOptions {
  name: string;
}
