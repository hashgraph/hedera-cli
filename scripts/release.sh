#!/usr/bin/env bash

file="./gradle.properties"
while IFS= read -r line
do
    # display $line or do somthing with $line
    export VERSION="$(echo $line | cut -d'=' -f2)"
    printf '%s\n' "v${VERSION} will be released"
done <"$file"

export GH_USER=hashgraph
export GH_PATH=$GITHUB_API_TOKEN
export GH_REPO=hedera-cli
GH_TARGET=master
ASSETS_PATH=.
NAME=hedera
SHA256="$(sha256sum ${NAME}-${VERSION}.tar.gz | cut -d' ' -f1)"
PACKAGE="${NAME}-${VERSION}.tar.gz"

tar -zcvf "${PACKAGE}" .

git add -u
git commit -m "$VERSION release"
git push

echo "${SHA256}" > hash.txt
echo "${VERSION}" > version.txt
echo "${PACKAGE}" > package.txt

res=`curl --user "$GH_USER:$GH_PATH" -X POST https://api.github.com/repos/${GH_USER}/${GH_REPO}/releases \
-d "
{
  \"tag_name\": \"v$VERSION\",
  \"target_commitish\": \"$GH_TARGET\",
  \"name\": \"v$VERSION\",
  \"body\": \"new version $VERSION\",
  \"draft\": false,
  \"prerelease\": false
}"`
echo Create release result: ${res}
rel_id=`echo ${res} | python -c 'import json,sys;print(json.load(sys.stdin)["id"])'`
file_name=${NAME}-${VERSION}.tar.gz

curl --user "$GH_USER:$GH_PATH" -X POST https://uploads.github.com/repos/${GH_USER}/${GH_REPO}/releases/${rel_id}/assets?name=${file_name}\
 --header 'Content-Type: text/javascript ' --upload-file ${ASSETS_PATH}/${file_name}

source package_homebrew.sh

# clean up
rm ${ASSETS_PATH}/${file_name}
rm hash.txt
rm version.txt
rm package.txt