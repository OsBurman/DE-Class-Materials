import java.util.EmptyStackException;

/** Generic Stack — Solution */
public class Stack<T> {
    private static class Node<T> {
        T data;
        Node<T> next;

        Node(T d) {
            data = d;
        }
    }

    private Node<T> top;
    private int size;

    public void push(T data) {
        Node<T> n = new Node<>(data);
        n.next = top;
        top = n;
        size++;
    }

    public T pop() {
        if (isEmpty())
            throw new EmptyStackException();
        T d = top.data;
        top = top.next;
        size--;
        return d;
    }

    public T peek() {
        if (isEmpty())
            throw new EmptyStackException();
        return top.data;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (Node<T> n = top; n != null; n = n.next) {
            sb.append(n.data);
            if (n.next != null)
                sb.append(" → ");
        }
        return sb.append("]").toString();
    }
}
