import { BalanceResponse } from "../../types/api";
import api from "../api";
import {
  getAccountByIdOrAlias,
  getHederaClient,
  addTokenAssociation,
} from "../state/stateService";
import {
  TokenAssociateTransaction,
  PrivateKey,
  TokenSupplyType,
} from "@hashgraph/sdk";

const getSupplyType = (type: string): TokenSupplyType => {
  const tokenType = type.toLowerCase();
  if (tokenType === "finite") {
    return TokenSupplyType.Finite;
  } else if (tokenType === "infinite") {
    return TokenSupplyType.Infinite;
  } else {
    throw new Error("Invalid supply type");
  }
};

/**
 * @description Checks if a token is associated with an account
 * @param tokenId The token ID
 * @param accountId The account ID
 * @returns True if the token is associated with the account, false otherwise
 * @throws Error if the API call fails
 */
const isTokenAssociated = async (
  tokenId: string,
  accountId: string
): Promise<boolean> => {
  const response = await api.token.getTokenBalance(tokenId, accountId);
  const balanceResponse = response.data as BalanceResponse;

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
  accountIdorAlias: string
): Promise<void> => {
  let account = getAccountByIdOrAlias(accountIdorAlias);

  const client = getHederaClient();
  try {
    // Associate token with account
    const tokenAssociateTx = await new TokenAssociateTransaction()
      .setAccountId(account.accountId)
      .setTokenIds([tokenId])
      .freezeWith(client)
      .sign(PrivateKey.fromString(account.privateKey));

    let tokenAssociateSubmit = await tokenAssociateTx.execute(client);
    await tokenAssociateSubmit.getReceipt(client);

    console.log("Token associated:", tokenId);
    client.close();
  } catch (error) {
    console.log("Failed to associate token:", tokenId);
    console.log(error);
    client.close();
    return;
  }

  // Store association in state for token
  addTokenAssociation(tokenId, account.accountId, account.alias);
  client.close();
};

export { getSupplyType, isTokenAssociated, associateToken };
