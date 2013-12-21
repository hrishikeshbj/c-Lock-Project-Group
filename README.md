c-Lock-Project-Group
====================

A distributed lock manager. Various large-scale distributed computing systems such as grids, clusters and other cloud services handle an ever increasing flow of data. This data is stored on remote storage and accessed in an ad-hoc manner. To maintain integrity of data, it is necessary to have an effective concurrency control (locking) mechanism in order that the shared storage can be collaborated among multiple users without collisions. C-Lock is an attempt to build such concurrency control framework for distributed systems.
1. User Module: User applications can create resources and lock these resources with c-Lock. 
	a: REST Client to communicate with c-Lock server.
	b: Resource and lock containers.
	c: Error reporting.	
	d: Heartbeating thread to maintain the locks and activity of the client.
	e: Leader election co-ordinator library.
2. c-Lock Server: The engine of c-Lock multiple c-Lock servers can be deployed in the network and user modules can talk to any c-Lock server. Complete replica of resources and locks is maintained at each server and each event is co-ordinated with other servers using gossip communication.
	a: REST server to connect with clients.
	b: Database of the resources and states registered by users. (sqlite db)
	c: In memory replica of locks granted and requests rejected.
	d: ServerListener to communicate with ther servers (Gossip protocol based communication.)
	e: Lockwalker: stagnant locks reclaimer and deadlock detection.

c-Lock is a specific approach towards distributed lock manager with performance comparable to Apache Zookeeper, eliminating some drawbacks of Zookeeper like no single master that is single point of failure, broadcasting events (Gossip generates n lg n messages against broadcasting creates n^2 messages) and providing extra features like explicit state control and deadlock detection.