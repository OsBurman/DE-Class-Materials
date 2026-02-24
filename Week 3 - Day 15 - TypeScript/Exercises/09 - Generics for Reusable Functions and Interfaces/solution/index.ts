// Exercise 09: Generics for Reusable Functions and Interfaces — SOLUTION

// 1. Identity — T is inferred from the argument at each call site
function identity<T>(value: T): T {
  return value;
}
console.log("identity string:", identity("hello"));
console.log("identity number:", identity(42));
console.log("identity boolean:", identity(true));

// 2. First element — returns T | undefined (safe for empty arrays)
function firstElement<T>(arr: T[]): T | undefined {
  return arr[0];
}
console.log("firstElement numbers:", firstElement([10, 20, 30]));
console.log("firstElement empty:", firstElement([]));

// 3. Multiple type parameters — each resolved independently
function pair<A, B>(first: A, second: B): [A, B] {
  return [first, second];
}
console.log("pair 1:", pair("Alice", 30));
console.log("pair 2:", pair(true, ["x", "y"]));

// 4. Generic interface + constrained generic class
interface Repository<T> {
  findById(id: number): T | undefined;
  findAll(): T[];
  save(item: T): void;
}

// T extends { id: number } — ensures every item has an id for lookup
class InMemoryRepository<T extends { id: number }> implements Repository<T> {
  private items: T[] = [];

  findById(id: number): T | undefined {
    return this.items.find((item) => item.id === id);
  }

  findAll(): T[] {
    return [...this.items];
  }

  save(item: T): void {
    this.items.push(item);
  }
}

type Product = { id: number; name: string };
const repo = new InMemoryRepository<Product>();
repo.save({ id: 1, name: "Widget" });
repo.save({ id: 2, name: "Gadget" });
console.log("findAll:", repo.findAll());
console.log("findById(1):", repo.findById(1));

// 5. Generic filter — works for any array type
function filterItems<T>(arr: T[], predicate: (item: T) => boolean): T[] {
  return arr.filter(predicate);
}
console.log("filterStrings length>4:", filterItems(["apple", "fig", "banana", "cherry"], (s) => s.length > 4));
console.log("filterNumbers even:", filterItems([1, 2, 3, 4, 5, 6], (n) => n % 2 === 0));

// 6. keyof constraint — K must be one of T's own keys; return type is T[K]
function getProperty<T, K extends keyof T>(obj: T, key: K): T[K] {
  return obj[key];
}
const person = { name: "Bob", age: 25 };
console.log("getProperty name:", getProperty(person, "name"));
console.log("getProperty age:", getProperty(person, "age"));
