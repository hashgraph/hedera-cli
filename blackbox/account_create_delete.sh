#!/usr/bin/env bash

x=$(expect -c 'send \"hello\"; exit 5;')
echo $?; 
echo "heee"
echo $x

y=$(expect -c 'expect <<
puts \"world\"
')
echo $y

expect <<EOF
set timeout -1
# create a new account for it to be deleted afterwards
spawn $EXEC_JAR -X account create -b 100000000 | head -c 200
expect "Your recovery words (store it safely): * saved"
set val [\$expect_out(0,string)];
puts $val

EOF