# Week 1 - Day 4 (Thursday) Part 1: Classes and Objects
## OOP Fundamentals: Classes, Constructors & Class Members

---

## Slide 1: Welcome to Object-Oriented Programming
**Visual:** Title slide with Java logo and "OOP: Classes and Objects"

Welcome to Part 1 of OOP! Today we're transitioning from procedural programming (functions and variables) to object-oriented programming. We're going to learn how to design and create classes—the blueprints for objects. By the end of this hour, you'll understand how to model real-world entities, create constructors, and organize code into meaningful class structures. OOP is the foundation for enterprise Java development, and mastering classes is your first critical step.

---

## Slide 2: What is Object-Oriented Programming?
**Visual:** Diagram showing procedural vs OOP approaches; think "functions in a box" vs "objects with behavior"

Object-Oriented Programming (OOP) is a paradigm that organizes code around objects—entities that combine data (state) and behavior (methods). In procedural programming, you write functions that act on data. In OOP, you bundle them together. An object is an instance of a class. A class is a blueprint—it defines what properties (fields) and actions (methods) objects of that type will have. Think of it like a cookie cutter (class) and cookies (objects). The cutter defines the shape; each cookie is a unique instance.

---

## Slide 3: Classes vs Objects
**Visual:** Two columns: "Class" (blueprint) vs "Object" (instance); show a car blueprint vs actual cars

A class is a template—an abstract definition. An object is a concrete realization of that template. If you define a `Car` class with properties like `color`, `speed`, and methods like `accelerate()`, every `Car` object you create has those properties and methods, but with different values. You can have 1,000 `Car` objects all created from the same `Car` class. Classes live in your source code; objects are created at runtime in memory.

---

## Slide 4: Class Anatomy Overview
**Visual:** Class structure diagram showing class name, fields, methods, constructor

Every class in Java has several key components: a class name, fields (variables that hold state), methods (functions that perform actions), and a constructor (special method to initialize objects). Optional elements include access modifiers, which control visibility. A class in its simplest form looks like `public class MyClass { }`. That's a valid class! Of course, it won't do much, but the structure is there.

---

## Slide 5: Defining Your First Class
**Visual:** Simple class definition on screen with `public class Person {}`

Here's how you define a class: `public class Person { }`. The `public` keyword means this class is accessible from anywhere. Inside the braces, you'll add fields, methods, and constructors. By convention, class names start with a capital letter and use PascalCase (e.g., `Person`, `StudentAccount`, `DatabaseConnection`). File organization: save your `Person` class in a file named `Person.java`. One public class per file is the standard practice.

---

## Slide 6: Class Fields (Instance Variables)
**Visual:** Class definition showing fields like `String name`, `int age`; show memory diagram with field values

Fields are variables that belong to an object. They hold the object's state—data that persists for the lifetime of that object. In the `Person` class, you might have fields: `String name`, `int age`, `double salary`. Each `Person` object gets its own copy of these fields. If you create two `Person` objects, `john` has its own `name` and `age`, separate from `jane`'s. This is instance-level data—it varies per instance.

---

## Slide 7: Declaring Class Fields
**Visual:** Code snippet: `private String name;` and `private int age;` with explanations

Declaration syntax: `[visibility] [type] fieldName;` Example: `private String name;` The type can be primitive (`int`, `double`, `boolean`) or object (`String`, `Date`). You can initialize fields at declaration: `private int age = 0;` or leave them uninitialized. Uninitialized fields get default values: numeric types default to 0, booleans to false, objects to null. Best practice: declare fields as `private` (we'll cover why in Part 2).

---

## Slide 8: Class Methods (Instance Methods)
**Visual:** Method signature with annotations pointing to visibility, return type, name, parameters

Methods are functions that belong to a class. They define the behavior of objects. A `Person` might have methods like `getName()`, `celebrateBirthday()`, or `calculateTaxes()`. Methods operate on the object's fields. Unlike standalone functions, methods have access to all the object's fields automatically. Method syntax: `[visibility] [return type] methodName(parameters) { body }`

---

## Slide 9: Method Example
**Visual:** Code snippet showing a complete method:
```java
public String getName() {
    return name;
}

public void celebrateBirthday() {
    age++;
}
```

Here's a practical example: `public String getName() { return name; }` This method is public (callable from anywhere), returns a String, has no parameters, and returns the `name` field. Second example: `public void celebrateBirthday() { age++; }` This method is public, returns void (nothing), and increments the age. Notice methods can access and modify fields directly.

---

## Slide 10: Constructors: What Are They?
**Visual:** Timeline showing class definition → constructor call → object created

A constructor is a special method that runs automatically when you create (instantiate) an object. Its job: initialize the object's state. A constructor has no return type (not even void), and its name matches the class name exactly. Every class gets a default constructor automatically—even if you don't write one. The default constructor takes no parameters and does nothing except create the object. If you write your own constructor, the default one disappears.

---

## Slide 11: Default Constructor
**Visual:** Code showing empty class and its implicit default constructor

If you write: `public class Person { }`, Java automatically gives it a default constructor equivalent to `public Person() { }`. You can call it: `Person p = new Person();` This creates a Person object, but the fields remain uninitialized (at their default values). The default constructor is useful for simple classes, but most real objects need initialization logic—that's where custom constructors come in.

---

## Slide 12: Parameterized Constructor
**Visual:** Constructor with parameters; show how it initializes fields from parameters

A parameterized constructor accepts arguments and uses them to initialize fields:
```java
public class Person {
    private String name;
    private int age;
    
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
```
Now you can create initialized objects: `Person p = new Person("Alice", 30);` The parameters `name` and `age` are assigned to the fields. Notice `this.name = name;`—the `this` keyword refers to the current object's field.

---

## Slide 13: Multiple Constructors (Constructor Overloading)
**Visual:** Class with two constructors: one no-arg, one with parameters

You can define multiple constructors—as long as they have different parameter lists. This is called overloading.
```java
public class Person {
    private String name;
    private int age;
    
    public Person() {
        this.name = "Unknown";
        this.age = 0;
    }
    
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
```
Now you can call `new Person()` or `new Person("Bob", 25)`. The Java compiler determines which constructor to use based on the arguments you provide.

---

## Slide 14: Constructor Chaining with this()
**Visual:** Diagram showing constructor calling another constructor with `this()`

To reduce code duplication, you can have one constructor call another using `this()`:
```java
public class Person {
    private String name;
    private int age;
    
    public Person() {
        this("Unknown", 0);
    }
    
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
```
The no-arg constructor calls the parameterized constructor, delegating the initialization. This avoids duplicating field assignment logic. `this()` must be the first statement in the constructor.

---

## Slide 15: Understanding new Keyword
**Visual:** Memory diagram showing stack and heap; show object creation process

When you write `Person p = new Person("Charlie", 28);`, here's what happens:

1. `new` allocates memory on the heap for the object
2. The constructor runs, initializing fields
3. A reference to that memory is assigned to `p` (on the stack)

The variable `p` holds a reference (address), not the object itself. Multiple variables can reference the same object. The object exists in memory until there are no more references to it (garbage collection).

---

## Slide 16: Object Creation and Initialization Flow
**Visual:** Step-by-step flow: 1) Declare variable, 2) new keyword, 3) Constructor runs, 4) Reference assigned

Let's trace through `Person p = new Person("Diana", 32);`:

1. Stack: space allocated for variable `p`
2. Heap: memory allocated for Person object
3. Default field values: `name = null`, `age = 0`
4. Constructor runs: `name = "Diana"`, `age = 32`
5. Reference: `p` now points to this object on the heap

After this line, `p` is usable; you can call `p.getName()`, `p.celebrateBirthday()`, etc.

---

## Slide 17: Creating Multiple Objects from Same Class
**Visual:** Show three Person objects all from Person class blueprint

```java
Person alice = new Person("Alice", 25);
Person bob = new Person("Bob", 30);
Person charlie = new Person("Charlie", 22);
```

Each object is independent. They're all `Person` instances, but each has its own fields. `alice.age` is 25; `bob.age` is 30. Each occupies separate memory on the heap. This is the power of classes—define once, create many variations.

---

## Slide 18: Accessing Object Fields and Methods
**Visual:** Syntax diagram showing `object.field` and `object.method()`

To access an object's field or call a method, use dot notation: `person.name` or `person.getName()`. Example:
```java
Person p = new Person("Eve", 28);
String name = p.getName();  // Call method, get result
p.celebrateBirthday();      // Call method with no return
System.out.println(p.age);  // Access field directly (if public)
```
In practice, fields are often private (you'll access them through getters), but methods are public.

---

## Slide 19: Getter and Setter Methods
**Visual:** Show private field with public getter/setter methods around it

Encapsulation best practice: make fields private, provide public methods to access them.
```java
public class Person {
    private String name;
    
    public String getName() {        // Getter
        return name;
    }
    
    public void setName(String name) { // Setter
        this.name = name;
    }
}
```
This pattern allows you to add validation (e.g., "name can't be null") or side effects (logging, auditing) without breaking code that uses the class.

---

## Slide 20: Class vs Instance Members
**Visual:** Two columns: instance members (belong to objects) vs static members (belong to class) [preview for Part 2]

Quick preview: instance fields and methods belong to individual objects. Static fields and methods belong to the class itself—all objects share them. You'll learn about `static` in Part 2, but it's important to know the distinction exists. Instance is the default; static is special.

---

## Slide 21: `this` Keyword in Constructor
**Visual:** Code showing `this.name = name;` with annotation explaining what this refers to

Inside a constructor or method, `this` refers to the current object. `this.name` means "the name field of the current object." Why use `this`? When a parameter has the same name as a field:
```java
public Person(String name, int age) {
    this.name = name;  // this.name is the field; name is the parameter
    this.age = age;
}
```
Without `this`, `name = name;` would just assign the parameter to itself. `this` disambiguates.

---

## Slide 22: Real-World Example: BankAccount Class
**Visual:** Class diagram and implementation

```java
public class BankAccount {
    private String accountNumber;
    private double balance;
    private String accountHolder;
    
    public BankAccount(String accountNumber, String accountHolder, double initialBalance) {
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.balance = initialBalance;
    }
    
    public void deposit(double amount) {
        balance += amount;
    }
    
    public void withdraw(double amount) {
        if (amount <= balance) {
            balance -= amount;
        }
    }
    
    public double getBalance() {
        return balance;
    }
}
```

This real-world class demonstrates fields (account number, balance, holder), a constructor that initializes them, and methods (deposit, withdraw, getBalance) that operate on them.

---

## Slide 23: Using BankAccount
**Visual:** Code showing object creation and method calls

```java
BankAccount account = new BankAccount("ACC123", "John Doe", 1000);
account.deposit(500);     // balance = 1500
account.withdraw(200);    // balance = 1300
double current = account.getBalance(); // 1300
```

Notice how the account object maintains state (balance) across method calls. This state persistence is a hallmark of OOP—objects remember their data between operations.

---

## Slide 24: Common Beginner Mistakes: Forgetting new
**Visual:** Red X next to incorrect code, green checkmark next to correct

❌ Wrong: `Person p = Person("Alice", 25);`
✓ Correct: `Person p = new Person("Alice", 25);`

Always use `new` to create objects. Without it, Java doesn't know to allocate memory and call the constructor.

---

## Slide 25: Common Beginner Mistakes: Constructor Parameters
**Visual:** Show confusion between fields and parameters

❌ Wrong: Fields and parameters with different names make initialization confusing:
```java
public Person(String n, int a) {
    name = n;
    age = a;
}
```

✓ Better: Match names and use `this`:
```java
public Person(String name, int age) {
    this.name = name;
    this.age = age;
}
```

Clear naming makes code self-documenting.

---

## Slide 26: Common Beginner Mistakes: Null Initialization
**Visual:** Show NullPointerException and explain

```java
public class Person {
    private String name;  // Default: null
    
    public Person() { }   // No initialization!
}

Person p = new Person();
System.out.println(p.getName());  // Returns null
```

If you don't initialize object fields (especially Strings, Lists), they remain null. Calling methods on null objects throws a NullPointerException. Always initialize fields in constructors or use default values.

---

## Slide 27: Thinking About Object State
**Visual:** Show object at different points in time with changing state

Objects are stateful. A `Person` object remembers its name and age. A `BankAccount` remembers its balance. This is different from functions, which are stateless—they take input, produce output, and forget. Objects persist data. Understanding this state machine mentality is crucial for OOP design.

---

## Slide 28: Object References and Aliases
**Visual:** Show two variables pointing to the same object

```java
Person p1 = new Person("Alice", 25);
Person p2 = p1;

p2.celebrateBirthday();
System.out.println(p1.age);  // Also 26!
```

`p1` and `p2` reference the same object. Changes through `p2` affect the object that `p1` sees. They're aliases—two names for one entity.

---

## Slide 29: Comparing Objects (Preview)
**Visual:** Show `==` vs `.equals()`; mention it's covered later

Don't use `==` to compare objects—it checks if two variables point to the same memory location. Use `.equals()` instead. We'll cover this deeply in Part 2, but it's a common pitfall to know about.

---

## Slide 30: Method Return Types Review
**Visual:** Examples of void, primitives, and objects as return types

Methods can return:
- **void**: no return value (`public void celebrateBirthday()`)
- **Primitives**: `int`, `double`, `boolean` (`public int getAge()`)
- **Objects**: any class (`public String getName()`)
- **Arrays**: arrays of any type (`public int[] getScores()`)

When a method returns a value, that value replaces the method call in your code.

---

## Slide 31: Method Void vs Return
**Visual:** Flow diagram showing void (does something) vs return (gives back value)

```java
public void printInfo() {
    System.out.println("Name: " + name);
}

public String getInfo() {
    return "Name: " + name;
}
```

`printInfo()` performs an action (side effect). `getInfo()` computes and returns a value. Use void for actions; use return types for queries.

---

## Slide 32: Using Constructors with Default Values
**Visual:** Constructor with some fields having defaults

```java
public class Person {
    private String name;
    private int age;
    private double height = 0.0;  // Default value
    
    public Person(String name) {
        this.name = name;
        this.age = 0;  // Default in constructor
    }
}
```

Fields can have default values assigned at declaration. Constructors can set defaults too. This is useful when some fields are always initialized to specific values.

---

## Slide 33: Overloaded Constructors Pattern
**Visual:** Show progression from specific to general constructor

Common pattern: create specific constructors, then a general one.
```java
public Person(String name) {
    this(name, 0);
}

public Person(String name, int age) {
    this.name = name;
    this.age = age;
}
```

The specific constructor delegates to the general one. This is DRY (Don't Repeat Yourself)—you write field initialization once.

---

## Slide 34: Class Organization Best Practices
**Visual:** Show class layout: fields at top, constructor, methods below

Conventional class structure:
1. Fields (usually private)
2. Constructor(s)
3. Public methods (getters, then other methods)
4. Private helper methods

This consistent structure makes classes easier to read.

---

## Slide 35: Method Naming Conventions
**Visual:** Examples of good naming: `getName()`, `calculateTotal()`, `isValid()`

Conventions for readability:
- Getters start with `get`: `getName()`, `getBalance()`
- Boolean queries start with `is`: `isActive()`, `isEmpty()`
- Actions are verbs: `deposit()`, `withdraw()`, `calculateTax()`
- Method names use camelCase

Following conventions makes your code predictable for other developers.

---

## Slide 36: Modeling Real-World Objects
**Visual:** Show Car class design: fields (make, model, color, speed) and methods (accelerate, brake, honk)

Practice: identify key data and behavior.

Car object:
- **Data**: make, model, color, currentSpeed, fuelLevel
- **Behavior**: accelerate(), brake(), refuel(), honk()

When designing a class, ask: "What data defines this object? What actions can it perform?" This leads to thoughtful OOP design.

---

## Slide 37: Objects as Method Parameters
**Visual:** Show method receiving an object as parameter

```java
public void transferOwnership(Person newOwner) {
    this.owner = newOwner;
}

BankAccount account = new BankAccount(...);
Person newPerson = new Person("Bob", 30);
account.transferOwnership(newPerson);
```

Methods can accept objects. You pass a reference; the method can read/modify that object.

---

## Slide 38: Objects as Return Values
**Visual:** Method creating and returning a new object

```java
public Person clone() {
    return new Person(this.name, this.age);
}

Person original = new Person("Alice", 25);
Person copy = original.clone();
```

Methods can create and return new objects. This is powerful for factory methods and object transformation.

---

## Slide 39: Introduction to UML Class Diagrams
**Visual:** Simple UML diagram showing Person class

UML notation helps visualize classes:
```
+------------------+
|      Person      |
+------------------+
| -name: String    |
| -age: int        |
+------------------+
| +getName(): String|
| +getAge(): int    |
| +celebrate...(): void
+------------------+
```

Plus sign = public, minus sign = private. This visual language communicates class design.

---

## Slide 40: Recap: Classes and Objects
**Visual:** Summary of key concepts

- **Class**: Blueprint for objects
- **Object**: Instance of a class
- **Fields**: Data (state) of an object
- **Methods**: Actions (behavior) of an object
- **Constructor**: Initializes objects
- **new**: Keyword to create objects
- **this**: Reference to current object

These are the foundations. Part 2 builds on this with access control and static members.

---

## Slide 41: Preview: What's Coming Next?
**Visual:** Teaser for Part 2 topics

In Part 2, we'll explore:
- **Access modifiers**: public, private, protected, package-private—controlling visibility
- **Static members**: class-level fields and methods
- **The `this` keyword**: deeper usage patterns
- **Non-access modifiers**: final, abstract

These allow you to write more controlled, organized code. Ready? Let's continue!

---
