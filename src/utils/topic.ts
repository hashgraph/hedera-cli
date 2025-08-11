import { selectTopics } from '../state/selectors';
import { Logger } from './logger';

import type { Topic } from '../../types';

const logger = Logger.getInstance();

function list() {
  const topics: Topic[] = Object.values(selectTopics());

  if (topics.length === 0) {
    logger.log('No topics found');
    return;
  }

  logger.log('Topics:');
  topics.forEach((topic) => {
    logger.log(`\tTopic ID: ${topic.topicId}`);
    logger.log(`\t\t- Submit key: ${topic.submitKey ? 'Yes' : 'No'}`);
    logger.log(`\t\t- Admin key: ${topic.adminKey ? 'Yes' : 'No'}`);
  });
}

const topicUtils = {
  list,
};

export default topicUtils;
