Java OOP: 60-Minute Comprehensive Lesson Script
Overview & Timing Breakdown

Introduction & Review (5 min)
Inheritance Deep Dive (12 min)
Method Overriding vs Overloading (8 min)
Polymorphism (15 min)
Abstraction (10 min)
Encapsulation & Packages (8 min)
Wrap-up & Q&A (2 min)


SEGMENT 1: Introduction & Review (0:00 - 5:00)
Script:
"Good morning everyone! Today we're diving deep into the core pillars of Object-Oriented Programming in Java. We'll be covering inheritance, polymorphism, abstraction, encapsulation, and how to organize your code professionally using packages. By the end of this hour, you'll understand how to build robust, maintainable Java applications using OOP principles.
I know you've already learned about classes, objects, and basic OOP concepts. Today we're building on that foundation to explore the more advanced features that make Java truly powerful."
Slide 1: Title Slide

Title: Java Object-Oriented Programming: Advanced Concepts
Subtitle: Inheritance, Polymorphism, Abstraction & Code Organization

Slide 2: Today's Learning Objectives

Implement inheritance hierarchies using extends and super
Distinguish between method overriding and overloading
Apply compile-time and runtime polymorphism
Create and use abstract classes and interfaces
Follow encapsulation best practices
Organize code using packages and imports


SEGMENT 2: Inheritance Deep Dive (5:00 - 17:00)
Script:
"Let's start with inheritance, which is the mechanism that allows one class to acquire the properties and behaviors of another class. This promotes code reuse and establishes relationships between classes."
Slide 3: Inheritance Basics

Definition: A class (child/subclass) inherits fields and methods from another class (parent/superclass)
Keyword: extends
Benefits: Code reuse, logical hierarchy, polymorphism foundation
Key Point: Java supports single inheritance only (one parent class)

Script:
"Here's a practical example. Imagine we're building a system for a company that manages different types of employees."
Slide 4: Code Example - Basic Inheritance
javapublic class Employee {
    protected String name;
    protected int id;
    protected double salary;
    
    public Employee(String name, int id, double salary) {
        this.name = name;
        this.id = id;
        this.salary = salary;
    }
    
    public void displayInfo() {
        System.out.println("Name: " + name);
        System.out.println("ID: " + id);
        System.out.println("Salary: $" + salary);
    }
}

public class Manager extends Employee {
    private int teamSize;
    
    public Manager(String name, int id, double salary, int teamSize) {
        super(name, id, salary);  // Calls parent constructor
        this.teamSize = teamSize;
    }
    
    public void displayInfo() {
        super.displayInfo();  // Calls parent method
        System.out.println("Team Size: " + teamSize);
    }
}
Script:
"Notice three critical things here. First, Manager extends Employee using the extends keyword. Second, we use super() in the constructor to call the parent class constructor. This MUST be the first statement in the constructor. Third, notice the protected access modifier in Employee - this allows subclasses to access these fields directly.
Now let's talk about the super keyword in more detail."
Slide 5: The super Keyword

Purpose: References the parent class
Three uses:

super() - Call parent constructor (must be first line)
super.method() - Call parent method
super.field - Access parent field (when shadowed)


Critical Rule: Constructor calls must happen in order: Object → Parent → Child

Script:
"Let me show you a more complex hierarchy to illustrate how this works in practice."
Slide 6: Multi-Level Inheritance Example
javapublic class Person {
    protected String name;
    
    public Person(String name) {
        this.name = name;
        System.out.println("Person constructor");
    }
}

public class Employee extends Person {
    protected int id;
    
    public Employee(String name, int id) {
        super(name);  // Must call Person constructor first
        this.id = id;
        System.out.println("Employee constructor");
    }
}

public class Developer extends Employee {
    private String programmingLanguage;
    
    public Developer(String name, int id, String language) {
        super(name, id);  // Calls Employee constructor
        this.programmingLanguage = language;
        System.out.println("Developer constructor");
    }
}
Script:
"When you create a Developer object, you'll see all three constructor messages print in order: Person, Employee, then Developer. This is called constructor chaining. Java automatically ensures that parent classes are fully initialized before child classes."

SEGMENT 3: Method Overriding vs Overloading (17:00 - 25:00)
Script:
"Now that we understand inheritance, let's clarify two concepts that students often confuse: overriding and overloading. They sound similar but are completely different mechanisms."
Slide 7: Method Overloading (Compile-Time)

Definition: Multiple methods with same name but different parameters in the SAME class
Rules:

Different number of parameters, OR
Different types of parameters, OR
Different order of parameter types


Return type alone is NOT sufficient
Happens at compile time (static polymorphism)

Slide 8: Overloading Example
javapublic class Calculator {
    // Same method name, different parameters
    public int add(int a, int b) {
        return a + b;
    }
    
    public double add(double a, double b) {
        return a + b;
    }
    
    public int add(int a, int b, int c) {
        return a + b + c;
    }
    
    // This would NOT compile - return type alone doesn't count
    // public double add(int a, int b) { ... }
}
Script:
"The compiler determines which method to call based on the arguments you provide. This happens at compile time, which is why it's called compile-time polymorphism or static binding."
Slide 9: Method Overriding (Runtime)

Definition: Subclass provides specific implementation of a method already defined in parent class
Rules:

Must have exact same signature (name, parameters, return type)
Access modifier must be same or MORE permissive
Cannot override final, static, or private methods


Use @Override annotation (best practice)
Happens at runtime (dynamic polymorphism)

Slide 10: Overriding Example
javapublic class Animal {
    public void makeSound() {
        System.out.println("Some generic sound");
    }
}

public class Dog extends Animal {
    @Override  // Best practice - compiler will verify
    public void makeSound() {
        System.out.println("Woof!");
    }
}

public class Cat extends Animal {
    @Override
    public void makeSound() {
        System.out.println("Meow!");
    }
}
Script:
"The @Override annotation is crucial. It's not required, but it tells the compiler 'I intend to override a parent method.' If you make a typo or get the signature wrong, the compiler will catch it. Always use this annotation."
Slide 11: Overloading vs Overriding Comparison Table
FeatureOverloadingOverridingWhereSame classParent-child classesSignatureMust differMust match exactlyBindingCompile-time (static)Runtime (dynamic)PurposeMultiple ways to call methodChange inherited behaviorAccess ModifierAnySame or more permissive

SEGMENT 4: Polymorphism (25:00 - 40:00)
Script:
"Now we get to one of the most powerful concepts in OOP: polymorphism. The word comes from Greek - 'poly' means many, 'morph' means forms. In Java, it means one interface, many implementations."
Slide 12: Two Types of Polymorphism

Compile-Time (Static) Polymorphism

Method overloading
Decided at compile time


Runtime (Dynamic) Polymorphism

Method overriding
Decided at runtime based on actual object type



Script:
"We've already seen compile-time polymorphism with overloading. Now let's focus on runtime polymorphism, which is where things get really interesting."
Slide 13: Runtime Polymorphism in Action
javapublic class PolymorphismDemo {
    public static void main(String[] args) {
        // Parent reference, child object
        Animal animal1 = new Dog();
        Animal animal2 = new Cat();
        Animal animal3 = new Animal();
        
        animal1.makeSound();  // Prints: Woof!
        animal2.makeSound();  // Prints: Meow!
        animal3.makeSound();  // Prints: Some generic sound
        
        // This is polymorphism - same method call,
        // different behavior based on actual object
    }
}
Script:
"This is crucial - the reference type is Animal, but the object type is Dog or Cat. At runtime, Java looks at the actual object to determine which makeSound() method to call. This is called dynamic method dispatch."
Slide 14: Why Polymorphism Matters
javapublic class AnimalShelter {
    public void feedAllAnimals(Animal[] animals) {
        for (Animal animal : animals) {
            animal.makeSound();  // Different sound for each!
            animal.eat();        // Different eating behavior!
        }
    }
}

// Usage
Animal[] shelter = {
    new Dog(),
    new Cat(),
    new Bird(),
    new Rabbit()
};
Script:
"This is the power of polymorphism. We write one method that works with the parent type, but it automatically handles all child types correctly. This makes our code flexible and extensible."
Slide 15: Polymorphism Through Interfaces
javapublic interface Playable {
    void play();
    void pause();
    void stop();
}

public class VideoPlayer implements Playable {
    @Override
    public void play() {
        System.out.println("Playing video...");
    }
    
    @Override
    public void pause() {
        System.out.println("Video paused");
    }
    
    @Override
    public void stop() {
        System.out.println("Video stopped");
    }
}

public class AudioPlayer implements Playable {
    @Override
    public void play() {
        System.out.println("Playing audio...");
    }
    
    @Override
    public void pause() {
        System.out.println("Audio paused");
    }
    
    @Override
    public void stop() {
        System.out.println("Audio stopped");
    }
}
Script:
"Interfaces are contracts. Any class that implements Playable MUST provide implementations for all three methods. This is another form of polymorphism."
Slide 16: Using Interface Polymorphism
javapublic class MediaController {
    private Playable media;
    
    public MediaController(Playable media) {
        this.media = media;
    }
    
    public void startPlayback() {
        media.play();  // Works for ANY Playable type!
    }
}

// Usage
MediaController videoController = new MediaController(new VideoPlayer());
MediaController audioController = new MediaController(new AudioPlayer());
Script:
"Notice that MediaController doesn't care whether it's controlling a VideoPlayer or AudioPlayer. It only cares that the object implements Playable. This is programming to an interface, not an implementation - a fundamental best practice."
Slide 17: Polymorphism Through Abstract Classes
javapublic abstract class Shape {
    protected String color;
    
    public Shape(String color) {
        this.color = color;
    }
    
    // Abstract method - must be implemented by subclasses
    public abstract double calculateArea();
    
    // Concrete method - shared by all shapes
    public void displayColor() {
        System.out.println("Color: " + color);
    }
}

public class Circle extends Shape {
    private double radius;
    
    public Circle(String color, double radius) {
        super(color);
        this.radius = radius;
    }
    
    @Override
    public double calculateArea() {
        return Math.PI * radius * radius;
    }
}

public class Rectangle extends Shape {
    private double width, height;
    
    public Rectangle(String color, double width, double height) {
        super(color);
        this.width = width;
        this.height = height;
    }
    
    @Override
    public double calculateArea() {
        return width * height;
    }
}
Script:
"Abstract classes sit between regular classes and interfaces. They can have both abstract methods that MUST be implemented, and concrete methods that are shared. You cannot instantiate an abstract class directly - you must extend it."

SEGMENT 5: Abstraction (40:00 - 50:00)
Script:
"Abstraction is about hiding complex implementation details and showing only essential features. We've already seen two tools for abstraction: abstract classes and interfaces. Let's compare them."
Slide 18: Abstract Classes vs Interfaces
FeatureAbstract ClassInterfaceMethodsCan have abstract and concreteAll methods abstract (before Java 8)FieldsCan have instance variablesOnly constants (public static final)ConstructorCan have constructorNo constructorMultiple InheritanceNo - single inheritanceYes - implement multipleAccess ModifiersAny modifierMethods public by defaultWhen to Use"is-a" relationship"can-do" capability
Slide 19: When to Use Abstract Classes
java// Use abstract class when:
// - Classes share common code
// - Need constructors
// - Need instance variables
// - Strong "is-a" relationship

public abstract class Vehicle {
    protected String brand;
    protected int year;
    
    public Vehicle(String brand, int year) {
        this.brand = brand;
        this.year = year;
    }
    
    // Shared concrete method
    public void displayInfo() {
        System.out.println(year + " " + brand);
    }
    
    // Force subclasses to define these
    public abstract void start();
    public abstract void stop();
}
Slide 20: When to Use Interfaces
java// Use interface when:
// - Defining capabilities/behaviors
// - Multiple unrelated classes share behavior
// - Need multiple inheritance
// - Loose coupling desired

public interface Flyable {
    void takeOff();
    void land();
    void fly();
}

public interface Swimmable {
    void swim();
}

// A duck can do both!
public class Duck extends Animal implements Flyable, Swimmable {
    @Override
    public void takeOff() { /* implementation */ }
    
    @Override
    public void land() { /* implementation */ }
    
    @Override
    public void fly() { /* implementation */ }
    
    @Override
    public void swim() { /* implementation */ }
}
Script:
"The key distinction: use abstract classes when you have a strong 'is-a' relationship and shared code. Use interfaces when you're defining a capability that multiple unrelated classes might have. A duck IS AN animal, but it CAN fly and CAN swim."
Slide 21: Real-World Abstraction Example
java// Payment processing system example
public interface PaymentMethod {
    boolean processPayment(double amount);
    String getTransactionId();
}

public class CreditCardPayment implements PaymentMethod {
    private String cardNumber;
    private String cvv;
    
    @Override
    public boolean processPayment(double amount) {
        // Credit card processing logic
        return true;
    }
    
    @Override
    public String getTransactionId() {
        return "CC-" + System.currentTimeMillis();
    }
}

public class PayPalPayment implements PaymentMethod {
    private String email;
    
    @Override
    public boolean processPayment(double amount) {
        // PayPal API logic
        return true;
    }
    
    @Override
    public String getTransactionId() {
        return "PP-" + System.currentTimeMillis();
    }
}

// Usage - abstraction in action
public class CheckoutService {
    public void checkout(PaymentMethod payment, double amount) {
        if (payment.processPayment(amount)) {
            System.out.println("Success: " + payment.getTransactionId());
        }
    }
}
Script:
"The checkout service doesn't need to know HOW payment is processed. It just knows that any PaymentMethod can process a payment. This is abstraction - hiding the complex details behind a simple interface."

SEGMENT 6: Encapsulation & Packages (50:00 - 58:00)
Script:
"Let's talk about encapsulation - bundling data and methods together, and controlling access to them. This is about data hiding and protection."
Slide 22: Encapsulation Best Practices

Make fields private
Provide public getters and setters (when needed)
Validate data in setters
Return defensive copies of mutable objects
Use meaningful names
Keep related data and methods together

Slide 23: Good Encapsulation Example
javapublic class BankAccount {
    private double balance;  // Private - cannot be accessed directly
    private final String accountNumber;  // Immutable
    
    public BankAccount(String accountNumber, double initialBalance) {
        this.accountNumber = accountNumber;
        setBalance(initialBalance);  // Use setter for validation
    }
    
    // Getter - read access
    public double getBalance() {
        return balance;
    }
    
    // Setter with validation
    private void setBalance(double balance) {
        if (balance < 0) {
            throw new IllegalArgumentException("Balance cannot be negative");
        }
        this.balance = balance;
    }
    
    // Business logic methods
    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit must be positive");
        }
        this.balance += amount;
    }
    
    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal must be positive");
        }
        if (amount > balance) {
            throw new IllegalArgumentException("Insufficient funds");
        }
        this.balance -= amount;
    }
    
    // Read-only access - no setter
    public String getAccountNumber() {
        return accountNumber;
    }
}
```

### Script:

"Notice that balance is private and we control all access through methods. We validate every change. The account number has no setter - it's immutable after construction. This is proper encapsulation."

### Slide 24: Packages - Organizing Code
- **Purpose:** Namespace management, access control, organization
- **Naming Convention:** Reverse domain name (com.company.project.module)
- **Package Declaration:** Must be first line (except comments)
- **Benefits:**
  - Prevents naming conflicts
  - Controls access (package-private)
  - Logical organization
  - Easier maintenance

### Slide 25: Package Structure Example
```
src/
  com/
    mycompany/
      hrmanagement/
        model/
          Employee.java
          Manager.java
          Department.java
        service/
          PayrollService.java
          HRService.java
        util/
          DateUtil.java
          ValidationUtil.java
        Main.java
Slide 26: Package Declaration and Imports
java// Employee.java
package com.mycompany.hrmanagement.model;

public class Employee {
    private String name;
    private int id;
    // ... rest of class
}

// PayrollService.java
package com.mycompany.hrmanagement.service;

// Import specific class
import com.mycompany.hrmanagement.model.Employee;
// Import all classes from package
import com.mycompany.hrmanagement.util.*;
// Import built-in Java classes
import java.util.ArrayList;
import java.util.List;

public class PayrollService {
    private List<Employee> employees = new ArrayList<>();
    
    public void processPayroll() {
        for (Employee emp : employees) {
            // Process each employee
        }
    }
}
Script:
"Package names should be all lowercase. Use meaningful names that reflect the organization. Import only what you need - avoid wildcard imports in production code as they can cause naming conflicts."
Slide 27: Access Modifiers with Packages
ModifierClassPackageSubclassWorldpublic✓✓✓✓protected✓✓✓✗(default)✓✓✗✗private✓✗✗✗
Script:
"Default access - no modifier - means package-private. It's visible within the same package but not outside. This is useful for internal implementation details you don't want to expose."

SEGMENT 7: Wrap-up & Key Takeaways (58:00 - 60:00)
Script:
"Let's quickly review what we've covered today."
Slide 28: Key Takeaways

Inheritance: Use extends for code reuse, super for parent access
Overloading: Same name, different parameters (compile-time)
Overriding: Same signature, different implementation (runtime)
Polymorphism: Write flexible code that works with parent types
Abstraction: Use interfaces for capabilities, abstract classes for shared behavior
Encapsulation: Private fields, controlled access, validation
Packages: Organize code logically, use reverse domain naming

Slide 29: Design Principles to Remember

Program to interfaces, not implementations
Favor composition over inheritance when possible
Keep classes focused (Single Responsibility)
Always use @Override annotation
Make fields private by default
Validate all inputs

Slide 30: Next Steps

Practice: Build a multi-class project using all these concepts
Review: Java documentation on inheritance and interfaces
Prepare: Next lesson covers collections and generics

Script:
"These concepts are the foundation of professional Java development. Practice by building real projects - maybe a library system, a game with different character types, or a shopping cart. The more you use these patterns, the more natural they'll become. Any quick questions before we finish?"

Additional Teaching Notes:
Pacing Tips:

Have live coding examples ready to demo
Pause after complex slides for questions
Use the compiler to show what works and what doesn't
Show common errors (missing @Override, wrong access modifiers)

Interactive Elements:

Ask students to predict output before running code
Have students identify overloading vs overriding in examples
Quick quiz: "Is this compile-time or runtime polymorphism?"

Common Student Mistakes to Address:

Forgetting super() call in constructors
Confusing overloading with overriding
Making classes too large (missing encapsulation)
Not using interfaces when they should
Overusing inheritance instead of composition

---
