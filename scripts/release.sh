#!/usr/bin/env bash

file="../gradle.properties"
while IFS= read -r line
do
    # display $line or do somthing with $line
    VERSION="$(echo $line | cut -d'=' -f2)"
    printf '%s\n' "v$B will be released"
done <"$file"

GH_USER=hashgraph
GH_PATH=$GITHUB_API_TOKEN
GH_REPO=hedera-cli
GH_TARGET=master
ASSETS_PATH=.
NAME=hedera
tar -zcvf "${NAME}-${VERSION}.tar.gz" .

git add -u
git commit -m "$VERSION release"
git push

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

rm ${ASSETS_PATH}/${file_name}
