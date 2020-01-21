#!/usr/bin/expect

set timeout -1

# recover an account, and then delete that account from the local disk
# to do command to remove account from local disk

set EXEC_JAR "$env(EXEC_JAR)"
set TEST_ACCOUNT_ID_RECOVERY "$env(TEST_ACCOUNT_ID_RECOVERY)"
spawn $EXEC_JAR -X account recovery $TEST_ACCOUNT_ID_RECOVERY
expect "Start the recovery process"
expect "Recover account using 24 words or keys? Enter words/keys: "
send "keys\r"

set TEST_PK_RECOVERY "$env(TEST_PK_RECOVERY)"
expect "Enter the private key of account $TEST_ACCOUNT_ID_RECOVERY: "
send "$TEST_PK_RECOVERY\r"

# expect account exists
expect "This account already exists!"
expect "Error in recovering account"

