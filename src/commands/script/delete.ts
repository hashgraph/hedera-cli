import { Command } from 'commander';
import scriptUtils from '../../utils/script';
import { telemetryPreAction } from '../shared/telemetryHook';
import { wrapAction } from '../shared/wrapAction';

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
        },
        { log: (o) => `Deleting script: ${o.name}` },
      ),
    );
};

interface ScriptDeleteOptions {
  name: string;
}
