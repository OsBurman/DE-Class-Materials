import java.util.*;

/** Generic Binary Search Tree — Solution */
public class BST<T extends Comparable<T>> {
    private static class Node<T> { T data; Node<T> left, right; Node(T d){data=d;} }
    private Node<T> root;

    public void insert(T data) { root = insertRec(root, data); }
    private Node<T> insertRec(Node<T> n, T data) {
        if (n == null) return new Node<>(data);
        int cmp = data.compareTo(n.data);
        if      (cmp < 0) n.left  = insertRec(n.left,  data);
        else if (cmp > 0) n.right = insertRec(n.right, data);
        return n;
    }

    public boolean contains(T data) { return containsRec(root, data); }
    private boolean containsRec(Node<T> n, T data) {
        if (n == null) return false;
        int cmp = data.compareTo(n.data);
        if (cmp == 0) return true;
        return cmp < 0 ? containsRec(n.left, data) : containsRec(n.right, data);
    }

    public List<T> inOrder() { List<T> r = new ArrayList<>(); inOrderRec(root, r); return r; }
    private void inOrderRec(Node<T> n, List<T> r) {
        if (n == null) return;
        inOrderRec(n.left, r); r.add(n.data); inOrderRec(n.right, r);
    }

    public int height() { return heightRec(root); }
    private int heightRec(Node<T> n) {
        if (n == null) return 0;
        return 1 + Math.max(heightRec(n.left), heightRec(n.right));
    }

    public T min() {
        if (root == null) return null;
        Node<T> cur = root; while (cur.left != null) cur = cur.left; return cur.data;
    }

    public T max() {
        if (root == null) return null;
        Node<T> cur = root; while (cur.right != null) cur = cur.right; return cur.data;
    }

    public boolean isEmpty() { return root == null; }
}
