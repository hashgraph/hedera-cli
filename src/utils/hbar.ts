import { TransferTransaction, Hbar, HbarUnit } from '@hashgraph/sdk';

import stateUtils from './state';
import { Logger } from '../utils/logger';
import { DomainError } from './errors';
import signUtils from './sign';

const logger = Logger.getInstance();

async function transfer(
  amount: number,
  from: string,
  to: string,
  memo: string,
): Promise<void> {
  if (from === to) {
    throw new DomainError('Cannot transfer to the same account');
  }

  // Find sender & receiver accounts
  const fromAccount = stateUtils.getAccountByIdOrName(from);
  const fromId = fromAccount.accountId;
  const toAccount = stateUtils.getAccountByIdOrName(to);
  const toId = toAccount.accountId;

  const client = stateUtils.getHederaClient();
  try {
    const transferTx = new TransferTransaction()
      .addHbarTransfer(fromId, new Hbar(-amount, HbarUnit.Tinybar))
      .addHbarTransfer(toId, new Hbar(amount, HbarUnit.Tinybar))
      .setTransactionMemo(memo)
      .freezeWith(client);

    const transferTxSign = await signUtils.sign(
      transferTx,
      fromAccount.privateKey,
    );

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
    logger.error('Unable to transfer hbar', error as object);
  }

  client.close();
}

const hbarUtils = {
  transfer,
};

export default hbarUtils;
