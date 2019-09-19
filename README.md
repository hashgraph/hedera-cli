[![CircleCI](https://circleci.com/gh/hashgraph/hedera-cli/tree/master.svg?style=shield)](https://circleci.com/gh/hashgraph/hedera-cli/tree/master)

Getting Started
===

```bash
brew tap hashgraph/hedera-cli
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

# To select network
network set -n=engnet1 OR
network set -n=mainnet OR
network set -n=wallet

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

Create Account
===

```bash
account create -b=100 
OR
account create --balance=100
```

Crypto Transfer
===

```bash
transfer single -r=1001,-a=44 
OR
transfer single -recipient=1001,-recipientAmt=44

```

Crypto Transfer Multiple
===

```bash
transfer multiple -r=1001,1002,1003,-a=44,55,66 
OR
transfer multiple -recipient=1001,1002,1003,-recipientAmt=44,55,66

```