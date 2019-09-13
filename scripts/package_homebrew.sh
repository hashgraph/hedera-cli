#!/usr/bin/env bash

HOMEBREW_TAP_REPO=homebrew-tap

git clone git@github.com:${GH_USER}/${HOMEBREW_TAP_REPO}.git
cd ${HOMEBREW_TAP_REPO}

./scripts/sha256update.rb "${GH_REPO}"

git add -u
git commit -m "${GH_REPO} ${VERSION} release"
git push

cd ..