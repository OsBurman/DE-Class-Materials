import java.util.NoSuchElementException;

/** Generic Queue — Solution */
public class Queue<T> {
    private static class Node<T> { T data; Node<T> next; Node(T d){data=d;} }
    private Node<T> head, tail; private int size;

    public void enqueue(T data) {
        Node<T> n = new Node<>(data);
        if (tail != null) tail.next = n;
        tail = n;
        if (head == null) head = n;
        size++;
    }

    public T dequeue() {
        if (isEmpty()) throw new NoSuchElementException("Queue is empty");
        T d = head.data; head = head.next;
        if (head == null) tail = null;
        size--; return d;
    }

    public T front() { if (isEmpty()) throw new NoSuchElementException("Queue is empty"); return head.data; }
    public boolean isEmpty() { return size == 0; }
    public int     size()    { return size; }

    @Override public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (Node<T> n = head; n != null; n = n.next) {
            sb.append(n.data); if (n.next != null) sb.append(", ");
        }
        return sb.append("]").toString();
    }
}
