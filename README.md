Project Setup
===

* Gradle version 5.0+ is required.
* Gradle can be installed via your OS' package manager
* Alternatively, use [sdkman.io](https://sdkman.io/install) to manage all your java/gradle tooling

```
git clone https://github.com/hashgraph/hedera-cli
cp .env.sample .env
# update .env with node id, node address, 
# operator's id and private key
```

Generating the Executable
===

```
./scripts/build.sh
```

Get Started
===

```bash
gradle build
./gradlew run
```

Account Creation
===

```bash
gradle runCreateAccount
```

Crypto Transfer
===

```bash
gradle runCryptoTransfer
```