[![CircleCI](https://circleci.com/gh/hashgraph/hedera-cli/tree/master.svg?style=shield)](https://circleci.com/gh/hashgraph/hedera-cli/tree/master) [![Coverage Status](https://coveralls.io/repos/github/hashgraph/hedera-cli/badge.svg?branch=test-coverage-jacoco)](https://coveralls.io/github/hashgraph/hedera-cli?branch=test-coverage-jacoco) [![GitHub](https://img.shields.io/github/license/hashgraph/hedera-mirror-node)](LICENSE)

Getting Started
===
```bash
brew tap hashgraph/tap
brew install hedera-cli
```
Run
===
```bash
hedera
```
Help
===
```bash
# shows all the available commands
help
```
Setup Network
===
```bash
# To list available networks
network ls

# To select/change network
network set -n=aspen OR
network set -n=mainnet OR
network set -n=external

# Cli will prompt setup on first run to save default operator key into ~/.hedera
setup
```
Create Account
===
```bash
account create -b=100 
OR
account create --balance=100
```
File Create
===
```bash
# where -d is file expiration date, must include time
file create -d=dd-MM-yyyy,hh:mm:ss
OR
file create --date=dd-MM-yyyy,hh:mm:ss
```
Crypto Transfer
===
```bash
transfer single -a=1001,-r=44 
OR
transfer single --accountId=1001,--recipientAmt=44
```
Crypto Transfer Multiple
===
```bash
transfer multiple -a=1001,1002,1003,-r=44,55,66 
OR
transfer multiple --accountId=1001,1002,1003,--recipientAmt=44,55,66
```
Switch Accounts
===
```bash
account use -a=0.0.xxxx
```
