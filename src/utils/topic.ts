import { selectTopics } from '../state/selectors';
import { color, heading } from './color';
import { Logger } from './logger';
import { isJsonOutput, printOutput } from './output';

import type { Topic } from '../../types';

const logger = Logger.getInstance();

function list() {
  const topics: Topic[] = Object.values(selectTopics());
  if (isJsonOutput()) {
    printOutput('topics', {
      topics: topics.map((t) => ({
        topicId: t.topicId,
        hasSubmitKey: Boolean(t.submitKey),
        hasAdminKey: Boolean(t.adminKey),
        memo: t.memo,
        network: t.network,
      })),
    });
    return;
  }

  if (topics.length === 0) {
    logger.log(heading('No topics found'));
    return;
  }

  logger.log(heading('Topics:'));
  topics.forEach((topic) => {
    logger.log(`\t${color.magenta('Topic ID:')} ${color.cyan(topic.topicId)}`);
    logger.log(
      `\t\t- Submit key: ${topic.submitKey ? color.green('Yes') : color.red('No')}`,
    );
    logger.log(
      `\t\t- Admin key: ${topic.adminKey ? color.green('Yes') : color.red('No')}`,
    );
  });
}

const topicUtils = {
  list,
};

export default topicUtils;
