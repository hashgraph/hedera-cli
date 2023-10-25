import * as path from "path";
import {
  TokenCreateTransaction,
  TokenType,
  PrivateKey,
  TokenSupplyType,
  TokenAssociateTransaction,
} from "@hashgraph/sdk";

import { myParseInt } from "../utils/verification";
import { createAccount } from "../utils/account";
import { getSupplyType } from "../utils/token";
import { recordCommand, getAccountById, getAccountByAlias, getHederaClient } from "../state/stateService";
import { saveStateAttribute, getState } from "../state/stateController";

import type { Account, Command, Token } from "../../types";

export default (program: any) => {
  const token = program.command("token").description("Create a new token");

  token
    .command("create-from-file")
    .hook("preAction", (thisCommand: Command) => {
      const command = [
        thisCommand.parent.action().name(),
        ...thisCommand.parent.args,
      ];
      recordCommand(command);
    })
    .description("Create a new token from a file")
    .requiredOption(
      "-f, --file <filename>",
      "Filename containing the token information"
    )
    .action(async (options: CreateTokenFromFileOptions) => {
      try {
        const filepath = path.join(
          __dirname,
          "..",
          "input",
          `token.${options.file}.json`
        );
        const tokenDefinition = require(filepath);
        await createTokenFromFile(tokenDefinition);
      } catch (error) {
        console.log(error);
      }
    });

  // alias is also possible for --acount-id
  token
    .command("associate")
    .hook("preAction", (thisCommand: Command) => {
      const command = [
        thisCommand.parent.action().name(),
        ...thisCommand.parent.args,
      ];
      recordCommand(command);
    })
    .description("Associate a token with an account")
    .requiredOption(
      "-a, --account-id <accountId>",
      "Account ID or account alias to associate with token"
    )
    .requiredOption(
      "-t, --token-id <tokenId>",
      "Token ID to associate with account"
    )
    .action(async (options: AssociateTokenOptions) => {
      const tokenId = options.tokenId;
      const accountIdorAlias = options.accountId;
      const client = getHederaClient();

      // Get account by ID or find alias
      let account;
      const accountIdPattern = /^0\.0\.\d+$/;
      const match = accountIdorAlias.match(accountIdPattern);
      if (match) {
        account = getAccountById(accountIdorAlias);
      } else {
        account = getAccountByAlias(accountIdorAlias);
      }      

      if (!account) {
        console.log("Account not found:", accountIdorAlias);
        client.close();
        return;
      }

      try {
        // Associate token with account
        const tokenAssociateTx = await new TokenAssociateTransaction()
          .setAccountId(account.accountId)
          .setTokenIds([tokenId])
          .freezeWith(client)
          .sign(PrivateKey.fromString(account.privateKey));

        let tokenAssociateSubmit = await tokenAssociateTx.execute(client);
        await tokenAssociateSubmit.getReceipt(client);

        console.log("Token associated:", options.tokenId);
      } catch (error) {
        console.log("Failed to associate token:", options.tokenId);
        console.log(error);
      }

      client.close();
    });

  token
    .command("create-ft")
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
    /*.addOption(
      new Option(
        "-s, --supply-type <supplyType>", 
        "Supply type of the token",
      )
      .choices(["finite", "infinite"])
    )*/
    .requiredOption(
      "-a, --admin-key <adminKey>",
      "Admin key of the fungible token"
    )
    // make optional later
    .action(async (options: CreateFTOptions) => {
      try {
        await createFungibleToken(
          options.name,
          options.symbol,
          options.treasuryId,
          options.treasuryKey,
          options.decimals,
          options.initialSupply,
          "infinite",
          options.adminKey
        );
      } catch (error) {
        console.log(error);
      }
    });
};

async function createTokenFromFile(token: TokenInput) {
  const client = getHederaClient();
  const type = getSupplyType(token.supplyType);

  // move this to an interpretation module
  const keys: Keys = {
    adminKey: "",
    supplyKey: "",
    wipeKey: "",
    kycKey: "",
    freezeKey: "",
    pauseKey: "",
    feeScheduleKey: "",
    treasuryKey: "",
  };

  token.treasuryId = "";

  // Look for alias pattern in keys
  const accounts = getState("accounts");
  const aliasPattern = /<alias:([a-zA-Z0-9_-]+)>/;
  Object.keys(keys).forEach((key) => {
    if (!(key in token)) return;

    const match = token[key].match(aliasPattern);
    if (match) {
      const alias = match[1];
      if (accounts[alias]) {
        keys[key] = accounts[alias].privateKey; // Provide private key in keys object

        if (key === "treasuryKey") {
          token.treasuryId = accounts[alias].accountId;
        }
      }
    }
  });

  // Look for `newkey` pattern in keys
  let newAccountPromises: Promise<{ key: string; account: Account }>[] = [];
  const newKeyPattern = /<newkey:(\d+)>/;
  Object.keys(keys).forEach(async (key) => {
    if (!(key in token)) return;

    const match = token[key].match(newKeyPattern);
    if (match) {
      const initialBalance = Number(match[1]);
      newAccountPromises.push(
        createAccountToken(key, initialBalance, "ecdsa", "random")
      );
    }
  });

  // Create new accounts
  if (newAccountPromises.length > 0) {
    try {
      const newAccounts = await Promise.all(newAccountPromises);
      newAccounts.forEach((newAccount) => {
        keys[newAccount.key] = newAccount.account.privateKey;
        if (newAccount.key === "treasuryKey") {
          token.treasuryId = newAccount.account.accountId;
        }
      });
    } catch (error) {
      throw new Error(
        `Failed to create new accounts for new token: ${token.name}`
      );
    }
  }

  // Create token
  let tokenId;
  try {
    const tokenCreateTx = new TokenCreateTransaction()
      .setTokenName(token.name)
      .setTokenSymbol(token.symbol)
      .setDecimals(token.decimals)
      .setInitialSupply(token.initialSupply)
      .setTokenType(TokenType.FungibleCommon)
      .setSupplyType(type)
      .setTreasuryAccountId(token.treasuryId);

    if (type === TokenSupplyType.Finite) {
      tokenCreateTx.setMaxSupply(token.maxSupply);
    }

    // Add keys
    if (keys.adminKey !== "")
      tokenCreateTx.setAdminKey(PrivateKey.fromString(keys.adminKey).publicKey);
    if (keys.pauseKey !== "")
      tokenCreateTx.setPauseKey(PrivateKey.fromString(keys.pauseKey).publicKey);
    if (keys.kycKey !== "")
      tokenCreateTx.setKycKey(PrivateKey.fromString(keys.kycKey).publicKey);
    if (keys.wipeKey !== "")
      tokenCreateTx.setWipeKey(PrivateKey.fromString(keys.wipeKey).publicKey);
    if (keys.freezeKey !== "")
      tokenCreateTx.setFreezeKey(
        PrivateKey.fromString(keys.freezeKey).publicKey
      );
    if (keys.supplyKey !== "")
      tokenCreateTx.setSupplyKey(
        PrivateKey.fromString(keys.supplyKey).publicKey
      );
    if (keys.feeScheduleKey !== "")
      tokenCreateTx.setFeeScheduleKey(
        PrivateKey.fromString(keys.feeScheduleKey).publicKey
      );

    // Signing
    tokenCreateTx
      .freezeWith(client)
      .sign(PrivateKey.fromString(keys.treasuryKey));

    if (keys.adminKey !== "") {
      tokenCreateTx.sign(PrivateKey.fromString(keys.adminKey));
    }

    let tokenCreateSubmit = await tokenCreateTx.execute(client);
    let tokenCreateRx = await tokenCreateSubmit.getReceipt(client);
    tokenId = tokenCreateRx.tokenId;

    if (tokenId == null) {
      throw new Error("Token was not created");
    }

    console.log("Token ID:", tokenId.toString());
  } catch (error) {
    console.log(error);
    client.close();
    return;
  }

  // Store new token in state
  const tokens: Record<string, Token> = getState("token");
  const updatedTokens = {
    ...tokens,
    [tokenId.toString()]: {
      tokenId: tokenId.toString(),
      name: token.name,
      symbol: token.symbol,
      treasuryId: token.treasuryId,
      treasuryKey: token.treasuryKey,
      decimals: token.decimals,
      initialSupply: token.initialSupply,
      adminKey: keys.adminKey,
      pauseKey: keys.pauseKey,
      kycKey: keys.kycKey,
      wipeKey: keys.wipeKey,
      freezeKey: keys.freezeKey,
      supplyKey: keys.supplyKey,
      feeScheduleKey: keys.feeScheduleKey,
    },
  };

  saveStateAttribute("token", updatedTokens);

  client.close();
}

async function createAccountToken(
  key: string,
  initialBalance: number,
  type: string,
  alias: string
): Promise<{ key: string; account: Account }> {
  const account = await createAccount(initialBalance, type, alias);
  return { key, account };
}

async function createFungibleToken(
  name: string,
  symbol: string,
  treasuryId: string,
  treasuryKey: string,
  decimals: number,
  initialSupply: number,
  supplyType: "finite" | "infinite",
  adminKey: string
) {
  const client = getHederaClient();

  const type = getSupplyType(supplyType);

  let tokenId;
  try {
    const tokenCreateTx = await new TokenCreateTransaction()
      .setTokenName(name)
      .setTokenSymbol(symbol)
      .setDecimals(decimals)
      .setInitialSupply(initialSupply)
      .setTokenType(TokenType.FungibleCommon)
      .setSupplyType(type)
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
    console.log(error);
    client.close();
    return;
  }

  // Store new token in state
  const token: Record<string, Token> = getState("token");
  const updatedToken = {
    ...token,
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

  saveStateAttribute("token", updatedToken);

  client.close();
}

interface CreateFTOptions {
  name: string;
  symbol: string;
  treasuryId: string;
  treasuryKey: string;
  decimals: number;
  initialSupply: number;
  supplyType: "finite" | "infinite";
  adminKey: string;
}

interface CreateTokenFromFileOptions {
  file: string;
}

interface AssociateTokenOptions {
  tokenId: string;
  accountId: string;
}

interface TokenInput {
  name: string;
  symbol: string;
  decimals: number;
  supplyType: "finite" | "infinite";
  initialSupply: number;
  maxSupply: number;
  treasuryId?: string;
  treasuryKey: string;
  customFees: [];
  memo: string;
  [key: string]: any;
}

interface Keys {
  [key: string]: string;
}
