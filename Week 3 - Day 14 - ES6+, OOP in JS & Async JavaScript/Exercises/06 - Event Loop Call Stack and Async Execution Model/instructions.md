# Exercise 06: Event Loop, Call Stack, and Async Execution Model

## Objective
Observe how JavaScript's **single-threaded event loop** determines execution order across synchronous code, microtasks (Promise callbacks), and macrotasks (setTimeout callbacks).

---

## Background
JavaScript uses a **call stack** to execute code and an **event loop** that processes:
- **Macrotask queue** — `setTimeout`, `setInterval`, I/O callbacks
- **Microtask queue** — Promise `.then`/`.catch`/`.finally`, `queueMicrotask`

**Execution priority:**
1. Current synchronous code (call stack)
2. All microtasks (until microtask queue is empty)
3. Next macrotask
4. Repeat

---

## Requirements

### Part A — Call Stack & Synchronous Execution

**Requirement 1:**  
Define three functions: `first()`, `second()`, `third()`.  
- `first()` logs `"first"`, then calls `second()`
- `second()` logs `"second"`, then calls `third()`
- `third()` logs `"third"`

Call `first()`. Observe the call stack unwinding.

Expected output (in order):
```
first
second
third
```

---

### Part B — Synchronous vs Asynchronous Ordering

**Requirement 2:**  
Log the following in this exact sequence of *declarations*:
1. `console.log("Start")`
2. `setTimeout(() => console.log("setTimeout 0ms"), 0)`
3. `Promise.resolve().then(() => console.log("Promise microtask"))`
4. `console.log("End")`

Before running, **predict** the output order (add a comment with your prediction). Then run and confirm.

Expected output:
```
Start
End
Promise microtask
setTimeout 0ms
```

---

### Part C — Multiple Timers & Microtasks Interleaved

**Requirement 3:**  
Write code that schedules:
- `setTimeout A` at 0 ms
- `setTimeout B` at 0 ms
- Two Promise `.then` microtasks queued synchronously

Log labels `"sync"`, `"microtask 1"`, `"microtask 2"`, `"macrotask A"`, `"macrotask B"`.

Expected output:
```
sync
microtask 1
microtask 2
macrotask A
macrotask B
```

---

### Part D — Nested Microtasks

**Requirement 4:**  
Create a Promise chain where the first `.then` queues another `.then`:
```
Promise.resolve()
  .then(() => { log "then 1"; return Promise.resolve(); })
  .then(() => log "then 2")
```
Also schedule `setTimeout("timer", 0)` before the chain.

Expected output:
```
then 1
then 2
timer
```

---

## Running Your Code
```
node script.js
```

Each Part is separated; you will see all four outputs in sequence.
