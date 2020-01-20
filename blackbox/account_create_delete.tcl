#!/usr/bin/expect

set timeout -1

# create a new account for it to be deleted afterwards
set EXEC_JAR "$env(EXEC_JAR)"
spawn $EXEC_JAR -X account create -b 100000000
expect "Your recovery words (store it safely): * saved"

set accounts $expect_out(0,string)
set responseList [split $accounts "\n"]

set i 0
foreach j $responseList {
    # accountId exists
    if {$i == 2 && [regexp "accountId" $j]} {
        set accountIdList [split $j "\"*\""]
        set accountId [lindex $accountIdList 3]
        puts $accountId
    } 
    # privateKey exists
    if {$i == 3 && [regexp "privateKey" $j]} {
        set privateKeyList [split $j "\"*\""]
        set privateKey [lindex $privateKeyList 3]
        puts $privateKey
    }
    incr i
}

# delete the account
set TEST_ACCOUNT_ID "$env(TEST_ACCOUNT_ID)"
spawn $EXEC_JAR -X account delete -o $accountId -n $TEST_ACCOUNT_ID
expect "Enter the private key of the account to be deleted: "
send "$privateKey\r"

expect "Account to be deleted: $accountId\n
Funds from deleted account to be transferred to: $TEST_ACCOUNT_ID\n
\n
Is this correct?\n
yes/no: "
send "yes\r"

expect "Info is correct, let's go! \n
SUCCESS\n
Account $TEST_ACCOUNT_ID new balance is *\n
File deleted from disk true"
