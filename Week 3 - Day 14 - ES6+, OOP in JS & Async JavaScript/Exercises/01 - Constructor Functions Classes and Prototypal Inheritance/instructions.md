# Exercise 01: Constructor Functions, Classes, and Prototypal Inheritance

## Objective
Practice writing JavaScript objects using both the older constructor-function style and the modern ES6 `class` syntax, and understand how the prototype chain enables inheritance between objects.

## Background
JavaScript's object system is prototype-based: every object has a hidden link (`__proto__`) to another object called its prototype, and property lookups "walk the chain" until they find a match or reach `null`. ES6 `class` syntax is syntactic sugar over this mechanism — it doesn't change how JavaScript works underneath, but it makes OOP patterns much easier to read and write.

## Requirements
1. In `script.js`, create a **constructor function** called `AnimalConstructor` that accepts `name` (string) and `sound` (string) parameters and sets them as properties.
2. Add a method `speak()` to `AnimalConstructor.prototype` that logs `"[name] says [sound]!"`.
3. Create a constructor function `DogConstructor` that calls `AnimalConstructor` with `super`-style delegation (use `AnimalConstructor.call(this, name, sound)`) and adds a `breed` property.
4. Set up `DogConstructor`'s prototype chain so that `DogConstructor.prototype` inherits from `AnimalConstructor.prototype` (use `Object.create`). Add a method `fetch()` to `DogConstructor.prototype` that logs `"[name] fetches the ball!"`.
5. Create an instance of `AnimalConstructor` (name: `"Cat"`, sound: `"Meow"`) and call `speak()`.
6. Create an instance of `DogConstructor` (name: `"Rex"`, sound: `"Woof"`, breed: `"Labrador"`) and call both `speak()` and `fetch()`. Log the breed.
7. Now re-implement the **same hierarchy using ES6 `class` syntax**: create a `class Animal` with a `constructor(name, sound)` and a `speak()` method; create a `class Dog extends Animal` with a `constructor(name, sound, breed)` that calls `super(name, sound)` and a `fetch()` method.
8. Create an instance of the `Dog` class (name: `"Buddy"`, sound: `"Bark"`, breed: `"Beagle"`). Call `speak()`, `fetch()`, and log `buddy.breed`.
9. Use `instanceof` to verify: log whether the Dog class instance is `instanceof Animal` and `instanceof Dog`.
10. Log `Object.getPrototypeOf(Dog.prototype) === Animal.prototype` to confirm the prototype chain is set up correctly.

## Hints
- `Object.create(AnimalConstructor.prototype)` creates a blank object whose `__proto__` is `AnimalConstructor.prototype` — assign this to `DogConstructor.prototype` to link the chain.
- When you re-assign `DogConstructor.prototype`, reset `DogConstructor.prototype.constructor = DogConstructor` to avoid confusion.
- ES6 `class` handles `super()`, prototype linking, and `constructor` assignment automatically — notice how much shorter the class version is compared to the manual version.
- `instanceof` checks whether the constructor's `prototype` appears anywhere in the instance's prototype chain.

## Expected Output

```
Cat says Meow!
Woof says Woof!
Rex fetches the ball!
Labrador
Buddy says Bark!
Buddy fetches the ball!
Beagle
Dog instance is instanceof Animal: true
Dog instance is instanceof Dog: true
Prototype chain correct: true
```
