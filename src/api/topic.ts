import axios from 'axios';

import type {
  APIResponse,
  Filter,
  TopicMessageResponse,
  TopicMessagesResponse,
} from '../../types';
import apiUtils from '../utils/api';
import { fail } from '../utils/errors';
import { Logger } from '../utils/logger';
import stateUtils from '../utils/state';

const logger = Logger.getInstance();

/**
 * API functions:
 * - findMessage(topicId, sequenceNumber): Find a message in a topic by sequence number
 */
async function findMessage(
  topicId: string,
  sequenceNumber: number,
): Promise<APIResponse<TopicMessageResponse>> {
  try {
    const mirrorNodeURL = stateUtils.getMirrorNodeURL();
    const response = await axios.get(
      `${mirrorNodeURL}/topics/${topicId}/messages/${sequenceNumber}`,
      {
        timeout: 5000, // 5 second timeout
      },
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
    fail('Failed to find topic message');
  }
}

/**
 * Finds messages in a topic based on provided filters.
 * @param topicId The ID of the topic.
 * @param filters Filters to apply for the search.
 * @note There's a limit for 100 messages per request. TODO: Add pagination.
 * @returns Promise resolving to the API response.
 */
async function getTopicMessages(
  topicId: string,
  filter?: Filter,
): Promise<APIResponse<TopicMessagesResponse>> {
  try {
    const mirrorNodeURL = stateUtils.getMirrorNodeURL();
    const baseUrl = `${mirrorNodeURL}/topics/${topicId}/messages`;
    const fullUrl = filter
      ? apiUtils.constructQueryUrl(baseUrl, [filter])
      : baseUrl;

    // Debug logging
    logger.debug(`Calling mirror node URL: ${fullUrl}`);
    logger.debug(`Mirror node base URL: ${mirrorNodeURL}`);
    logger.debug(
      `Topic ID: ${topicId}, Filter: ${JSON.stringify(filter || {})}`,
    );

    const response = await axios.get(fullUrl, {
      timeout: 5000, // 5 second timeout
    });
    return response;
  } catch (error) {
    if (axios.isAxiosError(error)) {
      logger.debug(`Axios error - ${error.code}: ${error.message}`);
      logger.error(`Resource ${topicId} doesn't exist. ${error.message}`);
    } else {
      logger.debug(`Unexpected error:`, error);
      logger.error('Unexpected error:', error as object);
    }
    fail('Failed to fetch topic messages');
  }
}

export default {
  findMessage,
  getTopicMessages,
};
