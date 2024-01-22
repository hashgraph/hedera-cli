import { PrivateKey, TransferTransaction } from '@hashgraph/sdk';

import stateUtils from './state';
import { Logger } from '../utils/logger';
import signUtils from './sign';

const logger = Logger.getInstance();

async function transfer(amount: number, from: string, to: string): Promise<void> {
    // Find sender account
    let fromAccount = stateUtils.getAccountByIdOrAlias(from);
    let fromId = fromAccount.accountId;
  
    // Find receiver account
    let toAccount = stateUtils.getAccountByIdOrAlias(to);
    let toId = toAccount.accountId;
  
    const client = stateUtils.getHederaClient();
    try {
      const transferTx = new TransferTransaction()
        .addHbarTransfer(fromId, amount * -1)
        .addHbarTransfer(toId, amount)
        .freezeWith(client);
  
      const transferTxSign = await signUtils.sign(transferTx, fromAccount.privateKey);
  
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
      logger.error('Unable to transfer hbar', error as object);
    }
  
    client.close();
  }

  const hbarUtils = {
    transfer,
  };
  
  export default hbarUtils;

