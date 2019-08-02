Project Setup
===

```
git clone https://github.com/hashgraph/hedera-cli
cp .env.sample .env
# update .env with node id, node address, 
# operator's id and private key
```

Generating the Executable
===

```bash
# Cleans gradle, builds gradle and jar
./scripts/build.sh
```

Get Started
===

```bash
# Runs the hedera shell
./hedera
```


Help
===

```bash
# shows all the available commands
help
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