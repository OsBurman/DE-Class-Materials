# Exercise 02: Constructor Types and Chaining

## Objective
Practice writing default constructors, parameterized constructors, and constructor chaining using `this()`.

---

## Background
A **constructor** is a special method that runs automatically when an object is created with `new`. Java provides a no-arg **default constructor** if you write none, but as soon as you write any constructor you must explicitly write a no-arg one if you still need it. **Constructor chaining** with `this(...)` lets one constructor delegate to another, eliminating duplicated initialization code.

---

## Requirements

1. Create a class `Rectangle` with two `double` fields: `width` and `height`.

2. Write **three** constructors:
   - **Default** (no arguments): sets both `width` and `height` to `1.0`.
   - **Single-argument** (`double side`): creates a square by setting both dimensions to `side`. **Must call the two-argument constructor** using `this(side, side)`.
   - **Two-argument** (`double width, double height`): sets the fields to the provided values.

3. Write an instance method `double area()` that returns `width * height`.

4. Write an instance method `double perimeter()` that returns `2 * (width + height)`.

5. Write an instance method `String describe()` that returns:
   `"Rectangle [width=X.X, height=X.X, area=X.X, perimeter=X.X]"`

6. In `main`, create four `Rectangle` objects:
   - `r1` — using the default constructor
   - `r2` — using the single-argument constructor with `5.0`
   - `r3` — using the two-argument constructor with `4.0` and `6.0`
   - `r4` — using the two-argument constructor with `3.0` and `8.0`
   - Print `describe()` for each.

---

## Hints
- `this(side, side)` must be the **first statement** in the constructor body — the compiler enforces this.
- The single-argument constructor should contain **only** the `this(...)` call — no other initialization.
- For `describe()`, use `String.format("%.1f", value)` to format decimals to one place, or simple concatenation is fine.
- Constructor chaining prevents code duplication: the two-argument constructor is the only place the fields are actually assigned.

---

## Expected Output
```
Rectangle [width=1.0, height=1.0, area=1.0, perimeter=4.0]
Rectangle [width=5.0, height=5.0, area=25.0, perimeter=20.0]
Rectangle [width=4.0, height=6.0, area=24.0, perimeter=20.0]
Rectangle [width=3.0, height=8.0, area=24.0, perimeter=22.0]
```
