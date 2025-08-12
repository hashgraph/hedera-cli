import { program } from 'commander';
import commands from './commands';
import { installGlobalErrorHandlers } from './utils/errors';
import { Logger } from './utils/logger';

// eslint-disable-next-line @typescript-eslint/no-var-requires
const pkg = require('../package.json') as { version?: string };
const logger = Logger.getInstance();

program
  .version(pkg.version || '0.0.0')
  .description('A CLI tool for managing Hedera environments')
  .option('-v, --verbose', 'Enable verbose logging')
  .option('-q, --quiet', 'Quiet mode (only errors)')
  .option(
    '--log-mode <mode>',
    'Explicit log mode (normal|verbose|quiet|silent)',
  );

// Ensure logging mode is applied before any command action executes.
program.hook('preAction', () => {
  const opts = program.opts<{
    verbose?: boolean;
    quiet?: boolean;
    logMode?: string;
  }>();
  if (opts.logMode) {
    const mode = opts.logMode as 'verbose' | 'quiet' | 'normal' | 'silent';
    if (mode === 'silent') logger.setMode('silent');
    else if (mode === 'verbose' || mode === 'quiet' || mode === 'normal')
      logger.setLevel(mode);
  } else if (opts.verbose) {
    logger.setLevel('verbose');
  } else if (opts.quiet) {
    logger.setLevel('quiet');
  }
});

// Auto-register all exported command registrar functions
Object.values(commands).forEach((register) => {
  if (typeof register === 'function') {
    register(program);
  }
});

installGlobalErrorHandlers();

program.parseAsync(process.argv);
