import {
  TokenAssociateTransaction,
  TokenSupplyType,
  TransferTransaction,
} from '@hashgraph/sdk';

import { Logger } from './logger';
import { DomainError } from './errors';
import api from '../api';
import stateUtils from '../utils/state';
import signUtils from '../utils/sign';

const logger = Logger.getInstance();

const getSupplyType = (type: string): TokenSupplyType => {
  const tokenType = type.toLowerCase();
  if (tokenType === 'finite') {
    return TokenSupplyType.Finite;
  } else if (tokenType === 'infinite') {
    return TokenSupplyType.Infinite;
  } else {
    throw new DomainError('Invalid supply type');
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
  accountIdorName: string,
): Promise<void> => {
  const account = stateUtils.getAccountByIdOrName(accountIdorName);

  const client = stateUtils.getHederaClient();
  try {
    // Associate token with account
    const tokenAssociateTx = new TokenAssociateTransaction()
      .setAccountId(account.accountId)
      .setTokenIds([tokenId])
      .freezeWith(client);

    const signedTokenAssociateTx = await signUtils.sign(
      tokenAssociateTx,
      account.privateKey,
    );

    const tokenAssociateSubmit = await signedTokenAssociateTx.execute(client);
    await tokenAssociateSubmit.getReceipt(client);

    logger.log(`Token associated: ${tokenId}`);
  } catch (error) {
    client.close();
    throw new DomainError(`Failed to associate token: ${tokenId}`);
  }

  // Store association in state for token
  stateUtils.addTokenAssociation(tokenId, account.accountId, account.name);
  client.close();
};

const transfer = async (
  tokenId: string,
  fromId: string,
  fromPrivateKey: string,
  toId: string,
  balance: number,
) => {
  const client = stateUtils.getHederaClient();
  try {
    const transferTx = new TransferTransaction()
      .addTokenTransfer(tokenId, fromId, balance * -1)
      .addTokenTransfer(tokenId, toId, balance)
      .freezeWith(client);

    const transferTxSign = await signUtils.sign(transferTx, fromPrivateKey);

    const submittedTransfer = await transferTxSign.execute(client);
    const receipt = await submittedTransfer.getReceipt(client);
    if (receipt.status._code === 22) {
      logger.log(
        `Transfer successful with tx ID: ${submittedTransfer.transactionId.toString()}`,
      );
    } else {
      throw new DomainError(
        `Transfer failed with tx ID: ${submittedTransfer.transactionId.toString()}`,
      );
    }
  } catch (error) {
    logger.error('Unable to transfer token', error as object);
  }

  client.close();
};

const tokenUtils = {
  getSupplyType,
  isTokenAssociated,
  associateToken,
  transfer,
};
export default tokenUtils;
