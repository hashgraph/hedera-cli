import { Transaction, PrivateKey } from '@hashgraph/sdk';
import { DomainError } from './errors';

interface SigningRequirements {
  [action: string]: {
    sign: string[];
  };
}

const signingRequirements: SigningRequirements = {
  tokenCreate: {
    sign: ['treasuryKey', 'adminKey'],
  },
  topicCreate: {
    sign: ['adminKey', 'submitKey'],
  },
};

/**
 * The sign module is responsible for signing transactions
 * @namespace sign
 * @memberof utils
 * @param {Transaction} transaction The transaction to sign
 * @param {string} key The private key to sign the transaction with
 * @returns {Promise<Transaction>} The signed transaction
 */
async function sign(
  transaction: Transaction,
  key: string,
): Promise<Transaction> {
  let signedTx: Transaction;
  try {
    signedTx = await transaction.sign(PrivateKey.fromStringDer(key));
    return signedTx;
  } catch (error) {
    throw new DomainError('Unable to sign transaction');
  }
}

/**
 * The signByType module is responsible for signing transactions by type
 * Each transaction type has a set of keys that need to sign the transaction
 * @namespace sign
 * @memberof utils
 * @param {Transaction} transaction The transaction to sign
 * @param {string} type The type of transaction
 * @param {Record<string, string>} keys The keys to sign the transaction with
 */
async function signByType(
  transaction: Transaction,
  type: string,
  keys: Record<string, string>,
): Promise<Transaction> {
  if (!signingRequirements[type]) {
    throw new DomainError('Transaction type is not recognized');
  }

  const signatures = signingRequirements[type].sign;
  let signedTx: Transaction = transaction;
  for (const signature of signatures) {
    try {
      if (!keys[signature]) continue; // skip iteration if the key is not set
      signedTx = await sign(signedTx, keys[signature]);
    } catch (error) {
      throw new DomainError('Unable to sign transaction');
    }
  }

  return signedTx;
}

const signUtils = {
  sign,
  signByType,
};

export default signUtils;
