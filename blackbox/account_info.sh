#!/usr/bin/env bash

expect << EOF
set timeout -1

spawn $EXEC_JAR -X account info $TEST_ACCOUNT_ID

expect "{
  \"key\" :*,
  \"proxyAccountId\" : \"0.0.0\",
  \"proxyReceived\" : 0,
  *
}"
EOF