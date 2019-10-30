[![CircleCI](https://circleci.com/gh/hashgraph/hedera-cli/tree/master.svg?style=shield)](https://circleci.com/gh/hashgraph/hedera-cli/tree/master) [![Codacy Badge](https://api.codacy.com/project/badge/Coverage/66c53b09f508477884f807f35ea755cc)](https://www.codacy.com/manual/HederaHashgraph/hedera-cli?utm_source=github.com&utm_medium=referral&utm_content=hashgraph/hedera-cli&utm_campaign=Badge_Coverage) [![GitHub](https://img.shields.io/github/license/hashgraph/hedera-mirror-node)](LICENSE) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/66c53b09f508477884f807f35ea755cc)](https://www.codacy.com/manual/HederaHashgraph/hedera-cli?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=hashgraph/hedera-cli&amp;utm_campaign=Badge_Grade) ![GitHub release (latest by date)](https://img.shields.io/github/v/release/hashgraph/hedera-cli)

# Getting Started

```bash
# macOS
brew tap hashgraph/tap
brew install hedera-cli

# unix / linux
curl -s https://raw.githubusercontent.com/hashgraph/hedera-cli/master/install.sh | bash
```

## Run Hedera shell

```bash
hedera
```

## Help
```bash
# shows all the available commands
help
```

## Setup operator account
```bash
# Cli will prompt setup on first run to save default operator key into ~/.hedera
setup
```

## Switch to a specific network
```bash
# To list available networks
network ls

# To select/change network
network use mainnet
network use testnet
```
## List Accounts
```bash
# Lists all accounts associated with current network
account ls
```

## Switch Accounts
```bash
# Switch to use any account that has been recovered and exists in `account ls`
account use 0.0.xxxx
```

## Recover Accounts
```bash
# Recovering a Hedera account using 24 recovery words, where words are separated by spaces. This is default.
account recovery 0.0.xxxx
```

## Create Account
```bash
# Creates a new Hedera account using NEW recovery words and keypair. This is default.
account create -b 100000000

# Creates a new Hedera account using OPERATOR's keypair 
account create -b 100000000 -k
```

## Delete Account
```bash
# Deletes an account from Hedera, and transfers the remaining funds from the deleted account to the new account
account delete -o 0.0.1001 -n 0.0.1002
account delete --oldAccount 0.0.1001 --newAccount 0.0.1002
```
## Account Balance
```bash
# Gets the balance of an account
account balance 0.0.xxxx
```
## Crypto Transfer
```bash
# Transfer in tinybars
transfer single -a 0.0.1001 -tb 4400 
transfer single --accountId 0.0.1001 --recipientAmtTinyBars 4400

# Transfer in hbars
transfer single -a 0.0.1001 -hb 0.00044 
transfer single --accountId 0.0.1001 --recipientAmtHBars 0.00044
```

## Crypto Transfer Multiple
```bash
# Transfer in tinybars
transfer multiple -a 0.0.1001,0.0.1002,0.0.1003 -tb 440000,550000,660000 
transfer multiple --accountId 0.0.1001,0.0.1002,0.0.1003 --recipientAmtTinyBars 44000,55000,66000

# Transfer in hbars
transfer multiple -a 0.0.1001,0.0.1002,0.0.1003 -hb 0.44,1.55,22.66
transfer multiple --accountId 0.0.1001,0.0.1002,0.0.1003 --recipientAmtHBars 0.44,1.55,22.66
```

TODO
```bash

# Creates a new Hedera account using ANY public key 
account create -b 100000000 -pk

# Recovering a Hedera account only using keypairs. (Perhaps you've misplaced your words)
account recovery 0.0.xxxx -k

# Changes the default account 
account use default 0.0.xxxx

# Enable topups (kiv multisig account)
account topup 0.0.xxxx
```