import {
  TokenAssociateTransaction,
  TokenSupplyType,
  TransferTransaction,
} from '@hashgraph/sdk';

import { Logger } from './logger';
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
  let account = stateUtils.getAccountByIdOrAlias(accountIdorAlias);

  const client = stateUtils.getHederaClient();
  try {
    // Associate token with account
    const tokenAssociateTx = await new TokenAssociateTransaction()
      .setAccountId(account.accountId)
      .setTokenIds([tokenId])
      .freezeWith(client);

    const signedTokenAssociateTx = await signUtils.sign(
      tokenAssociateTx,
      account.privateKey,
    );

    let tokenAssociateSubmit = await signedTokenAssociateTx.execute(client);
    await tokenAssociateSubmit.getReceipt(client);

    logger.log(`Token associated: ${tokenId}`);
  } catch (error) {
    logger.error(`Failed to associate token: ${tokenId}`, error as object);
    client.close();
    process.exit(1);
  }

  // Store association in state for token
  stateUtils.addTokenAssociation(tokenId, account.accountId, account.alias);
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
    const transferTx = await new TransferTransaction()
      .addTokenTransfer(tokenId, fromId, balance * -1)
      .addTokenTransfer(tokenId, toId, balance)
      .freezeWith(client);

    const transferTxSign = await signUtils.sign(transferTx, fromPrivateKey);

    const transfer = await transferTxSign.execute(client);
    const receipt = await transfer.getReceipt(client);
    if (receipt.status._code === 22) {
      logger.log(
        `Transfer successful with tx ID: ${transfer.transactionId.toString()}`,
      );
    } else {
      logger.error(
        `Transfer failed with tx ID: ${transfer.transactionId.toString()}`,
      );
      process.exit(1);
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
