// ============================================================
// Day 15 — Part 1  |  01-types-and-basics.ts
// TypeScript Basics, Type Annotations, Simple Types,
// Special Types, Interfaces, Unions, Aliases, Arrays,
// Tuples, Enums, Type Casting, Functions
// ============================================================
// Run with:  npx ts-node 01-types-and-basics.ts
// Compile :  npx tsc 01-types-and-basics.ts --target ES2020 --strict


// ============================================================
// 1. TYPESCRIPT BASICS & BENEFITS
// ============================================================
// TypeScript is a *statically typed superset* of JavaScript.
// Every valid JavaScript file is also valid TypeScript.
//
// Key benefits:
//   • Catch type errors at COMPILE time, not runtime
//   • Better IDE autocomplete and IntelliSense
//   • Self-documenting code — the types tell you what a function
//     expects and returns without reading the docs
//   • Safer refactoring — the compiler finds every call site
//   • Industry standard for Angular; very common in React/Node
//
// How it works:
//   TypeScript source (.ts) ──[tsc compiler]──► JavaScript (.js)
//   The browser/Node only ever runs the compiled JavaScript.


// ============================================================
// 2. TYPE ANNOTATIONS & TYPE INFERENCE
// ============================================================

// Explicit annotation — you tell TypeScript the type
let username: string = "Alice";
let score: number = 95;
let isLoggedIn: boolean = true;

// Type inference — TypeScript figures it out from the initial value
let city = "New York";   // inferred as string
let age  = 30;           // inferred as number
let active = true;       // inferred as boolean

// After inference, the type is locked — you can't reassign to a different type:
// city = 42;  // ✗ Error: Type 'number' is not assignable to type 'string'

// Annotation is useful when you declare a variable before assigning it
let winner: string;
winner = "Bob"; // TypeScript knows this must be a string

// Also useful to make intent clear for reviewers:
let retryCount: number = 0;


// ============================================================
// 3. SIMPLE TYPES: string, number, boolean
// ============================================================

function greet(name: string): string {
  return `Hello, ${name}!`;
}
console.log(greet("Alice")); // Hello, Alice!
// greet(42);  // ✗ Compile error: Argument of type 'number' is not assignable to parameter of type 'string'

function add(a: number, b: number): number {
  return a + b;
}
console.log(add(3, 7)); // 10

function isAdult(age: number): boolean {
  return age >= 18;
}
console.log(isAdult(20)); // true
console.log(isAdult(15)); // false

// TypeScript knows the return type of standard operations:
const pi: number = Math.PI;
const greeting: string = `The value of pi is ${pi.toFixed(2)}`;
const isPositive: boolean = pi > 0;


// ============================================================
// 4. SPECIAL TYPES: any, unknown, never, void
// ============================================================

// --- 4a. any ---
// Opts OUT of type checking — escape hatch, avoid when possible
let anything: any = "hello";
anything = 42;          // OK — no type checking
anything = { x: 1 };   // OK — no type checking
anything.doAnything();  // OK at compile time — but will crash at runtime!

// When to use: migrating existing JS code, working with third-party
// libraries that have poor types, genuinely dynamic data

// --- 4b. unknown ---
// Like `any`, but TYPE-SAFE — you must narrow before using the value
let userInput: unknown = "hello";

// ✗ You CANNOT do this with unknown:
// console.log(userInput.toUpperCase()); // Error: Object is of type 'unknown'

// ✓ You MUST narrow first:
if (typeof userInput === "string") {
  console.log(userInput.toUpperCase()); // now TypeScript knows it's a string
}

// unknown is the right choice when you receive external/untyped data
function processInput(input: unknown): string {
  if (typeof input === "string") return input.trim();
  if (typeof input === "number") return input.toFixed(2);
  return String(input);
}

// --- 4c. void ---
// Return type for functions that don't return a meaningful value
function logMessage(msg: string): void {
  console.log(`[LOG] ${msg}`);
  // No return statement (or an empty return)
}
logMessage("Server started");

// void is NOT the same as undefined — a void function just means
// "the caller shouldn't depend on its return value"

// --- 4d. never ---
// For code paths that should NEVER be reached
// Used in: functions that always throw, exhaustive switch checks

function fail(msg: string): never {
  throw new Error(msg); // always throws — never returns normally
}

// Exhaustive type guard (more below in the Union section)
type Direction = "north" | "south" | "east" | "west";

function move(direction: Direction): string {
  switch (direction) {
    case "north": return "Moving north";
    case "south": return "Moving south";
    case "east":  return "Moving east";
    case "west":  return "Moving west";
    default:
      // If you ever add a new Direction and forget to handle it here,
      // TypeScript will warn you because `direction` won't be `never`
      const exhaustiveCheck: never = direction;
      return fail(`Unhandled direction: ${exhaustiveCheck}`);
  }
}
console.log(move("north")); // Moving north


// ============================================================
// 5. OBJECT TYPES & INTERFACES
// ============================================================

// --- 5a. Inline object type ---
let user: { name: string; age: number; email: string } = {
  name: "Alice",
  age: 30,
  email: "alice@example.com",
};

// --- 5b. interface — the preferred way to describe object shapes ---
interface User {
  id: number;
  name: string;
  email: string;
  age?: number;       // ? = optional property
  readonly createdAt: Date; // readonly = cannot be reassigned after creation
}

const alice: User = {
  id: 1,
  name: "Alice",
  email: "alice@example.com",
  createdAt: new Date("2024-01-01"),
};

// alice.createdAt = new Date(); // ✗ Error: Cannot assign to 'createdAt' because it is a read-only property

// Optional property — alice.age is undefined (no error):
console.log(alice.age); // undefined

// --- 5c. Interface extending ---
interface Admin extends User {
  role: "super" | "regular";
  permissions: string[];
}

const adminUser: Admin = {
  id: 2,
  name: "Bob",
  email: "bob@example.com",
  createdAt: new Date(),
  role: "super",
  permissions: ["read", "write", "delete"],
};

// --- 5d. Interface for functions ---
interface Formatter {
  (value: string, width: number): string;
}

const padLeft: Formatter = (value, width) =>
  value.padStart(width, " ");

console.log(padLeft("hello", 10)); // "     hello"

// --- 5e. Interface declaration merging ---
// Interfaces can be declared multiple times — TypeScript merges them
// (This is unique to interfaces — types can't do this)
interface Product {
  id: number;
  name: string;
}
interface Product {
  price: number; // merged in!
}

const item: Product = { id: 1, name: "Widget", price: 9.99 };


// ============================================================
// 6. UNION TYPES & TYPE ALIASES
// ============================================================

// --- 6a. Union types (|) ---
// A value that can be one of several types
let id: number | string;
id = 101;       // OK
id = "user-42"; // OK
// id = true;   // ✗ Error

function formatId(id: number | string): string {
  if (typeof id === "number") {
    return id.toString().padStart(6, "0"); // "000101"
  }
  return id.toUpperCase(); // "USER-42"
}
console.log(formatId(42));       // 000042
console.log(formatId("user-7")); // USER-7

// --- 6b. Type aliases (type keyword) ---
// Name any type — primitives, unions, tuples, objects, functions
type ID = number | string;
type Status = "active" | "inactive" | "suspended"; // string literal union

let userStatus: Status = "active";
// userStatus = "deleted"; // ✗ Error — not one of the three values

// Type alias for an object
type Point = {
  x: number;
  y: number;
};

function distanceFromOrigin(p: Point): number {
  return Math.sqrt(p.x ** 2 + p.y ** 2);
}
console.log(distanceFromOrigin({ x: 3, y: 4 })); // 5

// --- 6c. Discriminated unions (tagged unions) ---
// A union where each member has a common literal field that identifies it
type Circle    = { kind: "circle";    radius: number };
type Rectangle = { kind: "rectangle"; width: number; height: number };
type Triangle  = { kind: "triangle";  base: number;  height: number };
type Shape = Circle | Rectangle | Triangle;

function area(shape: Shape): number {
  switch (shape.kind) {
    case "circle":    return Math.PI * shape.radius ** 2;
    case "rectangle": return shape.width * shape.height;
    case "triangle":  return 0.5 * shape.base * shape.height;
  }
}

console.log(area({ kind: "circle", radius: 5 }).toFixed(2));         // 78.54
console.log(area({ kind: "rectangle", width: 4, height: 6 }));       // 24
console.log(area({ kind: "triangle", base: 3, height: 8 }));         // 12


// ============================================================
// 7. ARRAYS & TUPLES
// ============================================================

// --- 7a. Arrays ---
// Two equivalent syntaxes:
const names: string[] = ["Alice", "Bob", "Carol"];
const scores: Array<number> = [95, 87, 72]; // generic syntax

// TypeScript prevents mixing types:
// names.push(42); // ✗ Error: Argument of type 'number' is not assignable

// Array of objects:
const users: User[] = [
  { id: 1, name: "Alice", email: "alice@example.com", createdAt: new Date() },
  { id: 2, name: "Bob",   email: "bob@example.com",   createdAt: new Date() },
];
console.log(users.map(u => u.name)); // ["Alice", "Bob"]

// readonly array — cannot be mutated
const CONSTANTS: readonly string[] = ["READ_ONLY", "IMMUTABLE"];
// CONSTANTS.push("new"); // ✗ Error

// --- 7b. Tuples ---
// A fixed-length array where each position has a specific type
type Coordinate = [number, number];            // exactly 2 numbers
type NamedCoord = [string, number, number];    // label, x, y
type RGB         = [number, number, number];   // red, green, blue

const origin: Coordinate = [0, 0];
const nyc: NamedCoord   = ["New York", 40.71, -74.01];
const red: RGB           = [255, 0, 0];

// Destructure a tuple:
const [city2, latitude, longitude] = nyc;
console.log(`${city2}: ${latitude}, ${longitude}`);

// Optional tuple elements:
type HttpResponse = [number, string, string?]; // [status, message, body?]
const ok: HttpResponse     = [200, "OK", '{"data": []}'];
const noBody: HttpResponse = [204, "No Content"];

// Tuple with rest elements:
type StringThenNumbers = [string, ...number[]];
const series: StringThenNumbers = ["scores", 95, 87, 72];

// ⚠️ Tuples are stricter than arrays:
// const badCoord: Coordinate = [1, 2, 3]; // ✗ Error: too many elements


// ============================================================
// 8. ENUMS
// ============================================================

// --- 8a. Numeric enum (default) ---
enum Direction {
  North, // = 0
  South, // = 1
  East,  // = 2
  West,  // = 3
}

console.log(Direction.North);       // 0
console.log(Direction[0]);          // "North" — reverse mapping!
console.log(Direction.East === 2);  // true

function getOpposite(dir: Direction): Direction {
  switch (dir) {
    case Direction.North: return Direction.South;
    case Direction.South: return Direction.North;
    case Direction.East:  return Direction.West;
    case Direction.West:  return Direction.East;
  }
}

// --- 8b. Enum with explicit values ---
enum HttpStatus {
  OK             = 200,
  Created        = 201,
  BadRequest     = 400,
  Unauthorized   = 401,
  NotFound       = 404,
  InternalError  = 500,
}
console.log(HttpStatus.NotFound); // 404

// --- 8c. String enum ---
enum LogLevel {
  Debug   = "DEBUG",
  Info    = "INFO",
  Warn    = "WARN",
  Error   = "ERROR",
}

function log(level: LogLevel, message: string): void {
  console.log(`[${level}] ${message}`);
}
log(LogLevel.Info, "Server started on port 3000");
log(LogLevel.Error, "Database connection failed");

// --- 8d. const enum (inlined at compile time — no reverse mapping) ---
const enum Color {
  Red   = "RED",
  Green = "GREEN",
  Blue  = "BLUE",
}
const favorite: Color = Color.Blue;
// In compiled JS, Color.Blue is replaced with the string "BLUE" directly — no object created


// ============================================================
// 9. TYPE CASTING & ASSERTIONS
// ============================================================

// Type assertions tell the compiler "trust me, I know what this is"
// They do NOT do runtime conversion — just tell TS to treat the value as that type

// Syntax 1: `as` keyword (preferred in .tsx files and modern TS)
const input = document.getElementById("username") as HTMLInputElement;
// Now we can access input.value without a type error

// Syntax 2: angle-bracket (not allowed in JSX files)
// const input2 = <HTMLInputElement>document.getElementById("username");

// Non-null assertion (!) — tells TS "this is not null or undefined"
const button = document.querySelector("#submit-btn")!; // ! asserts non-null
// Without !, button would be HTMLElement | null and you'd need a null check

// Double assertion — force a type through unknown when TS won't allow a direct cast
// Use sparingly — this bypasses type safety
const payload = { data: "hello" };
const asString = payload as unknown as string; // two-step assertion

// Practical: parsing JSON from an API
interface ApiResponse {
  users: User[];
  total: number;
}

function parseApiResponse(json: string): ApiResponse {
  const data = JSON.parse(json) as ApiResponse;
  return data;
}

// Safer alternative: use unknown and validate
function safeParseApiResponse(json: string): ApiResponse | null {
  try {
    const data: unknown = JSON.parse(json);
    // In production: use a library like zod/yup for runtime validation
    return data as ApiResponse;
  } catch {
    return null;
  }
}

// ⚠️ Type assertions do NOT check at runtime:
const value = "hello" as unknown as number; // TS is happy, but value is still "hello" at runtime!


// ============================================================
// 10. FUNCTIONS & FUNCTION TYPES
// ============================================================

// --- 10a. Parameter and return type annotations ---
function multiply(a: number, b: number): number {
  return a * b;
}

// --- 10b. Optional parameters (?) ---
function buildUrl(base: string, path: string, query?: string): string {
  const url = `${base}/${path}`;
  return query ? `${url}?${query}` : url;
}
console.log(buildUrl("https://api.example.com", "users"));
console.log(buildUrl("https://api.example.com", "users", "page=2&limit=10"));

// --- 10c. Default parameters ---
function createUser(name: string, role: string = "viewer", active: boolean = true) {
  return { name, role, active };
}
console.log(createUser("Alice"));            // { name: "Alice", role: "viewer", active: true }
console.log(createUser("Bob", "admin"));     // { name: "Bob", role: "admin", active: true }

// --- 10d. Rest parameters ---
function sum(...numbers: number[]): number {
  return numbers.reduce((acc, n) => acc + n, 0);
}
console.log(sum(1, 2, 3, 4, 5)); // 15

// --- 10e. Function type annotations ---
type MathOperation = (a: number, b: number) => number;
type StringTransformer = (input: string) => string;

const add2: MathOperation = (a, b) => a + b;
const square: MathOperation = (a, _b) => a * a; // _b = unused param convention

const capitalize: StringTransformer = s => s.charAt(0).toUpperCase() + s.slice(1);

// --- 10f. Function overloads ---
// Provide multiple type signatures for a single function
function formatValue(value: string): string;
function formatValue(value: number): string;
function formatValue(value: boolean): string;
function formatValue(value: string | number | boolean): string {
  // Implementation signature — not directly callable
  if (typeof value === "string")  return `"${value}"`;
  if (typeof value === "number")  return value.toLocaleString();
  return value ? "Yes" : "No";
}

console.log(formatValue("hello"));  // "hello"
console.log(formatValue(1234567));  // 1,234,567
console.log(formatValue(true));     // Yes

// --- 10g. Arrow functions and callbacks ---
const numbers2 = [1, 2, 3, 4, 5];
const doubled: number[] = numbers2.map((n: number): number => n * 2);
const evens: number[]   = numbers2.filter((n): boolean => n % 2 === 0);

// --- 10h. void vs undefined in callbacks ---
// void means "the return value will be ignored" — the function CAN return something
type Callback = () => void;
const doSomething: Callback = () => 42; // OK! void means caller ignores return value

// --- 10i. never in functions ---
function assertNever(value: never): never {
  throw new Error(`Unexpected value: ${JSON.stringify(value)}`);
}
// This is used in exhaustive checks — if you reach it, it's a bug

// --- 10j. Generic function (preview — covered in detail in Part 2) ---
function identity<T>(value: T): T {
  return value;
}
console.log(identity<string>("hello")); // hello
console.log(identity<number>(42));      // 42
// TypeScript can also infer the type:
console.log(identity("inferred"));      // hello
