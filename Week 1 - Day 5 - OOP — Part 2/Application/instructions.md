# Day 5 Application — OOP Part 2: Animal Kingdom

## Overview

You'll build an **Animal Kingdom** simulation — a Java application that models a hierarchy of animals using abstract classes, interfaces, inheritance, and polymorphism. You'll see how the same method call (`makeSound()`) produces different results at runtime depending on the actual object type.

---

## Learning Goals

- Implement inheritance hierarchies with `extends`
- Use `super` to call parent constructors and methods
- Override methods and apply runtime polymorphism
- Overload methods at compile time
- Create abstract classes with abstract and concrete methods
- Define and implement interfaces
- Organize classes into packages

---

## Project Structure

```
starter-code/
└── src/
    ├── Main.java
    ├── animals/
    │   ├── Animal.java          ← TODO: abstract base class
    │   ├── Mammal.java          ← TODO: extends Animal
    │   ├── Bird.java            ← TODO: extends Animal
    │   └── Reptile.java         ← TODO: extends Animal
    ├── behaviors/
    │   ├── Swimmable.java       ← TODO: interface
    │   ├── Flyable.java         ← TODO: interface
    │   └── Trainable.java       ← TODO: interface
    └── specific/
        ├── Dog.java             ← TODO: Mammal + Swimmable + Trainable
        ├── Eagle.java           ← TODO: Bird + Flyable
        ├── Duck.java            ← TODO: Bird + Swimmable + Flyable
        └── Crocodile.java       ← TODO: Reptile + Swimmable
```

---

## Part 1 — Abstract Base Class

**Task 1 — `Animal.java` (abstract)**
Fields: `private String name`, `private int age`, `private String habitat`

- Parameterized constructor
- Getters for all fields
- **Abstract method:** `makeSound()` — returns `String` — subclasses MUST implement this
- **Abstract method:** `move()` — returns `String`
- **Concrete method:** `describe()` — returns `"[Name] is a [age]-year-old [className] from [habitat]"` — uses `this.getClass().getSimpleName()` for class name
- **Overloaded method:** `describe(boolean verbose)` — if verbose, also appends the sound and movement description

**Task 2 — Intermediate classes**
- `Mammal` — adds `furColor` field, parameterized constructor calling `super()`
- `Bird` — adds `canFly` (boolean) field
- `Reptile` — adds `isVenomous` (boolean) field

Each must implement `makeSound()` and `move()` at a basic level (can be overridden).

---

## Part 2 — Interfaces

**Task 3 — Interfaces with default methods**
- `Swimmable` — abstract method `swim()` returns String; default method `diveDepth()` returns `"Shallow dive"`
- `Flyable` — abstract method `fly()` returns String; default method `maxAltitude()` returns `"Low altitude"`
- `Trainable` — abstract method `train(String command)` returns String; default method `getSkillLevel()` returns `"Beginner"`

---

## Part 3 — Concrete Animal Classes

**Task 4 — Implement the 4 specific animals**
Each class must:
- Extend the appropriate intermediate class
- Implement the appropriate interface(s)
- Override `makeSound()` and `move()` with specific behavior
- Have a constructor chaining up with `super()`

For example, `Dog` extends `Mammal`, implements `Swimmable` and `Trainable`.

---

## Part 4 — Polymorphism in `Main.java`

**Task 5 — Polymorphic array**
Declare `Animal[] animals = new Animal[4]` and store one of each concrete type. Loop over the array calling `describe()` and `makeSound()` — observe runtime polymorphism.

**Task 6 — Interface references**
Declare a `Swimmable[] swimmers` array containing all animals that can swim. Call `swim()` on each.

**Task 7 — `instanceof` and casting**
Loop over the `animals` array. Use `instanceof` to check if each is `Trainable`. If so, cast and call `train("sit")`.

---

## Stretch Goals

1. Add a `PetStore` class with an `ArrayList<Animal>` and methods to add, find by name, and list by type using `instanceof`.
2. Create a `Parrot` class that extends `Bird`, implements `Flyable` and `Trainable`, and overrides `train()` to return a string that repeats the command.
3. Add a `compareTo` pattern — give `Animal` a `compareAge(Animal other)` method.

---

## Submission Checklist

- [ ] Abstract class with abstract and concrete methods
- [ ] At least 3 levels of inheritance (Animal → Mammal → Dog)
- [ ] `super()` constructor call in every subclass
- [ ] Method overriding demonstrated (different output, same method name)
- [ ] Method overloading demonstrated (`describe()` / `describe(boolean)`)
- [ ] At least 2 interfaces implemented
- [ ] Default interface method used
- [ ] Polymorphic array of type `Animal[]` holding subclass instances
- [ ] `instanceof` check and downcast demonstrated
