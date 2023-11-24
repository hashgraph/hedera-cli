import {
  PrivateKey,
  TransferTransaction,
} from "@hashgraph/sdk";

import { myParseInt } from "../../utils/verification";
import {
  recordCommand,
  getHederaClient,
  getAccountByIdOrAlias,
} from "../../state/stateService";

import type { Command } from "../../../types";

export default (program: any) => {
  program
    .command("transfer")
    .hook("preAction", (thisCommand: Command) => {
      const command = [
        thisCommand.parent.action().name(),
        ...thisCommand.parent.args,
      ];
      recordCommand(command);
    })
    .description("Transfer a fungible token")
    .requiredOption("-t, --token-id <tokenId>", "Token ID to transfer")
    .requiredOption("--to <to>", "Account ID to transfer token to")
    .requiredOption("--from <from>", "Account ID to transfer token from")
    .requiredOption(
      "-b, --balance <balance>",
      "Amount of token to transfer",
      myParseInt
    )
    .action(async (options: TransferTokenOptions) => {
      const tokenId = options.tokenId;
      const toIdOrAlias = options.to;
      const fromIdOrAlias = options.from;
      const balance = options.balance;

      // Find sender account
      let fromAccount = getAccountByIdOrAlias(fromIdOrAlias);
      let fromId = fromAccount.accountId;

      // Find receiver account
      let toAccount = getAccountByIdOrAlias(toIdOrAlias);
      let toId = toAccount.accountId;

      const client = getHederaClient();
      try {
        const transferTx = await new TransferTransaction()
          .addTokenTransfer(tokenId, fromId, balance * -1)
          .addTokenTransfer(tokenId, toId, balance)
          .freezeWith(client);

        const transferTxSign = await transferTx.sign(
          PrivateKey.fromString(fromAccount.privateKey)
        );

        const receipt = await transferTxSign.execute(client);
        console.log(
          "Transfer successful, tx ID",
          receipt.transactionId.toString()
        );
      } catch (error) {
        console.log(error);
      }

      client.close();
    });
};

interface TransferTokenOptions {
  tokenId: string;
  to: string;
  from: string;
  balance: number;
}
