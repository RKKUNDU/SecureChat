There is two java file(Server.java & Client.java). One is for server and the other one is for Client.
These java files are compiled (command : javac filename).
Server is first started by executing the Server class(command: java Server). Then clients are connected to the server by executing Client class(command: java Client)(you have to execute the Client class for every client).
1)Client and Server generates a key using the Diffey-Hellman key exchange algorithm for their communication.
2)Then Client is asked for a Nick Name.( You shouldn't give a nickname which is already used because it will not be accepted). If you give a used nick name, Server will show the list of nicknames.
3)Now the client can send message to the other clients(syntax of the message should be like message@recipient_name like "Hello Rohit@rkundu". If recipient_name is not mentioned then the message is broadcasted to every online client like "This is a broadcasting message"). The message will be encrypted using the key. 
4)Server decrypts the message and send the message to the appropriate client(s) in encrypted form(Here the format is changed before encrypting. Format of the message is "sender_name : message").
5)Reciver decrypts the message and shows the message.
6)Client can send "logout" to log out.
