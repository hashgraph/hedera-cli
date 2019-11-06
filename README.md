[![CircleCI](https://circleci.com/gh/hashgraph/hedera-cli/tree/master.svg?style=shield)](https://circleci.com/gh/hashgraph/hedera-cli/tree/master) [![Codacy Badge](https://api.codacy.com/project/badge/Coverage/66c53b09f508477884f807f35ea755cc)](https://www.codacy.com/manual/HederaHashgraph/hedera-cli?utm_source=github.com&utm_medium=referral&utm_content=hashgraph/hedera-cli&utm_campaign=Badge_Coverage) [![GitHub](https://img.shields.io/github/license/hashgraph/hedera-mirror-node)](LICENSE) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/66c53b09f508477884f807f35ea755cc)](https://www.codacy.com/manual/HederaHashgraph/hedera-cli?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=hashgraph/hedera-cli&amp;utm_campaign=Badge_Grade) ![GitHub release (latest by date)](https://img.shields.io/github/v/release/hashgraph/hedera-cli)

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
curl -s https://raw.githubusercontent.com/hashgraph/hedera-cli/master/install.sh | bash
```

To run the hedera shell,
```bash
hedera
```

Once that is running, you should see 

![Hedera Shell](hedera.png)

___

### Commands

#### Setup operator account

There is the concept of an operator account where the operator can pay for the transaction costs, ie network and node fees.

Cli will prompt `setup` on first run to save default operator key into `~/.hedera`. You will need to have an accountID and 24 words ready.
AccountGetInfo is called during setup to confirm the account exists. This will cost some tinybars.
 
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
Accounts that have been created or recovered with Hedera CLI will be saved to local disk and shown here.
```bash
# Lists all accounts associated with current network
account ls
```

#### Switch Accounts
Switch to use any account that has been recovered and exists in `account ls`
```bash
# Switches from default operator to current operator (in-memory) 
account use 0.0.xxxx
```

#### Recover Accounts
Recovering a Hedera account using 24 recovery words, where words are separated by spaces.
AccountGetInfo is called during recovery to confirm the account exists. This will cost some tinybars.
Account recovered will be saved to local disk.
```bash
# 
account recovery 0.0.xxxx
```

#### Create Account

Account creation can be done in multiple ways.

Simple Key
1) Creating an account using new recovery words and keypair (safest).
2) Creating an account using operator's keypair.
3) Creating an account using any public key. (not yet supported)

MultiSig account creation (not yet supported)

```bash
# Creates a new Hedera account using NEW recovery words and keypair. This is default.
account create -b 100000000

# Creates a new Hedera account using Operator's public key 
account create -b 100000000 -k
```

#### Delete Account
Deletes an account from Hedera, and transfers the remaining funds from the deleted account to the new account.
The account that is to be delete will be required to sign the transaction for its deletion.
```bash
# Deletes an account from Hedera, and transfers the remaining funds from the deleted account to the new account
account delete -o 0.0.1001 -n 0.0.1002
account delete --oldAccount 0.0.1001 --newAccount 0.0.1002
```

#### Account Balance
Account get balance is free.
```bash
# Gets the balance of an account
account balance 0.0.xxxx
```

#### Account Info
Public key and stateproof (upcoming) are some of the information that you can retrieve from the said account.
```bash
# Gets the info of an account
account info 0.0.xxxx
```

#### Help

```bash
# shows all the available commands
help
```

### VERSION 0.1.0 (Latest)
#### Crypto Transfer
Currently supports 1 Operator, 1 Sender, multiple recipients
The account(s) that are releasing funds will be required to sign the transaction.

Caveat: Does not allow transferring an amount of 0.
```bash
# Transfer in tinybars (whole numbers)

# valid command for single sender [-s] and single recipient [-r]
transfer -s 0.0.1001 -r 0.0.1002 -tb 4400 

# valid command for single sender [-s] and single recipient [-r]
transfer -s 0.0.1001 -r 0.0.1002 -tb -4400,4400 

# valid command for single sender [-s] and multiple recipient [-r]
transfer -s 0.0.1001 -r 0.0.1003,0.0.1004,0.0.1005 -tb -500000,100000,100000,300000 


# Transfer in hbars (can be in decimals)
transfer -s 0.0.1001 -r 0.0.1002 -hb 4.4 
transfer --sender 0.0.1001 --recipient 0.0.1002 --recipientAmtHBars 4.4

transfer -s 0.0.1001 -r 0.0.1003,0.0.1004,0.0.1005 -hb -50,10,10,30 
```

### VERSION 0.0.4

#### Crypto Transfer
```bash
# Transfer in tinybars
transfer single -a 0.0.1001 -tb 4400 
transfer single --accountId 0.0.1001 --recipientAmtTinyBars 4400

# Transfer in hbars
transfer single -a 0.0.1001 -hb 0.00044 
transfer single --accountId 0.0.1001 --recipientAmtHBars 0.00044
```

#### Crypto Transfer Multiple

```bash
# Transfer in tinybars
transfer multiple -a 0.0.1001,0.0.1002,0.0.1003 -tb 440000,550000,660000 
transfer multiple --accountId 0.0.1001,0.0.1002,0.0.1003 --recipientAmtTinyBars 44000,55000,66000

# Transfer in hbars
transfer multiple -a 0.0.1001,0.0.1002,0.0.1003 -hb 0.44,1.55,22.66
transfer multiple --accountId 0.0.1001,0.0.1002,0.0.1003 --recipientAmtHBars 0.44,1.55,22.66
```
