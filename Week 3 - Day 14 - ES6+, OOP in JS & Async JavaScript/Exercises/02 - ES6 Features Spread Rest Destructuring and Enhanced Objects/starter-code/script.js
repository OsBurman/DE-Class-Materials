// Exercise 02: ES6 Features — Default Parameters, Spread, Rest, Destructuring, Enhanced Objects

// TODO: Requirement 1 — define greet(name, greeting = "Hello") that logs "[greeting], [name]!"
//       Call it as: greet("World") and greet("Alice", "Hi")

// TODO: Requirement 2 — define sum(...numbers) using a rest parameter that returns
//       the sum of all arguments. Log sum(1, 2, 3) and sum(10, 20, 30, 40)

// TODO: Requirement 3 — declare fruits = ["Apple", "Banana", "Cherry"]
//       Use spread to create moreFruits with all fruits plus "Date" and "Elderberry" appended.
//       Log moreFruits.

const defaults  = { theme: 'light', lang: 'en', fontSize: 14 };
const userPrefs = { theme: 'dark', fontSize: 16 };

// TODO: Requirement 4 — use spread to merge defaults and userPrefs into a `settings` object
//       so that userPrefs values override defaults. Log settings.

// TODO: Requirement 5 — array destructuring: destructure moreFruits into
//       first, second, and ...rest. Log each.

const user = { id: 1, name: 'Alice', role: 'admin', country: 'US' };

// TODO: Requirement 6 — object destructuring: extract name, role, and country from user
//       in a single destructuring statement. Log all three.

const config = { server: { host: 'localhost', port: 3000 }, db: { name: 'mydb' } };

// TODO: Requirement 7 — nested destructuring: extract host and port from config.server
//       in a single destructuring statement. Log: "host: localhost  port: 3000"

// TODO: Requirement 8 — define formatUser({ name, role, country = "Unknown" }) that
//       returns "[name] ([role]) from [country]". Log formatUser(user).

// TODO: Requirement 9 — declare const x = 10, y = 20.
//       Create a `point` object using shorthand property names { x, y } and a
//       shorthand method toString() that returns "(10, 20)".
//       Log point and log point.toString().
