# Distributed-Factorization-By-Trial-Division

The server will send packages which are ranges of numbers to clients that connect to it. When a factor of the target number is found, the server and all clients will exit.

Server is run with runServer.sh in server directory. First argument is the number to factorize, second argument is the number of packages (degree of parallelism).
A larger degree of parallelism allows more clients to execute simultaneously but for smaller numbers, a low degree of parallelism is optimal due to unnecessary overheads when the server repeatedly sends small packages to the client.

Client is run with runClient.sh in client directory. No additional arguments are needed.

This program was created to demonstrate the effectiveness and increase in speed offered by distributing processes, however trial division is slow (exponential time) and should not be used for extremely large numbers.
