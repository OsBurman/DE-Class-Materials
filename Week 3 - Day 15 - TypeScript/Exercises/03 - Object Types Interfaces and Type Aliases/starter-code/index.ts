// Exercise 03: Object Types, Interfaces, Type Aliases, and Interface vs Type

// ── PART A: Interfaces ────────────────────────────────────────────────────────

// TODO 1: Declare interface Person with:
//         name: string, age: number, optional email?: string
//         and a method signature greet(): string


// TODO 2: Create two Person objects — one with email, one without. Log both.
//         Format: "Person without email: { name: 'Bob', age: 25 }"
//                 "Person with email: { name: 'Alice', age: 30, email: '...' }"


// TODO 3: Declare interface Employee that extends Person, adding:
//         company: string, salary: number
//         Create an Employee object and log it.
//         Format: "Employee: { name: 'Carol', age: 28, company: 'Acme', salary: 75000 }"


// TODO 4: Implement greet() on one of your Person objects that returns
//         "Hello, my name is " + name
//         Call it and log: "greet: Hello, my name is Alice"


// ── PART B: Type Aliases ──────────────────────────────────────────────────────

// TODO 5: Create type alias: type Coordinate = { x: number; y: number }
//         Create two Coordinate objects and log them.
//         Format: "Coordinate A: { x: 1, y: 2 }"  / "Coordinate B: { x: 5, y: 10 }"


// TODO 6: Create intersection type: type Point3D = Coordinate & { z: number }
//         Create a Point3D object and log it.
//         Format: "Point3D: { x: 3, y: 4, z: 7 }"


// ── PART C: Interface vs Type ─────────────────────────────────────────────────

// TODO 7: Declare interface Animal { name: string }
//         Then declare interface Animal AGAIN adding { sound: string }
//         (Declaration merging — TypeScript merges both into one interface)
//         Create an Animal object satisfying both and log it.
//         Format: "Merged Animal: { name: 'Dog', sound: 'Woof' }"


// TODO 8: Add a comment below showing that re-declaring a type alias causes an error:
//         type MyType = { a: string }
//         type MyType = { b: number } // Error: Duplicate identifier 'MyType'


// TODO 9: Add a comment block comparing interface vs type:
//         - interface: use for extendable object shapes, class implements, OOP patterns
//         - type: use for unions, intersections, primitives, tuples, utility compositions
