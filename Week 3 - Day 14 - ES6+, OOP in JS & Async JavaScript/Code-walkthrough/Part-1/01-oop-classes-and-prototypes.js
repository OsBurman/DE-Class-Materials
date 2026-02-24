// =============================================================================
// DAY 14 — ES6+, OOP in JS & Async JavaScript
// FILE 1: Classes, Prototypes & Object-Oriented Patterns
// =============================================================================
// Run with Node.js:  node 01-oop-classes-and-prototypes.js
// =============================================================================

"use strict";

// =============================================================================
// SECTION 1: PROTOTYPES & THE PROTOTYPE CHAIN
// =============================================================================
// Before classes existed in JavaScript, objects inherited from other objects
// through a mechanism called the PROTOTYPE CHAIN.
// Every object has an internal link ([[Prototype]]) pointing to another object.
// When you access a property, JavaScript walks UP the chain until it finds it
// or reaches null (the end of the chain).
console.log("=== SECTION 1: Prototype Chain ===");

// Every regular object's [[Prototype]] is Object.prototype
const student = { name: "Alice", grade: "A" };

console.log(student.name);                   // own property
console.log(student.toString());             // inherited from Object.prototype
console.log(Object.getPrototypeOf(student) === Object.prototype); // true

// Manually setting up prototype-based inheritance:
const animal = {
  type: "Animal",
  describe() {
    return `I am a ${this.type} named ${this.name}`;
  },
};

const dog = Object.create(animal);  // dog's [[Prototype]] = animal
dog.name = "Rex";
dog.type = "Dog";
dog.bark = function () {
  return "Woof!";
};

console.log(dog.describe());        // found on animal via chain: "I am a Dog named Rex"
console.log(dog.bark());            // own method
console.log(Object.getPrototypeOf(dog) === animal); // true

// Visualising the chain:
// dog → animal → Object.prototype → null
//
// Property lookup order:
//   1. Own properties of dog
//   2. Properties of animal (dog's [[Prototype]])
//   3. Properties of Object.prototype
//   4. null → undefined (not found)

// hasOwnProperty — check whether it's truly the object's own, not inherited
console.log(dog.hasOwnProperty("bark"));    // true  (own)
console.log(dog.hasOwnProperty("describe")); // false (inherited)


// =============================================================================
// SECTION 2: CONSTRUCTOR FUNCTIONS (Pre-ES6 Classes)
// =============================================================================
// Constructor functions are regular functions called with `new`.
// The `new` keyword creates a fresh object, sets `this` to it,
// links its [[Prototype]] to the constructor's `.prototype`, and returns it.
console.log("\n=== SECTION 2: Constructor Functions ===");

function Course(title, instructor, credits) {
  // `this` is the new object being built
  this.title      = title;
  this.instructor = instructor;
  this.credits    = credits;
  this.students   = [];
}

// Methods should go on the PROTOTYPE, not inside the constructor.
// If you put them inside, every instance gets its own copy — wasteful.
Course.prototype.enroll = function (studentName) {
  this.students.push(studentName);
  console.log(`${studentName} enrolled in ${this.title}`);
};

Course.prototype.describe = function () {
  return `"${this.title}" by ${this.instructor} (${this.credits} credits)`;
};

const jsCourse  = new Course("JavaScript Fundamentals", "Alice Smith", 3);
const javaCourse = new Course("Core Java", "Bob Jones", 4);

jsCourse.enroll("Dave");
jsCourse.enroll("Eve");
console.log(jsCourse.describe());
console.log(`Students: ${jsCourse.students.join(", ")}`);

// All instances share the same prototype methods (memory efficient):
console.log(jsCourse.enroll === javaCourse.enroll); // true — same function ref

// Prototype-based inheritance with constructor functions:
function OnlineCourse(title, instructor, credits, platform) {
  Course.call(this, title, instructor, credits); // call parent constructor
  this.platform = platform;
}

// Link the prototype chain:
OnlineCourse.prototype = Object.create(Course.prototype);
OnlineCourse.prototype.constructor = OnlineCourse; // fix constructor pointer

OnlineCourse.prototype.getLiveLink = function () {
  return `${this.platform}/courses/${this.title.toLowerCase().replace(/ /g, "-")}`;
};

const reactCourse = new OnlineCourse("React Fundamentals", "Carol Lee", 3, "Udemy");
reactCourse.enroll("Frank");        // inherited from Course.prototype
console.log(reactCourse.getLiveLink()); // own method
console.log(reactCourse instanceof OnlineCourse); // true
console.log(reactCourse instanceof Course);       // true (prototype chain)


// =============================================================================
// SECTION 3: ES6 CLASS SYNTAX
// =============================================================================
// Classes are SYNTACTIC SUGAR over the prototype system above.
// They do not introduce a new OOP model — just cleaner syntax.
console.log("\n=== SECTION 3: ES6 Class Syntax ===");

class Animal {
  // ── Constructor ───────────────────────────────────────────────────────
  constructor(name, species) {
    this.name    = name;
    this.species = species;
    this._energy = 100;   // convention: _ prefix means "treat as private"
  }

  // ── Instance methods — placed on Animal.prototype automatically ───────
  eat(food) {
    this._energy += 20;
    return `${this.name} eats ${food}. Energy: ${this._energy}`;
  }

  sleep() {
    this._energy += 30;
    return `${this.name} sleeps. Energy: ${this._energy}`;
  }

  describe() {
    return `${this.name} is a ${this.species}`;
  }

  // ── Getter / Setter ───────────────────────────────────────────────────
  get energy() {
    return this._energy;
  }

  set energy(value) {
    if (value < 0) throw new RangeError("Energy cannot be negative");
    this._energy = value;
  }

  // ── Static method — called on the class, NOT on instances ────────────
  static create(name, species) {
    return new Animal(name, species);
  }

  static compare(a, b) {
    return a.name.localeCompare(b.name);
  }

  toString() {
    return this.describe();
  }
}

const cat = new Animal("Whiskers", "Cat");
console.log(cat.eat("tuna"));
console.log(cat.sleep());
console.log(cat.energy);       // getter
cat.energy = 50;               // setter
console.log(`Energy set to: ${cat.energy}`);

const dog2 = Animal.create("Buddy", "Dog");  // static factory
console.log(dog2.describe());

// Verify it's still prototype-based under the hood:
console.log(typeof Animal);          // "function"  ← classes ARE functions
console.log(Animal.prototype.eat === cat.eat); // true — on the prototype


// =============================================================================
// SECTION 4: INHERITANCE WITH extends & super
// =============================================================================
console.log("\n=== SECTION 4: Inheritance — extends & super ===");

class Dog extends Animal {
  // ── Constructor MUST call super() before using `this` ─────────────────
  constructor(name, breed) {
    super(name, "Dog");       // calls Animal constructor; sets name & species
    this.breed = breed;
    this.tricks = [];
  }

  // ── Override parent method ─────────────────────────────────────────────
  describe() {
    return `${super.describe()} (${this.breed})`;  // super.method() calls parent
  }

  // ── New method in subclass ─────────────────────────────────────────────
  learnTrick(trick) {
    this.tricks.push(trick);
    return `${this.name} learned: ${trick}`;
  }

  bark() {
    return `${this.name}: Woof!`;
  }
}

class GoldenRetriever extends Dog {
  constructor(name) {
    super(name, "Golden Retriever");  // Dog constructor → Animal constructor
    this.isTherapyDog = false;
  }

  fetch(item) {
    return `${this.name} fetches the ${item}!`;
  }

  // Override learnTrick from Dog:
  learnTrick(trick) {
    const result = super.learnTrick(trick);        // call Dog.learnTrick
    return `${result} 🐾 (Golden learns fast!)`;
  }
}

const rex = new Dog("Rex", "German Shepherd");
console.log(rex.describe());
console.log(rex.learnTrick("sit"));
console.log(rex.bark());

const buddy = new GoldenRetriever("Buddy");
console.log(buddy.fetch("ball"));
console.log(buddy.learnTrick("high five"));

// instanceof checks the prototype chain:
console.log(buddy instanceof GoldenRetriever); // true
console.log(buddy instanceof Dog);             // true
console.log(buddy instanceof Animal);          // true


// =============================================================================
// SECTION 5: PRIVATE FIELDS & METHODS (ES2022)
// =============================================================================
// Real private — enforced by the engine, not just convention.
// Private fields are declared with # prefix.
console.log("\n=== SECTION 5: Private Fields ===");

class BankAccount {
  #balance;         // private field — only accessible inside the class
  #transactionLog;  // private field

  constructor(owner, initialDeposit = 0) {
    this.owner         = owner;
    this.#balance      = initialDeposit;
    this.#transactionLog = [];
    this.#recordTransaction("opened", initialDeposit);
  }

  #recordTransaction(type, amount) {  // private method
    this.#transactionLog.push({
      type,
      amount,
      date: new Date().toISOString().slice(0, 10),
    });
  }

  deposit(amount) {
    if (amount <= 0) throw new RangeError("Deposit must be positive");
    this.#balance += amount;
    this.#recordTransaction("deposit", amount);
    return `Deposited £${amount}. Balance: £${this.#balance}`;
  }

  withdraw(amount) {
    if (amount > this.#balance) throw new Error("Insufficient funds");
    this.#balance -= amount;
    this.#recordTransaction("withdrawal", amount);
    return `Withdrew £${amount}. Balance: £${this.#balance}`;
  }

  get balance() {
    return this.#balance;           // expose via getter
  }

  getStatement() {
    return this.#transactionLog
      .map(t => `  [${t.date}] ${t.type.padEnd(12)} £${t.amount}`)
      .join("\n");
  }
}

const acct = new BankAccount("Alice", 500);
console.log(acct.deposit(200));
console.log(acct.withdraw(75));
console.log(`Balance: £${acct.balance}`);
console.log("Statement:\n" + acct.getStatement());

// console.log(acct.#balance);  // ← SyntaxError: Cannot access private field


// =============================================================================
// SECTION 6: OOP PATTERNS IN JAVASCRIPT
// =============================================================================
console.log("\n=== SECTION 6: OOP Patterns ===");

// ── 6a. Factory Pattern ───────────────────────────────────────────────────
// A factory function returns a new object without needing `new` or a class.
// Good for creating objects with private state (via closures).
function createStudent(name, track) {
  let _completedModules = 0;  // private via closure

  return {
    name,
    track,
    completeModule() {
      _completedModules++;
      return `${name} completed module ${_completedModules}`;
    },
    getProgress() {
      return { name, track, completedModules: _completedModules };
    },
  };
}

const student1 = createStudent("Dave", "React");
console.log(student1.completeModule());
console.log(student1.completeModule());
console.log(student1.getProgress());

// ── 6b. Mixin Pattern ────────────────────────────────────────────────────
// Mixins add capabilities to classes without inheritance.
// Useful when you want to share behaviour across unrelated classes.
const Serializable = (Base) => class extends Base {
  serialize() {
    return JSON.stringify(this);
  }

  static deserialize(json) {
    return Object.assign(new this(), JSON.parse(json));
  }
};

const Timestamped = (Base) => class extends Base {
  constructor(...args) {
    super(...args);
    this.createdAt = new Date().toISOString();
    this.updatedAt = new Date().toISOString();
  }

  touch() {
    this.updatedAt = new Date().toISOString();
  }
};

class BaseEntity {
  constructor(id) {
    this.id = id;
  }
}

class User extends Timestamped(Serializable(BaseEntity)) {
  constructor(id, username, email) {
    super(id);
    this.username = username;
    this.email    = email;
  }
}

const user = new User(1, "alice_dev", "alice@bootcamp.dev");
console.log("User:", user.username, "| created:", user.createdAt.slice(0, 10));
const json = user.serialize();
console.log("Serialized:", json.slice(0, 60) + "...");

// ── 6c. Singleton Pattern ────────────────────────────────────────────────
// Ensures only ONE instance of a class ever exists.
// Useful for global configuration, caches, connection pools.
class AppConfig {
  static #instance = null;

  #settings;

  constructor() {
    if (AppConfig.#instance) {
      return AppConfig.#instance;   // return existing instance
    }
    this.#settings = {
      theme: "light",
      language: "en",
      apiBaseUrl: "https://api.bootcamp.dev",
    };
    AppConfig.#instance = this;
  }

  get(key) {
    return this.#settings[key];
  }

  set(key, value) {
    this.#settings[key] = value;
  }
}

const config1 = new AppConfig();
const config2 = new AppConfig();

config1.set("theme", "dark");
console.log("config2 theme:", config2.get("theme")); // "dark" — same instance
console.log("Same instance?", config1 === config2);   // true
