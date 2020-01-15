#!/usr/bin/env bash

read_var() {
  if [ -z "$1" ]; then
    echo "environment variable name is required"
    return
  fi

  local ENV_FILE='.env'
  if [ ! -z "$2" ]; then
    ENV_FILE="$2"
  fi

  local VAR=$(grep $1 "$ENV_FILE" | xargs)
  IFS="=" read -ra VAR <<< "$VAR"
  echo ${VAR[1]}
}

expect <<EOF
set timeout -1
spawn ./build/libs/hedera-cli-0.1.6.jar -X account balance $(read_var TEST_ACCOUNT_ID)

expect "Balance: "

EOF