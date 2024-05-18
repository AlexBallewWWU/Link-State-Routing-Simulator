import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class LinkStateTest {

    @Before
    public void setup() {}

    // This is a simple test of two nodes connected by a single link.
    // The keep alives should allow both nodes to discover each other
    // as neighbors
    @Test
    public void testSingle() {
        System.out.println("Checking single link and two nodes");

        // Set up network and turn on nodes
        Node n1 = new Node(1);
        Node n2 = new Node(2);
        Link e1 = new Link(n1, n2);
        n1.turnOn();
        n2.turnOn();

        // wait for stabiliziation
        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            // do nothing
        }

        // Check that network states update correctly
        List<Integer> n1neighbors = n1.getNeighbors();
        assertEquals(1, n1neighbors.size());
        assertTrue(n1neighbors.contains(2));

        List<Integer> n2neighbors = n2.getNeighbors();
        assertEquals(1, n2neighbors.size());
        assertTrue(n2neighbors.contains(1));
    }

    // This is a simple test with 3 nodes in a Y configuration
    // n1 is connected to both n2 and n3.
    // n1 should have discovered that both n2 and n3 are neighbors
    @Test
    public void testThree() {
        System.out.println("Checking three nodes with Y link");

        // Set up network and turn on nodes
        Node n1 = new Node(1);
        Node n2 = new Node(2);
        Node n3 = new Node(3);
        Link e1 = new Link(n1, n2);
        Link e2 = new Link(n1, n3);
        n1.turnOn();
        n2.turnOn();
        n3.turnOn();

        // wait for stabiliziation
        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            // do nothing
        }

        // Check that network states update correctly
        List<Integer> n1neighbors = n1.getNeighbors();
        assertEquals(2, n1neighbors.size());
        assertTrue(n1neighbors.contains(2));
        assertTrue(n1neighbors.contains(3));

        List<Integer> n2neighbors = n2.getNeighbors();
        assertEquals(1, n2neighbors.size());
        assertTrue(n2neighbors.contains(1));

        List<Integer> n3neighbors = n3.getNeighbors();
        assertEquals(1, n3neighbors.size());
        assertTrue(n3neighbors.contains(1));
    }

    // This checks keep alive propagation in a simple two node configuration.
    // n1 should have discovered that n2 has n1 as a neighbor
    // n2 should have discovered that n1 has n2 as a neighbor
    @Test
    public void testSingleAdState() {
        System.out.println("Checking advertisement states with two nodes");

        // Set up network and turn on nodes
        Node n1 = new Node(1);
        Node n2 = new Node(2);
        Link e1 = new Link(n1, n2);
        n1.turnOn();
        n2.turnOn();

        // wait for stabiliziation
        try {
            Thread.sleep(2500);
        } catch (Exception e) {
            // do nothing
        }

        // Check that network states update correctly
        List<Integer> n1neighbors = n1.getNeighbors(2);
        assertEquals(1, n1neighbors.size());
        assertTrue(n1neighbors.contains(1));

        List<Integer> n2neighbors = n2.getNeighbors(1);
        assertEquals(1, n2neighbors.size());
        assertTrue(n2neighbors.contains(2));
    }

    // This test checks advertisement states with more than one hop.
    // The configuration is a linear one with three nodes n1 - n2 - n3
    // n1 should have discovered that n3 has n2 as a neighbor
    // n3 should have discovered that n1 has n2 as a neighbor
    @Test
    public void testDoubleHopAdState() {
        System.out.println("Checking advertisement states with double hops");

        // Set up network and turn on nodes
        Node n1 = new Node(1);
        Node n2 = new Node(2);
        Node n3 = new Node(3);
        Link e1 = new Link(n1, n2);
        Link e2 = new Link(n2, n3);
        n1.turnOn();
        n2.turnOn();
        n3.turnOn();

        // wait for stabiliziation
        try {
            Thread.sleep(2500);
        } catch (Exception e) {
            // do nothing
        }

        // Check that network states update correctly

        // Check that n3 knows about n1
        List<Integer> n1neighbors = n3.getNeighbors(1);
        //System.out.println("||NEIGHBORS||: " + n1neighbors.size());
        assertEquals(1, n1neighbors.size());
        assertTrue(n1neighbors.contains(2));

        // // Check that n1 knows about n3
        List<Integer> n3neighbors = n1.getNeighbors(3);
        assertEquals(1, n3neighbors.size());
        assertTrue(n3neighbors.contains(2));

        // // Check that n1 knows about n2
        List<Integer> n2neighbors = n1.getNeighbors(2);
        assertEquals(2, n2neighbors.size());
        assertTrue(n2neighbors.contains(1));
        assertTrue(n2neighbors.contains(3));

        // // Check that n3 knows about n2
        n2neighbors = n3.getNeighbors(2);
        assertEquals(2, n2neighbors.size());
        assertTrue(n2neighbors.contains(1));
        assertTrue(n2neighbors.contains(3));
    }

    // This checks the link state of a more complicated network with
    // multiple hops. The network looks like this
    //
    //     n1 - n2 - n4 - n5
    //     |   /     |  /
    //     n3       n6
    @Test
    public void testMultiHopAdState() {
        System.out.println("Checking advertisement states with multi hops");

        // Set up network and turn on nodes
        Node n1 = new Node(1);
        Node n2 = new Node(2);
        Node n3 = new Node(3);
        Node n4 = new Node(4);
        Node n5 = new Node(5);
        Node n6 = new Node(6);
        Link e1 = new Link(n1, n2);
        Link e2 = new Link(n1, n3);
        Link e3 = new Link(n2, n3);
        Link e4 = new Link(n2, n4);
        Link e5 = new Link(n4, n5);
        Link e6 = new Link(n4, n6);
        Link e7 = new Link(n5, n6);
        n1.turnOn();
        n2.turnOn();
        n3.turnOn();
        n4.turnOn();
        n5.turnOn();
        n6.turnOn();

        // wait for stabiliziation
        try {
            Thread.sleep(4500);
        } catch (Exception e) {
            // do nothing
        }

        // Check that network states update correctly

        // Check that n3 knows about n5
        List<Integer> n5neighbors = n1.getNeighbors(5);
        assertEquals(2, n5neighbors.size());
        assertTrue(n5neighbors.contains(4));
        assertTrue(n5neighbors.contains(6));

        // Check that n5 knows about n3
        List<Integer> n3neighbors = n5.getNeighbors(3);
        assertEquals(2, n3neighbors.size());
        assertTrue(n3neighbors.contains(1));
        assertTrue(n3neighbors.contains(2));
    }

    // This checks the link state of a more complicated network with
    // multiple hops. The network looks like this
    //
    //     n1 - n2 - n4 - n5
    //     |   /     |  /
    //     n3       n6
    //
    // We make n4 go offline
    @Test
    public void testGoingOffline() {
        System.out.println("Checking advertisement states with multi hops");

        // Set up network and turn on nodes
        Node n1 = new Node(1);
        Node n2 = new Node(2);
        Node n3 = new Node(3);
        Node n4 = new Node(4);
        Node n5 = new Node(5);
        Node n6 = new Node(6);
        Link e1 = new Link(n1, n2);
        Link e2 = new Link(n1, n3);
        Link e3 = new Link(n2, n3);
        Link e4 = new Link(n2, n4);
        Link e5 = new Link(n4, n5);
        Link e6 = new Link(n4, n6);
        Link e7 = new Link(n5, n6);
        n1.turnOn();
        n2.turnOn();
        n3.turnOn();
        n4.turnOn();
        n5.turnOn();
        n6.turnOn();

        // wait for stabiliziation
        try {
            Thread.sleep(4500);
        } catch (Exception e) {
            // do nothing
        }

        System.out.println("Making n4 go offline");
        n4.turnOff();

        // wait for stabilization
        try {
            Thread.sleep(3000);
        } catch (Exception e) {
            // do nothing
        }

        // Check that network states update correctly

        // Check that n3 knows about n5
        List<Integer> n5neighbors = n1.getNeighbors(5);
        assertEquals(null, n5neighbors);

        // Check that n2 is only connected to n1 and n3
        List<Integer> n2neighbors = n2.getNeighbors();
        assertEquals(2, n2neighbors.size());
        assertTrue(n2neighbors.contains(1));
        assertTrue(n2neighbors.contains(3));

        // Check that n5 is only connected to n6
        List<Integer> n6neighbors = n6.getNeighbors();
        assertEquals(1, n6neighbors.size());
        assertTrue(n6neighbors.contains(5));
    }

    // This checks the link state of a more complicated network with
    // multiple hops. The network looks like this
    //
    //     n1 - n2 - n4 - n5
    //     |   /     |  /
    //     n3       n6
    //
    // We make n2 go offline, then have it come back online
    @Test
    public void testComingBackOnline() {
        System.out.println("Checking advertisement states with multi hops");

        // Set up network and turn on nodes
        Node n1 = new Node(1);
        Node n2 = new Node(2);
        Node n3 = new Node(3);
        Node n4 = new Node(4);
        Node n5 = new Node(5);
        Node n6 = new Node(6);
        Link e1 = new Link(n1, n2);
        Link e2 = new Link(n1, n3);
        Link e3 = new Link(n2, n3);
        Link e4 = new Link(n2, n4);
        Link e5 = new Link(n4, n5);
        Link e6 = new Link(n4, n6);
        Link e7 = new Link(n5, n6);
        n1.turnOn();
        n2.turnOn();
        n3.turnOn();
        n4.turnOn();
        n5.turnOn();
        n6.turnOn();

        // wait for stabiliziation
        try {
            Thread.sleep(4500);
        } catch (Exception e) {
            // do nothing
        }

        System.out.println("Making n4 go offline");
        n4.turnOff();

        // wait for stabiliziation
        try {
            Thread.sleep(3000);
        } catch (Exception e) {
            // do nothing
        }

        // Check that network states update correctly

        // Check that n3 knows about n5
        List<Integer> n5neighbors = n1.getNeighbors(5);
        assertEquals(null, n5neighbors);

        // Check that n2 is only connected to n1 and n3
        List<Integer> n2neighbors = n2.getNeighbors();
        assertEquals(2, n2neighbors.size());
        assertTrue(n2neighbors.contains(1));
        assertTrue(n2neighbors.contains(3));

        // Check that n5 is only connected to n6
        List<Integer> n6neighbors = n6.getNeighbors();
        assertEquals(1, n6neighbors.size());
        assertTrue(n6neighbors.contains(5));

        System.out.println("Making n4 go back online");
        n4.turnOn();

        // wait for stabiliziation
        try {
            Thread.sleep(3000);
        } catch (Exception e) {
            // do nothing
        }
        System.out.println("THINGY" + n5.getNeighbors());
        // Check that n3 knows about n5
        n5neighbors = n1.getNeighbors(5);
        assertEquals(2, n5neighbors.size());
        assertTrue(n5neighbors.contains(4));
        assertTrue(n5neighbors.contains(6));
    }

    // This checks that the network is resilient against poisoning.
    // multiple hops. The network looks like this
    //
    //     n1 - n2 - n4 - n5
    //     |   /     |  /
    //     n3       n6
    //
    // We make send a bad advertisement deliberately.
    @Test
    public void testPoisoning() {
        System.out.println("Checking advertisement states with multi hops");

        // Set up network and turn on nodes
        Node n1 = new Node(1);
        Node n2 = new Node(2);
        Node n3 = new Node(3);
        Node n4 = new Node(4);
        Node n5 = new Node(5);
        Node n6 = new Node(6);
        Link e1 = new Link(n1, n2);
        Link e2 = new Link(n1, n3);
        Link e3 = new Link(n2, n3);
        Link e4 = new Link(n2, n4);
        Link e5 = new Link(n4, n5);
        Link e6 = new Link(n4, n6);
        Link e7 = new Link(n5, n6);
        n1.turnOn();
        n2.turnOn();
        n3.turnOn();
        n4.turnOn();
        n5.turnOn();
        n6.turnOn();

        // wait for stabiliziation
        try {
            Thread.sleep(4500);
        } catch (Exception e) {
            // do nothing
        }

        // Make a new advertisement that we will make it look like n4
        // sends saying that n1 is only connected to n2, and not connected to
        // n3 with a low sequence number of 1
        System.out.println("Sending poison...");
        Advertisement ad = new Advertisement(4, 1, List.of(2), 1);
        e4.sendMessage(ad);
        e5.sendMessage(ad);

        // wait for stabiliziation
        try {
            Thread.sleep(1500);
        } catch (Exception e) {
            // do nothing
        }

        // Check that network states update correctly

        // Check that n5 knows about n1
        List<Integer> n1neighbors = n5.getNeighbors(1);
        assertEquals(2, n1neighbors.size());
        assertTrue(n1neighbors.contains(2));
        assertTrue(n1neighbors.contains(3));
    }
}
