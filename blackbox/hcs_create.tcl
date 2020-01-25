#!/usr/bin/expect

set timeout -1

# create 1st new account for the keys to be updated and delete the account subsequently
set EXEC_JAR "$env(EXEC_JAR)"
spawn $EXEC_JAR -X network use previewnet
expect {
    "Setting network to previewnet" {
        expect {
            "To see available networks, enter \`network ls\`" {
                expect "You do not have a default operator account for this network. Please run \`setup\`"
                send "$TEST_ACCOUNT_ID_PREVIEWNET\r"
                expect "Recover account using 24 words or keys? Enter words/keys: "
                send "keys/r"
                expect "Enter the private key of account $TEST_ACCOUNT_ID_PREVIEWNET: "
                send "$TEST_PK/r"
                expect "Account recovered and verified with Hedera"
                expect "$TEST_ACCOUNT_ID_PREVIEWNET saved"
                expect "\"accountId\" : \"$TEST_ACCOUNT_ID_PREVIEWNET\""
                expect "\"privateKey\" : \"$TEST_PK_PREVIEWNET\""
                expect "\"publicKey\" : \"$TEST_PUB_PREVIEWNET\""
                }
            
            "" {
                spawn $EXEC_JAR -X hcs create hello
                expect "TransactionId: *"
                expect "TopicId: *"
            }
        }
    }
}



