import java.util.*;

/**
 * DAY 10 — PART 1 | Java Memory Model (JMM), Stack vs Heap
 * ─────────────────────────────────────────────────────────────────────────────
 * Every Java program uses two main memory regions managed by the JVM:
 *
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │  STACK (per thread)                │  HEAP (shared across all threads)  │
 * ├─────────────────────────────────────────────────────────────────────────┤
 * │  • Local variables (primitives)    │  • ALL objects (new Foo())         │
 * │  • References to heap objects      │  • Static variables (Method Area)  │
 * │  • Method call frames              │  • Instance variables              │
 * │  • LIFO — frame pushed on call,    │  • Managed by Garbage Collector    │
 * │    popped on return                │  • Survives across method calls     │
 * │  • Thread-private — no sharing     │  • Can be shared between threads   │
 * │  • Fixed per-thread size (~512KB–1M│  • Much larger (defaults to GBs)   │
 * │  • Fast allocation                 │  • Slower allocation               │
 * │  • StackOverflowError if full      │  • OutOfMemoryError if full        │
 * └─────────────────────────────────────────────────────────────────────────┘
 *
 * JMM (Java Memory Model):
 *   Defines the rules for how threads read/write shared variables.
 *   Guarantees: visibility, ordering, atomicity when using volatile/synchronized.
 *   See also: Day 9 synchronization and volatile demos.
 */
public class JavaMemoryModel {

    // ── Static field — lives in Method Area (part of Heap) ──────────────────
    static int applicationInstanceCount = 0;

    public static void main(String[] args) {
        demonstrateStackMemory();
        demonstrateHeapMemory();
        demonstrateReferenceVsValue();
        demonstrateStringPool();
        demonstrateMethodAreaAndStatic();
        demonstrateMemoryErrors();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 1 — Stack Memory
    // Each method invocation gets its own stack frame containing:
    //   - local variables (including primitive values and references)
    //   - return address
    //   - operand stack for intermediate computations
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateStackMemory() {
        System.out.println("=== Stack Memory ===");

        int quantity = 5;          // int value stored DIRECTLY in stack frame
        double price = 9.99;       // double value stored DIRECTLY in stack frame
        boolean inStock = true;    // boolean stored DIRECTLY in stack frame

        // When we call calculateTotal(), a new stack frame is pushed
        double total = calculateTotal(quantity, price);

        // The calculateTotal() frame is popped; we're back in this frame
        System.out.println("Total: $" + total);

        // Recursive calls build up stack frames (one per call)
        System.out.println("Factorial(5) = " + stackFactorial(5));
        // Call sequence: main → f(5) → f(4) → f(3) → f(2) → f(1) → f(0)
        // Each → pushes a new frame; each return pops it

        System.out.println("\nKey: 'quantity', 'price', 'inStock' live on THIS frame.");
        System.out.println("Once demonstrateStackMemory() returns, all those variables vanish.\n");
    }

    static double calculateTotal(int qty, double unitPrice) {
        // This is a new stack frame; qty and unitPrice are COPIES (pass by value)
        double subtotal = qty * unitPrice;      // subtotal on this frame
        double tax      = subtotal * 0.08;      // tax on this frame
        return subtotal + tax;                  // frame is popped after return
    }

    static long stackFactorial(int n) {
        if (n <= 0) return 1;
        return n * stackFactorial(n - 1);   // each call adds a stack frame
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 2 — Heap Memory
    // Objects live on the heap. The stack holds only a REFERENCE (pointer).
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateHeapMemory() {
        System.out.println("=== Heap Memory ===");

        // 'order' is a REFERENCE on the stack; the Order OBJECT is on the HEAP
        Order order = new Order("ORD-001", 3, 49.99);

        System.out.println("Reference 'order' is on the STACK");
        System.out.println("Order object {" + order + "} is on the HEAP");

        // Arrays are also objects — allocated on the heap
        int[] quantities = new int[5];         // array object on heap
        String[] names   = new String[3];      // array of references on heap

        // Object references can be passed around — all point to the SAME heap object
        Order sameOrder = order;                // sameOrder is a second reference to the SAME object
        sameOrder.quantity = 10;               // modifies the heap object
        System.out.println("After sameOrder.quantity = 10: order.quantity = " + order.quantity);
        System.out.println("(order and sameOrder point to the same heap object)\n");
    }

    static class Order {
        String orderId;
        int quantity;
        double price;

        Order(String orderId, int quantity, double price) {
            this.orderId  = orderId;
            this.quantity = quantity;
            this.price    = price;
        }

        @Override public String toString() {
            return "id=" + orderId + ", qty=" + quantity + ", price=$" + price;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 3 — Pass by Value (Java is ALWAYS pass-by-value)
    // Primitives: the VALUE is copied
    // Objects: the REFERENCE is copied (both variables point to same heap object)
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateReferenceVsValue() {
        System.out.println("=== Pass-by-Value vs Pass-by-Reference ===");

        // Primitive — pass by value: the method gets a COPY
        int stock = 100;
        tryToChangeInt(stock);
        System.out.println("stock after tryToChangeInt: " + stock);  // still 100

        // Object reference — pass by value: the REFERENCE is copied
        // but both the original and the copy point to the SAME heap object
        Order myOrder = new Order("ORD-XYZ", 5, 19.99);
        tryToChangeOrder(myOrder);
        System.out.println("myOrder.quantity after tryToChangeOrder: " + myOrder.quantity);  // changed to 99

        // But reassigning the parameter inside the method doesn't affect the original
        tryToReplaceOrder(myOrder);
        System.out.println("myOrder after tryToReplaceOrder: " + myOrder.orderId);  // still ORD-XYZ
        System.out.println();
    }

    static void tryToChangeInt(int value) {
        value = 999;   // only affects the local copy on the stack
    }

    static void tryToChangeOrder(Order order) {
        order.quantity = 99;   // modifies the actual heap object — visible to caller
    }

    static void tryToReplaceOrder(Order order) {
        order = new Order("NEW-ORDER", 1, 0.0);  // only replaces the LOCAL reference copy
        // The caller's 'myOrder' reference still points to the original heap object
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 4 — String Pool (PermGen/Metaspace optimisation)
    // String literals are interned — stored once in the String Pool on the Heap.
    // new String("...") always creates a new heap object outside the pool.
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateStringPool() {
        System.out.println("=== String Pool ===");

        // Literals — both point to the SAME object in the String Pool
        String s1 = "hello";
        String s2 = "hello";
        System.out.println("s1 == s2 (literals): " + (s1 == s2));   // true — same pool object

        // new String() — always a new object on the heap, OUTSIDE the pool
        String s3 = new String("hello");
        System.out.println("s1 == s3 (new):      " + (s1 == s3));   // false — different objects
        System.out.println("s1.equals(s3):       " + s1.equals(s3)); // true  — same content

        // intern() — look up / add to the pool
        String s4 = s3.intern();
        System.out.println("s1 == s4 (interned): " + (s1 == s4));   // true — back to pool

        System.out.println("\n⚠️  Always compare Strings with .equals(), never with ==\n");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 5 — Method Area / Metaspace and Static Variables
    // Class metadata, bytecode, and static fields are stored in Metaspace
    // (was PermGen before Java 8) — a separate region from the heap.
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateMethodAreaAndStatic() {
        System.out.println("=== Method Area / Static Variables ===");

        // Static field: shared across ALL instances, lives in Metaspace
        System.out.println("Before: applicationInstanceCount = " + applicationInstanceCount);

        // Creating instances — each constructor call increments the shared static
        Counter c1 = new Counter("counter-1");
        Counter c2 = new Counter("counter-2");
        Counter c3 = new Counter("counter-3");

        System.out.println("After creating 3 counters: totalCreated = " + Counter.totalCreated);
        System.out.println("c1.name=" + c1.name + ", c2.name=" + c2.name);
        System.out.println("Each Counter instance lives on the HEAP; Counter.totalCreated lives in Metaspace.\n");
    }

    static class Counter {
        static int totalCreated = 0;  // shared across all instances — Metaspace
        String name;                  // per-instance — Heap

        Counter(String name) {
            this.name = name;
            totalCreated++;           // increments the class-level (static) counter
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 6 — Memory Errors
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateMemoryErrors() {
        System.out.println("=== Common Memory Errors ===");

        // StackOverflowError — infinite recursion exhausts the thread's stack
        System.out.println("Triggering StackOverflowError:");
        try {
            infiniteRecurse(0);
        } catch (StackOverflowError e) {
            System.out.println("  Caught StackOverflowError after deep recursion");
        }

        // NullPointerException — dereferencing a null reference
        System.out.println("\nNullPointerException:");
        try {
            Order nullOrder = null;
            System.out.println(nullOrder.orderId);   // NPE — no object on heap
        } catch (NullPointerException e) {
            System.out.println("  Caught NPE — reference pointed to null, no object there");
        }

        // OutOfMemoryError — can't be safely triggered in a demo without killing the JVM
        // Hypothetical:
        // List<byte[]> leaking = new ArrayList<>();
        // while(true) leaking.add(new byte[1024 * 1024]);  // allocates 1MB per loop → OOM
        System.out.println("\nOutOfMemoryError: triggered by uncapped heap allocation");
        System.out.println("  Fix: use streams, weak references, or process data in chunks\n");

        System.out.println("Memory allocation summary:");
        System.out.println("  Stack:    primitives, references, method frames — per thread");
        System.out.println("  Heap:     all objects, arrays — shared across threads");
        System.out.println("  Metaspace: class definitions, static fields, bytecode");
    }

    static void infiniteRecurse(int depth) {
        infiniteRecurse(depth + 1);  // never reaches base case → blows the stack
    }
}
