// Exercise 05: Arrays, Tuples, and Enums — SOLUTION

// ── PART A: Typed Arrays ──────────────────────────────────────────────────────

const fruits: string[] = ["apple", "banana", "cherry"];
console.log("Fruits:", fruits, "length:", fruits.length);

const scores: number[] = [85, 92, 78, 95, 88];
console.log("Highest score:", Math.max(...scores));

const flags: Array<boolean> = [];
flags.push(true, false, true);
console.log("Flags:", flags);

// flags.push(1); // Error: Argument of type 'number' is not assignable to parameter of type 'boolean'.

// ── PART B: Tuples ────────────────────────────────────────────────────────────

// Tuple: fixed-length, each index has its own type
type UserRecord = [string, number, boolean];

const user1: UserRecord = ["Alice", 30, true];
const user2: UserRecord = ["Bob",   25, false];
console.log("UserRecord 1:", user1);
console.log("UserRecord 2:", user2);

// Destructuring a tuple
const [name, age, isActive] = user1;
console.log(`name: ${name}  age: ${age}  isActive: ${isActive}`);

// Optional last element with ?
type RGBColor = [number, number, number, number?];

const noAlpha: RGBColor   = [255, 128, 0];
const withAlpha: RGBColor = [255, 128, 0, 0.5];
console.log("RGB no alpha:", noAlpha);
console.log("RGB with alpha:", withAlpha);

// ── PART C: Enums ─────────────────────────────────────────────────────────────

// Numeric enum — members get auto-incremented values (0, 1, 2, 3)
enum Direction {
  North,  // 0
  South,  // 1
  East,   // 2
  West    // 3
}

console.log("Direction.North:", Direction.North);  // 0
console.log("Direction[2]:", Direction[2]);         // "East" — reverse mapping

// String enum — no reverse mapping, but values are readable in logs
enum LogLevel {
  Info  = "INFO",
  Warn  = "WARN",
  Error = "ERROR"
}

function logMsg(level: LogLevel, message: string): void {
  console.log(`[${level}] ${message}`);
}

logMsg(LogLevel.Info,  "Server started");
logMsg(LogLevel.Warn,  "Disk space low");
logMsg(LogLevel.Error, "Database connection failed");

// const enum — members are inlined at compile time (no runtime object created)
const enum Season { Spring, Summer, Autumn, Winter }

function describeSeason(s: Season): string {
  switch (s) {
    case Season.Spring: return "Spring: flowers blooming";
    case Season.Summer: return "Summer: hot and sunny";
    case Season.Autumn: return "Autumn: leaves falling";
    case Season.Winter: return "Winter: cold and snowy";
  }
}

console.log(describeSeason(Season.Spring));
console.log(describeSeason(Season.Summer));
console.log(describeSeason(Season.Autumn));
console.log(describeSeason(Season.Winter));
