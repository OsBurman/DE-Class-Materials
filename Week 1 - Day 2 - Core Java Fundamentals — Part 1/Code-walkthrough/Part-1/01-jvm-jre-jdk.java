// ============================================================
// FILE: 01-jvm-jre-jdk.java
// TOPIC: JVM, JRE, and JDK Architecture
// ============================================================
// This file is a conceptual walkthrough — Java is compiled and
// run, so we use comments and System.out to illustrate the
// layers of the Java platform architecture.
// ============================================================

/**
 * JDK  (Java Development Kit)
 *   └── JRE  (Java Runtime Environment)
 *         └── JVM  (Java Virtual Machine)
 *
 * JDK  = JRE + development tools (javac compiler, javadoc, jdb debugger, jar)
 * JRE  = JVM + standard class libraries (java.lang, java.util, java.io, etc.)
 * JVM  = The engine that RUNS bytecode — it is platform-specific
 *
 * HOW YOUR CODE RUNS:
 *
 *   Step 1: You write  HelloWorld.java        (source code)
 *   Step 2: javac compiles it →  HelloWorld.class  (bytecode — NOT machine code)
 *   Step 3: JVM reads the .class file and interprets/JIT-compiles it to
 *           native machine instructions for YOUR specific OS/CPU
 *
 *   This is why Java is "Write Once, Run Anywhere":
 *   The same .class bytecode file runs on Windows, Mac, or Linux
 *   as long as a JVM is installed.
 *
 * KEY TERMS:
 *   - Bytecode    : intermediate instructions understood by the JVM (not the CPU)
 *   - JIT         : Just-In-Time compiler — part of the JVM that converts
 *                   frequently-run bytecode to native machine code at runtime
 *                   for better performance
 *   - Class Loader: loads .class files into JVM memory
 *   - Garbage Collector (GC): automatically frees unused memory — you don't
 *                   manually allocate/free memory like in C/C++
 *
 * JVM MEMORY AREAS:
 *   - Heap     : where objects live (managed by Garbage Collector)
 *   - Stack    : each thread gets its own stack; holds method frames,
 *                local variables, and return addresses
 *   - Method Area: stores class-level info (class name, methods, fields)
 *   - PC Register: tracks the current instruction being executed per thread
 */
public class JvmJreJdk {

    public static void main(String[] args) {

        // ── PRINT JVM / RUNTIME INFO ──────────────────────────────────
        // Java exposes JVM metadata through System.getProperty()
        System.out.println("=== JVM / JRE / JDK Information ===");

        // Which JVM vendor and version is running right now?
        System.out.println("Java Version  : " + System.getProperty("java.version"));
        System.out.println("JVM Name      : " + System.getProperty("java.vm.name"));
        System.out.println("JVM Version   : " + System.getProperty("java.vm.version"));
        System.out.println("JRE Home      : " + System.getProperty("java.home"));
        System.out.println("OS Name       : " + System.getProperty("os.name"));
        System.out.println("OS Arch       : " + System.getProperty("os.arch"));

        System.out.println();

        // ── HEAP MEMORY DEMO ──────────────────────────────────────────
        // The JVM manages memory for us — we can query how much is available
        Runtime runtime = Runtime.getRuntime();
        long maxMemory  = runtime.maxMemory()   / (1024 * 1024); // convert bytes → MB
        long totalMemory = runtime.totalMemory() / (1024 * 1024);
        long freeMemory  = runtime.freeMemory()  / (1024 * 1024);

        System.out.println("=== JVM Heap Memory ===");
        System.out.println("Max Heap    : " + maxMemory   + " MB");
        System.out.println("Total Heap  : " + totalMemory + " MB  (currently allocated from OS)");
        System.out.println("Free Heap   : " + freeMemory  + " MB  (available within allocated heap)");

        System.out.println();

        // ── COMPILATION STAGES SUMMARY ───────────────────────────────
        System.out.println("=== Compilation Journey of This File ===");
        System.out.println("1. JvmJreJdk.java   → written by YOU");
        System.out.println("2. JvmJreJdk.class  → produced by 'javac' (JDK tool)");
        System.out.println("3. JVM loads .class → interprets bytecode via JIT");
        System.out.println("4. Output appears on screen  ← you are HERE");
    }
}
