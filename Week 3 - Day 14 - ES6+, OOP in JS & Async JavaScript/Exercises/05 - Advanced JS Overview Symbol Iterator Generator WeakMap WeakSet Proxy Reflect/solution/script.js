// Exercise 05: Advanced JS Overview — SOLUTION

// ── 1. SYMBOL ─────────────────────────────────────────────────────────────────

const sym1 = Symbol("id");
const sym2 = Symbol("id");
console.log("Two Symbols equal:", sym1 === sym2);          // false
console.log("Symbol id description:", sym1.description);   // id

const obj = { [sym1]: "secret value" };
console.log("Symbol key property:", obj[sym1]);             // secret value

// ── 2. ITERATOR PROTOCOL ──────────────────────────────────────────────────────

const counter = {
  [Symbol.iterator]() {
    let current = 1;
    const last = 5;
    return {
      next() {
        if (current <= last) {
          return { value: current++, done: false };
        }
        return { value: undefined, done: true };
      }
    };
  }
};

const values = [];
for (const n of counter) {
  values.push(n);
}
console.log("Counter via iterator:", values.join(" "));    // 1 2 3 4 5

// ── 3. GENERATOR FUNCTION ─────────────────────────────────────────────────────

function* fibonacci() {
  let [a, b] = [0, 1];
  for (let i = 0; i < 7; i++) {
    yield a;
    [a, b] = [b, a + b];
  }
}

const fibs = [...fibonacci()];
console.log("First 7 Fibonacci:", fibs);                   // [0,1,1,2,3,5,8]

// ── 4. WEAKMAP ────────────────────────────────────────────────────────────────

const cache = new WeakMap();
let objA = {};
cache.set(objA, "cached data");
console.log("WeakMap has objA:", cache.has(objA));          // true
objA = null;
console.log("WeakMap has null:", cache.has(null));          // false

// ── 5. WEAKSET ────────────────────────────────────────────────────────────────

const visited = new WeakSet();
let page = { url: "/home" };
visited.add(page);
console.log("WeakSet has page:", visited.has(page));        // true
page = null;
console.log("WeakSet has null:", visited.has(null));        // false

// ── 6. PROXY ─────────────────────────────────────────────────────────────────

const person = { name: "Alice", age: 30 };

const handler = {
  get(target, key) {
    console.log(`Getting property: ${key}`);
    return Reflect.get(target, key);
  }
};

const proxy = new Proxy(person, handler);
console.log("Proxy name:", proxy.name);                     // Getting property: name → Alice
console.log("Proxy age:", proxy.age);                       // Getting property: age  → 30

// ── 7. REFLECT ────────────────────────────────────────────────────────────────

console.log("Reflect.ownKeys:", Reflect.ownKeys({ a: 1, b: 2 }));  // ['a','b']
console.log("Reflect.has x:", Reflect.has({ x: 5 }, "x"));         // true
