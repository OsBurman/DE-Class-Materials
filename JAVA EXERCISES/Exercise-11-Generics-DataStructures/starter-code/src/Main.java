import java.util.*;

/**
 * Exercise 11 — Generics & Data Structures: Main driver
 * Do not modify.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("=== Generic Stack<Integer> ===");
        Stack<Integer> stack = new Stack<>();
        stack.push(10); stack.push(20); stack.push(30);
        System.out.println("Stack: " + stack);
        System.out.println("Peek: " + stack.peek());
        System.out.println("Pop:  " + stack.pop());
        System.out.println("After pop: " + stack);
        System.out.println("Size: " + stack.size());

        System.out.println("\n=== Generic Queue<String> ===");
        Queue<String> queue = new Queue<>();
        queue.enqueue("alpha"); queue.enqueue("beta"); queue.enqueue("gamma");
        System.out.println("Queue: " + queue);
        System.out.println("Front:   " + queue.front());
        System.out.println("Dequeue: " + queue.dequeue());
        System.out.println("After dequeue: " + queue);

        System.out.println("\n=== BST<Integer> ===");
        BST<Integer> bst = new BST<>();
        for (int v : new int[]{5, 3, 8, 1, 4, 7, 9, 2, 6}) bst.insert(v);
        System.out.println("In-order (sorted): " + bst.inOrder());
        System.out.println("Contains 4: " + bst.contains(4));
        System.out.println("Contains 99: " + bst.contains(99));
        System.out.println("Min: " + bst.min() + "  Max: " + bst.max());
        System.out.println("Height: " + bst.height());

        System.out.println("\n=== BST<String> ===");
        BST<String> strBst = new BST<>();
        for (String s : new String[]{"mango","apple","pear","banana","cherry"}) strBst.insert(s);
        System.out.println("In-order: " + strBst.inOrder());

        System.out.println("\n=== Utils ===");
        Integer[] nums = {5, 1, 8, 3};
        System.out.println("Before swap: " + Arrays.toString(nums));
        Utils.swap(nums, 0, 2);
        System.out.println("After swap [0,2]: " + Arrays.toString(nums));
        System.out.println("findMax: " + Utils.findMax(List.of(3, 1, 7, 2, 5)));
        System.out.println("sumList(int+double mix): " + Utils.sumList(List.of(1, 2.5, 3, 4.5)));
        System.out.println("repeat('★', 5): " + Utils.repeat("★", 5));
    }
}
