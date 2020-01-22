#!/usr/bin/expect

set timeout -1

set EXEC_JAR "$env(EXEC_JAR)"
set TEST_ACCOUNT_ID "$env(TEST_ACCOUNT_ID)"
set TEST_ACCOUNT_ID_2 "$env(TEST_ACCOUNT_ID_RECOVERY)"
set MEMO "Cryptotransfer integration test"

# transfer from test account to test recovery account and then transfer back
spawn $EXEC_JAR -X transfer -r $TEST_ACCOUNT_ID_2 -hb 1
expect "Memo field: "
send "$MEMO\r"

expect "Operator"
expect $TEST_ACCOUNT_ID
expect "Transfer List"

expect "Is this correct?"
expect "yes/no: "
send "yes\r"

expect "Info is correct, senders will need to sign the transaction to release funds"
expect "transferring..."
expect "$TEST_ACCOUNT_ID operator balance AFTER = *"
expect "Transfer receipt status: SUCCESS"
expect "Transfer memo: $MEMO"
expect "TransactionID : $TEST_ACCOUNT_ID *"

# change operator account and transfer amount back
spawn $EXEC_JAR -X account default $TEST_ACCOUNT_ID_2
expect "Default operator updated true"
spawn $EXEC_JAR -X transfer -r $TEST_ACCOUNT_ID -hb 1
expect "Memo field: "
send "$MEMO\r"

expect "Operator"
expect $TEST_ACCOUNT_ID_2
expect "Transfer List"

expect "Is this correct?"
expect "yes/no: "
send "yes\r"

expect "Info is correct, senders will need to sign the transaction to release funds"
expect "transferring..."
expect "$TEST_ACCOUNT_ID_2 operator balance AFTER = *"
expect "Transfer receipt status: SUCCESS"
expect "Transfer memo: $MEMO"
expect "TransactionID : $TEST_ACCOUNT_ID_2 *"

# set operator back
spawn $EXEC_JAR -X account default $TEST_ACCOUNT_ID
expect "Default operator updated true"
