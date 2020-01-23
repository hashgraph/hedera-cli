#!/usr/bin/expect

set timeout -1

# create 1st new account for the keys to be updated and delete the account subsequently
set EXEC_JAR "$env(EXEC_JAR)"
spawn $EXEC_JAR -X hcs create hello
expect "hello, HCS! "
