import java.lang.ref.*;
import java.util.*;

/**
 * DAY 10 — PART 2 | Garbage Collection, JVM Tuning & Reference Types
 * ─────────────────────────────────────────────────────────────────────────────
 * Java's Garbage Collector (GC) automatically reclaims memory for objects
 * that are no longer REACHABLE — no live reference chain can reach them.
 *
 * GC ALGORITHMS (evolution):
 * ┌───────────────────────────────────────────────────────────────────────────┐
 * │ Serial GC        │ Single-threaded; small heaps; -XX:+UseSerialGC         │
 * │ Parallel GC      │ Multi-threaded stop-the-world; default Java 8          │
 * │ CMS GC           │ Concurrent Mark Sweep; low-latency (deprecated)        │
 * │ G1 GC            │ Garbage-First; default Java 9+; predictable pause      │
 * │ ZGC              │ Sub-millisecond pauses; Java 15+; massive heaps        │
 * │ Shenandoah       │ Concurrent compaction; Red Hat; low latency            │
 * └───────────────────────────────────────────────────────────────────────────┘
 *
 * GENERATIONAL HYPOTHESIS:
 *   Most objects die young. GC exploits this by splitting the heap:
 *
 *   Heap
 *   ├── Young Generation
 *   │   ├── Eden      ← new objects allocated here
 *   │   ├── Survivor 0
 *   │   └── Survivor 1
 *   └── Old (Tenured) Generation  ← long-lived objects promoted here
 *
 *   Minor GC: collects Young Generation (frequent, fast)
 *   Major GC: collects Old Generation (infrequent, slower)
 *   Full GC:  collects entire heap (avoid in production)
 *
 * JVM TUNING FLAGS (awareness):
 *   -Xms512m            Initial heap size
 *   -Xmx4g              Maximum heap size
 *   -XX:+UseG1GC        Use G1 Garbage Collector
 *   -XX:MaxGCPauseMillis=200  Target max GC pause (G1)
 *   -XX:NewRatio=3      Old:Young ratio (3:1 means 75% old, 25% young)
 *   -verbose:gc         Log GC events
 *   -XX:+PrintGCDetails Log detailed GC events
 */
public class GarbageCollection {

    public static void main(String[] args) throws InterruptedException {
        demonstrateGCBasics();
        demonstrateGenerationalGC();
        demonstrateReferenceTypes();
        demonstrateMemoryLeak();
        demonstrateJvmTuningAwareness();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 1 — GC Basics: Reachability
    // An object is eligible for GC when no strong reference chain leads to it.
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateGCBasics() {
        System.out.println("=== GC Basics: Reachability ===");

        // Object is REACHABLE — strong reference 'product' points to it on the heap
        Product product = new Product("Laptop", 999.99);
        System.out.println("Object reachable: " + product);

        // Make it unreachable — now eligible for GC (but not necessarily collected yet)
        product = null;
        System.out.println("product set to null — object is now GC-eligible");

        // GC is non-deterministic — we can REQUEST it but not force it
        System.gc();   // hint to JVM; JVM is free to ignore this
        System.out.println("System.gc() called — JVM MAY collect unreachable objects");

        // finalize() is called (at most once) before collection — DEPRECATED in Java 9+
        // Modern pattern: use try-with-resources and Cleaners instead
        System.out.println("Note: finalize() is deprecated. Use try-with-resources or java.lang.ref.Cleaner.\n");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 2 — Generational GC in Action (simulated)
    // Young gen = Eden + 2 Survivors. Objects that survive multiple minor GCs
    // are promoted to Old gen.
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateGenerationalGC() throws InterruptedException {
        System.out.println("=== Generational GC ===");
        System.out.println("Heap regions (conceptual):");
        System.out.println("  [Eden] → Minor GC → [Survivor 0/1] → n rounds → [Old Gen]");

        // Simulate: short-lived objects (most objects are like this)
        System.out.println("\nSimulating short-lived objects (request handling pattern):");
        for (int request = 1; request <= 5; request++) {
            // These objects are created per-request and should be GC'd after
            processRequest(request);
        }
        System.out.println("Request objects created per call — eligible for GC after method returns");

        // Simulate: long-lived objects (cache, singletons, static collections)
        System.out.println("\nLong-lived object (simulated cache entry — stays in Old gen):");
        Cache cache = Cache.getInstance();
        cache.put("config.timeout", "30s");
        System.out.println("Cache entry survives: " + cache.get("config.timeout"));

        System.out.println("\nKey insight: short-lived objects cause Minor GC;");
        System.out.println("long-lived objects promote to Old gen and cause Major/Full GC.\n");
    }

    static void processRequest(int id) {
        // These objects are created on the heap but become unreachable when this method returns
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Request-ID", "req-" + id);
        headers.put("Content-Type", "application/json");
        String body = "{\"requestId\":" + id + "}";
        System.out.println("  Processing request " + id + " with " + headers.size() + " headers");
        // headers and body become GC-eligible after this method returns
    }

    static class Cache {
        private static final Cache INSTANCE = new Cache();  // static = lives until JVM exits
        private final Map<String, String> store = new HashMap<>();
        private Cache() {}
        static Cache getInstance() { return INSTANCE; }
        void put(String k, String v) { store.put(k, v); }
        String get(String k)         { return store.get(k); }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 3 — Reference Types
    // Java has four reference strength levels that interact with GC differently.
    //
    //   Strong   →  Normal references (Product p = new Product(...))
    //              GC will NEVER collect while strongly reachable
    //
    //   Weak     →  WeakReference<T> — GC collects on NEXT cycle
    //              Use: WeakHashMap (cache that evicts under memory pressure)
    //
    //   Soft     →  SoftReference<T> — GC collects only when MEMORY IS LOW
    //              Use: memory-sensitive caches (images, computed data)
    //
    //   Phantom  →  PhantomReference<T> — object already being finalized
    //              Use: resource cleanup, replace finalize()
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateReferenceTypes() throws InterruptedException {
        System.out.println("=== Reference Types ===");

        // ── Strong Reference ──────────────────────────────────────────────────
        System.out.println("-- Strong Reference --");
        Product strongProduct = new Product("Strong Laptop", 999.0);
        System.out.println("Strong ref: " + strongProduct.name + " — GC will NEVER collect");
        strongProduct = null;   // now eligible
        System.out.println("Strong ref nulled — now eligible for GC\n");

        // ── Weak Reference ─────────────────────────────────────────────────────
        System.out.println("-- Weak Reference --");
        Product product = new Product("Weak Laptop", 899.0);
        WeakReference<Product> weakRef = new WeakReference<>(product);

        System.out.println("Before GC hint — weakRef.get(): " + weakRef.get());

        product = null;    // remove the only strong reference
        System.gc();       // hint GC to run
        Thread.sleep(100); // give GC time to act

        System.out.println("After GC hint  — weakRef.get(): " + weakRef.get());
        // May be null — GC can collect when only weakly reachable
        System.out.println("(null means GC collected it)\n");

        // ── Soft Reference ─────────────────────────────────────────────────────
        System.out.println("-- Soft Reference --");
        Product expensiveData = new Product("Cached Report", 0.0);
        SoftReference<Product> softRef = new SoftReference<>(expensiveData);
        expensiveData = null;

        // Soft references survive GC as long as heap has plenty of free space
        // Only cleared when JVM is about to throw OutOfMemoryError
        System.out.println("Soft ref still available: " + (softRef.get() != null));
        System.out.println("Soft refs survive memory pressure — ideal for caches\n");

        // ── Phantom Reference ──────────────────────────────────────────────────
        System.out.println("-- Phantom Reference --");
        ReferenceQueue<Product> queue = new ReferenceQueue<>();
        Product expiring = new Product("Expiring Product", 0.0);
        PhantomReference<Product> phantomRef = new PhantomReference<>(expiring, queue);

        System.out.println("phantomRef.get() always returns: " + phantomRef.get());  // always null
        expiring = null;
        System.gc();
        Thread.sleep(100);

        Reference<?> ref = queue.poll();
        System.out.println("Phantom ref enqueued (object being collected): " + (ref != null));
        System.out.println("Use ReferenceQueue to run cleanup code when object is GC'd\n");

        // ── WeakHashMap — practical example ───────────────────────────────────
        System.out.println("-- WeakHashMap (practical cache) --");
        WeakHashMap<String, String> cache = new WeakHashMap<>();
        // In WeakHashMap, keys are weakly referenced
        // When a key has no other strong references, the entry is automatically removed
        String key = new String("session-abc");   // heap string (not pooled)
        cache.put(key, "user session data");
        System.out.println("Cache size before: " + cache.size());
        key = null;
        System.gc();
        Thread.sleep(100);
        System.out.println("Cache size after key GC'd: " + cache.size() + " (entry auto-removed)\n");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 4 — Memory Leak Patterns
    // Java prevents most memory leaks via GC, but you CAN still leak memory:
    //   1. Forgetting to remove listeners
    //   2. Static collections that grow without bound
    //   3. Holding references longer than needed (e.g., in fields vs local vars)
    //   4. Thread-local values not removed after thread returns to pool
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateMemoryLeak() {
        System.out.println("=== Memory Leak Patterns ===");

        // LEAK PATTERN 1: Static collection accumulates forever
        System.out.println("-- Static Collection Leak --");
        LeakyEventBus bus = LeakyEventBus.getInstance();
        for (int i = 0; i < 5; i++) {
            bus.register("handler-" + i);   // handlers registered but never unregistered
        }
        System.out.println("LeakyEventBus has " + bus.handlerCount() + " handlers (never cleaned)");
        System.out.println("In production, after days/weeks: thousands of stale handlers → OOM");

        // FIX: use weak references so abandoned handlers can be GC'd
        System.out.println("\n-- Fixed: WeakReference in event bus --");
        SafeEventBus safebus = new SafeEventBus();
        for (int i = 0; i < 5; i++) {
            safebus.register(new Object());  // registered with WeakReference — GC-friendly
        }
        System.gc();
        safebus.cleanup();
        System.out.println("SafeEventBus handlers after GC: " + safebus.handlerCount() +
                " (stale refs removed)\n");

        // LEAK PATTERN 2: Forgotten ThreadLocal
        System.out.println("-- ThreadLocal Leak Pattern --");
        System.out.println("ThreadLocal values must be removed when thread returns to pool:");
        System.out.println("  threadLocal.remove()   ← call in finally block");
        System.out.println("  Skipping remove() in thread pools = stale data + memory leak\n");
    }

    static class LeakyEventBus {
        private static final LeakyEventBus INSTANCE = new LeakyEventBus();
        private final List<String> handlers = new ArrayList<>();   // static list — never cleared
        private LeakyEventBus() {}
        static LeakyEventBus getInstance() { return INSTANCE; }
        void register(String handler) { handlers.add(handler); }
        int handlerCount() { return handlers.size(); }
    }

    static class SafeEventBus {
        // Weak references — GC can collect handlers when they have no other strong references
        private final List<WeakReference<Object>> handlers = new ArrayList<>();
        void register(Object handler) { handlers.add(new WeakReference<>(handler)); }
        void cleanup() { handlers.removeIf(ref -> ref.get() == null); }
        int handlerCount() { return handlers.size(); }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 5 — JVM Tuning Awareness
    // Common flags and diagnostics you'll see in production
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateJvmTuningAwareness() {
        System.out.println("=== JVM Tuning Basics ===");
        System.out.println();
        System.out.println("Heap Sizing:");
        System.out.println("  -Xms512m                  Initial heap (start here to avoid resizing)");
        System.out.println("  -Xmx4g                    Maximum heap (cap to avoid OOM)");
        System.out.println("  Rule: set -Xms = -Xmx to avoid heap resize pauses");
        System.out.println();
        System.out.println("GC Selection:");
        System.out.println("  -XX:+UseG1GC              G1 (default Java 9+, balanced)");
        System.out.println("  -XX:+UseZGC               ZGC (Java 15+, sub-ms pauses, large heaps)");
        System.out.println("  -XX:+UseSerialGC           Serial (single-thread, small containers)");
        System.out.println();
        System.out.println("G1 Tuning:");
        System.out.println("  -XX:MaxGCPauseMillis=200  Target max pause (G1 tries to meet this)");
        System.out.println("  -XX:NewRatio=3             Old:Young = 3:1");
        System.out.println();
        System.out.println("Diagnostics:");
        System.out.println("  -verbose:gc                Print GC events to stdout");
        System.out.println("  -Xlog:gc*:file=gc.log      Log GC to file (Java 9+)");
        System.out.println("  -XX:+HeapDumpOnOutOfMemoryError  Dump heap on OOM (critical for debugging)");
        System.out.println("  -XX:HeapDumpPath=/tmp/heap.hprof");
        System.out.println();
        System.out.println("Tools:");
        System.out.println("  jconsole       — visual JVM monitoring (live heap, threads, GC)");
        System.out.println("  jvisualvm      — heap dump analysis, CPU/memory profiling");
        System.out.println("  jstat -gcutil  <pid>  — GC stats from command line");
        System.out.println("  jmap -heap     <pid>  — heap summary");
        System.out.println("  jstack         <pid>  — thread dump (debug deadlocks)");

        // Runtime API: get live heap info
        Runtime rt = Runtime.getRuntime();
        System.out.println();
        System.out.printf("Runtime.maxMemory():   %,d bytes (~%d MB)%n",
                rt.maxMemory(), rt.maxMemory() / (1024 * 1024));
        System.out.printf("Runtime.totalMemory(): %,d bytes (currently allocated)%n",
                rt.totalMemory());
        System.out.printf("Runtime.freeMemory():  %,d bytes (free in allocated)%n",
                rt.freeMemory());
        System.out.printf("Available processors:  %d%n%n", rt.availableProcessors());
    }

    // Helper model class
    static class Product {
        String name;
        double price;
        Product(String name, double price) { this.name = name; this.price = price; }
        @Override public String toString() { return "Product{name='" + name + "', price=" + price + "}"; }
        @Override protected void finalize() {
            // finalize() called before GC — deprecated, just for demo
            // System.out.println("finalize() called for: " + name);
        }
    }
}
