import { PrivateKey, TransferTransaction } from '@hashgraph/sdk';

import {
  recordCommand,
  getHederaClient,
  getAccountByIdOrAlias,
} from '../state/stateService';
import stateController from '../state/stateController';
import enquirerUtils from '../utils/enquirer';
import dynamicVariablesUtils from '../utils/dynamicVariables';
import { Logger } from '../utils/logger';

import type { Command } from '../../types';

const logger = Logger.getInstance();

export default (program: any) => {
  const hbar = program.command('hbar');

  hbar
    .command('transfer')
    .hook('preAction', (thisCommand: Command) => {
      const command = [
        thisCommand.parent.action().name(),
        ...thisCommand.parent.args,
      ];
      recordCommand(command);
    })
    .description('Transfer hbar between accounts')
    .requiredOption('-b, --balance <balance>', 'Amount of hbar to transfer')
    .option('-t, --to <to>', 'Account ID to transfer hbar to')
    .option('-f, --from <from>', 'Account ID to transfer hbar from')
    .action(async (options: HbarTransferOptions) => {
      logger.verbose('Transferring hbar');
      options = dynamicVariablesUtils.replaceOptions(options);

      let to = options.to;
      let from = options.from;

      if (!options.from) {
        try {
          const accounts = Object.keys(stateController.getAll().accounts);
          if (accounts.length === 0) {
            logger.error('No accounts found to transfer hbar from. Please create an account first.');
            process.exit(1);
          }
          from = await enquirerUtils.createPrompt(accounts, 'Choose account to transfer hbar from:');
        } catch (error) {
          logger.error('Unable to get response:', error as object);
          process.exit(1);
        }
      }

      if (!options.to) {
        try {
          const accounts = Object.keys(stateController.getAll().accounts);
          if (accounts.length === 0) {
            logger.error('No accounts found to transfer hbar from. Please create an account first.');
            process.exit(1);
          }
          to = await enquirerUtils.createPrompt(accounts, 'Choose account to transfer hbar to:');
        } catch (error) {
          logger.error('Unable to get response:', error as object);
          process.exit(1);
        }
      }

      await transferHbar(Number(options.balance), from, to);
    });
};

async function transferHbar(amount: number, from: string, to: string) {
  // Find sender account
  let fromAccount = getAccountByIdOrAlias(from);
  let fromId = fromAccount.accountId;

  // Find receiver account
  let toAccount = getAccountByIdOrAlias(to);
  let toId = toAccount.accountId;

  const client = getHederaClient();
  try {
    const transferTx = new TransferTransaction()
      .addHbarTransfer(fromId, amount * -1)
      .addHbarTransfer(toId, amount)
      .freezeWith(client);

    const transferTxSign = await transferTx.sign(
      PrivateKey.fromStringDer(fromAccount.privateKey),
    );

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

interface HbarTransferOptions {
  balance: number;
  to: string;
  from: string;
}
