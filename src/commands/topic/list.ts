import { telemetryPreAction } from '../shared/telemetryHook';
import { Logger } from '../../utils/logger';
import topicUtils from '../../utils/topic';
import { Command } from 'commander';

const logger = Logger.getInstance();

export default (program: Command) => {
  program
    .command('list')
    .hook('preAction', telemetryPreAction)
    .description('List all topics')
    .action(() => {
      logger.verbose(`Listing all topic IDs and if they contain keys`);
      topicUtils.list();
    });
};
