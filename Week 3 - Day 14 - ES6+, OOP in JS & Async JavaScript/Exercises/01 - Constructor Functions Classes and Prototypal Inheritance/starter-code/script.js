// Exercise 01: Constructor Functions, Classes, and Prototypal Inheritance

// ── PART A: Constructor Functions ─────────────────────────────────────────────

// TODO: Requirement 1 — define a constructor function AnimalConstructor(name, sound)
//       that sets this.name and this.sound

// TODO: Requirement 2 — add a speak() method to AnimalConstructor.prototype
//       that logs "[name] says [sound]!"

// TODO: Requirement 3 — define a constructor function DogConstructor(name, sound, breed)
//       that delegates to AnimalConstructor using AnimalConstructor.call(this, name, sound)
//       and sets this.breed

// TODO: Requirement 4 — set DogConstructor.prototype to a new object that inherits from
//       AnimalConstructor.prototype using Object.create(...)
//       Then reset DogConstructor.prototype.constructor = DogConstructor
//       Then add a fetch() method to DogConstructor.prototype that logs "[name] fetches the ball!"

// TODO: Requirement 5 — create an AnimalConstructor instance (name: "Cat", sound: "Meow")
//       and call speak()

// TODO: Requirement 6 — create a DogConstructor instance (name: "Rex", sound: "Woof", breed: "Labrador")
//       call speak(), call fetch(), and log the breed


// ── PART B: ES6 Class Syntax ──────────────────────────────────────────────────

// TODO: Requirement 7 — create a class Animal with constructor(name, sound) and a speak() method

// TODO: Requirement 7 — create a class Dog that extends Animal with:
//       constructor(name, sound, breed) calling super(name, sound) and setting this.breed
//       a fetch() method that logs "[name] fetches the ball!"

// TODO: Requirement 8 — create a Dog instance: name "Buddy", sound "Bark", breed "Beagle"
//       Call speak(), fetch(), and log buddy.breed

// TODO: Requirement 9 — log whether the buddy instance is instanceof Animal and instanceof Dog
//       Format: "Dog instance is instanceof Animal: true/false"

// TODO: Requirement 10 — log: Object.getPrototypeOf(Dog.prototype) === Animal.prototype
//        Format: "Prototype chain correct: true/false"
