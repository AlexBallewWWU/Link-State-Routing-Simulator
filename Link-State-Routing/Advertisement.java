import java.util.List;

/**
 * An Advertisement is a type of Message.
 * Represent the Link State Advertisement that a switch sends to its'
 * neighbors. The advertisement includes the node id, and a list of
 * other nodes to which it is connected, and a sequence number.
 */
public class Advertisement extends Message {
    private static final int MAXTTL = 4;

    private int nodeId;
    private List<Integer> neighbors;
    private int sequenceNumber;
    private int ttl;

    /**
     * Create an advertisement mesage. This may be rebroadcast by other
     * nodes, hence the sending node and the node that the advertisement is
     * for may be different. The TTL is set to MAX TTL
     *
     * @param fromId The node that is sending this advertisement
     * @param nodeId The node that this advertisement is actually from
     * @param neighbors The list of node ids for the advertisement
     * @param sequenceNumber The sequence number for this advertisement
     */
    public Advertisement(int fromId, int nodeId, List<Integer> neighbors,
        int sequenceNumber) {
        super(fromId);
        this.nodeId = nodeId;
        this.neighbors = neighbors;
        this.sequenceNumber = sequenceNumber;
        this.ttl = MAXTTL;
    }

    /**
     * Construct an advertisement from a previous Advertisement message.
     * The TTL is reduced by 1.
     *
     * @param fromId The node that is sending this advertisement
     * @param ad The previous advertisement that will be rebroadcast
     */
    public Advertisement(int fromId, Advertisement ad) {
        this(fromId, ad.getNodeId(), ad.getNeighbors(), ad.getSequenceNumber());
        ttl = ad.getTtl() - 1;
    }

    /**
     * Get the node Id from an advertisement message.
     *
     * @return The node id
     */
    public int getNodeId() {
        return nodeId;
    }

    /**
     * Get the list of neighbors in this advertisement message.
     *
     * @return List of integers that represent the ids of the neighbors
     */
    public List<Integer> getNeighbors() {
        return neighbors;
    }

    /**
     * Get the sequence number for this advertisement message.
     *
     * @return An integer sequence number
     */
    public int getSequenceNumber() {
        return sequenceNumber;
    }

    /**
     * Get the TTL count for the ad.
     */
    public int getTtl() {
        return ttl;
    }

    /**
     * Get a nice human readable string representing the advertisement message.
     */
    public String toString() {
        return super.toString() + "::Advertisement(nodeId=" + getNodeId()
            + ", sequence=" + getSequenceNumber() + ", ttl=" + getTtl()
            + "):Neighbors=" + getNeighbors().toString();
    }
}
