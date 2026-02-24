import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

public class MemoryAndGCDemo {

    public static void main(String[] args) throws InterruptedException {

        // ── Part 1: Stack vs Heap ──────────────────────────────────────────────
        System.out.println("=== Part 1: Stack vs Heap ===");

        int x = 42;
        System.out.println("Local int x = " + x + "  →  lives on the STACK (primitive, no heap allocation)");

        int[] arr = new int[]{1, 2, 3};
        System.out.println("int[] arr = new int[]{1,2,3}  →  reference on stack, array object on HEAP");

        // ── Part 2: Heap Memory & GC ──────────────────────────────────────────
        System.out.println("\n=== Part 2: Heap Memory & GC ===");

        Runtime rt = Runtime.getRuntime();
        System.out.println("Total heap : ~" + rt.totalMemory() / 1024 / 1024 + " MB");
        System.out.println("Free before: ~" + rt.freeMemory() / 1024 / 1024 + " MB");

        byte[] big = new byte[10 * 1024 * 1024];   // 10 MB allocation
        System.out.println("Free after 10MB alloc: ~" + rt.freeMemory() / 1024 / 1024 + " MB");

        big = null;             // drop strong reference → eligible for GC
        System.gc();            // hint (not guaranteed)
        Thread.sleep(100);      // give GC time to run
        System.out.println("Freed! Free after gc: ~" + rt.freeMemory() / 1024 / 1024 + " MB");

        // ── Part 3: WeakReference ─────────────────────────────────────────────
        System.out.println("\n=== Part 3: WeakReference ===");

        String strong = new String("weakly held");
        WeakReference<String> weak = new WeakReference<>(strong);

        System.out.println("Before null: " + weak.get());   // non-null

        strong = null;          // remove strong reference
        System.gc();
        Thread.sleep(100);

        System.out.println("After gc:    " + weak.get());   // likely null

        // ── Part 4: SoftReference ─────────────────────────────────────────────
        System.out.println("\n=== Part 4: SoftReference ===");

        SoftReference<byte[]> soft = new SoftReference<>(new byte[1024]);
        System.out.println("soft.get() non-null: " + (soft.get() != null));
        System.out.println("SoftReferences are cleared only when the JVM is low on memory — ideal for caches");

        // ── Part 5: PhantomReference ──────────────────────────────────────────
        System.out.println("\n=== Part 5: PhantomReference ===");
        // PhantomReference.get() always returns null — it is used with a ReferenceQueue
        // to perform clean-up actions *after* the object is finalized but before its
        // memory is reclaimed. This is the modern replacement for finalize().
        System.out.println("PhantomReference.get() always returns null.");
        System.out.println("Used with ReferenceQueue for post-finalization cleanup (replaces finalize()).");
    }
}
