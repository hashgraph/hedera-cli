import axios from 'axios';
import type { APIResponse, TopicResponse } from '../../types';
import stateUtils from '../utils/state';
import { Logger } from '../utils/logger';

const logger = Logger.getInstance();

/**
 * API functions:
 * - findMessage(topicId, sequenceNumber): Find a message in a topic by sequence number
 */
async function findMessage(
  topicId: string,
  sequenceNumber: number,
): Promise<APIResponse<TopicResponse>> {
  try {
    const mirrorNodeURL = stateUtils.getMirrorNodeURL();
    const response = await axios.get(
      `${mirrorNodeURL}/topics/${topicId}/messages/${sequenceNumber}`,
    );
    return response;
  } catch (error) {
    if (axios.isAxiosError(error)) {
      logger.error(
        `Resource ${topicId} doesn't exist or ${sequenceNumber} is too high. ${error.message}`,
      );
    } else {
      logger.error('Unexpected error:', error as object);
    }
    process.exit(1);
  }
}

export default {
  findMessage,
};
