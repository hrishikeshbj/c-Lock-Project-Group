c-Lock-Project-Group
====================

c-Lock is a distributed lock manager. DLM is essentially a distributed infrastructure service for a cloud system. The DLM provides software applications a synchronized mechanism to access shared resources on cloud/ distributed system. In this project we propose to build an interface through which a user/ application can hit a “DLM host” and request for locks using apps (simple web service). The DLM hosts communicate among themselves based on Gossip protocol and manage a database of shared resources. The paper also deals with the deadlock detecting mechanisms. The project can be taken further to build long time lock management, leader election, etc. The project doesn’t need any cloud services as we build the cloud infrastructure itself.
