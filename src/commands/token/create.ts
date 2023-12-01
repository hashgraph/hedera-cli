import {
  TokenCreateTransaction,
  TokenType,
  PrivateKey,
  TokenSupplyType,
} from "@hashgraph/sdk";

import { myParseInt } from "../../utils/verification";
import { getSupplyType } from "../../utils/token";
import {
  recordCommand,
  getHederaClient,
} from "../../state/stateService";
import { Logger } from "../../utils/logger";
import stateController from "../../state/stateController";

import type { Command, Token } from "../../../types";

const logger = Logger.getInstance();

export default (program: any) => {
  program
    .command("create")
    .hook("preAction", (thisCommand: Command) => {
      const command = [
        thisCommand.parent.action().name(),
        ...thisCommand.parent.args,
      ];
      recordCommand(command);
    })
    .description("Create a new fungible token")
    .requiredOption(
      "-t, --treasury-id <treasuryId>",
      "Treasury of the fungible token"
    )
    .requiredOption(
      "-k, --treasury-key <treasuryKey>",
      "Treasury of the fungible token"
    )
    .requiredOption("-n, --name <name>", "Name of the fungible token")
    .requiredOption("-s, --symbol <symbol>", "Symbol of the fungible token")
    .requiredOption(
      "-d, --decimals <decimals>",
      "Decimals of the fungible token",
      myParseInt
    )
    .requiredOption(
      "-i, --initial-supply <initialSupply>",
      "Initial supply of the fungible token",
      myParseInt
    )
    .requiredOption(
      "--supply-type <supplyType>",
      "Supply type of the token: finite or infinite"
    )
    .requiredOption(
      "-a, --admin-key <adminKey>",
      "Admin key of the fungible token"
    )
    .action(async (options: CreateOptions) => {
      try {
        await createFungibleToken(
          options.name,
          options.symbol,
          options.treasuryId,
          options.treasuryKey,
          options.decimals,
          options.initialSupply,
          options.supplyType,
          options.adminKey
        );
      } catch (error) {
        logger.error(error as object);
      }
    });
};

async function createFungibleToken(
  name: string,
  symbol: string,
  treasuryId: string,
  treasuryKey: string,
  decimals: number,
  initialSupply: number,
  supplyType: string,
  adminKey: string
) {
  const client = getHederaClient();

  let tokenId;
  try {
    const tokenCreateTx = await new TokenCreateTransaction()
      .setTokenName(name)
      .setTokenSymbol(symbol)
      .setDecimals(decimals)
      .setInitialSupply(initialSupply)
      .setTokenType(TokenType.FungibleCommon)
      .setSupplyType(getSupplyType(supplyType))
      .setTreasuryAccountId(treasuryId)
      .setAdminKey(PrivateKey.fromString(adminKey).publicKey)
      .freezeWith(client)
      .sign(PrivateKey.fromString(treasuryKey));

    let tokenCreateSubmit = await tokenCreateTx.execute(client);
    let tokenCreateRx = await tokenCreateSubmit.getReceipt(client);
    tokenId = tokenCreateRx.tokenId;

    if (tokenId == null) {
      throw new Error("Token was not created");
    }

    console.log("Token ID:", tokenId.toString());
  } catch (error) {
    logger.error(error as object);
    client.close();
    return;
  }

  // Store new token in state
  const tokens: Record<string, Token> = stateController.get("tokens");
  const updatedTokens = {
    ...tokens,
    [tokenId.toString()]: {
      tokenId: tokenId.toString(),
      name,
      symbol,
      treasuryId,
      treasuryKey,
      decimals,
      initialSupply,
      adminKey,
    },
  };

  stateController.saveKey("tokens", updatedTokens);

  client.close();
}

interface CreateOptions {
  name: string;
  symbol: string;
  treasuryId: string;
  treasuryKey: string;
  decimals: number;
  initialSupply: number;
  supplyType: "finite" | "infinite";
  adminKey: string;
}
