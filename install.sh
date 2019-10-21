#!/bin/bash -

VERSION=$(curl -s https://api.github.com/repos/hashgraph/hedera-cli/releases/latest | grep "tag_name" | cut -d '"' -f 4)
echo "Latest hedera-cli is $VERSION"

DOWNLOAD_URL=$(curl -s https://api.github.com/repos/hashgraph/hedera-cli/releases/latest | grep "browser_download_url" | cut -d '"' -f 4)
FILENAME=$(echo "${DOWNLOAD_URL##*/}")

# download tar.gz
echo "Downloading $FILENAME"
wget --show-progress "$DOWNLOAD_URL" -O "$FILENAME"

# unpack and install into path
echo "Installing $FILENAME"
tar -C /usr/local/bin -xvf "$FILENAME"

# remove downloaded tar.gz
echo "Cleaning up"
rm "$FILENAME"

