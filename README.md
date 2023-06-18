# BroadcastSync
Java framework based on UDP protocol that use broadcast message to send messages between nodes

The BroadCastSync framework is designed to facilitate communication and synchronisation between nodes within a network through high-performance communication. 
BroadCastSync implements user-transparent data flow control by subdividing and recomposing messages sent over the network. 

To send messages over the network, BroadCastSync provides three strategies:

sendMessage(byte[] data): Allows an array of bytes to be sent over the network without length limitation.
sendMessage(String data): Allows a string to be sent over the network without length limitation.
sendMessage(Serializable object): Sends a Java object that implements the Serializable interface.

In addition, if enabled in the configuration, the framework also starts the heartbeat emitter for monitoring the connectivity of nodes in the network.

