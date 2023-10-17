import { program } from 'commander';

import commands from './commands';
import { Logger } from './utils/logger';

const logger = Logger.getInstance();

program
  .version('1.0.0')
  .description('A CLI tool for managing Hedera environments')
  .option('-v, --verbose', 'output extra debugging')
  .option('-q, --quiet', 'output only errors and warnings');

  if (process.argv.includes('-v')) {
    logger.setLevel('verbose');
  } else if (process.argv.includes('-q')) {
    logger.setLevel('quiet');
  }

  // Commands
commands.setupCommands(program);
commands.networkCommands(program);
commands.accountCommands(program);
commands.recordCommands(program);
commands.loadScriptCommands(program);
commands.backupCommands(program);
commands.tokenCommands(program);

program.parseAsync(process.argv);
