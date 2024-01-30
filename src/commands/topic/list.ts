import stateUtils from '../../utils/state';
import { Logger } from '../../utils/logger';
import topicUtils from '../../utils/topic';

import type { Command } from '../../../types';

const logger = Logger.getInstance();

export default (program: any) => {
  program
    .command('list')
    .hook('preAction', (thisCommand: Command) => {
      const command = [
        thisCommand.parent.action().name(),
        ...thisCommand.parent.args,
      ];
      stateUtils.recordCommand(command);
    })
    .description('List all topics')
    .action(() => {
      logger.verbose(`Listing all topic IDs and if they contain keys`);
      topicUtils.list();
    });
};
