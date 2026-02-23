# Week 3 - Day 15: TypeScript
## Part 2 Slide Descriptions — Generics, Classes & Advanced Types

**Total slides:** 17
**Duration:** 60 minutes
**Part 2 Topics:** Generics basics, classes in TypeScript, decorators overview (syntax, class and method decorators), compiler and tsconfig.json configuration, utility types (Partial, Required, Readonly, Pick, Omit), type guards, interface vs type comparison

---

### Slide 1 — Title Slide (Part 2)

**Layout:** Same TypeScript blue color scheme as Part 1.
**Title:** TypeScript
**Subtitle:** Week 3 - Day 15 | Part 2: Generics, Classes & Advanced Types
**Visual:** Same TypeScript logo.
**Footer:** "Part 2 of 2 — The tools that make TypeScript scale to real applications."

---

### Slide 2 — Generics: The Problem They Solve

**Title:** Generics — Type-Safe Code for Any Type

**The problem without generics:**

```typescript
// Option 1: any — loses all type information
function identity(value: any): any {
    return value;
}
const result = identity("hello");
result.toUpperCase();      // No error, but...
result.doesNotExist();     // Also no error ← TypeScript gave up

// Option 2: multiple overloads — doesn't scale
function identityString(value: string): string { return value; }
function identityNumber(value: number): number { return value; }
// One function per type? That defeats the purpose.
```

**The solution: Generics**

```typescript
// T is a type parameter — a placeholder TypeScript fills in at each call site
function identity<T>(value: T): T {
    return value;
}

const str = identity("hello");      // T = string → returns string
const num = identity(42);           // T = number → returns number
const arr = identity([1, 2, 3]);    // T = number[] → returns number[]

str.toUpperCase();      // ✅ TypeScript knows it's a string
str.doesNotExist();     // ❌ Error — TypeScript is watching again
num.toFixed(2);         // ✅ TypeScript knows it's a number
```

**Convention for type parameter names:**
- `T` — general "Type"
- `U` — second type parameter
- `K` — key type
- `V` — value type
- `E` — element type
- These are conventions, not rules — you can use any name

---

### Slide 3 — Generic Functions

**Title:** Writing and Using Generic Functions

**Code:**

```typescript
// ─── Type inference — TypeScript infers T from the argument ─────
function wrap<T>(value: T): { data: T } {
    return { data: value };
}

const wrapped1 = wrap(42);         // T inferred as number → { data: number }
const wrapped2 = wrap("hello");    // T inferred as string → { data: string }
const wrapped3 = wrap<boolean>(true);  // explicit type parameter

// ─── Multiple type parameters ────────────────────────────────────
function pair<K, V>(key: K, value: V): [K, V] {
    return [key, value];
}
const result = pair("age", 30);   // [string, number]

// ─── Generic array utilities ─────────────────────────────────────
function getFirst<T>(arr: T[]): T | undefined {
    return arr[0];
}
function getLast<T>(arr: T[]): T | undefined {
    return arr[arr.length - 1];
}
function filterArray<T>(arr: T[], predicate: (item: T) => boolean): T[] {
    return arr.filter(predicate);
}

const first = getFirst([1, 2, 3]);          // T = number → number | undefined
const firstStr = getFirst(["a", "b"]);      // T = string → string | undefined

// ─── Generic function with keyof ─────────────────────────────────
function getProperty<T, K extends keyof T>(obj: T, key: K): T[K] {
    return obj[key];
}

const user = { id: 1, name: "Alice", email: "alice@example.com" };
const name  = getProperty(user, "name");    // TypeScript knows: string
const id    = getProperty(user, "id");      // TypeScript knows: number
getProperty(user, "missing");               // ❌ Error: "missing" not a key of user
```

---

### Slide 4 — Generic Constraints

**Title:** Generic Constraints — Limiting What T Can Be

**Code:**

```typescript
// Without constraints — can't access any properties, T could be anything
function getLength<T>(value: T): number {
    return value.length;  // ❌ Error: Property 'length' does not exist on type 'T'
}

// With a constraint using 'extends' — T must have a .length property
function getLength<T extends { length: number }>(value: T): number {
    return value.length;  // ✅ guaranteed to exist
}

getLength("hello");           // 5 — string has .length
getLength([1, 2, 3]);         // 3 — array has .length
getLength({ length: 10 });    // 10 — object with .length property
getLength(42);                // ❌ Error: number has no .length

// ─── keyof constraint — the most common generic pattern ─────────
function getProperty<T, K extends keyof T>(obj: T, key: K): T[K] {
    return obj[key];
}
// K extends keyof T means K must be a key that actually exists on T
// T[K] is the return type — the type of that specific property

// ─── Constraining to a union ─────────────────────────────────────
function process<T extends string | number>(value: T): T {
    return value;
}
process("hello");   // ✅
process(42);        // ✅
process(true);      // ❌ Error: boolean not assignable to string | number

// ─── Default type parameters (TypeScript 2.3+) ───────────────────
interface ApiResponse<T = unknown> {
    data: T;
    status: number;
    message: string;
}

// Using the generic:
const userResponse: ApiResponse<User> = { data: user, status: 200, message: "OK" };
// Using the default:
const unknown: ApiResponse = { data: null, status: 404, message: "Not found" };
```

---

### Slide 5 — Generic Interfaces and Types

**Title:** Generic Interfaces and Type Aliases

**Code:**

```typescript
// ─── Generic interface ────────────────────────────────────────────
interface Stack<T> {
    push(item: T): void;
    pop(): T | undefined;
    peek(): T | undefined;
    readonly size: number;
    isEmpty(): boolean;
}

// Implementing a generic interface with a concrete type
class NumberStack implements Stack<number> {
    private items: number[] = [];
    push(item: number) { this.items.push(item); }
    pop() { return this.items.pop(); }
    peek() { return this.items[this.items.length - 1]; }
    get size() { return this.items.length; }
    isEmpty() { return this.items.length === 0; }
}

// ─── Generic Repository pattern ─────────────────────────────────
// (connects to Spring Data JPA in Week 6 — same conceptual pattern)
interface Repository<T, ID> {
    findById(id: ID): T | undefined;
    findAll(): T[];
    save(entity: T): T;
    delete(id: ID): void;
}

// ─── Generic type aliases ─────────────────────────────────────────
type Maybe<T> = T | null | undefined;   // "this might not exist"

// A Result type — forces explicit error handling (popular pattern)
type Result<T, E = Error> =
    | { ok: true;  value: T }
    | { ok: false; error: E };

function parseJSON<T>(json: string): Result<T> {
    try {
        return { ok: true, value: JSON.parse(json) as T };
    } catch (e) {
        return { ok: false, error: e instanceof Error ? e : new Error(String(e)) };
    }
}

const result = parseJSON<{ name: string }>('{"name": "Alice"}');
if (result.ok) {
    console.log(result.value.name);   // TypeScript knows: { name: string }
} else {
    console.error(result.error.message);
}
```

**The `Result<T, E>` pattern** is a TypeScript implementation of functional error handling — similar to Rust's `Result` or Kotlin's `Either`. It forces callers to handle both success and failure explicitly, without try/catch.

---

### Slide 6 — Classes in TypeScript

**Title:** Classes in TypeScript — Adding Types to ES6 Classes

**Code:**

```typescript
// Day 14's ES6 class syntax, now with TypeScript type annotations
// TypeScript requires you to declare properties before using them in the constructor

class Animal {
    name: string;       // property declaration — required in TypeScript
    sound: string;

    constructor(name: string, sound: string) {
        this.name = name;
        this.sound = sound;
    }

    speak(): string {
        return `${this.name} says ${this.sound}`;
    }
}

// With optional and readonly properties
class User {
    readonly id: number;
    name: string;
    email: string;
    age?: number;                    // optional — may not be set

    constructor(id: number, name: string, email: string) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    greet(): string {
        return `Hello, ${this.name}`;
    }
}

// TypeScript enforces strictPropertyInitialization (part of strict mode)
// All declared properties must be initialized in the constructor or with a default value
class Incomplete {
    name: string;      // ❌ Error: Property 'name' has no initializer
}

class Complete {
    name: string = "";        // ✅ default value
    description?: string;     // ✅ optional — no initializer needed
}
```

**Note:** Day 14's ES6 class syntax works exactly the same in TypeScript. TypeScript just adds type annotations, access modifiers, and stricter initialization rules.

---

### Slide 7 — Access Modifiers and Parameter Properties

**Title:** Access Modifiers — Controlling Visibility

**Code:**

```typescript
class BankAccount {
    private balance: number;     // accessible only inside BankAccount
    protected owner: string;     // accessible in BankAccount and subclasses
    public accountId: string;    // accessible anywhere (default when omitted)
    readonly openedDate: Date;   // set once in constructor, never changed

    constructor(owner: string, initialBalance: number) {
        this.owner = owner;
        this.balance = initialBalance;
        this.accountId = Math.random().toString(36).slice(2);
        this.openedDate = new Date();
    }

    deposit(amount: number): void {
        if (amount > 0) this.balance += amount;
    }

    getBalance(): number {
        return this.balance;
    }
}

const account = new BankAccount("Alice", 1000);
account.deposit(500);
console.log(account.getBalance());       // ✅ 1500
console.log(account.balance);            // ❌ Error: private
account.openedDate = new Date();          // ❌ Error: readonly

// ─── Parameter Properties — shorthand for declare + assign ───────
// Instead of declaring a property and then assigning in the constructor body:
class Product {
    constructor(
        public readonly id: number,        // declared AND assigned in one step
        public name: string,
        private price: number,
        protected category: string = "general"
    ) {
        // No body needed — TypeScript generates all the assignments
    }

    getPrice(): number { return this.price; }
}

// ─── TypeScript 'private' vs JavaScript '#private' ───────────────
class Comparison {
    private tsPrivate: string = "ts";   // TypeScript compile-time only
    #jsPrivate: string = "js";          // JavaScript runtime enforcement
}
// TypeScript's 'private' is erased at compile time — not enforced in JS output
// JavaScript's '#' is enforced by the JavaScript engine at runtime (Day 14)
// For true runtime encapsulation: prefer '#private'
```

---

### Slide 8 — Abstract Classes and `implements`

**Title:** Abstract Classes and Interface Implementation

**Code:**

```typescript
// Abstract class — defines a template; cannot be instantiated directly
abstract class Shape {
    abstract getArea(): number;          // subclasses MUST implement
    abstract getPerimeter(): number;     // subclasses MUST implement

    // Non-abstract method — inherited by all subclasses
    toString(): string {
        return `${this.constructor.name}: area=${this.getArea().toFixed(2)}`;
    }
}

class Circle extends Shape {
    constructor(private radius: number) { super(); }
    getArea(): number { return Math.PI * this.radius ** 2; }
    getPerimeter(): number { return 2 * Math.PI * this.radius; }
}

class Rectangle extends Shape {
    constructor(private width: number, private height: number) { super(); }
    getArea(): number { return this.width * this.height; }
    getPerimeter(): number { return 2 * (this.width + this.height); }
}

new Shape();   // ❌ Error: Cannot create instance of an abstract class

// ─── 'implements' — a class fulfills an interface contract ────────
interface Serializable {
    serialize(): string;
    deserialize(data: string): void;
}
interface Validatable {
    validate(): boolean;
}

// A class can implement multiple interfaces
class FormData implements Serializable, Validatable {
    constructor(private data: Record<string, string>) {}

    serialize(): string { return JSON.stringify(this.data); }
    deserialize(data: string): void { this.data = JSON.parse(data); }
    validate(): boolean { return Object.keys(this.data).length > 0; }
}

// ─── Abstract class vs Interface comparison ───────────────────────
// Abstract class:
//   ✅ Can have method implementations (shared logic)
//   ✅ Can have constructor and state
//   ❌ A class can only extend ONE abstract class

// Interface:
//   ✅ A class can implement MULTIPLE interfaces
//   ✅ Purely a contract — no implementation
//   ❌ Cannot have constructor or method bodies
```

---

### Slide 9 — Decorators: Overview and Setup

**Title:** Decorators — Functions That Modify Code

**What is a decorator?**
A decorator is a special function that can observe, modify, or replace a class, method, property, or parameter — applied with `@` syntax above the target.

**Setup — enable in tsconfig.json:**
```json
{
  "compilerOptions": {
    "experimentalDecorators": true,
    "emitDecoratorMetadata": true
  }
}
```

**Two decorator systems:**

| | Experimental Decorators | TC39 Stage 3 Decorators |
|---|---|---|
| Enable | `"experimentalDecorators": true` | TypeScript 5.0+, no flag needed |
| Used by | Angular, NestJS, TypeORM | Future standard |
| Syntax | Slightly different | Aligned with ECMAScript proposal |

**We cover experimental decorators** — they're what Angular uses and what you'll encounter day-to-day in Week 4+.

**A decorator is just a function:**
```typescript
// A basic method decorator — receives target, propertyKey, descriptor
function readonly(target: any, propertyKey: string, descriptor: PropertyDescriptor) {
    descriptor.writable = false;
    return descriptor;
}

class Greeter {
    @readonly
    greet(): string { return "Hello!"; }
}

// A decorator FACTORY — a decorator that accepts arguments
function log(level: "info" | "warn" | "error") {
    return function(target: any, key: string, descriptor: PropertyDescriptor) {
        const original = descriptor.value;
        descriptor.value = function(...args: any[]) {
            console[level](`Calling ${key}`);
            return original.apply(this, args);
        };
        return descriptor;
    };
}
```

**Where you'll see decorators:**
Angular's `@Component`, `@Injectable`, `@Input`, `@Output`, `@HostListener`, `@Pipe`, `@ViewChild` — every Angular class feature is a decorator. Day 14's TypeScript classes + today's decorators = the building block of Angular.

---

### Slide 10 — Class Decorators

**Title:** Class Decorators

**Code:**

```typescript
// A class decorator receives the constructor function
// It can observe, modify, or replace the class entirely

// Simple decorator — adds metadata without replacing the class
function Entity(tableName: string) {
    return function<T extends new (...args: any[]) => {}>(constructor: T) {
        return class extends constructor {
            _tableName = tableName;
        };
    };
}

@Entity("users")
class User {
    constructor(public name: string, public email: string) {}
}

// ─── @sealed — prevents adding new properties to the class ───────
function sealed(constructor: Function) {
    Object.seal(constructor);
    Object.seal(constructor.prototype);
}

@sealed
class BugReport {
    type = "report";
    title: string;
    constructor(title: string) { this.title = title; }
}

// ─── Stacking decorators — applied bottom-up ─────────────────────
function first()  { return (c: any) => { console.log("first");  return c; }; }
function second() { return (c: any) => { console.log("second"); return c; }; }

@first()
@second()
class Example {}
// Output: "second" then "first" — bottom decorators execute first

// ─── Angular preview ─────────────────────────────────────────────
// This is what Angular's @Component looks like under the hood:
// (You'll use this every day in Week 4 Angular days)
//
// @Component({
//   selector: 'app-root',
//   templateUrl: './app.component.html',
//   styleUrls: ['./app.component.scss']
// })
// class AppComponent { title = "My App"; }
//
// @Component IS a class decorator factory — exactly the pattern above.
```

---

### Slide 11 — Method and Property Decorators

**Title:** Method, Property, and Parameter Decorators

**Code:**

```typescript
// ─── Method Decorator — wraps or modifies a method ───────────────
// Receives: (target, propertyKey, PropertyDescriptor)
// descriptor.value = the method function itself

function measure(target: any, key: string, descriptor: PropertyDescriptor) {
    const original = descriptor.value;
    descriptor.value = function(...args: any[]) {
        const start = performance.now();
        const result = original.apply(this, args);
        const ms = (performance.now() - start).toFixed(2);
        console.log(`${key} executed in ${ms}ms`);
        return result;
    };
    return descriptor;
}

class DataService {
    @measure
    processData(data: number[]): number {
        return data.reduce((acc, n) => acc + n, 0);
    }
}

// ─── Property Decorator ───────────────────────────────────────────
// Receives: (target, propertyKey)
// Cannot access the property value (it only exists on instances, not the class)
// Used primarily for metadata registration

function Required(target: any, propertyKey: string): void {
    // Used with reflect-metadata libraries for validation frameworks
    const existing: string[] = Reflect.getMetadata("required", target) || [];
    Reflect.defineMetadata("required", [...existing, propertyKey], target);
}

class UserForm {
    @Required
    name: string = "";

    @Required
    email: string = "";

    age?: number;    // not required
}

// ─── Parameter Decorator ─────────────────────────────────────────
// Receives: (target, methodName, parameterIndex)
// Most commonly used in Angular's Dependency Injection
function LogParam(target: any, methodName: string, paramIndex: number): void {
    console.log(`Parameter ${paramIndex} of ${methodName} will be logged`);
}

class Service {
    doWork(@LogParam input: string): void { /* ... */ }
}

// ─── Real-world Angular mapping ───────────────────────────────────
// @Input()    → property decorator (Day 16b, 17b)
// @Output()   → property decorator (Day 16b, 17b)
// @ViewChild() → property decorator (Day 17a angular equivalent)
// @HostListener() → method decorator (Day 17b)
// @Inject()   → parameter decorator (Day 17b)
```

---

### Slide 12 — The TypeScript Compiler (tsc)

**Title:** The TypeScript Compiler — tsc CLI

**Common tsc commands:**

```bash
# Compile based on tsconfig.json (project root)
tsc

# Compile a single file (ignores tsconfig.json)
tsc app.ts

# Initialize a tsconfig.json with defaults
tsc --init

# Watch mode — recompile automatically on save
tsc --watch

# Type-check only — DO NOT emit files (perfect for CI/CD)
tsc --noEmit

# See what files are included
tsc --listFiles

# Show the compilation output without writing files
tsc --noEmit --listFiles
```

**JavaScript target options:**

| Target | Use When |
|---|---|
| `ES5` | Legacy browser support (IE11) |
| `ES6` / `ES2015` | Evergreen browsers, no transpilation needed |
| `ES2020` | Node.js 14+, modern browsers |
| `ES2022` | Node.js 16+, modern browsers |
| `ESNext` | Latest features, used with bundlers |

**tsc vs other tools:**

| Tool | What It Does |
|---|---|
| `tsc` | Official compiler; full type-checking + emit JS |
| `ts-node` | Development convenience; run TS without compiling |
| `esbuild` / `swc` | Transpile TS fast (strip types) — no full type-check |
| Vite / Webpack | Build pipeline — use esbuild/swc under the hood |

**Best practice for CI/CD:** Run `tsc --noEmit` in your pipeline to ensure no type errors exist, even if your bundler does the actual transpilation.

---

### Slide 13 — tsconfig.json — Key Configuration Options

**Title:** Configuring the TypeScript Compiler

**A production-ready tsconfig.json:**

```json
{
  "compilerOptions": {
    // ─── Target & Module ─────────────────────────────────────
    "target": "ES2020",           // what JavaScript version to output
    "module": "commonjs",         // module system: commonjs (Node) or esnext (bundler)
    "lib": ["ES2020", "DOM"],     // built-in type definitions to include

    // ─── Output ──────────────────────────────────────────────
    "outDir": "./dist",           // where to put compiled JS files
    "rootDir": "./src",           // where TypeScript source files live
    "declaration": true,          // generate .d.ts type definition files

    // ─── Strict Mode (ALWAYS ENABLE THIS) ────────────────────
    "strict": true,
    // 'strict: true' enables all of these:
    //   strictNullChecks         — null/undefined must be declared explicitly
    //   noImplicitAny            — no implicit 'any' type allowed
    //   strictFunctionTypes      — stricter checking of function parameter types
    //   strictPropertyInitialization — all properties initialized in constructor
    //   strictBindCallApply      — strict types for .bind, .call, .apply

    // ─── Additional Quality Checks ───────────────────────────
    "noUnusedLocals": true,
    "noUnusedParameters": true,
    "noImplicitReturns": true,
    "noFallthroughCasesInSwitch": true,

    // ─── Module Resolution ────────────────────────────────────
    "moduleResolution": "node",
    "esModuleInterop": true,      // allows default imports from CommonJS modules
    "resolveJsonModule": true,    // allow importing .json files

    // ─── Path Aliases (optional but common) ──────────────────
    "paths": {
      "@/*": ["./src/*"]          // import from "@/utils" instead of "../../utils"
    },

    // ─── Decorators (required for Angular/NestJS) ────────────
    "experimentalDecorators": true,
    "emitDecoratorMetadata": true
  },
  "include": ["src/**/*"],
  "exclude": ["node_modules", "dist", "**/*.spec.ts"]
}
```

**The single most important option:** `"strict": true`

Start every project with `strict: true`. Fixing strict violations on a large existing codebase is painful. Starting with it enabled costs nothing at day one and prevents an enormous range of runtime bugs.

---

### Slide 14 — Utility Types Part 1: Partial, Required, Readonly

**Title:** Utility Types — Transforming Types

TypeScript ships built-in utility types that transform existing types. They're generic types defined in TypeScript's standard library.

**Code:**

```typescript
interface User {
    id: number;
    name: string;
    email: string;
    age: number;
}

// ─── Partial<T> — makes ALL properties optional ───────────────────
type PartialUser = Partial<User>;
// { id?: number; name?: string; email?: string; age?: number }

// Perfect for PATCH/update operations where only some fields change
function updateUser(id: number, updates: Partial<User>): User {
    const current = getUserById(id);
    return { ...current, ...updates };
}
updateUser(1, { name: "Alice Updated" });    // ✅ only name
updateUser(1, { email: "new@email.com" });   // ✅ only email

// ─── Required<T> — makes ALL properties required ──────────────────
interface Config {
    host?: string;
    port?: number;
    debug?: boolean;
}
type FullConfig = Required<Config>;
// { host: string; port: number; debug: boolean }

// ─── Readonly<T> — makes ALL properties readonly ──────────────────
type ImmutableUser = Readonly<User>;
// { readonly id: number; readonly name: string; ... }

const frozenUser: Readonly<User> = { id: 1, name: "Alice", email: "a@a.com", age: 30 };
frozenUser.name = "Bob";    // ❌ Error: readonly

// ─── Composing utility types ──────────────────────────────────────
type ReadonlyPartialUser = Readonly<Partial<User>>;
// All properties are both optional AND readonly
// Common in React state initialization patterns
```

---

### Slide 15 — Utility Types Part 2: Pick, Omit, Record, and More

**Title:** More Utility Types

**Code:**

```typescript
interface User {
    id: number; name: string; email: string; age: number; password: string;
}

// ─── Pick<T, K> — keep only specified properties ──────────────────
type UserSummary = Pick<User, "id" | "name">;
// { id: number; name: string }
// Perfect when an API endpoint should return only safe/minimal fields

// ─── Omit<T, K> — exclude specified properties ────────────────────
type PublicUser = Omit<User, "password">;
// { id: number; name: string; email: string; age: number }
// "everything EXCEPT password" — common for API responses

// ─── Record<K, V> — typed dictionary ─────────────────────────────
type UsersByRole = Record<"admin" | "editor" | "viewer", User[]>;
// { admin: User[]; editor: User[]; viewer: User[] }

type CountryMap = Record<string, string>;
const codes: CountryMap = { US: "United States", GB: "United Kingdom" };

// ─── Exclude<T, U> — remove types from a union ────────────────────
type Primitive = string | number | boolean;
type StringOrNumber = Exclude<Primitive, boolean>;
// string | number

// ─── Extract<T, U> — keep only matching types from union ──────────
type OnlyString = Extract<Primitive, string>;
// string

// ─── ReturnType<T> — type of what a function returns ─────────────
function getUser(): { id: number; name: string } {
    return { id: 1, name: "Alice" };
}
type UserType = ReturnType<typeof getUser>;
// { id: number; name: string }

// ─── Parameters<T> — tuple of a function's parameter types ────────
function createUser(name: string, age: number, admin: boolean): User { /* ... */ }
type CreateUserArgs = Parameters<typeof createUser>;
// [name: string, age: number, admin: boolean]

// ─── NonNullable<T> — removes null and undefined from a type ──────
type MaybeString = string | null | undefined;
type DefiniteString = NonNullable<MaybeString>;
// string
```

**Utility types are how TypeScript avoids repetition at the type level.** Instead of writing a second interface by hand that partially duplicates the first, derive it with `Partial`, `Pick`, or `Omit`.

---

### Slide 16 — Type Guards and Narrowing

**Title:** Type Guards — Safe Type Narrowing

**Code:**

```typescript
// TypeScript narrows types within conditional blocks based on control flow

// ─── typeof narrowing ─────────────────────────────────────────────
function processInput(input: string | number): string {
    if (typeof input === "string") {
        return input.toUpperCase();   // TypeScript knows: string
    } else {
        return input.toFixed(2);      // TypeScript knows: number
    }
}

// ─── instanceof narrowing ─────────────────────────────────────────
function handleError(error: unknown): string {
    if (error instanceof TypeError) {
        return `Type error: ${error.message}`;
    }
    if (error instanceof Error) {
        return `Error: ${error.message}`;
    }
    return String(error);
}

// ─── 'in' operator narrowing ──────────────────────────────────────
type Cat = { meow(): void };
type Dog = { bark(): void };

function makeNoise(animal: Cat | Dog): void {
    if ("meow" in animal) {
        animal.meow();   // TypeScript knows: Cat
    } else {
        animal.bark();   // TypeScript knows: Dog
    }
}

// ─── User-defined type guard (type predicate) ─────────────────────
// Syntax: parameter is Type — tells TypeScript "if this returns true, param IS that type"
function isString(value: unknown): value is string {
    return typeof value === "string";
}

function isUser(value: unknown): value is User {
    return (
        typeof value === "object" &&
        value !== null &&
        "id" in value &&
        "name" in value &&
        "email" in value
    );
}

// Using the type guard
const rawData: unknown = fetchFromAPI();
if (isUser(rawData)) {
    console.log(rawData.name);    // TypeScript knows: User — no assertion needed
}

// ─── Discriminated union narrowing (connects to Part 1 Slide 8) ──
type ApiResult<T> =
    | { status: "success"; data: T }
    | { status: "error";   message: string };

function handle<T>(result: ApiResult<T>): void {
    if (result.status === "success") {
        console.log(result.data);       // TypeScript knows the success variant
    } else {
        console.error(result.message);  // TypeScript knows the error variant
    }
}
```

**Type guards are essential** when working with `unknown`, `any`, or union types received from external sources (API responses, JSON parsing, event handlers). They let you write safe code without type assertions.

---

### Slide 17 — Interface vs Type: The Definitive Comparison

**Title:** Interface vs Type — When to Use Each

**Comparison table:**

| Feature | `interface` | `type` |
|---|---|---|
| Object shapes | ✅ Yes | ✅ Yes |
| Primitive aliases | ❌ No | ✅ Yes: `type ID = string` |
| Union types | ❌ No | ✅ Yes: `type A = B \| C` |
| Intersection | via `extends` | ✅ Yes: `A & B` |
| Tuples | ❌ No | ✅ Yes |
| Conditional types | ❌ No | ✅ Yes |
| Mapped types | ❌ No | ✅ Yes |
| Declaration merging | ✅ Yes | ❌ No |
| `implements` in class | ✅ Yes | ✅ Yes (for object shapes) |
| Extension syntax | `extends` keyword | `&` intersection |
| Error messages | More readable | Can be verbose |

**When to use `interface`:**
- Defining the shape of an object or class contract
- Building a public API (library, SDK) — interfaces can be augmented by consumers via declaration merging
- Working with classes that `implements` a contract
- You want consumers to extend your type (open for extension)

**When to use `type`:**
- Creating union types: `type Status = "active" | "inactive"`
- Creating intersection combinations: `type AdminUser = User & Admin`
- Aliasing primitive types: `type UserId = string`
- Tuples: `type Point = [number, number]`
- Mapped and conditional types
- Anything that `interface` syntactically cannot express

**The TypeScript team's guidance:**
> *"In general, use `interface` until you need the features of `type`."*

In practice, modern production TypeScript codebases use both freely and interchangeably for object shapes. The most important thing is **consistency within a project**.

---

**Day 15 Summary — Learning Objectives Complete:**

| Learning Objective | Coverage |
|---|---|
| Write type-safe TypeScript code | ✅ P1 throughout + P2 throughout |
| Configure the TypeScript compiler | ✅ P2 Slides 12–13 |
| Use interfaces and type aliases effectively | ✅ P1 Slides 7, 10; P2 Slides 5, 17 |
| Apply generics for reusable code | ✅ P2 Slides 2–5 |
| Understand when to use Interface vs Type | ✅ P2 Slide 17 |

**Coming up — Week 4:**
- **Angular (Day 16b onward):** TypeScript is mandatory. Everything from today — interfaces, decorators, generics, utility types — appears immediately
- **React (Day 16a onward):** `React.FC<Props>`, `useState<T>`, `useRef<HTMLElement>`, typed event handlers — all use the TypeScript you learned today
- The `@Component`, `@Injectable`, `@Input`, `@Output` decorators in Angular are class and property decorators — exactly what you just learned
