# Week 3 - Day 15: TypeScript
## Part 2 Lecture Script — Generics, Classes & Advanced Types

**Total time:** 60 minutes
**Slides:** 17
**Delivery pace:** ~165 words/minute

---

## [00:00–02:00] Opening — The Power of TypeScript's Type System

Welcome back. Part 1 gave you the vocabulary of TypeScript — the core types, the syntax, how to describe the shape of data. Part 2 is about the machinery — the features that make TypeScript scale to real applications.

We're covering five major areas: generics, classes, decorators, compiler configuration, and the advanced type features that professional TypeScript developers use every day. By the end of this session you'll understand why TypeScript is not just "JavaScript with types" — it's a genuinely different development experience.

Let's start with what is arguably the single most important TypeScript feature for writing reusable code: generics.

---

## [02:00–10:00] Generics

Slide 2 — The Problem Generics Solve.

Look at the first code block on this slide. I have a function called `identity`. It takes a value and returns that same value. Simple. But there's a problem: if I type it as `(value: any): any`, I've lost all type information. Look at what TypeScript lets me do with the result — I can call `doesNotExist()` on it, and TypeScript says nothing. Because `any` tells TypeScript to stop checking. The output type is `any`, which means "I give up."

How do we fix this without writing a separate function for every possible type? That's the problem. We can't write `identityString` and `identityNumber` and `identityArray` forever.

The solution is a type parameter. Look at the second code block:

```typescript
function identity<T>(value: T): T {
    return value;
}
```

The `<T>` in angle brackets is the generic type parameter. `T` is a placeholder. When you call `identity("hello")`, TypeScript replaces `T` with `string`. The parameter becomes `string`, and the return type becomes `string`. When you call `identity(42)`, `T` becomes `number`. TypeScript preserves the type through the function.

```typescript
const str = identity("hello");    // str is string
str.toUpperCase();                 // ✅
str.doesNotExist();               // ❌ Error — TypeScript is watching again
```

That's the fundamental insight of generics. You're parameterizing a function over a type, just like you parameterize a function over a value.

The naming convention: `T` for a general type, `U` for a second type, `K` for a key type, `V` for a value type, `E` for an element type. These are conventions. You can use any identifier — but stick to conventions because that's what other developers will expect.

Slide 3 — Generic Functions in Practice.

Let's build on this. `wrap<T>` takes a value of type `T` and returns `{ data: T }`. TypeScript infers `T` from the argument — you don't need to write `wrap<number>(42)`. TypeScript sees the `42`, infers `T = number`, and the return type is `{ data: number }`.

Multiple type parameters — `pair<K, V>` takes a key and a value and returns a tuple `[K, V]`. Call it with `pair("age", 30)` — K is `string`, V is `number`, return is `[string, number]`. TypeScript figures all of this out from the call site.

Generic array utilities are extremely useful. `getFirst<T>` returns `T | undefined` — because the array might be empty. TypeScript knows that `getFirst([1, 2, 3])` returns `number | undefined`. You can't call `.toFixed()` on the result without first checking it's not `undefined`.

Now look at the `getProperty` function at the bottom. `<T, K extends keyof T>`. I'm going to break this down. `T` is the object type. `K` must be a key that exists on `T` — that's what `extends keyof T` means. And the return type is `T[K]` — the type of the value at that specific key in `T`.

Call it with `getProperty(user, "name")` — TypeScript knows `"name"` is a valid key of `user`, and the return type is `string`. Call it with `getProperty(user, "id")` — return is `number`. Call it with `getProperty(user, "missing")` — compile error: `"missing"` is not a key of `user`. This is type-safe property access. No more `obj[key]` returning `any`.

Slide 4 — Generic Constraints.

When `T` can be absolutely anything, you can't access any properties on it. TypeScript doesn't know if `T` has a `.length` property because `T` might be a number, which doesn't.

The constraint syntax uses `extends`:

```typescript
function getLength<T extends { length: number }>(value: T): number {
    return value.length;
}
```

`T extends { length: number }` means: "T must have at least a `length` property of type `number`." Now TypeScript knows `.length` is safe to access. Strings pass this constraint. Arrays pass it. Even custom objects with a `length` property pass it. Plain numbers don't — and TypeScript will catch that.

Default type parameters — `interface ApiResponse<T = unknown>`. If you use `ApiResponse` without specifying `T`, it defaults to `unknown`. You've seen this pattern in Promise and Array — `Promise<void>` means you specified `void`, but `new Promise()` without a type argument defaults to `unknown`.

Slide 5 — Generic Interfaces and Types.

Generic interfaces let you define reusable data structures. `Stack<T>` is a stack that can hold any type. You implement it with a concrete type — `class NumberStack implements Stack<number>`. TypeScript enforces that all the methods work with `number`.

Look at the `Result<T, E>` type at the bottom. This is a pattern borrowed from functional programming languages. A `Result` is either `{ ok: true; value: T }` or `{ ok: false; error: E }`. When you call `parseJSON<User>(someString)`, you get back a `Result<User>`. Before you can access the `user` data, you MUST check whether the result is `ok`. If you try to access `result.value` without checking, TypeScript says the property might not exist — because the error variant doesn't have `value`.

This pattern forces explicit error handling at the type level. You can't accidentally ignore the error case. This is significantly safer than try/catch, where you might forget the catch block.

You'll see this pattern in real-world libraries. Rust's `Result` type is the most famous example. TypeScript's `Either` type in libraries like `fp-ts`. They all follow this same idea.

---

## [10:00–16:00] Classes in TypeScript

Slide 6 — Classes in TypeScript.

You know ES6 classes from Day 14. TypeScript adds three things to them: type annotations, access modifiers, and stricter initialization rules.

First, in TypeScript, you must declare class properties before you can use them in the constructor. You can't just do `this.name = name` in the constructor if you haven't declared `name` at the class level. TypeScript requires:

```typescript
class Animal {
    name: string;    // ← must declare this
    sound: string;
    constructor(name: string, sound: string) {
        this.name = name;
        this.sound = sound;
    }
}
```

The types on the constructor parameters describe what you PASS IN. The types on the class properties describe what you STORE. Usually they're the same, but they don't have to be.

The `strictPropertyInitialization` rule — part of `strict: true` — requires that every declared class property is initialized either with a default value or in the constructor. If you declare `name: string` and never initialize it, TypeScript flags it as an error. This catches a very real category of bugs where you access `this.name` and it's `undefined` because you forgot to set it.

Slide 7 — Access Modifiers.

TypeScript adds four access modifiers. `public` is the default — the property or method is accessible from anywhere. `private` restricts access to inside the class. `protected` allows access inside the class AND inside subclasses. `readonly` means the property can be set during initialization but never afterward.

Look at the `BankAccount` example. `balance` is `private` — external code can't read or write it directly. `owner` is `protected` — subclasses like `SavingsAccount` can access it. `accountId` is `public` — readable anywhere. `openedDate` is `readonly` — it's set once in the constructor and never changes.

This is familiar if you're coming from Java or C#. The syntax is slightly different but the semantics are identical.

Now, parameter properties — this is a TypeScript shorthand that I want you to know because you'll see it constantly in Angular and NestJS code:

```typescript
class Product {
    constructor(
        public readonly id: number,
        public name: string,
        private price: number
    ) {}
}
```

By putting `public`, `private`, `protected`, or `readonly` in front of a constructor parameter, TypeScript automatically declares the property AND assigns the parameter value to it. You don't need a property declaration at the class level, and you don't need assignment statements in the constructor body. This dramatically reduces boilerplate.

TypeScript `private` versus JavaScript `#private` — I need to distinguish these. TypeScript's `private` keyword is a compile-time restriction only. At runtime, in the compiled JavaScript, the property is completely accessible. If someone imports your compiled JS file, they can access private properties freely. JavaScript's `#` private fields — which you learned in Day 14 — are enforced by the JavaScript engine at runtime. For true encapsulation that holds in the output code, use `#`. For type-system-level enforcement with slightly better readability, use TypeScript `private`. Angular uses TypeScript `private` throughout its code.

Slide 8 — Abstract Classes and `implements`.

An abstract class is a class that cannot be instantiated directly. It's a template. You define abstract methods — methods that have no body, just a signature — and every concrete subclass must implement them.

Look at `Shape`. It has two abstract methods: `getArea` and `getPerimeter`. It also has a non-abstract `toString` method — this is shared implementation that all shapes inherit for free. When I extend `Shape` with `Circle` and provide implementations of `getArea` and `getPerimeter`, everything works. If I forget to implement one, TypeScript tells me immediately.

Try to do `new Shape()` — error. Can't instantiate an abstract class. This mirrors the concept from Java exactly.

`implements` is a different relationship. Where `extends` means "I am a subtype of," `implements` means "I fulfill this contract." A class can only `extends` one parent. But a class can `implements` any number of interfaces.

`class FormData implements Serializable, Validatable` — the class must provide implementations for every method in both interfaces. If it's missing any, TypeScript flags the class with an error.

Abstract class versus interface: use an abstract class when you have shared implementation to provide alongside the contract. Use an interface when the contract is purely structural — no implementation. A class can only inherit from one abstract class but can implement many interfaces.

---

## [16:00–24:00] Decorators

Slide 9 — Decorators Overview.

Decorators are a feature that people either love immediately or find confusing at first. By the end of this section, you're going to understand exactly what they are, because you're going to need them on Day 16b — Angular uses decorators for everything.

A decorator is a function. That's it. It's a function that receives information about the thing it's decorating — a class, a method, a property, or a parameter — and can observe or modify it.

The syntax is the `@` symbol followed by the decorator name, placed above the thing it decorates. Like this: `@Component({...})` above a class definition in Angular.

Before you can use decorators, you need to enable them in `tsconfig.json`. Set `"experimentalDecorators": true`. If you're building an Angular application, Angular sets this for you automatically.

There are actually two decorator systems in TypeScript right now. The experimental one — enabled by that flag — is what Angular, NestJS, TypeORM, and virtually every major TypeScript framework uses today. There's also a newer TC39 Stage 3 standard, available in TypeScript 5.0+, that represents the future standard. They're slightly different syntactically. We're covering the experimental system because that's what you'll encounter in production code.

A decorator factory is a function that returns a decorator. It accepts arguments. `@log("info")` — the `log("info")` call returns the actual decorator function. This lets you configure decorators. Almost all decorators you'll use in frameworks are factories — `@Component({...})`, `@Injectable()`, `@Input()`.

Slide 10 — Class Decorators.

A class decorator receives the constructor function of the class. It can return nothing, in which case it just observes or modifies the class in place. Or it can return a new class that replaces the original.

Look at the `@sealed` decorator. It calls `Object.seal()` on the constructor and the prototype. After sealing, you can't add new properties to the class or its prototype. This is exactly the kind of enforcement TypeScript can't do alone — it's a runtime modification.

Look at the `@Entity("users")` example. The decorator receives the class constructor, extends it, and adds `_tableName` to every instance. This is the basic mechanism that ORMs like TypeORM use — you decorate a class with `@Entity("table_name")` and the ORM reads that metadata to know which database table to query.

And at the bottom — the Angular preview. Angular's `@Component` is a class decorator factory. It receives a configuration object with `selector`, `templateUrl`, `styleUrls`. It uses that information to register the component with Angular's framework. Internally it's doing exactly what we've been looking at — it's a function that receives the constructor and does something with the configuration.

When you see this on Day 16b:

```typescript
@Component({
  selector: 'app-root',
  templateUrl: './app.component.html'
})
class AppComponent { }
```

You'll recognize it. It's a class decorator. The configuration object is an argument to the decorator factory. Angular reads it at startup to set up the component system. It is not magic. It is the pattern on this slide.

Slide 11 — Method and Property Decorators.

A method decorator receives three arguments: the target (the class prototype), the property key (the method name as a string), and a property descriptor. The `PropertyDescriptor` is JavaScript's way of describing the attributes of a property — whether it's writable, enumerable, configurable, and what its value is. For a method, `descriptor.value` is the function.

The `@measure` decorator wraps the original method. It stores a reference to the original function, replaces `descriptor.value` with a new function that measures time, calls the original, and logs the duration. The replacement is transparent — the caller doesn't know the method was wrapped.

This is one of the most common use cases for method decorators — logging, timing, authorization checks, retry logic, caching. The decorator pattern from Gang of Four design patterns maps directly onto TypeScript method decorators.

A property decorator receives just the target and the property key. It cannot access the actual value — property values exist on instances, not on the class definition, so by the time a property decorator runs, no instances exist yet. Property decorators are primarily used for metadata registration — frameworks like NestJS use them to mark which properties should be validated, transformed, or injected.

Parameter decorators receive the target, the method name, and the index of the parameter. Angular's `@Inject()` is a parameter decorator — it tells Angular's DI system to inject a specific dependency at that parameter position.

Here's the mapping to Angular that you'll use in Week 4: `@Input()` and `@Output()` are property decorators. `@HostListener()` is a method decorator. `@Inject()` is a parameter decorator. These are not magic keywords — they're TypeScript decorators exactly like what you see on this slide.

---

## [24:00–32:00] Compiler and tsconfig.json

Slide 12 — the TypeScript Compiler.

The `tsc` command is the TypeScript compiler. Most of the time you run it without arguments from the project root, and it reads `tsconfig.json` to know what to do.

A few commands to know: `tsc --init` creates a `tsconfig.json` with sensible defaults. `tsc --watch` puts the compiler in watch mode — it recompiles every time you save a file. `tsc --noEmit` runs the type checker but doesn't write any output files. This is extremely useful in CI/CD pipelines where you want to verify there are no type errors without actually running a build.

Look at the target options in the table. If you're building a Node.js 16+ application, target `ES2022`. If you're building for modern browsers, `ES2020` or `ES2022` are both fine. If you have a legacy compatibility requirement — IE11, for example — you'd target `ES5`. In most cases today, you're targeting ES2020 or newer.

The comparison table at the bottom is important. `tsc` does the full type checking AND produces JavaScript output. `ts-node` runs TypeScript directly — convenient for scripts and development, but it doesn't do full type checking on all files, just the ones it executes. Tools like esbuild and Vite use fast transpilers that strip TypeScript types without doing full type checking. These are faster but they can miss type errors.

The safest practice: run `tsc --noEmit` in your CI pipeline as a separate step. Let your bundler handle the fast compilation for development. Let `tsc` handle the authoritative type check before you deploy.

Slide 13 — tsconfig.json.

Let's walk through the key options. `target` is the JavaScript version you're outputting. `module` is the module system — `commonjs` for Node.js applications, `esnext` or `es2020` for browser applications built with bundlers. `lib` is the list of built-in type definitions to include — `"DOM"` gives you types for `document`, `window`, `HTMLElement`, and so on.

`outDir` and `rootDir` define the source and output folder structure. `declaration: true` generates `.d.ts` type definition files alongside the JavaScript — you need this if you're publishing a library.

`strict: true` — this is the single most important option. It enables a bundle of strict checks all at once. `strictNullChecks` — you know this one. `noImplicitAny` — no implicit `any` types allowed. `strictFunctionTypes` — more rigorous checking of function type compatibility. `strictPropertyInitialization` — all class properties must be initialized.

Turn on `strict: true` from day one on every project. Every single time. If you don't, you will accumulate technical debt in the form of hidden type errors that are painful to fix later.

Additional quality checks: `noUnusedLocals` and `noUnusedParameters` flag variables and parameters you declared but never used. `noImplicitReturns` requires that every code path in a function explicitly returns a value — no silent `undefined` returns. `noFallthroughCasesInSwitch` prevents accidental fall-through in switch statements.

`esModuleInterop: true` — this is almost always on. It allows you to write `import React from "react"` even though React is technically a CommonJS module. Without it, you'd need the more verbose `import * as React from "react"`.

The `paths` option lets you set up module aliases. Instead of `import { utils } from "../../utils"`, you can write `import { utils } from "@/utils"`. Very common in large projects.

And the decorators options — `experimentalDecorators` and `emitDecoratorMetadata`. Both are required for Angular. Angular projects set these automatically in the default `tsconfig.json` when you scaffold with the Angular CLI.

---

## [32:00–42:00] Utility Types

Slide 14 — Utility Types, Part 1.

TypeScript ships with a set of built-in generic types that transform other types. They're defined in TypeScript's standard library. You use them by composing them with your own types.

`Partial<T>` makes every property of `T` optional. If `User` has `id`, `name`, `email`, and `age` — all required — then `Partial<User>` has all four properties but they're all optional.

Where do you use this? The most common case is update operations. When you PATCH a resource, you're providing a subset of the fields. The function that applies the update should accept `Partial<User>` — any combination of fields, none required. Internally, it spreads the current object with the updates:

```typescript
return { ...currentUser, ...updates };
```

`Required<T>` is the inverse — it makes every optional property required. If you have a config interface with all optional fields, `Required<Config>` forces every one to be provided.

`Readonly<T>` makes every property readonly. You've seen `readonly` on individual properties. `Readonly<User>` applies it to every property in one step. Useful for function parameters that should not be mutated, for freeze patterns, for Redux-style immutable state.

And you can compose them: `Readonly<Partial<User>>` gives you an object where every property is both optional and readonly. This is what you'd pass to a React component that displays user information — it can access whatever fields are present, but it should not modify any of them.

Slide 15 — Utility Types, Part 2.

`Pick<T, K>` selects a subset of properties. `Pick<User, "id" | "name">` gives you just `{ id: number; name: string }`. Use this when an API endpoint should return only some fields — for example, a list endpoint that returns user summaries without email, password, or sensitive data.

`Omit<T, K>` is the inverse — it gives you everything EXCEPT the specified keys. `Omit<User, "password">` gives you all user fields minus the password. Use this for public-facing types that should never expose sensitive data.

These two are complementary. When there are a few fields you want to KEEP, use `Pick`. When there are a few fields you want to REMOVE, use `Omit`.

`Record<K, V>` — a typed dictionary. `Record<"admin" | "editor" | "viewer", User[]>` creates an object where each key must be one of those three role strings and each value must be a `User[]`. TypeScript enforces both the allowed keys and the value type.

`Exclude<T, U>` removes types from a union. `Exclude<string | number | boolean, boolean>` gives `string | number`. `Extract<T, U>` keeps only the matching types.

`ReturnType<T>` extracts the return type of a function — `ReturnType<typeof getUser>` gives you the type that `getUser` returns, without you having to write it out again. This is extremely useful when you can't easily import the return type, or when the function's return type changes and you want derived types to update automatically.

`Parameters<T>` gives you the parameter types as a tuple. If `createUser` takes `(name: string, age: number)`, then `Parameters<typeof createUser>` is `[name: string, age: number]`. Useful for higher-order functions.

The mental model for utility types: instead of copy-pasting interfaces and modifying them, DERIVE new types from existing ones. If `User` changes, `Partial<User>` and `Omit<User, "password">` and `Pick<User, "id">` all update automatically.

---

## [42:00–50:00] Type Guards and Narrowing

Slide 16 — Type Guards.

In Part 1 we talked about `unknown` — you have to check the type before you can use it. In Part 1 we also talked about discriminated unions — TypeScript uses the `kind` property to narrow. Now we're going deeper.

Type narrowing is TypeScript's ability to look at a conditional check and understand that inside the block, the type is more specific than outside it.

`typeof` narrowing — the most basic form. `if (typeof input === "string")` — inside the block, TypeScript knows `input` is `string`. Outside it, `input` is still `string | number`. TypeScript's control flow analysis is smart enough to track this through multiple branches, ternary operators, early returns, and more.

`instanceof` narrowing — used for class instances. `if (error instanceof TypeError)` — inside the block, TypeScript knows `error` is a `TypeError`, which has all the properties of both `TypeError` and its parent `Error`. TypeScript also knows that in subsequent `else if` branches, the type is narrowed further.

The `in` operator — checks if a property exists on an object. `if ("meow" in animal)` — TypeScript knows inside the block that `animal` has a `meow` property, which means it must be a `Cat`. In the else branch, TypeScript knows it's a `Dog`. This works without a `kind` discriminant — TypeScript uses the structure of the object.

User-defined type guards — this is the most powerful form. You write a function whose return type is `value is SomeType`. That's a type predicate. If the function returns `true`, TypeScript treats the parameter as `SomeType` in the calling scope.

Look at `isUser`:

```typescript
function isUser(value: unknown): value is User {
    return typeof value === "object" && value !== null && "id" in value && "name" in value;
}
```

After `if (isUser(rawData))`, TypeScript knows `rawData` is a `User`. No assertion needed. No `as User`. The type guard provides the verification, and TypeScript accepts it.

This is how you should handle API responses. Fetch the JSON, parse it as `unknown`, run it through a type guard, and then work with the typed value. This is correct, safe, and composable.

The discriminated union narrowing at the bottom connects back to Part 1. I have `ApiResult<T>` with a `status` field — either `"success"` or `"error"`. Check `result.status === "success"` and TypeScript narrows to the success variant. The `data` property exists. In the else branch, TypeScript knows it's the error variant and the `message` property exists.

This is type-safe error handling for async operations. Significantly safer than throwing and catching exceptions, especially in asynchronous code.

---

## [50:00–58:00] Interface vs Type and Summary

Slide 17 — Interface vs Type. This is the question I get asked constantly.

Let me go through the comparison table. Both `interface` and `type` can describe object shapes. Both work with `implements` in classes. Both support recursive definitions. In terms of everyday usage for describing the shape of a plain object — they are functionally equivalent.

So what are the differences?

`type` can do things `interface` cannot. Union types — `type Status = "active" | "inactive"` — you cannot express this with `interface`. Intersection types — `type AdminUser = User & Admin` — interfaces use `extends` instead, but the semantics differ slightly. Tuples, mapped types, conditional types, template literal types — all `type` only.

`interface` can do one thing `type` cannot: declaration merging. If you declare the same interface name twice, TypeScript merges them. This is how the global `Window`, `Element`, `NodeList` types in the browser are extended by libraries. How `@types/react` adds `JSX` namespace. How `@types/node` adds `process`, `__dirname`, and so on to the global scope. This is not possible with type aliases.

When should you use which? The TypeScript team's official guidance is "use interface until you need type." But in practice, many large TypeScript codebases use both freely for object shapes and the choice is stylistic.

Here's my practical guidance: Use `interface` when you're defining a contract for a class to implement, when you're building a public library API that consumers might want to augment, or when you specifically want declaration merging. Use `type` when you're working with unions, intersections, tuples, or any of the advanced type features. When both work — choose one and be consistent within your project.

I'll also mention: some style guides and linters enforce one over the other. ESLint's `@typescript-eslint` plugin has rules for this. If you're joining a project with existing conventions, follow them.

Now let's look at the summary at the bottom of this slide.

All five learning objectives are covered. Write type-safe code — you now know annotations, inference, interfaces, type aliases, union types, generics, utility types, and type guards. Configure the TypeScript compiler — tsconfig.json, strict mode, all the key options. Use interfaces and type aliases effectively — we've covered both in depth. Apply generics for reusable code — generic functions, interfaces, constraints, defaults. Understand when to use Interface vs Type — you just heard the definitive answer.

---

## [58:00–60:00] Looking Ahead to Week 4

Tomorrow — or depending on your track, starting next week — you're going into React and Angular. Let me tell you exactly how everything from today connects.

If you're on the React track: you'll immediately use `React.FC<Props>` — that's a generic type. You'll use `useState<User | null>(null)` — generic with a union type. You'll type event handlers like `(e: React.ChangeEvent<HTMLInputElement>) => void` — and now you know what that type expression means. You'll write interfaces for your component props and API response types.

If you're on the Angular track: Angular is TypeScript-first. The `@Component`, `@Injectable`, `@Input`, `@Output` decorators you'll use every single day — you understand exactly what they are. The interfaces you'll implement like `OnInit`, `OnDestroy`, `ChangeDetectionStrategy` — you know what `implements` means. Generics appear in `HttpClient.get<User[]>()` — you know that.

The utility types will appear in both tracks. `Partial<T>` in form state. `Omit<T, "password">` in API response types. `Pick<T, K>` in component props. `Record<string, T>` in dictionaries.

TypeScript is not something separate from the frameworks you're about to learn. It's the language those frameworks are written in and designed for. Today's investment pays dividends starting tomorrow.

That's Day 15. Take a moment to review the summary slides. On Monday we're building applications.

---

*End of Part 2 Script*
