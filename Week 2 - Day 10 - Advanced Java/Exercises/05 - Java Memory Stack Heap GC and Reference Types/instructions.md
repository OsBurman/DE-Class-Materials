# Exercise 05: Java Memory Model — Stack, Heap, GC & Reference Types

## Objective
Understand how the JVM manages memory (stack vs. heap), how the Garbage Collector (GC) works, and the difference between strong, weak, soft, and phantom references.

## Background

### Stack vs. Heap
| Memory area | Stores | Lifetime |
|---|---|---|
| **Stack** | Local primitives, local reference variables | Until method returns |
| **Heap** | All objects (`new ...`) | Until GC collects |

Each thread has its own stack. The heap is shared across all threads, which is why multithreading needs synchronisation.

### Garbage Collection
The JVM automatically reclaims heap memory for objects that are **no longer reachable**. The default GC (G1GC in modern JVMs) uses a **generational** model:
- **Young generation** (Eden + Survivor spaces) — newly allocated objects. Minor GC runs frequently and is fast.
- **Old generation** (Tenured) — objects that survive several minor GCs. Major/Full GC is slower.
- **Metaspace** — class metadata (not heap).

### Reference Types (`java.lang.ref`)
| Type | Collected when | Typical use |
|---|---|---|
| **Strong** (default) | Never while reachable | Normal variables |
| **Soft** `SoftReference<T>` | JVM is low on memory | Caches |
| **Weak** `WeakReference<T>` | Next GC cycle after no strong refs | Canonical maps, listeners |
| **Phantom** `PhantomReference<T>` | After finalization, before memory reclaimed | Off-heap cleanup |

## Requirements

1. **Part 1 — Stack vs. Heap demo:**
   - Print a brief explanation of the difference
   - Show a local `int x` (stack) and a `new int[]{1,2,3}` stored in a local reference (stack reference → heap array)

2. **Part 2 — Heap memory stats:**
   - Use `Runtime rt = Runtime.getRuntime()`
   - Print `rt.totalMemory()` and `rt.freeMemory()` before allocation
   - Allocate `byte[] big = new byte[10 * 1024 * 1024]` (10 MB)
   - Print free memory after allocation
   - Set `big = null`, call `System.gc()`, sleep 100 ms, print free memory again
   - Observe (approximately) that free memory recovers

3. **Part 3 — WeakReference:**
   - Create `String strong = new String("weakly held")`
   - Wrap it: `WeakReference<String> weak = new WeakReference<>(strong)`
   - Print `weak.get()` — should be non-null
   - Set `strong = null` and call `System.gc()`, sleep 100 ms
   - Print `weak.get()` again — likely null (GC may have collected it)

4. **Part 4 — SoftReference:**
   - Create `SoftReference<byte[]> soft = new SoftReference<>(new byte[1024])`
   - Print whether `soft.get()` is non-null immediately
   - Print a note explaining when soft refs are cleared (only under memory pressure — useful for caches)

5. **Part 5 — Phantom reference (description only):**
   - Print a brief explanation of `PhantomReference`: requires a `ReferenceQueue`; `get()` always returns null; used to perform cleanup *after* an object is finalized but *before* its memory is freed — the modern alternative to `finalize()`

## Hints
- `System.gc()` is a *hint* — the JVM may or may not GC immediately; results of weak-ref nullification can vary
- Use `Thread.sleep(100)` to give GC time to run (wrap in try/catch)
- Format memory in MB: `rt.freeMemory() / 1024 / 1024 + " MB"`

## Expected Output (approximate — memory values will vary)

```
=== Part 1: Stack vs Heap ===
Local int x = 42  →  lives on the STACK (primitive, no heap allocation)
int[] arr = new int[]{1,2,3}  →  reference on stack, array object on HEAP

=== Part 2: Heap Memory & GC ===
Total heap : ~256 MB
Free before: ~240 MB
Free after 10MB alloc: ~229 MB
Freed! Free after gc: ~239 MB

=== Part 3: WeakReference ===
Before null: weakly held
After gc:    null

=== Part 4: SoftReference ===
soft.get() non-null: true
SoftReferences are cleared only when the JVM is low on memory — ideal for caches

=== Part 5: PhantomReference ===
PhantomReference.get() always returns null.
Used with ReferenceQueue for post-finalization cleanup (replaces finalize()).
```
