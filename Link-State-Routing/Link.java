/**
 * Represent a network link connecting two nodes.
 * A link allows a node to communicate with the peer.
 */
public class Link {
    private Node[] nodes;

    /**
     * Create a bidirectional link connecting nodes n1 and n2.
     * Plug the link into both nodes.
     *
     * @param n1 The first node
     * @param n2 The second node
     */
    public Link(Node n1, Node n2) {
        nodes = new Node[2];
        nodes[0] = n1;
        nodes[1] = n2;
        n1.plugin(this);
        n2.plugin(this);
    }

    /**
     * Get the nodes in this link.
     */
    public Node[] getNodes() {
        return nodes;
    }

    /**
     * Get one end of the link.
     *
     * @param i The index to get
     * @return The node at that end of the link
     */
    public Node getNode(int i) {
        return nodes[i];
    }

    /**
     * Replace old node with the new node on this link.
     *
     * @param oldNode The old node to replace
     * @param newNode The node to replace with
     */
    public void replace(Node oldNode, Node newNode) {
        if (oldNode.getId() == nodes[0].getId()) {
            nodes[0] = newNode;
        } else if (oldNode.getId() == nodes[1].getId()) {
            nodes[1] = newNode;
        } else {
            // Should not replace a node that is not on this link
            assert false;
        }
    }


    /**
     * Send a message on this link. The node at the other ends receives
     * the message.
     *
     * @param message The message to send
     */
    public void sendMessage(Message message) {
        if (nodes[0].getId() == message.getFromId()) {
            nodes[1].receiveMessage(this, message);
        } else if (nodes[1].getId() == message.getFromId()) {
            nodes[0].receiveMessage(this, message);
        } else {
            // We should never get here where a sending node is not on the link
            assert false;
        }
    }
}
