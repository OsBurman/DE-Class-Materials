# Exercise 02: Method Overriding and Runtime Polymorphism

## Objective
Practice overriding methods in subclasses and observe how Java's runtime polymorphism dispatches the correct method based on the actual object type, not the reference type.

## Background
A graphics application needs to calculate and display the area and perimeter of different shapes. All shapes share a common interface, but each shape calculates these values differently. You'll build a shape hierarchy where the correct calculation is automatically chosen at runtime based on the actual shape type.

## Requirements

1. Create a base class `Shape` with:
   - A private field `String color`
   - A constructor that takes and assigns `color`
   - A getter `getColor()`
   - A method `double area()` that returns `0.0`
   - A method `double perimeter()` that returns `0.0`
   - A method `String describe()` that returns `"Shape: [color], Area: [area], Perimeter: [perimeter]"` — format area and perimeter to 2 decimal places using `String.format("%.2f", value)`

2. Create class `Circle` extending `Shape`:
   - Additional field: `double radius`
   - Constructor takes `color` and `radius`, calls `super(color)`
   - Override `area()`: return `Math.PI * radius * radius`
   - Override `perimeter()`: return `2 * Math.PI * radius`
   - Override `describe()`: return `"Circle [color]: radius=[radius], Area=[area], Perimeter=[perimeter]"` (area/perimeter to 2 dp)

3. Create class `Rectangle` extending `Shape`:
   - Additional fields: `double width`, `double height`
   - Constructor takes `color`, `width`, `height`
   - Override `area()`: return `width * height`
   - Override `perimeter()`: return `2 * (width + height)`
   - Override `describe()`: return `"Rectangle [color]: [width]x[height], Area=[area], Perimeter=[perimeter]"`

4. Create class `Triangle` extending `Shape`:
   - Additional fields: `double sideA`, `double sideB`, `double sideC`
   - Constructor takes `color`, `sideA`, `sideB`, `sideC`
   - Override `perimeter()`: return sum of all three sides
   - Override `area()`: use Heron's formula: `s = perimeter/2`, `area = Math.sqrt(s*(s-a)*(s-b)*(s-c))`
   - Override `describe()`: return `"Triangle [color]: sides=[a],[b],[c], Area=[area], Perimeter=[perimeter]"`

5. In `main`:
   - Create a `Shape[]` array containing one `Circle` (red, radius 5.0), one `Rectangle` (blue, 4.0×6.0), and one `Triangle` (green, sides 3.0, 4.0, 5.0)
   - Loop through the array and call `describe()` on each element
   - Demonstrate that calling `area()` on a `Shape` reference calls the correct subclass implementation
   - Find and print the shape with the largest area from the array

## Hints
- The `@Override` annotation is optional but strongly recommended — it tells the compiler you intend to override and will catch typos in method names
- When you call `shape.describe()` where `shape` is declared as type `Shape`, Java looks at the **actual runtime type** of the object to decide which `describe()` to call — this is dynamic dispatch
- For Heron's formula: compute `s` (semi-perimeter) first, then use `Math.sqrt(...)`
- To find the largest area, initialize a variable to the first element and loop through the rest

## Expected Output

```
=== Shape Hierarchy - Runtime Polymorphism ===

Circle red: radius=5.0, Area=78.54, Perimeter=31.42
Rectangle blue: 4.0x6.0, Area=24.00, Perimeter=20.00
Triangle green: sides=3.0,4.0,5.0, Area=6.00, Perimeter=12.00

Largest area: Circle red: radius=5.0, Area=78.54, Perimeter=31.42
```
