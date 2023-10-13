const { program } = require("commander");
const {
  TokenCreateTransaction,
  TokenType,
  PrivateKey,
  TokenSupplyType,
} = require("@hashgraph/sdk");

const { getHederaClient } = require("../state/stateService");
const { myParseInt } = require("../utils/verification");
const { getSupplyType } = require("../utils/token");
const { recordCommand } = require("../state/stateService");
const { saveStateAttribute, getState } = require("../state/stateController");

module.exports = () => {
  const token = program.command("token").description("Create a new token");

  token
    .command("create-ft")
    .hook("preAction", (thisCommand) => {
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
    .option("-s, --supply-type <supplyType>", "Supply type of the token", "infinite")
    .requiredOption(
      "-a, --admin-key <adminKey>",
      "Admin key of the fungible token"
    )
    // make optional later
    .action(async (options) => {
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
        console.log(error);
      }
    });

  token
    .command("create-nft")
    .hook("preAction", (thisCommand) => {
      const command = [
        thisCommand.parent.action().name(),
        ...thisCommand.parent.args,
      ];
      recordCommand(command);
    })
    .description("Create a new non-fungible token")
    .option("-n, --name <name>", "Name of the non-fungible token")
    .option("-s, --symbol <symbol>", "Symbol of the non-fungible token")
    .action((options) => {});

    // I need to send this from the perspective of the user account?
    token
        .command("associate")
        .description("Associate a token with an account")
        .requiredOption("-a, --account-id <accountId>", "Account ID")
        .action((options) => {
            
        });
};

async function createFungibleToken(
  name,
  symbol,
  treasuryId,
  treasuryKey,
  decimals,
  initialSupply,
  supplyType,
  adminKey
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
    
    console.log("Token ID:", tokenId.toString());
  } catch (error) {
    console.log(error);
    client.close();
  }

  // Store new token in state
  const token = getState("token");
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
