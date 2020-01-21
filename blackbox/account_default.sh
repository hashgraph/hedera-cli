#!/usr/bin/env bash

expect <<EOF
set timeout -1
spawn $EXEC_JAR -X account default $TEST_ACCOUNT_ID_DEFAULT
expect "Default operator updated true"

# set the default operator back to the test accountId
spawn $EXEC_JAR -X account default $TEST_ACCOUNT_ID
expect "Default operator updated true"

EOF