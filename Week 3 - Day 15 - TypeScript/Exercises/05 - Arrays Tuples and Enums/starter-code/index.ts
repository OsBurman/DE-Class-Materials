// Exercise 05: Arrays, Tuples, and Enums

// ── PART A: Typed Arrays ──────────────────────────────────────────────────────

// TODO 1: Declare a string[] of three fruit names. Log the array and its length.
//         Format: "Fruits: ['apple','banana','cherry'] length: 3"


// TODO 2: Declare number[] scores = [85, 92, 78, 95, 88].
//         Log the highest score using Math.max(...scores).
//         Format: "Highest score: 95"


// TODO 3: Declare Array<boolean> flags. Push true, false, true. Log flags.
//         Format: "Flags: [ true, false, true ]"


// TODO 4: Try pushing a number into flags (flags.push(1)).
//         Comment it out and add a note about the TypeScript error.


// ── PART B: Tuples ────────────────────────────────────────────────────────────

// TODO 5: Declare type UserRecord = [string, number, boolean]
//         Create two UserRecord instances. Log each.
//         Format: "UserRecord 1: [ 'Alice', 30, true ]"
//                 "UserRecord 2: [ 'Bob', 25, false ]"


// TODO 6: Destructure a UserRecord into variables: name, age, isActive.
//         Log each individually.
//         Format: "name: Alice  age: 30  isActive: true"


// TODO 7: Declare type RGBColor = [number, number, number, number?]
//         Create one without alpha, one with alpha. Log both.
//         Format: "RGB no alpha: [ 255, 128, 0 ]"
//                 "RGB with alpha: [ 255, 128, 0, 0.5 ]"


// ── PART C: Enums ─────────────────────────────────────────────────────────────

// TODO 8: Declare numeric enum Direction { North, South, East, West }
//         Log Direction.North (0) and Direction[2] ("East").


// TODO 9: Declare string enum LogLevel { Info = "INFO", Warn = "WARN", Error = "ERROR" }
//         Write function log(level: LogLevel, message: string): void
//         that logs "[<LEVEL>] <message>"
//         Call with all three levels.


// TODO 10: Declare const enum Season { Spring, Summer, Autumn, Winter }
//          Write function describeSeason(s: Season): string using a switch.
//          Return descriptions for each. Call and log all four.
