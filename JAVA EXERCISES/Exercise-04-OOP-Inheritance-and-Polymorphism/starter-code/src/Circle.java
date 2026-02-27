/**
 * Exercise 04 — OOP Part 2 (STARTER)
 * Circle shape.
 */
// TODO 4: Extend Shape and implement Drawable
public class Circle extends Shape {

    private double radius;

    public Circle(double radius, String color) {
        super(color); // calls Shape(color) — add that constructor in Shape first!
        this.radius = radius;
    }

    public double getRadius() {
        return radius;
    }

    // TODO 5a: Override area() → π * r² (use Math.PI)
    @Override
    public double area() {
        return 0.0; // your code here
    }

    // TODO 5b: Override perimeter() → 2 * π * r
    @Override
    public double perimeter() {
        return 0.0; // your code here
    }

    // TODO 6: Override draw() from Drawable
    // Print a simple circle: " ( o ) radius=5.0"
    @Override
    public void draw() {
        // your code here
    }

    // TODO 7: Override toString()
    // "Circle[radius=5.0, color=Red]"
    @Override
    public String toString() {
        return "Circle(not implemented)";
    }
}
