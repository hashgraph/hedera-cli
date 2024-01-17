import axios from 'axios';
import type { APIResponse, BalanceResponse, TokenResponse } from '../../types';
import stateUtils from '../utils/state';
import { Logger } from '../utils/logger';

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
    const response = await axios.get(`${mirrorNodeURL}/tokens/${tokenId}`);
    return response;
  } catch (error) {
    if (axios.isAxiosError(error)) {
      logger.error(error.message);
    } else {
      logger.error('Unexpected error:', error as object);
    }
    process.exit(1);
  }
}

async function getTokenBalance(
  tokenId: string,
  accountId: string,
): Promise<APIResponse<BalanceResponse>> {
  try {
    const mirrorNodeURL = stateUtils.getMirrorNodeURL();
    const response = await axios.get(
      `${mirrorNodeURL}/tokens/${tokenId}/balances?account.id=eq:${accountId}`,
    );
    return response;
  } catch (error) {
    if (axios.isAxiosError(error)) {
      logger.error(error.message);
    } else {
      logger.error('Unexpected error:', error as object);
    }
    process.exit(1);
  }
}

export default {
  getTokenInfo,
  getTokenBalance,
};
