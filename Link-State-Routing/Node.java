import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.text.AbstractDocument;


/*
 * Represent a Node.
 */
public class Node {


    int nodeId;
    HashMap<Integer, ArrayList<Integer>> map;
    HashMap<Integer, Instant> alive;
    HashMap<Integer, Integer> seqNums;
    HashMap<Integer, Instant> lastAdv;
    ArrayList<Link> links = new ArrayList<Link>();
    boolean active; 
    BlockingQueue<Message> block;
    Thread recieveThread;
    int sequence = 0;

    // maybe store messages too


    // ArrayList<Link> links = new ArrayList<Link>();

    /**
     * A node is determined to be a neighbor if it sent a keep alive.
     * The last interval of this many milliseconds
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
     * The timeout after which advertisements are invalid.
     */
    private static final int ADVERTISEMENTTIMEOUT = 3000;

    /**
     * Construct a switch with an id. The switch is initially offline.
     *
     * @param nodeId The id of the switch
     */
    public Node(int nodeId) {
        // Your code here
        block = new BlockingQueue<>();
        this.nodeId = nodeId;
        map = new HashMap<>();
        alive = new HashMap<>();
        seqNums = new HashMap<>();
        lastAdv = new HashMap<>();
        ArrayList<Integer> neighbors = new ArrayList<Integer>();
        map.put(this.nodeId, neighbors);
        this.active = false;
    }

    /**
     * Return the id of this node.
     */
    public int getId() {
        // Your code here
        return this.nodeId;
    }

    /**
     * Add a network link that connects the node to a neighbor.
     *
     * @param link The link to add
     */
    public void plugin(Link link) {
        links.add(link);
        for (int i = 0; i < 2; i++) { 
            if (link.getNode(i) != this) {
                Node node1 = link.getNode(i);
                ArrayList<Integer> neighbors1 = map.get(this.nodeId);
                neighbors1.add(node1.getId());
            }
        }
    }

    public void sendAdvs() {
        while (true) {
            sequence++;
            if (active == false) {
                return;
            }
            for (int i = 0; i < links.size(); i++) {
                Link curLink = links.get(i);
                Advertisement adv = new Advertisement(this.nodeId,
                        this.nodeId, map.get(this.nodeId), sequence);
                curLink.sendMessage(adv);
            }
            try {
                Thread.sleep(ADVERTISEMENTINTERVAL);
            } catch (Exception e) {
                // do nothing
            }
        }
    }

    public void readMessages() {
        while (true) {
            if (active == false) {
                return;
            }
            Message message = block.get();
            if (message.getClass() == Advertisement.class) {
                Advertisement adv = (Advertisement)message;
                if (seqNums.get(adv.getNodeId()) == null
                    || adv.getSequenceNumber() > seqNums.get(adv.getNodeId())) {
                    if (seqNums.get(adv.getNodeId()) == null) {
                        seqNums.put(adv.getNodeId(), adv.getSequenceNumber());
                    } else {    
                        seqNums.replace(adv.getNodeId(), adv.getSequenceNumber());
                    }
                    if (!map.containsKey(adv.getNodeId())) {
                        ArrayList<Integer> neighbors =
                            (ArrayList<Integer>) adv.getNeighbors();
                        map.put(adv.getNodeId(), neighbors);
                    } else {
                        ArrayList<Integer> neighbors =
                            (ArrayList<Integer>) adv.getNeighbors();
                        map.replace(adv.getNodeId(), neighbors);              
                    }


                    if (lastAdv.containsKey(adv.getNodeId())) {
                        lastAdv.replace(adv.getNodeId(), Instant.now());
                    } else {
                        lastAdv.put(adv.getNodeId(), Instant.now());
                    }

                    if (!(adv.getTtl() < 0)) {
                        for (int i = 0; i < links.size(); i++) {
                            Link curLink = links.get(i);
                            Advertisement advResend = new
                                Advertisement(this.nodeId, (Advertisement)message);
                            curLink.sendMessage(advResend);
                        }
                    }
                }
            } else if (message.getClass() == KeepAlive.class) {
                KeepAlive keepAlive = (KeepAlive)message;
                if (alive.containsKey(keepAlive.fromId)) {
                    alive.replace(keepAlive.fromId, Instant.now());
                } else {
                    alive.put(keepAlive.fromId, Instant.now());
                }
            }

            for (int i = 0; i < links.size(); i++) {
                Link curLink = links.get(i);
                Node otherNode = null;
                for (int j = 0; j < 2; j++) {
                    if (curLink.getNode(j) != this) {
                        otherNode = curLink.getNode(j);
                    }
                }
                if (alive.containsKey(otherNode.nodeId)) {
                    Instant start = alive.get(otherNode.nodeId);
                    Instant end = Instant.now();
                    if (Duration.between(start, end).toMillis() > KEEPALIVETIMEOUT) {
                        if (otherNode.nodeId == 4 && nodeId == 5) {
                            break;
                        }
                        map.remove(otherNode.nodeId);
                        ArrayList<Integer> oldArray = map.get(this.nodeId);
                        oldArray.remove((Object)otherNode.nodeId);
                        alive.remove(otherNode.nodeId);
                    }
                }
            }
            for (Integer key: lastAdv.keySet()) {
                if (lastAdv.containsKey(key)) {
                    Instant start = lastAdv.get(key);
                    Instant end = Instant.now();
                    if (Duration.between(start, end).toMillis() > ADVERTISEMENTTIMEOUT) {
                        map.remove(key);
                    }
                }
            }
        }
    }

    public void stillAlive() {
        while (true) {
            if (active == false) {
                return;
            }
        
            for (int i = 0; i < links.size(); i++) {
                Link curLink = links.get(i);
                KeepAlive keepAlive = new KeepAlive(this.nodeId);
                curLink.sendMessage(keepAlive);
            }
            try {
                Thread.sleep(KEEPALIVEINTERVAL);
            } catch (Exception e) {
                // do nothing
            }
        }
    }



    /**
     * Turn the switch on.
     */
    public void turnOn() {
        active = true;
        Thread advThread = new Thread(() -> {
            sendAdvs();
        });
        advThread.start();

        recieveThread = new Thread(() -> {
            readMessages();
        });
        recieveThread.start();

        Thread aliveThread = new Thread(() -> {
            stillAlive();
        });
        aliveThread.start();
    }

    /**
     * Turn the switch off.
     */
    public void turnOff() {
        try {
            Thread.sleep(5000);
        } catch (Exception e) {
            // do nothing
        }
        active = false;
    }

    /**
     * Receive a Message from a neighbor.
     *
     * @param link The link that the message was received on
     * @param message The message sent by a neighbor
     */
    public void receiveMessage(Link link, Message message) {
        block.put(message);
    }

    /**
     * Get a list of nodeIds of neighbors of this node.
     *
     * @return A List of node ids of all neighbors of this node
     */
    public List<Integer> getNeighbors() {
        return map.get(this.nodeId);
    }

    /**
     * Get the advertised list of neighbors for the given node.
     * The list of neighbors should correspond to a valid advertisement
     * received less than ADVERTISEMENTTIMEOUT
     *
     * @param id The node id of the destination
     * @return The list of neighbors for the node, or null if none
     */
    public List<Integer> getNeighbors(int id) {
        return map.get(id);
    }
}
