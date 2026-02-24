// Exercise 03: Object Types, Interfaces, Type Aliases, and Interface vs Type — SOLUTION

// ── PART A: Interfaces ────────────────────────────────────────────────────────

// interface supports optional properties (?) and method signatures
interface Person {
  name: string;
  age: number;
  email?: string;       // optional
  greet(): string;
}

const bob: Person = {
  name: "Bob",
  age: 25,
  greet() { return `Hello, my name is ${this.name}`; }
};

const alice: Person = {
  name: "Alice",
  age: 30,
  email: "alice@example.com",
  greet() { return `Hello, my name is ${this.name}`; }
};

console.log("Person without email:", { name: bob.name, age: bob.age });
console.log("Person with email:", { name: alice.name, age: alice.age, email: alice.email });

// extends lets interface inherit all properties of another interface
interface Employee extends Person {
  company: string;
  salary: number;
}

const carol: Employee = {
  name: "Carol",
  age: 28,
  company: "Acme",
  salary: 75000,
  greet() { return `Hello, my name is ${this.name}`; }
};

console.log("Employee:", { name: carol.name, age: carol.age, company: carol.company, salary: carol.salary });

console.log("greet:", alice.greet());

// ── PART B: Type Aliases ──────────────────────────────────────────────────────

type Coordinate = { x: number; y: number };

const coordA: Coordinate = { x: 1, y: 2 };
const coordB: Coordinate = { x: 5, y: 10 };
console.log("Coordinate A:", coordA);
console.log("Coordinate B:", coordB);

// & (intersection) combines two types into one
type Point3D = Coordinate & { z: number };

const pt: Point3D = { x: 3, y: 4, z: 7 };
console.log("Point3D:", pt);

// ── PART C: Interface vs Type ─────────────────────────────────────────────────

// Declaration merging: two interface Animal declarations are merged by TypeScript
interface Animal {
  name: string;
}
interface Animal {
  sound: string;
}

const dog: Animal = { name: "Dog", sound: "Woof" };
console.log("Merged Animal:", dog);

// type aliases cannot be re-declared:
// type MyType = { a: string };
// type MyType = { b: number }; // Error: Duplicate identifier 'MyType'

/*
 Interface vs Type — Quick Reference
 ────────────────────────────────────
 Use `interface` when:
   - Describing extendable object shapes (class-style OOP)
   - A class will `implements` the contract
   - You may need declaration merging (e.g., augmenting third-party types)

 Use `type` when:
   - Representing union types:        type ID = string | number
   - Building intersection types:     type Admin = User & { role: string }
   - Aliasing primitives or tuples:   type Pair = [string, number]
   - Composing utility types:         type ReadonlyUser = Readonly<User>
*/
