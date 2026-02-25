// Day 15 Part 2 — TypeScript Advanced
// Topics: Generics, Classes, Decorators, Utility Types, Type Guards, Interface vs Type
// Run: npm install && npm start

console.log("╔══════════════════════════════════════════════════════════════╗");
console.log("║  Day 15 Part 2 — TypeScript Advanced                       ║");
console.log("╚══════════════════════════════════════════════════════════════╝\n");

demoGenerics();
demoClasses();
demoUtilityTypes();
demoTypeGuards();
demoInterfaceVsType();

// ─────────────────────────────────────────────────────────────
// 1. Generics
// ─────────────────────────────────────────────────────────────
function demoGenerics(): void {
  console.log("=== 1. Generics ===");

  // Generic function
  function identity<T>(value: T): T { return value; }
  console.log("  identity<string>('hello'):", identity("hello"));
  console.log("  identity<number>(42):", identity(42));

  // Generic with constraint
  function getProperty<T, K extends keyof T>(obj: T, key: K): T[K] {
    return obj[key];
  }
  const user = { name: "Alice", age: 28, role: "admin" };
  console.log("  getProperty(user, 'name'):", getProperty(user, "name"));
  console.log("  getProperty(user, 'age'):", getProperty(user, "age"));

  // Generic class — Stack data structure
  class Stack<T> {
    private items: T[] = [];
    push(item: T):    void { this.items.push(item); }
    pop():             T | undefined { return this.items.pop(); }
    peek():            T | undefined { return this.items[this.items.length - 1]; }
    isEmpty():         boolean { return this.items.length === 0; }
    get size():        number  { return this.items.length; }
    toString():        string  { return `Stack[${this.items.join(", ")}]`; }
  }

  const numStack = new Stack<number>();
  numStack.push(1); numStack.push(2); numStack.push(3);
  console.log("  numStack:", numStack.toString());
  console.log("  peek:", numStack.peek(), "| pop:", numStack.pop(), "| size:", numStack.size);

  const strStack = new Stack<string>();
  strStack.push("a"); strStack.push("b");
  console.log("  strStack:", strStack.toString());

  // Generic interface
  interface Repository<T, ID> {
    findById(id: ID):    T | undefined;
    findAll():           T[];
    save(entity: T):     void;
    delete(id: ID):      void;
  }

  interface Product { id: number; name: string; price: number; }
  class ProductRepository implements Repository<Product, number> {
    private store = new Map<number, Product>();
    findById(id: number) { return this.store.get(id); }
    findAll()            { return [...this.store.values()]; }
    save(p: Product)     { this.store.set(p.id, p); }
    delete(id: number)   { this.store.delete(id); }
  }

  const repo = new ProductRepository();
  repo.save({ id: 1, name: "Laptop", price: 999 });
  repo.save({ id: 2, name: "Phone",  price: 599 });
  console.log("  repo.findAll():", repo.findAll().map(p => p.name));
  console.log();
}

// ─────────────────────────────────────────────────────────────
// 2. Classes in TypeScript
// ─────────────────────────────────────────────────────────────
abstract class Shape {
  abstract area():      number;
  abstract perimeter(): number;
  describe(): string { return `${this.constructor.name}: area=${this.area().toFixed(2)}, perimeter=${this.perimeter().toFixed(2)}`; }
}

class Circle extends Shape {
  constructor(private radius: number) { super(); }
  area()      { return Math.PI * this.radius ** 2; }
  perimeter() { return 2 * Math.PI * this.radius; }
}

class Rectangle extends Shape {
  constructor(protected width: number, protected height: number) { super(); }
  area()      { return this.width * this.height; }
  perimeter() { return 2 * (this.width + this.height); }
}

class Square extends Rectangle {
  constructor(side: number) { super(side, side); }
}

function demoClasses(): void {
  console.log("=== 2. Classes ===");

  const shapes: Shape[] = [new Circle(5), new Rectangle(4, 6), new Square(3)];
  shapes.forEach(s => console.log("  " + s.describe()));

  // Access modifiers demo
  class BankAccount {
    readonly accountNumber: string;
    private _balance: number;
    protected owner: string;

    constructor(owner: string, initial: number) {
      this.owner = owner;
      this._balance = initial;
      this.accountNumber = `ACC-${Math.random().toString(36).slice(2, 8).toUpperCase()}`;
    }

    // Getter / Setter
    get balance(): number { return this._balance; }
    set balance(v: number) {
      if (v < 0) throw new RangeError("Balance cannot be negative");
      this._balance = v;
    }

    deposit(amount: number): void { this._balance += amount; }
    withdraw(amount: number): boolean {
      if (amount > this._balance) return false;
      this._balance -= amount;
      return true;
    }
    toString() { return `${this.owner}: $${this._balance} (${this.accountNumber})`; }
  }

  const acct = new BankAccount("Alice", 1000);
  acct.deposit(500);
  acct.withdraw(200);
  console.log("  " + acct.toString());
  console.log("  balance getter:", acct.balance);
  console.log();
}

// ─────────────────────────────────────────────────────────────
// 3. Utility Types
// ─────────────────────────────────────────────────────────────
interface User {
  id:       number;
  name:     string;
  email:    string;
  password: string;
  role:     string;
  age:      number;
}

function demoUtilityTypes(): void {
  console.log("=== 3. Utility Types ===");

  // Partial<T> — all properties optional
  const partial: Partial<User> = { name: "Alice" };
  console.log("  Partial<User>:", partial);

  // Required<T> — all properties required
  // const required: Required<Partial<User>> = { id: 1, name: "Alice", email: "a@b.com", password: "x", role: "admin", age: 25 };

  // Readonly<T> — all properties readonly
  const readonlyUser: Readonly<User> = { id: 1, name: "Alice", email: "a@b.com", password: "secret", role: "admin", age: 25 };
  // readonlyUser.name = "Bob";  // ← TS error

  // Pick<T, K> — only specific properties
  type PublicUser = Pick<User, "id" | "name" | "role">;
  const pub: PublicUser = { id: 1, name: "Alice", role: "admin" };
  console.log("  Pick<User, id|name|role>:", pub);

  // Omit<T, K> — exclude specific properties
  type SafeUser = Omit<User, "password">;
  const safe: SafeUser = { id: 1, name: "Alice", email: "a@b.com", role: "admin", age: 25 };
  console.log("  Omit<User, password>:", Object.keys(safe));

  // Record<K, V>
  const statusMap: Record<string, number> = { active: 100, inactive: 20, pending: 5 };
  console.log("  Record<string,number>:", statusMap);

  // ReturnType & Parameters
  function fetchUser(id: number, verbose: boolean): User {
    return { id, name: "Alice", email: "a@b.com", password: "x", role: "admin", age: 25 };
  }
  type FetchReturn = ReturnType<typeof fetchUser>;       // User
  type FetchParams = Parameters<typeof fetchUser>;       // [number, boolean]
  console.log("  ReturnType<fetchUser>: User interface");
  console.log("  Parameters<fetchUser>: [number, boolean]");
  console.log();
}

// ─────────────────────────────────────────────────────────────
// 4. Type Guards
// ─────────────────────────────────────────────────────────────
interface Cat { kind: "cat"; meow(): string; }
interface Dog { kind: "dog"; bark(): string; }
type Pet = Cat | Dog;

function demoTypeGuards(): void {
  console.log("=== 4. Type Guards ===");

  // typeof guard
  function formatValue(val: string | number): string {
    if (typeof val === "string") return val.toUpperCase();
    return val.toFixed(2);
  }
  console.log("  typeof guard:", formatValue("hello"), formatValue(3.14159));

  // instanceof guard
  class ApiError extends Error { constructor(public statusCode: number, msg: string) { super(msg); } }
  class NetworkError extends Error { constructor(public url: string, msg: string) { super(msg); } }

  function handle(err: Error): string {
    if (err instanceof ApiError)     return `API ${err.statusCode}: ${err.message}`;
    if (err instanceof NetworkError) return `Network [${err.url}]: ${err.message}`;
    return `Unknown: ${err.message}`;
  }
  console.log("  instanceof:", handle(new ApiError(404, "Not Found")));
  console.log("  instanceof:", handle(new NetworkError("/api/users", "Timeout")));

  // Discriminated union (kind field)
  const pets: Pet[] = [
    { kind: "cat", meow: () => "Meow!" },
    { kind: "dog", bark: () => "Woof!" },
  ];
  pets.forEach(pet => {
    if (pet.kind === "cat") console.log("  discriminated union (cat):", pet.meow());
    else                    console.log("  discriminated union (dog):", pet.bark());
  });

  // User-defined type guard
  function isString(val: unknown): val is string { return typeof val === "string"; }
  const values: unknown[] = ["hello", 42, true, "world"];
  const strings = values.filter(isString);
  console.log("  isString type guard filter:", strings);
  console.log();
}

// ─────────────────────────────────────────────────────────────
// 5. Interface vs Type
// ─────────────────────────────────────────────────────────────
function demoInterfaceVsType(): void {
  console.log("=== 5. Interface vs Type Alias ===");

  // Interface — can be extended, merged (declaration merging)
  interface Animal { name: string; }
  interface Animal { sound: string; }  // declaration merging — adds to Animal
  interface Dog extends Animal { breed: string; }

  const dog: Dog = { name: "Rex", sound: "Woof", breed: "Lab" };
  console.log("  Interface (merged + extended):", dog);

  // Type alias — can represent unions, primitives, tuples; no merging
  type Point    = { x: number; y: number };
  type Point3D  = Point & { z: number };      // intersection (like extending)
  type StringOrNum = string | number;          // union (only possible with type)

  const p3d: Point3D = { x: 1, y: 2, z: 3 };
  console.log("  Type alias intersection:", p3d);

  console.log("\n  Key differences:");
  console.log("  ✓ interface: extendable, declaration merging, better for OOP");
  console.log("  ✓ type:      unions, intersections, mapped types, primitives");
  console.log("  → Prefer interface for object shapes; type for complex types/unions");
  console.log("\n✓ TypeScript Advanced Part 2 demo complete.");
}
