// Exercise 02: Special Types — any, unknown, never, and void — SOLUTION

// ── 1. any ────────────────────────────────────────────────────────────────────

// `any` turns off all type checking for this variable
let flexible: any = "hello";
console.log("flexible as string:", flexible);

flexible = 42;
console.log("flexible as number:", flexible);

flexible = true;
console.log("flexible as boolean:", flexible);

// ── 2. unknown ────────────────────────────────────────────────────────────────

// `unknown` requires a type check before use — safer than `any`
let mystery: unknown = "42";

// mystery.toUpperCase(); // Error: Object is of type 'unknown'

if (typeof mystery === "string") {
  // Inside this block TypeScript knows mystery is a string
  console.log("mystery uppercased:", mystery.toUpperCase());
}

// ── 3. void ───────────────────────────────────────────────────────────────────

// void = function does not return a meaningful value
function logMessage(msg: string): void {
  console.log("logMessage:", msg);
}
logMessage("TypeScript is great");

// ── 4. never ─────────────────────────────────────────────────────────────────

// never = this function never completes normally (always throws or infinite loops)
function throwError(message: string): never {
  throw new Error(message);
}

try {
  throwError("Something went wrong");
} catch (err) {
  if (err instanceof Error) {
    console.log("Caught:", err.message);
  }
}

// ── 5. Exhaustive switch with never ──────────────────────────────────────────

type Direction = "north" | "south" | "east" | "west";

// assertNever is called when a value that should be `never` reaches the default branch
function assertNever(x: never): never {
  throw new Error("Unexpected value: " + x);
}

function describeDirection(d: Direction): string {
  switch (d) {
    case "north": return "north → Go north";
    case "south": return "south → Go south";
    case "east":  return "east → Go east";
    case "west":  return "west → Go west";
    default:      return assertNever(d); // TypeScript confirms `d` is `never` here
  }
}

const directions: Direction[] = ["north", "south", "east", "west"];
directions.forEach((dir) => console.log(describeDirection(dir)));
