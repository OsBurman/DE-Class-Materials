// Exercise 07: Type Casting and Type Assertions

// ── REQUIREMENT 1: Basic `as` assertion ──────────────────────────────────────

const rawInput: unknown = "TypeScript";
// TODO: Assert rawInput as string, call .toUpperCase(), log the result.
//       Format: "Uppercased: TYPESCRIPT"


// ── REQUIREMENT 2: DOM-style casting ─────────────────────────────────────────

// Simulated element (in a browser you'd use document.getElementById("btn") as HTMLButtonElement)
const elem = { id: "btn", disabled: false } as unknown as HTMLButtonElement;
// TODO: Access elem.disabled and log it.
//       Format: "Button disabled: false"


// ── REQUIREMENT 3: Double assertion ──────────────────────────────────────────

const numericId: number = 123;
// TODO: Use `numericId as unknown as string` to assert to string.
//       Log the result: "Double asserted: 123"
//       Add a comment warning that double assertion bypasses type safety.


// ── REQUIREMENT 4: as const ───────────────────────────────────────────────────

const ROLES = ["admin", "user", "moderator"] as const;
// TODO: Log ROLES[0].  Format: "ROLES[0]: admin"
// TODO: Comment out ROLES.push("guest") and note the error TypeScript shows.
// TODO: Add a comment showing the inferred union type:
//       type RoleType = typeof ROLES[number]  →  "admin" | "user" | "moderator"


// ── REQUIREMENT 5: satisfies operator ────────────────────────────────────────

type Palette = { [key: string]: [number, number, number] };
// TODO: Create `colors` using `satisfies Palette` (see instructions for the object).
//       Log colors.red[0].  Format: "colors.red[0]: 255"


// ── REQUIREMENT 6: Type assertion from JSON parse ────────────────────────────

// TODO: Write function parseJSON(json: string): unknown that returns JSON.parse(json).
// TODO: Call it with '{"name":"Alice","age":30}'.
//       Assert the result as { name: string; age: number }.
//       Log: "Parsed name: Alice  age: 30"
