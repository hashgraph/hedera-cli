import axios from 'axios';

import type {
  APIResponse,
  TopicMessageResponse,
  TopicMessagesResponse,
  Filter,
} from '../../types';
import stateUtils from '../utils/state';
import apiUtils from '../utils/api';
import { Logger } from '../utils/logger';
import { fail } from '../utils/errors';

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
async function findMessagesWithFilters(
  topicId: string,
  filters: Filter[],
): Promise<APIResponse<TopicMessagesResponse>> {
  try {
    const mirrorNodeURL = stateUtils.getMirrorNodeURL();
    const baseUrl = `${mirrorNodeURL}/topics/${topicId}/messages`;
    const fullUrl = apiUtils.constructQueryUrl(baseUrl, filters);

    const response = await axios.get(fullUrl);
    return response;
  } catch (error) {
    if (axios.isAxiosError(error)) {
      logger.error(
        `Failed to find messages for topic ${topicId} with filters. ${error.message}`,
      );
    } else {
      logger.error('Unexpected error:', error as object);
    }
    fail('Failed to find topic messages with filters');
  }
}

export default {
  findMessage,
  findMessagesWithFilters,
};
