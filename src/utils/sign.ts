import { Transaction, PrivateKey } from '@hashgraph/sdk';

import { Logger } from './logger';

const logger = Logger.getInstance();

async function sign(
  transaction: Transaction,
  key: string,
): Promise<Transaction> {
  let signedTx: Transaction;
  try {
    signedTx = await transaction.sign(PrivateKey.fromStringDer(key));
    return signedTx;
  } catch (error) {
    logger.error('Unable to sign transaction', error as object);
    process.exit(1);
  }
}

/*async function signMultiple(transaction: Transaction, type: string, keys: object): Promise<Transaction> {
    let signedTx: Transaction;
    try {
      signedTx = await transaction.sign(
        PrivateKey.fromStringDer(keys),
      );
      return signedTx;
    } catch (error) {
      logger.error('Unable to sign transaction', error as object);
      process.exit(1);
    }

  return signedTx;
}

const signingRequirements = {
    'tokenAssociate':
}*/

const signUtils = {
  sign,
};

export default signUtils;
