// Exercise 04: Union Types, Literal Types, and Type Guards

// ── REQUIREMENT 1: Union type + typeof guard ──────────────────────────────────

// TODO: Define type StringOrNumber = string | number
// TODO: Write function formatValue(val: StringOrNumber): string
//       Use typeof to check: if string → "String: " + val.toUpperCase()
//                            if number → "Number: " + val.toFixed(2)
// TODO: Log formatValue("hello") and formatValue(42.5)


// ── REQUIREMENT 2: Literal type + exhaustive switch ───────────────────────────

// TODO: Define type Status = "pending" | "active" | "inactive"
// TODO: Write function describeStatus(s: Status): string
//       "pending"  → "pending → Waiting for approval"
//       "active"   → "active → Currently active"
//       "inactive" → "inactive → Account deactivated"
// TODO: Call and log all three.


// ── REQUIREMENT 3: Discriminated union ───────────────────────────────────────

// TODO: Define interface Circle  { kind: "circle";    radius: number }
// TODO: Define interface Rectangle { kind: "rectangle"; width: number; height: number }
// TODO: Define type Shape = Circle | Rectangle
// TODO: Write function area(shape: Shape): number using switch on shape.kind
//       Circle area: Math.PI * radius * radius
//       Rectangle area: width * height
// TODO: Log area of circle r=5 and rectangle 4×6
//       Format: "Circle area (r=5): 78.54"
//               "Rectangle area (4×6): 24"


// ── REQUIREMENT 4: Custom type guard with `in` operator ───────────────────────

// TODO: Define interface Cat { meow(): void }
// TODO: Define interface Dog { bark(): void }
// TODO: Write function isCat(animal: Cat | Dog): animal is Cat
//       Use the `in` operator: return "meow" in animal
// TODO: Write function makeSound(animal: Cat | Dog): void
//       Use isCat() guard to call meow() or bark()
// TODO: Create a Cat object { meow() { console.log("Cat says: meow!") } }
//       Create a Dog object { bark() { console.log("Dog says: woof!") } }
//       Call makeSound on both.


// ── REQUIREMENT 5: Nullish coalescing + optional chaining ─────────────────────

// TODO: Declare let val: string | null | undefined; assign null.
//       Use val?.length ?? "no value" to log:
//       "val is null/undefined: no value"
