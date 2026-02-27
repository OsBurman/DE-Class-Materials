# Exercise 04 — OOP Part 2: Inheritance & Polymorphism

## Overview

Build a **Shape Calculator** that models different geometric shapes using inheritance and interfaces. You will practice abstract classes, method overriding, polymorphism, and the `instanceof` operator.

---

## Concepts Covered

- Abstract classes and abstract methods
- Inheritance with `extends`
- Method overriding with `@Override`
- The `super` keyword
- Interfaces and `implements`
- Polymorphism (treating subclasses as the parent type)
- The `instanceof` operator and pattern matching (`instanceof Shape s`)
- Final classes and methods

---

## Class Hierarchy

```
          Drawable (interface)
               │
           Shape  (abstract)
          /    |    \
     Circle Rectangle Triangle
```

| Class/Interface | Key members |
|---|---|
| `Drawable` | `void draw()` |
| `Shape` | `String color`, `abstract double area()`, `abstract double perimeter()`, `describe()` |
| `Circle` | `double radius` |
| `Rectangle` | `double width`, `double height` |
| `Triangle` | `double a`, `double b`, `double c` |
| `ShapeCalculator` | `totalArea()`, `largestShape()`, `filterByType()`, `printAll()` |

---

## TODOs

### Shape.java
- [ ] **TODO 1** — Declare `Shape` as `abstract`, add `String color` field and a constructor
- [ ] **TODO 2** — Declare two `abstract` methods: `area()` and `perimeter()`
- [ ] **TODO 3** — Implement `describe()` — prints shape type, color, area, perimeter

### Circle.java / Rectangle.java / Triangle.java
- [ ] **TODO 4** — Extend `Shape`, implement `Drawable`, add own fields, call `super(color)` in constructor
- [ ] **TODO 5** — `@Override area()` and `perimeter()` with correct formulas
- [ ] **TODO 6** — `@Override draw()` from Drawable — print an ASCII art representation
- [ ] **TODO 7** — `@Override toString()` — concise summary string

### ShapeCalculator.java
- [ ] **TODO 8** — Implement `totalArea(Shape[] shapes)` using a polymorphic loop
- [ ] **TODO 9** — Implement `largestShape(Shape[] shapes)` — returns the Shape with max area
- [ ] **TODO 10** — Implement `printAll(Shape[] shapes)` — call `describe()` on each

---

## Running the Program

```bash
cd starter-code/src
javac *.java
java Main
```
