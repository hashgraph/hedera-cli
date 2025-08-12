import { telemetryPreAction } from '../shared/telemetryHook';
import { Logger } from '../../utils/logger';
import topicUtils from '../../utils/topic';
import { Command } from 'commander';
import { exitOnError } from '../../utils/errors';

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
