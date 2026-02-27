SLIDE 1: Title Slide
Slide content: "TypeScript: From JavaScript to Type Safety" — your name, date, course name.
Script:
"Good morning everyone. Today we're diving into TypeScript, and I want you to think of this lesson as the bridge between the JavaScript you already know and the professional-grade code you'll be writing on the job. By the end of this hour, TypeScript should feel less like a restriction and more like a superpower. Let's get into it."
(~1 minute)

SLIDE 2: What Is TypeScript and Why Does It Exist?
Slide content: Two columns — "JavaScript" (dynamic, no types, errors at runtime) vs "TypeScript" (static types, errors at compile time, superset of JS). Add the famous quote: "TypeScript is JavaScript with a safety net."
Script:
"You all just learned JavaScript. You know how easy it is to write something like let age = 'twenty-five' when you meant a number, and JavaScript just... goes with it. No complaints. Until your program breaks at 2am in production.
TypeScript was created by Microsoft in 2012 to solve exactly this problem. It's a superset of JavaScript, meaning every JavaScript file is already valid TypeScript. You're not throwing away what you learned. You're adding a layer on top of it.
The key difference is this: JavaScript finds your type errors at runtime — when the code is actually running. TypeScript finds them at compile time — before the code ever runs. That's the safety net.
When you write TypeScript, it compiles down to plain JavaScript. The browser never sees TypeScript. It only sees the JS output. So TypeScript is entirely a developer tool."
(~3 minutes)

SLIDE 3: The TypeScript Compiler and tsconfig.json
Slide content: Show all four commands students will use:
npm install -g typescript    # install the TypeScript compiler globally
tsc --init                   # generate tsconfig.json in your project folder
tsc                          # compile all TypeScript files
tsc --watch                  # recompile automatically on save
Below that, show the full generated tsconfig.json with key fields annotated:
{
  "compilerOptions": {
    "target": "ES2016",       // JS version to output — use ES2016+ for modern projects
    "strict": true,           // ALWAYS true — enables all safety checks
    "outDir": "./dist",       // where compiled JS files go
    "rootDir": "./src",       // where your .ts source files live
    "module": "commonjs"      // commonjs for Node; ESNext for modern frontend
  }
}
Note: All commands and the full tsconfig output are shown on the slide — no live terminal needed.
Script:
"Before we write a single line of TypeScript, you need to understand the toolchain. You install the TypeScript compiler globally with the first command on screen. Then you run tsc --init in your project folder — it generates a tsconfig.json file, which you can see here in full. This file is the brain of your TypeScript project.
Let me walk you through the most important options:
target — this tells the compiler which version of JavaScript to output. Set it to ES2016 or higher for modern projects.
strict — and I cannot stress this enough, always set this to true. Without it you're leaving half the benefits of TypeScript on the table.
outDir — where your compiled JavaScript files go. Typically a folder called dist.
rootDir — where your TypeScript source files live. Typically src.
module — how modules are handled. Use commonjs for Node projects or ESNext for modern frontend work.
In real projects you'll usually see tsc wired into an npm script so it runs automatically."
(~4 minutes)

SLIDE 4: Type Annotations and Type Inference
Slide content: Two columns side by side. Left column labeled "Explicit Annotation":
let name: string = "Alice";
let age: number = 30;
let isLoggedIn: boolean = true;
Right column labeled "Type Inference (no annotation needed)":
let name = "Alice";
// TypeScript infers: string ✓

let count = 0;
// TypeScript infers: number ✓
The inline comments on the right stand in for the IDE tooltip — no live editor needed. Below both columns, add the rule of thumb: "Annotate function parameters and return types. Let inference handle local variables."
Script:
"Now let's write some TypeScript. The most fundamental concept is the type annotation. You add a colon after a variable name and then the type, as shown on the left.
But TypeScript is smart. If you write let name = 'Alice' without any annotation at all, TypeScript looks at the value and infers the type. It already knows it's a string. You don't have to annotate everything — the comments on the right side stand in for what you'd see as a tooltip in your editor.
The general rule is: annotate when TypeScript can't infer, especially in function parameters and return types. Let the compiler do the work everywhere else. You'll develop a feel for this quickly."
(~3 minutes)

SLIDE 5: Simple Types — string, number, boolean
Slide content: One code block showing valid declarations, then the error case with the full compiler error message inline — no live editor needed:
let username: string  = "Alice";
let age:      number  = 30;
let isAdmin:  boolean = true;

// ✅ This is fine:
age = 31;

// ❌ This causes a compile error:
age = "thirty";
//  ^ Type 'string' is not assignable to type 'number'.  ts(2322)
Add a note: use lowercase string, number, boolean — not String, Number, Boolean. The uppercase versions are JS wrapper objects and you almost never want them.
Script:
"The three types you'll use most often are string, number, and boolean. They map directly to their JavaScript counterparts.
Notice TypeScript uses lowercase — string, not String. The uppercase versions exist but refer to JavaScript's built-in wrapper objects, and you almost never want those.
The real value shows up the moment you try to do something wrong. If I declare let age: number = 30 and then write age = 'thirty', the error you see on screen appears immediately in your editor. The program hasn't run yet. You already know there's a bug. That is the whole point."
(~3 minutes)

SLIDE 6A: Special Types — any vs unknown
Slide content: Two columns.
Left column labeled "any — opt out of type checking":
let x: any = 5;
x = "hello";  // fine
x = true;     // fine
x.foo();      // fine — no error!

⚠ Escape hatch only.
Use sparingly in new code.
Right column labeled "unknown — safe until proven":
let y: unknown = getData();

// Must prove the type first:
if (typeof y === "string") {
  y.toUpperCase(); // ✅ safe
}

✅ Prefer this over any.
Add callout: "Whenever you feel tempted to use any, ask: could I use unknown instead? Then write a type guard to narrow it."
Script:
"Now the special types — and these are important because misusing them is the most common mistake beginners make.
any turns off type checking entirely. You can assign anything to it and TypeScript stops checking how you use it. This is TypeScript's escape hatch — useful when migrating a large JavaScript codebase, but if you reach for any in new code you're opting out of everything TypeScript offers.
unknown is the safer version. You can assign anything to an unknown variable, but you cannot use it until you prove what type it is. You have to check first. You can see that on the right: only after the typeof check does TypeScript allow you to call .toUpperCase().
Whenever you'd reach for any, ask yourself if unknown is more appropriate. The answer is usually yes."
(~3 minutes)

SLIDE 6B: Special Types — void and never
Slide content: Two columns.
Left column labeled "void — no return value":
function logMessage(
  msg: string
): void {
  console.log(msg);
  // no return value
}

Common for: event handlers,
fire-and-forget functions.
Right column labeled "never — unreachable / impossible":
function crash(
  msg: string
): never {
  throw new Error(msg);
}

Also used in exhaustive checks —
TypeScript uses never to confirm
every case in a union is handled.
Script:
"Two more special types.
void is for functions that don't return a value. You'll use this as the return type of event handlers and other fire-and-forget functions. If you've worked with other typed languages this will feel familiar.
never represents something that can never happen. A function that always throws an error or runs an infinite loop returns never, because it literally never produces a value. You'll also see it in exhaustive checks — TypeScript uses never to tell you that you've handled every possible case in a union. It appears in more advanced patterns, but now you'll recognize it when you see it."
(~2 minutes)

SLIDE 7: Object Types and Interfaces
Slide content: One code block showing the inline type first, then the refactored interface, then a valid and invalid usage:
// Inline object type — works but not reusable:
let user: { name: string; age: number } = { name: "Alice", age: 30 };

// Interface — reusable, readable, preferred:
interface User {
  name: string;
  age: number;
  email?: string;   // ? makes this optional
}

const alice: User = { name: "Alice", age: 30 };  // ✅ email omitted — fine
const bob: User   = { name: "Bob" };             // ❌ age is missing — error
Add callout: "TypeScript uses structural typing (duck typing): if an object has all required properties, it's compatible — even without explicitly declaring the type."
Script:
"In JavaScript, objects are everywhere. TypeScript lets you describe the shape of an object using either an inline type or an interface.
The inline version works but once you need that shape in more than one place, you pull it into an interface. That question mark on email makes it optional. TypeScript is fine if a User object doesn't include email, but it will complain if name or age is missing.
TypeScript uses structural typing — sometimes called duck typing. If an object has all the required properties, TypeScript considers it compatible with the interface, even if it was never explicitly declared as that type. This feels natural coming from JavaScript."
(~4 minutes)

SLIDE 8: Union Types and Type Aliases
Slide content: One code block:
// Union type — value can be one of several types:
let id: string | number;
id = 101;         // ✅
id = "ABC-101";   // ✅
id = true;        // ❌ boolean not in union

// Type alias — give a name to any type:
type ID = string | number;

// Literal type union — only these exact string values allowed:
type Status = "active" | "inactive" | "pending";

let s: Status = "active";   // ✅
let t: Status = "deleted";  // ❌ not in the union
Add callout: "Literal type unions are powerful — TypeScript catches typos immediately. 'activ' won't compile."
Script:
"Union types let a variable be one of several types. You write them with a pipe character. This is incredibly useful because real-world data is messy — an API might return either a string or a number for an ID field.
Type aliases let you give a name to any type, including unions. The Status example is called a literal type union — you're saying this variable can only ever hold one of those three exact string values. TypeScript will catch any typo immediately."
(~3 minutes)

SLIDE 9: Interface vs Type — When to Use Which
Slide content: Comparison table with three columns — Feature, interface, type:
FeatureinterfacetypeObject shapes & classes✅ Best use✅ Also worksPrimitives & unions❌ Cannot✅ Best useExtendingextends keyword& intersectionRecommendationDefault choice for objectsUse for unions, primitives, complex combinations
Below the table, show both extension syntaxes side by side:
// interface — extending:
interface Animal { name: string }
interface Dog extends Animal { breed: string }

// type — intersection:
type Animal = { name: string }
type Dog = Animal & { breed: string }
Add callout: "Practical rule: use interface for object shapes by default. Reach for type when you need unions, intersections, or are aliasing a primitive."
Script:
"This question comes up constantly: should I use interface or type? Here's the practical breakdown.
Interfaces are for describing the shape of objects and classes. Type aliases are more flexible — they can describe primitives, unions, tuples, and complex combinations.
Both support extension, just with different syntax as shown. My recommendation — and this aligns with the official TypeScript style guide — use interface by default for object shapes. Reach for type when you need unions, intersections, or you're aliasing a primitive. In practice the difference is small, but being consistent matters for readability."
(~3 minutes)

SLIDE 10: Arrays and Tuples
Slide content:
// Arrays — two identical syntaxes:
let names:  string[]      = ["Alice", "Bob"];
let scores: Array<number> = [95, 87, 100];

// Tuples — fixed length, fixed types at each position:
let coordinate: [number, number] = [40.7128, -74.0060];
let entry:      [string, number] = ["Alice", 30];

// ❌ Wrong order in tuple:
let bad: [string, number] = [30, "Alice"];
//        ^ Type 'number' is not assignable to type 'string'.  ts(2322)
Add note: "Most people prefer string[] for simple arrays. Use Array<T> when nesting generics gets hard to read."
Script:
"Arrays in TypeScript are straightforward — two ways to write the same thing. Most people prefer the bracket syntax for simple cases.
Tuples are a different beast. They're arrays with a fixed length and fixed types at each position. The order matters — if you swap the string and the number, TypeScript tells you immediately. Tuples are great for coordinates, key-value pairs, or any time you're returning exactly two or three related values from a function."
(~3 minutes)

SLIDE 11: Enums
Slide content: Two columns — numeric enum on the left, string enum on the right:
// Numeric enum (default)        // String enum (preferred)
enum Direction {                 enum Status {
  Up,    // 0                      Active   = "ACTIVE",
  Down,  // 1                      Inactive = "INACTIVE",
  Left,  // 2                      Pending  = "PENDING"
  Right  // 3                    }
}
let move: Direction = Direction.Up;
Add note: "Enums compile into real JS objects (small runtime cost). For zero runtime overhead, a literal union type — type Status = 'active' | 'inactive' | 'pending' — is equivalent and also valid."
Script:
"Enums let you define a set of named constants, which makes your code much more readable than magic strings or numbers scattered everywhere.
By default TypeScript assigns numeric values starting at zero. But for most real-world usage, string enums are safer and easier to debug because the values are human-readable.
One thing worth knowing: enums compile into actual JavaScript objects. For pure type safety without any runtime overhead, some developers prefer literal union types — which we saw a few slides ago. Both are valid and readable."
(~3 minutes)

SLIDE 12: Type Casting and Type Assertions
Slide content:
// TypeScript knows: getElementById returns HTMLElement | null
// You know: it's specifically an HTMLInputElement

const input = document.getElementById("username") as HTMLInputElement;
input.value;  // ✅ now TypeScript knows .value exists

// Older angle-bracket syntax (avoid in React/JSX projects):
const input2 = <HTMLInputElement>document.getElementById("username");
Add warning callout: "⚠ Assertions don't transform data at runtime — they only tell the compiler what type to assume. If you're wrong, you get runtime errors just like plain JS. Use assertions when you genuinely have info the compiler doesn't — not to silence errors you haven't fixed."
Script:
"Sometimes you know more about a type than TypeScript does. Type assertions — also called type casting — let you tell the compiler: trust me on this one.
The most common place you'll see this is in DOM manipulation. TypeScript knows getElementById returns HTMLElement or null. But you know — from looking at your HTML — that it's specifically an HTMLInputElement. The as keyword lets you assert that.
Critical warning: type assertions don't transform the data at runtime. You're just telling the compiler to treat it differently. If you're wrong, you'll get runtime errors just like in plain JavaScript. Use assertions when you genuinely have information the compiler doesn't — not to silence errors you haven't actually fixed."
(~3 minutes)

SLIDE 13: Functions and Function Types
Slide content:
// Always annotate parameters and return types:
function greet(name: string): string {
  return `Hello, ${name}`;
}

// Optional parameter with ?:
function greet(name: string, greeting?: string): string {
  return `${greeting ?? "Hello"}, ${name}`;
}

// Describing a function's type:
type MathOperation = (a: number, b: number) => number;

const add:      MathOperation = (a, b) => a + b;
const multiply: MathOperation = (a, b) => a * b;
Add callout: "When you define a callback parameter as MathOperation, TypeScript verifies that any function passed in accepts two numbers and returns a number. No surprises."
Script:
"Functions are where type annotations pay off most visibly. Always annotate your parameters and return types.
Optional parameters use ?, just like in interfaces.
You can also describe the type of a function itself — useful when passing functions as arguments. When you define a parameter as callback: MathOperation, TypeScript will verify that any function passed in matches that signature. No surprises at runtime."
(~4 minutes)

SLIDE 14: Type Guards
Slide content:
// 1. typeof — for primitives:
function format(value: string | number): string {
  if (typeof value === "string") {
    return value.toUpperCase(); // TypeScript knows: string here
  }
  return value.toFixed(2);     // TypeScript knows: number here
}

// 2. instanceof — for class instances:
if (error instanceof Error) {
  console.log(error.message);  // .message is safe now
}

// 3. Custom type guard — the 'is' keyword:
function isUser(obj: any): obj is User {
  return typeof obj.name === "string" && typeof obj.age === "number";
}
if (isUser(data)) {
  console.log(data.name); // TypeScript treats data as User here
}
Add callout: "Type guards are essential for safely working with unknown values and API responses — anything where the type isn't guaranteed at the boundary."
Script:
"Type guards narrow a broad type down to something more specific at runtime. You already use them in JavaScript — you just didn't have a name for them.
typeof is the most basic. Inside the if block, TypeScript knows which branch you're in and narrows the type automatically.
instanceof works for class instances.
For more complex scenarios you can write custom type guard functions using the is keyword. When the function returns true, TypeScript treats the variable as that type in that code path. This is essential for safely working with API responses and external data."
(~4 minutes)

SLIDE 15: Generics Basics
Slide content: Show the problem first, then the solution:
// Problem: using any loses type information
function first(arr: any[]): any {
  return arr[0];
}
const n = first([1, 2, 3]);  // n is 'any' — TypeScript lost the type

// Solution: generic — T is a placeholder filled in at call time
function first<T>(arr: T[]): T {
  return arr[0];
}
const n = first([1, 2, 3]);  // T inferred as 'number' — n is number ✅
const s = first(["a", "b"]); // T inferred as 'string' — s is string ✅
Add callouts: "Think of T as a type parameter — just like a function parameter, but for types. TypeScript fills it in automatically from context." And: "You'll see generics everywhere: Array<T>, Promise<T>, and all utility types. Understanding generics is the key to reading them."
Script:
"Generics are one of the concepts that make TypeScript genuinely powerful, and they're not as scary as they look.
Here's the problem they solve. If you write a function that takes any[] and returns any, it works — but you've lost all type information. TypeScript can't tell you what comes out.
Generics fix this by letting the type be a parameter. T is a placeholder that gets filled in when the function is called. When I call first([1, 2, 3]), TypeScript infers T as number and knows the return value is a number. The type flows through without losing information.
You'll see generics everywhere in TypeScript — in built-in types like Array<T> and Promise<T>, and in utility types which we're about to cover."
(~4 minutes)

SLIDE 16A: Utility Types — Partial, Required, Readonly
Slide content: Starting from this interface shown at the top:
interface User { name: string; age: number; email?: string; }
Then three examples:
// Partial<T> — makes every property optional
// Perfect for update functions where you only send changed fields
function updateUser(id: number, changes: Partial<User>) { ... }
updateUser(1, { name: "Alice" }); // ✅ age and email not required

// Required<T> — opposite: makes every property required (even optional ones)
type FullUser = Required<User>;
// { name: string; age: number; email: string } — no more ?

// Readonly<T> — prevents reassignment after creation
const config: Readonly<User> = { name: "system", age: 0 };
config.name = "other"; // ❌ Cannot assign to "name" — it is read-only
Script:
"TypeScript ships with built-in generic types that transform other types — called utility types. These will save you enormous amounts of repetitive work.
Partial makes every property optional. Perfect for update functions where you only send the fields that changed.
Required is the opposite — it makes every property required, including ones marked optional.
Readonly prevents any property from being reassigned after creation. Great for config objects or constants."
(~2 minutes)

SLIDE 16B: Utility Types — Pick, Omit, Record
Slide content: Continuing from the same User interface:
// Pick<T, Keys> — new type with only the specified properties
type UserPreview = Pick<User, "name" | "email">;
// { name: string; email?: string }

// Omit<T, Keys> — new type with everything EXCEPT specified properties
type PublicUser = Omit<User, "age">;
// { name: string; email?: string }

// Record<Keys, Value> — object type with specific key and value types
type ScoreBoard = Record<string, number>;
// { "Alice": 95, "Bob": 87 } — every key string, every value number

// Pair Record with a union to enforce all keys are present:
type StatusLabels = Record<Status, string>;
// Must have a key for every Status value — TypeScript catches missing ones
Add callout: "Record<Status, string> is extremely useful for mapping over known states — TypeScript tells you if you're missing a case."
Script:
"Pick creates a new type with only the properties you specify — useful when you only need a subset.
Omit is the inverse — everything except the properties you name. Very common when you want to exclude sensitive or internal fields before sending data to a client.
Record creates an object type where you specify the key type and value type. Record<string, number> is a lookup map where every key is a string and every value is a number.
The more powerful version pairs Record with a union or enum: Record<Status, string> means your object must have a key for every possible Status value. TypeScript will tell you if you're missing one. You'll use this constantly in real projects."
(~3 minutes)

SLIDE 17: Classes in TypeScript
Slide content:
// Access modifiers + constructor shorthand:
class User {
  constructor(
    public  name:  string,  // public: accessible anywhere (default)
    private age:   number,  // private: only within this class
    protected role: string  // protected: this class + subclasses
  ) {}
}
// Shorthand creates and assigns in one step — no 'this.name = name' needed

// implements enforces interface compliance:
interface Serializable {
  serialize(): string;
}
class User implements Serializable {
  constructor(public name: string) {}
  serialize(): string { return JSON.stringify(this); }
}
// If serialize() is missing, TypeScript errors at compile time
Script:
"TypeScript extends JavaScript classes with access modifiers and type safety.
public is accessible from anywhere — the default. private is only accessible within the class itself. protected is accessible within the class and any subclasses.
TypeScript also has a shorthand: declaring constructor parameters with access modifiers creates and assigns them in one step. No need to write this.name = name manually.
Classes can implement interfaces, which forces them to have specific properties and methods. If a class says it implements an interface but is missing a required method, TypeScript tells you at compile time."
(~4 minutes)

SLIDE 18: Decorators — A Quick Look
Slide content: Show only the @ syntax and where you'll encounter it:
// The @ syntax — you'll see this in Angular and NestJS:

@Component({ selector: 'app-root' })  // Angular
class AppComponent { }

@Injectable()                          // NestJS
class UserService { }

// Decorators are functions that run at class definition time
// and modify or annotate the target class/method.
Add note: "We won't go deep on decorators here — you'll learn them in context when we reach Angular/NestJS. For now: recognize the @ syntax and know they're functions attached to classes."
Script:
"Decorators are a special syntax — the @ symbol — used heavily in frameworks like Angular and NestJS. You'll encounter them when we get to those parts of the course.
For now: recognize the syntax. A decorator is just a function attached to a class or method that runs at definition time and can modify or annotate it.
When we get to Angular, the purpose of each decorator will make much more sense in context."
(~1 minute)

SLIDE 19: Declaration Files (.d.ts) — You Will See This Error
Slide content: Show the error, the fix, and what a .d.ts file actually looks like:
// ❌ Error you'll see when using third-party packages:
Could not find a declaration file for module 'lodash'.
  Try: npm i --save-dev @types/lodash

// ✅ Fix: install the @types package
npm install --save-dev @types/lodash

// What a .d.ts file looks like (type info only — no runtime code):
// lodash/index.d.ts
export function chunk<T>(array: T[], size?: number): T[][];
export function flatten<T>(array: Array<T | T[]>): T[];
Add callout: "Most popular packages either ship their own .d.ts files or have a community @types/ package. The 'Could not find declaration file' error is almost always fixed with: npm install --save-dev @types/package-name."
Script:
"One more thing before we wrap up — and I promise this will save you from a frustrating afternoon.
When you start using third-party npm packages, you'll often see the error on screen: 'Could not find a declaration file for module X.' Declaration files — files ending in .d.ts — are how TypeScript learns about libraries that weren't written in TypeScript. They contain only type information, no runtime code.
Most popular packages either include their own declaration files or have a community-maintained types package you install separately from @types/. You can see what one looks like on screen — just function signatures, no implementation.
So if you hit that error, the fix is almost always: npm install --save-dev @types/the-package-name."
(~2 minutes)

SLIDE 20: Writing Type-Safe TypeScript — Putting It Together
Slide content: A complete example combining an interface, a generic function, a type guard, and a utility type — fully written out:
interface User { id: number; name: string; email?: string; }

// Type guard — safely validate an API response
function isUser(obj: unknown): obj is User {
  return (
    typeof obj === "object" && obj !== null &&
    typeof (obj as any).id === "number" &&
    typeof (obj as any).name === "string"
  );
}

// Generic fetch — returns Partial<User> | null
async function getUser(id: number): Promise<Partial<User> | null> {
  const res  = await fetch(`/api/users/${id}`);
  const data: unknown = await res.json();
  return isUser(data) ? data : null;
}

// Usage:
const user = await getUser(1);
if (user) {
  console.log(user.name); // TypeScript knows name may be undefined (Partial)
}
Script:
"Let's zoom out and look at what type-safe code actually looks like when everything comes together.
A few practical principles to leave you with:
Enable strict mode. strict: true in tsconfig turns on null checks and several other guards that prevent entire categories of bugs.
Never use any as a shortcut. Use unknown instead, then write a type guard to narrow it. The few extra lines are worth it.
Type your function boundaries carefully. Internal variable types can usually be inferred. Parameters, return types, and public interfaces should be explicit.
Use the compiler as a collaborator. When TypeScript gives you an error, don't just add as any to silence it. Read the error — it's usually pointing at a genuine logical problem in your code.
The shift from JavaScript to TypeScript is a shift in mindset as much as syntax. You're moving from 'I hope this is correct' to 'I have proven this is correct.'"
(~4 minutes)