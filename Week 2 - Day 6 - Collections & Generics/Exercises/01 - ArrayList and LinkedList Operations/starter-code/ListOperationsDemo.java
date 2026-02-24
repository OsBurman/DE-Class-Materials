import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ListOperationsDemo {
    public static void main(String[] args) {

        // ===================== PART 1: ArrayList =====================
        System.out.println("=== ArrayList - Student Roster ===");

        // TODO: Create an ArrayList<String> named roster
        // TODO: Add "Alice", "Bob", "Carol", "Dave", "Alice" (duplicate is intentional)

        // TODO: Print "Roster: " + roster
        // TODO: Print "Size: " + roster.size()
        // TODO: Print "Element at index 2: " + roster.get(2)
        // TODO: Print "Contains Carol: " + roster.contains("Carol")

        // TODO: Remove "Bob" by value: roster.remove("Bob")
        // TODO: Print "After removing Bob: " + roster

        // TODO: Remove the element at index 0: roster.remove(0)
        // TODO: Print "After removing index 0: " + roster

        System.out.println();

        // ===================== PART 2: LinkedList =====================
        System.out.println("=== LinkedList - Waitlist ===");

        // TODO: Create a LinkedList<String> named waitlist
        // TODO: Add "Eve", "Frank", "Grace" using add()
        // TODO: Add "Zara" to the FRONT using addFirst()
        // TODO: Add "Henry" to the BACK using addLast()
        // TODO: Print "Waitlist: " + waitlist

        // TODO: Store waitlist.removeFirst() in a variable 'first'
        // TODO: Print "Removed first: " + first + " → Waitlist: " + waitlist

        // TODO: Store waitlist.removeLast() in a variable 'last'
        // TODO: Print "Removed last: " + last + " → Waitlist: " + waitlist

        System.out.println();

        // ===================== PART 3: Iteration =====================
        System.out.println("=== Indexed For Loop ===");
        // TODO: Use a standard indexed for loop over roster
        //       Print "[i]: [roster.get(i)]" for each element

        System.out.println();
        System.out.println("=== Enhanced For Loop ===");
        // TODO: Use an enhanced for loop over roster and print each name

        System.out.println();

        // TODO: Print the comparison note:
        // "ArrayList: fast random access (get by index). LinkedList: fast add/remove at ends."
    }
}
