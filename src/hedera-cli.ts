import { program } from 'commander';

import commands from './commands';

program
  .version('1.0.0')
  .description('A CLI tool for managing Hedera environments');

// Commands
commands.setupCommands(program);
commands.networkCommands(program);
commands.accountCommands(program);
commands.recordCommands(program);
commands.loadScriptCommands(program);
commands.backupCommands(program);
commands.tokenCommands(program);

program.parseAsync(process.argv);