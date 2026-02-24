// Exercise 02 Solution: ES6 Features

// Requirement 1: Default parameter
function greet(name, greeting = 'Hello') {
  console.log(`${greeting}, ${name}!`);
}
greet('World');       // Hello, World!
greet('Alice', 'Hi'); // Hi, Alice!

// Requirement 2: Rest parameter — collects all arguments into an array
function sum(...numbers) {
  return numbers.reduce((total, n) => total + n, 0);
}
console.log(sum(1, 2, 3));        // 6
console.log(sum(10, 20, 30, 40)); // 100

// Requirement 3: Spread to build a new array without mutating the original
const fruits = ['Apple', 'Banana', 'Cherry'];
const moreFruits = [...fruits, 'Date', 'Elderberry'];
console.log(moreFruits); // [ 'Apple', 'Banana', 'Cherry', 'Date', 'Elderberry' ]

// Requirement 4: Object spread — later properties overwrite earlier ones
const defaults  = { theme: 'light', lang: 'en', fontSize: 14 };
const userPrefs = { theme: 'dark', fontSize: 16 };
const settings  = { ...defaults, ...userPrefs }; // userPrefs overrides matching keys
console.log(settings); // { theme: 'dark', lang: 'en', fontSize: 16 }

// Requirement 5: Array destructuring with rest
const [first, second, ...rest] = moreFruits;
console.log(first);  // Apple
console.log(second); // Banana
console.log(rest);   // [ 'Cherry', 'Date', 'Elderberry' ]

// Requirement 6: Object destructuring
const user = { id: 1, name: 'Alice', role: 'admin', country: 'US' };
const { name, role, country } = user;
console.log(name);    // Alice
console.log(role);    // admin
console.log(country); // US

// Requirement 7: Nested destructuring — reach inside config.server in one statement
const config = { server: { host: 'localhost', port: 3000 }, db: { name: 'mydb' } };
const { server: { host, port } } = config;
console.log(`host: ${host}  port: ${port}`); // host: localhost  port: 3000

// Requirement 8: Destructuring in function parameters with a default value
function formatUser({ name, role, country = 'Unknown' }) {
  return `${name} (${role}) from ${country}`;
}
console.log(formatUser(user)); // Alice (admin) from US

// Requirement 9: Enhanced object literals — shorthand properties and methods
const x = 10, y = 20;
const point = {
  x,          // shorthand for x: x
  y,          // shorthand for y: y
  toString() { return `(${this.x}, ${this.y})`; } // method shorthand
};
console.log(point);            // { x: 10, y: 20, toString: [Function: toString] }
console.log(point.toString()); // (10, 20)
