// Exercise 09: Generics for Reusable Functions and Interfaces

// TODO 1: Write generic function identity<T>(value: T): T that returns value.
//         Call with "hello", 42, true — TypeScript infers T each time.
//         Log each: "identity string: hello" / "identity number: 42" / "identity boolean: true"


// TODO 2: Write generic function firstElement<T>(arr: T[]): T | undefined
//         Returns arr[0] or undefined if empty.
//         Test with [10, 20, 30] and []. Log both.
//         Format: "firstElement numbers: 10"  /  "firstElement empty: undefined"


// TODO 3: Write generic function pair<A, B>(first: A, second: B): [A, B]
//         Call with ("Alice", 30) and (true, ["x","y"]). Log both.
//         Format: "pair 1: [ 'Alice', 30 ]"  /  "pair 2: [ true, [ 'x', 'y' ] ]"


// TODO 4: Declare interface Repository<T> with:
//         - findById(id: number): T | undefined
//         - findAll(): T[]
//         - save(item: T): void
//
//         Implement class InMemoryRepository<T extends { id: number }> satisfying Repository<T>.
//         Store items in a private items: T[] = [].
//
//         Test: save two items ({ id: 1, name: "Widget" }, { id: 2, name: "Gadget" })
//         Log findAll() and findById(1).


// TODO 5: Write generic function filterItems<T>(arr: T[], predicate: (item: T) => boolean): T[]
//         Test 1: filter ["apple","fig","banana","cherry"] — keep length > 4
//         Test 2: filter [1,2,3,4,5,6] — keep even numbers
//         Log both results.


// TODO 6: Write constrained generic:
//         function getProperty<T, K extends keyof T>(obj: T, key: K): T[K]
//         Call with { name: "Bob", age: 25 }, key "name" and key "age".
//         Log both: "getProperty name: Bob"  /  "getProperty age: 25"
