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

async function signByType(transaction: Transaction, type: string, keys: Record<string, string>): Promise<Transaction> {
    if (!signingRequirements[type]) {
        logger.error('Transaction type is not recognized');
        process.exit(1);
    }

    const signatures = signingRequirements[type].sign; 
    let signedTx: Transaction = transaction;
    for (const signature of signatures) {
        try {
            if (!keys[signature]) continue; // skip iteration if the key is not set
            signedTx = await sign(signedTx, keys[signature]);
        } catch (error) {
            logger.error('Unable to sign transaction', error as object);
            process.exit(1);
        }
    }

    return signedTx;
}

const signingRequirements: Record<string, Record<string, string[]>> = {
    'tokenCreate': {
        sign: ['treasuryKey', 'adminKey'],
    }
}

const signUtils = {
  sign,
  signByType,
};

export default signUtils;
