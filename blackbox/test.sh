#!/usr/bin/env bash

# we will need coreutils (via `brew install coreutils`) on macOS

# rm -irf $HOME/.hedera

source ./blackbox/read_var.sh
expect <<EOF
set timeout -1
spawn $(read_var EXEC_JAR) -X network ls
expect "  mainnet"
expect "* testnet"

spawn $(read_var EXEC_JAR) -X setup
expect "default account does not exist"
expect "Start the setup process"
expect "account ID in the format of 0.0.xxxx that will be used as default operator: "
send -- "${TEST_ACCOUNT_ID}\r"

expect "Recover account using 24 words or keys? Enter words/keys: "
send -- "words\r"

expect "24 words phrase: "
send -- "${TEST_PASSPHRASE}\r"

expect "Have you migrated your account on Hedera wallet? If migrated, enter \`bip\`, else enter \`hgc\`: "
send -- "bip\r"

expect "Account recovered and verified with Hedera"
expect "${TEST_ACCOUNT_ID} saved"
expect "{
\"accountId\" : \"${TEST_ACCOUNT_ID}\",
\"privateKey\" : \"${TEST_PK}\",
\"publicKey\" : \"${TEST_PUB}\",
\"privateKeyEncoded\" : \"${TEST_PK_ENCODED}\",
\"publicKeyEncoded\" : \"${TEST_PUB_ENCODED}\",
\"privateKeyBrowserCompatible\" : \"${TEST_PK_BROWSER}\"
}"
EOF