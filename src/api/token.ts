import axios from 'axios';
import type { APIResponse, BalanceResponse, TokenResponse } from '../../types';
import { fail } from '../utils/errors';
import { Logger } from '../utils/logger';
import stateUtils from '../utils/state';

const logger = Logger.getInstance();

/**
 *
 * API functions:
 * - getTokenInfo(tokenId): Get the info of a token
 * - getTokenBalance(tokenId, accountId): Get the balance of a token for an account
 */

async function getTokenInfo(
  tokenId: string,
): Promise<APIResponse<TokenResponse>> {
  try {
    const mirrorNodeURL = stateUtils.getMirrorNodeURL();
    const fullUrl = `${mirrorNodeURL}/tokens/${tokenId}`;

    // Debug logging
    logger.debug(`Calling mirror node URL: ${fullUrl}`);
    logger.debug(`Mirror node base URL: ${mirrorNodeURL}`);
    logger.debug(`Token ID: ${tokenId}`);

    const response = await axios.get(fullUrl, {
      timeout: 5000, // 5 second timeout
    });
    return response;
  } catch (error) {
    if (axios.isAxiosError(error)) {
      logger.debug(`Axios error - ${error.code}: ${error.message}`);
      logger.error(`Resource ${tokenId} doesn't exist. ${error.message}`);
    } else {
      logger.debug(`Unexpected error:`, error);
      logger.error('Unexpected error:', error as object);
    }
    fail('Failed to fetch token info');
  }
}

async function getTokenBalance(
  tokenId: string,
  accountId: string,
): Promise<APIResponse<BalanceResponse>> {
  try {
    const mirrorNodeURL = stateUtils.getMirrorNodeURL();
    const fullUrl = `${mirrorNodeURL}/accounts/${accountId}/tokens?token.id=${tokenId}`;

    // Debug logging
    logger.debug(`Calling mirror node URL: ${fullUrl}`);
    logger.debug(`Mirror node base URL: ${mirrorNodeURL}`);
    logger.debug(`Token ID: ${tokenId}, Account ID: ${accountId}`);

    const response = await axios.get(fullUrl, {
      timeout: 5000, // 5 second timeout
    });
    return response;
  } catch (error) {
    if (axios.isAxiosError(error)) {
      logger.debug(`Axios error - ${error.code}: ${error.message}`);
      logger.error(`Resource ${tokenId} doesn't exist. ${error.message}`);
    } else {
      logger.debug(`Unexpected error:`, error);
      logger.error('Unexpected error:', error as object);
    }
    fail('Failed to fetch token balance');
  }
}

export default {
  getTokenInfo,
  getTokenBalance,
};
