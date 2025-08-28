import axios from 'axios';
import type { APIResponse, AccountResponse } from '../../types';
import { fail } from '../utils/errors';
import { Logger } from '../utils/logger';
import stateUtils from '../utils/state';

const logger = Logger.getInstance();

/**
 * API functions:
 * - getAccountInfo(accountId): Get the info of an account
 * - getAccountInfoByNetwork(accountId, network): Get the info of an account by network
 */
async function getAccountInfo(
  accountId: string,
): Promise<APIResponse<AccountResponse>> {
  try {
    const mirrorNodeURL = stateUtils.getMirrorNodeURL();
    const fullUrl = `${mirrorNodeURL}/accounts/${accountId}`;

    // Debug logging
    logger.debug(`Calling mirror node URL: ${fullUrl}`);
    logger.debug(`Mirror node base URL: ${mirrorNodeURL}`);
    logger.debug(`Account ID: ${accountId}`);

    const response = await axios.get(fullUrl, {
      timeout: 5000, // 5 second timeout
    });
    return response;
  } catch (error) {
    if (axios.isAxiosError(error)) {
      logger.debug(`Axios error - ${error.code}: ${error.message}`);
      logger.error(`Resource ${accountId} doesn't exist. ${error.message}`);
    } else {
      logger.debug(`Unexpected error:`, error);
      logger.error('Unexpected error:', error as object);
    }
    fail('Failed to fetch account info');
  }
}

async function getAccountInfoByNetwork(
  accountId: string,
  network: string,
): Promise<APIResponse<AccountResponse>> {
  try {
    const mirrorNodeURL = stateUtils.getMirrorNodeURLByNetwork(network);
    const fullUrl = `${mirrorNodeURL}/accounts/${accountId}`;

    // Debug logging
    logger.debug(`Calling mirror node URL for network ${network}: ${fullUrl}`);
    logger.debug(`Mirror node base URL: ${mirrorNodeURL}`);
    logger.debug(`Account ID: ${accountId}`);

    const response = await axios.get(fullUrl, {
      timeout: 5000, // 5 second timeout
    });
    return response;
  } catch (error) {
    if (axios.isAxiosError(error)) {
      logger.debug(`Axios error - ${error.code}: ${error.message}`);
      logger.error(`Resource ${accountId} doesn't exist. ${error.message}`);
    } else {
      logger.debug(`Unexpected error:`, error);
      logger.error('Unexpected error:', error as object);
    }
    fail('Failed to fetch account info for network');
  }
}

export default {
  getAccountInfo,
  getAccountInfoByNetwork,
};
