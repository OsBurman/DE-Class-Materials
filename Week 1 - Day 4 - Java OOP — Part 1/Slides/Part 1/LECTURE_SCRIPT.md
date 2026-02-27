# Week 1 - Day 4 (Thursday) Part 1: Classes and Objects
## OOP Fundamentals: Classes, Constructors & Class Members
### Complete 60-Minute Lecture Script

---

## [00:00-02:00] Introduction: Welcome to OOP

Good morning, everyone! Welcome to Part 1 of Object-Oriented Programming. We're at a pivotal point in the course. For the last few days, you've been writing procedural code—functions that act on data, variables scattered about, logic flowing step by step. Today, we fundamentally change that perspective. We're moving into the world of objects, and it's going to transform how you write code.

Object-oriented programming is not just a syntax change; it's a way of thinking about problems. Instead of asking "What are the steps to accomplish this task?" you'll ask "What objects exist in this problem, and how do they interact?" This shift is powerful. By the end of this hour, you'll understand how to design classes, create objects from them, and write code that's more modular, reusable, and closer to how humans naturally think about systems.

So let's get started. Today's focus: classes, constructors, and how to bundle data and behavior into meaningful objects.

---

## [02:00-04:00] Object-Oriented Programming Philosophy

What is object-oriented programming? At its heart, OOP is a way to organize code around entities—objects—that have both data (what they know) and behavior (what they can do). In procedural programming, which you've been doing, you write functions and pass data to them. The function and the data are separate. A function is like a tool; data is like raw materials. You pick up the tool, use it on the material, set it down.

OOP is different. An object bundles data and behavior together. It's like a Lego brick—the brick knows its own shape, its color, how it connects to other bricks. Or think of a car: a car object knows its color, its speed, its fuel level. A car object can accelerate, brake, and honk. The car doesn't require an external function to make it go. The car itself has methods to make it go.

This approach has huge advantages. Code becomes more intuitive because it mirrors the real world. It's easier to maintain because related code is grouped together. It's easier to reuse because you define a class once and create many objects from it. By the time you finish this bootcamp, you'll have built entire applications using OOP principles.

---

## [04:00-06:00] Classes vs Objects

Let me make a critical distinction: a class is not an object. A class is a blueprint. An object is a thing created from that blueprint. If I define a class called `Car`, that's the blueprint—the template. When I create a specific car, say "a red Honda Civic," that's an object, an instance of the `Car` class.

Here's an analogy: a class is like a cookie cutter. The cookie cutter has a particular shape—say, a star. Every cookie made with that cutter will be star-shaped. But each individual cookie is unique. You can bite one and leave the others whole. You can ice one chocolate and another vanilla. Each cookie is an object; the cutter is the class.

In Java, when you write `public class Car { }`, you're defining the blueprint. No car exists yet. When you write `Car myCar = new Car();`, you're creating an actual car object. That object is an instance of the Car class. You can create hundreds of Car objects from the same Car class, and each one is independent. Each has its own color, speed, and fuel level. Changes to one don't affect the others.

---

## [06:00-08:00] Class Anatomy: The Core Components

Every class in Java has the same basic structure. Let me break it down. First, you have the class declaration: `public class Person { }`. The keyword `public` means this class is accessible from anywhere in your program. The keyword `class` tells Java "this is a class." `Person` is the name—conventionally, class names start with a capital letter and use PascalCase.

Inside the braces, you can add several things. First, fields—variables that hold the object's state. If this is a Person class, you might have a `String name` field and an `int age` field. These fields belong to each instance. Every Person object has its own name and age.

Second, you have methods—functions that define the object's behavior. A Person might have a `getName()` method that returns the name, or a `celebrateBirthday()` method that increments the age.

Third, you have a constructor—a special method that runs when you create the object to initialize its state. It ensures that every Person object starts in a valid, initialized state.

Finally, you might have modifiers like `private` and `public` that control who can see these components. We'll dive deep into those in Part 2, but know they exist.

---

## [08:00-10:00] Creating Your First Class

Let's write some actual code. Here's a simple Person class:

```java
public class Person {
    private String name;
    private int age;
}
```

That's a valid class. It has two fields: `name` and `age`. Both are marked `private`, which means only code inside the Person class can access them directly. Other code can't peek at another object's age without permission.

Now, save this code in a file called `Person.java`. That's the convention: one public class per file, and the filename matches the class name. Now, can you create Person objects? Not yet—not easily. We need a constructor. A constructor is a special method that initializes objects when they're created.

---

## [10:00-12:00] Understanding Fields (Instance Variables)

Before we write a constructor, let's really understand fields. Fields are variables that belong to an object. Each object gets its own copy. If I create two Person objects, `alice` and `bob`, each has its own `name` and `age`. `alice.name` is "Alice"; `bob.name` is "Bob". They don't share fields; they're independent.

This is called instance-level data because the data is specific to each instance. The field `name` is not shared by all Persons; it's individual. If I have 10,000 Person objects, I have 10,000 separate name fields in memory.

When you declare a field but don't initialize it, Java assigns a default value. For numeric types like `int`, the default is 0. For `boolean`, it's false. For object types like `String`, it's null. This is important to know. An uninitialized string field is null, not an empty string. If you call a method on it without checking, you'll get a NullPointerException.

---

## [12:00-14:00] Understanding Methods (Instance Methods)

Methods are functions that belong to an object. They define what the object can do. A Person might have a `getName()` method to retrieve the name or a `celebrateBirthday()` method to increment the age. Methods have access to the object's fields automatically.

Here's what I mean:

```java
public class Person {
    private String name;
    private int age;
    
    public String getName() {
        return name;
    }
    
    public void celebrateBirthday() {
        age++;
    }
}
```

In the `getName()` method, I can return `name` directly. I don't need to pass it as a parameter. In `celebrateBirthday()`, I can modify `age` directly. This is the power of methods—they're intimately connected to the object's data.

Methods can have parameters, just like functions. They can return values or return void (nothing). They use the same flow control as you've learned—if statements, loops, everything. The only difference is they have access to fields.

---

## [14:00-16:00] Constructors: The Initializer

Now, the constructor. This is a special method that runs automatically when you create an object. Its job is to set up the object in a valid state. A constructor has no return type—not even void. Its name must match the class name exactly.

Here's an example:

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

This constructor takes two parameters: a name and an age. When you call `new Person("Alice", 30)`, this constructor runs. It assigns the parameter `name` to the field `this.name`, and the parameter `age` to the field `this.age`. The `this` keyword means "this object's field." We'll talk more about `this` in a moment.

After the constructor finishes, you have a fully initialized Person object. This is much better than creating a Person with null fields and hoping someone remembers to initialize them later.

---

## [16:00-18:00] Default Constructor

Here's something important: every class gets a constructor automatically, even if you don't write one. It's called the default constructor, and it takes no parameters. If you write:

```java
public class Person {
}
```

Java secretly gives you:

```java
public class Person {
    public Person() {
    }
}
```

So you can create a Person with `new Person();`. However, the object's fields remain uninitialized—`name` is null, `age` is 0. That might be okay for some classes, but usually you want to initialize properly.

Here's the catch: if you write any constructor yourself, the default one disappears. If you define `public Person(String name, int age)`, you can no longer call `new Person();`. If you want both, you have to write both constructors.

---

## [18:00-20:00] Parameterized Constructors

The constructor I showed earlier is called a parameterized constructor because it takes parameters. You can write multiple parameterized constructors, as long as they have different parameter lists. This is called constructor overloading.

For example:

```java
public class Person {
    private String name;
    private int age;
    
    public Person() {
        this.name = "Unknown";
        this.age = 0;
    }
    
    public Person(String name) {
        this.name = name;
        this.age = 0;
    }
    
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
```

Now you can create Person objects in three ways: `new Person()`, `new Person("Bob")`, or `new Person("Bob", 25)`. Java looks at the arguments you provide and picks the matching constructor. This flexibility is useful.

---

## [20:00-22:00] Constructor Chaining with this()

Now, look at that code I just showed. Notice how each constructor repeats field initialization? The no-arg constructor sets name and age. The one-arg constructor also sets age. That's code duplication, which we want to avoid.

To reduce duplication, you can have one constructor call another using `this()`:

```java
public class Person {
    private String name;
    private int age;
    
    public Person() {
        this("Unknown", 0);
    }
    
    public Person(String name) {
        this(name, 0);
    }
    
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
```

Now the no-arg constructor calls `this("Unknown", 0)`, which delegates to the two-arg constructor. The one-arg constructor calls `this(name, 0)`, also delegating. Only the two-arg constructor does the actual initialization. This follows DRY—Don't Repeat Yourself.

There's a rule: `this()` must be the first statement in the constructor. You can't do other stuff first.

---

## [22:00-24:00] The `this` Keyword

Let me explain `this` more fully because it's important. `this` is a keyword that refers to the current object. When you're inside a method or constructor, `this` points to the object that method belongs to.

For example:

```java
public Person(String name, int age) {
    this.name = name;
    this.age = age;
}
```

`this.name` means "the name field of this object." `name` (without `this`) refers to the parameter. They have different meanings. If you write `name = name;`, you're assigning the parameter to itself, which does nothing. `this.name = name;` assigns the parameter to the field.

Why would the parameter and field have the same name? Because it's clear and self-documenting. When someone reads the constructor, they immediately understand: the name parameter sets the name field. It's more intuitive than `this.name = n;` where you have to guess that `n` is the name.

You use `this` whenever you need to disambiguate between fields and local variables or parameters with the same name. Some people use `this` everywhere, even when unnecessary, just for consistency. Others use it sparingly. Either way is fine; consistency matters most.

---

## [24:00-26:00] Object Creation: The new Keyword

When you write `Person p = new Person("Charlie", 28);`, what happens? Let's break it down step by step.

First, `new` is a keyword that allocates memory. Java asks the operating system: "I need space for a Person object." The OS grants that space on the heap, and Java gets the address of that memory.

Second, the constructor runs. The constructor has code: `this.name = name;` and `this.age = age;`. That code runs, initializing the fields with the values you passed.

Third, the reference is assigned to the variable. The variable `p` holds a reference (an address), not the object itself. So `p` is pointing to that space on the heap where the Person object lives.

The variable `p` lives on the stack. The Person object lives on the heap. They're separate. The variable points to the object.

This is crucial: `p` is not the object. `p` refers to the object. You can have multiple variables refer to the same object. They're like multiple names for the same entity.

---

## [26:00-28:00] Object Creation Flow in Detail

Let me trace through the full flow of `Person p = new Person("Diana", 32);`:

1. **Declaration**: Stack allocates space for variable `p`. It's not yet initialized.

2. **new Keyword**: Heap allocates memory for a Person object. Let's say it's at address 0x1000.

3. **Field Defaults**: The Person object is created with default field values. `name = null`, `age = 0`.

4. **Constructor Execution**: The constructor runs with arguments "Diana" and 32. It executes `this.name = "Diana";` and `this.age = 32;`. Now the object on the heap has `name = "Diana"` and `age = 32`.

5. **Reference Assignment**: The reference 0x1000 is assigned to `p`. So `p` now holds 0x1000.

6. **Usage**: You can now call `p.getName()`, `p.celebrateBirthday()`, etc.

After this line completes, `p` is a valid reference to a fully initialized Person object.

---

## [28:00-30:00] Creating Multiple Independent Objects

Here's the beauty of classes:

```java
Person alice = new Person("Alice", 25);
Person bob = new Person("Bob", 30);
Person charlie = new Person("Charlie", 22);
```

Each line creates a new object on the heap. Each object has its own fields. `alice.age` is 25, `bob.age` is 30, `charlie.age` is 22. They're independent. If I call `alice.celebrateBirthday();`, only alice's age changes. Bob and Charlie are unaffected.

This is the power of OOP. Define the class once, create many objects with different data. Imagine a bank system. Define a BankAccount class once. Then create thousands of BankAccount objects, one for each customer. Each account has its own balance, account number, and owner. They're all different objects, but all follow the same pattern.

---

## [30:00-32:00] Accessing Fields and Methods via Dot Notation

To interact with an object, use the dot operator: `object.field` or `object.method()`.

```java
Person p = new Person("Eve", 28);
String name = p.getName();
p.celebrateBirthday();
```

`p.getName()` calls the `getName()` method on the object `p` references. The method returns a String, which is assigned to `name`.

`p.celebrateBirthday()` calls the `celebrateBirthday()` method on object `p`. If the method is void, the call stands alone as a statement.

If a field is public (and we generally advise against this), you can access it directly: `p.age = 30;`. But best practice is to make fields private and use methods instead. This gives you control and flexibility.

---

## [32:00-34:00] Getters and Setters: Encapsulation

A common pattern is getter and setter methods:

```java
public class Person {
    private String name;
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
}
```

The field is private. The getter method (`getName()`) returns the field. The setter method (`setName(String name)`) sets the field. Why do this? Because it gives you control. You could add validation:

```java
public void setName(String name) {
    if (name != null && !name.isEmpty()) {
        this.name = name;
    }
}
```

Now, code using Person can't set an invalid name. Or you could add logging:

```java
public void setName(String name) {
    System.out.println("Name changed from " + this.name + " to " + name);
    this.name = name;
}
```

Now every name change is logged. This flexibility is why encapsulation matters. It's a design pattern that protects your data.

---

## [34:00-36:00] Real-World Example: BankAccount Class

Let me show you a real-world example that ties everything together:

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
        if (amount > 0) {
            balance += amount;
        }
    }
    
    public void withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
        }
    }
    
    public double getBalance() {
        return balance;
    }
}
```

This class has fields: account number, balance, and account holder. It has a constructor that initializes all three. It has methods: deposit, withdraw, and getBalance. The constructor and methods validate inputs (amount must be positive, withdrawal can't exceed balance). This is a real, useful class.

Using it:

```java
BankAccount account = new BankAccount("ACC123", "John Doe", 1000);
account.deposit(500);
account.withdraw(200);
double current = account.getBalance();  // 1300
```

The account object maintains state. After deposit, the balance is 1500. After withdrawal, it's 1300. The object remembers. This is state persistence, a hallmark of OOP.

---

## [36:00-38:00] Common Beginner Mistakes

Let me warn you about mistakes I see repeatedly.

**Mistake 1: Forgetting `new`.** You write `Person p = Person("Alice", 25);`. Java gives an error: you can't call a constructor without `new`. Always use `new` to create objects.

**Mistake 2: Mismatched parameter and field names.** You write `public Person(String n, int a) { name = n; age = a; }`. It works, but it's confusing. Which parameter is which? Better: `public Person(String name, int age) { this.name = name; this.age = age; }`. The names match, making the code self-explanatory.

**Mistake 3: Forgetting to initialize fields.** You create a Person but don't initialize name. Later, you call `person.getName()`. It returns null. You print it, and you see "null" in your output. Or worse, you try to call a method on that name, expecting a String, and you get a NullPointerException. Always initialize fields in constructors or provide defaults.

**Mistake 4: Misunderstanding references.** You think `Person p = new Person("Alice", 25);` means `p` is the person. No, `p` is a reference to the person. It's a pointer, an address. Multiple variables can point to the same object. Don't think of `p` as the object itself.

---

## [38:00-40:00] Object State and Identity

Let me deepen your understanding of what an object is. An object is a collection of state plus behavior. The state is its fields. The behavior is its methods. When you create an object, the state is initialized by the constructor. Over time, the state changes. A BankAccount's balance changes as you deposit and withdraw. A Person's age changes as years pass.

This is fundamentally different from functions, which are stateless. A function like `add(int a, int b)` doesn't remember anything. Call it with 5 and 3 today, it returns 8. Call it with 5 and 3 tomorrow, it returns 8. It has no memory. An object is the opposite—it has memory. It's a stateful entity.

Understanding this distinction is key. When you design a class, you're deciding: What state does this object have? What operations change or query that state? Once you think in those terms, object-oriented design becomes natural.

---

## [40:00-42:00] Object References and Aliases

Here's a subtle but important point:

```java
Person p1 = new Person("Alice", 25);
Person p2 = p1;

p2.celebrateBirthday();
System.out.println(p1.age);  // What does this print?
```

`p1` and `p2` are both references to the same object. When you call `p2.celebrateBirthday()`, you're modifying the object that both p1 and p2 reference. So `p1.age` is also 26. They're aliases—two names for one entity.

This is a source of confusion. Beginners think "I copied p1 into p2, so they're independent." No. You didn't copy the object; you copied the reference. p1 and p2 point to the same object in memory. Changes through one are visible through the other.

If you want a true independent copy, you have to create a new object:

```java
Person p1 = new Person("Alice", 25);
Person p2 = new Person(p1.getName(), p1.getAge());
```

Now p1 and p2 are separate objects with the same initial values. Changes to one don't affect the other.

---

## [42:00-44:00] Instance Methods vs Static Methods (Preview)

I want to give you a preview of something we'll cover in Part 2: the difference between instance methods and static methods. What I've shown you so far—methods that operate on the object's fields—are instance methods. They belong to an object.

There are also static methods that belong to the class, not to any particular object. You don't call them on an object; you call them on the class:

```java
Person p = new Person("Alice", 25);
p.getName();  // Instance method; called on object

Math.abs(-5);  // Static method; called on class
```

`getName()` is an instance method; `abs()` is a static method. We'll dive deep into this in Part 2, but it's good to know the distinction exists.

---

## [44:00-46:00] Method Return Values

Methods can return different types:

- **void**: No return value. `public void celebrateBirthday() { age++; }`
- **Primitives**: `public int getAge() { return age; }`
- **Objects**: `public String getName() { return name; }`
- **Arrays**: `public int[] getScores() { return scores; }`

When a method returns a value, that value replaces the method call in your code. `int age = person.getAge();` calls the method, receives the return value, and assigns it to `age`. If the method returns void, you don't assign the result to anything; you call it for its side effects (like printing).

---

## [46:00-48:00] Method Parameters and Passing Objects

Methods can accept parameters, including objects:

```java
public void transferOwnership(Person newOwner) {
    this.owner = newOwner;
}
```

When you pass an object to a method, you're passing a reference, not a copy. The method receives a reference to the same object. If the method modifies the object, those modifications are visible outside the method.

Similarly, methods can return objects:

```java
public Person clone() {
    return new Person(this.name, this.age);
}
```

This method creates a new Person object with the same name and age and returns a reference to it. The caller receives that reference.

---

## [48:00-50:00] Designing Classes: Thinking in Objects

Now that you understand the mechanics, let me talk about the art. How do you design a class? How do you decide what fields and methods it needs?

Start with the real-world entity or concept. What data defines it? For a Person, that's name and age. For a BankAccount, that's account number, balance, and owner. For a Car, that's make, model, color, current speed, fuel level.

Then ask: What actions can this entity perform? What operations are meaningful? A Person can age. A BankAccount can accept deposits and withdrawals. A Car can accelerate and brake.

Write those down. They're your fields and methods. This exercise is called noun and verb analysis. Nouns become classes and fields; verbs become methods.

---

## [50:00-52:00] Class Organization and Conventions

By convention, classes have a standard layout:

1. **Fields** (usually private, listed first)
2. **Constructors** (next)
3. **Public methods** (getters first, then other methods)
4. **Private helper methods** (at the end)

This consistent structure makes classes easier to read. When you open a Java file, you know where to look for what. Fields are at the top. Constructors are next. Public interface is in the middle. Private helpers are at the end.

---

## [52:00-54:00] Method Naming Conventions

Follow conventions for method names:

- **Getters**: `get` prefix. `getName()`, `getBalance()`, `getAge()`.
- **Boolean queries**: `is` prefix. `isActive()`, `isEmpty()`, `isValid()`.
- **Actions**: verb form. `deposit()`, `withdraw()`, `accelerate()`, `brake()`.
- **Casing**: camelCase (first letter lowercase, subsequent words uppercase).

These conventions make code predictable. Another developer reading your code immediately understands what a method does based on its name.

---

## [54:00-56:00] UML Class Diagrams

Let me introduce a useful visual tool: UML class diagrams. UML is a standard notation for representing software design. A simple UML diagram for the Person class looks like:

```
+------------------+
|      Person      |
+------------------+
| -name: String    |
| -age: int        |
+------------------+
| +Person(String, int)
| +getName(): String
| +getAge(): int
| +celebrateBirthday(): void
+------------------+
```

The plus sign indicates public; the minus sign indicates private. This visual representation communicates the class structure at a glance. As you build more complex systems, UML diagrams help you visualize and communicate design.

---

## [56:00-58:00] Recap and Key Takeaways

Let me summarize what we've covered:

- A **class** is a blueprint; an **object** is an instance of that blueprint.
- **Fields** are variables that hold an object's state.
- **Methods** are functions that define an object's behavior.
- A **constructor** is a special method that initializes an object.
- **new** is the keyword that creates objects.
- **this** refers to the current object.
- **Encapsulation** is bundling data and behavior together.
- **Getters and setters** control access to fields.
- **State persistence** means objects remember data over time.

These are the fundamentals. Everything else in OOP builds on these ideas. Classes and objects are the atoms of object-oriented design. Once you're comfortable with them, the rest becomes manageable.

---

## [58:00-60:00] What's Next and Preparation

In Part 2, we'll take what you've learned and add layers of control. We'll explore access modifiers—public, private, protected—which let you fine-tune who can access what. We'll learn about static members—fields and methods that belong to the class, not to objects. We'll dive deeper into the `this` keyword and its uses. And we'll introduce abstract classes and interfaces, which are advanced class designs.

Between now and Part 2, I encourage you to practice. Design a class or two. A Student class with name, ID, GPA. A BankAccount class like we discussed. A Car class. Write constructors, getters, setters. Create objects and manipulate them. The best way to internalize OOP is to build with it. Write code, make mistakes, learn from them.

You're on the path to becoming a software engineer. Object-oriented thinking is a superpower. Keep practicing. See you in Part 2!

---
