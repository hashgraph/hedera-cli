[![CircleCI](https://circleci.com/gh/hashgraph/hedera-cli/tree/master.svg?style=shield)](https://circleci.com/gh/hashgraph/hedera-cli/tree/master) [![Codacy Badge](https://api.codacy.com/project/badge/Coverage/66c53b09f508477884f807f35ea755cc)](https://www.codacy.com/manual/HederaHashgraph/hedera-cli?utm_source=github.com&utm_medium=referral&utm_content=hashgraph/hedera-cli&utm_campaign=Badge_Coverage) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/66c53b09f508477884f807f35ea755cc)](https://www.codacy.com/manual/HederaHashgraph/hedera-cli?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=hashgraph/hedera-cli&amp;utm_campaign=Badge_Grade) [![Maintainability](https://api.codeclimate.com/v1/badges/0b8720d1b480910c0437/maintainability)](https://codeclimate.com/github/hashgraph/hedera-cli/maintainability) ![GitHub release (latest by date)](https://img.shields.io/github/v/release/hashgraph/hedera-cli) [![GitHub](https://img.shields.io/github/license/hashgraph/hedera-mirror-node)](LICENSE)

# Hedera CLI

This is a simple quick start tool to understand the apis via gRPC that Hedera platform provides.

To get started, you must either have a **`mainnet`** or a **`testnet`** account.

You can register at [portal.hedera.com](https://portal.hedera.com) to get an *account*, or get another user who already has an account to create one for you.

A *Hedera account* is represented in a numeric format, delimited by 2 dots. For example `0.0.1234`. The first number in this example "0" represents the shard number. The second number "0" represents the realm number and the last number "1234" is the account number.

## Overview

Hedera currently has 3 services.
1) Crypto service
2) File service
3) Smart contract service

and an upcoming 4th service - Hedera consensus service.

To read what Hedera has to offer, you can check out

1) [docs.hedera.com](https://docs.hedera.com/docs)
2) [hedera.com](https://www.hedera.com)
3) [Hedera's whitepaper](https://www.hedera.com/whitepaper)

### Description

#### Prerequisites

Hedera CLI works with Java versions 10/11/12.

#### Quick Start
There are 2 ways to start Hedera CLI.

The first, download hedera cli via `brew tap` or `curl`.
```bash
# macOS
brew tap hashgraph/tap
brew install hedera-cli

# unix / linux
curl -s https://raw.githubusercontent.com/hashgraph/hedera-cli/master/install.sh | sudo bash
```

To run the hedera shell,
```bash
hedera
```

Once that is running, you should see this
![](setup.gif)

___

### Commands

#### Setup operator account

There is the concept of an operator account where the operator can pay for the transaction costs, ie network and node fees.

Cli will prompt `setup` on first run to save default operator key into `~/.hedera`. 
Setting up an operator can be done with either passphrase or private key
1) 24 recovery words, where words are separated by spaces.
2) Private key either in hexadecimal or ASN1 encoded format.

AccountGetInfo is called during setup to confirm the account exists on Hedera. This will cost some tinybars.
The default operator can be changed anytime by using `account use 0.0.xxxx`.
```bash
# Sets the default operator
setup
```

#### Switch to a specific network

```bash
# To see the list of available networks
network ls

# To change network
network use mainnet
network use testnet
```
#### List Accounts

```bash
# Lists all accounts associated with current network
account ls
```
#### Version (v0.1.6)
```bash
# Checks current version
version
```
#### Switch Accounts
```bash
# Switch to use any account that has been recovered and exists in `account ls`
account use 0.0.xxxx
```

#### Default Accounts
The account must have been recovered and exists in `account ls`
```bash
# Sets the account as default operator. 
account default 0.0.xxxx
```

#### Recover Accounts
Recovering an Hedera account using either 
1) 24 recovery words, where words are separated by spaces.
2) Private key either in hexadecimal or ASN1 encoded format.
```bash
# Recovering an Hedera account
account recovery 0.0.xxxx
```

#### Create Account

Account creation can be done in multiple ways.

Simple Key
1) Creating an account using new recovery words and keypair.
2) Creating an account using operator's keypair.
3) Creating an account using any public key (not yet supported).

MultiSig account creation (not yet supported)

```bash
# Creates a new Hedera account using NEW recovery words and keypair. This is default.
account create -b 100000000

# Creates a new Hedera account using Operator's public key 
account create -b 100000000 -k
```

#### Delete Account
Cli will prompt for key of account(-o) that is set for deletion. 
Only the account for deletion can sign the transaction.
```bash
# Deletes an account from Hedera, and transfers the remaining funds from the deleted account to the new account
account delete -o 0.0.1001 -n 0.0.1002
account delete --oldAccount 0.0.1001 --newAccount 0.0.1002
```
#### Account Balance
Account balance calls are free.
```bash
# Gets the balance of an account
account balance 0.0.xxxx
```

#### Account Get Info
Gets the information of an account, such as the public keys, and stateproofs (upcoming)
```bash
# Gets the information of an account
account info 0.0.xxxx
```

#### Account Update
Cli will prompt for original key as well as the new key that said account will be updated with.
```bash
# Updated the keypair of account
account update 0.0.xxxx 
```

#### Help

```bash
# shows all the available commands
help
```

### Cryptotransfer
Cryptotransfer in tinybars(-tb) or hbars(-hb). Tinybars are integers while hbars can be down to 8 decimals.
Recipients(-r) are separated by commas
```bash
Valid commands
## hedera [testnet][0.0.112533] :>

## Sender is not operator
transfer -s 0.0.119312 -r 0.0.116681,0.0.121290 -tb -500,300,200
transfer -s 0.0.119312 -r 0.0.116681,0.0.121290 -hb -5,3,2
transfer -s 0.0.119312 -r 0.0.116681,0.0.121290 -hb -0.005,0.003,0.002

## Sender is operator
transfer -s 0.0.112533 -r 0.0.116681,0.0.121290 -hb -0.005,0.003,0.002
transfer -s 0.0.112533 -r 0.0.116681,0.0.121290 -hb -5,3,2
transfer -s 0.0.112533 -r 0.0.116681,0.0.121290 -tb 3000000,2000000
transfer -s 0.0.112533 -r 0.0.116681,0.0.121290 -tb -5000000,3000000,2000000
transfer -s 0.0.112533 -r 0.0.116681,0.0.121290 -tb 3000000,2000000

transfer -r 0.0.116681,0.0.121290 -tb 3000000,2000000
transfer -r 0.0.116681,0.0.121290 -tb -5000000,3000000,2000000
transfer -r 0.0.116681,0.0.121290 -hb -0.5,0.3,0.2
transfer -r 0.0.116681,0.0.121290 -hb 0.3,0.2
```

### HEDERA CONSENSUS SERVICE AND MIRROR NODE
In resources you will notice 2 json files. One belongs to the hedera network, the other belongs to the mirror network.
For customised network address book, you will need to add 
`addressbook.json` and/or `mirrornode.json` in `~/.hedera`.
The application will automatically search for all custom addressbook in the same json format and populate them into cli,
where `network ls` and `mirror ls` will show the list of networks available

### Hedera Consensus Service Create
```bash
[command][subcommand][args]
hcs create topic
```

### Hedera Consensus Service Read
```bash
[command][subcommand][args]
hcs read topic
```