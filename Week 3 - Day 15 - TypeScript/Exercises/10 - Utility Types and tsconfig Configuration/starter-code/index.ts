// Exercise 10: Utility Types and tsconfig.json Compiler Configuration

// Base type used throughout this exercise
interface User {
  id: number;
  name: string;
  email: string;
  age: number;
  isAdmin: boolean;
}

// ── PART A: Utility Types ─────────────────────────────────────────────────────

// TODO 1: Use Partial<User> to type a variable `updatePayload` with only name and age.
//         Log it: "Partial update: { name: 'Alice Updated', age: 31 }"


// TODO 2: Declare type AllRequired = Required<Partial<User>>
//         Create a fully-populated variable of that type and log it.
//         Format: "Required user: { id: 1, name: 'Alice', email: 'alice@example.com', age: 30, isAdmin: false }"


// TODO 3: Use Readonly<User> to create `frozenUser`.
//         Assign a full User object. Log `frozenUser.name`.
//         Comment out: frozenUser.name = "X";
//         Add a note about the error TypeScript would show.


// TODO 4: Use Pick<User, "id" | "name" | "email"> to create type UserSummary.
//         Create a variable and log it.
//         Format: "UserSummary: { id: 1, name: 'Alice', email: 'alice@example.com' }"


// TODO 5: Use Omit<User, "isAdmin" | "age"> to create type PublicProfile.
//         Create a variable and log it.
//         Format: "PublicProfile: { id: 1, name: 'Alice', email: 'alice@example.com' }"


// TODO 6: Declare const scores: Record<string, number> = {}
//         Add Alice: 95, Bob: 87, Carol: 92. Log scores.
//         Format: "Scores: { Alice: 95, Bob: 87, Carol: 92 }"


// TODO 7: Write function getConfig() that returns { host: "localhost", port: 3000 }.
//         Declare: type AppConfig = ReturnType<typeof getConfig>
//         Declare a variable of type AppConfig and log it.
//         Format: "Config: { host: 'localhost', port: 3000 }"
