# Week 1 - Day 4 (Thursday) Part 2: Access Modifiers, Static Members, and this Keyword
## OOP Control: Visibility, Class-Level Members & Object Identity

---

## Slide 1: Welcome to Part 2: Taking Control
**Visual:** Reinforcement of OOP concepts, now adding layers of control

Welcome back! In Part 1, you learned the core of OOP: classes, objects, constructors, and methods. You can create classes and bring objects to life. Now in Part 2, we add sophistication. We learn how to control visibility—who can see what. We learn about static members—data and methods that belong to the class, not to objects. And we deepen our understanding of the `this` keyword. These tools transform you from someone who can write a class to someone who can design professional, maintainable code.

---

## Slide 2: The Problem: Uncontrolled Access
**Visual:** Show a Person object with unprotected fields; illustrate "bad code" modifying age directly

Imagine you write:

```java
public class Person {
    public String name;
    public int age;
}
```

All fields are public. Anyone can do this:

```java
Person p = new Person();
p.age = -50;  // Invalid!
```

You can't prevent it. The age is now impossible. Or someone could do `p.name = null;`. Now the name is invalid. Public fields offer no protection. Real code needs guards to prevent invalid states. That's where access modifiers come in.

---

## Slide 3: Access Modifiers Overview
**Visual:** Table showing public, private, protected, package-private with visibility ranges

Java offers four levels of visibility, controlled by access modifiers:

1. **public**: Accessible from anywhere in the program.
2. **private**: Accessible only from within the same class.
3. **protected**: Accessible from the same class and subclasses (we'll cover subclasses later).
4. **package-private** (no keyword): Accessible from within the same package. You'll rarely use this.

This is your toolkit for encapsulation. You decide who can see what. Most of the time, fields are private; methods are public. This protects fields while exposing a controlled interface.

---

## Slide 4: Private Modifier
**Visual:** Private fields surrounded by a shield; external code cannot access

`private` is the most restrictive. A private field or method is accessible only from within the same class. No external code can see it.

```java
public class Person {
    private String name;  // Only Person class can access
    private int age;      // Only Person class can access
    
    public String getName() {
        return name;      // Allowed; we're inside Person
    }
}
```

If you try to access a private field from outside:

```java
Person p = new Person();
System.out.println(p.name);  // Compilation error!
```

The compiler won't allow it. This is good. It forces external code to use the controlled interface (public methods) instead of directly accessing fields.

---

## Slide 5: Public Modifier
**Visual:** Public method accessible from anywhere; show arrows pointing from everywhere to the method

`public` means accessible from anywhere. A public method is the interface your class offers to the world.

```java
public class Person {
    private String name;
    
    public String getName() {  // Public method
        return name;
    }
    
    public void setName(String name) {  // Public method
        this.name = name;
    }
}
```

External code can call `getName()` and `setName()`. These public methods control access to the private `name` field. You can add validation in setters, logging, side effects—anything. The caller doesn't care; they just call the public method.

---

## Slide 6: Protected Modifier (Preview)
**Visual:** Protected access: class, subclasses, same package

`protected` is a middle ground. A protected member is accessible from within the same class, from subclasses, and from the same package. We'll cover subclasses later (Week 1 Day 5), so for now, know that `protected` is more open than `private` but more restrictive than `public`. It's useful when designing class hierarchies.

---

## Slide 7: Package-Private (Default Access)
**Visual:** Package-private scope limited to one package

If you don't specify a modifier, it's package-private (also called default access). A package-private member is accessible from anywhere in the same package but nowhere else. You'll rarely use this explicitly; it's the default when you omit a modifier. Generally, prefer `private` or `public` for clarity.

---

## Slide 8: Encapsulation: Principle and Practice
**Visual:** Diagram showing private fields protected by public methods

Encapsulation is the practice of bundling data and behavior together and hiding the internal details. It's one of the four pillars of OOP. In Java, you encapsulate by making fields private and methods public.

```java
public class BankAccount {
    private double balance;  // Hidden
    
    public void deposit(double amount) {  // Controlled access
        if (amount > 0) {
            balance += amount;
        }
    }
    
    public double getBalance() {
        return balance;
    }
}
```

External code can't modify balance directly. It can only deposit (through the public method), which validates the amount. This protection ensures the account is always in a valid state.

---

## Slide 9: Getters and Setters (Deep Dive)
**Visual:** Show getter and setter with optional validation in setter

Getters retrieve field values. Setters modify field values. By using getters and setters, you maintain a boundary between internal state and external access.

```java
public class Person {
    private String email;
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        if (email != null && email.contains("@")) {
            this.email = email;
        }
    }
}
```

The setter validates the email. Only valid emails are set. If someone tries `person.setEmail("invalid");`, it's ignored. The field remains unchanged. This protection is powerful.

---

## Slide 10: When to Use Private vs Public
**Visual:** Decision tree: is this an implementation detail? → private. Is this part of the interface? → public.

Guidelines:

- **Make fields private**. Fields are implementation details. Expose them through getters/setters.
- **Make methods public** if they're part of the interface—actions external code should perform.
- **Make methods private** if they're helpers only the class itself needs.

This separation of concerns is crucial for maintainability. If internal methods change, external code isn't affected as long as the public interface remains stable.

---

## Slide 11: Introduction to Static Members
**Visual:** Show contrast between instance members (one per object) and static members (one per class)

So far, everything we've discussed belongs to objects. Each object has its own fields and methods. But there's another category: static members. Static members belong to the class, not to any particular object. All objects share static members.

Think of it this way: instance members answer "What does this object know/do?" Static members answer "What does the class itself know/do?"

---

## Slide 12: Static Fields (Class Variables)
**Visual:** Show multiple Person objects sharing a single static field

A static field is shared by all objects of the class. There's only one copy, not one per object.

```java
public class Person {
    static int populationCount = 0;  // Shared by all Person objects
    private String name;             // Instance field; one per object
    
    public Person(String name) {
        this.name = name;
        populationCount++;
    }
}
```

Every time you create a Person, `populationCount` increments. It's shared across all Person objects. Create 1,000 Persons, and `populationCount` is 1,000. It's not 1,000 per object; it's one count for the entire class.

---

## Slide 13: Accessing Static Fields
**Visual:** Show accessing static field via class name, not instance

You access static fields via the class name:

```java
System.out.println(Person.populationCount);  // Via class
```

Not via an object:

```java
Person p = new Person("Alice");
System.out.println(p.populationCount);  // Works, but confusing
```

Though the second way technically works, it's bad practice. It suggests the field belongs to the object when it actually belongs to the class. Use the class name to access static members.

---

## Slide 14: Static Methods
**Visual:** Method signature with `static` keyword; show calling via class name

Static methods belong to the class, not to objects. They don't have access to instance fields (unless they're also static).

```java
public class Math {
    public static int add(int a, int b) {
        return a + b;
    }
}
```

You call it via the class name:

```java
int result = Math.add(5, 3);  // Via class, not object
```

You don't create a Math object to call `add()`. The method is a utility of the Math class itself. Static methods are often used for utility functions.

---

## Slide 15: Static vs Instance Methods
**Visual:** Comparison table showing key differences

**Instance methods:**
- Called on objects: `person.getName()`
- Have access to instance fields
- Each object has its own method (well, technically they share code, but conceptually each object can behave differently)

**Static methods:**
- Called on classes: `Math.abs(-5)`
- No access to instance fields
- Belong to the class; there's only one copy

Use instance methods for behavior that depends on the object's state. Use static methods for utility functions that don't depend on state.

---

## Slide 16: Static Methods Example
**Visual:** Calculator class with static methods

```java
public class Calculator {
    public static int multiply(int a, int b) {
        return a * b;
    }
    
    public static double divide(double a, double b) {
        if (b != 0) {
            return a / b;
        }
        return 0;
    }
}
```

These methods are utilities. They don't depend on Calculator state. You call them directly:

```java
int product = Calculator.multiply(5, 3);     // 15
double quotient = Calculator.divide(10, 2);  // 5.0
```

No object creation needed.

---

## Slide 17: Static Initialization Blocks
**Visual:** Code block with `static` keyword; explain it runs once when class loads

Static fields often need initialization. You can use a static initialization block:

```java
public class Configuration {
    static Map<String, String> config;
    
    static {
        config = new HashMap<>();
        config.put("host", "localhost");
        config.put("port", "8080");
    }
}
```

The static block runs once when the class is first loaded into memory. It's useful for complex initialization of static fields. You'll rarely use this, but it's good to know.

---

## Slide 18: Constants with Static Final
**Visual:** Show `static final` fields as unchangeable class-level values

Combine `static` and `final` to create constants:

```java
public class Constants {
    static final double PI = 3.14159;
    static final String APP_NAME = "MyApp";
}
```

These are class-level constants. By convention, constant names are in UPPERCASE. They're immutable—once set, they can't change. Use them for values that never change across the application.

Access them:

```java
System.out.println(Constants.PI);       // 3.14159
System.out.println(Constants.APP_NAME); // MyApp
```

---

## Slide 19: Non-Access Modifiers: final
**Visual:** `final` keyword preventing modification or overriding

`final` has different meanings depending on context:

- **On a field**: The field can't be changed after initialization.
- **On a method**: The method can't be overridden in subclasses (we'll cover this later).
- **On a class**: The class can't be subclassed (we'll cover subclassing later).

For now, focus on `final` fields:

```java
public class Person {
    private final String socialSecurityNumber;  // Can't be changed
    
    public Person(String ssn) {
        this.socialSecurityNumber = ssn;  // Set once in constructor
    }
}
```

Once the SSN is set in the constructor, it can't change. This immutability is useful for security-sensitive data.

---

## Slide 20: Non-Access Modifiers: abstract
**Visual:** Abstract class/method as a template; can't instantiate

`abstract` indicates something is incomplete. An abstract class is a template meant to be extended by subclasses, not instantiated directly. An abstract method is a method signature without implementation; subclasses must provide the implementation.

```java
abstract class Animal {
    abstract void makeSound();  // No implementation
}
```

You can't create an Animal object directly. Subclasses like Dog or Cat must implement `makeSound()`. We'll cover this in Day 5 when we discuss inheritance.

---

## Slide 21: This Keyword: Disambiguating Fields and Parameters
**Visual:** Show `this.field` vs `field` in constructor/method

We touched on `this` in Part 1. Let's deepen it. `this` refers to the current object. Use it to disambiguate between fields and parameters with the same name.

```java
public class Person {
    private String name;
    
    public Person(String name) {
        this.name = name;  // this.name is the field; name is the parameter
    }
}
```

Without `this`, `name = name;` assigns the parameter to itself—useless. `this.name = name;` assigns the parameter to the field—correct.

---

## Slide 22: This Keyword: Calling Constructors
**Visual:** Show constructor calling another constructor with `this(...)`

We covered this in Part 1, but reinforce: `this()` calls another constructor in the same class.

```java
public class Person {
    private String name;
    private int age;
    
    public Person() {
        this("Unknown", 0);  // Delegates to the two-arg constructor
    }
    
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
```

Use this pattern to avoid code duplication. The no-arg constructor delegates all initialization logic to the two-arg constructor.

---

## Slide 23: This Keyword: Calling Instance Methods
**Visual:** Show `this.method()` calling another method on current object

`this` can also be used to call instance methods:

```java
public class Person {
    public void celebrateBirthday() {
        this.incrementAge();  // Calling via this
    }
    
    private void incrementAge() {
        age++;
    }
}
```

Usually, you omit `this` when calling other methods:

```java
public void celebrateBirthday() {
    incrementAge();  // Implicit this
}
```

Both work. Using `this.incrementAge()` is explicit; omitting it is implicit. Consistency matters more than which you choose.

---

## Slide 24: This Keyword: Passing to Other Methods
**Visual:** Show passing `this` as argument to another method

You can pass `this` to other methods:

```java
public class Person {
    public void introduceTo(Person other) {
        other.greet(this);  // Passing this as argument
    }
    
    public void greet(Person other) {
        System.out.println("Hi, I'm " + other.name);
    }
}
```

When you pass `this`, you're passing a reference to the current object. The other method can then interact with you.

---

## Slide 25: Static Context and This
**Visual:** Show that static methods CAN'T use `this`

Important: static methods can't use `this`. Why? `this` refers to the current object. Static methods don't belong to any particular object; they belong to the class. There's no "current object" in a static context.

```java
public class Math {
    static int count = 0;
    
    public static void increment() {
        count++;              // Allowed; count is static
        System.out.println(this.count);  // ERROR! No 'this' in static context
    }
}
```

This is a common beginner mistake. Remember: `this` is only available in instance methods and constructors.

---

## Slide 26: Real-World Example: Student Class
**Visual:** Show Student class with mixed access levels, static fields, and methods

```java
public class Student {
    private static int nextId = 1000;  // Static; shared by all
    
    private int id;                    // Instance
    private String name;               // Instance
    private double gpa;                // Instance
    
    public Student(String name, double gpa) {
        this.id = nextId++;            // Auto-increment ID
        this.name = name;
        this.gpa = gpa;
    }
    
    public static int getTotalStudents() {
        return nextId - 1000;  // Count of students created
    }
    
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public double getGpa() {
        return gpa;
    }
}
```

This class demonstrates: static field (nextId), instance fields (id, name, gpa), constructor, static method (getTotalStudents), and instance methods (getters).

---

## Slide 27: Using the Student Class
**Visual:** Create multiple students, show IDs incrementing

```java
Student s1 = new Student("Alice", 3.9);
Student s2 = new Student("Bob", 3.5);
Student s3 = new Student("Charlie", 3.8);

System.out.println(s1.getId());        // 1000
System.out.println(s2.getId());        // 1001
System.out.println(s3.getId());        // 1002
System.out.println(Student.getTotalStudents());  // 3
```

Each student gets a unique ID because `nextId` increments. The static method `getTotalStudents()` returns the count.

---

## Slide 28: Immutability and Defensive Copies
**Visual:** Show immutable object with no setters

Some objects are designed to be immutable—they can't change after creation. String is a classic example:

```java
public class ImmutablePerson {
    private final String name;
    private final int age;
    
    public ImmutablePerson(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    public String getName() {
        return name;
    }
    
    public int getAge() {
        return age;
    }
    
    // No setters!
}
```

No setters mean the object can't be modified after creation. This is useful for thread-safe code and for representing fixed values.

---

## Slide 29: Avoiding Unintended Field Modification
**Visual:** Show private setter preventing external modification

Sometimes you want a field readable but not writable from outside:

```java
public class Person {
    private int age;
    
    public int getAge() {
        return age;
    }
    
    // No public setAge() method
}
```

Age is readable but can't be changed externally. Only internal code (like celebrateBirthday()) can modify it. This protects the invariant that age doesn't go backward.

---

## Slide 30: Transient and Volatile (Awareness)
**Visual:** Mention these modifiers exist; focus on serialization context for transient

Two modifiers worth knowing but not focusing on today:

- **transient**: Fields marked transient are excluded from serialization (saving objects to files). You'll use this when saving objects.
- **volatile**: Used in multithreading to ensure visibility. You'll learn this in Week 2.

For now, just know they exist.

---

## Slide 31: Method Overloading Reminder
**Visual:** Show multiple constructors/methods with same name, different parameters

We discussed overloading briefly; reinforce it: methods can have the same name as long as they have different parameter lists (different number or types of parameters).

```java
public class Person {
    public Person() { }
    public Person(String name) { }
    public Person(String name, int age) { }
    
    public void setAge(int age) { }
    public void setAge(double age) { }  // Overloaded; parameter type differs
}
```

The compiler determines which method to call based on the arguments. This is determined at compile time (not runtime) and is called static polymorphism. We'll cover runtime polymorphism (dynamic dispatch) later.

---

## Slide 32: Comparing Objects with equals()
**Visual:** Show `==` vs `.equals()`; explain reference equality vs value equality

Don't use `==` to compare objects. It checks if two variables reference the same object (reference equality):

```java
Person p1 = new Person("Alice", 25);
Person p2 = new Person("Alice", 25);

System.out.println(p1 == p2);  // false; different objects
```

Use `.equals()` for value equality:

```java
System.out.println(p1.equals(p2));  // true (if properly implemented)
```

By default, `.equals()` is the same as `==`. But classes can override it to compare by value. String overrides it to compare content:

```java
String s1 = "Hello";
String s2 = new String("Hello");

System.out.println(s1 == s2);           // false; different objects
System.out.println(s1.equals(s2));      // true; same content
```

---

## Slide 33: Implementing Equals (Overview)
**Visual:** Show overriding `equals()` method in a class

To implement `.equals()` properly:

```java
public class Person {
    private String name;
    private int age;
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Person)) return false;
        Person other = (Person) obj;
        return this.name.equals(other.name) && this.age == other.age;
    }
}
```

This is complex, and we'll cover it more thoroughly later. For now, know that it's a method you can override to customize equality. We'll deep dive in Day 2 of Week 2.

---

## Slide 34: ToString Method
**Visual:** Show `toString()` for custom string representation

Every object has a `toString()` method that returns a String representation. The default is not useful:

```java
Person p = new Person("Alice", 25);
System.out.println(p);  // Person@deadbeef (memory address)
```

Override it for something useful:

```java
@Override
public String toString() {
    return "Person{name='" + name + "', age=" + age + "}";
}
```

Now:

```java
System.out.println(p);  // Person{name='Alice', age=25}
```

Much better for debugging.

---

## Slide 35: Access Modifier Best Practices
**Visual:** Summary of conventions and best practices

- **Make fields private.** Expose them via getters/setters.
- **Make constructors public.** Users need to create objects.
- **Make public methods part of the interface.** They're promises; don't break them lightly.
- **Make private methods for internal helpers.** Hide implementation details.
- **Use `static` for utilities and class-level data.** Avoid overusing static.

These practices lead to robust, maintainable code.

---

## Slide 36: Common Mistakes with Access Modifiers
**Visual:** Red X for bad practices; green checkmark for good

❌ Wrong: `public int age;` (public field, no protection)
✓ Correct: `private int age; public int getAge() { }`

❌ Wrong: Mixing instance and static, confusing which is which
✓ Correct: Clear naming convention distinguishes them

❌ Wrong: Calling static method on an object `person.getTotalStudents();`
✓ Correct: Calling on class `Student.getTotalStudents();`

---

## Slide 37: Recap: Access Modifiers
**Visual:** Quick reference table

- **public**: Accessible everywhere
- **private**: Accessible only within the class
- **protected**: Accessible within class, subclasses, same package
- **package-private**: Accessible within same package (default)

Use them to enforce encapsulation and protect internal state.

---

## Slide 38: Recap: Static Members
**Visual:** Quick reference

- **Static fields**: Shared by all objects; one copy per class
- **Static methods**: Called on the class, not on objects
- **Static initialization**: Runs once when class loads
- **Static final**: Constants

Use static sparingly. Most code should be instance-based.

---

## Slide 39: Recap: This Keyword
**Visual:** Use cases of `this`

- **Field/parameter disambiguation**: `this.name = name;`
- **Constructor chaining**: `this(...)`
- **Method calls**: `this.method()`
- **Passing to other methods**: `other.method(this)`

`this` is unavailable in static context. It represents the current object.

---

## Slide 40: Preview: Inheritance and Polymorphism
**Visual:** Teaser for Day 5

Tomorrow, we'll take these ideas further. We'll learn inheritance—creating class hierarchies where a subclass inherits from a superclass. We'll learn polymorphism—treating objects as instances of parent classes. We'll learn about abstract classes and interfaces—blueprints for other classes. These are advanced OOP concepts that make Java truly powerful. Get some rest tonight and prepare for an exciting Day 5!

---

## Slide 41: How All This Fits Together
**Visual:** Show a real-world system using all concepts from Days 4 and 5

Imagine a bank system. You design a BankAccount class with private fields (balance, accountNumber) and public methods (deposit, withdraw). You add validation in setters. You create thousands of BankAccount objects, each independent. You might add a static field to track total accounts or a static method to get all accounts. Each Account has its own state but shares class-level logic.

This is enterprise programming. This is how real systems are built. You now have the foundation. Keep practicing. By the time you finish this bootcamp, building systems like this will feel natural.

---

## Slide 42: Moving Forward: Your Toolkit
**Visual:** Summary of OOP toolkit so far

You now have:
- Classes and objects
- Constructors and initialization
- Instance and static members
- Access control (public/private)
- Encapsulation best practices
- The `this` keyword

This toolkit is powerful. Use it wisely. Write code. Build projects. Internalize these concepts through practice. The theoretical understanding is important, but the practical skill comes from doing. Good luck!

---
