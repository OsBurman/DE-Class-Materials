package com.academy;

import java.util.*;

/**
 * Day 6 Part 1 — Collections: List, Set, Map, Queue
 *
 * Theme: Student Enrollment System
 * Run: mvn compile exec:java
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════╗");
        System.out.println("║  Day 6 Part 1 — Collections Framework Demo       ║");
        System.out.println("╚═══════════════════════════════════════════════════╝\n");

        demoList();
        demoSet();
        demoMap();
        demoQueue();
    }

    static void demoList() {
        System.out.println("=== 1. List — ArrayList (ordered, allows duplicates) ===");
        List<String> roster = new ArrayList<>();
        roster.add("Alice"); roster.add("Bob"); roster.add("Carol");
        roster.add("Dave");  roster.add("Bob"); // duplicates allowed

        System.out.println("  Roster: " + roster);
        System.out.println("  Size:   " + roster.size());
        System.out.println("  Get(1): " + roster.get(1));
        roster.remove("Bob");      // removes first occurrence
        System.out.println("  After remove(\"Bob\"): " + roster);

        roster.add(1, "Eve");
        System.out.println("  After add(1,\"Eve\"): " + roster);

        System.out.println("  Contains \"Carol\": " + roster.contains("Carol"));
        System.out.println("  Sorted: " + roster.stream().sorted().toList());

        // LinkedList — better for frequent insertions at front/middle
        LinkedList<String> queue = new LinkedList<>();
        queue.addFirst("Alice");
        queue.addLast("Bob");
        queue.addFirst("Eve");  // add to front
        System.out.println("  LinkedList: " + queue + " (Eve added to front)");
        System.out.println();
    }

    static void demoSet() {
        System.out.println("=== 2. Set — HashSet, LinkedHashSet, TreeSet ===");

        // HashSet — no order, no duplicates
        Set<String> hashSet = new HashSet<>(Arrays.asList("Math", "English", "Science", "Math"));
        System.out.println("  HashSet (no dups, no order): " + hashSet);

        // LinkedHashSet — insertion order preserved
        Set<String> linkedSet = new LinkedHashSet<>(Arrays.asList("Math", "English", "Science", "Math"));
        System.out.println("  LinkedHashSet (insertion order): " + linkedSet);

        // TreeSet — sorted alphabetically
        Set<String> treeSet = new TreeSet<>(Arrays.asList("Math", "English", "Science", "Art"));
        System.out.println("  TreeSet (sorted): " + treeSet);

        // Set operations
        Set<String> groupA = new HashSet<>(Arrays.asList("Alice", "Bob", "Carol"));
        Set<String> groupB = new HashSet<>(Arrays.asList("Bob", "Carol", "Dave"));
        Set<String> intersection = new HashSet<>(groupA);
        intersection.retainAll(groupB);
        System.out.println("  Intersection of groupA & groupB: " + intersection);
        System.out.println();
    }

    static void demoMap() {
        System.out.println("=== 3. Map — HashMap, LinkedHashMap, TreeMap ===");

        // HashMap — key→value, no order
        Map<String, Integer> grades = new HashMap<>();
        grades.put("Alice", 92); grades.put("Bob", 78);
        grades.put("Carol", 85); grades.put("Dave", 91);
        System.out.println("  Grades: " + grades);
        System.out.println("  Alice's grade: " + grades.get("Alice"));
        System.out.println("  Contains Eve: " + grades.containsKey("Eve"));
        System.out.println("  getOrDefault(\"Eve\", 0): " + grades.getOrDefault("Eve", 0));

        // Iterate
        System.out.println("  Iterating with entrySet():");
        grades.entrySet().stream()
              .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
              .forEach(e -> System.out.printf("    %-6s → %d%n", e.getKey(), e.getValue()));

        // putIfAbsent, computeIfAbsent
        grades.putIfAbsent("Eve", 88);
        System.out.println("  After putIfAbsent(\"Eve\",88): " + grades.get("Eve"));

        // TreeMap — sorted by key
        Map<String, Integer> sorted = new TreeMap<>(grades);
        System.out.println("  TreeMap (alpha order by name): " + sorted);
        System.out.println();
    }

    static void demoQueue() {
        System.out.println("=== 4. Queue — PriorityQueue, ArrayDeque ===");

        // PriorityQueue — min-heap by default
        Queue<Integer> pq = new PriorityQueue<>();
        pq.offer(5); pq.offer(1); pq.offer(3); pq.offer(2); pq.offer(4);
        System.out.print("  PriorityQueue poll order (min first): ");
        while (!pq.isEmpty()) System.out.print(pq.poll() + " ");
        System.out.println();

        // ArrayDeque — double-ended queue (Deque)
        Deque<String> deque = new ArrayDeque<>();
        deque.offerFirst("Alice");    // add to front
        deque.offerLast("Bob");       // add to back
        deque.offerFirst("Eve");      // add to front
        System.out.println("  Deque: " + deque);
        System.out.println("  peekFirst: " + deque.peekFirst() + "  peekLast: " + deque.peekLast());

        // Simulate a waiting list
        Queue<String> waitlist = new LinkedList<>();
        waitlist.offer("StudentA"); waitlist.offer("StudentB"); waitlist.offer("StudentC");
        System.out.print("  Processing waitlist (FIFO): ");
        while (!waitlist.isEmpty()) System.out.print(waitlist.poll() + " ");
        System.out.println();

        System.out.println("\n✓ Collections demo complete.");
    }
}
