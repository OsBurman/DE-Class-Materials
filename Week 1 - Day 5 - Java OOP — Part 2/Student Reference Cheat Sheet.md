# Day 5 — OOP Part 2: Inheritance, Polymorphism & Interfaces
## Quick Reference Guide

---

## 1. Inheritance

Inheritance lets a subclass **reuse** fields and methods from its superclass.

```java
// Superclass
public class Animal {
    protected String name;

    public Animal(String name) {
        this.name = name;
    }

    public void speak() {
        System.out.println(name + " makes a sound");
    }
}

// Subclass
public class Dog extends Animal {
    private String breed;

    public Dog(String name, String breed) {
        super(name);              // ← must be first statement in constructor
        this.breed = breed;
    }

    @Override
    public void speak() {
        System.out.println(name + " barks");   // name inherited from Animal
    }

    public String getBreed() { return breed; }
}
```

- `extends` — creates an IS-A relationship; Java supports **single inheritance** only
- `super()` — calls the parent constructor; compiler inserts `super()` implicitly if omitted
- `super.method()` — explicitly calls the parent's version of an overridden method

---

## 2. Method Overriding

Redefine an inherited method in the subclass to change its behavior.

| Rule | Detail |
|------|--------|
| `@Override` | Optional but **strongly recommended** — compiler catches typos |
| Same signature | Method name, parameter types, and order must match exactly |
| Return type | Same, or a **covariant** (subtype) return type |
| Access modifier | Can only **widen** (e.g., `protected` → `public`), never narrow |
| `static` methods | Cannot be overridden — they are **hidden**, not overridden |
| `final` methods | Cannot be overridden |
| `private` methods | Not inherited; cannot be overridden |

---

## 3. Overloading vs Overriding

| Aspect | Overloading | Overriding |
|--------|-------------|------------|
| Where | Same class | Subclass |
| Signature | **Different** parameters | **Same** signature |
| Return type | Can differ | Must match (or covariant) |
| Resolved at | **Compile time** (static dispatch) | **Runtime** (dynamic dispatch) |
| `@Override` | No | Yes (recommended) |

---

## 4. Polymorphism

**Compile-time polymorphism** = method overloading  
**Runtime polymorphism** = method overriding via supertype reference

```java
Animal a = new Dog("Rex", "Husky");   // upcasting — implicit, always safe
a.speak();                             // → "Rex barks"  (Dog's version at runtime)

// Downcasting — explicit cast; throws ClassCastException if wrong type
if (a instanceof Dog d) {             // Java 16+ pattern matching
    System.out.println(d.getBreed());
}

// Pre-Java 16 style
if (a instanceof Dog) {
    Dog d = (Dog) a;
    System.out.println(d.getBreed());
}
```

**Upcasting** — subtype → supertype variable; implicit, always safe.  
**Downcasting** — supertype → subtype variable; explicit cast; always guard with `instanceof`.

---

## 5. Abstract Classes

An abstract class **cannot be instantiated** directly; it exists to be subclassed.

```java
public abstract class Shape {
    protected String color;

    public Shape(String color) { this.color = color; }

    // Abstract method — no body; every concrete subclass MUST override
    public abstract double area();

    // Concrete method — inherited as-is
    public void describe() {
        System.out.println(color + " shape, area = " + area());
    }
}

public class Circle extends Shape {
    private double radius;

    public Circle(String color, double radius) {
        super(color);
        this.radius = radius;
    }

    @Override
    public double area() { return Math.PI * radius * radius; }
}
```

**Rules:**
- A class with any `abstract` method must be declared `abstract`
- An abstract class can have constructors, fields, concrete methods, and abstract methods
- A concrete subclass must implement **all** abstract methods (or itself be `abstract`)

---

## 6. Interfaces

An interface is a **pure contract** — methods are implicitly `public abstract` unless marked `default` or `static`.

```java
public interface Flyable {
    // Constant — implicitly public static final
    double MAX_ALTITUDE = 10_000.0;

    // Abstract method — implicitly public abstract
    void fly();

    // Default method (Java 8+) — has a body; subclass may override
    default void land() {
        System.out.println("Landing...");
    }

    // Static method (Java 8+) — called on the interface, not an instance
    static boolean isSafe(double alt) { return alt < MAX_ALTITUDE; }

    // Private method (Java 9+) — internal helper shared by default methods
    private void log(String msg) { System.out.println("[Flyable] " + msg); }
}

// Implement one interface
public class Bird implements Flyable {
    @Override public void fly() { System.out.println("Flapping wings"); }
}

// Implement multiple interfaces — Java's answer to multiple inheritance
public class FlyingFish extends Fish implements Flyable, Swimmable {
    @Override public void fly()  { ... }
    @Override public void swim() { ... }
}
```

---

## 7. Abstract Class vs Interface

| Feature | Abstract Class | Interface |
|---------|---------------|-----------|
| Keyword | `extends` (one only) | `implements` (many) |
| Constructors | ✅ Yes | ❌ No |
| Instance fields | ✅ Yes | ❌ No (`public static final` constants only) |
| Concrete methods | ✅ Yes | ✅ `default` / `static` / `private` only |
| Abstract methods | ✅ Yes | ✅ Yes (all unmarked methods are abstract) |
| Access modifiers | Any | Methods implicitly `public` |
| **Use when** | Shared implementation + IS-A relationship | Pure contract / capability ("can-do") |

---

## 8. Packages & Imports

```java
// Declare package at top of file — must match folder structure
package com.example.animals;

// Import a specific class
import java.util.ArrayList;

// Import all public types in a package (wildcard — avoid in production)
import java.util.*;

// Import a static member (method or constant)
import static java.lang.Math.PI;
import static java.lang.Math.sqrt;
```

**Naming convention:** reverse domain name → `com.company.module.submodule`  
**Same-package access:** Classes in the same package can access each other without imports.

---

## 9. Access Modifiers + Inheritance

| Modifier | Same Class | Same Package | Subclass (any pkg) | Everywhere |
|----------|:----------:|:------------:|:------------------:|:----------:|
| `private` | ✅ | ❌ | ❌ | ❌ |
| *(package-private)* | ✅ | ✅ | ❌ | ❌ |
| `protected` | ✅ | ✅ | ✅ | ❌ |
| `public` | ✅ | ✅ | ✅ | ✅ |

> `protected` members are accessible in subclasses **even across packages**.

---

## 10. Object — The Universal Parent

All classes implicitly extend `java.lang.Object`. Override these as needed:

| Method | Purpose | Notes |
|--------|---------|-------|
| `toString()` | String representation | Called by `println`, string concatenation |
| `equals(Object o)` | Logical equality | Default: reference equality (`==`) |
| `hashCode()` | Hash bucket placement | **Must** override together with `equals` |
| `clone()` | Shallow copy | Implement `Cloneable`; rarely used |

**Contract:** If `a.equals(b)` then `a.hashCode() == b.hashCode()` must always hold.

---

## 11. Quick Patterns

```java
// Calling overridden parent method alongside child additions
@Override
public String toString() {
    return super.toString() + " | breed=" + breed;
}

// Checking type before cast (classic pattern)
for (Animal a : animals) {
    if (a instanceof Cat cat) {
        cat.purr();
    }
}

// Abstract factory skeleton
public abstract class Logger {
    public abstract void write(String msg);    // subclass fills this in
    public void log(String msg) {              // reusable template method
        write("[LOG] " + msg);
    }
}
```
