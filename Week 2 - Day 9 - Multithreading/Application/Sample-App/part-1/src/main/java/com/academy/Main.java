package com.academy;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * Day 9 Part 1 — Thread Basics, Runnable, Synchronization, Deadlock
 *
 * Theme: Concert Ticket Booking System
 * Run: mvn compile exec:java
 */
public class Main {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║  Day 9 Part 1 — Threads & Synchronization Demo          ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");

        demoThreadCreation();
        demoRaceCondition();
        demoSynchronization();
        demoThreadLifecycle();
    }

    // ─────────────────────────────────────────────────────────
    // 1. Creating Threads (Thread class vs Runnable)
    // ─────────────────────────────────────────────────────────
    static void demoThreadCreation() throws InterruptedException {
        System.out.println("=== 1. Creating Threads ===");

        // Method 1: Extend Thread
        Thread t1 = new Thread("Thread-Extend") {
            @Override public void run() {
                System.out.println("  [" + getName() + "] Booking ticket for Alice");
            }
        };

        // Method 2: Implement Runnable (preferred)
        Runnable r = () -> System.out.println("  [" + Thread.currentThread().getName() + "] Booking ticket for Bob");
        Thread t2 = new Thread(r, "Thread-Runnable");

        // Method 3: Lambda Runnable
        Thread t3 = new Thread(() -> System.out.println("  [" + Thread.currentThread().getName() + "] Booking ticket for Carol"),
                               "Thread-Lambda");

        t1.start(); t2.start(); t3.start();
        t1.join(); t2.join(); t3.join(); // wait for all to finish

        System.out.println("  Thread.currentThread().getName(): " + Thread.currentThread().getName());
        System.out.println("  Thread states: NEW → RUNNABLE → (BLOCKED/WAITING) → TERMINATED");
        System.out.println();
    }

    // ─────────────────────────────────────────────────────────
    // 2. Race Condition — not synchronized
    // ─────────────────────────────────────────────────────────
    static void demoRaceCondition() throws InterruptedException {
        System.out.println("=== 2. Race Condition (unsynchronized) ===");

        UnsafeTicketCounter unsafe = new UnsafeTicketCounter(100);
        Thread[] buyers = new Thread[10];
        for (int i = 0; i < 10; i++) {
            final int buyerId = i + 1;
            buyers[i] = new Thread(() -> {
                for (int j = 0; j < 10; j++) unsafe.buy();
            }, "Buyer-" + buyerId);
        }
        for (Thread t : buyers) t.start();
        for (Thread t : buyers) t.join();

        System.out.println("  Expected tickets sold: 100");
        System.out.println("  Actual tickets sold:   " + unsafe.getSold() + "  ← may differ due to race condition!");
        System.out.println();
    }

    // ─────────────────────────────────────────────────────────
    // 3. Synchronized — fixing the race condition
    // ─────────────────────────────────────────────────────────
    static void demoSynchronization() throws InterruptedException {
        System.out.println("=== 3. Synchronized Counter (thread-safe) ===");

        SafeTicketCounter safe = new SafeTicketCounter(100);
        Thread[] buyers = new Thread[10];
        for (int i = 0; i < 10; i++) {
            buyers[i] = new Thread(() -> {
                for (int j = 0; j < 10; j++) safe.buy();
            });
        }
        for (Thread t : buyers) t.start();
        for (Thread t : buyers) t.join();

        System.out.println("  Expected tickets sold: 100");
        System.out.println("  Actual tickets sold:   " + safe.getSold() + "  ← always correct with synchronized");

        // AtomicInteger — lock-free thread safety
        AtomicInteger atomicCounter = new AtomicInteger(0);
        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) threads[i] = new Thread(() -> {
            for (int j = 0; j < 10; j++) atomicCounter.incrementAndGet();
        });
        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();
        System.out.println("  AtomicInteger result:  " + atomicCounter.get() + "  ← also always correct");
        System.out.println();
    }

    // ─────────────────────────────────────────────────────────
    // 4. Thread States
    // ─────────────────────────────────────────────────────────
    static void demoThreadLifecycle() throws InterruptedException {
        System.out.println("=== 4. Thread Lifecycle & States ===");

        Thread t = new Thread(() -> {
            try { Thread.sleep(200); }
            catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }, "lifecycle-thread");

        System.out.println("  Before start:   " + t.getState()); // NEW
        t.start();
        System.out.println("  After start:    " + t.getState()); // RUNNABLE or TIMED_WAITING
        Thread.sleep(50);
        System.out.println("  During sleep:   " + t.getState()); // TIMED_WAITING
        t.join();
        System.out.println("  After join:     " + t.getState()); // TERMINATED

        System.out.println("\n  States: NEW → RUNNABLE → BLOCKED/WAITING/TIMED_WAITING → TERMINATED");
        System.out.println("\n✓ Threads & Synchronization demo complete.");
    }
}

class UnsafeTicketCounter {
    private int sold = 0;
    private final int capacity;
    UnsafeTicketCounter(int cap) { this.capacity = cap; }
    void buy() { if (sold < capacity) sold++; }          // NOT thread-safe!
    int getSold() { return sold; }
}

class SafeTicketCounter {
    private int sold = 0;
    private final int capacity;
    SafeTicketCounter(int cap) { this.capacity = cap; }
    synchronized void buy() { if (sold < capacity) sold++; }  // synchronized = thread-safe
    int getSold() { return sold; }
}
