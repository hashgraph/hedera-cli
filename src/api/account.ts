import axios from 'axios';
import type { APIResponse, AccountResponse } from '../../types';
import stateUtils from '../utils/state';
import { Logger } from '../utils/logger';
import { fail } from '../utils/errors';

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
    const response = await axios.get(`${mirrorNodeURL}/accounts/${accountId}`);
    return response;
  } catch (error) {
    if (axios.isAxiosError(error)) {
      logger.error(`Resource ${accountId} doesn't exist. ${error.message}`);
    } else {
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
    const response = await axios.get(`${mirrorNodeURL}/accounts/${accountId}`);
    return response;
  } catch (error) {
    if (axios.isAxiosError(error)) {
      logger.error(`Resource ${accountId} doesn't exist. ${error.message}`);
    } else {
      logger.error('Unexpected error:', error as object);
    }
    fail('Failed to fetch account info for network');
  }
}

export default {
  getAccountInfo,
  getAccountInfoByNetwork,
};
