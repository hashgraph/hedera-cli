#!/usr/bin/env bash

source ./blackbox/read_var.sh
expect <<EOF
set timeout -1
spawn $(read_var EXEC_JAR) -X account default $(read_var TEST_ACCOUNT_ID_DEFAULT)

expect "Default operator updated true"
EOF