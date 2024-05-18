/**
 * A KeepAlive is a reachability message sent to a neighbor to indicate
 * that the sending node is still on.
 */
public class KeepAlive extends Message {
    /**
     * Create a keep alive message.
     *
     * @param fromId The id of the node sending the keep alive
     */
    public KeepAlive(int fromId) {
        super(fromId);
    }

    /**
     * Get a nice human readable string representing the keep alive message.
     */
    public String toString() {
        return super.toString() + "::KeepAlive()";
    }
}
