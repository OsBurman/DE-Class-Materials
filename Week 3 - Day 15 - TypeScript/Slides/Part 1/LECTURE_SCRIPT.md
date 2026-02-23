# Week 3 - Day 15: TypeScript
## Part 1 Lecture Script — The Type System

**Total time:** 60 minutes
**Slides:** 16
**Delivery pace:** ~165 words/minute

---

## [00:00–02:00] Opening — Why TypeScript Exists

Good morning, everyone. Welcome to Day 15 — TypeScript day. By the end of today, you're going to be writing type-safe JavaScript code, and more importantly, you're going to understand what that phrase actually means.

Let's start with a quick question. Raise your hand if you've ever written JavaScript code that ran without any errors, and then crashed at runtime because you passed a string where a number was expected, or tried to call a method on `undefined`, or spelled a property name wrong. Yeah — everyone. That is the problem TypeScript solves.

TypeScript is a typed superset of JavaScript. That means two things. First, every JavaScript file is already valid TypeScript — you can rename any `.js` file to `.ts` and it compiles. Second, TypeScript adds an optional type system on top. You can be as strict or as loose as you want.

But the real key is this: TypeScript compiles down to plain JavaScript. Browsers don't know what TypeScript is. Node doesn't know. TypeScript runs entirely at development time — it checks your code, catches errors, and then disappears. What runs in production is ordinary JavaScript.

This is different from languages like Java or Go where the type system is enforced at runtime. TypeScript's types are erased. They exist purely to help you write better code, faster, with fewer bugs.

---

## [02:00–08:00] What Is TypeScript + Setup

Let's look at Slide 2. Who made TypeScript? Microsoft, in 2012. And the person who designed it — Anders Hejlsberg — also designed C# and Delphi. This wasn't someone hacking something together on a weekend. This is one of the most experienced language designers in the industry.

The reason TypeScript was created is right here in this comparison table. Look at the JavaScript column — type checking happens at runtime. That means your code has to actually run before you discover the error. In production. With real users. TypeScript moves that to compile time — before you run anything. Error discovered at runtime? That's a bug report. Error discovered at compile time? That's a fix you make in five seconds.

Look at IDE autocomplete. With plain JavaScript, your editor makes educated guesses. With TypeScript, your editor knows exactly what methods exist on every object, what parameters every function expects, what every function returns. VS Code autocomplete becomes dramatically more useful. And VS Code is itself written in TypeScript — so the tooling is exceptional out of the box.

Let me also read you the industry adoption list because it matters. Angular — mandatory, you cannot write Angular without TypeScript. VS Code — written entirely in TypeScript. Slack, Airbnb, Notion, Stripe, Microsoft, Google — all major TypeScript users. If you are going to write front-end code professionally in 2025, TypeScript is expected.

Now, Slide 3 — let's look at the setup. The setup is almost trivially simple. You install the TypeScript compiler globally with npm:

```
npm install -g typescript
```

Then you compile a TypeScript file with `tsc hello.ts`. That produces a `hello.js` file in the same folder. That `.js` file is what actually runs.

There's also `ts-node` — which lets you run TypeScript files directly without an explicit compile step. This is great for scripts and development work. You'll use this occasionally.

If you want to try TypeScript without installing anything, go to typescriptlang.org/play. It's a browser-based playground where you write TypeScript on the left and see the compiled JavaScript on the right. I use this constantly for quick experiments. Let's keep it open in a tab today.

And notice this: VS Code has TypeScript IntelliSense built in, no plugins required. As you type TypeScript code, VS Code shows you red squiggles for type errors in real time, before you compile. This is one of the most compelling parts of TypeScript as a developer tool — the feedback is immediate.

---

## [08:00–14:00] Annotations vs Inference

Slide 4 — and this slide is foundational. You need to internalize this before we go further.

TypeScript has two ways to know the type of a value. Annotations — where you explicitly tell TypeScript the type. And inference — where TypeScript figures it out on its own.

Watch:

```typescript
let name: string = "Alice";
```

That colon after `name`, before the equals sign — that's a type annotation. I am explicitly declaring that `name` is a `string`.

But I can also write:

```typescript
let name = "Alice";
```

No annotation. TypeScript looks at the value `"Alice"`, sees it's a string literal, and infers that `name` is of type `string`. The result is identical. TypeScript has the same information either way.

This is important: TypeScript's inference is excellent. For simple initialized variables, you do NOT need to annotate. Writing `: string` on `let x = "hello"` is noise — it adds visual clutter without adding value. Trust the inference.

When do you annotate explicitly? Function parameters — always. TypeScript will never assume what type a function parameter should be. If you write `function greet(name)`, TypeScript immediately flags an error: `parameter 'name' implicitly has 'any' type`. You must write `function greet(name: string)`.

The second case is uninitialized variables — where you declare a variable but don't assign a value right away. `let value;` — TypeScript can't infer anything, so it assumes `any`. You should annotate: `let value: string`.

Third case: complex or ambiguous situations where inference doesn't capture your intent. And fourth: public APIs and exported functions where annotating the return type makes the contract explicit for callers.

For everything else — assignments with initial values, array literals, object literals, simple return types — let TypeScript infer. You'll write cleaner, more readable code.

---

## [14:00–20:00] Simple Types

Slide 5 — the primitive types. Most of these map directly to JavaScript's primitives.

`string` — annotated with lowercase `string`. Not `String` with a capital S. The capital `String` refers to the wrapper object class, which you almost never use. Lowercase `string` is the primitive type. Template literals — backtick strings — are also typed as `string`.

`number` — there is one number type in TypeScript, just like JavaScript. It covers integers, floats, hexadecimal, binary, octal — all numeric values. You don't have `int` and `double` like you do in Java.

`boolean` — `true` or `false`. Lowercase, not `Boolean`.

Now here's where TypeScript gets more interesting: `null` and `undefined`. In JavaScript, both of these are just values you can put anywhere. In TypeScript with `strictNullChecks` enabled — and I'm going to emphasize this — if a variable is typed as `string`, you cannot put `null` into it. You'll get an error:

```typescript
let name: string = null;  // Error with strictNullChecks
```

If you want to allow null, you must declare it explicitly:

```typescript
let name: string | null = null;  // OK — the union type says "string or null"
```

This is one of the most valuable things TypeScript does. It forces you to be explicit about whether a value can be absent. The designer of `null` — Tony Hoare — famously called it his "billion dollar mistake" because of how many bugs null values have caused over the decades. TypeScript's `strictNullChecks` is the fix.

`bigint` is for integers larger than JavaScript's `Number.MAX_SAFE_INTEGER`. You write bigint literals with an `n` suffix: `9007199254740993n`. You'll use this rarely in application code, but it exists.

`symbol` creates a guaranteed-unique value. Every call to `Symbol()` returns something that is not equal to anything else. Used in advanced patterns and as unique property keys.

The types you'll use 95% of the time: `string`, `number`, `boolean`, `null`, `undefined`, and combinations of those in union types.

---

## [20:00–26:00] Special Types

Slide 6 — four special types that every TypeScript developer needs to know: `any`, `unknown`, `void`, and `never`.

Let's start with `any`. Here's the honest description: `any` is a trapdoor. When you type a variable as `any`, TypeScript stops checking it entirely. You can put anything in, you can call any method on it, you can do anything — and TypeScript won't say a word. No errors. Until it crashes at runtime.

This sounds terrible — and it is — but it exists for a reason. When you're migrating a JavaScript codebase to TypeScript incrementally, you need a way to say "I'll type this properly later." `any` is that escape hatch. Use it during migration. Don't build new code with it.

Now look at `unknown`. This is the safe version. When a value is typed as `unknown`, TypeScript also accepts any value. But — and this is the key difference — before you can USE the value, you have to prove to TypeScript what type it is.

```typescript
let input: unknown = getUserInput();
input.toUpperCase();    // Error — can't call methods on unknown

if (typeof input === "string") {
    input.toUpperCase();  // Fine — TypeScript now knows it's a string
}
```

That `typeof` check is called narrowing. TypeScript looks at the condition and says: inside this `if` block, `input` is guaranteed to be a `string`. So it's safe to call string methods.

Use `unknown` when you genuinely don't know what type a value is — like parsing JSON from an external API, or receiving data from a form.

`void` — a function return type that means the function doesn't return anything meaningful. `console.log` returns `void`. Event handlers return `void`. Any function that exists purely for its side effects returns `void`. This is slightly different from `undefined` — a function that explicitly `return undefined` has a return type of `undefined`, not `void`.

`never` — this is one of the most powerful types in TypeScript. A function typed as `never` is a function that never returns. Not a function that returns `undefined` — a function that never reaches a return point. Either because it always throws an error, or because it runs an infinite loop.

But look at the bottom of this slide — the exhaustive check pattern. This is where `never` really shines. I have a union type `Status` with three values. In my switch statement, I handle all three cases. In the `default` branch, I assign the value to a variable typed as `never`. If TypeScript can narrow all other cases away, `status` should be `never` in the default branch — because no valid values are left. This assignment is fine.

But what happens if I add `"suspended"` to the `Status` type and forget to add a case for it? TypeScript will now reach the default branch with a value of type `"suspended"` — which is NOT `never`. Assigning `"suspended"` to `never` is a type error. TypeScript will tell you at compile time that you have an unhandled case. This turns a potential runtime crash into a compile-time error. Extremely useful.

---

## [26:00–32:00] Type Aliases and Union Types

Slide 7 — type aliases. The `type` keyword lets you give a reusable name to any type expression.

```typescript
type UserId = string;
type Price = number;
```

Simple — you're just creating a named alias. `UserId` and `string` are functionally identical. But `UserId` communicates intent. When you see a parameter typed as `UserId`, you immediately know what it represents — even before reading any documentation.

Type aliases can name object shapes too:

```typescript
type Point = { x: number; y: number; };
```

And function signatures:

```typescript
type StringTransformer = (input: string) => string;
```

Now look at intersection types — the `&` operator. `type Person = Named & Aged` means a `Person` must have ALL the properties of BOTH `Named` and `Aged`. It combines types additively. Think of it as the AND to the OR of union types.

This is different from extension. With `extends` in an interface, you're building a hierarchy. With `&`, you're composing independent types. I have a `Timestamped` shape and an `EntityBase` shape and a `UserProfile` shape — I can combine them with `&` without any hierarchy.

And look at the recursive type alias at the bottom — `JSONValue`. It represents any valid JSON value. And it's defined in terms of itself. TypeScript allows this with type aliases as long as the recursion terminates. This is genuinely hard to express in many type systems. In TypeScript, it's six lines.

Slide 8 — union types and literal types. Union types use the pipe character, `|`. `string | number` means "this value is either a string or a number."

But literal types are where it gets really interesting. Look at `type Direction = "north" | "south" | "east" | "west"`. Those string literals ARE the type. Not just any string — specifically one of those four values. If you call `move("up")`, TypeScript immediately says: `"up"` is not assignable to type `Direction`. Typo caught. Wrong value caught. At compile time.

Now look at discriminated unions at the bottom of slide 8. I have three types — `Circle`, `Rectangle`, `Triangle` — each with a `kind` property that holds a specific string literal. The union of all three is `Shape`. And in my `getArea` function, TypeScript uses the `kind` field to narrow. In the `case "circle"` branch, TypeScript knows the shape is a `Circle` — so `shape.radius` exists. In `case "rectangle"`, it knows `shape.width` and `shape.height` exist. This is type narrowing through discriminated unions, and it is one of the most powerful patterns in all of TypeScript.

---

## [32:00–38:00] Object Types and Interfaces

Slide 9 — object types. You can annotate an object's shape inline, right where you declare the variable. Optional properties use `?`. Readonly properties use the `readonly` modifier.

Index signatures — `{ [key: string]: string }` — are for when you know the value type but not the key names. This represents a dictionary. The HTTP headers example is perfect — you know the values are strings, but you don't know in advance what the header names will be.

But inline object types don't scale. If you need that shape in five places, you'd copy and paste it five times. That's where interfaces come in.

Slide 10 — interfaces. An interface defines a named contract for an object shape.

```typescript
interface User {
    readonly id: number;
    name: string;
    email: string;
    age?: number;
    greet(): string;
    updateName(name: string): void;
}
```

I can now use `User` anywhere. If I have a variable of type `User`, TypeScript guarantees it has all of these properties and methods. If I try to access something that's not in the interface, TypeScript tells me.

Interfaces can extend other interfaces — `interface Employee extends User`. An `Employee` has everything a `User` has, plus the additional fields. This is clean, explicit, and hierarchical.

Interfaces also support multiple inheritance — `interface Document extends Serializable, Printable`. You can extend as many interfaces as you want.

Now, the unique feature of `interface` versus `type`: declaration merging. If you declare the same interface name twice, TypeScript merges them into one type. This is how `@types/react` and `@types/node` extend the built-in browser globals. Your app code can add a property to the global `Window` type just by writing a second `interface Window` declaration. TypeScript merges it automatically.

This is something `type` aliases cannot do. You cannot declare a type alias with the same name twice — TypeScript will give you an error. This distinction will matter when we compare the two at the end of Part 2.

---

## [38:00–44:00] Arrays, Tuples, and Enums

Slide 11 — arrays and tuples. Arrays have two equivalent syntaxes. `string[]` is the most common. `Array<string>` is the generic syntax — we'll cover generics in Part 2, and you'll see why both exist.

TypeScript infers array element types from initialization. If you write `const items = ["apple", "banana"]`, TypeScript infers `string[]`. Pushing `42` into that array — `items.push(42)` — is an error.

Readonly arrays prevent mutation entirely. `const PRIMES: readonly number[] = [2, 3, 5, 7, 11]` — you cannot call `push`, `pop`, `splice`, or any mutating method. You cannot reassign elements by index. This is very useful for constants that should never change.

Tuples are fixed-length arrays where each position has a specific type. `type Point = [number, number]` says: this array has exactly two elements, both numbers. TypeScript enforces both the length and the types at each position.

Named tuples, introduced in TypeScript 4.0, let you put labels on tuple positions: `type UserRecord = [id: number, name: string, active: boolean]`. The labels don't change the runtime behavior, but they make the code much more readable. And destructuring works: `const [userId, userName, isActive] = record`.

Real-world tuple: React's `useState` hook returns a tuple — the value and the setter function. When you write `const [count, setCount] = useState(0)`, TypeScript knows the first element is a `number` and the second is `(value: number) => void`. That's a typed tuple return.

Slide 12 — enums. Numeric enums auto-increment from zero by default. `Direction.Up` is 0, `Down` is 1, and so on. Numeric enums also have reverse mapping — `Direction[0]` gives you back the string `"Up"`. This seems convenient, but it creates confusion.

String enums are almost always the better choice. `Color.Red = "RED"` — the value is readable in logs, readable in JSON, readable in the debugger. There's no numeric magic happening.

Look at the bottom of the slide — the `as const` pattern. This is the modern alternative to enums in many codebases. Instead of:

```typescript
enum Status { PENDING = "pending", ACTIVE = "active" }
```

You write:

```typescript
const STATUS = { PENDING: "pending", ACTIVE: "active" } as const;
type Status = typeof STATUS[keyof typeof STATUS];
```

The type `Status` is now `"pending" | "active"` — the literal string values. When you use this pattern, callers can pass either `STATUS.ACTIVE` OR the raw string `"active"`. With a string enum, only `Status.ACTIVE` works. The `as const` pattern is more flexible and produces cleaner code in many cases.

---

## [44:00–50:00] Type Assertions and Functions

Slide 13 — type assertions. When you use the `as` keyword, you're telling TypeScript: "I know better than you about the type of this value. Trust me."

The most common legitimate use case is the DOM API. `document.getElementById()` returns `HTMLElement | null`. It has no idea what specific kind of element you put in your HTML. But you know you put an `<input>` there. So you assert:

```typescript
const input = document.getElementById("username") as HTMLInputElement;
```

Now TypeScript knows it's an `HTMLInputElement`, which has a `.value` property. Without the assertion, TypeScript would only allow you to use properties that exist on the generic `HTMLElement`, and `.value` is not one of them.

The non-null assertion — the `!` at the end — tells TypeScript: this value is definitely not null. `const canvas = document.getElementById("myCanvas")!`. Use this when you are certain the element exists. If you're wrong, you'll get a runtime error. TypeScript trusts you on this one.

The `as unknown as Type` double assertion — I want to mention this so you recognize it when you see it. It's an escape hatch. It compiles but it completely bypasses the type system. Use it only when you genuinely have no other option. Never use it to silence errors you haven't fixed.

Now look at `satisfies` — this is TypeScript 4.9 and newer. The difference between `as` and `satisfies` is subtle but important. With `as`, TypeScript widens the type to what you assert. With `satisfies`, TypeScript validates the type but keeps the most specific inferred type.

In the `palette` example: the values are typed as `string | number[]`. But TypeScript still knows that `palette.red` is specifically `number[]` — not just `string | number[]`. That precision is preserved. With `as`, you'd lose it. Use `satisfies` when you want to validate a type constraint without giving up the specific inferred type.

Slide 14 — functions. You add type annotations to parameters and optionally to the return type. The return type comes after a colon after the closing parenthesis of the parameter list:

```typescript
function add(a: number, b: number): number {
    return a + b;
}
```

Optional parameters use `?` — same syntax as optional interface properties. Default parameters work identically to ES6 default parameters, and TypeScript infers the type from the default value. Rest parameters are typed as an array: `...numbers: number[]`.

Function type expressions let you describe the shape of a function: `type Callback = (error: Error | null, data: string) => void`. You can then annotate a parameter as `callback: Callback` and TypeScript knows exactly what to expect.

Function overloads — multiple type signatures for one implementation. The signatures above the implementation define the public contract. The implementation below handles all cases. Callers only see the signatures, not the implementation signature. This is how `document.createElement` works — it has dozens of overload signatures so that `createElement("canvas")` returns `HTMLCanvasElement` and `createElement("input")` returns `HTMLInputElement`.

---

## [50:00–56:00] Readonly and Immutability

Slide 15 — `readonly` and `as const`.

You've seen `readonly` on individual interface properties. `readonly` on a property means it can be set during initialization but never changed afterward.

```typescript
interface Config {
    readonly apiUrl: string;
}
config.apiUrl = "something";  // Error
```

Readonly arrays — `readonly number[]` — prevent all mutations: no `push`, no `pop`, no index assignment.

Now `as const`. When you write `as const` after an object or array literal, you're telling TypeScript to infer the most specific, narrowest possible type for every value in it — AND to make every property `readonly`. Without `as const`, the object `{ PENDING: "pending" }` has type `{ PENDING: string }`. With `as const`, it has type `{ readonly PENDING: "pending" }`. The string `"pending"` is now a literal type, not the wide `string` type.

This lets you extract a union of the values:

```typescript
type Status = typeof STATUS[keyof typeof STATUS];
```

`keyof typeof STATUS` gives you the union of the property names — `"PENDING" | "ACTIVE" | "INACTIVE"`. Then `typeof STATUS[keyof typeof STATUS]` gives you the type of the values at those keys — `"pending" | "active" | "inactive"`.

The same principle applies to `as const` arrays. `["admin", "editor", "viewer"] as const` has type `readonly ["admin", "editor", "viewer"]` — a tuple of three literal string types, not `string[]`. And `(typeof VALID_ROLES)[number]` extracts the union `"admin" | "editor" | "viewer"`.

This pattern — `as const` plus `typeof` plus `keyof` — is one of those TypeScript idioms that looks intimidating the first time you see it and becomes completely natural within a week of using it.

---

## [56:00–60:00] Summary and Part 2 Preview

Slide 16 — let's look at what we've covered in Part 1.

Primitive types — `string`, `number`, `boolean`, `null`, `undefined`. Always enable `strictNullChecks`. Special types — `any` as escape hatch, `unknown` as the safe alternative, `void` for side-effect functions, `never` for unreachable code and exhaustive checks.

Type aliases give names to any type expression. Union types say "one of these types." Literal types constrain to specific values. Discriminated unions use a shared literal field to enable safe narrowing.

Interfaces describe object shapes — with optional properties, readonly properties, method signatures, extension, and the unique ability to merge declarations.

Arrays typed as `T[]`, tuples for fixed-length positionally-typed data, enums for named constants with string enums as the safer choice, `as const` as the lightweight alternative.

Type assertions with `as` for the DOM and other known-type scenarios. `satisfies` for validation without widening. Function typing with parameters, optional and default values, rest params, type expressions, and overloads. And `readonly` with `as const` for immutability at the type level.

In Part 2, we're going up a level. Generics will let you write functions and data structures that work with any type while remaining fully type-safe — think `Array<T>`, `Promise<T>`. Classes in TypeScript add access modifiers on top of what you already know from Day 14. Decorators — these are the mechanism behind Angular's entire component model. TypeScript's compiler configuration lets you dial in exactly how strict you want to be. Utility types like `Partial`, `Pick`, and `Omit` transform existing types without repetition. Type guards let you safely narrow unions. And we'll settle the interface versus type debate definitively.

Take a ten minute break. We'll pick up with generics.

---

*End of Part 1 Script*
