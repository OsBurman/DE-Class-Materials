# Week 1 - Day 5 (Friday) Part 1: Inheritance, Overriding & Polymorphism
## Advanced OOP: Building Class Hierarchies and Polymorphic Behavior

---

## Slide 1: Welcome to Day 5: Advanced OOP
**Visual:** Banner showing "Day 5: OOP Part 2" with inheritance hierarchy diagram

Welcome back! Yesterday, you learned the fundamentals: classes, objects, constructors, access modifiers, and static members. Today, we take OOP to the next level. We're learning inheritance—how classes can extend other classes and reuse their behavior. We're learning polymorphism—treating objects of different types through a common interface. We're learning method overriding—customizing inherited methods. These concepts separate novice from professional programmers. They enable code reuse, flexible designs, and elegant solutions to complex problems. Let's dive in.

---

## Slide 2: The Problem: Code Duplication
**Visual:** Show two separate classes with duplicate methods; highlight redundancy

Imagine you're designing a zoo management system. You have a `Dog` class with properties like `name`, `age`, `weight`. You have a `Cat` class with the same properties. Both have a `makeSound()` method (which you'll implement differently). Both have `eat()` and `sleep()` methods. You're duplicating code. This violates the DRY principle—Don't Repeat Yourself. Inheritance solves this. You create a parent `Animal` class with common properties and methods. `Dog` and `Cat` inherit from it and only add specific behaviors. That's the power of inheritance.

---

## Slide 3: Inheritance Fundamentals
**Visual:** Diagram showing Animal as parent, Dog and Cat as children; show "extends" relationship

Inheritance is a mechanism where a class (called a subclass or child class) inherits properties and methods from another class (called a superclass or parent class). In Java, you use the `extends` keyword.

```java
public class Animal {
    protected String name;
    protected int age;
    
    public void eat() { }
    public void sleep() { }
}

public class Dog extends Animal {
    public void bark() { }
}
```

`Dog` extends `Animal`. It inherits `name`, `age`, `eat()`, and `sleep()`. It adds its own method `bark()`. `Dog` objects have all the behavior of animals plus dog-specific behavior.

---

## Slide 4: Is-A Relationship
**Visual:** Show "Dog IS-A Animal", "Cat IS-A Animal", "Bird IS-A Animal"; illustrate semantic meaning

Inheritance represents an "is-a" relationship. A `Dog` is an `Animal`. A `Cat` is an `Animal`. A `Circle` is a `Shape`. This relationship should be meaningful. Don't create arbitrary hierarchies. If it doesn't make semantic sense to say "X is a Y," then X shouldn't extend Y. An inheritance hierarchy should reflect reality.

---

## Slide 5: Superclass vs Subclass
**Visual:** Family tree metaphor; show superclass at top, subclasses below

The class being extended is the **superclass** (or parent, or base class). The class extending is the **subclass** (or child, or derived class). A superclass can have multiple subclasses. A subclass has only one superclass (Java doesn't support multiple inheritance directly). In a hierarchy, upper classes are more general; lower classes are more specific.

---

## Slide 6: Inherited Members and Properties
**Visual:** Show Dog class accessing inherited fields and methods from Animal

When a subclass extends a superclass, it automatically inherits:
- **Public fields**: Accessible directly
- **Protected fields**: Accessible directly (within the subclass)
- **Public methods**: Can be called on subclass objects
- **Protected methods**: Accessible within the subclass

The subclass does NOT inherit:
- **Private fields/methods**: Not visible to subclasses
- **Constructors**: You must call them explicitly (we'll cover this)

Example:
```java
Dog dog = new Dog();
dog.eat();      // Inherited method from Animal
dog.bark();     // Dog's own method
```

---

## Slide 7: The super Keyword
**Visual:** Show `super.method()` and `super(...)` in code; explain usage

The `super` keyword refers to the parent class. Use it to:
1. **Call parent methods**: `super.eat();`
2. **Call parent constructor**: `super(name, age);`

Example:
```java
public class Dog extends Animal {
    private String breed;
    
    public Dog(String name, int age, String breed) {
        super(name, age);  // Call parent constructor
        this.breed = breed;
    }
    
    @Override
    public void eat() {
        System.out.println("Dog eats dog food.");
        super.eat();  // Call parent method, then add behavior
    }
}
```

`super(name, age)` calls the parent constructor. `super.eat()` calls the parent's `eat()` method.

---

## Slide 8: Constructor Inheritance
**Visual:** Show constructor chain: subclass → superclass initialization

Constructors are NOT inherited, but subclasses must initialize the parent class. Every subclass constructor must call a parent constructor (explicitly or implicitly).

If the parent has a no-arg constructor, you can omit the call—Java calls it implicitly:
```java
public class Animal {
    public Animal() { }
}

public class Dog extends Animal {
    public Dog() { }  // Implicitly calls super()
}
```

If the parent has only a parameterized constructor:
```java
public class Animal {
    public Animal(String name) { }
}

public class Dog extends Animal {
    public Dog(String name) {
        super(name);  // Must explicitly call parent
    }
}
```

You must call a parent constructor; you can't skip it.

---

## Slide 9: Method Overriding
**Visual:** Show parent method and child method with same signature; highlight @Override

Method overriding is when a subclass provides its own implementation of a method defined in the parent class. The signature (name, parameters, return type) must match exactly.

```java
public class Animal {
    public void makeSound() {
        System.out.println("Some generic sound");
    }
}

public class Dog extends Animal {
    @Override
    public void makeSound() {
        System.out.println("Woof!");
    }
}
```

`Dog` overrides `makeSound()`. When you call `dog.makeSound()`, the Dog's version runs, not the Animal's. The `@Override` annotation is optional but recommended—it tells the compiler you intend to override. If you misspell the method name, the compiler catches the error.

---

## Slide 10: When to Override
**Visual:** Show examples: meaningless override vs appropriate override

Override a method when:
- The subclass needs different behavior than the parent
- The subclass specializes the general parent behavior

Example: `Animal.eat()` is generic. `Dog.eat()` is specific. Override makes sense.

Don't override just for the sake of it. If the parent's implementation is fine, don't override.

---

## Slide 11: Super vs This in Overridden Methods
**Visual:** Show calling super.method() alongside this-specific behavior

Often, you override a method but still want some of the parent's behavior. Use `super`:

```java
public class Dog extends Animal {
    @Override
    public void eat() {
        System.out.println("Dog eats dog food");  // Dog-specific
        super.eat();                               // Parent behavior
    }
}
```

Or you might completely replace the parent's behavior:
```java
@Override
public void eat() {
    System.out.println("Dog eats dog food");  // Only dog behavior
}
```

Your choice depends on what makes sense.

---

## Slide 12: Method Overloading vs Method Overriding
**Visual:** Table comparing overloading and overriding

**Overloading** (same class):
- Same method name, DIFFERENT parameters
- Resolved at compile-time
- Example: `add(int, int)` vs `add(double, double)`

**Overriding** (parent-child):
- Same method name, SAME parameters
- Resolved at runtime
- Child's version replaces parent's

Different concepts:
```java
public class Animal {
    public void makeSound() { }  // Override this in subclass
    public void move() { }
}

public class Dog extends Animal {
    @Override
    public void makeSound() { }  // Overriding
    
    public void makeSound(String volume) { }  // Overloading (same class, different params)
}
```

---

## Slide 13: Polymorphism Basics
**Visual:** Show multiple objects treated as the same parent type

Polymorphism (Greek: "many forms") allows objects of different types to be treated as objects of a common parent type. An `Animal` reference can point to a `Dog`, `Cat`, or `Bird` object.

```java
Animal animal1 = new Dog();
Animal animal2 = new Cat();
Animal animal3 = new Bird();

animal1.makeSound();  // Calls Dog.makeSound()
animal2.makeSound();  // Calls Cat.makeSound()
animal3.makeSound();  // Calls Bird.makeSound()
```

Same call, different behavior. The actual type determines which method runs. This is called runtime polymorphism or dynamic dispatch.

---

## Slide 14: Runtime Polymorphism (Dynamic Dispatch)
**Visual:** Runtime decision diagram; show method resolution based on actual object type

At runtime, Java determines which method to call based on the actual object type, not the reference type.

```java
Animal animal = new Dog();  // Reference type: Animal, Actual type: Dog
animal.makeSound();  // Calls Dog.makeSound(), not Animal.makeSound()
```

Even though `animal` is typed as `Animal`, the actual object is a `Dog`. When you call `makeSound()`, Java looks at the actual type (Dog) and calls the Dog's version. This flexibility is powerful. It lets you write code that works with any Animal subclass without knowing the specific type.

---

## Slide 15: Compile-Time Polymorphism (Method Overloading)
**Visual:** Show compile-time decision; method name same, parameters different

Compile-time polymorphism is achieved through method overloading. The compiler determines which method to call based on the arguments.

```java
public class Calculator {
    public int add(int a, int b) { return a + b; }
    public double add(double a, double b) { return a + b; }
    public int add(int a, int b, int c) { return a + b + c; }
}

Calculator calc = new Calculator();
calc.add(5, 3);        // Calls int version
calc.add(5.5, 3.3);    // Calls double version
calc.add(5, 3, 1);     // Calls three-arg version
```

The compiler looks at the argument types and picks the matching method. This decision is made at compile-time, not runtime. That's why it's compile-time polymorphism.

---

## Slide 16: Polymorphism in Collections
**Visual:** ArrayList<Animal> holding Dog, Cat, Bird objects

Polymorphism is powerful with collections:

```java
List<Animal> animals = new ArrayList<>();
animals.add(new Dog());
animals.add(new Cat());
animals.add(new Bird());

for (Animal animal : animals) {
    animal.makeSound();  // Each calls its own version
}
```

You have a list of Animals. You don't know or care what specific type each is. You just call methods. Each object's actual type determines behavior. This is elegant and flexible.

---

## Slide 17: Type Casting in Polymorphism
**Visual:** Show upcasting and downcasting with diagrams

**Upcasting**: Converting a subclass reference to a superclass reference (always safe):
```java
Dog dog = new Dog();
Animal animal = dog;  // Upcasting; always works
```

**Downcasting**: Converting a superclass reference back to a subclass reference (must check first):
```java
Animal animal = new Dog();
Dog dog = (Dog) animal;  // Downcasting; requires cast

Animal animal2 = new Cat();
Dog dog2 = (Dog) animal2;  // Runtime error! animal2 is Cat, not Dog
```

Downcasting requires a cast operator `(Type)`. Be careful—if the actual type doesn't match, you get a ClassCastException. Use `instanceof` to check first:

```java
if (animal instanceof Dog) {
    Dog dog = (Dog) animal;
}
```

---

## Slide 18: The instanceof Operator
**Visual:** Show instanceof checks in conditional logic

`instanceof` checks if an object is an instance of a particular type (including subclasses):

```java
Animal animal = new Dog();
System.out.println(animal instanceof Dog);     // true
System.out.println(animal instanceof Animal);  // true
System.out.println(animal instanceof Cat);     // false
```

Use `instanceof` before downcasting:

```java
if (animal instanceof Dog) {
    Dog dog = (Dog) animal;
    dog.bark();  // Dog-specific method
}
```

This prevents ClassCastExceptions and makes code safe.

---

## Slide 19: Inheritance Hierarchy Example: Vehicle System
**Visual:** Inheritance tree: Vehicle → Car, Truck, Motorcycle; Car → SportsCar

Let's design a vehicle system:

```java
public class Vehicle {
    protected String brand;
    protected int year;
    
    public void start() { System.out.println("Vehicle started"); }
    public void stop() { System.out.println("Vehicle stopped"); }
}

public class Car extends Vehicle {
    private int doors;
    
    @Override
    public void start() { System.out.println("Car engine started"); }
}

public class SportsCar extends Car {
    private int maxSpeed;
    
    @Override
    public void start() { System.out.println("Sports car turbo engaged"); }
}
```

Vehicle is the most general. Car specializes it. SportsCar specializes Car further. This is a multi-level hierarchy.

---

## Slide 20: Using the Vehicle Hierarchy
**Visual:** Collections and polymorphic method calls

```java
List<Vehicle> vehicles = new ArrayList<>();
vehicles.add(new Car());
vehicles.add(new Truck());
vehicles.add(new SportsCar());

for (Vehicle vehicle : vehicles) {
    vehicle.start();  // Each calls its own version
}
```

Output:
```
Car engine started
Truck engine started (if Truck overrides)
Sports car turbo engaged
```

Each vehicle behaves according to its type. Same code, different behavior.

---

## Slide 21: Common Beginner Mistakes: Overriding vs Overloading Confusion
**Visual:** Red X for wrong, green checkmark for correct

❌ Mistake: Confusing override with overload:
```java
public class Parent {
    public void method(int x) { }
}

public class Child extends Parent {
    public void method(String x) { }  // This is overloading, not overriding!
}
```

✓ Correct: Override requires same signature:
```java
@Override
public void method(int x) { }  // This overrides
```

---

## Slide 22: Common Beginner Mistakes: Forgetting @Override
**Visual:** Show typo catching with @Override vs without

❌ Without @Override, typos go unnoticed:
```java
public class Dog extends Animal {
    public void makeSounds() { }  // Typo! Should be makeSound()
}
```

Now you have a new method, not an override. The parent's `makeSound()` still runs when you call it.

✓ With @Override, the compiler catches typos:
```java
@Override
public void makeSounds() { }  // Compiler error: no such method in parent
```

Always use `@Override`. It catches mistakes.

---

## Slide 23: Common Beginner Mistakes: Downcasting Without Check
**Visual:** ClassCastException thrown at runtime

❌ Dangerous:
```java
Animal animal = new Cat();
Dog dog = (Dog) animal;  // Runtime error!
```

✓ Safe:
```java
Animal animal = new Cat();
if (animal instanceof Dog) {
    Dog dog = (Dog) animal;
}
```

Check the type before downcasting. Always.

---

## Slide 24: Access Modifiers and Inheritance
**Visual:** Table showing visibility: public, protected, private across parent-child

- **public**: Accessible everywhere, including subclasses
- **protected**: Accessible within the package and from subclasses
- **private**: NOT accessible from subclasses

```java
public class Animal {
    public String name;           // Subclass can access
    protected int age;            // Subclass can access
    private String secret;        // Subclass cannot access
}

public class Dog extends Animal {
    public void info() {
        System.out.println(name);    // OK
        System.out.println(age);     // OK
        System.out.println(secret);  // ERROR: private
    }
}
```

This is why `protected` exists—to allow subclass access while hiding from the world.

---

## Slide 25: Final Classes and Methods
**Visual:** Show `final` preventing extension or override

The `final` keyword prevents inheritance:

On a class:
```java
public final class ImmutableClass { }
public class Subclass extends ImmutableClass { }  // ERROR!
```

On a method:
```java
public class Parent {
    public final void protectedMethod() { }
}

public class Child extends Parent {
    @Override
    public void protectedMethod() { }  // ERROR!
}
```

Use `final` when you want to prevent subclassing or method override. String is a final class—you can't extend it.

---

## Slide 26: Object Class: The Root of All Classes
**Visual:** Show Object at the top of all inheritance hierarchies

In Java, every class ultimately extends `Object` (even if you don't explicitly say `extends Object`). Object is the superclass of all classes.

```java
public class Animal { }  // Implicitly extends Object
```

Object provides methods like:
- `toString()`: Returns a string representation
- `equals(Object)`: Checks equality
- `hashCode()`: Returns a hash code
- `getClass()`: Returns the class type
- `clone()`: Creates a copy

You can override these methods in your classes. This is why all objects support `.toString()`, `.equals()`, etc.

---

## Slide 27: toString() and equals() Revisited
**Visual:** Show overriding these common methods

Recall from Day 4: you can override `toString()` and `equals()`. These come from Object:

```java
public class Animal {
    private String name;
    
    @Override
    public String toString() {
        return "Animal{name='" + name + "'}";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Animal)) return false;
        Animal other = (Animal) obj;
        return this.name.equals(other.name);
    }
}
```

Downcasting in `equals()` is a standard pattern. Check the type, then cast.

---

## Slide 28: Method Resolution Order
**Visual:** Show which method is called in multi-level hierarchy

With multi-level inheritance, Java searches the hierarchy for a method:

```java
public class Animal {
    public void move() { System.out.println("Animal moves"); }
}

public class Mammal extends Animal {
    @Override
    public void move() { System.out.println("Mammal walks"); }
}

public class Dog extends Mammal { }  // Doesn't override move()

Dog dog = new Dog();
dog.move();  // Calls Mammal.move() (first found in hierarchy)
```

Java searches: Dog → Mammal → Animal. It finds `move()` in Mammal and uses that. This is the method resolution order.

---

## Slide 29: Real-World Example: Shape Hierarchy
**Visual:** Show Shape, Circle, Rectangle, Triangle classes

```java
public abstract class Shape {
    public abstract double area();
    public abstract double perimeter();
}

public class Circle extends Shape {
    private double radius;
    
    @Override
    public double area() { return Math.PI * radius * radius; }
    
    @Override
    public double perimeter() { return 2 * Math.PI * radius; }
}

public class Rectangle extends Shape {
    private double width, height;
    
    @Override
    public double area() { return width * height; }
    
    @Override
    public double perimeter() { return 2 * (width + height); }
}
```

Different shapes, different area/perimeter calculations. Polymorphism lets you treat them uniformly.

---

## Slide 30: Using Shape Polymorphically
**Visual:** Calculate areas of mixed shapes

```java
List<Shape> shapes = new ArrayList<>();
shapes.add(new Circle(5));
shapes.add(new Rectangle(4, 6));
shapes.add(new Circle(3));

double totalArea = 0;
for (Shape shape : shapes) {
    totalArea += shape.area();  // Each calculates its own way
}

System.out.println("Total area: " + totalArea);
```

You don't care what shape each is. You just call `area()`. Each does the right calculation. This is polymorphism's power.

---

## Slide 31: Liskov Substitution Principle (SOLID)
**Visual:** Principle statement; show substitutable subclass example

A key principle: **A subclass should be substitutable for its superclass.** If your code works with an Animal, it should work with any Animal subclass (Dog, Cat, Bird) without modification.

```java
public void feedAnimal(Animal animal) {
    animal.eat();  // Works for any Animal subclass
}

feedAnimal(new Dog());    // OK
feedAnimal(new Cat());    // OK
feedAnimal(new Bird());   // OK
```

This principle guides good inheritance design. If you violate it, your hierarchy is broken. Subclasses that don't fit the parent's contract are problematic.

---

## Slide 32: Single Responsibility in Hierarchies
**Visual:** Show well-structured vs poorly-structured hierarchies

Each class should have one reason to change. Inheritance should represent genuine is-a relationships:

✓ Good:
```java
class Employee { }
class Manager extends Employee { }  // Manager is-a Employee
```

❌ Bad:
```java
class Dog { }
class DogAndCar extends Dog { }  // A dog is NOT a car!
```

Odd hierarchies indicate design problems. Think carefully about inheritance.

---

## Slide 33: Depth vs Breadth of Hierarchies
**Visual:** Show deep (3+ levels) vs wide (many children) hierarchies

Generally:
- **Shallow hierarchies** (1-2 levels): Easy to understand, maintain
- **Deep hierarchies** (3+ levels): Can become complex and hard to follow
- **Wide hierarchies** (many children): More manageable than deep

Prefer shallow, wide hierarchies over deep, narrow ones. Deep hierarchies introduce complexity; you have to understand many levels to grasp behavior.

---

## Slide 34: Composition Over Inheritance
**Visual:** Compare inheritance approach vs composition approach

Sometimes, instead of inheritance, use composition (object has-a another object):

❌ Overusing inheritance:
```java
class Engine { }
class Car extends Engine { }  // Car is-a Engine? No!
```

✓ Better: Composition:
```java
class Engine { }
class Car {
    private Engine engine;  // Car has-a Engine
}
```

Use inheritance for is-a relationships. Use composition for has-a relationships. This leads to more flexible, maintainable code.

---

## Slide 35: Common Design Pattern: Template Method
**Visual:** Show abstract method pattern; concrete methods call abstract methods

The template method pattern defines an algorithm's structure in a base class, leaving details to subclasses:

```java
public abstract class DataProcessor {
    public final void process() {
        readData();
        transformData();
        writeData();
    }
    
    protected abstract void readData();
    protected abstract void transformData();
    protected abstract void writeData();
}

public class CSVProcessor extends DataProcessor {
    @Override
    protected void readData() { /* CSV-specific */ }
    
    @Override
    protected void transformData() { /* CSV-specific */ }
    
    @Override
    protected void writeData() { /* CSV-specific */ }
}
```

The parent defines the skeleton; subclasses fill in the details. Powerful pattern.

---

## Slide 36: Recap: Key Inheritance Concepts
**Visual:** Mind map or checklist of inheritance ideas

- **Inheritance**: Subclass extends superclass
- **super**: Reference to parent class
- **Method overriding**: Replace parent method
- **Method overloading**: Same name, different params
- **Polymorphism**: Treat objects as parent type
- **Dynamic dispatch**: Runtime method resolution
- **instanceof**: Type checking
- **Downcasting**: Subclass reference from superclass
- **Access modifiers**: public, protected, private with inheritance

These are your tools for building hierarchies.

---

## Slide 37: Recap: Runtime vs Compile-Time Polymorphism
**Visual:** Side-by-side comparison

**Compile-Time (Overloading):**
- Multiple methods, same name, different params
- Resolved by compiler based on args
- Example: `add(int, int)` vs `add(double, double)`

**Runtime (Overriding):**
- Subclass overrides superclass method
- Resolved by runtime based on actual object type
- Example: `Dog.makeSound()` vs `Cat.makeSound()`

Both powerful; both important.

---

## Slide 38: Preview: Interfaces Tomorrow
**Visual:** Teaser for Part 2 and beyond

In Part 2, we'll explore abstract classes and interfaces—tools for defining contracts. An interface is like a contract that classes agree to follow. A class can implement multiple interfaces, getting around Java's single-inheritance limitation. These are even more powerful than basic inheritance. Get ready!

---

## Slide 39: Design Principles Summary
**Visual:** SOLID principles teaser

What you've learned enables professional design:
- **DRY**: Reuse code through inheritance
- **Liskov Substitution**: Subclasses are substitutable
- **Single Responsibility**: Each class has one reason to change

These principles lead to maintainable, scalable code. Master them, and you'll be a skilled engineer.

---

## Slide 40: Thinking About Hierarchies
**Visual:** Ask questions: Is this really is-a? Can subclasses substitute?

Before creating a hierarchy, ask:
- Does X really "is-a" Y semantically?
- Can X objects substitute for Y objects?
- Is the hierarchy shallow enough to understand?
- Would composition work better?

Thoughtful design pays dividends. Careless design creates technical debt.

---

## Slide 41: You're Ready for Part 2
**Visual:** Encouragement slide; bridge to Part 2 topics

You now understand inheritance and polymorphism—cornerstones of OOP. You can build class hierarchies, override methods, and leverage polymorphic behavior. In Part 2, we'll add abstraction and interfaces, completing your OOP toolkit. You're progressing rapidly. Great work!

---
