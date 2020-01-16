#!/bin/bash

read_var() {
  if [ -z "$1" ]; then
    echo "environment variable name is required"
    return
  fi

  local ENV_FILE='.env'
  if [ ! -z "$2" ]; then
    ENV_FILE="$2"
  fi

  local VAR="$(grep $1 "$ENV_FILE" | xargs)"
  IFS="=" read -ra VAR <<< "$VAR"
  echo "${VAR[1]}"
}