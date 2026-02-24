// Exercise 07: Type Casting and Type Assertions — SOLUTION

// ── REQUIREMENT 1: Basic `as` assertion ──────────────────────────────────────

const rawInput: unknown = "TypeScript";
// Asserting `unknown` as `string` — safe because we know the value
const upperInput = (rawInput as string).toUpperCase();
console.log("Uppercased:", upperInput);

// ── REQUIREMENT 2: DOM-style casting ─────────────────────────────────────────

// In a browser: document.getElementById("btn") as HTMLButtonElement
const elem = { id: "btn", disabled: false } as unknown as HTMLButtonElement;
console.log("Button disabled:", elem.disabled);

// ── REQUIREMENT 3: Double assertion ──────────────────────────────────────────

const numericId: number = 123;
// Double assertion: first assert to `unknown`, then to the target type.
// WARNING: This bypasses TypeScript's type safety entirely.
// Only use this when you are absolutely certain of the runtime value.
const idAsString = numericId as unknown as string;
console.log("Double asserted:", idAsString);

// ── REQUIREMENT 4: as const ───────────────────────────────────────────────────

// `as const` makes the array readonly and infers literal types for each element
const ROLES = ["admin", "user", "moderator"] as const;
console.log("ROLES[0]:", ROLES[0]);

// ROLES.push("guest"); // Error: Property 'push' does not exist on type 'readonly [...]'

// The inferred element union type:
// type RoleType = typeof ROLES[number];  →  "admin" | "user" | "moderator"

// ── REQUIREMENT 5: satisfies operator ────────────────────────────────────────

type Palette = { [key: string]: [number, number, number] };

// `satisfies` validates against Palette BUT keeps the tuple inferred type per key.
// Without satisfies, colors.red would be typed as number[] (widened).
const colors = {
  red:   [255, 0, 0],
  green: [0, 255, 0],
} satisfies Palette;

// TypeScript knows colors.red is [number, number, number] — tuple access is safe
console.log("colors.red[0]:", colors.red[0]);

// ── REQUIREMENT 6: Type assertion from JSON parse ────────────────────────────

function parseJSON(json: string): unknown {
  return JSON.parse(json);
}

const data = parseJSON('{"name":"Alice","age":30}') as { name: string; age: number };
console.log(`Parsed name: ${data.name}  age: ${data.age}`);
