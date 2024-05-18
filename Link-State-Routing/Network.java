import java.util.ArrayList;
import java.util.List;

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
