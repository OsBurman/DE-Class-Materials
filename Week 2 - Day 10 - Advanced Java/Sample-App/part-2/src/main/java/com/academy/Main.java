package com.academy;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Day 10 Part 2 — Serialization, Debugging Tips, Garbage Collection & Memory
 *
 * Theme: Student Records Persistence System
 * Run: mvn compile exec:java
 */
public class Main {

    public static void main(String[] args) throws Exception {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║  Day 10 Part 2 — Serialization, GC & Debugging Demo      ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");

        demoSerialization();
        demoGarbageCollection();
        demoReferenceTypes();
        demoDebuggingTips();
    }

    // ─────────────────────────────────────────────────────────
    // 1. Serialization & Deserialization
    // ─────────────────────────────────────────────────────────
    static void demoSerialization() throws Exception {
        System.out.println("=== 1. Serialization & Deserialization ===");
        System.out.println("  Serialization = convert object → byte stream (save to file/send over network)");
        System.out.println("  Deserialization = byte stream → object (restore state)");

        Path serFile = Path.of("student.ser");
        StudentRecord alice = new StudentRecord("Alice", 1001, 95.5, "Computer Science");
        alice.setTempCache("runtime data"); // transient — will NOT be serialized

        // Serialize
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(serFile))) {
            oos.writeObject(alice);
        }
        System.out.println("  Serialized: " + alice);

        // Deserialize
        StudentRecord restored;
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(serFile))) {
            restored = (StudentRecord) ois.readObject();
        }
        System.out.println("  Restored:   " + restored);
        System.out.println("  transient field (tempCache): '" + restored.getTempCache() + "'  ← not saved");

        // Serialization of a List
        List<StudentRecord> records = List.of(
            new StudentRecord("Bob",   1002, 88.0, "Data Engineering"),
            new StudentRecord("Carol", 1003, 92.0, "Software Engineering")
        );
        Path listFile = Path.of("students.ser");
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(listFile))) {
            oos.writeObject(new ArrayList<>(records));
        }
        List<StudentRecord> restoredList;
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(listFile))) {
            restoredList = (List<StudentRecord>) ois.readObject();
        }
        System.out.println("  Restored list: " + restoredList.stream().map(StudentRecord::getName).toList());

        // Cleanup
        Files.deleteIfExists(serFile);
        Files.deleteIfExists(listFile);
        System.out.println();
    }

    // ─────────────────────────────────────────────────────────
    // 2. Garbage Collection Overview
    // ─────────────────────────────────────────────────────────
    static void demoGarbageCollection() {
        System.out.println("=== 2. Garbage Collection ===");

        Runtime rt = Runtime.getRuntime();
        long before = rt.totalMemory() - rt.freeMemory();

        // Allocate many short-lived objects
        for (int i = 0; i < 50_000; i++) {
            String s = "temp-object-" + i;
        }

        long after = rt.totalMemory() - rt.freeMemory();
        System.out.printf("  Memory before alloc: %,d bytes%n", before);
        System.out.printf("  Memory after alloc:  %,d bytes%n", after);

        // Suggest GC (JVM may or may not comply)
        System.gc();
        long afterGC = rt.totalMemory() - rt.freeMemory();
        System.out.printf("  Memory after gc():   %,d bytes%n", afterGC);

        System.out.println("  GC Basics:");
        System.out.println("   - Objects with NO references are eligible for GC");
        System.out.println("   - Young Gen (Eden/Survivor) → Old Gen → PermGen/Metaspace");
        System.out.println("   - G1GC is default since Java 9; ZGC for low-latency");
        System.out.println("   - Avoid finalize() — use try-with-resources instead");
        System.out.println();
    }

    // ─────────────────────────────────────────────────────────
    // 3. Reference Types
    // ─────────────────────────────────────────────────────────
    static void demoReferenceTypes() {
        System.out.println("=== 3. Reference Types ===");

        // Strong reference — normal
        StudentRecord strong = new StudentRecord("Dave", 1004, 78.0, "QA");
        System.out.println("  Strong ref: " + strong.getName() + " (never GC'd while reachable)");

        // Weak reference — GC'd when only weakly reachable
        java.lang.ref.WeakReference<StudentRecord> weak =
            new java.lang.ref.WeakReference<>(new StudentRecord("Eve", 1005, 85.0, "DevOps"));
        System.out.println("  Weak ref before GC: " + (weak.get() != null ? weak.get().getName() : "null"));
        System.gc();
        System.out.println("  Weak ref after GC:  " + (weak.get() != null ? weak.get().getName() : "collected (null)"));

        // Soft reference — GC'd only under memory pressure (useful for caches)
        java.lang.ref.SoftReference<byte[]> cache =
            new java.lang.ref.SoftReference<>(new byte[1024]);
        System.out.println("  Soft ref (cache): " + (cache.get() != null ? "present" : "evicted"));
        System.out.println();
    }

    // ─────────────────────────────────────────────────────────
    // 4. Debugging Tips
    // ─────────────────────────────────────────────────────────
    static void demoDebuggingTips() {
        System.out.println("=== 4. Debugging Strategies ===");
        System.out.println("  ① Use breakpoints in IDE instead of print statements");
        System.out.println("  ② Inspect stack traces — read from top (where error occurred)");
        System.out.println("  ③ Divide & Conquer — binary-search the bug");
        System.out.println("  ④ Check edge cases: null, empty, 0, negative, max value");

        // Demonstrate stack trace reading
        try {
            methodA();
        } catch (Exception e) {
            System.out.println("\n  Stack trace (read top-to-bottom):");
            for (StackTraceElement el : e.getStackTrace()) {
                if (el.getClassName().startsWith("com.academy")) {
                    System.out.println("    at " + el);
                }
            }
            System.out.println("  Root cause: " + e.getMessage());
        }

        System.out.println("\n  ⑤ Thread dumps: jstack <pid>");
        System.out.println("  ⑥ Heap dumps: jmap -dump:format=b,file=heap.hprof <pid>");
        System.out.println("  ⑦ JVM flags: -Xmx512m -Xms256m -verbose:gc");
        System.out.println("\n✓ Advanced Java Part 2 demo complete.");
    }

    static void methodA() { methodB(); }
    static void methodB() { methodC(); }
    static void methodC() { throw new RuntimeException("NullPointerException traced to methodC"); }
}

class StudentRecord implements Serializable {
    @Serial private static final long serialVersionUID = 1L;
    private final String name;
    private final int id;
    private final double gpa;
    private final String major;
    private transient String tempCache; // NOT serialized

    public StudentRecord(String n, int id, double g, String m) { name=n; this.id=id; gpa=g; major=m; }
    public String getName()         { return name; }
    public void setTempCache(String v) { tempCache = v; }
    public String getTempCache()    { return tempCache; }
    public String toString()        { return "Student[" + name + " #" + id + " GPA=" + gpa + " " + major + "]"; }
}
