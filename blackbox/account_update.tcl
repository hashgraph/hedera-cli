#!usr/bin/expect

set timeout -1

# create 2 new accounts for the keys to be updated and delete the account subsequently
set EXEC_JAR "$env(EXEC_JAR)"
spawn $EXEC_JAR -X account create -b 100000000
expect "Your recovery words (store it safely): * saved"

set accounts $expect_out(0,string)
set responseList [split $accounts "\n"]

set i 0
foreach j $responseList {
    # accountId exists
    if { $i == 2 && [regexp "accountId" $j]} {
        set accountIdList [split $j] "\"*\""]
        set accountId [lindex $accountIdList 3]
        puts $accountId
    }
    # private key exists
    if { $i == 3 && [regexp "privateKey" $j]} {
        set privateKeyList [split $j] "\"*\""]
        set privateKey [lindex $privateKeyList 3]
        puts $privateKey
    }
    # public key exists
    if { $i == 4 && [regexp "publicKey" $j]} {
        set publicKeyList [split $j] "\"*\""]
        set publicKey [lindex $publicKeyList 3]
        puts $publicKey
    }
    incr 1
}

# update the account
set TEST_ACCOUNT_ID "$env(TEST_ACCOUNT_ID)"
spawn $EXEC_JAR -X account update $accountId
expect "Enter the NEW private key that will be used to update $accountId"
send "$privateKey\r"

expect "Enter the ORIGINAL private key of $accountId that will be changed"
send "$TEST_PK\r"

expect "Account to be updated: $accountId"
expect "Public key of account will be updated from: "
expect "Public key in HEX: $TEST_PUB"
expect "To new public key: "
expect "NEW Public key in HEX: $publicKey" 
expect "Is this correct?"
expect "yes/no"
send "yes\r"

expect "Info is correct, let's go!"
expect "Account updated: SUCCESS"
expect "Retrieving account info to verify the current key.."
expect "Public key in Encoded form: "
expect "File updated in disk true"

# write a test that updates account with operator key and throw this error
# transaction already signed with key: