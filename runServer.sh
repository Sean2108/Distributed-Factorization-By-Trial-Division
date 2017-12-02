#!/bin/bash - 
#===============================================================================
#
#          FILE: runServer.sh
# 
#         USAGE: ./runServer.sh 
# 
#   DESCRIPTION: 
# 
#       OPTIONS: ---
#  REQUIREMENTS: ---
#          BUGS: ---
#         NOTES: ---
#        AUTHOR: Sean 
#  ORGANIZATION: 
#       CREATED: 10/05/2017 11:50
#      REVISION:  ---
#===============================================================================

set -o nounset                              # Treat unset variables as an error

cd server
rmiregistry &
java -Djava.security.policy=policy.txt FactorServer $1 $2
## example numbers:
#36893488065814724653
#2417851639014853581997859  
#4503591742082057
