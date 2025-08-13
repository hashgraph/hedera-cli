import { Command } from 'commander';
import { heading, success } from '../../utils/color';
import { Logger } from '../../utils/logger';
import { isJsonOutput, printOutput } from '../../utils/output';
import scriptUtils from '../../utils/script';
import { telemetryPreAction } from '../shared/telemetryHook';
import { wrapAction } from '../shared/wrapAction';

const logger = Logger.getInstance();

export default (program: Command) => {
  program
    .command('delete')
    .hook('preAction', telemetryPreAction)
    .description('Delete a script')
    .requiredOption('-n, --name <name>', 'Name of script to delete')
    .action(
      wrapAction<ScriptDeleteOptions>(
        (options) => {
          scriptUtils.deleteScript(options.name);
          if (isJsonOutput()) {
            printOutput('scriptDelete', { name: options.name });
          } else {
            logger.log(
              heading('Script deleted:') + ' ' + success(options.name),
            );
          }
        },
        { log: (o) => `Deleting script: ${o.name}` },
      ),
    )
    .addHelpText(
      'afterAll',
      '\nExamples:\n  $ hedera script delete -n setup-env\n  $ hedera script delete -n setup-env --json',
    );
};

interface ScriptDeleteOptions {
  name: string;
}
