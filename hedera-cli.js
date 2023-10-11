const { program } = require('commander');
const networkCommands = require('./commands/network');
const setupCommands = require('./commands/setup');
const accountCommands = require('./commands/account');
const recordCommands = require('./commands/record');
const loadScriptCommands = require('./commands/loadScript');
const backupCommands = require('./commands/backup');

program
  .version('1.0.0')
  .description('A CLI tool for managing Hedera environments');

// Commands
setupCommands(program);
networkCommands(program);
accountCommands(program);
recordCommands(program);
loadScriptCommands(program);
backupCommands(program);

program.parseAsync(process.argv);