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
account recovery 0.0.xxxx
```
## Create Account
```bash
# valid commands
account create -b 100000000 
account create --balance 100000000
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

