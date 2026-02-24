// ============================================================
// Day 15 — Part 2  |  02-decorators-utility-types-guards.ts
// Decorators, Utility Types, Type Guards, Interface vs Type
// ============================================================
// Run with:  npx ts-node --experimentalDecorators 02-decorators-utility-types-guards.ts
// Or set experimentalDecorators: true in tsconfig.json

// ============================================================
// 1. DECORATORS — Overview
// ============================================================
// Decorators are a STAGE 3 ECMAScript proposal.
// They are enabled via:
//   "experimentalDecorators": true  in tsconfig.json  (legacy TS decorators)
//   "useDefineForClassFields": false (needed for legacy decorators)
//
// You already know decorators from Angular:
//   @Component, @NgModule, @Injectable, @Input, @Output
//
// A decorator is a function that receives a class/method/property
// and returns a modified version (or augments it in place).

// --- 1a. Class Decorator ---
// Receives: the constructor function
// Can return: a new constructor (to replace), or void (to augment)

function Singleton<T extends { new (...args: any[]): {} }>(constructor: T) {
  let instance: T;
  return class extends (constructor as any) {
    constructor(...args: any[]) {
      if (instance) return instance;
      super(...args);
      instance = this as any;
    }
  };
}

function Serializable(constructor: Function) {
  constructor.prototype.toJSON = function () {
    return JSON.stringify(this);
  };
  console.log(`[Decorator] ${constructor.name} is now Serializable`);
}

function Log(constructor: Function) {
  console.log(`[Decorator] Class "${constructor.name}" was decorated`);
}

// Stack decorators from bottom to top (Log runs first, then Serializable):
@Log
@Serializable
class UserService {
  constructor(public name: string = "UserService") {}
  getUsers(): string[] {
    return ["Alice", "Bob"];
  }
}

const svc = new UserService();
console.log((svc as any).toJSON()); // {"name":"UserService"}


// --- 1b. Method Decorator ---
// Receives: target (prototype), property name, property descriptor
// Can modify: the descriptor to wrap the original method

function MeasureTime(
  target: any,
  propertyKey: string,
  descriptor: PropertyDescriptor
): PropertyDescriptor {
  const originalMethod = descriptor.value;

  descriptor.value = function (...args: any[]) {
    const start = performance.now();
    const result = originalMethod.apply(this, args);
    const end = performance.now();
    console.log(`[Timer] ${propertyKey} took ${(end - start).toFixed(2)}ms`);
    return result;
  };

  return descriptor;
}

function Deprecated(message: string) {
  return function (target: any, propertyKey: string, descriptor: PropertyDescriptor) {
    const originalMethod = descriptor.value;
    descriptor.value = function (...args: any[]) {
      console.warn(`⚠️  DEPRECATED: ${propertyKey} — ${message}`);
      return originalMethod.apply(this, args);
    };
    return descriptor;
  };
}

function Validate(target: any, propertyKey: string, descriptor: PropertyDescriptor) {
  const originalMethod = descriptor.value;
  descriptor.value = function (...args: any[]) {
    for (const arg of args) {
      if (arg === null || arg === undefined) {
        throw new Error(`[Validate] ${propertyKey} received null/undefined argument`);
      }
    }
    return originalMethod.apply(this, args);
  };
  return descriptor;
}

class DataProcessor {
  @MeasureTime
  processLargeDataset(size: number): number[] {
    // Simulate work
    const result: number[] = [];
    for (let i = 0; i < size; i++) result.push(i * 2);
    return result;
  }

  @Deprecated("Use processLargeDataset() instead")
  processData(data: number[]): number[] {
    return data.map(n => n * 2);
  }

  @Validate
  divide(a: number, b: number): number {
    if (b === 0) throw new Error("Division by zero");
    return a / b;
  }
}

const processor = new DataProcessor();
const result = processor.processLargeDataset(10000);
console.log(`Processed ${result.length} items`);

processor.processData([1, 2, 3]); // logs deprecation warning

console.log(processor.divide(10, 2)); // 5
try {
  processor.divide(10, null as any); // throws from @Validate
} catch (e) {
  console.error((e as Error).message);
}


// --- 1c. Property Decorator ---
// Receives: target (prototype for instance properties, constructor for static)
//           and the property name

function Required(target: any, propertyKey: string) {
  // Track required fields
  const required: string[] = Reflect.getMetadata?.("required", target) ?? [];
  required.push(propertyKey);
  Reflect.defineMetadata?.("required", required, target);
}

// Simpler property decorator — attach metadata as a plain property
function Default(defaultValue: any) {
  return function (target: any, propertyKey: string) {
    target[propertyKey] = defaultValue; // sets prototype default
    console.log(`[Decorator] Default value "${defaultValue}" applied to ${propertyKey}`);
  };
}

class AppConfig {
  @Default("localhost")
  host!: string;

  @Default(3000)
  port!: number;

  @Default(false)
  debug!: boolean;
}

const config = new AppConfig();
console.log(config.host);  // localhost
console.log(config.port);  // 3000
console.log(config.debug); // false


// ============================================================
// 2. UTILITY TYPES
// ============================================================
// TypeScript ships with built-in "utility types" that transform
// existing types. No need to define them — they're built in.

interface UserFull {
  id: number;
  name: string;
  email: string;
  password: string;
  role: "admin" | "viewer" | "editor";
  createdAt: Date;
  updatedAt: Date;
}

// --- 2a. Partial<T> — makes all properties optional ---
// Perfect for update/patch operations
type UpdateUserDto = Partial<UserFull>;
// Equivalent to: { id?: number; name?: string; email?: string; ... }

function updateUser(id: number, updates: Partial<UserFull>): UserFull {
  // Fetch current user (simulated)
  const current: UserFull = {
    id,
    name: "Alice",
    email: "alice@example.com",
    password: "hashed",
    role: "viewer",
    createdAt: new Date("2024-01-01"),
    updatedAt: new Date(),
  };
  return { ...current, ...updates, updatedAt: new Date() };
}

const updated = updateUser(1, { name: "Alice Smith", role: "editor" });
console.log(updated.name, updated.role); // Alice Smith editor


// --- 2b. Required<T> — makes all properties required ---
// Opposite of Partial — use when you need to ensure completeness

interface DraftConfig {
  host?: string;
  port?: number;
  timeout?: number;
}

type FinalConfig = Required<DraftConfig>;
// Equivalent to: { host: string; port: number; timeout: number }

const finalConfig: FinalConfig = {
  host: "api.example.com",
  port: 443,
  timeout: 5000,
};


// --- 2c. Readonly<T> — makes all properties readonly ---
// Use when you want to prevent mutation of a value

type ImmutableUser = Readonly<UserFull>;

const frozenUser: ImmutableUser = {
  id: 1,
  name: "Alice",
  email: "alice@example.com",
  password: "hashed",
  role: "admin",
  createdAt: new Date(),
  updatedAt: new Date(),
};

// frozenUser.name = "Bob"; // ✗ Error: Cannot assign to 'name' because it is a read-only property

// Practical: freeze function arguments to prevent mutation:
function displayUser(user: Readonly<UserFull>): void {
  console.log(`${user.name} (${user.role})`);
  // user.role = "admin"; // ✗ not allowed in this function
}


// --- 2d. Pick<T, K> — select only specific keys ---
// Create a type with a subset of properties

type UserPublicProfile = Pick<UserFull, "id" | "name" | "role">;
// Equivalent to: { id: number; name: string; role: "admin" | "viewer" | "editor" }

const publicProfile: UserPublicProfile = {
  id: 1,
  name: "Alice",
  role: "admin",
};
// publicProfile.password is not accessible — correctly hidden

type LoginDto = Pick<UserFull, "email" | "password">;
const loginData: LoginDto = { email: "alice@example.com", password: "secret" };


// --- 2e. Omit<T, K> — exclude specific keys ---
// The inverse of Pick — create a type WITHOUT certain properties

type UserWithoutPassword = Omit<UserFull, "password">;
// Safe to send to frontend — password is excluded

type CreateUserDto = Omit<UserFull, "id" | "createdAt" | "updatedAt">;
// id, createdAt, updatedAt are server-generated — don't include in creation DTO

const newUserData: CreateUserDto = {
  name: "Carol",
  email: "carol@example.com",
  password: "secret123",
  role: "viewer",
};


// --- 2f. Record<K, V> — creates an object type with specific keys and value type ---
type RolePermissions = Record<"admin" | "editor" | "viewer", string[]>;

const permissions: RolePermissions = {
  admin:  ["read", "write", "delete", "manage"],
  editor: ["read", "write"],
  viewer: ["read"],
};

// Generic use: map type over a set of keys
type StringRecord = Record<string, string>;
type NumberRecord = Record<string, number>;

const headers: StringRecord = { "Content-Type": "application/json", Authorization: "Bearer token" };
const wordFreq: NumberRecord = { hello: 3, world: 2, typescript: 5 };


// --- 2g. Combining utility types ---
// Mix and match for precise DTOs

type PublicUserUpdate = Partial<Pick<UserFull, "name" | "email">>;
// Optional name and email — nothing else

type UserResponse = Readonly<Omit<UserFull, "password">>;
// Immutable user without password — safe API response type


// ============================================================
// 3. TYPE GUARDS
// ============================================================
// Type guards are expressions that narrow a type within a code block

interface Cat { kind: "cat"; name: string; meow(): void }
interface Dog { kind: "dog"; name: string; bark(): void }
interface Fish { kind: "fish"; name: string; swim(): void }
type Pet = Cat | Dog | Fish;

// --- 3a. typeof guard (primitives) ---
function process(value: string | number | boolean): string {
  if (typeof value === "string") {
    return value.toUpperCase(); // TS knows: string here
  }
  if (typeof value === "number") {
    return value.toFixed(2);   // TS knows: number here
  }
  return value ? "Yes" : "No"; // TS knows: boolean here
}

// --- 3b. instanceof guard (class instances) ---
class DatabaseError extends Error {
  constructor(public query: string, message: string) {
    super(message);
    this.name = "DatabaseError";
  }
}

class NetworkError extends Error {
  constructor(public statusCode: number, message: string) {
    super(message);
    this.name = "NetworkError";
  }
}

function handleError(error: unknown): string {
  if (error instanceof DatabaseError) {
    return `DB error on query "${error.query}": ${error.message}`;
  }
  if (error instanceof NetworkError) {
    return `Network error ${error.statusCode}: ${error.message}`;
  }
  if (error instanceof Error) {
    return `Error: ${error.message}`;
  }
  return "Unknown error";
}

console.log(handleError(new DatabaseError("SELECT *", "timeout")));
console.log(handleError(new NetworkError(503, "Service Unavailable")));

// --- 3c. in operator guard ---
function describeAnimal(pet: Pet): string {
  if ("meow" in pet) {
    return `${pet.name} is a cat`; // TS knows: Cat
  }
  if ("bark" in pet) {
    return `${pet.name} is a dog`; // TS knows: Dog
  }
  return `${pet.name} is a fish`;  // TS knows: Fish
}

// --- 3d. Discriminated union guard (kind field) ---
function makePetSound(pet: Pet): void {
  switch (pet.kind) {
    case "cat":  pet.meow(); break;
    case "dog":  pet.bark(); break;
    case "fish":
      console.log(`${pet.name} swims silently...`);
      pet.swim();
      break;
  }
}

// --- 3e. User-defined type guard (is keyword) ---
// Returns `value is Type` — tells TypeScript what type you've confirmed

function isString(value: unknown): value is string {
  return typeof value === "string";
}

function isUser(value: unknown): value is UserFull {
  return (
    typeof value === "object" &&
    value !== null &&
    "id" in value &&
    "name" in value &&
    "email" in value
  );
}

function isNonEmpty<T>(arr: T[]): arr is [T, ...T[]] {
  return arr.length > 0;
}

// Usage:
function processValue(value: unknown): void {
  if (isString(value)) {
    console.log(value.toUpperCase()); // TypeScript knows it's a string here
  }
}

function processArray(items: string[]): string {
  if (isNonEmpty(items)) {
    return items[0]; // TypeScript knows items[0] exists
  }
  return "empty";
}

// --- 3f. Assertion functions ---
// `asserts value is Type` — throws if condition is false, narrows if it passes
function assertIsString(value: unknown): asserts value is string {
  if (typeof value !== "string") {
    throw new Error(`Expected string, got ${typeof value}`);
  }
}

function assertDefined<T>(value: T | null | undefined): asserts value is T {
  if (value == null) {
    throw new Error("Value must not be null or undefined");
  }
}

const maybeString: unknown = "hello";
assertIsString(maybeString);
console.log(maybeString.toUpperCase()); // TypeScript knows it's a string after the assertion


// ============================================================
// 4. INTERFACE vs TYPE — Comparison
// ============================================================

// Both can describe object shapes. Here's the definitive comparison:

// -------------------------------------------------------
// INTERFACES                    | TYPE ALIASES
// -------------------------------------------------------
// interface User { name: string }  | type User = { name: string }
// Can extend with `extends`        | Can extend with intersection (&)
// Can be merged (declaration)      | Cannot be merged
// Can only describe objects/fns    | Can describe ANY type (unions, primitives, tuples)
// Error messages use interface name| Error messages use type alias name
// -------------------------------------------------------

// --- 4a. Extending ---
interface AnimalInterface {
  name: string;
  sound(): string;
}

interface DogInterface extends AnimalInterface {
  breed: string;
  fetch(): void;
}

// Type equivalent uses intersection (&):
type AnimalType = {
  name: string;
  sound(): string;
};

type DogType = AnimalType & {
  breed: string;
  fetch(): void;
};

// Both work for `implements`:
class GoldenRetriever implements DogInterface {
  name = "Buddy";
  breed = "Golden Retriever";
  sound() { return "Woof!"; }
  fetch() { console.log(`${this.name} fetches the ball!`); }
}

class Poodle implements DogType {
  name = "Coco";
  breed = "Poodle";
  sound() { return "Yip!"; }
  fetch() { console.log(`${this.name} fetches elegantly`); }
}


// --- 4b. Declaration merging (interfaces only) ---
interface Config {
  host: string;
}
interface Config {
  port: number; // MERGED — now Config = { host: string; port: number }
}

const cfg: Config = { host: "localhost", port: 3000 };

// This is NOT possible with type aliases:
// type Config = { host: string };
// type Config = { port: number }; // ✗ Error: Duplicate identifier


// --- 4c. Things ONLY type aliases can do ---

// Union of primitives:
type StringOrNumber = string | number;
type Falsy = false | 0 | "" | null | undefined;

// Tuple:
type Pair = [string, number];

// Intersection:
type ReadonlyUser = Readonly<UserFull> & { lastLogin: Date };

// Mapped type:
type Optional<T> = { [K in keyof T]?: T[K] };

// Conditional type:
type IsArray<T> = T extends any[] ? "yes" : "no";
type Test1 = IsArray<string[]>; // "yes"
type Test2 = IsArray<string>;   // "no"


// --- 4d. RECOMMENDATION ---
/*
 * USE interface when:
 *   • Describing the shape of an object that might be extended by others
 *   • Building a public API or library (declaration merging is useful)
 *   • Working in Angular (convention uses interfaces heavily)
 *   • You want `implements` for class contracts
 *
 * USE type when:
 *   • Defining unions, intersections, tuples, primitives
 *   • Creating utility/helper types (mapped/conditional types)
 *   • You need to name a function signature without a callable interface
 *   • The type should never be merged or extended
 *
 * In MOST day-to-day code: the choice is stylistic.
 * Pick one convention for your team and stick with it.
 * Many teams use `interface` for objects and `type` for everything else.
 */

console.log("All type checks passed — TypeScript is working!");
