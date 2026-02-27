# Week 1 - Day 5 (Friday) Part 2: Abstraction, Encapsulation & Packages
## Advanced OOP: Contracts, Best Practices & Code Organization

---

## Slide 1: Welcome to Part 2: Abstraction and Beyond
**Visual:** Part 2 title with abstraction concept art; show layered approach

Welcome to Part 2! In Part 1, you learned inheritance and polymorphism. You can build class hierarchies and leverage runtime polymorphism. Now we layer abstraction on top. Abstraction means hiding details and exposing only what matters. In Part 2, we'll explore abstract classes and interfaces—tools for defining contracts that classes agree to follow. We'll deepen encapsulation best practices. And we'll organize our code into packages for maintainability. These are professional tools used in enterprise systems every day. Let's dive in.

---

## Slide 2: The Concept of Abstraction
**Visual:** Show reality → model → abstraction; highlight what's hidden and what's exposed

Abstraction means representing something complex by showing only essential features while hiding unnecessary details. When you drive a car, you care about the steering wheel, pedals, and gear shift. You don't care about the internal combustion details. The car abstracts away complexity.

In programming, abstraction works the same way. You define what an object should do (its contract) without worrying about how it's implemented. This lets different implementations coexist under a common interface.

---

## Slide 3: Abstract Classes
**Visual:** Show abstract class syntax and marking with `abstract` keyword

An abstract class is a class that can't be instantiated directly. It's meant to be extended by subclasses. Use the `abstract` keyword:

```java
public abstract class Animal {
    abstract void makeSound();
}
```

You can't create an Animal object: `new Animal()` is an error. But you can create Dog, Cat, Bird—subclasses that implement the contract.

Abstract classes are blueprints. They define the structure subclasses must follow.

---

## Slide 4: Abstract Methods
**Visual:** Show abstract method (no body) vs concrete method (with body)

An abstract method has no implementation—just a signature. Subclasses must provide the implementation.

```java
public abstract class Animal {
    public abstract void makeSound();
    public void eat() { System.out.println("Eating"); }  // Concrete
}
```

`makeSound()` is abstract; subclasses must override it. `eat()` is concrete; subclasses inherit it.

A class with abstract methods must be abstract:

```java
public class Dog extends Animal {
    @Override
    public void makeSound() { System.out.println("Woof!"); }
}
```

Dog provides the implementation. Now you can create Dog objects.

---

## Slide 5: When to Use Abstract Classes
**Visual:** Decision tree: Is this a concrete entity vs template/blueprint?

Use abstract classes when:
- You have a template for subclasses to follow
- You want to force subclasses to implement certain methods
- You want shared code in a parent class
- You're modeling an hierarchy of related types

Don't use abstract classes when:
- The class is concrete and can stand alone
- You need multiple inheritance (use interfaces instead)

---

## Slide 6: Abstract Class Example: Shape Hierarchy
**Visual:** Shape → Circle, Rectangle, Triangle; abstract methods highlighted

```java
public abstract class Shape {
    protected String color;
    
    public abstract double area();
    public abstract double perimeter();
    
    public void describe() {
        System.out.println("A " + color + " shape");
    }
}

public class Circle extends Shape {
    private double radius;
    
    @Override
    public double area() { return Math.PI * radius * radius; }
    
    @Override
    public double perimeter() { return 2 * Math.PI * radius; }
}
```

Shape defines the contract. Subclasses implement it.

---

## Slide 7: Partial Implementation in Abstract Classes
**Visual:** Show mix of abstract and concrete methods

Abstract classes can have both abstract and concrete methods. Concrete methods provide default behavior; subclasses can override them or inherit them.

```java
public abstract class Vehicle {
    public abstract void start();
    
    public void stop() {  // Concrete; all vehicles stop the same way
        System.out.println("Vehicle stopped");
    }
}

public class Car extends Vehicle {
    @Override
    public void start() { System.out.println("Car engine started"); }
    // Inherits stop()
}
```

All vehicles stop the same way, so `stop()` is concrete. Vehicles start differently, so `start()` is abstract.

---

## Slide 8: Introduction to Interfaces
**Visual:** Interface syntax with `implements` keyword; show multiple inheritance possibility

An interface is a contract. It specifies what methods a class must have, but not how they're implemented. Use the `interface` keyword:

```java
public interface Animal {
    void makeSound();
    void eat();
}

public class Dog implements Animal {
    @Override
    public void makeSound() { System.out.println("Woof!"); }
    
    @Override
    public void eat() { System.out.println("Dog eats kibble"); }
}
```

Dog implements the Animal contract. It must provide all methods. Unlike classes, a class can implement multiple interfaces:

```java
public class Dog implements Animal, Pet, Companion { }
```

Dog is an Animal, a Pet, and a Companion. Interfaces allow multiple inheritance of interface contracts.

---

## Slide 9: Interfaces vs Abstract Classes
**Visual:** Comparison table: purpose, methods, inheritance, instantiation

| Aspect | Abstract Class | Interface |
|--------|---|---|
| Purpose | Template with shared code | Contract/specification |
| Methods | Can have concrete or abstract | All abstract (until Java 8+) |
| State | Can have fields and state | No state (constants only) |
| Inheritance | Single inheritance (extends) | Multiple inheritance (implements) |
| Instantiation | Cannot instantiate | Cannot instantiate |
| Access | public, protected, private | Usually public |

Use abstract classes for common code and shared state. Use interfaces for contracts without shared code.

---

## Slide 10: Implementing Interfaces
**Visual:** Show `implements` with one or multiple interfaces

To implement an interface, use the `implements` keyword and provide all methods:

```java
public interface Drawable {
    void draw();
    void erase();
}

public class Circle implements Drawable {
    @Override
    public void draw() { /* draw circle */ }
    
    @Override
    public void erase() { /* erase circle */ }
}
```

Implement every method. If you don't, the class must be abstract:

```java
public abstract class Shape implements Drawable {
    public abstract void draw();
    public abstract void erase();
}
```

---

## Slide 11: Multiple Interface Implementation
**Visual:** Show class implementing multiple interfaces; Venn diagram of contracts

A class can implement multiple interfaces:

```java
public interface Drawable { void draw(); }
public interface Serializable { byte[] serialize(); }
public interface Comparable { int compareTo(Object obj); }

public class Document implements Drawable, Serializable, Comparable {
    @Override
    public void draw() { /* draw document */ }
    
    @Override
    public byte[] serialize() { /* serialize document */ }
    
    @Override
    public int compareTo(Object obj) { /* compare documents */ }
}
```

Document agrees to all three contracts. It must implement all methods from all interfaces. This is multiple interface inheritance—multiple contracts, one class.

---

## Slide 12: Interface as Type
**Visual:** Show reference type as interface, pointing to different implementations

Like abstract classes, you can use an interface as a reference type:

```java
Drawable drawable = new Circle();
drawable.draw();  // Calls Circle's draw()

drawable = new Rectangle();
drawable.draw();  // Calls Rectangle's draw()
```

Any class that implements Drawable can be assigned to a Drawable reference. This is polymorphism at work. You don't care what concrete class it is; you just care that it's Drawable.

---

## Slide 13: Common Interfaces in Java
**Visual:** List java.util.*, java.io.*, java.lang.* interfaces with examples

Java provides many built-in interfaces:

- **Comparable**: Objects that can be compared (`compareTo()`)
- **Iterable**: Objects that can be iterated over (for-each loops)
- **Serializable**: Objects that can be saved/loaded
- **Runnable**: Objects that can be executed as threads
- **AutoCloseable**: Objects that manage resources (try-with-resources)

Understanding these interfaces helps you use Java libraries effectively.

---

## Slide 14: Real-World Example: Payment System with Interfaces
**Visual:** PaymentMethod interface with Credit Card, PayPal, Apple Pay implementations

```java
public interface PaymentMethod {
    void charge(double amount);
    void refund(double amount);
    String getPaymentType();
}

public class CreditCard implements PaymentMethod {
    private String cardNumber;
    
    @Override
    public void charge(double amount) { /* charge card */ }
    
    @Override
    public void refund(double amount) { /* refund to card */ }
    
    @Override
    public String getPaymentType() { return "Credit Card"; }
}

public class PayPal implements PaymentMethod {
    private String email;
    
    @Override
    public void charge(double amount) { /* charge PayPal */ }
    
    @Override
    public void refund(double amount) { /* refund via PayPal */ }
    
    @Override
    public String getPaymentType() { return "PayPal"; }
}
```

Different payment methods, same interface. When processing payment:

```java
PaymentMethod payment = getPaymentMethod();  // Could be any type
payment.charge(99.99);  // Works regardless of implementation
```

This is the power of interfaces. You write code once, works with many types.

---

## Slide 15: Encapsulation Best Practices Recap
**Visual:** Checklist of encapsulation principles

From Day 4, recall encapsulation best practices:

- ✓ Make fields **private** (except static final constants)
- ✓ Provide **public** getters/setters for access
- ✓ Add **validation** in setters
- ✓ Use **access modifiers** to control visibility
- ✓ Hide **implementation details**
- ✓ Use **protected** for subclass access
- ✓ Override **toString()** and **equals()** thoughtfully

These practices protect your classes and enable flexibility.

---

## Slide 16: Encapsulation with Inheritance
**Visual:** Show protected fields accessible to subclasses; private hidden

When combining encapsulation with inheritance:

```java
public class Animal {
    private String name;          // Only Animal can access
    protected int age;             // Animal and subclasses can access
    private String secret;         // Hidden from everyone
    
    protected void sleep() { }    // Subclasses can call
    private void hideFood() { }   // Subclasses cannot call
}

public class Dog extends Animal {
    public void info() {
        System.out.println(name);    // ERROR: private
        System.out.println(age);     // OK: protected
        System.out.println(secret);  // ERROR: private
    }
}
```

`protected` members are accessible to subclasses. Private members remain private even to subclasses.

---

## Slide 17: Getter and Setter Best Practices
**Visual:** Show validation, side effects, and transformations in getters/setters

Getters and setters aren't just pass-throughs. They can do important work:

```java
public class Person {
    private String email;
    private int age;
    
    public String getEmail() {
        return email.toLowerCase();  // Transformation
    }
    
    public void setEmail(String email) {
        if (email != null && email.contains("@")) {  // Validation
            this.email = email;
            notifyEmailChanged();  // Side effect
        }
    }
    
    public int getAge() {
        return age;
    }
    
    public void setAge(int age) {
        if (age > 0 && age < 150) {  // Validation
            this.age = age;
        }
    }
}
```

Getters can transform. Setters can validate and trigger side effects. This flexibility is why encapsulation matters.

---

## Slide 18: Immutability and Defensive Copying
**Visual:** Show immutable object pattern; defensive copying in getters/setters

For security-sensitive data, consider immutability. Make objects unchangeable:

```java
public final class ImmutablePerson {
    private final String name;
    private final int age;
    
    public ImmutablePerson(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    public String getName() { return name; }
    public int getAge() { return age; }
    // No setters!
}
```

No setters mean the object can't change. If you have mutable fields (like List), use defensive copying:

```java
public class PersonList {
    private List<String> friends;
    
    public List<String> getFriends() {
        return new ArrayList<>(friends);  // Copy, not original
    }
    
    public void setFriends(List<String> friends) {
        this.friends = new ArrayList<>(friends);  // Copy, not reference
    }
}
```

Return copies, not references. This prevents external code from modifying your internals.

---

## Slide 19: Packages: Organizing Code
**Visual:** Package hierarchy diagram; folders and structure

A package is a namespace that organizes classes. Think of it like a folder structure for your code.

```
com/
  example/
    myapp/
      models/
        User.java
        Product.java
      utils/
        Formatter.java
        Logger.java
      controllers/
        UserController.java
```

The package names reflect the folder structure. `User.java` in the `models` folder has package `com.example.myapp.models`.

Packages prevent naming conflicts. Two projects can have a `User` class as long as they're in different packages: `project1.models.User` vs `project2.models.User`.

---

## Slide 20: Package Declaration and Naming Conventions
**Visual:** Show package declaration at top of file; naming convention rules

Every Java file starts with a package declaration:

```java
package com.example.myapp.models;

public class User {
    // ...
}
```

Package names are lowercase, dot-separated. By convention:
- Start with reverse domain name: `com.example`
- Add application name: `com.example.myapp`
- Add module/subsystem: `com.example.myapp.models`

This structure prevents conflicts. `com.example.myapp.User` is different from `org.othername.myapp.User`.

---

## Slide 21: Import Statements
**Visual:** Show `import` and `import static`; illustrate what gets imported

To use a class from another package, import it:

```java
import com.example.myapp.models.User;

public class UserService {
    public User getUser(int id) { /* ... */ }
}
```

Without the import, you'd have to use the full name:

```java
public com.example.myapp.models.User getUser(int id) { /* ... */ }
```

Imports save typing. You can import specific classes or entire packages:

```java
import com.example.myapp.models.*;  // Import all classes in models
```

Use specific imports over wildcard imports for clarity. It's clear what you're using.

---

## Slide 22: Static Imports
**Visual:** Show `import static` for constants and static methods

You can import static members of classes:

```java
import static java.lang.Math.PI;
import static java.lang.Math.sqrt;

public class Calculator {
    public double circleArea(double radius) {
        return PI * radius * radius;
    }
    
    public double hypotenuse(double a, double b) {
        return sqrt(a * a + b * b);
    }
}
```

Now you use `PI` directly instead of `Math.PI`. Use static imports sparingly—they can make code less clear if overused.

---

## Slide 23: Package-Private Access (Default)
**Visual:** Show package-private scope; classes in same package can access

If a class doesn't have `public`, it's package-private:

```java
class LocalUtility {  // package-private
    void helperMethod() { }
}

public class PublicClass {
    public void useLocal() {
        LocalUtility util = new LocalUtility();  // Works; same package
    }
}
```

`LocalUtility` is accessible only within its package. Other packages can't access it. Use package-private for internal helper classes.

---

## Slide 24: Organizing Large Projects
**Visual:** Multi-layer architecture: controllers, services, models, utils, etc.

For large projects, organize into layers:

```
src/
  com/example/myapp/
    controllers/
      UserController.java
      ProductController.java
    services/
      UserService.java
      ProductService.java
    models/
      User.java
      Product.java
    repositories/
      UserRepository.java
      ProductRepository.java
    utils/
      Formatter.java
      Logger.java
    exceptions/
      CustomException.java
```

This structure is scalable. Each layer has responsibilities. Controllers handle HTTP requests. Services contain business logic. Models represent data. Repositories handle database access. This separation of concerns keeps code maintainable.

---

## Slide 25: Visibility Across Packages
**Visual:** Table showing public/protected/private visibility by location

| Modifier | Same Class | Same Package | Subclass (Different Package) | World |
|----------|---|---|---|---|
| public | ✓ | ✓ | ✓ | ✓ |
| protected | ✓ | ✓ | ✓ | ✗ |
| package-private | ✓ | ✓ | ✗ | ✗ |
| private | ✓ | ✗ | ✗ | ✗ |

Use this table to decide on access levels. Generally: public for interface, private for internals, protected for subclass access, package-private for local helpers.

---

## Slide 26: Java Standard Library Packages
**Visual:** Show common packages: java.util.*, java.io.*, java.lang.*, etc.

The Java standard library is organized into packages:

- **java.lang**: Core classes (String, Object, Math)
- **java.util**: Collections (List, Map, Set)
- **java.io**: Input/output
- **java.nio**: New I/O (faster, more features)
- **java.time**: Date and time
- **java.net**: Networking
- **java.reflect**: Reflection
- **java.sql**: Database access
- **java.security**: Security and cryptography

Familiarizing yourself with these packages helps you use Java effectively.

---

## Slide 27: Package Best Practices
**Visual:** Checklist of package organization rules

- ✓ Use **reverse domain** naming: `com.company.project`
- ✓ Keep **packages focused**: one responsibility per package
- ✓ Avoid **deep nesting**: 3-4 levels usually sufficient
- ✓ Use **descriptive names**: `models`, `services`, `controllers`
- ✓ Group **related classes**: users, products, orders in respective packages
- ✓ Make **internal helpers** package-private
- ✓ Keep **interfaces public**: they're contracts
- ✓ Hide **implementation details** in separate packages if needed

Good package structure makes projects navigable.

---

## Slide 28: Common Beginner Mistakes: Misusing Access Modifiers
**Visual:** Red X for wrong, green checkmark for correct

❌ Making everything public:
```java
public class User {
    public String name;
    public int age;
    public String email;
}
```

✓ Proper encapsulation:
```java
public class User {
    private String name;
    private int age;
    private String email;
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    // ... other getters/setters with validation
}
```

Encapsulation is crucial.

---

## Slide 29: Common Beginner Mistakes: Abstract Class Errors
**Visual:** Show attempting to instantiate abstract class; compilation error

❌ Wrong:
```java
abstract class Animal { }
Animal a = new Animal();  // ERROR: Cannot instantiate abstract class
```

✓ Correct:
```java
class Dog extends Animal { }
Animal a = new Dog();  // OK; Dog is concrete
```

Abstract classes are templates, not objects. Instantiate subclasses.

---

## Slide 30: Common Beginner Mistakes: Forgetting to Implement Interface Methods
**Visual:** Show incomplete interface implementation

❌ Wrong:
```java
class Dog implements Animal {
    // Forgot to implement makeSound()!
}
```

Compiler error: "Dog must either define abstract method... or be declared abstract."

✓ Correct:
```java
class Dog implements Animal {
    @Override
    public void makeSound() { System.out.println("Woof!"); }
}
```

Implement all interface methods or make the class abstract.

---

## Slide 31: The Dependency Inversion Principle (SOLID)
**Visual:** Show depending on abstractions (interfaces) vs concrete classes

High-level modules should depend on abstractions, not concrete implementations:

```java
// BAD: Depends on concrete class
public class UserService {
    private MySQLDatabase db = new MySQLDatabase();
}

// GOOD: Depends on interface
public interface Database { /* methods */ }
public class UserService {
    private Database db;
    
    public UserService(Database db) {
        this.db = db;
    }
}
```

When you depend on interfaces, you can swap implementations easily. This is flexible and testable.

---

## Slide 32: Comparing Abstract Classes and Interfaces Revisited
**Visual:** Decision flowchart: Use abstract class or interface?

**Use abstract class when:**
- Classes share code or state
- You need non-public members (protected, private)
- You need instance variables with initial values
- Constructors with initialization

**Use interface when:**
- Defining a contract, multiple implementations
- Classes unrelated except by contract
- You want multiple inheritance
- All members are static final (constants)

Often, you use both: abstract class with shared code, interface for contract.

---

## Slide 33: Real-World Architecture Example
**Visual:** Layer diagram with interfaces and implementations

```java
// Interface (contract)
public interface UserRepository {
    User findById(int id);
    void save(User user);
}

// Implementation (concrete)
public class MySQLUserRepository implements UserRepository {
    @Override
    public User findById(int id) { /* MySQL query */ }
    
    @Override
    public void save(User user) { /* MySQL insert */ }
}

// Service (depends on interface)
public class UserService {
    private UserRepository repo;
    
    public UserService(UserRepository repo) {
        this.repo = repo;
    }
    
    public User getUser(int id) {
        return repo.findById(id);
    }
}
```

Service depends on UserRepository (interface), not MySQLUserRepository (concrete). You can swap implementations without changing the service. This is professional architecture.

---

## Slide 34: Preview: Beyond Week 1
**Visual:** Teaser for Week 2 and beyond; collections, exceptions, concurrency

By the end of Day 5, you've completed Week 1. You understand OOP fundamentals: classes, inheritance, polymorphism, abstraction, encapsulation, packages. Next week, we'll build on this: collections, exceptions, lambdas, concurrency. Week 3 begins frontend. Week 4 brings frameworks. Your foundation is strong. Great work!

---

## Slide 35: Recap: Abstraction Tools
**Visual:** Summary of abstract classes vs interfaces

**Abstract Classes:**
- Blueprint for subclasses
- Can have concrete and abstract methods
- Can have state (fields)
- Single inheritance

**Interfaces:**
- Contract specification
- All methods abstract (before Java 8)
- No state, only constants
- Multiple inheritance

Both are abstraction tools. Use them wisely.

---

## Slide 36: Recap: Encapsulation Mastery
**Visual:** Checklist of encapsulation principles

- Private fields, public getters/setters
- Validation in setters
- Protected for subclass access
- Immutability where appropriate
- Defensive copying for mutable fields
- Override toString() and equals()

Professional encapsulation protects your classes.

---

## Slide 37: Recap: Package Organization
**Visual:** Package structure best practices

- Reverse domain naming
- Focused, single-responsibility packages
- Shallow hierarchy (3-4 levels)
- Descriptive names
- Package-private for internals
- Public for interfaces

Good organization scales with your project.

---

## Slide 38: Design Principles You Now Know
**Visual:** SOLID principles summary

- **S**ingle Responsibility: Each class has one reason to change
- **O**pen/Closed: Open for extension, closed for modification
- **L**iskov Substitution: Subclasses substitute for superclasses
- **I**nterface Segregation: Clients depend on interfaces they use
- **D**ependency Inversion: Depend on abstractions, not concrete classes

These principles guide professional design.

---

## Slide 39: Thinking About Design
**Visual:** Ask questions before coding

Before creating a class or hierarchy, ask:
- Is this an is-a or has-a relationship?
- Should this be abstract or concrete?
- Does it need to be an interface or abstract class?
- How should this be packaged?
- What methods should be public vs private?

Thoughtful design pays dividends.

---

## Slide 40: Your OOP Mastery
**Visual:** Congratulations banner; you've completed Week 1 OOP

You now understand:
- Classes and objects
- Constructors and initialization
- Access modifiers and encapsulation
- Static members
- Inheritance and hierarchies
- Method overriding and overloading
- Polymorphism (runtime and compile-time)
- Abstract classes and interfaces
- Packages and organization

You're ready to build professional systems. Congratulations!

---

## Slide 41: Looking Forward: Your Engineering Journey
**Visual:** Path forward: Week 2 begins; collections, exceptions, frameworks

Next week begins the advanced phase. You'll learn collections (ArrayList, HashMap), exceptions, lambdas, concurrency. Week 3 adds frontend. Week 4 brings frameworks. Week 5 opens to SQL, Spring, databases. By the end of the bootcamp, you'll be a full-stack engineer. The journey begins now. See you next week!

---
