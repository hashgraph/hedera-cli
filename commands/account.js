const { program } = require("commander");
const {
  PrivateKey,
  AccountCreateTransaction,
  Hbar,
  AccountId,
} = require("@hashgraph/sdk");

const { getState, saveStateAttribute } = require("../state/stateController");
const { getHederaClient } = require("../state/stateService");
const { myParseInt } = require("../utils/verification");
const { recordCommand } = require("../state/stateService");
const { displayBalances } = require("../utils/balance");
const api = require("../api");

module.exports = () => {
  const account = program.command("account");

  account
    .command("create")
    .hook("preAction", (thisCommand) => {
      const command = [
        thisCommand.parent.action().name(),
        ...thisCommand.parent.args,
      ];
      recordCommand(command);
    })
    .description(
      "Create a new Hedera account using NEW recovery words and keypair. This is default."
    )
    .requiredOption("-a, --alias <alias>", "account must have an alias")
    .option(
      "-b, --balance <balance>",
      "Initial balance in tinybars",
      myParseInt,
      1000
    )
    .option(
      "-t, --type <type>",
      "Type of account to create (ECDSA or ED25519)",
      "ED25519"
    )
    .action(async (options) => {
      try {
        await createAccount(options.balance, options.type, options.alias);
      } catch (error) {
        console.log(error);
      }
    });

  account
    .command("balance <accountIdOrAlias>")
    .hook("preAction", (thisCommand) => {
      const command = [
        thisCommand.parent.action().name(),
        ...thisCommand.parent.args,
      ];
      recordCommand(command);
    })
    .description("Retrieve the balance for an account ID or alias")
    .option("-h, --only-hbar", "Show only Hbar balance")
    .option("-t, --token-id <tokenId>", "Show balance for a specific token ID")
    .action(async (accountIdOrAlias, options) => {
      if (options.onlyHbar && options.tokenId) {
        console.error(
          "Error: You cannot use both --only-hbar and --token-id options at the same time."
        );
        return;
      }

      try {
        await getAccountBalance(accountIdOrAlias, options);
      } catch (error) {
        console.log(error);
      }
    });

  account
    .command("ls")
    .hook("preAction", (thisCommand) => {
      const command = [
        thisCommand.parent.action().name(),
        ...thisCommand.parent.args,
      ];
      recordCommand(command);
    })
    .description("List all accounts in the address book")
    .option("-p, --private", "Show private keys")
    .action((options) => {
      listAccounts(options.private);
    });

  account
    .command("import")
    .hook("preAction", (thisCommand) => {
      const command = [
        thisCommand.parent.action().name(),
        ...thisCommand.parent.args,
      ];
      recordCommand(command);
    })
    .description(
      "Import an existing account using a private key, type, account ID, and alias"
    )
    .requiredOption("-a, --alias <alias>", "account must have an alias")
    .requiredOption("-i, --id <id>", "Account ID")
    .requiredOption("-k, --key <key>", "Private key")
    .action((options) => {
      importAccount(options.id, options.key, options.alias);
    });

  account
    .command("clear")
    .hook("preAction", (thisCommand) => {
      const command = [
        thisCommand.parent.action().name(),
        ...thisCommand.parent.args,
      ];
      recordCommand(command);
    })
    .description("Clear all accounts from the address book")
    .action(() => {
      saveStateAttribute("accounts", {});
    });
};

async function createAccount(balance, type, alias) {
  // Validate balance
  if (isNaN(balance) || balance <= 0) {
    console.error("Invalid balance. Balance must be a positive number.");
    return;
  }

  // Validate type
  if (!["ecdsa", "ed25519"].includes(type.toLowerCase())) {
    console.error('Invalid type. Type must be either "ecdsa" or "ed25519".');
    return;
  }

  // Get client from config
  const accounts = getState("accounts");
  const client = getHederaClient();

  // Generate random alias if "random" is provided
  let isRandomAlias = false;
  if (alias.toLowerCase() === "random") {
    isRandomAlias = true;
    alias = generateRandomAlias(); // Implement this function to generate a random string
  }

  // Check if name is unique
  if (!isRandomAlias && config.accounts && config.accounts[alias]) {
    console.error("An account with this alias already exists.");
    client.close();
    return;
  }

  // Handle different types of account creation
  let newAccountPrivateKey, newAccountPublicKey;
  if (type.toLowerCase() === "ed25519") {
    newAccountPrivateKey = PrivateKey.generateED25519();
    newAccountPublicKey = newAccountPrivateKey.publicKey;
  } else {
    newAccountPrivateKey = PrivateKey.generateECDSA();
    newAccountPublicKey = newAccountPrivateKey.publicKey;
  }

  let newAccountId;
  try {
    const newAccount = await new AccountCreateTransaction()
      .setKey(newAccountPublicKey)
      .setInitialBalance(Hbar.fromTinybars(balance))
      .execute(client);

    // Get the new account ID
    const getReceipt = await newAccount.getReceipt(client);
    newAccountId = getReceipt.accountId;
  } catch (error) {
    console.log("Error creating new account", error);
    client.close();
  }

  // Store the new account in the config
  const newAccountDetails = {
    accountId: newAccountId.toString(),
    type,
    publickey: newAccountPrivateKey.publicKey.toString(),
    evmAddress:
      type.toLowerCase() === "ed25519"
        ? ""
        : newAccountPrivateKey.publicKey.toEvmAddress(),
    solidityAddress:
      type.toLowerCase() === "ed25519" ? "" : newAccountId.toSolidityAddress(),
    solidityAddressFull:
      type.toLowerCase() === "ed25519"
        ? ""
        : `0x${newAccountId.toSolidityAddress()}`,
    privatekey: newAccountPrivateKey.toString(),
  };

  // Add the new account to the accounts object in the config
  const updatedAccounts = { ...accounts, [alias]: newAccountDetails };
  saveStateAttribute("accounts", updatedAccounts);

  // Log the account ID
  console.log("The new account ID is: " + newAccountId);

  client.close();
}

function listAccounts(showPrivateKeys = false) {
  const accounts = getState("accounts");

  // Check if there are any accounts in the config
  if (!accounts || Object.keys(accounts).length === 0) {
    console.log("No accounts found.");
    return;
  }

  // Log details for each account
  console.log("Accounts:");
  for (const [alias, account] of Object.entries(accounts)) {
    console.log(`- Alias: ${alias}`);
    console.log(`  Account ID: ${account.accountId}`);
    console.log(`  Type: ${account.type}`);
    if (showPrivateKeys) {
      console.log(`  Private Key: ${account.privateKey}`);
    }
  }
}

// Write the importAccount function here
function importAccount(id, key, alias) {
  const accounts = getState("accounts");

  // Check if name is unique
  if (accounts && accounts[alias]) {
    console.error("An account with this alias already exists.");
    return;
  }

  let privateKey, type;
  const accountId = AccountId.fromString(id);
  switch (getKeyType(key)) {
    case "ecdsa":
      type = "ECDSA";
      privateKey = PrivateKey.fromStringECDSA(key);
      break;
    case "ed25519":
      type = "ED25519";
      privateKey = PrivateKey.fromStringED25519(key);
      break;
    default:
      console.error(
        "Invalid key type. Only ECDSA and ED25519 keys are supported."
      );
      return;
  }

  // No Solidity and EVM address for ED25519 keys
  const updatedAccounts = { ...accounts };
  updatedAccounts[alias] = {
    accountId: id,
    type,
    publickey: privateKey.publicKey.toString(),
    evmAddress:
      type.toLowerCase() === "ed25519"
        ? ""
        : privateKey.publicKey.toEvmAddress(),
    solidityAddress:
      type.toLowerCase() === "ed25519"
        ? ""
        : privateKey.publicKey.toEvmAddress(),
    solidityAddressFull: `0x${accountId.toSolidityAddress()}`,
    privatekey: key,
  };

  saveStateAttribute("accounts", updatedAccounts);
}

async function getAccountBalance(accountIdOrAlias, options) {
  const accounts = getState("accounts");
  const client = getHederaClient();

  let accountId;

  // Check if input is an alias or an account ID
  if (/^0\.0\.\d+$/.test(accountIdOrAlias)) {
    accountId = accountIdOrAlias;
  } else if (accounts && accounts[accountIdOrAlias]) {
    accountId = accounts[accountIdOrAlias].accountId;
  } else {
    console.error("Invalid account ID or alias not found in address book.");
    client.close();
    return;
  }

  try {
    const response = await api.account.getAccountBalance(accountId);
    displayBalances(response, options);
  } catch (error) {
    console.error("Error fetching account balance:", error.message);
  }

  client.close();
}

function getKeyType(keyString) {
  try {
    PrivateKey.fromStringED25519(keyString);
    return "ed25519";
  } catch (e) {
    // Not an Ed25519 private key
  }

  try {
    PrivateKey.fromStringECDSA(keyString);
    return "ecdsa";
  } catch (e) {
    // Not an ECDSA private key
  }

  return "Unknown key type";
}

function generateRandomAlias() {
  const length = 20; // Define the length of the random string
  const characters =
    "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
  let result = "";
  for (let i = 0; i < length; i++) {
    result += characters.charAt(Math.floor(Math.random() * characters.length));
  }
  return result;
}
