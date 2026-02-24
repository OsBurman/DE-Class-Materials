// Exercise 04: Union Types, Literal Types, and Type Guards — SOLUTION

// ── REQUIREMENT 1: Union type + typeof guard ──────────────────────────────────

type StringOrNumber = string | number;

function formatValue(val: StringOrNumber): string {
  if (typeof val === "string") {
    return "String: " + val.toUpperCase();
  }
  // TypeScript narrows val to number here
  return "Number: " + val.toFixed(2);
}

console.log('formatValue("hello"):', formatValue("hello"));
console.log("formatValue(42.5):", formatValue(42.5));

// ── REQUIREMENT 2: Literal type + exhaustive switch ───────────────────────────

type Status = "pending" | "active" | "inactive";

function describeStatus(s: Status): string {
  switch (s) {
    case "pending":  return "pending → Waiting for approval";
    case "active":   return "active → Currently active";
    case "inactive": return "inactive → Account deactivated";
  }
}

console.log(describeStatus("pending"));
console.log(describeStatus("active"));
console.log(describeStatus("inactive"));

// ── REQUIREMENT 3: Discriminated union ───────────────────────────────────────

// The `kind` property is the discriminant — TypeScript uses it to narrow the union
interface Circle     { kind: "circle";    radius: number }
interface Rectangle  { kind: "rectangle"; width: number; height: number }
type Shape = Circle | Rectangle;

function area(shape: Shape): number {
  switch (shape.kind) {
    case "circle":    return Math.PI * shape.radius ** 2;
    case "rectangle": return shape.width * shape.height;
  }
}

const circle: Circle    = { kind: "circle", radius: 5 };
const rect: Rectangle   = { kind: "rectangle", width: 4, height: 6 };

console.log(`Circle area (r=5): ${area(circle).toFixed(2)}`);
console.log(`Rectangle area (4×6): ${area(rect)}`);

// ── REQUIREMENT 4: Custom type guard with `in` operator ───────────────────────

interface Cat2 { meow(): void }
interface Dog2 { bark(): void }

// Return type `animal is Cat2` tells TypeScript this function narrows the type
function isCat(animal: Cat2 | Dog2): animal is Cat2 {
  return "meow" in animal;
}

function makeSound(animal: Cat2 | Dog2): void {
  if (isCat(animal)) {
    animal.meow(); // TypeScript knows this is Cat2
  } else {
    animal.bark(); // TypeScript knows this is Dog2
  }
}

const kitty: Cat2 = { meow() { console.log("Cat says: meow!"); } };
const fido: Dog2  = { bark() { console.log("Dog says: woof!"); } };

makeSound(kitty);
makeSound(fido);

// ── REQUIREMENT 5: Nullish coalescing + optional chaining ─────────────────────

let val: string | null | undefined = null;
// ?. safely accesses .length (returns undefined if val is null/undefined)
// ?? falls back to "no value" if the left side is null or undefined
console.log("val is null/undefined:", val?.length ?? "no value");
