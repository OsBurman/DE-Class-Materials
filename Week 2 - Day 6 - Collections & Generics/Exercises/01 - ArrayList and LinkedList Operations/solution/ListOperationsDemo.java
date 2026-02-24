import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ListOperationsDemo {
    public static void main(String[] args) {

        // ===================== PART 1: ArrayList =====================
        System.out.println("=== ArrayList - Student Roster ===");

        ArrayList<String> roster = new ArrayList<>();
        roster.add("Alice");
        roster.add("Bob");
        roster.add("Carol");
        roster.add("Dave");
        roster.add("Alice");   // duplicate — List allows this, unlike Set

        System.out.println("Roster: " + roster);
        System.out.println("Size: " + roster.size());
        System.out.println("Element at index 2: " + roster.get(2));
        System.out.println("Contains Carol: " + roster.contains("Carol"));

        roster.remove("Bob");           // remove(Object) — removes first matching value
        System.out.println("After removing Bob: " + roster);

        roster.remove(0);               // remove(int index) — removes by position
        System.out.println("After removing index 0: " + roster);

        System.out.println();

        // ===================== PART 2: LinkedList =====================
        System.out.println("=== LinkedList - Waitlist ===");

        // LinkedList implements both List and Deque, giving it addFirst/addLast etc.
        LinkedList<String> waitlist = new LinkedList<>();
        waitlist.add("Eve");
        waitlist.add("Frank");
        waitlist.add("Grace");
        waitlist.addFirst("Zara");    // O(1) — head insertion, no shifting needed
        waitlist.addLast("Henry");    // O(1) — tail insertion
        System.out.println("Waitlist: " + waitlist);

        String first = waitlist.removeFirst();
        System.out.println("Removed first: " + first + " → Waitlist: " + waitlist);

        String last = waitlist.removeLast();
        System.out.println("Removed last: " + last + " → Waitlist: " + waitlist);

        System.out.println();

        // ===================== PART 3: Iteration =====================
        System.out.println("=== Indexed For Loop ===");
        for (int i = 0; i < roster.size(); i++) {
            System.out.println(i + ": " + roster.get(i));
        }

        System.out.println();
        System.out.println("=== Enhanced For Loop ===");
        for (String name : roster) {
            System.out.println(name);
        }

        System.out.println();
        System.out.println("ArrayList: fast random access (get by index). LinkedList: fast add/remove at ends.");
    }
}
