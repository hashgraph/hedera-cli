# Hedera CLI

Welcome to the Hedera CLI Tool, a powerful and intuitive command-line interface designed to streamline your interactions with the Hedera network. Whether you're a developer needing to set up test environments, automate network-related tasks, or explore the extensive capabilities of the Hedera mainnet and testnet, this tool is your one-stop solution.

The Hedera CLI Tool elegantly addresses the complexities associated with distributed ledger technologies. It simplifies the process of executing actions such as creating new accounts, sending transactions, managing tokens, and associating with existing tokens directly from the CLI. This high level of functionality and ease of use significantly reduces the barrier to entry for developers working on Hedera-based projects.

A key advantage of the Hedera CLI Tool is its potential to enhance your workflow. It's not just about performing individual tasks; it's about integrating these tasks into a larger, more efficient development process. With plans for future integration into Continuous Integration/Continuous Deployment (CI/CD) pipelines, this tool promises to be a versatile asset in the automation and management of Hedera network operations.

## Prerequisites

Before proceeding with the installation and setup of the Hedera CLI Tool, ensure the following prerequisites are met:

**1. Node.js Installation:**

The Hedera CLI Tool requires Node.js (version LTS 16.20.2 or higher). You can check your current version by running `node -v` in your terminal. If you do not have Node.js installed, you can download it from [Node.js official website](https://nodejs.org/en).

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

Add the following lines to your `~/.hedera/.env` file, replacing the placeholders with your actual operator ID and key for previewnet, testnet, and mainnet. It's **not mandatory** to set keys for all networks. If you only want to use one network, you can leave the other credentials empty. 

```text
PREVIEWNET_OPERATOR_KEY=
PREVIEWNET_OPERATOR_ID=
TESTNET_OPERATOR_KEY=302e0201003005060[...]
TESTNET_OPERATOR_ID=0.0.12345
MAINNET_OPERATOR_KEY=
MAINNET_OPERATOR_ID=
```

Next, set up the CLI tool with the command:

```sh
node dist/hedera-cli.js setup init
```

**4. Verify Installation:**

You can verify the installation by listing all accounts in your address book. If you haven't added any accounts yet, you should see the following output:

```sh
node dist/hedera-cli.js account list
// No accounts found.
```

**5. Set Network**

When first using the network, the CLI tool will use the `testnet` network. You can switch to the `mainnet` (or `previewnet`) network using the following command:

```sh
node dist/hedera-cli.js network use mainnet
```

**6. Optional: Setting Up an Alias**

To avoid typing the full command each time, you can set an alias in your shell profile. For example, in bash or Zshell, you can add the following line to your `.bashrc`/`.bash_profile` or `.zshrc`. Replace the path with the absolute path to your `hedera-cli` installation path.

```sh
alias hcli="node /Users/myUser/hedera-cli/dist/hedera-cli.js"
```

# Video Guide

Learn how to use the Hedera CLI Tool by watching the video below.

[![Thumbnail video guide](https://img.youtube.com/vi/3XCkdtMzR14/0.jpg)](https://www.youtube.com/watch?v=3XCkdtMzR14 "Learn how to use the Hedera CLI")

# Commands

Let's explore the different commands, their options, and outputs.

- [Setup Commands](#setup-commands): Instantiate or reset the Hedera CLI tool
- [Network Commands](#network-commands): Switch Hedera networks
- [Wait Commmand](#wait-command): Wait for a specified amount of seconds
- [Account Commands](#account-commands): Create and manage accounts
- [Token Commands](#token-commands): Create and manage tokens
- [Hbar Command](#hbar-command): Transfer Hbars between accounts
- [Backup Commands](#backup-commands): Create a backup of your state
- [Record Commands](#record-commands): Record CLI interactions and store it in scripts
- [State Commands](#state-commands): Manage the state of the CLI tool
- [Script Commands](#script-commands): Replay and manage scripts containing recorded CLI interactions
  - [Dynamic Variables in Scripts](#dynamic-variables-in-scripts): Use dynamic variables in scripts


> Each of the commands can be run with the `--help` flag to display the command's options and usage. 
>
> Use the `--quiet` flag to suppress the output of the command or the `--verbose` flag to display more information.

## Setup Commands

### setup

#### Overview

The setup command is an essential component of the Hedera CLI tool, designed to initialize and configure your working environment. This command facilitates the process of setting up the CLI with your operator key and ID.

```sh
setup init
setup reload
```

#### Usage

**1. Initialization:**
Sets up the CLI with the operator key and ID.

```sh
hcli setup init
```

When executed, the setup command performs several key functions:

**Environment Variable Validation:**
It checks if the HOME environment variable is defined and reads `PREVIEWNET_OPERATOR_KEY`, `PREVIEWNET_OPERATOR_KEY`, `TESTNET_OPERATOR_KEY`, `TESTNET_OPERATOR_ID`, `MAINNET_OPERATOR_KEY`, `MAINNET_OPERATOR_ID` from the `~/.hedera/.env` file.

**State Update:**
Once the previewnet, testnet, and mainnet operator key and ID are validated, these credentials are used to update the `state/state.json` file, which holds the configuration state of the CLI tool.

**2. Reload Operator Key and Id:**

Reload the operator key and ID from the `.env` file. This command is useful when you add new networks to your `.env` file and want to update the state, so you can use the new networks.

```sh
hcli setup reload
```

## Network Commands

### Overview

The network command in the Hedera CLI tool is designed to manage and interact with different Hedera networks. It allows users to switch between networks and list available networks. This flexibility is crucial for developers who need to test their applications in different network environments.

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

Replace `<name>` with the name of the network you wish to switch to (`mainnet`, `testnet`, or `previewnet`).

**2. Listing Available Networks:**

This command lists all available networks that the CLI tool can interact with. It's useful for confirming the network options and ensuring correct network names are used when switching networks.

```sh
hcli network list
// Available networks: mainnet, testnet, previewnet
```

#### Description

The network command includes a catch-all for unknown subcommands. If an unrecognized command is entered, it triggers an error message and displays the help text for the network command.

```sh
// Invalid network name. Available networks: mainnet, testnet, previewnet
```

## Wait Commmand

### Overview

The wait command in the Hedera CLI tool is designed to pause the execution of commands for a specified amount of time. This command is useful for waiting for transactions to be confirmed on the network or the mirror node to update before executing subsequent commands.

```sh
hcli wait <seconds>

// Example
hcli wait 3
```

The `wait` command is used in the [examples section](#other-examples) below.

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

## Hbar Command

### Overview

The `hbar` command in the Hedera CLI tool is designed for transferring Hbars between accounts.

```sh
hcli hbar transfer -b,--balance <balance> [-f,--from <from>] [-t,--to <to>]

// Example
hcli hbar transfer -f alice -t bob -b 1000
hcli hbar transfer -f alice -t 0.0.12345 -b 1000
```

Flags:
- **Balance:** (required) Amount of Hbars to transfer.
- **From:** (optional) Account ID or alias to transfer the Hbars from.
- **To:** (optional) Account ID or alias to transfer the Hbars to.

> **Note:** If you don't specify a `from` or `to` account, the CLI tool will prompt you to select an account from your address book, listed by alias.

## Backup Commands

### Overview

The `backup` command in the Hedera CLI tool is designed for creating backups of the `state.json` file, which contains configuration and state information.

```
backup create
backup restore
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

**2. Restoring Backup:**

This command restores a backup of the `state.json` file stored in the same `dist/state` directory. It only restores state files with the format `state.backup.<timestamp>.json`.

```sh
hcli backup restore -f,--file <filename> [--restore-accounts] [--restore-tokens] [--restore-scripts]
```

Flags:
- **File:** (optional) Filename of the backup file to restore.
- **Restore Accounts:** (optional) Restores the accounts section of the state.
- **Restore Tokens:** (optional) Restores the tokens section of the state.
- **Restore Scripts:** (optional) Restores the scripts section of the state.

You can combine the flags to restore only certain parts of the state. For example, you can restore only the accounts and tokens section of the state by using the following command:

```sh
hcli backup restore -f state.backup.1704321015228.json --restore-accounts --restore-tokens
```

> **Note: If you don't provide a filename, the CLI tool will list all available backups and ask you to select one.** You can still use the flags to restore only certain parts of the state.


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

## State Commands

### Overview

The `state` command in the Hedera CLI tool is designed for managing the state of the CLI tool. It allows users to view the current state, clear the state, and download a new state via a remote URL.

```
state download
state view
state clear
```

#### Usage

**1. Download State:**

Downloads a state file from an external URL and add it to the `dist/state.json` file. You can use this command to update your state with new accounts, tokens, or scripts. You can choose to overwrite the current state or merge the downloaded state with the current state.

```sh
hcli state download --url <url> [--overwrite] [--merge]
```

Flags:
- **URL:** (required) URL to download the state file from.
- **Overwrite:** (optional) Overwrites the current state file with the contents of the downloaded file.
- **Merge:** (optional) Merges the downloaded state file with the current state file. It won't fail when the state file contains duplicate keys.

Format for remote script files:
```json
{
  "accounts": {
    "myalias": {
      "network": "testnet",
      "alias": "myalias",
      "accountId": "0.0.7426198",
      "type": "ecdsa",
      "publicKey": "302d300706052b8104000a03220003732a9daae40e2a41ccd10dd35b521cbcafdd4bf906a66e37d0a65512a1d7db23",
      "evmAddress": "a5accb5010ad3ee50c66a433d5b8fdfe0d0eab59",
      "solidityAddress": "0000000000000000000000000000000000715096",
      "solidityAddressFull": "0x0000000000000000000000000000000000715096",
      "privateKey": "303002010030[...]"
    }
  },
  "scripts": {
    "script-script1": {
      "name": "script1",
      "commands": [
        "account create -a alice",
        "account create -a bob"
      ]
    }
  },
  "tokens": {
    "0.0.7426199": {
      "network": "testnet",
      "associations": [],
      "tokenId": "0.0.7426199",
      "name": "myToken",
      "symbol": "MTK",
      "treasuryId": "0.0.7426195",
      "decimals": 2,
      "initialSupply": 1000,
      "supplyType": "finite",
      "maxSupply": 1000000,
      "keys": {
        "adminKey": "303002010030[...]",
        "pauseKey": "",
        "kycKey": "",
        "wipeKey": "",
        "freezeKey": "",
        "supplyKey": "302e02010030[...]",
        "feeScheduleKey": "",
        "treasuryKey": "302e02010030[...]"
      }
    }
  }
}
```

_You can access an example [here](https://gist.githubusercontent.com/michielmulders/f8ae878431d3d551ecf5e478e9e96ea5/raw/9d5c0329eb3fe5bfda02b8ec1880c5894bd4539e/stateUpdate.json). You can use it like this:_

```sh
hcli state download --url https://gist.githubusercontent.com/michielmulders/f8ae878431d3d551ecf5e478e9e96ea5/raw/9d5c0329eb3fe5bfda02b8ec1880c5894bd4539e/stateUpdate.json --overwrite
```

**2. View State:**

Displays the current state of the CLI tool.

```sh
hcli state view [--accounts] [--tokens] [--scripts] [--account-alias <account-alias>] [--account-id <account-id>] [--token-id <token-id>]
```

Flags:
- **Accounts:** (optional) Displays the accounts section of the state.
- **Tokens:** (optional) Displays the tokens section of the state.
- **Scripts:** (optional) Displays the scripts section of the state.
- **Account Alias:** (optional) Displays the account with the specified alias.
- **Account ID:** (optional) Displays the account with the specified ID.
- **Token ID:** (optional) Displays the token with the specified ID.

**3. Clear State:**

Clears the state of the CLI tool. This command is useful for resetting the state to its initial state. Depending on the flags provided, it resets the entire state or skips certain parts of the state, such as the accounts, tokens, or scripts sections in your state. For example, this might be useful when you want to reset your state but keep your address book (`state.accounts`).

```sh
hcli state clear [-a, --skip-accounts] [-t, --skip-tokens] [-s, --skip-scripts]
```

Flags:
- **-a, --skip-accounts**: (optional) Skips clearing accounts.
- **-t, --skip-tokens:** (optional) Skips clearing tokens.
- **-s, --skip-scripts:** (optional) Skips clearing scripts.

## Script Commands

### Overview

The `script` command in the Hedera CLI tool allows users to load and execute previously recorded scripts. This command is particularly useful for automating repetitive tasks or for quickly setting up specific states or environments that have been captured in a script.

```
script load
script list
script delete
```

#### Usage

**1. Load and Execute Recorded Script:**

Loads a script by name from state and sequentially executes each command in the script.

```sh
hcli script load -n,--name <name>
```

Each command is executed via [`execSync`](https://nodejs.org/api/child_process.html), which runs the command in a synchronous child process.

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

### Dynamic Variables in Scripts

The dynamic variables feature in our script execution command (`script load`) allows you to store variables during script execution and reference them in other commands within the script. This feature enhances script flexibility and reusability by enabling you to replace options with arguments or state variables, and store and retrieve variables as needed.

#### Example

Let's look at an example of how dynamic variables work. In this example, we'll create a script that creates a random account and stores the privateKey in a variable called `tokenMichielAdminKey` and the account alias in a variable called `accountAlias`. We'll then use these variables to create a new token. Funnily, we are using the `accountAlias` variable to set the token name.

```json
{
  "name": "test",
  "commands": [
    "network use testnet",
    "account create -a random --args privateKey,tokenMichielAdminKey --args alias,accountAlias",
    "token create -n {{accountAlias}} --symbol rand --decimals 2 --initial-supply 1000 --supply-type infinite --admin-key {{tokenMichielAdminKey}} --treasury-id 0.0.4536940 --treasury-key 302302[...]"
  ],
  "args": {}
}
```

> Make sure to not use a space between the variable name and the comma. Otherwise, the CLI tool will not recognize the variable. `--args privateKey,tokenMichielAdminKey` is correct, `--args privateKey, tokenMichielAdminKey` is not.

When a command fails, the script execution stops and the error is displayed.

#### Other Examples

The following example shows how you can use dynamic variables to create a script that creates three accounts, creates a token, associates the token with the third account, and transfers one token from the second account (treasury) to the third account. Then, it displays the token state and the balance of the third account. Often, it will tell you that the third account has a `0` balance because the mirror node hasn't updated yet.

```json
{
  "name": "transfer",
  "commands": [
    "network use testnet",
    "account create -a random --args privateKey,privKeyAcc1 --args alias,aliasAcc1 --args accountId,idAcc1",
    "account create -a random --args privateKey,privKeyAcc2 --args alias,aliasAcc2 --args accountId,idAcc2",
    "account create -a random --args privateKey,privKeyAcc3 --args alias,aliasAcc3 --args accountId,idAcc3",
    "token create -n mytoken -s MTK -d 2 -i 1000 --supply-type infinite -a {{privKeyAcc1}} -t {{idAcc2}} -k {{privKeyAcc2}} --args tokenId,tokenId",
    "token associate --account-id {{idAcc3}} --token-id {{tokenId}}",
    "token transfer -t {{tokenId}} -b 1 --from {{aliasAcc2}} --to {{aliasAcc3}}",
    "wait 3",
    "account balance --account-id-or-alias {{aliasAcc3}} --token-id {{tokenId}}",
    "state view --token-id {{tokenId}}"
  ],
  "args": {}
}
```

The below command shows how to create a new account on testnet with 1 hbar and prints the hbar balance.

```json
{
  "name": "account-create",
  "commands": [
    "network use testnet",
    "account create -a random -b 100000000 --type ecdsa --args privateKey,privKeyAcc1 --args alias,aliasAcc1 --args accountId,idAcc1",
    "wait 3",
    "account balance --account-id-or-alias {{idAcc1}} --only-hbar"
  ],
  "args": {}
}
```

#### Mapping Dynamic Variables to Commands

Not each command exposes the same variables. Here's a list of commands and the variables they expose, which you can use in your scripts.

| Command | Variables |
| --- | --- |
| `account create` | `alias`, `accountId`, `type`, `publicKey`, `evmAddress`, `solidityAddress`, `solidityAddressFull`, `privateKey` |
| `account import` | `alias`, `accountId`, `type`, `publicKey`, `evmAddress`, `solidityAddress`, `solidityAddressFull`, `privateKey` |
| `token create` | `tokenId`, `name`, `symbol`, `treasuryId`, `adminKey` |
| `token create-from-file` | `tokenId`, `name`, `symbol`, `treasuryId`, `treasuryKey`, `adminKey`, `pauseKey`, `kycKey`, `wipeKey`, `freezeKey`, `supplyKey`, `feeScheduleKey` |

# CLI State

The Hedera CLI tool stores its state in the `dist/state/state.json` file. This file contains all the information about your accounts, tokens, scripts, and network. You can edit this file manually, but it's not recommended.

Here's an example state:

```json
{
  "network": "testnet",
  "mirrorNodeTestnet": "https://testnet.mirrornode.hedera.com/api/v1",
  "mirrorNodeMainnet": "https://mainnet.mirrornode.hedera.com/api/v1",
  "testnetOperatorKey": "",
  "testnetOperatorId": "",
  "mainnetOperatorKey": "",
  "mainnetOperatorId": "",
  "previewnetOperatorId": "",
  "previewnetOperatorKey": "",
  "recording": 0,
  "recordingScriptName": "",
  "scriptExecution": 0,
  "scriptExecutionName": "",
  "accounts": {
    "bob": {
      "network": "testnet",
      "alias": "bob",
      "accountId": "0.0.7393086",
      "type": "ED25519",
      "publicKey": "302a300506032b657003210059b9fc2413aa2a1dccda4b6ea0f99a48414db6f6ad6eb28589bab12f578f8697",
      "evmAddress": "",
      "solidityAddress": "000000000000000000000000000000000070cf3e",
      "solidityAddressFull": "0x000000000000000000000000000000000070cf3e",
      "privateKey": "302e0201003005060507c46c02ad871ffc38bb216497c6ac9a34aff3ac637153815a896"
    }
  },
  "scripts": {
    "script-test": {
      "name": "test",
      "creation": 1697103669402,
      "commands": [
        "network use testnet",
        "account create -a random --args privateKey,tokenMichielAdminKey --args alias,accountAlias",
        "token create -n {{accountAlias}} -s mm -d 2 -i 1000 --supply-type infinite -a {{tokenMichielAdminKey}} -t 0.0.4536940 -k 302e020100300506032b6568253a539643468dda3128a734c9fcb07a927b3f742719db731f9f50"
      ],
      "args": {}
    }
  },
  "tokens": {
    "0.0.7393102": {
      "network": "testnet",
      "associations": [],
      "tokenId": "0.0.7393102",
      "name": "myToken",
      "symbol": "MTK",
      "treasuryId": "0.0.7393093",
      "decimals": 2,
      "initialSupply": 1000,
      "supplyType": "finite",
      "maxSupply": 1000000,
      "keys": {
        "adminKey": "3030020100300506e9fdf92f82267a40c9ce7932d2622ba29aad3d8d7036dbe5d27",
        "pauseKey": "",
        "kycKey": "",
        "wipeKey": "",
        "freezeKey": "",
        "supplyKey": "302e0201003005002ad871ffc38bb216497c6ac9a34aff3ac637153815a896",
        "feeScheduleKey": "",
        "treasuryKey": "302e0201003078aede2e6a5c46701d89ab48b3e28a31e50243bd85c19f0"
      }
    }
  }
}
```

# Contributing Tips

## Development Mode

You can run the application in development mode. It will watch for changes in the `src` folder and automatically recompile the application while maintaining the `dist/state.json` file.

To get started, create a new state file called `test_state.json` in the `/src/state/` folder.

```sh
cd src/state
touch test_state.json
```

Next, copy the contents of the `src/state/base_state.json` file into the `test_state.json` file.

Once that's done, you can start the application in development mode using the following command:

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

Use `program.parseAsync` if you are testing an asynchronous command.

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
      expect(opts.network).toBe("testnet");
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
    // [...]
  });
});
```

## Dynamic Variables

[Dynamic variables](#dynamic-variables-in-scripts) are variables that are stored in the state and can be used in scripts. They are useful for storing information that is generated during script execution and can be used in other commands within the script. 

### How to allow processing of dynamic variables in a command?

To allow processing of dynamic variables in a command, you need to add a single line of code converting the dynamic variables in your `options` to their actual values. Don't forget to import the `dynamicVariablesUtils` which holds the `replaceOptions` function.

```js
import dynamicVariablesUtils from '../../utils/dynamicVariables';

program
    .command('create')
    .action(async (options: CreateAccountOptions) => {
      options = dynamicVariablesUtils.replaceOptions(options);
      // [...]
    })
```

### How to allow storing variables in the state?

To allow dynamic variables in a command, you need to add the `--args` flag to the command. The `--args` flag takes a list of arguments that are allowed to be dynamic variables. 

```js
program
    .command('create')
    // ...
    .option(
      '--args <args>',
      'Store arguments for scripts',
      (value: string, previous: string) =>
        previous ? previous.concat(value) : [value],
      [],
    )
```

Further, for each command you want to allow the user to store variables, you need to define a command action. Command actions define the mapping between script commands and the corresponding actions. You can specify actions for different commands and use them when storing variables. 

```js
const commandActions: CommandActions = {
  account: {
    create: {
      action: 'accountCreate',
    },
    import: {
      action: 'accountImport',
    },
  },
  token: {
    create: {
      action: 'tokenCreate',
    },
    createFromFile: {
      action: 'tokenCreateFromFile',
    }
  },
};
```

Next, you can define command outputs for your action. Command outputs define the output variables that can be captured and stored for specific actions. You can specify the output variables for each action to use them later in the script.

```js
const commandOutputs: CommandOutputs = {
  accountCreate: {
    alias: 'alias',
    accountId: 'accountId',
    type: 'type',
    publicKey: 'publicKey',
    evmAddress: 'evmAddress',
    solidityAddress: 'solidityAddress',
    solidityAddressFull: 'solidityAddressFull',
    privateKey: 'privateKey',
  },
  // Define outputs for other actions here
};
```

Make sure that each property you define exists in the output for the command. Here's the code for the `accountCreate` command. The `accountDetails` output contains all the properties defined in the `commandOutputs` object. If you define a property in the `commandOutputs` object that doesn't exist in the `accountDetails` output, the script execution will fail.

```js
.action(async (options: CreateAccountOptions) => {
  options = dynamicVariablesUtils.replaceOptions(options);
  try {
    let accountDetails = await accountUtils.createAccount(
      options.balance,
      options.type,
      options.alias,
    );

    // Store dynamic variables
    dynamicVariablesUtils.storeArgs(
      options.args,
      dynamicVariablesUtils.commandActions.account.create.action,
      accountDetails,
    );
  } catch (error) {
    logger.error(error as object);
  }
});
```

The `storeArgs` function takes the `options.args` and the `commandAction` as arguments. It then stores the output variables in the state according to the user's instructions. 

Whenever changing the `commandActions` or `commandOutputs` objects, make sure to update the documentation as well.

## Logging

You can use the `logger` object to log messages to the console. The logger object is defined in `src/utils/logger.ts`. It is defined as a singleton which you can import in your files. 

```js
import { Logger } from '../../utils/logger';
const logger = Logger.getInstance();
```

- Regular output messages are logged using the `logger.log` function. 
- Verbose output messages are logged using the `logger.verbose` function.
- Error messages are logged using the `logger.error` function which has an overload signature
  - `logger.error(error: Error | string)`: Log a single object or string
  - `logger.error(error: string, data: object)`: Log an error string and object

## Support

If you have a question on how to use the product, please see our [support guide](https://github.com/hashgraph/.github/blob/main/SUPPORT.md).

## Contributing

Contributions are welcome. Please see the [contributing guide](https://github.com/hashgraph/.github/blob/main/CONTRIBUTING.md) to see how you can get involved.

## Code of Conduct

This project is governed by the [Contributor Covenant Code of Conduct](https://github.com/hashgraph/.github/blob/main/CODE_OF_CONDUCT.md). By participating, you are
expected to uphold this code of conduct.

## License

[Apache License 2.0](LICENSE)
