#!/bin/bash

# Development version of hedera-cli that runs TypeScript directly
# Usage: ./hcli-dev.sh <command> [args...]

# Set the config file to the local one
export HCLI_CONFIG_FILE=./hedera-cli.config.json

# Run the TypeScript version directly
npx ts-node src/hedera-cli.ts "$@"
