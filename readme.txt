There is two java file(Server.java & Client.java). One is for server and the other one is for Client.
Steps-
1)Compile these java files(command : javac filename).
2)Start the Server by executing the Server class(command: java Server). Then you can connect clients to the server by executing Client class(command: java Client)(you have to execute the Client class for every client).
  Client and Server generates a key using the Diffey-Hellman key exchange algorithm for their communication.    
  Every pair of Client and Server generates a key.
3)Then Client is asked for a Nick Name.(You shouldn't give a nickname which is already used by some other online client because it will not be accepted). If you give a used nick name, Server will show the list of used nicknames and again ask you to enter nick name.
4)Now the client can send message to the other clients(syntax of the message should be like message@recipient_name like "Hello Rohit@rkundu". If recipient_name is not mentioned then the message is broadcasted to every other client like "This is a broadcasting message").
  The message will be encrypted using the key before sending. 
  Server decrypts the message and sends the message to the appropriate client(s) in encrypted form(Here the format is changed before encrypting. Format of the message becomes "sender_name : message").
  Reciver decrypts the message and shows the message.
5)Client can send "logout" to log out.
