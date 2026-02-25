package com.academy;

/**
 * Day 5 Part 2 — Abstraction (Abstract Classes & Interfaces), Encapsulation, Packages
 *
 * Theme: Shape Calculator with multiple interfaces
 * Run: mvn compile exec:java
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║  Day 5 Part 2 — Abstraction & Interfaces Demo           ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝\n");

        Shape[] shapes = {
            new Circle(5.0),
            new Rectangle(4.0, 6.0),
            new Triangle(3.0, 4.0, 5.0),
            new Square(4.0)
        };

        System.out.println("=== 1. Polymorphism via Abstract Class ===");
        for (Shape shape : shapes) {
            System.out.printf("  %-12s area=%-8.2f perimeter=%.2f%n",
                    shape.getClass().getSimpleName(),
                    shape.area(),
                    shape.perimeter());
        }

        System.out.println("\n=== 2. Interface — Drawable ===");
        for (Shape shape : shapes) {
            shape.draw();  // interface default + overridden methods
        }

        System.out.println("\n=== 3. Interface — Resizable ===");
        // Only Circle and Rectangle implement Resizable
        for (Shape shape : shapes) {
            if (shape instanceof Resizable r) {
                r.resize(2.0);
                System.out.printf("  After resize(2.0): area=%.2f%n", shape.area());
            }
        }

        System.out.println("\n=== 4. Abstract Class vs Interface Summary ===");
        System.out.println("  Abstract Class:");
        System.out.println("    - Can have concrete + abstract methods");
        System.out.println("    - Can have instance fields");
        System.out.println("    - A class can extend only ONE abstract class");
        System.out.println("  Interface:");
        System.out.println("    - All methods implicitly public (default or abstract)");
        System.out.println("    - Can have default methods (Java 8+)");
        System.out.println("    - A class can implement MULTIPLE interfaces");

        System.out.println("\n✓ Abstraction & Interfaces demo complete.");
    }
}

// ─────────────────────────────────────────────────────────────
// Interfaces
// ─────────────────────────────────────────────────────────────
interface Drawable {
    void draw();  // abstract by default

    // Default method — provides a base implementation
    default String getColor() { return "black"; }
}

interface Resizable {
    void resize(double factor);
}

// ─────────────────────────────────────────────────────────────
// Abstract Class — partial implementation
// ─────────────────────────────────────────────────────────────
abstract class Shape implements Drawable {
    // Abstract methods — subclasses MUST implement
    public abstract double area();
    public abstract double perimeter();

    // Concrete method — shared by all shapes
    public String describe() {
        return String.format("%s: area=%.2f, perimeter=%.2f",
                getClass().getSimpleName(), area(), perimeter());
    }
}

// ─────────────────────────────────────────────────────────────
// Concrete classes
// ─────────────────────────────────────────────────────────────
class Circle extends Shape implements Resizable {
    private double radius;

    public Circle(double radius) { this.radius = radius; }

    @Override public double area()      { return Math.PI * radius * radius; }
    @Override public double perimeter() { return 2 * Math.PI * radius; }
    @Override public void   draw()      { System.out.println("  Drawing Circle ○ (radius=" + radius + ")"); }
    @Override public void   resize(double factor) { radius *= factor; }
}

class Rectangle extends Shape implements Resizable {
    protected double width, height;

    public Rectangle(double width, double height) {
        this.width = width; this.height = height;
    }

    @Override public double area()      { return width * height; }
    @Override public double perimeter() { return 2 * (width + height); }
    @Override public void   draw()      { System.out.println("  Drawing Rectangle □ (" + width + "×" + height + ")"); }
    @Override public void   resize(double factor) { width *= factor; height *= factor; }
}

// Square extends Rectangle (is-a relationship)
class Square extends Rectangle {
    public Square(double side) { super(side, side); }

    @Override public void draw() { System.out.println("  Drawing Square ■ (side=" + width + ")"); }
}

class Triangle extends Shape {
    private double a, b, c;  // three sides

    public Triangle(double a, double b, double c) {
        this.a = a; this.b = b; this.c = c;
    }

    @Override
    public double area() {
        double s = (a + b + c) / 2;  // Heron's formula
        return Math.sqrt(s * (s-a) * (s-b) * (s-c));
    }

    @Override public double perimeter() { return a + b + c; }
    @Override public void   draw()      { System.out.println("  Drawing Triangle △ (sides=" + a + "," + b + "," + c + ")"); }
}
