# Day 15 — TypeScript · Part 2 Walkthrough Script
## Generics, Classes, Decorators, tsconfig, Utility Types & Type Guards

**Duration:** ~90 minutes  
**Files:**
- `Part-2/01-generics-and-classes.ts`
- `Part-2/02-decorators-utility-types-guards.ts`
- `Part-2/03-tsconfig-reference/tsconfig.annotated.json`

---

## ⚙️ Pre-Class Setup (2 min)

[ACTION] Verify ts-node is available:
```bash
npx ts-node --version
```

[ACTION] For files using decorators, remind students of the flag:
```bash
npx ts-node --experimentalDecorators Part-2/02-decorators-utility-types-guards.ts
```

> "Part 1 covered TypeScript's type system — the rules of the language.
> Part 2 is about the *power tools* — patterns that make large applications
> maintainable. Generics, classes, decorators, and configuration."

---

## Part 2A — Generics (25 min)
### File: `01-generics-and-classes.ts` · Section 1

---

### A1 — The Problem Generics Solve (5 min)

[ACTION] Draw on the board before opening any code:

```
WITHOUT GENERICS:
  function identity(value: any): any { return value; }
  // We lose type information — the return type is 'any'

WITH GENERICS:
  function identity<T>(value: T): T { return value; }
  //                ↑                ↑        ↑
  //          type variable    input type  output type
  //          (placeholder)
  // T is captured at call time — no information is lost
```

[ASK] "Why is returning `any` a problem if the function works fine?"
> _Answer: TypeScript can't check what you do with the result. You lose all type safety after the call._

[ACTION] Open `01-generics-and-classes.ts`, scroll to Section 1. Run:
```bash
npx ts-node 01-generics-and-classes.ts
```

[ACTION] Walk through `identity<T>`:
```typescript
const num = identity<number>(42);     // explicit T
const str = identity("hello");         // inferred T = string
```

> "TypeScript infers `T` from the argument — you rarely need to write `<number>` explicitly."

---

### A2 — Multiple Type Parameters & Array Utilities (5 min)

[ACTION] Show `pair<K, V>`:
> "When you have two independent type slots, use two type variables. By convention K and V for key/value."

[ACTION] Show `first<T>`, `last<T>`, `compact<T>`:

[ASK] "What does `item is T` mean in `compact`?"
> _Answer: It's a type guard — it tells TypeScript that if the function returns true, `item` is narrowed to T (not null/undefined)._

⚠️ **WATCH OUT:** Multiple type parameters become hard to read beyond 3. If you need more, consider an object parameter with a generic type.

---

### A3 — Constraints with `extends` (5 min)

[ACTION] Draw on the board:

```
UNCONSTRAINED:    function getLength<T>(x: T)
                  // Error: Property 'length' does not exist on T
                  // T could be anything — a number has no .length

CONSTRAINED:      function getLength<T extends { length: number }>(x: T)
                  //                   ↑
                  //        T must have at least these properties
                  // Accepts: string, array, anything with .length
```

[ACTION] Show `getProperty<T, K extends keyof T>`:
> "This is the most useful constraint pattern. `keyof T` gives us a union of the object's keys at the type level."

```typescript
getProperty(user, "name");  // ✅ "name" exists on User
getProperty(user, "email"); // ✅
getProperty(user, "age");   // ✗ TypeScript error at compile time
```

---

### A4 — Generic Interfaces and Classes (10 min)

[ACTION] Show `ApiResponse<T>`:
```typescript
// Same wrapper, different data shapes:
ApiResponse<User>    → data: User
ApiResponse<Product> → data: Product
ApiResponse<User[]>  → data: User[]
```

> "This is how real API clients work. One response type, T is whatever the endpoint returns."

[ACTION] Show `Stack<T>` — walk through push/pop/peek:
> "Stack is the classic teaching example because it's simple enough to understand but real enough to be useful."

[ASK] "What would happen if we tried to push a string onto a `Stack<number>`?"
> _Answer: TypeScript error at compile time — T is fixed to `number` when we write `new Stack<number>()`._

[ACTION] Show `Repository<T extends Entity>`:
> "The `extends Entity` constraint means T must have an `id` field. This is how you build a type-safe data layer."

→ TRANSITION: "Generics are type-level programming. Now let's look at value-level structure: classes."

---

## Part 2B — Classes (20 min)
### File: `01-generics-and-classes.ts` · Section 2

---

### B1 — Access Modifiers (7 min)

[ACTION] Draw the access modifier table on the board:

```
┌───────────────┬─────────────────┬──────────────────┬──────────────────┐
│   Modifier    │ Same Class      │ Subclass         │ Outside Class    │
├───────────────┼─────────────────┼──────────────────┼──────────────────┤
│ public        │ ✅              │ ✅               │ ✅               │
│ protected     │ ✅              │ ✅               │ ✗                │
│ private       │ ✅              │ ✗                │ ✗                │
│ readonly      │ read ✅         │ read ✅           │ read ✅           │
│ (any)         │ write once      │ write ✗          │ write ✗          │
└───────────────┴─────────────────┴──────────────────┴──────────────────┘
```

> "JavaScript has no `private` or `protected` at runtime. TypeScript enforces these at compile time only. There is also `#field` (JS private fields) which IS enforced at runtime — different thing."

[ACTION] Show `BankAccount` — highlight `private balance`, `readonly accountNumber`, `protected owner`:

[ASK] "Why might we want `protected` on `owner` instead of `private`?"
> _Answer: So that subclasses (like `SavingsAccount`) can read the owner's name — e.g., for statements._

---

### B2 — Parameter Property Shorthand (3 min)

[ACTION] Show `Point` class — compare long vs. short form:

```typescript
// Verbose (what TypeScript generates):
class Point {
  public readonly x: number;
  public readonly y: number;
  constructor(x: number, y: number) {
    this.x = x;
    this.y = y;
  }
}

// Shorthand (what we write):
class Point {
  constructor(
    public readonly x: number,
    public readonly y: number
  ) {}
}
```

> "The shorthand declares AND assigns in one step. Very common in Angular services and NestJS."

---

### B3 — Implements, Inheritance, Abstract Classes (10 min)

[ACTION] Show `UserProfile implements Printable, Serializable`:
> "A class can implement multiple interfaces. TypeScript ensures you provide all required methods."

[ACTION] Show `SavingsAccount extends BankAccount`:
> "Inheritance is `extends`. The child calls `super()` to run the parent constructor."

[ACTION] Draw the abstract class diagram:

```
abstract class Shape
  ├── abstract area(): number        ← subclass MUST implement
  ├── abstract perimeter(): number   ← subclass MUST implement
  ├── abstract kind: string          ← subclass MUST implement
  └── toString(): string             ← concrete: inherited for free

concrete class Circle extends Shape  → must implement area/perimeter/kind
concrete class Rect   extends Shape  → must implement area/perimeter/kind
```

> "Abstract classes are blueprints. You can't do `new Shape()` — only `new Circle()` or `new Rect()`."

[ASK] "When would you choose an abstract class over an interface?"
> _Answer: When you want to provide some default implementation (like `toString`) alongside required contract methods._

→ TRANSITION: "Classes are object blueprints. Decorators are a way to add behavior to them declaratively."

---

## Part 2C — Decorators (15 min)
### File: `02-decorators-utility-types-guards.ts` · Section 1

---

### C1 — What Are Decorators? (3 min)

[ACTION] Write on the board:

```
// A decorator is just a function applied to a class/method/property
// with the @ syntax:

@Component({ selector: "app-root" })   ← Angular
@Injectable()                          ← Angular DI
@Controller("/users")                  ← NestJS
@Entity()                              ← TypeORM

// All of these are TypeScript decorators under the hood.
```

> "You've probably seen decorators in Angular. Today we build them from scratch to see what they really do."

⚠️ **WATCH OUT:** The `--experimentalDecorators` flag is required for the legacy decorator syntax. TypeScript 5+ has a new stage-3 TC39 decorator syntax (slightly different). Angular currently uses the legacy style.

---

### C2 — Class Decorators (5 min)

[ACTION] Show `@Singleton`:
> "It replaces the class constructor with one that returns the cached instance. The class reference is unchanged — callers don't know."

[ACTION] Show `@Serializable`:
> "It adds a `toJSON()` method to the class prototype. After decoration, every instance has `toJSON()` for free."

[ACTION] Show stacking on `UserService`:
```typescript
@Log
@Serializable
class UserService { … }
```

> "Decorators execute **bottom-up**. `@Serializable` runs first (closest to the class), then `@Log`."

[ASK] "What order would you see the logs in?"
> _Answer: First "Decorating class: UserService" from `@Log`, after `@Serializable` has already modified the class._

---

### C3 — Method & Property Decorators (7 min)

[ACTION] Show `@MeasureTime` — highlight the `descriptor.value` swap pattern:
> "The method decorator receives the property descriptor. We replace `.value` (the function) with a wrapper that adds our behavior."

[ACTION] Show `@Deprecated("use newMethod instead")` — factory decorator:
> "A factory decorator is a function that *returns* a decorator. You need this when your decorator takes arguments."

```typescript
@Deprecated("use newMethod instead")
//           ↑ argument
// TypeScript calls Deprecated("...") first → gets back the actual decorator
```

[ACTION] Show `@Default` property decorator on `AppConfig`:
> "Property decorators receive the prototype and property name. Setting `target[propertyName]` gives all instances a default value."

→ TRANSITION: "Decorators are applied at class definition time. Now let's look at configuration — the tsconfig."

---

## Part 2D — tsconfig.json (10 min)
### File: `03-tsconfig-reference/tsconfig.annotated.json`

---

### D1 — target vs module (3 min)

[ACTION] Open `tsconfig.annotated.json`. Start at the top.

[ACTION] Draw on the board:

```
target  = "what JavaScript syntax do I output?"
           ES2020 → keeps async/await, arrow functions, optional chaining
           ES5    → compiles down to function() {} and .then() chains

module  = "what module system do I output?"
           commonjs → require() / module.exports  (Node.js default)
           ESNext   → import / export              (browsers, Vite, Webpack)
```

> "These two are independent. You can output ES2020 syntax but use CommonJS modules, or ES5 syntax with ESModules."

---

### D2 — strict: true (4 min)

[ACTION] Walk through the strict sub-flags in the file:

| Flag | What it catches |
|------|----------------|
| `noImplicitAny` | Missing type annotations |
| `strictNullChecks` | Forgetting to handle `null`/`undefined` |
| `strictFunctionTypes` | Contravariance bugs in callbacks |
| `noImplicitThis` | `this` being `any` in functions |

> "Enable `strict: true` on every new project. It's a one-line setting that prevents entire categories of bugs."

[ASK] "What's the difference between `noUnusedLocals` and `noUnusedParameters`?"
> _Answer: `noUnusedLocals` flags variables you declared but never read. `noUnusedParameters` flags function parameters that are never used._

---

### D3 — paths aliases (3 min)

[ACTION] Show the `paths` section:
```json
"paths": {
  "@/*": ["src/*"]
}
```

> "Instead of `import { Button } from '../../../components/Button'`, you write `import { Button } from '@/components/Button'`. Clean, readable, refactor-friendly."

⚠️ **WATCH OUT:** `paths` only affects TypeScript resolution. Your bundler (Vite, Webpack) needs its own alias config to match, or you get runtime errors.

→ TRANSITION: "tsconfig controls what TypeScript checks. Utility types let us transform types without writing new ones from scratch."

---

## Part 2E — Utility Types (10 min)
### File: `02-decorators-utility-types-guards.ts` · Section 2

---

### E1 — Think of Them as Type-Level Functions (2 min)

[ACTION] Draw on the board:

```
JavaScript (runtime):
  const numbers = [1, 2, 3].map(x => x * 2);  // transforms values

TypeScript (compile time):
  type UpdateUserDto = Partial<UserFull>;       // transforms types

Both are transformations — just at different layers.
```

> "Utility types are generic types built into TypeScript that transform other types. You don't have to invent them — they ship with TypeScript."

---

### E2 — Walk Through the Big Six (8 min)

[ACTION] Walk through each in the file, one at a time:

**`Partial<T>`** — every property becomes optional
```typescript
// Use case: PATCH endpoint — send only the fields you want to update
type UpdateUserDto = Partial<UserFull>;
```

**`Required<T>`** — every property becomes required (inverse of Partial)
```typescript
// Use case: validate a draft config is fully filled in before use
type FinalConfig = Required<DraftConfig>;
```

**`Readonly<T>`** — every property becomes read-only
```typescript
// Use case: snapshot/frozen objects — prevent accidental mutation
const user: Readonly<UserFull> = fetchUser();
```

**`Pick<T, Keys>`** — keep only listed keys
```typescript
// Use case: public-facing DTO — expose only safe fields
type UserPublicProfile = Pick<UserFull, "id" | "name" | "role">;
```

**`Omit<T, Keys>`** — remove listed keys (inverse of Pick)
```typescript
// Use case: remove sensitive field before returning to client
type UserWithoutPassword = Omit<UserFull, "password">;
```

**`Record<Keys, Value>`** — object type with specific keys and value type
```typescript
// Use case: permission maps, lookup tables
type RolePermissions = Record<"admin" | "editor" | "viewer", string[]>;
```

[ASK] "When would you use `Pick` vs `Omit`? Is there a rule of thumb?"
> _Answer: Pick when you want a small subset of properties (fewer to list). Omit when you want almost everything except a few (fewer to exclude)._

→ TRANSITION: "Utility types shape static types. Type guards narrow types at runtime."

---

## Part 2F — Type Guards & Interface vs Type (10 min)
### File: `02-decorators-utility-types-guards.ts` · Sections 3 & 4

---

### F1 — Type Guard Decision Tree (6 min)

[ACTION] Draw the decision tree on the board:

```
You have a value and need to narrow its type. Use:

1. typeof        → primitives (string, number, boolean, symbol)
2. instanceof    → class instances (Error subclasses, Date, etc.)
3. in            → object shapes (check if property exists)
4. switch(x.kind)→ discriminated unions (tagged types with a literal field)
5. custom is     → complex checks — wrap in a reusable predicate function
```

[ACTION] Walk through each example quickly in the file — show the narrowed type in each `if` branch:

> "Notice that inside each `if` block, TypeScript knows the specific type — no casting needed."

[ACTION] Highlight user-defined guard with `is`:
```typescript
function isUser(value: unknown): value is UserFull {
  return (
    typeof value === "object" && value !== null &&
    "id" in value && "name" in value && "email" in value
  );
}
```

> "The `value is UserFull` return type is the contract. TypeScript trusts this function's judgment at call sites."

⚠️ **WATCH OUT:** TypeScript trusts your `is` guard implementation. If you write it wrong, TypeScript won't catch the mistake — a bad guard is worse than no guard.

---

### F2 — Interface vs Type — Final Word (4 min)

[ACTION] Show the comparison table in Section 4 comments. Summarize on the board:

```
interface                   type alias
─────────────────────────── ──────────────────────────────────────
Extendable (extends)         Composable (&)
Declaration merging ✅       Declaration merging ✗
Works with implements ✅     Works with implements ✅
Object shapes only           Unions, tuples, primitives, mapped types
```

> "The TypeScript team's recommendation: use `interface` for object shapes that might be extended. Use `type` for everything else — unions, utility compositions, function types, tuples."

[ASK] "Why does declaration merging exist for interfaces but not type aliases?"
> _Answer: Interfaces model open contracts — third-party code should be able to add to them (e.g., augmenting browser types or library types). Type aliases are closed compositions — merging would be ambiguous._

---

## 🔁 Wrap-Up Q&A (5 min)

Ask the class — go around the room:

1. **"What's the difference between `<T extends Foo>` and `<T>`?"**
   > _Constrained T must have the shape of Foo. Unconstrained T can be anything._

2. **"Can a TypeScript `private` field be accessed at runtime from outside the class?"**
   > _Yes — TypeScript `private` is compile-time only. Use JS `#field` for true runtime privacy._

3. **"What does `strict: true` in tsconfig actually enable?"**
   > _A bundle of strict flags: noImplicitAny, strictNullChecks, strictFunctionTypes, strictPropertyInitialization, etc._

4. **"When would you use `Omit` vs `Pick`?"**
   > _Omit when you want almost all properties (remove few). Pick when you want a small subset (select few)._

5. **"What would break if you removed `--experimentalDecorators` when running the decorators file?"**
   > _TypeScript would throw an error: "Experimental support for decorators is a feature that is subject to change." The file would not compile._

---

## 📚 Take-Home Exercises

### Exercise 1 — Generic Cache
Build a generic `Cache<K, V>` class with:
- `set(key: K, value: V, ttlMs: number): void`
- `get(key: K): V | undefined` (returns undefined if expired)
- `has(key: K): boolean`
- `invalidate(key: K): void`

Use a `Map` internally to store `{ value: V; expiresAt: number }`.

---

### Exercise 2 — Utility Type Composition
Given this type:
```typescript
interface Employee {
  id: number;
  name: string;
  email: string;
  salary: number;
  department: string;
  startDate: Date;
  ssn: string;
  performanceRating: number;
}
```
Create these derived types using only utility types (no rewriting properties):
- `EmployeePublic` — omit `salary`, `ssn`, `performanceRating`
- `EmployeeUpdateDto` — all optional, omit `id` and `startDate`
- `EmployeeDirectory` — pick only `name`, `email`, `department`
- `EmployeeRecord` — a `Record` keyed by department name (`"engineering" | "marketing" | "sales"`) with value `EmployeePublic[]`

---

### Exercise 3 — Type-Safe Event Emitter
Build a generic `EventEmitter<Events>` class where `Events` is a record of event names to payload types:
```typescript
type AppEvents = {
  login: { userId: string; timestamp: Date };
  logout: { userId: string };
  error: { code: number; message: string };
};

const emitter = new EventEmitter<AppEvents>();
emitter.on("login", (payload) => console.log(payload.userId));  // ✅
emitter.emit("login", { userId: "123", timestamp: new Date() }); // ✅
emitter.emit("login", { userId: 123 }); // ✗ TypeScript error
```

---

### Exercise 4 — tsconfig for a Real Project
Create a `tsconfig.json` for a Node.js REST API project with these requirements:
- TypeScript target: ES2022
- Module system: CommonJS (Node.js)
- Source files: `src/` directory
- Output: `dist/` directory
- Strict mode: enabled
- Path alias: `@services/*` → `src/services/*`
- Decorators: enabled (for TypeORM)
- JSON imports: enabled
- Only compile `.ts` files, not tests (`*.test.ts`, `*.spec.ts`)

Then write a second `tsconfig.test.json` that extends the base config but includes test files.

---

## ✅ End of Day 15 — TypeScript

> "You've covered the full TypeScript language in one day — from basic type annotations to generics, classes, decorators, compiler configuration, utility types, and type guards. The best way to solidify this is to pick up an existing JavaScript project and migrate one file to TypeScript."

**Suggested next step for students:**
- Pick any Day 12/13/14 JavaScript exercise
- Rename to `.ts`
- Add type annotations
- Enable `strict: true`
- Fix all errors TypeScript finds

---

*Day 16 — React Fundamentals / Angular Fundamentals*
