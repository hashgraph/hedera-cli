import { Logger } from './logger';
import api from '../api';
import {
  getAccountByIdOrAlias,
  getHederaClient,
  addTokenAssociation,
} from '../state/stateService';

import {
  TokenAssociateTransaction,
  PrivateKey,
  TokenSupplyType,
} from '@hashgraph/sdk';

import { BalanceResponse } from '../../types/api';

const logger = Logger.getInstance();

const getSupplyType = (type: string): TokenSupplyType => {
  const tokenType = type.toLowerCase();
  if (tokenType === 'finite') {
    return TokenSupplyType.Finite;
  } else if (tokenType === 'infinite') {
    return TokenSupplyType.Infinite;
  } else {
    logger.error('Invalid supply type');
    process.exit(1);
  }
};

/**
 * @description Checks if a token is associated with an account
 * @param tokenId The token ID
 * @param accountId The account ID
 * @returns True if the token is associated with the account, false otherwise
 */
const isTokenAssociated = async (
  tokenId: string,
  accountId: string,
): Promise<boolean> => {
  const response = await api.token.getTokenBalance(tokenId, accountId);
  const balanceResponse = response.data;

  // verify if the accountId occurs in the balances array
  for (const balance of balanceResponse.balances) {
    if (balance.account === accountId) {
      return true;
    }
  }

  return false;
};

const associateToken = async (
  tokenId: string,
  accountIdorAlias: string,
): Promise<void> => {
  let account = getAccountByIdOrAlias(accountIdorAlias);

  const client = getHederaClient();
  try {
    // Associate token with account
    const tokenAssociateTx = await new TokenAssociateTransaction()
      .setAccountId(account.accountId)
      .setTokenIds([tokenId])
      .freezeWith(client)
      .sign(PrivateKey.fromStringDer(account.privateKey));

    let tokenAssociateSubmit = await tokenAssociateTx.execute(client);
    await tokenAssociateSubmit.getReceipt(client);

    logger.log(`Token associated: ${tokenId}`);
  } catch (error) {
    logger.error(`Failed to associate token: ${tokenId}`, error as object);
    client.close();
    process.exit(1);
  }

  // Store association in state for token
  addTokenAssociation(tokenId, account.accountId, account.alias);
  client.close();
};

export { getSupplyType, isTokenAssociated, associateToken };
