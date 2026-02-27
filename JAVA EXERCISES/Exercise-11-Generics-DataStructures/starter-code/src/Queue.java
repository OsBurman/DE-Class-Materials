import java.util.NoSuchElementException;

/**
 * Generic Queue — STARTER CODE
 * FIFO (First-In First-Out) backed by linked nodes.
 *
 * TODO 1: Add fields: private Node<T> head (front), private Node<T> tail
 * (back), private int size.
 * TODO 2: Implement enqueue(T data)
 * Create node. If tail != null, tail.next = newNode. Update tail.
 * If head == null (was empty), set head = newNode too. Increment size.
 * TODO 3: Implement dequeue()
 * Throw NoSuchElementException if empty.
 * Save head.data. Advance head. If head == null, set tail = null. Decrement
 * size.
 * TODO 4: Implement front() — peek at head without removing. Throw if empty.
 * TODO 5: Implement isEmpty() and size().
 * TODO 6: Implement toString() — [front, ..., back]
 */
public class Queue<T> {

    private static class Node<T> {
        T data;
        Node<T> next;

        Node(T data) {
            this.data = data;
        }
    }

    // TODO 1: fields

    // TODO 2
    public void enqueue(T data) {
        /* TODO */ }

    // TODO 3
    public T dequeue() {
        // TODO
        throw new NoSuchElementException("Queue is empty");
    }

    // TODO 4
    public T front() {
        // TODO
        throw new NoSuchElementException("Queue is empty");
    }

    // TODO 5
    public boolean isEmpty() {
        return true;
        /* TODO */ }

    public int size() {
        return 0;
        /* TODO */ }

    // TODO 6
    @Override
    public String toString() {
        return "[]";
        /* TODO */ }
}
