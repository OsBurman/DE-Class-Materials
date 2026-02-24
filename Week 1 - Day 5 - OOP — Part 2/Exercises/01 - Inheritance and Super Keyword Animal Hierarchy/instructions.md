# Exercise 01: Inheritance and Super Keyword Animal Hierarchy

## Objective
Practice creating class hierarchies using `extends` and calling parent constructors and methods using the `super` keyword.

## Background
A wildlife sanctuary tracks different types of animals. All animals share common attributes like a name and age, but each species has unique behaviors and traits. You'll model this using Java inheritance to avoid duplicating common code across every animal class.

## Requirements

1. Create a base class `Animal` with:
   - Private fields: `String name`, `int age`
   - A constructor that takes `name` and `age` and assigns them
   - A getter for each field (`getName()`, `getAge()`)
   - A method `String describe()` that returns: `"[name] (age [age])"` — e.g., `"Leo (age 5)"`
   - A method `String sound()` that returns `"..."`

2. Create a subclass `Dog` that extends `Animal` with:
   - An additional private field: `String breed`
   - A constructor that takes `name`, `age`, and `breed` — use `super(name, age)` to initialize the parent
   - A getter `getBreed()`
   - Override `sound()` to return `"Woof!"`
   - Override `describe()` to return: `"[name] the [breed] (age [age])"` — call `super.describe()` to reuse the parent's age/name portion, **or** build the string using the parent getters

3. Create a subclass `Cat` that extends `Animal` with:
   - An additional private field: `boolean isIndoor`
   - A constructor that takes `name`, `age`, and `isIndoor` — use `super(name, age)`
   - A getter `isIndoor()`
   - Override `sound()` to return `"Meow!"`
   - Override `describe()` to return: `"[name] (age [age]) - [Indoor/Outdoor] cat"`

4. Create a subclass `Bird` that extends `Animal` with:
   - An additional private field: `String species`
   - A constructor that takes `name`, `age`, and `species` — use `super(name, age)`
   - Override `sound()` to return `"Tweet!"`
   - Override `describe()` to return: `"[name] the [species] (age [age])"`

5. In `main`:
   - Create at least one instance of each (`Dog`, `Cat`, `Bird`)
   - For each animal, print its `describe()` output and its `sound()`
   - Demonstrate calling `super`'s version of `describe()` by printing the parent's description for the `Dog` instance using an `Animal` reference

## Hints
- Use `super(name, age)` as the **first line** of each subclass constructor — Java requires this
- To call the overridden parent method from inside the child, use `super.describe()`
- You can assign a `Dog` object to an `Animal` variable — the object is still a `Dog`; calling `describe()` through an `Animal` reference will call the `Dog`'s overridden version (runtime polymorphism)
- Private fields in the parent are NOT directly accessible in the child — use the parent's getters

## Expected Output

```
=== Animal Hierarchy Demo ===

Dog:
Rex the Labrador (age 3)
Rex says: Woof!

Cat:
Whiskers (age 7) - Indoor cat
Whiskers says: Meow!

Bird:
Tweety the Canary (age 2)
Tweety says: Tweet!

Animal reference pointing to Dog:
Rex the Labrador (age 3)
```
