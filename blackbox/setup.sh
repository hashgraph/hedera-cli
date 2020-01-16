#!/usr/bin/env bash

expect <<EOF
set timeout -1

spawn $EXEC_JAR -X setup

if {[file exists [file join ~/.hedera/testnet/accounts/default.txt]]} {
    expect "You have already setup a default Hedera account."
    expect "Use \`account recovery\` command to import another account"
    expect "or \`account default\` command to set a different default account"
    expect "if you would like to change this default account.\n"
} else {
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
}

EOF