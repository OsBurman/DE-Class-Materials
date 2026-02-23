# Week 3 - Day 15: TypeScript
## Part 1 Slide Descriptions — The Type System

**Total slides:** 16
**Duration:** 60 minutes
**Part 1 Topics:** TypeScript basics and benefits, type annotations and inference, simple types (string, number, boolean), special types (any, unknown, never, void), object types and interfaces, union types and type aliases, arrays and tuples, enums, type casting and assertions, functions and function types

---

### Slide 1 — Title Slide

**Layout:** Full-bleed background using TypeScript's official dark blue (#3178c6) color palette.
**Title:** TypeScript
**Subtitle:** Week 3 - Day 15 | Part 1: The Type System
**Visual:** TypeScript official logo (blue square with white "TS"). Tagline: "JavaScript that scales."
**Footer:** "Building on ES6+ (Day 14) — adding types to everything you already know."

---

### Slide 2 — What Is TypeScript and Why Does It Exist?

**Title:** What Is TypeScript?

**Content:**

TypeScript is a **typed superset of JavaScript** that compiles to plain JavaScript.

- Created by Microsoft (2012), maintained as open source on GitHub
- Designed by Anders Hejlsberg — also the creator of C# and Delphi
- Every valid JavaScript file is also a valid TypeScript file
- TypeScript is fully erased at compile time — browsers and Node never see it

**Why it was created:**
JavaScript was designed for small scripts. As applications grew to hundreds of thousands of lines, maintaining them became increasingly difficult — no compile-time error checking, no intelligent IDE autocomplete, dangerous refactoring, unclear code intent.

**TypeScript vs JavaScript — Comparison Table:**

| | JavaScript | TypeScript |
|---|---|---|
| Type checking | Runtime (crashes) | Compile-time (before you run) |
| IDE autocomplete | Limited | Full, context-aware |
| Refactoring safety | Risky | Confident |
| Code as documentation | Implicit | Explicit |
| Error discovery | In production | In your editor |
| Learning curve | Lower | Moderate (absolutely worthwhile) |

**Industry adoption:**
Used by: Angular (mandatory), VS Code (written in TypeScript), Slack, Airbnb, Notion, Stripe, Microsoft, Google. TypeScript is consistently a top-10 most-downloaded npm package.

**One-sentence value proposition:**
TypeScript catches an entire class of bugs — wrong argument types, accessing properties that don't exist, incorrect return types — before your code ever runs.

---

### Slide 3 — TypeScript in Your Workflow

**Title:** Setting Up TypeScript

**Code:**

```bash
# Install TypeScript globally
npm install -g typescript

# Check version
tsc --version                     # TypeScript 5.x.x

# Compile a TypeScript file to JavaScript
tsc hello.ts                      # produces hello.js in the same folder

# Watch mode — recompile automatically when files change
tsc --watch

# Initialize a tsconfig.json project configuration
tsc --init                        # creates tsconfig.json with sensible defaults

# Run TypeScript directly (development only — no explicit compile step)
npm install -g ts-node
ts-node hello.ts
```

**The compilation pipeline:**
```
hello.ts  →  tsc  →  hello.js  →  Node.js / Browser
  (TS)              (plain JS)
```

**TypeScript Playground:** [typescriptlang.org/play](https://www.typescriptlang.org/play)
Try TypeScript in the browser — no install needed; see JavaScript output side-by-side.

**VS Code and TypeScript:**
- VS Code is itself written in TypeScript
- Out-of-the-box TypeScript IntelliSense — no plugin required
- Red squiggles appear as you type, before you compile
- Hover over any symbol to see its inferred type

**Note:** `tsconfig.json` is covered in depth in Part 2. For now, `tsc hello.ts` works for individual files.

---

### Slide 4 — Type Annotations and Type Inference

**Title:** Annotations vs Inference — Let TypeScript Help You

**Key concept:** TypeScript has two ways to know the type of a value:
1. **Annotation** — you explicitly declare the type
2. **Inference** — TypeScript figures it out from context

**Code:**

```typescript
// ─── TYPE ANNOTATIONS — you tell TypeScript the type ──────────
let name: string = "Alice";
let age: number = 30;
let isActive: boolean = true;

// ─── TYPE INFERENCE — TypeScript figures it out automatically ──
let name = "Alice";        // inferred: string
let age = 30;              // inferred: number
let isActive = true;       // inferred: boolean

// Inference works for complex values too
const scores = [90, 85, 92];            // inferred: number[]
const user = { name: "Alice", age: 30 }; // inferred: { name: string; age: number }

// Return type inferred from function body
const double = (n: number) => n * 2;   // return type inferred: number

// When TypeScript can't infer — annotation is required
let value;              // inferred: any ← TypeScript gives up (BAD)
let value: string;      // GOOD — declared uninitialized, type is explicit

// Function parameters ALWAYS need annotation
function greet(name) { ... }         // Error: 'name' implicitly has 'any' type
function greet(name: string) { ... } // Correct
```

**Rule of thumb:**
- **Let TypeScript infer** when: variable is initialized with a value, simple function return types
- **Annotate explicitly** when: function parameters (always), uninitialized variables, complex or ambiguous situations, public API surfaces

**Important:** TypeScript's inference is excellent. Overannotating — writing `: string` on `let x = "hello"` — is considered noise. Trust the inference engine.

---

### Slide 5 — Simple Types

**Title:** The Core Primitive Types

**Code:**

```typescript
// ─── string ─────────────────────────────────────────────────────
let firstName: string = "Alice";
let lastName: string = 'Smith';
let greeting: string = `Hello, ${firstName}!`;   // template literals work

// ─── number ─────────────────────────────────────────────────────
// TypeScript uses ONE number type for all numeric values (like JavaScript)
let count: number = 42;
let price: number = 9.99;
let hex: number = 0xff;        // 255
let binary: number = 0b1010;   // 10
let octal: number = 0o17;      // 15

// ─── boolean ────────────────────────────────────────────────────
let isLoggedIn: boolean = true;
let hasPermission: boolean = false;

// ─── null and undefined ─────────────────────────────────────────
// These have their own distinct types in TypeScript
let nothing: null = null;
let missing: undefined = undefined;

// With strictNullChecks: true (ALWAYS enable this):
let name: string = null;        // ❌ Error: null not assignable to string
let name: string | null = null; // ✅ Must explicitly declare null is acceptable

// ─── bigint (ES2020+) ───────────────────────────────────────────
// For integers larger than Number.MAX_SAFE_INTEGER
let bigNum: bigint = 9007199254740993n;

// ─── symbol ─────────────────────────────────────────────────────
const sym1: symbol = Symbol("id");
const sym2: symbol = Symbol("id");
console.log(sym1 === sym2);   // false — every Symbol() call produces a unique value
```

**Critical:** Always enable `"strictNullChecks": true` in tsconfig (included in `"strict": true`). Without it, `null` and `undefined` are silently assignable to every type — which defeats a large portion of TypeScript's value. Tony Hoare called the invention of `null` his "billion dollar mistake" — strictNullChecks is the fix.

---

### Slide 6 — Special Types: any, unknown, void, never

**Title:** Special Types

**Code:**

```typescript
// ─── any — opt out of type checking entirely ────────────────────
let data: any = "hello";
data = 42;                      // OK — any accepts anything
data = { x: 1 };               // OK
data.nonExistent.method();      // NO ERROR from TypeScript — but CRASHES at runtime
// 'any' silences TypeScript completely. It's a trapdoor.

// ─── unknown — type-safe alternative to any ─────────────────────
let input: unknown = getUserInput();
input.toUpperCase();            // ❌ Error: Cannot call methods on unknown

// Must NARROW first before using:
if (typeof input === "string") {
    input.toUpperCase();        // ✅ TypeScript now knows it's string
}
if (input instanceof Date) {
    input.getFullYear();        // ✅ TypeScript now knows it's Date
}

// ─── void — function that returns nothing ───────────────────────
function logMessage(msg: string): void {
    console.log(msg);
    // no return statement, or return; with no value
}

// ─── never — function that NEVER returns ────────────────────────
function throwError(message: string): never {
    throw new Error(message);   // never reaches a return point
}

function infiniteLoop(): never {
    while (true) { }
}

// never for exhaustive checks — TypeScript's most powerful safety net
type Status = "pending" | "active" | "inactive";

function handleStatus(status: Status): string {
    switch (status) {
        case "pending":   return "Waiting...";
        case "active":    return "Running!";
        case "inactive":  return "Stopped.";
        default:
            // If you add "suspended" to Status and forget to handle it,
            // TypeScript gives a compile error HERE — not a runtime crash.
            const _exhaustive: never = status;
            throw new Error(`Unhandled status: ${status}`);
    }
}
```

**Quick rules:**
- `any` = "TypeScript, stop watching me" — use only as a last resort during migration
- `unknown` = "I don't know the type yet, but I'll check before using it" — PREFER this over `any`
- `void` = "this function exists for side effects, not return values"
- `never` = "this code path is unreachable" / "this function never completes normally"

---

### Slide 7 — Type Aliases

**Title:** Type Aliases — The `type` Keyword

**Key concept:** A type alias gives a reusable, named label to any type expression.

**Code:**

```typescript
// Aliasing primitives — adds domain vocabulary
type UserId = string;
type Price = number;
type IsActive = boolean;

const id: UserId = "usr_abc123";
const price: Price = 49.99;

// Aliasing object shapes
type Point = {
    x: number;
    y: number;
};

const origin: Point = { x: 0, y: 0 };

// Aliasing function signatures
type StringTransformer = (input: string) => string;
const toUpper: StringTransformer = (s) => s.toUpperCase();
const trim: StringTransformer = (s) => s.trim();

// Aliasing union types (covered next slide)
type Status = "pending" | "active" | "inactive";
type ID = string | number;

// Intersection type alias — combining multiple types with &
type Named = { name: string };
type Aged  = { age: number };
type Person = Named & Aged;     // Person must have BOTH name AND age

const alice: Person = { name: "Alice", age: 30 };

// Recursive type alias — shows the expressiveness of type aliases
type JSONValue =
    | string
    | number
    | boolean
    | null
    | JSONValue[]
    | { [key: string]: JSONValue };
```

**Naming convention:** Type aliases use **PascalCase** — `UserId`, `UserProfile`, `ApiResponse`, `StringTransformer`.

**Note:** Both `type` and `interface` can describe object shapes — the detailed comparison is in Part 2. For now: `type` can alias anything; `interface` is limited to object shapes.

---

### Slide 8 — Union Types and Literal Types

**Title:** Union Types and Literal Types

**Core idea:** A union type says "this value can be ONE of these types." A literal type constrains to a specific exact value.

**Code:**

```typescript
// ─── Union types — one of several types ─────────────────────────
function formatId(id: string | number): string {
    if (typeof id === "number") {
        return id.toString().padStart(6, "0");  // "000042"
    }
    return id;  // TypeScript knows it's string here (narrowing)
}

// ─── Literal types — specific values as types ───────────────────
type Direction = "north" | "south" | "east" | "west";
type DiceRoll  = 1 | 2 | 3 | 4 | 5 | 6;

function move(dir: Direction): void {
    console.log(`Moving ${dir}`);
}
move("north");    // ✅
move("up");       // ❌ Error: "up" not assignable to Direction ← compile-time catch!

// ─── Discriminated unions — TypeScript's most powerful pattern ──
// A shared literal field ("kind") tells TypeScript exactly which variant you have

type Circle    = { kind: "circle";    radius: number };
type Rectangle = { kind: "rectangle"; width: number; height: number };
type Triangle  = { kind: "triangle";  base: number; height: number };
type Shape = Circle | Rectangle | Triangle;

function getArea(shape: Shape): number {
    switch (shape.kind) {
        case "circle":
            return Math.PI * shape.radius ** 2;
            // TypeScript knows: shape is Circle
        case "rectangle":
            return shape.width * shape.height;
            // TypeScript knows: shape is Rectangle
        case "triangle":
            return (shape.base * shape.height) / 2;
            // TypeScript knows: shape is Triangle
    }
}

// ─── Intersection types — combining with & ──────────────────────
type Timestamped = { createdAt: Date; updatedAt: Date };
type EntityBase  = { id: string };
type UserProfile = { name: string; email: string };

type User = EntityBase & UserProfile & Timestamped;
// User must have: id, name, email, createdAt, updatedAt
```

**Why discriminated unions matter:** They are the TypeScript-native way to model type hierarchies without classes. Used constantly in Redux action types, React reducer patterns, and API response handling.

---

### Slide 9 — Object Types and Inline Shapes

**Title:** Object Types

**Code:**

```typescript
// Inline object type annotation — for one-off shapes
let user: { name: string; age: number; email?: string } = {
    name: "Alice",
    age: 30
    // email is optional — may or may not be present
};

// Accessing optional properties (connects to Day 14 optional chaining)
const upperEmail = user.email?.toUpperCase();  // string | undefined

// readonly modifier — prevents reassignment after initialization
let config: { readonly apiUrl: string; timeout: number } = {
    apiUrl: "https://api.example.com",
    timeout: 5000
};
config.apiUrl = "https://other.com";  // ❌ Error: readonly
config.timeout = 10000;               // ✅ timeout is writable

// Index signatures — when property names are dynamic
type HttpHeaders = { [headerName: string]: string };
const headers: HttpHeaders = {
    "Content-Type": "application/json",
    "Authorization": "Bearer token123"
};

// Nested object types
type Address = {
    street: string;
    city: string;
    state: string;
    zip: string;
};

type UserWithAddress = {
    name: string;
    email: string;
    address: Address;
    shippingAddress?: Address;   // optional nested object
};
```

**When to use inline type annotations:** For one-off shapes that won't be reused elsewhere in the codebase. For any shape that will be reused — use an interface (next slide).

---

### Slide 10 — Interfaces

**Title:** Interfaces — Contracts for Object Shapes

**Code:**

```typescript
// Defining an interface
interface User {
    readonly id: number;           // cannot be changed after assignment
    name: string;
    email: string;
    age?: number;                  // optional property
    greet(): string;               // method signature
    updateName(name: string): void;
}

// Using an interface
const user: User = {
    id: 1,
    name: "Alice",
    email: "alice@example.com",
    greet() { return `Hi, I'm ${this.name}`; },
    updateName(name) { this.name = name; }
};

// Extending interfaces — build compound contracts
interface Employee extends User {
    department: string;
    salary: number;
    startDate: Date;
}

// Multiple interface inheritance (interfaces support this; classes do not)
interface Serializable {
    serialize(): string;
}
interface Printable {
    print(): void;
}
interface Document extends Serializable, Printable {
    title: string;
    content: string;
}

// Declaration merging — unique to interface (not possible with type alias)
// Declaring the same interface name twice causes TypeScript to MERGE them
interface Window {
    myAnalytics: { track(event: string): void };
    // adds a property to the existing global Window type
}
// This is how @types/node and @types/react extend built-in browser types.

// Implementing an interface in a class (preview — Part 2 covers this in depth)
class ConcreteUser implements User {
    constructor(
        readonly id: number,
        public name: string,
        public email: string
    ) {}
    greet() { return `Hi, I'm ${this.name}`; }
    updateName(name: string) { this.name = name; }
}
```

**Declaration merging** is the key difference between `interface` and `type alias` that matters most for library authors and anyone augmenting global or third-party types.

---

### Slide 11 — Arrays and Tuples

**Title:** Arrays and Tuples

**Code:**

```typescript
// ─── Arrays — two equivalent syntaxes ──────────────────────────
let names: string[] = ["Alice", "Bob", "Charlie"];
let scores: Array<number> = [90, 85, 92];    // generic syntax (Part 2 covers generics)

// TypeScript infers array element type
const items = ["apple", "banana"];  // inferred: string[]
items.push(42);                     // ❌ Error: number not assignable to string[]

// Array of objects
interface Product { id: number; name: string; price: number; }
let products: Product[] = [];
products.push({ id: 1, name: "Widget", price: 9.99 });

// Readonly arrays — cannot add, remove, or replace elements
const PRIMES: readonly number[] = [2, 3, 5, 7, 11];
PRIMES.push(13);       // ❌ Error: push doesn't exist on readonly number[]
PRIMES[0] = 1;         // ❌ Error: index signature is readonly

// ─── Tuples — fixed-length, positionally typed arrays ───────────
type Point = [number, number];
const origin: Point = [0, 0];
const location: Point = [51.5074, -0.1278];     // [latitude, longitude]
const extra: Point = [1, 2, 3];                 // ❌ Error: too many elements

// Named tuples (TypeScript 4.0+) — more readable
type UserRecord = [id: number, name: string, active: boolean];
const record: UserRecord = [1, "Alice", true];
const [userId, userName, isActive] = record;    // destructuring works

// Optional tuple elements
type OptionalConfig = [host: string, port?: number];
const c1: OptionalConfig = ["localhost"];           // ✅
const c2: OptionalConfig = ["localhost", 3000];     // ✅

// Real-world tuple — React's useState returns a tuple
// const [count, setCount] = useState<number>(0);
// count is number, setCount is (n: number) => void
// TypeScript infers both types from the generic parameter
```

**Tuples are the right choice** when a function naturally returns exactly 2 (or 3) things with different types — `[data, error]`, `[value, setter]`, `[min, max]`.

---

### Slide 12 — Enums

**Title:** Enums — Named Sets of Constants

**Code:**

```typescript
// ─── Numeric enum (auto-increments from 0) ──────────────────────
enum Direction {
    Up,      // 0
    Down,    // 1
    Left,    // 2
    Right    // 3
}
console.log(Direction.Up);    // 0
console.log(Direction[0]);    // "Up"  ← reverse mapping (numeric enums only)

// Custom starting value
enum StatusCode {
    OK = 200,
    NotFound = 404,
    ServerError = 500
}

// ─── String enum (recommended over numeric) ─────────────────────
enum Color {
    Red   = "RED",
    Green = "GREEN",
    Blue  = "BLUE"
}
console.log(Color.Red);    // "RED" — readable in logs and serialized JSON

function paintWall(color: Color): void {
    console.log(`Painting with ${color}`);
}
paintWall(Color.Red);    // ✅
paintWall("RED");        // ❌ Error: string not assignable to Color

// ─── const enum — zero runtime overhead ─────────────────────────
// Fully inlined at compile time — no JavaScript object is generated
const enum Permission {
    Read  = "READ",
    Write = "WRITE",
    Admin = "ADMIN"
}

// ─── Modern alternative: as const object ────────────────────────
// Many production TypeScript codebases prefer this over enums
const STATUS = {
    PENDING:  "pending",
    ACTIVE:   "active",
    INACTIVE: "inactive"
} as const;

type Status = typeof STATUS[keyof typeof STATUS];
// Equivalent to: type Status = "pending" | "active" | "inactive"

function processStatus(status: Status) { /* ... */ }
processStatus(STATUS.ACTIVE);   // ✅
processStatus("active");        // ✅ — raw string literals also work
processStatus("unknown");       // ❌ Error
```

**Recommendation:**
- Use **string enums** when you want readable values in logs and JSON
- Use **`as const` objects** when you also want raw string literals to be accepted (no need to write `Color.Red`, just `"RED"` works)
- Avoid **numeric enums** — reverse mappings are confusing and they're easy to misuse

---

### Slide 13 — Type Assertions and Casting

**Title:** Type Assertions — "Trust Me, TypeScript"

**Code:**

```typescript
// ─── 'as' assertion — the primary syntax ────────────────────────
// Use when YOU know the type and TypeScript can't determine it

// DOM APIs return generic types; you know the specific element type
const usernameInput = document.getElementById("username") as HTMLInputElement;
usernameInput.value = "Alice";
// Without assertion: getElementById returns HTMLElement | null
// HTMLElement doesn't have .value — only HTMLInputElement does

// ─── Non-null assertion (!) ──────────────────────────────────────
// Tells TypeScript: "this is definitely NOT null or undefined"
const canvas = document.getElementById("myCanvas")!;
canvas.innerHTML = "";    // No error — TypeScript trusts the !
// Only use when you are certain the element exists

// ─── Angle bracket syntax — only in .ts (NOT .tsx) files ────────
const input = <HTMLInputElement>document.getElementById("username");
// Avoid in JSX/React projects — indistinguishable from JSX syntax

// ─── What you CANNOT do ─────────────────────────────────────────
const num = "42" as number;  // ❌ Error: string and number have no overlap

// Double assertion — emergency escape hatch (use very sparingly)
const forced = ("42" as unknown) as number;  // ✅ Compiles, but defeats TypeScript

// ─── The 'satisfies' operator (TypeScript 4.9+) ──────────────────
// Validates type WITHOUT widening — preserves the inferred specific type
const palette = {
    red:   [255, 0, 0],
    green: "#00ff00",
    blue:  [0, 0, 255]
} satisfies Record<string, string | number[]>;

// palette.red   is typed as number[]   — NOT string | number[]  ← specificity preserved
// palette.green is typed as string     — NOT string | number[]
// Adding "purple": 42 would be an Error (number doesn't satisfy string | number[])
```

**Four rules of type assertions:**
1. Prefer proper typing over assertions
2. Use `as` when you genuinely know something TypeScript can't determine (DOM API is the #1 legitimate use case)
3. Never use assertions to silence errors you haven't actually fixed — that's a bug waiting to happen
4. Use `satisfies` when you want to validate a type while keeping the most specific inferred type

---

### Slide 14 — Functions and Function Types

**Title:** Functions in TypeScript

**Code:**

```typescript
// ─── Basic typed function ────────────────────────────────────────
function add(a: number, b: number): number {
    return a + b;
}

// Arrow function with explicit return type
const multiply = (a: number, b: number): number => a * b;

// Return type often inferred — both are equivalent
const divide = (a: number, b: number) => a / b;  // inferred return: number

// ─── Optional parameters ─────────────────────────────────────────
function greet(name: string, greeting?: string): string {
    return `${greeting ?? "Hello"}, ${name}!`;
    // greeting is: string | undefined
}
greet("Alice");              // "Hello, Alice!"
greet("Alice", "Howdy");     // "Howdy, Alice!"

// ─── Default parameters ──────────────────────────────────────────
function createUser(name: string, role: string = "viewer") {
    return { name, role };
}
// TypeScript infers role's type from the default value: string

// ─── Rest parameters ─────────────────────────────────────────────
function sum(...numbers: number[]): number {
    return numbers.reduce((acc, n) => acc + n, 0);
}
sum(1, 2, 3, 4, 5);    // 15

// ─── Function type expressions ────────────────────────────────────
type Callback = (error: Error | null, data: string) => void;

function fetchData(url: string, callback: Callback): void {
    // ...
}

// ─── Function overloads — multiple signatures, one implementation ─
function parse(input: string): number;
function parse(input: number): string;
function parse(input: string | number): string | number {
    if (typeof input === "string") return parseInt(input, 10);
    return input.toString();
}

const n = parse("42");    // TypeScript knows: number
const s = parse(42);      // TypeScript knows: string

// ─── void return type ─────────────────────────────────────────────
function logEvent(event: string): void {
    console.log(`[EVENT] ${event}`);
    // no return value
}
```

**Key rules:**
- **Parameters always need explicit types** — TypeScript does not infer parameter types from usage
- **Return types can usually be inferred** — but annotating them is recommended for public APIs and exported functions
- **Overloads** are used when the return type depends on the argument type, like `document.createElement("canvas")` returning `HTMLCanvasElement` specifically

---

### Slide 15 — Readonly and Immutability

**Title:** `readonly` and `as const` — Immutability at the Type Level

**Code:**

```typescript
// ─── readonly on interface/object properties ─────────────────────
interface Config {
    readonly apiUrl: string;
    readonly timeout: number;
    retries: number;          // mutable
}
const config: Config = { apiUrl: "https://api.example.com", timeout: 5000, retries: 3 };
config.apiUrl = "https://other.com";  // ❌ Error: readonly
config.retries = 5;                   // ✅ not readonly

// ─── readonly arrays ─────────────────────────────────────────────
const nums: readonly number[] = [1, 2, 3];
// Also written as: ReadonlyArray<number>
nums.push(4);       // ❌
nums[0] = 99;       // ❌

// ─── as const — lock the entire expression to its literal types ──
const STATUS = {
    PENDING:  "pending",
    ACTIVE:   "active",
    INACTIVE: "inactive"
} as const;
// Without: { PENDING: string; ACTIVE: string; INACTIVE: string }
// With:    { readonly PENDING: "pending"; readonly ACTIVE: "active"; ... }

// Extract a union type from an as const object
type Status = typeof STATUS[keyof typeof STATUS];
// "pending" | "active" | "inactive"

// ─── as const on arrays ───────────────────────────────────────────
const VALID_ROLES = ["admin", "editor", "viewer"] as const;
// Type: readonly ["admin", "editor", "viewer"]
// Each element is its literal type — not just string

type Role = (typeof VALID_ROLES)[number];
// "admin" | "editor" | "viewer"

// ─── typeof in type position ──────────────────────────────────────
const defaultSettings = { host: "localhost", port: 3000, debug: false };
type AppSettings = typeof defaultSettings;
// { host: string; port: number; debug: boolean }

// keyof — get the union of property names of a type
type SettingKeys = keyof AppSettings;
// "host" | "port" | "debug"
```

**`as const` is the modern, lightweight alternative to enums** when you want string literal types without enum syntax overhead. Extremely common in production TypeScript.

---

### Slide 16 — Part 1 Summary and Part 2 Preview

**Title:** Part 1 Complete

**Summary checklist:**

| Topic | Key Takeaway |
|---|---|
| TypeScript basics | Compiles to JS; type errors caught before runtime; VS Code IntelliSense out of the box |
| Annotations vs inference | Trust inference for initialized values; annotate parameters and uninitialized variables |
| Primitive types | string, number, boolean, null, undefined — enable strictNullChecks always |
| Special types | `any` = escape hatch; `unknown` = safe alternative; `void` = no return; `never` = unreachable |
| Type aliases | `type` keyword; PascalCase; can alias anything including unions and intersections |
| Union & literal types | `\|` operator; discriminated unions are the most powerful narrowing pattern |
| Object types | Inline shapes for one-offs; interfaces for reusable contracts |
| Interfaces | `readonly`, optional `?`, `extends`, declaration merging |
| Arrays & tuples | `T[]` for arrays; tuples for fixed-length heterogeneous ordered data |
| Enums | String enums preferred; `as const` objects are a lightweight alternative |
| Type assertions | `as` for DOM and known-type situations; `satisfies` to validate without widening |
| Functions | Parameters always typed; return types inferred; overloads for type-dependent returns |
| `readonly` / `as const` | Immutability at the type level; literal types from const expressions |

**Coming up in Part 2:**
- **Generics** — write functions and classes that work with any type while staying fully type-safe
- **Classes in TypeScript** — take Day 14's ES6 classes and add access modifiers, `abstract`, `implements`
- **Decorators** — the exact mechanism behind Angular's `@Component`, `@Injectable`, and `@Input`
- **tsconfig.json** — configure the TypeScript compiler with strict settings
- **Utility types** — `Partial`, `Required`, `Readonly`, `Pick`, `Omit` and more
- **Type guards** — `typeof`, `instanceof`, custom `is` predicates for safe narrowing
- **Interface vs Type** — the definitive comparison and guidance
