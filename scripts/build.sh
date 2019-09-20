#!/bin/sh

# build gradle
echo "clean, build (and, by default, test)"
./gradlew clean build jacocoTestReport coveralls
