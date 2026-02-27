/**
 * Exercise 04 — OOP Part 2 (STARTER)
 * Rectangle shape.
 */
// TODO 4: Extend Shape and implement Drawable
public class Rectangle extends Shape {

    private double width;
    private double height;

    public Rectangle(double width, double height, String color) {
        super(color);
        this.width = width;
        this.height = height;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    // TODO 5a: area() → width * height
    @Override
    public double area() {
        return 0.0;
    }

    // TODO 5b: perimeter() → 2 * (width + height)
    @Override
    public double perimeter() {
        return 0.0;
    }

    // TODO 6: draw() — print "[ width x height ]"
    @Override
    public void draw() {
    }

    // TODO 7: toString() — "Rectangle[5.0x3.0, color=Blue]"
    @Override
    public String toString() {
        return "Rectangle(not implemented)";
    }
}
