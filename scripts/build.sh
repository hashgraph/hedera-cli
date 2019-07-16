#!/bin/sh

# create the fat jar with shadow jar plugin
./gradlew shadowJar

# copy out the fat jar
cp build/libs/hedera-cli-all.jar hedera.jar

# pack our fat jar into a linux/unix executable
cat ./scripts/stub.sh hedera.jar > hedera && chmod +x hedera

# clean up
rm hedera.jar 2> /dev/null 

# once the hedera executable is moved to a PATH directory,
# it should be available anywhere as a command line program
echo "\nhedera binary created in this directory.\nMove hedera binary to your PATH.\n"