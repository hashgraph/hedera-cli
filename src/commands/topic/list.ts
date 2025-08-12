import { Command } from 'commander';
import { exitOnError } from '../../utils/errors';
import { Logger } from '../../utils/logger';
import topicUtils from '../../utils/topic';
import { telemetryPreAction } from '../shared/telemetryHook';

const logger = Logger.getInstance();

export default (program: Command) => {
  program
    .command('list')
    .hook('preAction', telemetryPreAction)
    .description('List all topics')
    .action(
      exitOnError(() => {
        logger.verbose(`Listing all topic IDs and if they contain keys`);
        topicUtils.list();
      }),
    );
};
