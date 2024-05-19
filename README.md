[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-24ddc0f5d75046c5622901739e7c5dd533143b0c8e959d652212380cedb1ea36.svg)](https://classroom.github.com/a/r_U0wNiU)
# Link State Routing
<!--- Your assignment is available [here](https://docs.google.com/document/d/1XjKAUvFsbVtMEoKBRNQYvmcXHlRIrKpH1Ga9mfvMEso/edit#). --->



![alt_text](Link-State-Routing/images/lsr.png)



## Introduction

[Link State Routing](https://en.wikipedia.org/wiki/Link-state_routing_protocol) is typically used in intra-domain networks, e.g. within a small campus, or perhaps within a building like the Communications Facility. Networks are connected to _routers_. The link state protocol is run by every router. The protocol helps the router learn about how to reach computers connected by remote routers in the network. Because link state routing gives every router knowledge about the entire network, it works only for smaller networks (but is very effective in that case).


## The Link State Routing Protocol

Link state routing protocols have several parts:



1. Determining who your neighbors are
2. Letting other switches know who your neighbors are
3. Using the information gathered to generate a path to a destination 


### Determining the Neighbors

A router does not have a priori information about what computers or other routers are connected to it. It needs to learn about this from a _reachability protocol._ As part of the connection process, the switch learns about who is connected to it and what their addresses are.

The simplest reachability protocol is for a node to periodically send and receive _keep-alive_ packets. These packets typically contain the address or id of the sending node. Once a receiving node gets the keep alive packet, it knows that the sending node is alive and has the sending id. If the sending node goes offline and doesn't continue to send any keep alives, the receiving node will delete the sending node from its list of neighbors after a suitable timeout.


### Telling Other Routers About Your Neighbors

The next part of the protocol is to tell other routers about who you are connected to. Link State Routing does this by sending an _advertisement_ to your neighbors. The advertisement includes who you are, and a list of your neighbors to which you are directly connected. It also includes a sequence number, which you increment each time you send the advertisement.

Each node remembers the sequence number of the advertisement from each node that it receives. If the advertisement is newer with a greater sequence number, it is saved. Otherwise it is discarded as out of date.

Routers forward advertisements that they receive to the neighbors that they are directly connected to (except for the link on which they received the advertisement). This way, all routers in the network rapidly learn the state of every router in the network.


### Creating a Path

Once every switch has learned about the state of every other router in the network, it can construct a map of the network. A path from one node to another is easily constructed using [Djikstra's Shortest Path Algorithm](https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm)..


### Caveats

One possible problem with the flooding approach used in telling other routers about your neighbors is if there are cycles in the network. In that case, it should be clear that advertisement packets will circulate endlessly.

One solution to this is to add a TTL field in advertisements. This is termed a _Time To Live_ value, and it is initially set to some positive value, e.g. 15. At each hop when the advertisement is forwarded, the TTL is decremented. When it reaches 0, the advertisement is discarded. This does limit the total size of any network to 15 hops.


## The Simulation


1. Determining Your Neighbors, and
2. Telling Other Switches About Your Neighbors.

We will leave Dijkstra's Shortest Path for another time.


### Objects

While networks in real life have computers, switches and routers, we are going to combine all of them in this assignment. The basic element in the simulation is the class `Node`. A node represents a network node.

This assignment is implemented in Java.


![drawing](Link-State-Routing/images/node.png)

### Architecture of Node

The figure below shows the architecture of Node that you probably want.

![drawing](Link-State-Routing/images/architecture.png)

There should be 3 threads:



1. The Keep Alive thread sends keep alives periodically to all neighbors at an interval of <strong><code>KEEPALIVEINTERVAL </code></strong>milliseconds<strong>.</strong>
2. The Advertisement thread sends advertisements periodically to all neighbors at an interval of <strong><code>ADVERTISEMENTINTERVAL </code></strong>milliseconds<strong>.</strong>
3. The Read thread receives messages and processes them. 

The Keep Alive and Advertisement threads should continue operating so long as the Node is in the On state. These messages should not be sent in the Off state.

When a Link sends a Message to a Node, it calls the node's `receiveMessage` function. The `receiveMessage` function should enqueue the Message in a blocking queue. On the other end, the Read thread should wait on a message arriving in the blocking queue. Once it removes a message, it should process it.



* If it is a Keep Alive, it updates the list of active neighbors it has.
* If it is an advertisement:
    * It updates the list of advertisements it has received for the node in the advertisement, if the sequence number is higher than the last sequence number it received for that node.
    * It rebroadcasts the advertisements to all neighbors, except
        * It does not rebroadcast back on the link it received it from
        * It does not rebroadcast if the TTL is 0


### Utility Classes

* <strong><code>make test-single </code></strong>- Tests two nodes connected by a single link learning about each other with keep-alives
* <strong><code>make test-three </code></strong>- Three nodes connected in a Y learning about each other with keep-alives
* <strong><code>make test-single-ad-state </code></strong>- Two nodes learning about each other with advertisements
* <strong><code>make test-double-hop-ad-state </code></strong>- Double hops, with ad state propagation
* <strong><code>make test-multi-hop-ad-state </code></strong>- Multi hops with ad state propagation
* <strong><code>make test-going-offline </code></strong>- Test a node going offline
* <strong><code>make test-coming-back-line</code></strong> - Check a node going offline then coming back online
* <strong><code>make test-poisoning</code></strong> - Check that the protocol is resistant against poisoning with bad advertisements
* <strong><code>make test-style</code></strong> - Check for good Java style
