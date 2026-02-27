import java.util.*;

/**
 * Generic Binary Search Tree — STARTER CODE
 *
 * BST property: left subtree values < node < right subtree values
 * Requires T to implement Comparable so we can compare values.
 *
 * TODO 1: The Node class is provided.
 * TODO 2: Add field: private Node<T> root.
 * TODO 3: Implement insert(T data) — call recursive insertRec.
 *   insertRec: if node == null return new Node(data).
 *              if data < node.data → recurse left; if > recurse right; if == ignore.
 * TODO 4: Implement contains(T data) — call recursive containsRec.
 * TODO 5: Implement inOrder() — return sorted List<T> via recursive in-order traversal.
 * TODO 6: Implement height() — max depth. Empty tree = 0. Leaf = 1.
 *   heightRec(node): if null return 0; return 1 + max(left, right)
 * TODO 7: Implement min() — traverse left until null. Return leftmost value.
 * TODO 8: Implement max() — traverse right until null.
 */
public class BST<T extends Comparable<T>> {

    private static class Node<T> {
        T data; Node<T> left, right;
        Node(T data) { this.data = data; }
    }

    // TODO 2: private Node<T> root;

    // TODO 3
    public void insert(T data) { /* TODO: root = insertRec(root, data); */ }
    private Node<T> insertRec(Node<T> node, T data) { return null; /* TODO */ }

    // TODO 4
    public boolean contains(T data) { return false; /* TODO */ }
    private boolean containsRec(Node<T> node, T data) { return false; /* TODO */ }

    // TODO 5
    public List<T> inOrder() { List<T> result = new ArrayList<>(); /* TODO: inOrderRec(root, result); */ return result; }
    private void inOrderRec(Node<T> node, List<T> result) { /* TODO */ }

    // TODO 6
    public int height() { return 0; /* TODO: return heightRec(root); */ }
    private int heightRec(Node<T> node) { return 0; /* TODO */ }

    // TODO 7
    public T min() { return null; /* TODO: traverse left */ }

    // TODO 8
    public T max() { return null; /* TODO: traverse right */ }

    public boolean isEmpty() { return root == null; }
    // above line will fail to compile until TODO 2 is done — that's intentional
}
