Project Setup
1. Make sure you have gradle `brew install gradle`
2. Clone repository and change directory to `hedera-cli`
3. copy `.env.sample` and create `.env` with node id, node address as well as operator's id and private key


To run
```bash
gradle build
./gradlew run
```

For account creation run
```bash
gradle runCreateAccount
```


For crypto transfer run
```bash
gradle runCryptoTransfer
```