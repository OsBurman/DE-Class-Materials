# Day 15 — TypeScript
## Quick Reference Guide

---

## 1. TypeScript vs JavaScript

| | JavaScript | TypeScript |
|---|---|---|
| Type checking | Runtime | Compile-time |
| Error detection | After execution | Before running |
| IDE support | Basic | Rich (autocomplete, refactoring) |
| Output | Runs directly | Compiled to JS |
| Learning curve | Lower | Slightly higher |
| **Use when** | Quick scripts | Any production codebase |

```bash
npm install -g typescript
tsc --init          # create tsconfig.json
tsc                 # compile all ts files
tsc --watch         # watch mode
npx ts-node file.ts # run without explicit compile step
```

---

## 2. Basic Type Annotations

```ts
// Primitives
let name:    string  = "Alice";
let age:     number  = 30;        // includes integers and floats
let active:  boolean = true;
let id:      bigint  = 42n;
let sym:     symbol  = Symbol("key");

// Special types
let anything: any     = "foo";   // ❌ defeats type checking — avoid
let safe:     unknown = "foo";   // ✅ must narrow before use
let nothing:  void    = undefined; // function returns nothing
let neverEnd: never;             // unreachable code, exhaustive checks

// Null and undefined
let maybeNull:  string | null      = null;
let maybeUndef: string | undefined = undefined;
```

---

## 3. Type Inference

TypeScript **infers** types from assigned values — explicit annotation often unnecessary:

```ts
let count = 0;         // inferred: number
let label = "hello";   // inferred: string
const PI  = 3.14159;   // inferred: 3.14159 (literal type)

function add(a: number, b: number) {
    return a + b;      // return type inferred: number
}

// Annotate when inference is wrong or not possible
let result;            // inferred: any — specify the type
let result: string;
```

---

## 4. Arrays & Tuples

```ts
// Arrays
let nums:    number[]          = [1, 2, 3];
let strs:    Array<string>     = ["a", "b"];
let mixed:   (string | number)[] = ["a", 1, "b"];

// Readonly array
const config: readonly string[] = ["dev", "prod"];
// config.push("test"); ❌ — immutable

// Tuples — fixed-length array with specific types per position
let point: [number, number]       = [10, 20];
let entry: [string, number, boolean] = ["Alice", 30, true];

// Named tuple elements (TypeScript 4.0+)
type RGB = [red: number, green: number, blue: number];
const white: RGB = [255, 255, 255];

// Optional + rest in tuples
type Flex = [string, number?, ...boolean[]];
```

---

## 5. Interfaces

```ts
interface User {
    readonly id: number;      // read-only after creation
    name: string;
    email?: string;           // optional property
    greet(): string;
}

// Implement an interface
class Employee implements User {
    constructor(public readonly id: number, public name: string) {}
    greet() { return `Hi, I'm ${this.name}`; }
}

// Extend an interface
interface Admin extends User {
    permissions: string[];
}

// Index signature — dynamic keys
interface StringMap {
    [key: string]: string;
}

// Call signature — describe callable objects
interface Logger {
    (message: string): void;
    level: string;
}
```

---

## 6. Type Aliases

```ts
type Point    = { x: number; y: number };
type ID       = string | number;
type Callback = (err: Error | null, result?: string) => void;
type Tree<T>  = { value: T; left?: Tree<T>; right?: Tree<T> };
```

---

## 7. Interfaces vs Type Aliases

| | `interface` | `type` |
|---|---|---|
| Extend | `extends` | `&` (intersection) |
| Implement | `implements` | `implements` (object types only) |
| Declaration merging | ✅ Yes (auto-merged) | ❌ No |
| Primitives / unions | ❌ No | ✅ Yes |
| Computed keys | Limited | ✅ Mapped types |
| **Prefer** | Object shapes, API contracts | Unions, intersections, utilities |

```ts
// Declaration merging (only interfaces)
interface Window { myProp: string; }
interface Window { anotherProp: number; }
// merged into one interface ✓

// Intersection type (type aliases)
type A = { a: string };
type B = { b: number };
type AB = A & B;   // { a: string; b: number }
```

---

## 8. Union & Intersection Types

```ts
// Union — value can be ONE of the listed types
type StringOrNumber = string | number;
function format(val: StringOrNumber) {
    if (typeof val === "string") return val.toUpperCase();
    return val.toFixed(2);
}

// Discriminated union — a "tag" field narrows the type
type Shape =
    | { kind: "circle";  radius: number }
    | { kind: "square";  side: number   }
    | { kind: "rect";    width: number; height: number };

function area(s: Shape): number {
    switch (s.kind) {
        case "circle": return Math.PI * s.radius ** 2;
        case "square": return s.side ** 2;
        case "rect":   return s.width * s.height;
    }
}

// Intersection — value must satisfy ALL types
type AdminUser = User & Admin;
```

---

## 9. Type Guards

```ts
// typeof guard
function process(val: string | number) {
    if (typeof val === "string") val.toUpperCase(); // val: string here
    else                         val.toFixed(2);   // val: number here
}

// instanceof guard
if (err instanceof TypeError) { err.message; }

// in operator guard
function handle(animal: Dog | Cat) {
    if ("bark" in animal) animal.bark();   // narrowed to Dog
    else                  animal.meow();   // narrowed to Cat
}

// Custom type guard (type predicate)
function isString(val: unknown): val is string {
    return typeof val === "string";
}
if (isString(input)) input.toUpperCase(); // narrowed to string

// Truthiness narrowing
function print(val: string | null | undefined) {
    if (val) console.log(val.toUpperCase()); // string only
}
```

---

## 10. Generics

```ts
// Generic function
function identity<T>(val: T): T { return val; }
identity<string>("hello");   // explicit
identity(42);                // inferred as number

// Generic with constraint
function getLength<T extends { length: number }>(val: T): number {
    return val.length;
}
getLength("hello");    // 5
getLength([1, 2, 3]);  // 3

// Generic interface
interface Repository<T> {
    findById(id: number): T | undefined;
    save(item: T): void;
    getAll(): T[];
}

// Generic class
class Stack<T> {
    private items: T[] = [];
    push(item: T) { this.items.push(item); }
    pop(): T | undefined { return this.items.pop(); }
}

// Multiple type params
function zip<A, B>(a: A[], b: B[]): [A, B][] {
    return a.map((item, i) => [item, b[i]]);
}
```

---

## 11. Enums

```ts
// Numeric enum (default — starts at 0)
enum Direction { Up, Down, Left, Right }
Direction.Up;       // 0
Direction[0];       // "Up"

// String enum (preferred — better debugging)
enum Status {
    Pending  = "PENDING",
    Active   = "ACTIVE",
    Inactive = "INACTIVE"
}
Status.Active;      // "ACTIVE"

// Const enum — inlined at compile time (no runtime object)
const enum LogLevel { Debug, Info, Warn, Error }
// Compiled to: LogLevel.Error → 3 (literal)
```

---

## 12. Utility Types

```ts
interface User { id: number; name: string; email: string; age?: number; }

Partial<User>              // all properties optional
Required<User>             // all properties required
Readonly<User>             // all properties readonly
Pick<User, "id" | "name">  // { id: number; name: string }
Omit<User, "email" | "age"> // all except email and age
Record<string, User>       // { [key: string]: User }

// Conditional / extraction
Exclude<string | number | boolean, boolean>  // string | number
Extract<string | number | boolean, string | number> // string | number
NonNullable<string | null | undefined>       // string
ReturnType<typeof fetchUser>                 // return type of fetchUser
Parameters<typeof fetchUser>                 // param types as tuple
InstanceType<typeof MyClass>                 // MyClass instance type
```

---

## 13. tsconfig.json Key Options

```json
{
  "compilerOptions": {
    "target": "ES2022",          // compiled JS version
    "module": "CommonJS",        // module system (CommonJS, ESNext, etc.)
    "lib": ["ES2022", "DOM"],    // built-in type definitions
    "rootDir": "./src",          // input files location
    "outDir": "./dist",          // output location
    "strict": true,              // ✅ enable ALL strict checks (recommended)
    "noImplicitAny": true,       // error on inferred 'any' (included in strict)
    "strictNullChecks": true,    // null/undefined not assignable to other types
    "noUnusedLocals": true,      // error on unused variables
    "noUnusedParameters": true,  // error on unused function parameters
    "esModuleInterop": true,     // better CommonJS interop (default imports)
    "resolveJsonModule": true,   // import .json files
    "declaration": true,         // generate .d.ts files
    "sourceMap": true,           // generate .map files for debugging
    "baseUrl": ".",              // base for non-relative module paths
    "paths": {
      "@/*": ["src/*"]           // path aliases
    }
  },
  "include": ["src/**/*"],
  "exclude": ["node_modules", "dist"]
}
```

---

## 14. Non-Null Assertion & Type Casting

```ts
// Non-null assertion (!) — tells TS: "I know this is not null"
const el = document.getElementById("app")!;   // HTMLElement, not null
el.textContent = "Hello";

// as — type assertion (not a runtime cast)
const input = event.target as HTMLInputElement;
input.value;

// Double assertion (escape hatch — use sparingly)
const el = document.getElementById("app") as unknown as SVGElement;

// Satisfies operator (TypeScript 4.9+) — check without widening type
const config = {
    port: 3000,
    host: "localhost"
} satisfies Partial<ServerConfig>;
```
