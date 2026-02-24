# Exercise 06: The `final` Keyword and `this`

## Objective
Use the `this` keyword to resolve naming conflicts between fields and parameters, and apply `final` to fields, variables, and constants to prevent reassignment.

---

## Background
- **`this`** refers to the current object instance. It is most commonly used to disambiguate between a field and a parameter that share the same name: `this.speed = speed`.
- **`final` field**: must be assigned exactly once (in the declaration or constructor) and cannot be changed afterwards.
- **`final` local variable**: assigned once and never reassigned inside the method.
- **`static final`**: a compile-time constant — by convention named in `UPPER_SNAKE_CASE`.

---

## Requirements

1. Create a class `Circle` with:
   - A `public static final double PI = 3.14159` constant.
   - A `private final double radius` field — set once in the constructor, never changed.
   - A `private String color` field — mutable.

2. Write a constructor `Circle(double radius, String color)` that uses `this.radius = radius` and `this.color = color` to assign both fields.

3. Write a **copy constructor** `Circle(Circle other)` that creates a new `Circle` with the same `radius` and `color` as `other`. Use `this(other.radius, other.color)` to chain to the main constructor.

4. Write instance methods:
   - `double area()` — returns `PI * radius * radius`
   - `double circumference()` — returns `2 * PI * radius`
   - `void setColor(String color)` — uses `this.color = color`
   - `String getColor()` — returns `color`
   - `double getRadius()` — returns `radius`

5. Override `toString()` returning `"Circle{radius=[r], color='[c]', area=[a]}"` with area formatted to 2 decimal places.

6. In `main`:
   - Print the constant: `"PI constant: " + Circle.PI`.
   - Create `c1 = new Circle(5.0, "red")` and print it.
   - Create `c2` using the copy constructor from `c1`, then change its color to `"blue"`. Print both to show they are independent.
   - Declare a `final double scale = 2.0` local variable. Use it to print `"Scaled radius: " + (c1.getRadius() * scale)`.
   - Try to explain (in a comment) why you cannot do `c1.radius = 10.0` — attempting it would be a compile error.

---

## Hints
- `this.field = parameter` inside the constructor makes both sides unambiguous even when they share the same name.
- A `final` field after assignment is read-only — attempting to reassign it causes a **compile-time** error, not a runtime error.
- `static final` constants are accessed via the class name: `Circle.PI`.
- The copy constructor chains with `this(other.radius, other.color)` so there is no code duplication.

---

## Expected Output
```
PI constant: 3.14159
Circle{radius=5.0, color='red', area=78.54}
Circle{radius=5.0, color='red', area=78.54}
Circle{radius=5.0, color='blue', area=78.54}
Scaled radius: 10.0
```
