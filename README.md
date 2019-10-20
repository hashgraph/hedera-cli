[![CircleCI](https://circleci.com/gh/hashgraph/hedera-cli/tree/master.svg?style=shield)](https://circleci.com/gh/hashgraph/hedera-cli/tree/master) [![Coverage Status](https://coveralls.io/repos/github/hashgraph/hedera-cli/badge.svg?branch=test-coverage-jacoco)](https://coveralls.io/github/hashgraph/hedera-cli?branch=test-coverage-jacoco) [![GitHub](https://img.shields.io/github/license/hashgraph/hedera-mirror-node)](LICENSE)

Getting Started
===

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
## Switch Accounts
```bash
account use 0.0.xxxx
```
## Recover Accounts
```bash
# Recovering a Hedera account using 24 recovery words, where words are separated by spaces. This is default.
account recovery 0.0.xxxx
# Recovering a Hedera account only using keypairs. (Perhaps you've misplaced your words)
TODO
account recovery 0.0.xxxx -k

```
## Create Account
```bash
# Creates a new Hedera account using NEW recovery words and keypair. This is default.
account create -b 100000000

# Creates a new Hedera account using OPERATOR's keypair 
account create -b 100000000 -k

# Creates a new Hedera account using ANY public key 
TODO
```
## Delete Account
```bash
# valid commands
account delete -o 0.0.1001 -n 0.0.1002
account delete --oldAccount 0.0.1001 --newAccount 0.0.1002
```
## Crypto Transfer
```bash
# valid commands
transfer single -a 1001 -r 44 
transfer single --accountId 1001 --recipientAmt 44
```
## Crypto Transfer Multiple

```bash
# valid commands
transfer multiple -a 1001,1002,1003 -r 44,55,66 
transfer multiple --accountId 1001,1002,1003 --recipientAmt 44,55,66
```

