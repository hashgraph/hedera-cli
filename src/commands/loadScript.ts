import { getState } from '../state/stateController';
import { execSync } from 'child_process';

import type { Command, Script } from "../../types";

export default (program: any) => {
    program
    .command('load script <name>')
    .description('Load and execute a recorded script')
    .action((command: Command, name: string) => {
      loadScript(name);
    });
};

function loadScript(name: string) {
  const scripts: Record<string, Script> = getState('scripts');
  const scriptName = `script-${name}`;
  const script = scripts[scriptName];

  if (!script) {
    console.error(`No script found with name: ${scriptName}`);
    return;
  }

  console.log(`Executing script: ${script.name}\n`);
  
  script.commands.forEach((command) => {
    console.log(`Executing command: \t${command}`);

    try {
      execSync(`node dist/hedera-cli.js ${command}`, { stdio: 'inherit' });
    } catch (error: any) {
      console.error(`Error executing command: ${command}`);
      console.error(error.message);
      return;
    }
  });

  console.log(`\nScript ${script.name} executed successfully`);
}

