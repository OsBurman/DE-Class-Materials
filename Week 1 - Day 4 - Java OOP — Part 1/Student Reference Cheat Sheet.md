# Day 4 Review — OOP Part 1
## Quick Reference Guide

---

## 1. OOP Core Concepts

**Classes and Objects:**
- **Class** — a blueprint/template that defines fields and methods
- **Object** — a specific instance of a class created at runtime

```java
// Class: the template (written in source code)
public class Car {
    String color;
    int speed;
}

// Object: a concrete instance (created at runtime)
Car myCar = new Car();      // one object
Car herCar = new Car();     // another object — separate copy of all fields
```

**The 4 pillars of OOP (full coverage in Day 4–5):**
| Pillar | Meaning |
|---|---|
| **Encapsulation** | Bundle data + behavior; control access via private fields + public methods |
| **Abstraction** | Hide complexity; expose only what callers need |
| **Inheritance** | Child class extends parent class, inheriting fields and methods |
| **Polymorphism** | One interface, many implementations |

---

## 2. Class Structure

```java
public class Person {

    // Fields (instance variables) — state of the object
    private String name;
    private int age;

    // Constructor — initializes fields when object is created
    public Person(String name, int age) {
        this.name = name;    // this.name = field; name = parameter
        this.age = age;
    }

    // No-arg constructor
    public Person() {
        this("Unknown", 0);  // delegate to parameterized constructor
    }

    // Instance methods — behavior of the object
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void celebrateBirthday() {
        age++;
    }

    // toString — called automatically when printing the object
    @Override
    public String toString() {
        return "Person{name='" + name + "', age=" + age + "}";
    }
}
```

**Creating and using objects:**
```java
Person john = new Person("John", 30);
Person blank = new Person();           // uses no-arg constructor

john.celebrateBirthday();
System.out.println(john.getName());    // "John"
System.out.println(john);             // calls toString() automatically
```

---

## 3. Constructors

| Concept | Description |
|---|---|
| **Default constructor** | Zero-arg, auto-provided if NO constructor is written |
| **No-arg constructor** | You write it explicitly — overrides the default |
| **Parameterized constructor** | Takes arguments to initialize fields |
| **Constructor overloading** | Multiple constructors with different parameter lists |
| **Constructor chaining** | `this(...)` calls another constructor in the same class |

```java
public class Rectangle {
    private double width;
    private double height;

    public Rectangle() {
        this(1.0, 1.0);              // calls the 2-param constructor
    }

    public Rectangle(double side) {
        this(side, side);            // square
    }

    public Rectangle(double width, double height) {
        this.width = width;
        this.height = height;
    }
}
```

**Key rules:**
- Constructors have no return type (not even `void`)
- Name must exactly match the class name
- If you define ANY constructor, the default is removed
- `this(...)` must be the **first statement** in a constructor

---

## 4. Access Modifiers

| Modifier | Accessible From |
|---|---|
| `public` | Anywhere |
| `private` | Same class only |
| `protected` | Same class + subclasses + same package |
| *(none — package-private)* | Same package only |

**Standard pattern:** Fields `private`, methods `public`

```java
public class BankAccount {
    private double balance;     // nobody outside can touch this directly

    public void deposit(double amount) {
        if (amount > 0) balance += amount;   // validation in public method
    }

    public double getBalance() {
        return balance;
    }
}

BankAccount acct = new BankAccount();
acct.balance = -999;      // COMPILE ERROR — private
acct.deposit(100.0);      // OK — public method
```

---

## 5. Encapsulation — Getters and Setters

```java
private String email;

// Getter: read the field
public String getEmail() {
    return email;
}

// Setter: write the field with validation
public void setEmail(String email) {
    if (email != null && email.contains("@")) {
        this.email = email;
    }
    // silently reject invalid email — or throw exception
}
```

**Why encapsulate:**
- Prevent invalid state (`age = -50`, `balance = null`)
- Change internal implementation without breaking callers
- Add logging, validation, or side effects in one place

---

## 6. The `this` Keyword

```java
// 1. Disambiguate field from parameter with same name:
public void setName(String name) {
    this.name = name;   // this.name = field; name = parameter
}

// 2. Pass the current object as an argument:
public void register(Registry r) {
    r.add(this);        // passes this object to the registry
}

// 3. Call another constructor (must be first line):
public Person() {
    this("Unknown", 0);
}
```

---

## 7. Static Members

**Static fields and methods belong to the CLASS, not to any instance:**

```java
public class Counter {
    private static int count = 0;   // shared across ALL Counter objects
    private int id;

    public Counter() {
        count++;               // incremented each time a Counter is created
        this.id = count;       // this instance gets the current count as its ID
    }

    public static int getCount() {   // static — no object needed to call
        return count;
    }

    public int getId() {             // instance — needs an object
        return id;
    }
}

Counter a = new Counter();
Counter b = new Counter();
Counter.getCount();   // 2 — called on the class, not an instance
a.getId();            // 1
b.getId();            // 2
```

**Static method rules:**
- Cannot use `this`
- Cannot access instance fields directly
- Can only call other static methods directly
- Called via `ClassName.methodName()`, not on an object

**Common static use cases:**
```java
// Utility/factory methods:
Math.sqrt(16.0)
Integer.parseInt("42")
Arrays.sort(arr)

// Constants:
public static final double TAX_RATE = 0.08;
public static final int MAX_SIZE = 100;
```

---

## 8. Method Overloading

Same method name, different parameter lists (different type, count, or order):

```java
public class Calculator {
    public int add(int a, int b) { return a + b; }
    public double add(double a, double b) { return a + b; }
    public int add(int a, int b, int c) { return a + b + c; }
    // NOT overloading: different return type only → compile error
}
```

Java picks the correct version at compile time based on the arguments you pass.

---

## 9. Object Memory Model

```
Stack                   Heap
─────────────────       ──────────────────────────
john  → ──────────────→ Person object
                          name: "John"
                          age: 30

jane  → ──────────────→ Person object
                          name: "Jane"
                          age: 25
```

- **Primitive variables** store their value directly on the stack
- **Object variables** store a reference (pointer) to the object on the heap
- Assigning `Person copy = john` copies the **reference**, not the object — both point to the same Person

```java
Person a = new Person("Alice", 25);
Person b = a;           // b and a point to the SAME object
b.setName("Bob");
System.out.println(a.getName());   // "Bob" — a was affected!
```

---

## 10. toString, equals, and hashCode

```java
// toString — called when you print the object
@Override
public String toString() {
    return "Person{name='" + name + "', age=" + age + "}";
}

// equals — compare by content, not reference
@Override
public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!(obj instanceof Person)) return false;
    Person other = (Person) obj;
    return age == other.age && Objects.equals(name, other.name);
}

// hashCode — must be overridden alongside equals
@Override
public int hashCode() {
    return Objects.hash(name, age);
}
```

**Rule:** If you override `equals`, always override `hashCode` too.

---

## 11. Class Design Checklist

- [ ] Fields are `private`
- [ ] Public getters and setters with validation where needed
- [ ] At least one constructor that sets all required fields
- [ ] `@Override toString()` for readable output
- [ ] `@Override equals()` and `hashCode()` if objects will be compared or stored in collections
- [ ] Static fields/methods for data shared across all instances
- [ ] `final` fields for immutable values
