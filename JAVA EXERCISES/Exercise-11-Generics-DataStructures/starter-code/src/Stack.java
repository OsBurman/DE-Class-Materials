import java.util.EmptyStackException;

/**
 * Exercise 11 — Generics & Data Structures
 * Generic Stack — STARTER CODE
 *
 * A LIFO (Last-In First-Out) stack backed by linked nodes.
 *
 * TODO 1: The inner Node<T> class is provided. Study it.
 * TODO 2: Add fields: private Node<T> top and private int size.
 * TODO 3: Implement push(T data)
 * Create a new Node, link it to top, update top and size.
 * TODO 4: Implement pop()
 * If empty throw EmptyStackException.
 * Save top.data, advance top = top.next, decrement size, return saved data.
 * TODO 5: Implement peek()
 * If empty throw EmptyStackException. Return top.data.
 * TODO 6: Implement isEmpty() and size().
 * TODO 7: Implement toString() — print [top → ... → bottom]
 */
public class Stack<T> {

    private static class Node<T> {
        T data;
        Node<T> next;

        Node(T data) {
            this.data = data;
        }
    }

    // TODO 2: fields

    // TODO 3
    public void push(T data) {
        /* TODO */ }

    // TODO 4
    public T pop() {
        // TODO
        throw new EmptyStackException();
    }

    // TODO 5
    public T peek() {
        // TODO
        throw new EmptyStackException();
    }

    // TODO 6
    public boolean isEmpty() {
        return true;
        /* TODO */ }

    public int size() {
        return 0;
        /* TODO */ }

    // TODO 7
    @Override
    public String toString() {
        return "[]";
        /* TODO */ }
}
