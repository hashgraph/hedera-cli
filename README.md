# Hedera CLI

Welcome to the Hedera CLI Tool, a powerful and intuitive command-line interface designed to streamline your interactions with the Hedera network. Whether you're a developer needing to set up test environments, automate network-related tasks, or explore the extensive capabilities of the Hedera mainnet and testnet, this tool is your one-stop solution.

The Hedera CLI Tool elegantly addresses the complexities associated with distributed ledger technologies. It simplifies the process of executing actions such as creating new accounts, sending transactions, managing tokens, and associating with existing tokens directly from the CLI. This high level of functionality and ease of use significantly reduces the barrier to entry for developers working on Hedera-based projects.

A key advantage of the Hedera CLI Tool is its potential to enhance your workflow. It's not just about performing individual tasks; it's about integrating these tasks into a larger, more efficient development process. With plans for future integration into Continuous Integration/Continuous Deployment (CI/CD) pipelines, this tool promises to be a versatile asset in the automation and management of Hedera network operations.

## Prerequisites

Before proceeding with the installation and setup of the Hedera CLI Tool, ensure the following prerequisites are met:

**1. Node.js Installation:**

The Hedera CLI Tool requires Node.js (version LTS 16.20.2 or higher). You can check your current version by running `node -v`` in your terminal. If you do not have Node.js installed, you can download it from [Node.js official website](https://nodejs.org/en).

**2. Hedera Account Setup:**

You will need an account on the Hedera network to interact with the ledger. Follow these steps to set up your account:

- Visit the [Hedera Portal](https://portal.hedera.com/) and create a new account.
- During the account creation process, you will receive a DER encoded private key and an account ID. The private key corresponds to your `OPERATOR_KEY`, and the account ID is your `OPERATOR_ID`. These credentials are essential for authenticating and performing operations using the Hedera CLI Tool.

Make sure to securely store your DER encoded private key and account ID, as they are crucial for accessing and managing your Hedera account.

## Installation

**1. Install Depencencies:**

Install necessary packages using npm. This sets up everything you need to get started with the Hedera CLI Tool.

```sh
npm install
```

**2. Build the Package:**

Compile the package to ensure all components are ready for use.

```sh
npm run build
```

**3. Set Up Operator Credentials**

Create a .hedera folder in your home directory. This folder will store your configuration files.

```sh
mkdir -p ~/.hedera
cd ~/.hedera
```

Create a `.env` file to securely store your operator credentials.

```sh
touch .env
```

Add the following lines to your `~/.hedera/.env` file, replacing the placeholders with your actual operator ID and key:

```text
OPERATOR_KEY=302e0201003005060[...]
OPERATOR_ID=0.0.12345
```

**4. Verify Installation:**

```sh
node dist/hedera-cli.js account ls
// No accounts found.
```

**5. Optional: Setting Up an Alias**

To avoid typing the full command each time, you can set an alias in your shell profile. For example, in bash or Zshell, you can add the following line to your `.bashrc`/`.bash_profile` or `.zshrc`. Replace the path with the absolute path to your `hedera-cli` installation path.

```sh
alias hcli="node /Users/myUser/hedera-cli/dist/hedera-cli.js"
```

# Commands

Let's explore the different commands, their options, and outputs.

- [Setup Commands](#setup-commands): Instantiate or reset the Hedera CLI tool
- [Network Commands](#network-commands): Switch Hedera networks
- [Account Commands](#account-commands): Create and manage accounts
- [Token Commands](#token-commands): Create and manage tokens
- [Backup Commands](#backup-commands): Create a backup of your state
- [Record Commands](#record-commands): Record CLI interactions and store it in scripts
- [Script Commands](#script-commands): Replay and manage scripts containing recorded CLI interactions


## Setup Commands

### setup

#### Overview

The setup command is an essential component of the Hedera CLI tool, designed to initialize and configure your working environment. This command facilitates the process of setting up the CLI with your operator key and ID.

```sh
setup init
setup reset
```

#### Usage

**1. Initialization:**
Sets up the CLI with the operator key and ID.

```sh
hcli setup init
```

When executed, the setup command performs several key functions:

**Environment Variable Validation:**
It checks if the HOME environment variable is defined and reads `OPERATOR_KEY` and `OPERATOR_ID` from the `~/.hedera/.env` file.

**State Update:**
Once the operator key and ID are validated, these credentials are used to update the `state/state.json` file, which holds the configuration state of the CLI tool.

**2. Reset Setup:**

Depending on the flags provided, it resets the entire state or skips certain parts of the state, such as the accounts, tokens, or scripts sections in your state. This might be useful when you want to reset your state but keep your address book.

```sh
hcli setup reset [-a, --skip-accounts] [-t, --skip-tokens] [-s, --skip-scripts]
```

Flags:
- **-a, --skip-accounts**: (optional) Skips resetting accounts.
- **-t, --skip-tokens:** (optional) Skips resetting tokens.
- **-s, --skip-scripts:** (optional) Skips resetting scripts.

## Network Commands

### Overview

The network command in the Hedera CLI tool is designed to manage and interact with different Hedera networks. It allows users to switch between networks (such as mainnet and testnet) and list available networks. This flexibility is crucial for developers who need to test their applications in different network environments.

```
network use
network list
```

#### Usage

**1. Switching Networks:**

This command switches the current network context to the specified network. 

```sh
hcli network use <name>
```

Replace `<name>` with the name of the network you wish to switch to (e.g., `mainnet`, `testnet`).

**2. Listing Available Networks:**

This command lists all available networks that the CLI tool can interact with. It's useful for confirming the network options and ensuring correct network names are used when switching networks.

```sh
hcli network list
// Available networks: mainnet, testnet
```

#### Description

The network command includes a catch-all for unknown subcommands. If an unrecognized command is entered, it triggers an error message and displays the help text for the network command.

```sh
// Invalid network name. Available networks: mainnet, testnet
```

## Account Commands

### Overview

The `account` command in the Hedera CLI tool encompasses a suite of subcommands designed for managing Hedera accounts. These subcommands provide functionalities for creating new accounts, retrieving account balances, listing accounts in the address book, importing existing accounts, and clearing the address book.

```
account create
account balance
account list
account import
account clear
account delete
```

#### Usage

**1. Create a New Account:**

Initializes a new Hedera account with a specified alias, balance, and type. The balance and type are optional and have defaults if not specified.

```sh
hcli account create -a,--alias <alias> [-b,--balance <balance>] [-t,--type <type>]
```

Flags:
- **Alias:** (required) A unique identifier for the new account. 
- **Balance:** (optional) Initial balance in tinybars. Defaults to 1000.
- **Type:** (optional) The account type (`ECDSA` or `ED25519`). Defaults to `ED25519`.

> **Note:** Setting the **`<alias>` to `random`** will generate a random 20-char long alias. This is useful for scripting functionality to avoid running into non-unique alias errors. 

**2. Retrieve Account Balance:**

Displays the balance of a specified account. Users can choose to view only the Hbar balance or the balance of a specific token. It's not possible to use both options at once.

```sh
hcli account balance <accountIdOrAlias> [-h,--only-hbar] [-t,--token-id <tokenId>]

// Output
Balance for account 0.0.5892294:
1000 Hbars

Token balances:
0.0.5892308: 2
```

**3. List All Accounts:**

Lists all accounts stored in the address book. An optional flag allows displaying private keys.

```sh
hcli account list [-p,--private]

// Output with -p flag
Accounts:
- Alias: bob
  Account ID: 0.0.4536938
  Type: ECDSA
  Private Key: 30300201[...]
```

**4. Import an Existing Account:**

Allows users to import an existing account into the CLI tool using the account's alias, ID, type, and optionally private key. You can import accounts without a private key, but you won't be able to sign transactions with them.

```sh
hcli account import -a,--alias <alias> -i,--id <id> [-k,--key <key>]

// Example
hcli account import -a alice -i 0.0.5892294 -k 30300201[...]
hcli account import -a alice -i 0.0.12450
```

Flags:
- **Alias:** (required) Alias for the imported account.
- **Id:** (required) Account ID.
- **Key:** (optional) Private key.

**5. Clear All Accounts:**

Removes all account information from the address book.

```sh
hcli account clear
```

**6. Delete an Account:**

Deletes an account from the address book.

```sh
hcli account delete -a,--alias <alias> -i,--id <id>
```

Flags:
- **-a, --alias:** (optional) Alias of the account to delete.
- **-i, --id:** (optional) Account ID of the account to delete.


## Token Commands

### Overview

The `token` command in the Hedera CLI tool provides functionality for creating, associating, and transferring tokens on the Hedera network. It includes subcommands for creating fungible tokens, associating tokens, and transferring tokens between accounts.

```
token create-from-file
token create
token associate
token transfer
```

#### Usage

**1. Create Token from File:**

This command allows users to create a new token by specifying a JSON file that contains the token's configuration. Files are stored in the `dist/input` folder of the project using the format `token.<filename>.json`. Replace `<filename>` with your desired filename. 

```sh
hcli token create-from-file -f,--file <filename>
```

Flags:
- **File:** (required) Filename containing the token information in JSON format.

A token input file looks like below. You can define all properties you would normally define when using using the SDK to create a token. All of the properties are required. If you don't need a key, leave it as an empty string.

```json
{
  "name": "myToken",
  "symbol": "MTK",
  "decimals": 2,
  "supplyType": "finite",
  "initialSupply": 1000,
  "maxSupply": 1000000,
  "keys": {
    "supplyKey": "<alias:bob>",
    "treasuryKey": "<alias:alice>",
    "adminKey": "<newkey:ecdsa:10000>",
    "feeScheduleKey": "",
    "freezeKey": "",
    "wipeKey": "",
    "pauseKey": "",
    "kycKey": ""
  },
  "customFees": [],
  "memo": "Test token"
}
```

> **Note:** that you can use placeholders for all keys on a token. The format `<alias:bob>` refers to an account with alias `bob` in your address book. It will use Bob's key. 
> 
> You can also tell the CLI tool to create a new account with an account type (`ecdsa` or `ed25519`) and an initial balance in TinyBars. The `<newkey:ecdsa:10000>` placeholder creates a new ECDSA account with 10,000 TinyBars and uses its key for the admin key.

**2. Create Fungible Token:**

Creates a new fungible token with specified properties like name, symbol, treasury ID, treasury key, decimals, initial supply, and admin key.

```sh
hcli token create --treasury-id <treasuryId> --treasury-key <treasuryKey> --name <name> --symbol <symbol> --decimals <decimals> --suply-type <supplyType> --initial-supply <initialSupply> --admin-key <adminKey>
```

Flags:
- **Treasury ID:** (required) Treasury ID for the fungible token.
- **Treasury Key:** (required) Treasury key for the fungible token.
- **Name:** (required) Name of the fungible token.
- **Symbol:** (required) Symbol of the fungible token.
- **Decimals:** (required) Decimals of the fungible token.
- **Supply type:** (required) Supply type can be either `finite` or `infinite`.
- **Initial Supply:** (required) Initial supply of the fungible token.
- **Admin Key:** (required) Admin key of the fungible token.

> **Note:** It's better to use the `token create-from-file` if you want to set all properties for a token. The `token create` command only allows for a couple of options.

**3. Associate Token with Account:**

Associates a specified token with an account. Both the token ID and the account ID (or alias) are required.

```sh
hcli token associate -a,--account-id <accountId> -t,--token-id <tokenId>

// Example
hcli token associate -a bob -t 0.0.5892309
```

Flags:
- Account ID: (required) Account ID or alias to associate with the token.
- Token ID: (required) Token ID to be associated with the account.

**4. Transfer Fungible Token:**

Facilitates the transfer of a specified amount of a fungible token from one account to another.

```sh
hcli token transfer -t,--token-id <tokenId> --to <to> --from <from> -b,--balance <balance>
```

Flags:
- **Token ID:** (required) Token ID to transfer.
- **To:** (required) Account ID to transfer the token to.
- **From:** (required) Account ID to transfer the token from.
- **Balance:** (required) Amount of token to transfer.

## Backup Commands

### Overview

The `backup` command in the Hedera CLI tool is designed for creating backups of the `state.json` file, which contains configuration and state information.

```
backup create
```

#### Usage

**1. Creating Backup:**

This command creates a backup of the `state.json` file. The backup file is named using a timestamp for easy identification and recovery. The format is: `state.backup.<timestamp>.json`. The backup is stored in the same `dist/state` directory as `state.json`.

```sh
hcli backup create [--accounts] [--safe]

// Example
hcli backup create --accounts --safe
hcli backup create --safe
hcli backup create
```

Flags:
- **Accounts:** (optional) Creates a backup of the accounts section of the state. The backup file is named `accounts.backup.<timestamp>.json`.
- **Safe:** (optional) Removes private information from the backup like token keys, account keys, and operator key/ID. It does not remove the private keys in scripts' commands.

## Record Commands

### Overview

The `record` command in the Hedera CLI tool is designed for recording sequences of commands executed in the CLI. This feature is particularly useful for automating network operations or setting up testing environments by replaying recorded scripts containing commands.

```
record start
record stop
```

#### Usage

**1. Start Recording:**

Initiates the recording of commands under a new script tag in your `dist/state.json` file. A unique script name must be provided to start recording. It also sets the `recording` variable in your state to `1` to indicate a recording is active.

```sh
hcli record start <script_name>
```

> **Note:** You can load other scripts (see "Script Commands" below) within your script. This allows you to combine different script blocks to build complex sequences.

**2. Stop Recording:**

Ends the current recording session. The recorded commands are saved under the script tag initiated with `start`.

```sh
hcli record stop
```

## Script Commands

### Overview

The `script` command in the Hedera CLI tool allows users to load and execute previously recorded scripts. This command is particularly useful for automating repetitive tasks or for quickly setting up specific states or environments that have been captured in a script.

```
script load
script list
script delete
script download
```

#### Usage

**1. Load and Execute Recorded Script:**

Loads a script by name from state and sequentially executes each command in the script.

```sh
hcli script load -n,--name <name>
```

> **Note:** Commands are executed in the order they were recorded.
Each command is executed via execSync, which runs the command in a synchronous child process.

**2. List All Scripts:**

Lists all scripts stored in the `dist/state.json` file.

```sh
hcli script list
```

**3. Delete Script:**

Deletes a script from the `dist/state.json` file.

```sh
hcli script delete -n,--name <name>
```

**4. Download Script:**

Downloads a script from an external URL and adds it to the `dist/state.json` file.

```sh
hcli script download -u,--url <url>
```

Format for remote script files:
```json
{
  "scripts": [
    {
      "name": "script1",
      "commands": [
        "account create -a alice",
        "account create -a bob"
      ]
    },
    {
      "name": "script2",
      "commands": [
        "account create -a charlie",
        "account create -a dave"
      ]
    }
  ]
}
```

_You can access an example [here](https://gist.githubusercontent.com/michielmulders/ed7a639bb3a5629380cdd57290d24b91/raw/fc072bf5682467113faaae19ce65f0ef92b6a4cd/createAccAndFT.json)._

# Contributing Tips

## Development Mode

You can run the application in development mode. It will watch for changes in the `src` folder and automatically recompile the application while maintaining the `dist/state.json` file.

```sh
npm run dev-build
```

Further, you can lint or format the code using the following commands:

```sh
npm run lint
npm run format
```

## Config

**How to handle config?**

If you add features that affect the initial config, make sure to update both the `state/config.ts` and `types/state.d.ts` files.

## Unit Testing

You need to create a local clone of commander program each time you run a unit test to ensure test encapsulation: `const program = new Command();`

```js
const { Command } = require('commander');
const networkCommands = require("../../commands/network");

const fs = require("fs");

describe("network commands", () => {

  describe("network switch command", () => {
    test("switching networks successfully", () => {
      // Arrange
      fs.readFileSync = jest.fn(() => JSON.stringify({ network: "mainnet" })); // Mock fs.readFileSync to return a sample config
      fs.writeFileSync = jest.fn(); // Mock fs.writeFileSync to do nothing
      //console.log = jest.fn(); // Mock console.log to check the log messages

      const program = new Command();
      networkCommands(program);

      // Act
      program.parse(["node", "hedera-cli.js", "network", "use", "testnet"]);

      // Assert
      const opts = program.opts();
      console.log(opts)
      expect(opts.network).toBe("testnet");
      console.log(program.args);
      // expect(program.args).toEqual(["--type", "order-cake"]);

      // Check that console.log was called with the correct message
      expect(console.log).toHaveBeenCalledWith("Switched to testnet");

      // Check that fs.writeFileSync was called with the updated config
      expect(fs.writeFileSync).toHaveBeenCalledWith(
        expect.any(String), // path
        JSON.stringify({ network: "testnet" }, null, 2),
        "utf-8"
      );
    });
  });

  describe("network switch ls", () => {
    // TODO
  });
});
```

## Support

If you have a question on how to use the product, please see our [support guide](https://github.com/hashgraph/.github/blob/main/SUPPORT.md).

## Contributing

Contributions are welcome. Please see the [contributing guide](https://github.com/hashgraph/.github/blob/main/CONTRIBUTING.md) to see how you can get involved.

## Code of Conduct

This project is governed by the [Contributor Covenant Code of Conduct](https://github.com/hashgraph/.github/blob/main/CODE_OF_CONDUCT.md). By participating, you are
expected to uphold this code of conduct.

## License

[Apache License 2.0](LICENSE)
