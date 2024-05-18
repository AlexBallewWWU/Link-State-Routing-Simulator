

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
