import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

public class MemoryAndGCDemo {

    public static void main(String[] args) throws InterruptedException {

        // ── Part 1: Stack vs Heap ──────────────────────────────────────────────
        System.out.println("=== Part 1: Stack vs Heap ===");

        // TODO: declare a local int x = 42 and print:
        //   "Local int x = 42  →  lives on the STACK (primitive, no heap allocation)"

        // TODO: declare int[] arr = new int[]{1, 2, 3} and print:
        //   "int[] arr = new int[]{1,2,3}  →  reference on stack, array object on HEAP"


        // ── Part 2: Heap Memory & GC ──────────────────────────────────────────
        System.out.println("\n=== Part 2: Heap Memory & GC ===");

        Runtime rt = Runtime.getRuntime();

        // TODO: print total heap in MB using rt.totalMemory()
        // TODO: print free memory before allocation

        // TODO: allocate byte[] big = new byte[10 * 1024 * 1024]
        // TODO: print free memory after allocation

        // TODO: set big = null, call System.gc(), Thread.sleep(100)
        // TODO: print free memory after GC (should partially recover)


        // ── Part 3: WeakReference ─────────────────────────────────────────────
        System.out.println("\n=== Part 3: WeakReference ===");

        String strong = new String("weakly held");
        WeakReference<String> weak = new WeakReference<>(strong);

        // TODO: print weak.get() — should be non-null

        strong = null;
        System.gc();
        Thread.sleep(100);

        // TODO: print weak.get() — may be null after GC


        // ── Part 4: SoftReference ─────────────────────────────────────────────
        System.out.println("\n=== Part 4: SoftReference ===");

        SoftReference<byte[]> soft = new SoftReference<>(new byte[1024]);

        // TODO: print whether soft.get() != null (should be true)
        // TODO: print a note: "SoftReferences are cleared only when the JVM is low on memory — ideal for caches"


        // ── Part 5: PhantomReference ──────────────────────────────────────────
        System.out.println("\n=== Part 5: PhantomReference ===");

        // No live demo needed — PhantomReference.get() always returns null
        // TODO: print two lines:
        //   "PhantomReference.get() always returns null."
        //   "Used with ReferenceQueue for post-finalization cleanup (replaces finalize())."
    }
}
