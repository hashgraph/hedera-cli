#!/usr/bin/expect

set timeout -1

# create 1st new account for the keys to be updated and delete the account subsequently
set EXEC_JAR "$env(EXEC_JAR)"
spawn $EXEC_JAR -X account create -b 100000000
expect "Your recovery words (store it safely): * saved"

set accounts $expect_out(0,string)
set responseList [split $accounts "\n"]

set i 0
foreach j $responseList {
    # accountId exists
    if {$i == 2 && [regexp "accountId" $j]} {
        set accountIdList [split $j] "\"*\""]
        set accountId [lindex $accountIdList 3]
        puts $accountId
    }
    # private key exists
    if {$i == 3 && [regexp "privateKey" $j]} {
        set privateKeyList [split $j] "\"*\""]
        set privateKey [lindex $privateKeyList 3]
        puts $privateKey
    }
    # public key exists
    if {$i == 4 && [regexp "publicKey" $j]} {
        puts $j
        set publicKeyList [split $j] "\"*\""]
        set publicKey [lindex $publicKeyList 3]
        puts $publicKey
        puts "hello"
        puts $publicKey
    }
    incr i
}

# create 2nd account for the keys to be updated and delete the accounts subsequently
spawn $EXEC_JAR -X account create -b 100000000
expect "Your recovery words (store it safely): * saved"

set accounts2 $expect_out(0,string)
set responseList2 [split $accounts2 "\n"]

set i2 0
foreach j2 $responseList2 {
    # accountId exists
    if {$i2 == 2 && [regexp "accountId" $j2]} {
        set accountIdList2 [split $j2] "\"*\""]
        set accountId2 [lindex $accountIdList2 3]
        puts $accountId2
    }
    # private key exists
    if {$i2 == 3 && [regexp "privateKey" $j2]} {
        set privateKeyList2 [split $j2] "\"*\""]
        set privateKey2 [lindex $privateKeyList2 3]
        puts $privateKey2
    }
    # public key exists
    if {$i2 == 4 && [regexp "publicKey" $j2]} {
        set publicKeyList2 [split $j2] "\"*\""]
        set publicKey2 [lindex $publicKeyList2 3]
        puts $publicKey2
    }
    incr i2
}

# update the account
set TEST_ACCOUNT_ID "$env(TEST_ACCOUNT_ID)"
spawn $EXEC_JAR -X account update $accountId
expect "Enter the NEW private key that will be used to update $accountId"
send "$privateKey2\r"

expect "Enter the ORIGINAL private key of $accountId that will be changed"
send "$privateKey\r"

expect "Account to be updated: $accountId"
expect "Public key of account will be updated from: "
expect "To new public key: "
expect "Is this correct?"
expect "yes/no"
send "yes\r"

expect "Info is correct, let's go!"
expect "Account updated: SUCCESS"
expect "Retrieving account info to verify the current key.."
expect "File updated in disk true"


# delete both accounts generated
spawn $EXEC_JAR -X account delete -o $accountId -n $TEST_ACCOUNT_ID
expect "Enter the private key of the account to be deleted: "
send "$privateKey2\r"

expect "Account to be deleted: $accountId"
expect "Funds from deleted account to be transferred to: $TEST_ACCOUNT_ID"
expect "\n"
expect "Is this correct?"
expect "yes/no: "
send "yes\r"

expect "Info is correct, let's go! \n
SUCCESS\n
Account $TEST_ACCOUNT_ID new balance is *\n
File deleted from disk true"

# delete both accounts generated
spawn $EXEC_JAR -X account delete -o $accountId2 -n $TEST_ACCOUNT_ID
expect "Enter the private key of the account to be deleted: "
send "$privateKey2\r"

expect "Account to be deleted: $accountId2"
expect "Funds from deleted account to be transferred to: $TEST_ACCOUNT_ID"
expect "\n"
expect "Is this correct?"
expect "yes/no: "
send "yes\r"

expect "Info is correct, let's go! \n
SUCCESS\n
Account $TEST_ACCOUNT_ID new balance is *\n
File deleted from disk true"

# write a test that updates account with operator key and throw this error
# transaction already signed with key: