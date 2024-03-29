import axios from 'axios';
import type { APIResponse, AccountResponse } from '../../types';
import stateUtils from '../utils/state';
import { Logger } from '../utils/logger';

const logger = Logger.getInstance();

/**
 * API functions:
 * - getAccountInfo(accountId): Get the info of an account
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
    process.exit(1);
  }
}

export default {
  getAccountInfo,
};
