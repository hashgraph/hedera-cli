#!/usr/bin/env bash

expect <<EOF
set timeout -1
spawn $EXEC_JAR -X account balance $TEST_ACCOUNT_ID

expect "Balance: "

EOF