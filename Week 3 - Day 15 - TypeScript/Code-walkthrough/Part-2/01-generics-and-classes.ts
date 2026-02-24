// ============================================================
// Day 15 — Part 2  |  01-generics-and-classes.ts
// Generics Basics & Classes in TypeScript
// ============================================================
// Run with:  npx ts-node 01-generics-and-classes.ts


// ============================================================
// 1. GENERICS BASICS
// ============================================================
// Generics let you write code that works with MULTIPLE types
// while still being type-safe.
//
// Without generics: write the same function for string, number, etc.
// With generics: write it ONCE with a type parameter <T>

// --- 1a. The identity function — the canonical generic example ---

// Without generics — you'd need separate functions:
function identityString(value: string): string { return value; }
function identityNumber(value: number): number { return value; }
// This doesn't scale.

// With a generic type parameter T:
function identity<T>(value: T): T {
  return value;
}

// TypeScript can infer T from the argument:
console.log(identity("hello"));          // T inferred as string
console.log(identity(42));               // T inferred as number
console.log(identity({ name: "Alice" })); // T inferred as { name: string }

// Or you can provide T explicitly:
console.log(identity<string>("hello"));  // explicit


// --- 1b. Generic function with multiple type parameters ---
function pair<K, V>(key: K, value: V): { key: K; value: V } {
  return { key, value };
}

const p1 = pair("name", "Alice");   // { key: string, value: string }
const p2 = pair(1, true);          // { key: number, value: boolean }
const p3 = pair("score", 95);      // { key: string, value: number }


// --- 1c. Generic array utilities ---
function first<T>(arr: T[]): T | undefined {
  return arr[0];
}
function last<T>(arr: T[]): T | undefined {
  return arr[arr.length - 1];
}
function compact<T>(arr: (T | null | undefined)[]): T[] {
  return arr.filter((item): item is T => item != null);
}

console.log(first([1, 2, 3]));    // 1
console.log(last(["a", "b"]));    // b
console.log(compact([1, null, 2, undefined, 3])); // [1, 2, 3]


// --- 1d. Generic constraints (extends) ---
// Use `extends` to require the type parameter to have certain properties

function getProperty<T, K extends keyof T>(obj: T, key: K): T[K] {
  return obj[key];
}

const user = { id: 1, name: "Alice", email: "alice@example.com" };
console.log(getProperty(user, "name"));   // Alice
console.log(getProperty(user, "id"));     // 1
// getProperty(user, "missing"); // ✗ Error: "missing" is not a key of user

// Constrain T to objects with a `.length` property:
function logLength<T extends { length: number }>(item: T): void {
  console.log(`Length: ${item.length}`);
}
logLength("hello");          // Length: 5
logLength([1, 2, 3]);        // Length: 3
logLength({ length: 42 });   // Length: 42
// logLength(123);            // ✗ Error: number has no .length


// --- 1e. Generic interfaces ---
interface ApiResponse<T> {
  data: T;
  status: number;
  message: string;
  timestamp: string;
}

interface User {
  id: number;
  name: string;
  email: string;
}

interface Product {
  id: number;
  title: string;
  price: number;
}

// The same wrapper works for any data type:
const userResponse: ApiResponse<User> = {
  data: { id: 1, name: "Alice", email: "alice@example.com" },
  status: 200,
  message: "OK",
  timestamp: new Date().toISOString(),
};

const productResponse: ApiResponse<Product> = {
  data: { id: 10, title: "Widget", price: 9.99 },
  status: 200,
  message: "OK",
  timestamp: new Date().toISOString(),
};

const listResponse: ApiResponse<User[]> = {
  data: [
    { id: 1, name: "Alice", email: "alice@example.com" },
    { id: 2, name: "Bob",   email: "bob@example.com" },
  ],
  status: 200,
  message: "OK",
  timestamp: new Date().toISOString(),
};


// --- 1f. Generic classes ---
class Stack<T> {
  private items: T[] = [];

  push(item: T): void {
    this.items.push(item);
  }

  pop(): T | undefined {
    return this.items.pop();
  }

  peek(): T | undefined {
    return this.items[this.items.length - 1];
  }

  isEmpty(): boolean {
    return this.items.length === 0;
  }

  get size(): number {
    return this.items.length;
  }
}

const numStack = new Stack<number>();
numStack.push(1);
numStack.push(2);
numStack.push(3);
console.log(numStack.peek());  // 3
console.log(numStack.pop());   // 3
console.log(numStack.size);    // 2
// numStack.push("string");    // ✗ Error: string not assignable to number

const strStack = new Stack<string>();
strStack.push("first");
strStack.push("second");
console.log(strStack.pop()); // second


// --- 1g. Generic utility — building a simple type-safe repository ---
interface Entity {
  id: number;
}

class Repository<T extends Entity> {
  private store: Map<number, T> = new Map();
  private nextId = 1;

  add(item: Omit<T, "id">): T {
    const entity = { ...item, id: this.nextId++ } as T;
    this.store.set(entity.id, entity);
    return entity;
  }

  findById(id: number): T | undefined {
    return this.store.get(id);
  }

  findAll(): T[] {
    return [...this.store.values()];
  }

  delete(id: number): boolean {
    return this.store.delete(id);
  }

  count(): number {
    return this.store.size;
  }
}

interface Note {
  id: number;
  title: string;
  content: string;
}

const noteRepo = new Repository<Note>();
noteRepo.add({ title: "Shopping list", content: "Milk, eggs" });
noteRepo.add({ title: "Meeting notes", content: "Discuss Q4 goals" });
console.log(noteRepo.count()); // 2
console.log(noteRepo.findById(1)?.title); // Shopping list


// ============================================================
// 2. CLASSES IN TYPESCRIPT
// ============================================================
// TypeScript classes extend JavaScript classes with:
//   • Access modifiers: public, private, protected, readonly
//   • Parameter properties (shorthand in constructor)
//   • Abstract classes
//   • Implementing interfaces
//   • Type-safe method return types

// --- 2a. Basic class with access modifiers ---
class BankAccount {
  private balance: number;       // only accessible within this class
  private owner: string;
  readonly accountNumber: string; // can be read, but not reassigned
  protected interestRate: number; // accessible by this class AND subclasses

  constructor(owner: string, initialBalance: number = 0) {
    this.owner = owner;
    this.balance = initialBalance;
    this.interestRate = 0.02;
    this.accountNumber = `ACC-${Date.now()}-${Math.floor(Math.random() * 1000)}`;
  }

  deposit(amount: number): void {
    if (amount <= 0) throw new Error("Deposit amount must be positive");
    this.balance += amount;
    console.log(`Deposited $${amount}. New balance: $${this.balance}`);
  }

  withdraw(amount: number): void {
    if (amount <= 0) throw new Error("Withdrawal amount must be positive");
    if (amount > this.balance) throw new Error("Insufficient funds");
    this.balance -= amount;
    console.log(`Withdrew $${amount}. New balance: $${this.balance}`);
  }

  getBalance(): number {
    return this.balance; // controlled access to private field
  }

  toString(): string {
    return `Account[${this.accountNumber}] — Owner: ${this.owner}, Balance: $${this.balance}`;
  }
}

const checking = new BankAccount("Alice", 1000);
checking.deposit(500);
checking.withdraw(200);
console.log(checking.getBalance()); // 1300
// checking.balance = 999999;       // ✗ Error: 'balance' is private
// checking.accountNumber = "hack"; // ✗ Error: 'accountNumber' is readonly

console.log(checking.toString());


// --- 2b. Parameter properties shorthand ---
// Instead of: declare property → parameter → this.x = x
// TypeScript lets you do it in one step with the modifier in the constructor

class Point {
  constructor(
    public readonly x: number,
    public readonly y: number,
  ) {}

  distanceTo(other: Point): number {
    return Math.sqrt((this.x - other.x) ** 2 + (this.y - other.y) ** 2);
  }

  toString(): string {
    return `(${this.x}, ${this.y})`;
  }
}

const origin = new Point(0, 0);
const point  = new Point(3, 4);
console.log(origin.distanceTo(point)); // 5
console.log(point.toString());         // (3, 4)


// --- 2c. Implementing interfaces ---
interface Printable {
  print(): void;
}

interface Serializable {
  serialize(): string;
  deserialize(data: string): void;
}

class UserProfile implements Printable, Serializable {
  constructor(
    private name: string,
    private email: string,
    private age: number,
  ) {}

  print(): void {
    console.log(`User: ${this.name} <${this.email}> (age ${this.age})`);
  }

  serialize(): string {
    return JSON.stringify({ name: this.name, email: this.email, age: this.age });
  }

  deserialize(data: string): void {
    const parsed = JSON.parse(data);
    this.name  = parsed.name;
    this.email = parsed.email;
    this.age   = parsed.age;
  }
}

const profile = new UserProfile("Alice", "alice@example.com", 30);
profile.print();
const serialized = profile.serialize();
console.log("Serialized:", serialized);


// --- 2d. Inheritance with access modifiers ---
class SavingsAccount extends BankAccount {
  constructor(owner: string, initialBalance: number = 0) {
    super(owner, initialBalance);
    this.interestRate = 0.05; // protected — accessible in subclass
  }

  applyInterest(): void {
    const interest = this.getBalance() * this.interestRate;
    this.deposit(interest);
    console.log(`Applied ${this.interestRate * 100}% interest`);
  }
}

const savings = new SavingsAccount("Bob", 2000);
savings.deposit(1000);
savings.applyInterest(); // adds 5% of 3000 = $150
console.log(`Savings balance: $${savings.getBalance()}`);


// --- 2e. Abstract classes ---
// Cannot be instantiated directly — only used as a base class
abstract class Shape {
  abstract readonly kind: string;
  abstract area(): number;
  abstract perimeter(): number;

  // Concrete method — shared by all shapes
  describe(): string {
    return `${this.kind}: area=${this.area().toFixed(2)}, perimeter=${this.perimeter().toFixed(2)}`;
  }
}

class Circle extends Shape {
  readonly kind = "Circle";
  constructor(private radius: number) { super(); }
  area(): number      { return Math.PI * this.radius ** 2; }
  perimeter(): number { return 2 * Math.PI * this.radius; }
}

class Rect extends Shape {
  readonly kind = "Rectangle";
  constructor(private width: number, private height: number) { super(); }
  area(): number      { return this.width * this.height; }
  perimeter(): number { return 2 * (this.width + this.height); }
}

const shapes: Shape[] = [new Circle(5), new Rect(4, 6)];
shapes.forEach(s => console.log(s.describe()));
// Circle: area=78.54, perimeter=31.42
// Rectangle: area=24.00, perimeter=20.00

// new Shape(); // ✗ Error: Cannot create an instance of an abstract class


// --- 2f. Static members ---
class IdGenerator {
  private static counter = 0;
  static readonly prefix = "ID";

  static generate(): string {
    return `${IdGenerator.prefix}-${++IdGenerator.counter}`;
  }

  static reset(): void {
    IdGenerator.counter = 0;
  }
}

console.log(IdGenerator.generate()); // ID-1
console.log(IdGenerator.generate()); // ID-2
console.log(IdGenerator.generate()); // ID-3
IdGenerator.reset();
console.log(IdGenerator.generate()); // ID-1


// --- 2g. Getters and setters ---
class Temperature {
  private _celsius: number;

  constructor(celsius: number) {
    this._celsius = celsius;
  }

  get fahrenheit(): number {
    return this._celsius * 9 / 5 + 32;
  }

  set fahrenheit(value: number) {
    this._celsius = (value - 32) * 5 / 9;
  }

  get celsius(): number {
    return this._celsius;
  }

  set celsius(value: number) {
    if (value < -273.15) throw new RangeError("Temperature below absolute zero");
    this._celsius = value;
  }
}

const temp = new Temperature(100);
console.log(temp.fahrenheit); // 212
temp.fahrenheit = 32;
console.log(temp.celsius);    // 0
try {
  temp.celsius = -300; // RangeError
} catch (e) {
  console.error((e as Error).message);
}
