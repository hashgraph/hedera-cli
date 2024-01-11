import axios from 'axios';
import type { APIResponse, AccountResponse } from '../../types';
import { getMirrorNodeURL } from '../state/stateService';
import { Logger } from '../utils/logger';

const logger = Logger.getInstance();

/**
 * API functions:
 * - getAccountBalance(accountId): Get the balance of an account
 */

async function getAccountBalance(accountId: string): Promise<APIResponse<AccountResponse>> {
  try {
    const mirrorNodeURL = getMirrorNodeURL();
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
  getAccountBalance,
};
