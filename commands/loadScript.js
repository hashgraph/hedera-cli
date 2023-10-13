const { program } = require('commander');
const { getState } = require('../state/stateController');
const { execSync } = require('child_process');

module.exports = () => {
  program
    .command('load script <name>')
    .description('Load and execute a recorded script')
    .action((command, name) => {
      loadScript(name);
    });
};

function loadScript(name) {
  const scripts = getState('scripts');
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
      execSync(`node hedera-cli.js ${command}`, { stdio: 'inherit' });
    } catch (error) {
      console.error(`Error executing command: ${command}`);
      console.error(error.message);
      return;
    }
  });

  console.log(`\nScript ${script.name} executed successfully`);
}

