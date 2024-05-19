[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-24ddc0f5d75046c5622901739e7c5dd533143b0c8e959d652212380cedb1ea36.svg)](https://classroom.github.com/a/r_U0wNiU)
# Link State Routing
<!--- Your assignment is available [here](https://docs.google.com/document/d/1XjKAUvFsbVtMEoKBRNQYvmcXHlRIrKpH1Ga9mfvMEso/edit#). --->



![alt_text(images/lsr.png)



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

The assignment is to produce an implementation of a Link State Routing Protocol in a network simulation that has the first two parts: 



1. Determining Your Neighbors, and
2. Telling Other Switches About Your Neighbors.

We will leave Dijkstra's Shortest Path for another time.


### Objects

While networks in real life have computers, switches and routers, we are going to combine all of them in this assignment. The basic element in the simulation is the class `Node`. A node represents a network node.

This assignment is implemented in Java.


![drawing](images/node.png)

Nodes are connected by Links, which are a bidirectional connection between nodes. Messages are sent between nodes on the link connecting them. There are only two types of Messages, either Keep Alive messages or Advertisements. 


#### Message, KeepAlive and Advertisement

A Message type is provided for you in **Message.java**. A message has a _from id_ indicating the id (an integer) of the node that sent the message.


```
/**
 * A message that is sent and received between nodes.
 */
public class Message {
    /**
     * There are 3 types of Messages.
     */
    enum MessageType {
        KEEPALIVE,
        ADVERTISEMENT,
        UNKNOWN,
    }

    /**
     * The node id that the message gets sent from.
     */
    protected int fromId;

    /**
     * Create a message from the given node.
     *
     * @param fromId The id that is sending the message
     */
    public Message(int fromId) {
        this.fromId = fromId;
    }

    /**
     * Retrieve the from id for the message.
     *
     * @return The from id for the message
     */
    public int getFromId() {
        return fromId;
    }

    /**
     * Get a nice human readable string representing the message.
     */
    public String toString() {
        return "Message(from=" + getFromId() + ")";
    }

    /**
     * Return the class of the message as an enum.
     *
     * @return A MessageType enum of KEEPALIVE or ADVERTISEMENT
     */
    public MessageType getType() {
        if (this instanceof KeepAlive) {
            return MessageType.KEEPALIVE;
        } else if (this instanceof Advertisement) {
            return MessageType.ADVERTISEMENT;
        } else {
            // We should never get here
            assert false;
            return MessageType.UNKNOWN;
        }
    }
}
```


You can call the method getType() to find out what type the message is. There are only two types, KeepAlive in **KeepAlive.java** and Advertisement in **Advertisement.java**. These are subclasses that extend the Message class and are provided for you.



* The KeepAlive message does not include any extra fields.
* The Advertisement message includes
    * The node id for the advertisement (this is different from the sending from id, since advertisements are rebroadcast by nodes). For example, node 4 may rebroadcast the advertisement for node 1, in which case the from id is 4 but the node id in the Advertisement is 1.
    * The list of neighbors directly connected to the node given by the node id
    * The sequence number, which increases every time a new advertisement is sent 
    * A TTL field, which is set to MAXTTL for each new advertisement


#### Link

The Link class represents a bidirectional link between two nodes. This class is provided for you in **Link.java**.


```
/**
 * Represent a network link connecting two nodes.
 * A link allows a node to communicate with the peer.
 */
public class Link {
    /**
     * Create a bidirectional link connecting nodes n1 and n2.
     * Plug the link into both nodes.
     *
     * @param n1 The first node
     * @param n2 The second node
     */
    public Link(Node n1, Node n2) {
	...
    }

    /**
     * Send a message on this link. The node at the other end receives
     * the message.
     *
     * @param message The message to send
     */
    public void sendMessage(Message message) {
	...
    }
}
```


A node can call the sendMessage method with a message. The link will deliver the message to the node on the other end.


#### Node

The Node class represents a node in the network. Nodes in this simulation have two states:



1. On, in which case it is actively and sending/receiving messages
2. Off, in which case it does not send or receive any messages (think of it as powered off)

When a Node is in the on state, it does 3 things:



1. Periodically send _KeepAlive_ messages to all links connected to it
2. Periodically send _Advertisement_ messages to all links connected to it
3. Receives messages that are sent to it and processes the messages
    1. _KeepAlive_ messages result in the node updating it's list of active neighbors
    2. _Advertisement_ messages update the node's cache of advertisements from other nodes, and are rebroadcast as appropriate

You are given several constants.



* <strong><code>KEEPALIVETIMEOUT </code></strong>is the time in milliseconds after which a node is considered inactive
* <strong><code>KEEPALIVEINTERVAL </code></strong>is the time in milliseconds at which a node sends keep alives to its neighbors
* <strong><code>ADVERTISEMENTINTERVAL </code></strong>is the time in milliseconds at which a node sends advertisement messages to its neighbors
* <strong><code>ADVERTISEMENTTIMEOUT </code></strong>is the time in milliseconds after which previously received advertisements expire and are no longer valid


```
/*
 * Represent a Node.
 */
public class Node {
    /**
     * A node is determined to be a neighbor if it is kept alive.
     * The last interval of this many milliseconds.
     */
    private static final int KEEPALIVETIMEOUT = 1500;

    /**
     * The keep alive interval in milliseconds.
     */
    private static final int KEEPALIVEINTERVAL = 1000;

    /**
     * The interval for advertisement messages.
     */
    private static final int ADVERTISEMENTINTERVAL = 2000;

    /**
     * The timeout after which advertisements are invalid
     */
    private static final int ADVERTISEMENTTIMEOUT = 3000;

    /**
     * Construct a switch with an id. The switch is initially offline.
     *
     * @param nodeId The id of the switch
     */
    public Node(int nodeId) {
        ...
    }

    /**
     * Return the id of this node.
     */
    public int getId() {
        return ...;
    }

    /**
     * Add a network link that connects the node to a neighbor.
     *
     * @param link The link to add
     */
    public void plugin(Link link) {
        ...
    }

    /**
     * Turn the switch on.
     */
    public void turnOn() {
        ...
    }

    /**
     * Turn the switch off.
     */
    public void turnOff() {
        ...
    }

    /**
     * Receive a Message from a neighbor.
     *
     * @param link The link that the message was received on
     * @param message The message sent by a neighbor
     */
    public void receiveMessage(Link link, Message message) {
        ...
    }

    /**
     * Get a list of nodeIds of neighbors of this node. 
     *
     * @return A List of node ids of all neighbors of this node
     */
    public List<Integer> getNeighbors() {
        return ...;
    }

    /**
     * Get the advertised list of neighbors for the given node.
     * The list of neighbors should correspond to a valid advertisement
     * received less than ADVERTISEMENTTIMEOUT.
     *
     * @param id The node id of the destination
     * @return The list of neighbors for the node, or null if none
     */
    public List<Integer> getNeighbors(int id) {
        return ...;
    }
}
```


Your task is to write the Node class. The methods you have to implement are given in the skeleton above.



* <strong><code>Node(int nodeId)</code></strong> is the constructor that creates a Node object. You will want to initialize whatever instance variables you need.
* <strong><code>int getNodeId()</code></strong> returns the node id of the node. 
* <strong><code>void plugin(Link link)</code></strong> is called when a link object is plugged into your node.
* <strong><code>void turnOn()</code></strong> makes the node start be active and start sending keep alives and advertisements
* <strong><code>void turnOff()</code></strong> makes the node go quiescent.
* <strong><code>void receiveMessage(Link link, Message message)</code></strong> is called by the Link when a message is sent to the Node.
* There are two <strong>getNeighbors</strong> methods, one of which gets the directly connected neighbors of the Node, and the other returns the neighbors for a remote node given by the node id. A node can reply with the list of neighbors received from advertisements from the remote node.

Note that the node will have many threads calling it at once, as messages arrive on links from other nodes, as well as being accessed from its own threads. Synchronization is critical in this case to protect shared data structures, but be careful or you might get into deadlock situations if you get circular dependencies. You may either mark a method with the <code>synchronized</code> keyword, or use the <code>synchronized</code> blocks within the body of a function. 


### Architecture of Node

The figure below shows the architecture of Node that you probably want.



![drawing](images/architecture.png)

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

To help in your assignment, I have included a class <strong><code>BlockingQueue&lt;T></code></strong> in <strong>BlockingQueue.java</strong>. This class allows you to create a blocking queue such that calling <code>get()</code> on the queue will block until something is inserted into it. Calling <code>put()</code> will wake up any waiting thread.


### Main Program

There is a sample Main program in **Network.java**. You can run it with

	<strong><code>make run</code></strong>

The program creates two nodes, links them up, turns on the nodes and then checks what knowledge each node has of its neighbors.


```
/**
 * Represent a network of switches.
 */
public class Network {
    /**
     * Set up a network and run the code to simulate link state.
     */
    public static void main(String[] args) {
        // Set up network
        Node n1 = new Node(1);
        Node n2 = new Node(2);
        Link e1 = new Link(n1, n2);

        // power on the nodes
        n1.turnOn();
        n2.turnOn();

        // Wait for things to stabilize
        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            // do nothing
        }

        // Print n1's neighbors
        System.out.println("n1's neighbors are " + n1.getNeighbors());
        // Print what n1 knows about n2
        System.out.println("n2 knows that n1's neighbors are "
            + n2.getNeighbors(1));

        // Turn off n1
        n1.turnOff();

        try {
            Thread.sleep(3500);
        } catch (Exception e) {
            // do nothing
        }

        // Print n2's neighbors
        System.out.println("n2's neighbors are " + n2.getNeighbors());
    }
}
```



#### Testing

The test files are in <strong><code>Test/LinkStateTest.java</code></strong>. Run all the tests by typing

	make test

Here are the standalone tests you can run:



* <strong><code>make test-single </code></strong>- Tests two nodes connected by a single link learning about each other with keep-alives
* <strong><code>make test-three </code></strong>- Three nodes connected in a Y learning about each other with keep-alives
* <strong><code>make test-single-ad-state </code></strong>- Two nodes learning about each other with advertisements
* <strong><code>make test-double-hop-ad-state </code></strong>- Double hops, with ad state propagation
* <strong><code>make test-multi-hop-ad-state </code></strong>- Multi hops with ad state propagation
* <strong><code>make test-going-offline </code></strong>- Test a node going offline
* <strong><code>make test-coming-back-line</code></strong> - Check a node going offline then coming back online
* <strong><code>make test-poisoning</code></strong> - Check that the protocol is resistant against poisoning with bad advertisements
* <strong><code>make test-style</code></strong> - Check for good Java style


## Debugging

Debug targets are also available through [JDB, the Java Debugger.](https://www.tutorialspoint.com/jdb/jdb_quick_guide.htm)



* <strong><code>make debug</code></strong> - Debug the main program
* <strong><code>make debug-test</code></strong> - Debug the test code


## Hints



* Make sure that you inspect the TTL before forwarding advertisements.
* You can track whether a node is alive just from _KeepAlive_ messages
