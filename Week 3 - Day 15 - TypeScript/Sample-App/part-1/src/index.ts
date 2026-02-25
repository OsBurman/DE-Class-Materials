// Day 15 Part 1 — TypeScript Basics
// Topics: Type annotations, Interfaces, Union types, Enums, Tuples, Functions
// Run: npm install && npm start

// ── Type Annotations & Inference ─────────────────────────────
console.log("╔══════════════════════════════════════════════════════════════╗");
console.log("║  Day 15 Part 1 — TypeScript Basics                         ║");
console.log("╚══════════════════════════════════════════════════════════════╝\n");

demoBasicTypes();
demoSpecialTypes();
demoInterfaces();
demoUnionsAndAliases();
demoArraysAndTuples();
demoEnums();
demoFunctions();

// ─────────────────────────────────────────────────────────────
// 1. Basic Types & Type Annotations
// ─────────────────────────────────────────────────────────────
function demoBasicTypes(): void {
  console.log("=== 1. Basic Types ===");

  // Explicit annotations
  const name:    string  = "Alice";
  const age:     number  = 28;
  const active:  boolean = true;
  const score:   number  = 95.5;

  // Type inference — TypeScript infers the type
  const inferred = "TypeScript infers this is a string";

  console.log(`  name: ${name} (string)`);
  console.log(`  age:  ${age} (number)`);
  console.log(`  active: ${active} (boolean)`);
  console.log(`  TypeScript infers: ${typeof inferred}`);

  // Type casting / assertion
  const rawValue: unknown = "42";
  const asNumber = Number(rawValue as string);      // safe conversion
  const asString = rawValue as string;              // type assertion
  console.log(`  Type assertion: "${asString}" → ${asNumber}`);
  console.log();
}

// ─────────────────────────────────────────────────────────────
// 2. Special Types
// ─────────────────────────────────────────────────────────────
function demoSpecialTypes(): void {
  console.log("=== 2. Special Types ===");

  // any — opt out of type checking (avoid when possible)
  let anything: any = "start";
  anything = 42;
  anything = true;
  console.log("  any: can hold", anything, "(no type error)");

  // unknown — type-safe alternative to any; must narrow before use
  let unknownVal: unknown = "hello world";
  if (typeof unknownVal === "string") {
    console.log("  unknown (narrowed to string):", unknownVal.toUpperCase());
  }

  // void — function returns nothing
  function logMessage(msg: string): void { console.log("  void fn:", msg); }
  logMessage("no return value");

  // never — function never returns (throws or infinite loop)
  function throwError(msg: string): never { throw new Error(msg); }
  try { throwError("demo never type"); } catch (e) { console.log("  never fn threw:", (e as Error).message); }

  // null & undefined (strict mode separates them)
  let maybeNull: string | null = null;
  maybeNull = "now a string";
  console.log("  string | null:", maybeNull);
  console.log();
}

// ─────────────────────────────────────────────────────────────
// 3. Object Types & Interfaces
// ─────────────────────────────────────────────────────────────
interface Address {
  street: string;
  city:   string;
  zip?:   string;         // optional property
}

interface Student {
  readonly id:  number;   // readonly — cannot be changed after creation
  name:    string;
  age:     number;
  email:   string;
  address?: Address;      // optional nested interface
  grades:  number[];
}

// Extending interfaces
interface GraduateStudent extends Student {
  thesis:    string;
  advisor:   string;
}

function demoInterfaces(): void {
  console.log("=== 3. Interfaces ===");

  const alice: Student = {
    id:     1001,
    name:   "Alice",
    age:    22,
    email:  "alice@academy.com",
    grades: [88, 92, 95, 87],
    address: { street: "123 Main St", city: "Boston", zip: "02101" },
  };

  // alice.id = 999;  // ← TypeScript error: cannot assign to readonly

  const avg = alice.grades.reduce((a, b) => a + b) / alice.grades.length;
  console.log(`  ${alice.name} (id: ${alice.id}) avg grade: ${avg.toFixed(1)}`);
  console.log(`  Address: ${alice.address?.street}, ${alice.address?.city}`);

  const grad: GraduateStudent = {
    ...alice,
    id:      1002,
    name:    "Bob",
    email:   "bob@academy.com",
    thesis:  "Machine Learning in Java",
    advisor: "Prof. Smith",
    grades:  [90, 94, 98],
  };
  console.log(`  Graduate: ${grad.name} — thesis: "${grad.thesis}"`);
  console.log();
}

// ─────────────────────────────────────────────────────────────
// 4. Union Types & Type Aliases
// ─────────────────────────────────────────────────────────────
type ID      = string | number;         // type alias
type Status  = "active" | "inactive" | "pending";  // literal union
type Result<T> = { ok: true; value: T } | { ok: false; error: string };

function demoUnionsAndAliases(): void {
  console.log("=== 4. Union Types & Type Aliases ===");

  function formatId(id: ID): string {
    return typeof id === "number" ? `#${id.toString().padStart(5, "0")}` : id.toUpperCase();
  }
  console.log("  formatId(1001):", formatId(1001));
  console.log("  formatId('abc'):", formatId("abc"));

  const statuses: Status[] = ["active", "inactive", "pending"];
  statuses.forEach(s => {
    const label = s === "active" ? "✓" : s === "pending" ? "⏳" : "✗";
    console.log(`  Status ${s}: ${label}`);
  });

  // Generic Result type
  function divide(a: number, b: number): Result<number> {
    if (b === 0) return { ok: false, error: "Division by zero" };
    return { ok: true, value: a / b };
  }
  const r1 = divide(10, 2);
  const r2 = divide(5, 0);
  console.log("  divide(10,2):", r1.ok ? r1.value : r1.error);
  console.log("  divide(5,0):",  r2.ok ? r2.value : r2.error);
  console.log();
}

// ─────────────────────────────────────────────────────────────
// 5. Arrays & Tuples
// ─────────────────────────────────────────────────────────────
function demoArraysAndTuples(): void {
  console.log("=== 5. Arrays & Tuples ===");

  // Typed arrays
  const scores:  number[]  = [88, 92, 95, 77];
  const names:   string[]  = ["Alice", "Bob", "Carol"];
  const mixed:   Array<string | number> = ["Alice", 1001, "Bob", 1002];

  console.log("  number[]:", scores);
  console.log("  string[]:", names);
  console.log("  Array<string|number>:", mixed);

  // Tuples — fixed-length, fixed-type arrays
  const point:  [number, number]         = [10, 20];
  const record: [number, string, boolean] = [1, "Alice", true];
  const rgb:    [number, number, number]  = [255, 128, 0];

  console.log("  [number, number] point:", point);
  console.log("  [id, name, active]:", record);
  console.log("  RGB tuple:", rgb);

  // Named tuple elements (TS 4.0+)
  type RGB = [red: number, green: number, blue: number];
  const color: RGB = [200, 100, 50];
  console.log("  Named tuple RGB:", color);
  console.log();
}

// ─────────────────────────────────────────────────────────────
// 6. Enums
// ─────────────────────────────────────────────────────────────
enum Direction { Up = "UP", Down = "DOWN", Left = "LEFT", Right = "RIGHT" }
enum HttpStatus { OK = 200, Created = 201, BadRequest = 400, Unauthorized = 401, NotFound = 404 }
const enum Role  { Admin, Editor, Viewer }   // const enum — erased at runtime

function demoEnums(): void {
  console.log("=== 6. Enums ===");

  console.log("  Direction.Up:", Direction.Up);
  console.log("  HttpStatus.NotFound:", HttpStatus.NotFound);

  function handleStatus(code: HttpStatus): string {
    switch (code) {
      case HttpStatus.OK:           return "Success";
      case HttpStatus.Created:      return "Resource created";
      case HttpStatus.BadRequest:   return "Bad request";
      case HttpStatus.Unauthorized: return "Auth required";
      case HttpStatus.NotFound:     return "Not found";
    }
  }
  console.log("  handleStatus(200):", handleStatus(HttpStatus.OK));
  console.log("  handleStatus(404):", handleStatus(HttpStatus.NotFound));

  const userRole: Role = Role.Editor;
  console.log("  Role.Editor (const enum):", userRole);
  console.log();
}

// ─────────────────────────────────────────────────────────────
// 7. Functions & Function Types
// ─────────────────────────────────────────────────────────────
function demoFunctions(): void {
  console.log("=== 7. Functions & Function Types ===");

  // Typed parameters & return type
  function add(a: number, b: number): number { return a + b; }

  // Optional & default parameters
  function greet(name: string, greeting: string = "Hello", title?: string): string {
    return `${greeting}, ${title ? title + " " : ""}${name}!`;
  }

  // Rest parameters
  function sum(...nums: number[]): number { return nums.reduce((a, b) => a + b, 0); }

  // Function type alias
  type Transformer = (value: number) => number;
  const double:   Transformer = n => n * 2;
  const addTax:   Transformer = n => n * 1.1;

  // Higher-order function
  function applyAll(value: number, ...fns: Transformer[]): number {
    return fns.reduce((v, fn) => fn(v), value);
  }

  console.log("  add(5, 3):", add(5, 3));
  console.log("  greet('Alice'):", greet("Alice"));
  console.log("  greet('Smith', 'Hi', 'Dr.'):", greet("Smith", "Hi", "Dr."));
  console.log("  sum(1,2,3,4,5):", sum(1, 2, 3, 4, 5));
  console.log("  applyAll(100, double, addTax):", applyAll(100, double, addTax));
  console.log("\n✓ TypeScript Basics Part 1 demo complete.");
}
