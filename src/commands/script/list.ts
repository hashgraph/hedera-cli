import { Command } from 'commander';
import scriptUtils from '../../utils/script';
import { telemetryPreAction } from '../shared/telemetryHook';
import { wrapAction } from '../shared/wrapAction';

export default (program: Command) => {
  program
    .command('list')
    .hook('preAction', telemetryPreAction)
    .description('List all scripts')
    .action(
      wrapAction(
        () => {
          scriptUtils.listScripts();
        },
        { log: 'Listing all script names' },
      ),
    );
};
