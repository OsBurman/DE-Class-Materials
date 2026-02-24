// Exercise 10: Utility Types and tsconfig.json Compiler Configuration — SOLUTION

// Base interface
interface User {
  id: number;
  name: string;
  email: string;
  age: number;
  isAdmin: boolean;
}

// ── PART A: Utility Types ─────────────────────────────────────────────────────

// 1. Partial<T> — makes all properties optional
const updatePayload: Partial<User> = { name: "Alice Updated", age: 31 };
console.log("Partial update:", updatePayload);

// 2. Required<T> — makes ALL properties required (even optional ones)
type AllRequired = Required<Partial<User>>;
const fullUser: AllRequired = { id: 1, name: "Alice", email: "alice@example.com", age: 30, isAdmin: false };
console.log("Required user:", fullUser);

// 3. Readonly<T> — prevents reassignment of any property after creation
const frozenUser: Readonly<User> = { id: 1, name: "Alice", email: "alice@example.com", age: 30, isAdmin: false };
console.log("Readonly user name:", frozenUser.name);
// frozenUser.name = "X"; // Error: Cannot assign to 'name' because it is a read-only property.

// 4. Pick<T, Keys> — keep only the listed keys
type UserSummary = Pick<User, "id" | "name" | "email">;
const summary: UserSummary = { id: 1, name: "Alice", email: "alice@example.com" };
console.log("UserSummary:", summary);

// 5. Omit<T, Keys> — remove the listed keys
type PublicProfile = Omit<User, "isAdmin" | "age">;
const profile: PublicProfile = { id: 1, name: "Alice", email: "alice@example.com" };
console.log("PublicProfile:", profile);

// 6. Record<K, V> — object type with keys of type K and values of type V
const scores: Record<string, number> = {};
scores["Alice"] = 95;
scores["Bob"]   = 87;
scores["Carol"] = 92;
console.log("Scores:", scores);

// 7. ReturnType<typeof fn> — extracts the return type of a function without writing it manually
function getConfig() {
  return { host: "localhost", port: 3000 };
}

// AppConfig is inferred as { host: string; port: number }
type AppConfig = ReturnType<typeof getConfig>;
const config: AppConfig = getConfig();
console.log("Config:", config);
