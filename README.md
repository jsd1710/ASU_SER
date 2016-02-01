#Useage
SockServer <delay in ms>
SockServer <delay in ms> <xml file name>

SockClient <number to add>
SockClient <client ID> <number to add>
SockClient <client ID> <number to add> <time delay>

#Examples
java -cp bin SockServer //Runs the server

java -cp bin SockClient 100 //Adds 100 to default Client (id = 0 at first, but becomes last connected client)
java -cp bin SockClient 1 100 //Adds 100 to Client 1
java -cp bin SockClient reset //Resets total value for default client (id = 0 at first, but becomes last connected client)
java -cp bin SockClient 1 reset //Reset total value for Client 1