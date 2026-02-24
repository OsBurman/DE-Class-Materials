// Exercise 01 Solution: Constructor Functions, Classes, and Prototypal Inheritance

// ── PART A: Constructor Functions ─────────────────────────────────────────────

// Requirement 1: Constructor function — sets instance properties
function AnimalConstructor(name, sound) {
  this.name  = name;
  this.sound = sound;
}

// Requirement 2: Method on the shared prototype (not duplicated per instance)
AnimalConstructor.prototype.speak = function() {
  console.log(`${this.name} says ${this.sound}!`);
};

// Requirement 3: DogConstructor borrows AnimalConstructor's initialisation
function DogConstructor(name, sound, breed) {
  AnimalConstructor.call(this, name, sound); // sets this.name and this.sound
  this.breed = breed;
}

// Requirement 4: Link DogConstructor into the prototype chain
// Object.create creates an object whose __proto__ is AnimalConstructor.prototype
DogConstructor.prototype = Object.create(AnimalConstructor.prototype);
DogConstructor.prototype.constructor = DogConstructor; // restore the constructor reference

DogConstructor.prototype.fetch = function() {
  console.log(`${this.name} fetches the ball!`);
};

// Requirement 5
const cat = new AnimalConstructor('Cat', 'Meow');
cat.speak(); // Cat says Meow!

// Requirement 6
const rex = new DogConstructor('Rex', 'Woof', 'Labrador');
rex.speak();         // Rex says Woof!
rex.fetch();         // Rex fetches the ball!
console.log(rex.breed); // Labrador


// ── PART B: ES6 Class Syntax ──────────────────────────────────────────────────

// Requirement 7: class syntax — same prototype mechanics, cleaner surface
class Animal {
  constructor(name, sound) {
    this.name  = name;
    this.sound = sound;
  }
  speak() {
    console.log(`${this.name} says ${this.sound}!`);
  }
}

// extends sets up the prototype chain automatically; super() calls the parent constructor
class Dog extends Animal {
  constructor(name, sound, breed) {
    super(name, sound); // must call super before using `this`
    this.breed = breed;
  }
  fetch() {
    console.log(`${this.name} fetches the ball!`);
  }
}

// Requirement 8
const buddy = new Dog('Buddy', 'Bark', 'Beagle');
buddy.speak();          // Buddy says Bark!
buddy.fetch();          // Buddy fetches the ball!
console.log(buddy.breed); // Beagle

// Requirement 9: instanceof checks the prototype chain
console.log(`Dog instance is instanceof Animal: ${buddy instanceof Animal}`); // true
console.log(`Dog instance is instanceof Dog: ${buddy instanceof Dog}`);       // true

// Requirement 10: class `extends` wires Dog.prototype.__proto__ to Animal.prototype
console.log(`Prototype chain correct: ${Object.getPrototypeOf(Dog.prototype) === Animal.prototype}`); // true
