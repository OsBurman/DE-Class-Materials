# Day 15 — Part 1 Walkthrough Script
## TypeScript: Types, Annotations, Interfaces, Enums, Functions
### Estimated time: 90 minutes

---

## Pre-Class Setup
- Open VS Code with `Part-1/01-types-and-basics.ts` visible
- Have a terminal ready: `npx ts-node 01-types-and-basics.ts`
- Install if needed: `npm install -g ts-node typescript`
- Have the TypeScript Playground open as backup: https://www.typescriptlang.org/play
- Confirm VS Code shows TypeScript errors inline (hover to see them)

---

## Opening (3 min)

**Say:**
> "Today we start TypeScript. If you've been doing JavaScript this week, TypeScript is going to feel immediately familiar — because it IS JavaScript, plus a type system layered on top. Every JavaScript file is valid TypeScript. You don't relearn the language; you add types to it."

**[ACTION]** Write on board:
```
TypeScript = JavaScript + Static Type System
         .ts ─── tsc compiler ──► .js
```

**Say:**
> "The TypeScript compiler reads your `.ts` files and outputs plain `.js` files. The browser never sees TypeScript — it runs the JavaScript. But the compiler checks your types before that happens, catching bugs that would otherwise only appear at runtime."

**[ASK]** "What kind of bugs do you think a type system can catch?"
> *(Passing a string where a number is expected, calling `.length` on a number, accessing a property that doesn't exist, forgetting to handle `null`.)*

---

## PART 1A — Type Annotations & Inference (10 min)
### File: `01-types-and-basics.ts` → Section 2

---

### [ACTION] Scroll to Section 2 — TYPE ANNOTATIONS & TYPE INFERENCE.

**Say:**
> "Two ways to give TypeScript type information. First: you annotate explicitly — you write the type yourself. Second: inference — TypeScript figures it out."

**[ACTION]** Show `let username: string = "Alice"`.

**Say:**
> "The colon after the variable name is the annotation. `string` is the type. This tells TypeScript: this variable can only ever hold strings."

**[ACTION]** Show `let city = "New York"` — the inferred version.

**Say:**
> "Here we don't annotate, but TypeScript still knows it's a string because we assigned a string. Once inferred, the type is locked. Try to assign a number to it — the compiler will tell you no."

**[ACTION]** Temporarily un-comment `// city = 42;` in the editor so students see the red underline. Then re-comment it.

**Say:**
> "That red squiggle is the TypeScript compiler working in real time. You'd normally catch this BEFORE running your code. This is the core value proposition."

**[ASK]** "When would you use an explicit annotation instead of letting TypeScript infer?"
> *(When you declare a variable without assigning it yet — `let winner: string;` — and when you want the code to be intentionally self-documenting.)*

---

## PART 1B — Simple Types: string, number, boolean (5 min)
### Section 3

---

**Say:**
> "The three workhorses. You already know these types from JavaScript — TypeScript just makes them explicit."

**[ACTION]** Show `greet(name: string): string` and run it.

**Say:**
> "Two annotations on this function: the parameter type and the return type. The return type after the colon at the end. TypeScript will verify that what you return actually matches."

**[ACTION]** Try calling `greet(42)` in the editor. Show the error.

**Say:**
> "The compiler catches this immediately — before the code runs. In plain JavaScript you'd only discover this if you happened to test that specific code path."

---

## PART 1C — Special Types: any, unknown, never, void (12 min)
### Section 4

---

### [ACTION] Scroll to Section 4.

**Say:**
> "Now the interesting ones. TypeScript has four special types that don't exist in JavaScript. Understanding these is what separates competent TypeScript from cargo-culting it."

---

### any (3 min)

**[ACTION]** Show `let anything: any = "hello"`.

**Say:**
> "`any` is the escape hatch. It turns off type checking for that variable. You can assign anything to it, call any method on it — the compiler won't complain. It's like writing plain JavaScript."

**⚠️ WATCH OUT:**
> "`any` is TypeScript giving up. Using `any` everywhere defeats the entire purpose of TypeScript. Use it only when migrating existing JavaScript code or when you genuinely can't know the type — and plan to remove it later."

---

### unknown (4 min)

**[ACTION]** Show `let userInput: unknown = "hello"`.

**Say:**
> "`unknown` is the safe alternative to `any`. The value could be anything — but you MUST narrow it before you use it. The compiler won't let you call methods on an `unknown` value."

**[ACTION]** Show the `if (typeof userInput === "string")` check.

**Say:**
> "This is called type narrowing. Inside that if-block, TypeScript knows it's a string, so you can call `.toUpperCase()`. Outside it, it's still `unknown`."

**[ASK]** "Why would you prefer `unknown` over `any` when receiving data from an external API?"
> *(Because `any` lets you use the data incorrectly without any warnings. `unknown` forces you to check what you've got before using it — much safer.)*

---

### void (2 min)

**[ACTION]** Show `function logMessage(msg: string): void`.

**Say:**
> "`void` is for functions that don't return a meaningful value. You'll use this constantly — any function that just logs, mutates state, or performs a side effect."

---

### never (3 min)

**[ACTION]** Show `function fail(msg: string): never`.

**Say:**
> "`never` is for code that should never reach a certain point. A function that always throws never returns — its return type is `never`. But the real power is in exhaustive checking."

**[ACTION]** Show the `switch` on `Direction` with the `never` in the `default` case.

**Say:**
> "If you add a new direction — say `'up'` — to the `Direction` type and forget to add it to this switch, TypeScript will error on the `default` case because the value is no longer `never`. The compiler forces you to handle every case. This is incredibly powerful."

→ **TRANSITION:** "Now let's look at how we describe the shape of objects."

---

## PART 1D — Object Types & Interfaces (15 min)
### Section 5

---

### [ACTION] Scroll to Section 5.

**Say:**
> "In JavaScript, objects are just bags of properties. TypeScript lets you describe what those bags must look like — using interfaces."

**[ACTION]** Show the `interface User` definition.

**Say:**
> "An interface is a named blueprint for an object's shape. Every property is listed with its name, a colon, and its type. This isn't runtime code — it compiles away to nothing. It's purely for the type checker."

**[ACTION]** Point to `age?: number` and `readonly createdAt: Date`.

**Say:**
> "The `?` makes a property optional. Without it, the property is required. The `readonly` modifier prevents reassignment after the object is created — useful for IDs, timestamps, immutable config."

**[ACTION]** Show what happens if you try `alice.createdAt = new Date()`.

**Say:**
> "The compiler stops you. This is the kind of bug that's nearly impossible to catch in plain JavaScript because nothing prevents you from accidentally overwriting a timestamp."

**[ACTION]** Show `interface Admin extends User`.

**Say:**
> "Interfaces compose with `extends`. `Admin` gets everything from `User` plus its own additional properties. This is the TypeScript equivalent of class inheritance, but for types."

**[ACTION]** Show interface declaration merging.

**Say:**
> "This is unique to interfaces: if you declare the same interface name twice, TypeScript merges them. This is how third-party libraries let you extend their types — you can add properties to their interfaces in your own code without touching their source. Types can't do this."

**⚠️ WATCH OUT:**
> "Declaration merging is powerful but can be confusing. If two interface declarations conflict — same property name, different types — TypeScript will error. It only merges compatible declarations."

---

## PART 1E — Union Types & Type Aliases (10 min)
### Section 6

---

### [ACTION] Scroll to Section 6.

**Say:**
> "Sometimes a value can legitimately be more than one type. TypeScript handles this with union types."

**[ACTION]** Show `let id: number | string`.

**Say:**
> "The pipe character `|` means 'or'. This `id` can be a number or a string — both valid. TypeScript tracks which type it actually is and only lets you use operations common to both."

**[ACTION]** Show `formatId` with the `typeof` narrowing inside.

**Say:**
> "When you have a union, you often need to handle each case separately. This `if (typeof id === 'number')` is a type guard — we'll cover those in depth in Part 2."

**[ACTION]** Show `type Status = "active" | "inactive" | "suspended"`.

**Say:**
> "String literal unions are extremely powerful. `Status` isn't any string — it's exactly one of three values. TypeScript will catch a typo like `'actve'` immediately."

**[ACTION]** Show the discriminated union with `Shape`.

**Say:**
> "Discriminated unions are a pattern you'll use constantly. Each member has a `kind` field that's a unique literal string. The compiler can use that field to narrow to the exact type in each `switch` case. Notice: no `default` needed — TypeScript knows we've handled all three shapes."

---

## PART 1F — Arrays & Tuples (8 min)
### Section 7

---

### [ACTION] Scroll to Section 7.

**Say:**
> "Two syntaxes for typed arrays. `string[]` and `Array<string>` are equivalent. I prefer `string[]` for simple types, `Array<T>` for generics."

**[ACTION]** Show `const users: User[]`.

**Say:**
> "Array of objects typed with our interface. Every element is checked. TypeScript knows that `users[0].name` is a `string` — it doesn't need to look up the type at runtime."

**[ACTION]** Show `readonly string[]`.

**Say:**
> "Readonly arrays prevent mutation. No `push`, `pop`, `splice`. Great for configuration data, lookup tables, or any value that should never change."

**[ACTION]** Scroll to tuples.

**Say:**
> "Tuples are arrays where every position has a specific, fixed type. The length is also fixed. It's perfect when you have a small, ordered group of related values."

**[ACTION]** Show `type NamedCoord = [string, number, number]` and the destructuring.

**Say:**
> "Tuples destructure cleanly. `const [city, latitude, longitude] = nyc` — each variable gets the exact type from that position."

**⚠️ WATCH OUT:**
> "If you try to access index 3 on a tuple that only has 3 elements, TypeScript catches it. Arrays are open-ended; tuples have a known, fixed length."

---

## PART 1G — Enums (8 min)
### Section 8

---

### [ACTION] Scroll to Section 8.

**Say:**
> "Enums are named sets of constants. They make intent crystal clear — instead of sprinkling the string `'north'` or the number `404` throughout your code, you use `Direction.North` or `HttpStatus.NotFound`."

**[ACTION]** Show `enum Direction` — the numeric default.

**Say:**
> "By default, TypeScript assigns 0, 1, 2, 3. Notice you can also reverse-look them up — `Direction[0]` gives you the string `'North'`. That's called reverse mapping and it's unique to numeric enums."

**[ACTION]** Show `enum HttpStatus` with explicit values.

**Say:**
> "You can assign explicit numbers. HTTP status codes are a perfect use case — the numbers are meaningful and well-known."

**[ACTION]** Show `enum LogLevel` with string values.

**Say:**
> "String enums are often preferable because the values are meaningful in logs and debugging. A numeric enum value of `2` in a log tells you nothing. `'WARN'` tells you exactly what happened."

**[ACTION]** Show `const enum Color`.

**Say:**
> "`const enum` is an optimization — at compile time, every use of `Color.Blue` is replaced with the literal string `'BLUE'`. No enum object is created in the JavaScript output. Use this when performance matters and you don't need the reverse mapping."

---

## PART 1H — Type Casting & Assertions (5 min)
### Section 9

---

### [ACTION] Scroll to Section 9.

**Say:**
> "Sometimes YOU know more than the compiler. Type assertions let you tell TypeScript 'trust me on this one'."

**[ACTION]** Show `as HTMLInputElement`.

**Say:**
> "`document.getElementById` returns `HTMLElement | null` — TypeScript doesn't know which specific element type it is. You know it's an input, so you assert it with `as HTMLInputElement`. Now you can access `.value` without an error."

**[ACTION]** Show the non-null assertion `!`.

**Say:**
> "The exclamation mark is a non-null assertion. You're telling TypeScript 'I know this is not null or undefined'. Use it sparingly — if you're wrong at runtime, you'll get the classic 'Cannot read properties of null' error."

**⚠️ WATCH OUT:**
> "Type assertions are a promise to the compiler, not a guarantee at runtime. If you lie — `'hello' as unknown as number` — the compiler trusts you but your code will behave incorrectly. Always be confident you're right when you assert."

---

## PART 1I — Functions & Function Types (10 min)
### Section 10

---

### [ACTION] Scroll to Section 10.

**[ACTION]** Show `function buildUrl(base, path, query?)`.

**Say:**
> "Optional parameters with `?`. The type becomes `string | undefined` automatically inside the function. You must handle the case where it's not provided."

**[ACTION]** Show `type MathOperation = (a: number, b: number) => number`.

**Say:**
> "You can type functions themselves. This is a function type — a type alias that describes a function's shape. Any function that takes two numbers and returns a number satisfies it. This is great for callbacks."

**[ACTION]** Show function overloads.

**Say:**
> "Overloads let you define multiple call signatures for the same function. The first three are the signatures — the public API. The last one is the implementation — never called directly. TypeScript checks callers against the signatures, not the implementation."

---

## Wrap-Up Q&A (5 min)

**[ASK]** "What's the difference between `any` and `unknown`?"
> *(`any` disables type checking. `unknown` accepts any value but forces you to narrow before using it — safer.)*

**[ASK]** "When would you use a tuple instead of an array?"
> *(When you have a fixed-length, ordered set of heterogeneous values — like a coordinate pair, or a [status, message] response.)*

**[ASK]** "What's the advantage of string literal union types like `type Status = "active" | "inactive"`?"
> *(Prevents invalid values at compile time. Typos become compiler errors instead of runtime bugs.)*

**[ASK]** "Why might you prefer a string enum over a numeric enum?"
> *(String values are meaningful in logs, debugging, and serialization. Numeric values require knowing the mapping.)*

**[ASK]** "What does the `readonly` keyword on an interface property do?"
> *(Prevents reassignment after the object is created — caught at compile time.)*

---

## Take-Home Exercise

1. Create a `type` alias `ApiResult<T>` that is either `{ success: true; data: T }` or `{ success: false; error: string }`. Write a function `handleResult(result: ApiResult<User>)` that uses a discriminated union switch to handle both cases.

2. Model a chess board: create a `type File = "a" | "b" | "c" | "d" | "e" | "f" | "g" | "h"` and `type Rank = 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8`. Create a `type Square = [File, Rank]` tuple. Write a function `squareToString(sq: Square): string` that returns e.g. `"e4"`.

3. Write an `interface Vehicle` with `make`, `model`, `year`, and optional `color`. Extend it with `interface ElectricVehicle` adding `batteryRange: number` and `chargeLevel: number`. Create two objects typed with each.

4. Create an enum `PaymentStatus` with values `Pending`, `Processing`, `Completed`, `Failed`, `Refunded`. Write a function `getStatusMessage(status: PaymentStatus): string` with a `switch` that handles every case and has a `never` check in the `default`.
