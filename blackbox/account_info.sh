#!/usr/bin/env bash

source ./blackbox/read_var.sh

expect << EOF
set timeout -1

spawn $(read_var EXEC_JAR) -X account info $(read_var TEST_ACCOUNT_ID)

expect "{
  \"key\" :*,
  \"proxyAccountId\" : \"0.0.0\",
  \"proxyReceived\" : 0,
  *
}"
EOF