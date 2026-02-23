# Week 1 - Day 5 (Friday) Part 1: Inheritance, Overriding & Polymorphism
## Advanced OOP: Building Class Hierarchies and Polymorphic Behavior
### Complete 60-Minute Lecture Script

---

## [00:00-02:00] Introduction: Welcome to Part 2

Welcome to the final day of Week 1. Yesterday, you built the foundation: classes, objects, constructors, access modifiers, static members. Today, we build on that foundation with inheritance, method overriding, and polymorphism. These concepts take OOP from a toolset to an art form. Inheritance is about code reuse and modeling hierarchies. Polymorphism is about writing flexible code that works with multiple types. Together, they enable elegant solutions to complex problems. By the end of this day, you'll have a complete understanding of OOP fundamentals. You'll be able to design class hierarchies, leverage polymorphism, and write professional object-oriented code. Let's begin.

---

## [02:00-04:00] The Problem Inheritance Solves: Code Duplication

Imagine you're designing a zoo management system. You have a Dog class: it has a name, an age, a weight. You have methods like eat(), sleep(), and makeSound(). You have a Cat class. It also has name, age, weight. It also has eat(), sleep(), and makeSound(), but with different implementations. You're duplicating code. This is inefficient and error-prone. If you need to change how all animals sleep, you have to change it in every class.

What if you had twenty animal types? The duplication would be massive. Inheritance solves this. You create a parent Animal class with name, age, weight, eat(), and sleep(). Dog and Cat inherit from Animal. They only implement their unique behavior—like Dog has bark(), Cat has scratch(). Now, the common code lives in one place. If you change how animals sleep, you change it once. This is the power of code reuse through inheritance.

---

## [04:00-06:00] Inheritance Fundamentals and the extends Keyword

Inheritance is a mechanism where a class inherits properties and methods from another class. In Java, you use the `extends` keyword.

```java
public class Animal {
    protected String name;
    protected int age;
    
    public void eat() {
        System.out.println(name + " is eating");
    }
    
    public void sleep() {
        System.out.println(name + " is sleeping");
    }
}

public class Dog extends Animal {
    public void bark() {
        System.out.println(name + " says Woof!");
    }
}
```

Here, Dog extends Animal. It inherits name, age, eat(), and sleep(). It adds bark(). Now a Dog object can do everything an animal can do, plus bark.

You create Dog objects the same way:

```java
Dog dog = new Dog();
dog.name = "Buddy";
dog.age = 3;
dog.eat();      // Inherited method
dog.sleep();    // Inherited method
dog.bark();     // Dog's own method
```

The dog can call inherited methods and its own methods. This is inheritance in action.

---

## [06:00-08:00] Superclass and Subclass Terminology

In inheritance, we use specific terminology. The class being extended is called the **superclass**, **base class**, or **parent class**. The class that extends is called the **subclass**, **derived class**, or **child class**. In my example, Animal is the superclass; Dog is the subclass.

A superclass can have multiple subclasses. Animal can have Dog, Cat, Bird, all extending it. But a subclass has only one direct superclass. Dog extends only Animal, not Animal and Pet simultaneously. Java doesn't support multiple inheritance directly (though it has workarounds with interfaces, which we'll cover in Part 2).

Think of a hierarchy. At the top is the most general class. As you go down, classes get more specific. Animal is general. Dog is a specific type of animal. This hierarchy reflects reality and leads to good design.

---

## [08:00-10:00] What Is Inherited and What Is Not

When a subclass extends a superclass, what does it inherit? Let me be clear about what passes down and what doesn't.

**Inherited:**
- Public fields
- Protected fields
- Public methods
- Protected methods

**NOT inherited:**
- Private fields and methods (subclasses can't see them)
- Constructors (you must call them explicitly)

Example:

```java
public class Animal {
    public String name;           // Inherited
    protected int age;            // Inherited
    private String secret;        // NOT inherited
    
    public void eat() { }        // Inherited
    protected void sleep() { }   // Inherited
    private void hideFood() { }  // NOT inherited
}

public class Dog extends Animal {
    public void info() {
        System.out.println(name);     // OK; public
        System.out.println(age);      // OK; protected
        System.out.println(secret);   // ERROR; private
        eat();                        // OK; public method
        sleep();                      // OK; protected method
        hideFood();                   // ERROR; private method
    }
}
```

Private members are hidden even from subclasses. This is intentional—it enforces encapsulation. Subclasses can't violate a class's internal contracts.

---

## [10:00-12:00] The super Keyword: Calling Parent Methods and Constructors

The `super` keyword refers to the parent class. Use it in two main ways.

First, call parent methods:

```java
public class Dog extends Animal {
    @Override
    public void eat() {
        System.out.println("Dog eats dog food");
        super.eat();  // Call parent method
    }
}
```

When you call `super.eat()`, you're explicitly calling the parent's implementation. Then you add your own behavior.

Second, call parent constructors:

```java
public class Dog extends Animal {
    private String breed;
    
    public Dog(String name, int age, String breed) {
        super(name, age);  // Call parent constructor
        this.breed = breed;
    }
}
```

`super(name, age)` calls the parent's constructor. This initializes the inherited fields. Then you initialize the Dog-specific field. This is crucial—the parent must be initialized before the subclass adds its own initialization.

Rule: `super()` must be the first statement in the constructor. You can't do other stuff first.

---

## [12:00-14:00] Constructor Inheritance Rules

Here's something important: constructors are NOT inherited. But subclasses must initialize the parent class. Every subclass constructor must call a parent constructor, either explicitly or implicitly.

If the parent has a no-arg constructor, you can omit the call. Java calls it implicitly:

```java
public class Animal {
    public Animal() {
        System.out.println("Animal created");
    }
}

public class Dog extends Animal {
    public Dog() {
        // super() is called implicitly
    }
}

Dog dog = new Dog();
// Prints "Animal created"
```

But if the parent has only a parameterized constructor, you MUST call it explicitly:

```java
public class Animal {
    private String name;
    
    public Animal(String name) {
        this.name = name;
    }
}

public class Dog extends Animal {
    private String breed;
    
    public Dog(String name, String breed) {
        super(name);  // MUST call parent
        this.breed = breed;
    }
}
```

If you forget `super(name)`, the compiler gives an error. Java requires parent initialization.

---

## [14:00-16:00] Method Overriding: Custom Behavior in Subclasses

Method overriding is when a subclass provides its own implementation of a method defined in the parent. The signature must match exactly—same name, same parameters, same return type.

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

public class Cat extends Animal {
    @Override
    public void makeSound() {
        System.out.println("Meow!");
    }
}
```

Dog and Cat both override `makeSound()`. When you call the method on a Dog object, Dog's version runs. When you call it on a Cat object, Cat's version runs. Simple concept, powerful outcome.

The `@Override` annotation is optional but highly recommended. It tells the compiler you intend to override. If you misspell the method name, the compiler catches the error. Without `@Override`, you might accidentally create a new method instead of overriding, and you wouldn't notice.

---

## [16:00-18:00] Method Overriding: When and How

When should you override a method? When the subclass needs different behavior than the parent's default. If the parent's implementation is fine for a subclass, don't override—just use the inherited method.

When you override, you have choices. You can completely replace the parent's behavior:

```java
@Override
public void eat() {
    System.out.println("Dog eats kibble");
}
```

Or you can extend the parent's behavior, calling the parent's implementation and adding your own:

```java
@Override
public void eat() {
    super.eat();  // Parent behavior first
    System.out.println("Dog's tail wags");  // Then dog-specific behavior
}
```

Choose based on what makes sense. If the parent's behavior is a good foundation, build on it with `super`. If you want completely different behavior, replace it entirely.

---

## [18:00-20:00] Method Overloading vs Method Overriding: The Crucial Difference

This is a common source of confusion. Overloading and overriding sound similar but are entirely different concepts.

**Method Overloading**: Same class, same method name, DIFFERENT parameters. Resolved at compile-time.

```java
public class Math {
    public int add(int a, int b) { return a + b; }
    public double add(double a, double b) { return a + b; }
    public int add(int a, int b, int c) { return a + b + c; }
}
```

Same method name, different parameter types/counts. When you call `add()`, the compiler looks at the arguments and picks the right version.

**Method Overriding**: Parent and child classes, same method name, SAME parameters. Resolved at runtime.

```java
public class Animal {
    public void makeSound() { }
}

public class Dog extends Animal {
    @Override
    public void makeSound() { }  // Same signature as parent
}
```

When you call `makeSound()` on a Dog, the runtime looks at the actual object type and calls Dog's version, not Animal's.

Different concepts, different purposes. Overloading is about having multiple methods with the same name for convenience. Overriding is about customizing inherited methods. Don't confuse them.

---

## [20:00-22:00] Introduction to Polymorphism

Polymorphism (Greek for "many forms") is the ability to treat objects of different types through a common interface. This is where OOP's power really shows.

Here's the key idea: a reference to a parent class can point to an object of any subclass.

```java
Animal animal1 = new Dog();
Animal animal2 = new Cat();
Animal animal3 = new Bird();
```

`animal1`, `animal2`, and `animal3` are all typed as Animal, but they actually reference Dog, Cat, and Bird objects. This is legal in Java because Dog is an Animal (through inheritance), Cat is an Animal, Bird is an Animal.

Now, when you call a method:

```java
animal1.makeSound();  // Calls Dog.makeSound()
animal2.makeSound();  // Calls Cat.makeSound()
animal3.makeSound();  // Calls Bird.makeSound()
```

Same call, different behavior. The actual type of the object determines which method runs. This is runtime polymorphism—the decision about which method to call is made at runtime, not at compile-time.

---

## [22:00-24:00] Runtime Polymorphism (Dynamic Dispatch)

Let me explain runtime polymorphism more deeply because it's crucial. When you write:

```java
Animal animal = new Dog();
animal.makeSound();
```

What method gets called? The compiler doesn't know `animal` is actually a Dog—it only sees the type Animal. But at runtime, Java looks at the actual object type. It sees "this is a Dog." It calls Dog's `makeSound()` method, not Animal's.

This is called dynamic dispatch or late binding. The decision is made at runtime, not compile-time. This flexibility is incredibly powerful.

Consider this scenario:

```java
public void feedAnimals(List<Animal> animals) {
    for (Animal animal : animals) {
        animal.eat();  // Each calls its own eat() method
    }
}

List<Animal> animals = new ArrayList<>();
animals.add(new Dog());
animals.add(new Cat());
animals.add(new Bird());

feedAnimals(animals);
```

The `feedAnimals()` method doesn't know or care what specific types are in the list. It just calls `eat()` on each. Each object's actual type determines the behavior. This is polymorphism's elegance. You write one method that works with many types.

---

## [24:00-26:00] Compile-Time Polymorphism (Method Overloading)

Not all polymorphism is runtime. Compile-time polymorphism happens through method overloading. The compiler determines which method to call based on the arguments.

```java
public class Printer {
    public void print(String text) { System.out.println(text); }
    public void print(int number) { System.out.println(number); }
    public void print(double decimal) { System.out.println(decimal); }
}

Printer printer = new Printer();
printer.print("Hello");     // Calls print(String)
printer.print(42);          // Calls print(int)
printer.print(3.14);        // Calls print(double)
```

At compile-time, the compiler sees the types of the arguments and picks the matching method. If you pass a String, it calls the String version. If you pass an int, it calls the int version. The compiler resolves this at compile-time, not runtime.

Both compile-time and runtime polymorphism are powerful. Overloading adds flexibility and convenience. Overriding enables code reuse and extensibility.

---

## [26:00-28:00] Polymorphism with Collections: A Practical Example

Polymorphism truly shines with collections. Imagine you have a list of various animals. You don't know the exact types. You just want to make them all sound off. Polymorphism makes this trivial.

```java
List<Animal> zoo = new ArrayList<>();
zoo.add(new Dog());
zoo.add(new Cat());
zoo.add(new Bird());
zoo.add(new Dog());
zoo.add(new Cat());

for (Animal animal : zoo) {
    animal.makeSound();
}
```

You don't check the type. You don't cast. You just call the method. Each animal behaves according to its type. This is the polymorphic way of thinking. Objects know how to behave; you just give commands.

This is powerful because you can add new animal types later without changing this code. If you add a Elephant class, it fits right in. The code doesn't care about new types; it just calls methods.

---

## [28:00-30:00] Type Casting: Upcasting and Downcasting

Sometimes you have a parent reference but need the subclass type. You use casting.

**Upcasting** is converting a subclass reference to a parent reference. This is always safe:

```java
Dog dog = new Dog();
Animal animal = dog;  // Upcasting; always works
```

You don't even need the cast syntax explicitly. Java does it automatically.

**Downcasting** is converting a parent reference back to a subclass reference. This requires a cast and can be risky:

```java
Animal animal = new Dog();
Dog dog = (Dog) animal;  // Downcasting; works because animal is actually a Dog
```

The cast operator `(Dog)` tells Java "convert this Animal reference to a Dog reference." But what if the animal isn't actually a Dog?

```java
Animal animal = new Cat();
Dog dog = (Dog) animal;  // Runtime error! ClassCastException
```

You get a ClassCastException. The object is a Cat, not a Dog. You can't cast it to Dog. This is a runtime error, not a compile-time error—Java can't know the actual type until runtime.

To avoid this, use `instanceof` before downcasting:

```java
Animal animal = zoo.get(0);  // Unknown type

if (animal instanceof Dog) {
    Dog dog = (Dog) animal;
    dog.bark();  // Dog-specific method
}
```

Check first, then cast. This is safe.

---

## [30:00-32:00] The instanceof Operator

The `instanceof` operator checks if an object is an instance of a particular class (including subclasses):

```java
Animal animal = new Dog();
System.out.println(animal instanceof Dog);     // true; animal is actually a Dog
System.out.println(animal instanceof Animal);  // true; Dog is-a Animal
System.out.println(animal instanceof Cat);     // false; animal is not a Cat
```

Use `instanceof` to check types before downcasting. It prevents ClassCastExceptions and makes code safe.

```java
if (animal instanceof Dog) {
    Dog dog = (Dog) animal;
    dog.bark();
} else if (animal instanceof Cat) {
    Cat cat = (Cat) animal;
    cat.scratch();
} else if (animal instanceof Bird) {
    Bird bird = (Bird) animal;
    bird.fly();
}
```

This pattern is common: check the type, cast, call type-specific methods. It's safe and clear.

---

## [32:00-34:00] Real-World Example: Shape Hierarchy

Let me show a realistic example that ties everything together. We'll create a shape system with different types of shapes.

```java
public class Shape {
    public double area() { return 0; }
    public double perimeter() { return 0; }
}

public class Circle extends Shape {
    private double radius;
    
    public Circle(double radius) {
        this.radius = radius;
    }
    
    @Override
    public double area() {
        return Math.PI * radius * radius;
    }
    
    @Override
    public double perimeter() {
        return 2 * Math.PI * radius;
    }
}

public class Rectangle extends Shape {
    private double width, height;
    
    public Rectangle(double width, double height) {
        this.width = width;
        this.height = height;
    }
    
    @Override
    public double area() {
        return width * height;
    }
    
    @Override
    public double perimeter() {
        return 2 * (width + height);
    }
}
```

Different shapes, different calculations for area and perimeter. Now use them polymorphically:

```java
List<Shape> shapes = new ArrayList<>();
shapes.add(new Circle(5));
shapes.add(new Rectangle(4, 6));
shapes.add(new Circle(3));

double totalArea = 0;
for (Shape shape : shapes) {
    totalArea += shape.area();
}

System.out.println("Total area: " + totalArea);
```

You don't know what each shape is. You just call `area()`. Each shape calculates correctly. This is polymorphism in action.

---

## [34:00-36:00] Common Beginner Mistakes: Overriding vs Overloading

A frequent mistake is confusing overriding and overloading.

Don't do this:

```java
public class Animal {
    public void makeSound() { }
}

public class Dog extends Animal {
    public void makeSounds() { }  // Typo! Different method name
}
```

This is neither overriding nor overloading; it's a new method. The Dog has `makeSound()` (inherited from Animal) and `makeSounds()` (its own). They're different.

The compiler doesn't help here unless you use `@Override`:

```java
@Override
public void makeSounds() { }  // Compiler error! No such method in parent
```

`@Override` catches the mistake. Always use it.

Or do this:

```java
public class Dog extends Animal {
    @Override
    public void makeSound() { }  // Correct; same signature as parent
}
```

Same name, same parameters, same return type. This is overriding.

---

## [36:00-38:00] Common Beginner Mistakes: Forgetting super() in Constructor

Another mistake is forgetting to call the parent constructor:

```java
public class Dog extends Animal {
    private String breed;
    
    public Dog(String name, int age, String breed) {
        this.breed = breed;  // Forgot super()!
    }
}
```

If Animal has a parameterized constructor, you MUST call it. The compiler will error: "Cannot find symbol: constructor Animal()". Java requires parent initialization.

Always call `super()`:

```java
public Dog(String name, int age, String breed) {
    super(name, age);  // Call parent first
    this.breed = breed;
}
```

---

## [38:00-40:00] Common Beginner Mistakes: Downcasting Without Check

Another mistake is downcasting without checking:

```java
Animal animal = zoo.get(0);  // Unknown type
Dog dog = (Dog) animal;      // Might crash!
dog.bark();
```

If `animal` isn't a Dog, you get a ClassCastException. Always check first:

```java
if (animal instanceof Dog) {
    Dog dog = (Dog) animal;
    dog.bark();
}
```

Safe. Always verify before downcasting.

---

## [40:00-42:00] Access Modifiers with Inheritance

Remember access modifiers from Day 4? They interact with inheritance. Let me clarify:

- **public**: Accessible everywhere, including subclasses ✓
- **protected**: Accessible within the same package and from subclasses ✓
- **private**: NOT accessible from subclasses ✗
- **package-private**: Accessible within the same package only

```java
public class Animal {
    public String name;           // Subclass can access
    protected int age;            // Subclass can access
    private String secret;        // Subclass cannot access
}

public class Dog extends Animal {
    public void info() {
        System.out.println(name);    // OK; public
        System.out.println(age);     // OK; protected
        System.out.println(secret);  // ERROR; private
    }
}
```

This is why `protected` exists—to allow subclass access while hiding from the world. Private fields remain private even to subclasses. This enforces encapsulation.

---

## [42:00-44:00] The Object Class: Root of All Classes

In Java, every class ultimately extends Object, even if you don't say so explicitly. Object is the superclass of all classes. When you write:

```java
public class Dog { }
```

Java treats it as:

```java
public class Dog extends Object { }
```

Object provides methods like:
- `toString()`: Returns a string representation
- `equals(Object obj)`: Checks equality
- `hashCode()`: Returns a hash code
- `getClass()`: Returns the class type
- `clone()`: Creates a copy

You can (and should) override these methods in your classes. Since all objects inherit from Object, all objects have these methods.

---

## [44:00-46:00] Overriding toString() and equals()

Two important methods from Object that you often override:

**toString():**

```java
public class Dog extends Animal {
    @Override
    public String toString() {
        return "Dog{name='" + name + "', breed='" + breed + "'}";
    }
}

Dog dog = new Dog("Buddy", "Golden Retriever");
System.out.println(dog);  // Dog{name='Buddy', breed='Golden Retriever'}
```

By default, `toString()` returns a memory address. Override it for something useful. This is essential for debugging.

**equals():**

```java
public class Dog extends Animal {
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Dog)) return false;
        Dog other = (Dog) obj;
        return this.name.equals(other.name) && this.breed.equals(other.breed);
    }
}

Dog dog1 = new Dog("Buddy", "Golden Retriever");
Dog dog2 = new Dog("Buddy", "Golden Retriever");
System.out.println(dog1.equals(dog2));  // true (if properly overridden)
```

Notice the `instanceof` check and downcasting. This is a standard pattern. Check the type, cast, then compare.

---

## [46:00-48:00] Final Classes and Final Methods

The `final` keyword prevents extension or override. Use it when you want to lock down behavior.

On a class:
```java
public final class ImmutableClass { }
public class Subclass extends ImmutableClass { }  // ERROR!
```

You can't extend a final class. String is final—you can't create a StringPlus class that extends it.

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

You can't override a final method. Use `final` when you want to guarantee behavior doesn't change in subclasses.

---

## [48:00-50:00] Method Resolution Order: Which Method Gets Called?

With multi-level hierarchies, which method runs when you call it? Java searches up the hierarchy.

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
dog.move();  // Which method runs?
```

Java searches: Dog → Mammal → Animal. It finds `move()` in Mammal and uses that. The search starts at the actual type and goes up until it finds the method. The first match wins.

---

## [50:00-52:00] Liskov Substitution Principle (SOLID)

A key principle in OOP design: **A subclass should be substitutable for its superclass.** If your code works with an Animal, it should work with any Animal subclass.

```java
public void feedAnimal(Animal animal) {
    animal.eat();  // Works for any Animal subclass
}

feedAnimal(new Dog());    // OK
feedAnimal(new Cat());    // OK
feedAnimal(new Bird());   // OK
```

This principle guides good inheritance. If a subclass violates the parent's contract, the hierarchy is broken. Design hierarchies so subclasses can be used anywhere the parent is expected.

---

## [52:00-54:00] When NOT to Use Inheritance: Composition

Sometimes, inheritance seems tempting but composition is better. Composition means one class has another class as a field.

❌ Don't do this:
```java
public class Engine { }
public class Car extends Engine { }  // Car is-a Engine? No!
```

✓ Do this instead:
```java
public class Engine { }
public class Car {
    private Engine engine;  // Car has-a Engine
}
```

Use inheritance for is-a relationships (Dog is-a Animal). Use composition for has-a relationships (Car has-a Engine). This principle leads to flexible, maintainable code.

---

## [54:00-56:00] Hierarchy Depth and Breadth

Be thoughtful about hierarchy structure.

**Shallow, wide hierarchies** are usually good:
```
       Animal
      / | | \
    Dog Cat Bird Fish
```

Easy to understand, easy to maintain.

**Deep, narrow hierarchies** can be problematic:
```
Vehicle → Car → SportsCar → RaceCar → Formula1Car → ...
```

Each level adds complexity. You have to understand many levels to grasp behavior. Generally, keep hierarchies shallow (2-3 levels) and wide (many children).

---

## [56:00-58:00] Recap: Part 1 Concepts

Let me recap what we've covered in Part 1:

- **Inheritance**: Subclass extends superclass for code reuse
- **super**: Reference to parent class
- **Method overriding**: Replace parent method in subclass
- **Method overloading**: Same name, different params (same class)
- **Polymorphism**: Treat objects as parent type; runtime dispatch
- **Casting**: Upcasting (always safe) vs downcasting (check first)
- **instanceof**: Type checking
- **Access modifiers**: Control visibility in inheritance
- **Object class**: Root of all classes
- **Composition vs Inheritance**: Choose the right tool

These are your tools for building hierarchies and leveraging polymorphism.

---

## [58:00-60:00] Preview: Part 2 and Looking Forward

In Part 2, we'll add abstraction to your toolkit. Abstract classes define incomplete templates. Interfaces define contracts. These are even more powerful than basic inheritance. Combined with what you know, they enable professional OOP design.

By the end of today, you'll have a complete understanding of OOP fundamentals. You'll be able to design systems, build hierarchies, and write flexible code. You're progressing rapidly. See you in Part 2!

---
