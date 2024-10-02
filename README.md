# Link-State Routing #

## Project Idea ##

This project was given through my Networking class at Western Washington University and through professor See-Mong Tan. The test
provided to run the program are written by See-Mong Tan and the makefile is also provided by See-Mong Tan.

## Background ##

Link state routing is typically used on a small scale in intra-domain networks, with examples like small campuses or even individual buildings on campuses. 
These networks are connected by routers and each of these routers run the link state protocol to learn about who other routers are connected to, and to work 
to achieve global knowledge. Due to link state routing requiring ever router has global knowledge, it only works for small scale networks. 

## The Protocol For Routers ##

<pre>  1. Determine you neighbors
  2. Tell other switches who your neighbors are
  3. Use the global knowledge to find shortest path (Can implement Dijkstras, but not implemented in this assignment)</pre>

## A More Deep Dive Into Protocol ##

### Determining Neighbors ###

Routers do not know who their neighbors are initially, the simplest way to implement this is to periodically send keep alive messages containing 
a routers id (Insert more info here). If the sending router goes offline, it will not continue sending keep alive messages and the router after
a set timer will delete the router from its list of neighbors. 

### Finding Path ###

Finding the shortest path is not implemented, but can easily be by using a shortest path algorithm like dijkstras.

### Visual Representations (provided by See-Mong Tan) 

![alt_text](Link-State-Routing/images/lsr.png)

![drawing](Link-State-Routing/images/node.png)

![drawing](Link-State-Routing/images/architecture.png)
