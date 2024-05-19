import java.util.LinkedList;

/**
 * This is a simple blocking queue for receiving messages for one reader
 * and one writer.
 */
public class BlockingQueue<T> {
    /**
     * The queue itself is represented as a simple Linked List of type T.
     */
    private LinkedList<T> queue;

    /**
     * Create a BlockingQueue of type T items.
     */
    public BlockingQueue() {
        queue = new LinkedList<T>();
    }

    /**
     * This method will block until there is something in the list.
     *
     * @return An item from the front of the list, or null if interrupted
     */
    public synchronized T get() {
        if (queue.size() == 0) {
            try {
                wait();              // go to sleep and wait
            } catch (Exception e) {
                return null;
            }
        }
        return queue.removeFirst();
    }

    /**
     * This methods puts the item on the back of the list. Wake up any waiters.
     *
     * @param item The item to put on the list.
     */
    public synchronized void put(T item) {
        queue.addLast(item);
        if (queue.size() == 1) {
            notifyAll();             // wake up anyone waiting for something
        }
    }

    /**
     * Clear the queue.
     */
    public synchronized void clear() {
        queue.clear();
    }
}
