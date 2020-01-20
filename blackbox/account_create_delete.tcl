#!/usr/bin/expect

set timeout -1
set val "blah"
puts $val

# x=$(expect -c 'send \"hello\"; exit 5;')
# echo $?; 
# echo "heee"
# echo $x

# y=$(expect -c 'expect <<
# puts \"world\"
# ')
# echo $y

# create a new account for it to be deleted afterwards
set EXEC_JAR "$env(EXEC_JAR)"
spawn $EXEC_JAR -X account create -b 100000000
expect "Your recovery words (store it safely): * saved"

set accounts $expect_out(0,string)
set responseList [split $accounts "\n"]

set i 0
foreach j $responseList {
    if {$i == 2} {
        # puts "We found our accountId $j"
        set accountIdList [split $j "\"*\""]
        set accountId [lindex $accountIdList 3]
        puts $accountId
    } 
    if {$i == 3} {
        set privateKeyList [split $j "\"*\""]
        set privateKey [lindex $privateKeyList 3]
        puts $privateKey
    }
    incr i
}

# set testRegMatch "will any of these match? \n or will it exist here?"
# if {[regexp "match" $testRegMatch]} {
#     puts "hello"
# } else {
#     puts "nope, not matched"
# }

# set val $expect_out(0,string)
## prints whether or not this is a command
# puts $accounts
# prints another time when there are 2 $val puts command not sure why 
# puts $val

# set acc | grep -P (?<="\"accountId\" : ")
# puts $acc

# foreach item $parameters {
#   if {[regexp "var1\\s*=\\s*(\\w+);" $item wholeMatch myVal]} {
#        break
#   }
# }
# puts "value is '$myVal'"


# # # delete the account
# # spawn $EXEC_JAR -X account delete -o thevariablehere -n $TEST_ACCOUNT_ID
# EOF

# grep "\"accountId\" : ?"