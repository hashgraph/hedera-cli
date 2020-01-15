#!/usr/bin/env bash

source ./blackbox/read_var.sh
expect <<EOF
set timeout -1
spawn $(read_var EXEC_JAR) -X account balance $(read_var TEST_ACCOUNT_ID)

expect "Balance: "

EOF