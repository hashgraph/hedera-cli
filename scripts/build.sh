#!/bin/sh

# clean gradle
echo "cleaning up the ocean..."
./gradlew clean

# build gradle
echo "rebuilding the world..."
./gradlew clean build

# copy out the executable jar built with Spring Boot's LaunchScript
cp build/libs/hedera-cli-0.0.1.jar hedera && chmod +x hedera

# once the hedera executable is moved to a PATH directory,
# it should be available anywhere as a command line program
echo "\nhedera binary created in this directory.\nMove hedera binary to your PATH.\n"