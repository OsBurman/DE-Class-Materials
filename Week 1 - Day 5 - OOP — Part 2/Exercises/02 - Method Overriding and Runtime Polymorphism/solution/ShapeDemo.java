// Base class — provides default (no-op) implementations overridden by every subclass
class Shape {
    private String color;

    public Shape(String color) {
        this.color = color;
    }

    public String getColor() { return color; }

    // Default implementations — subclasses must override these
    public double area()      { return 0.0; }
    public double perimeter() { return 0.0; }

    public String describe() {
        return String.format("Shape: %s, Area: %.2f, Perimeter: %.2f",
                color, area(), perimeter());
    }
}

// Circle — overrides area and perimeter with circular geometry
class Circle extends Shape {
    private double radius;

    public Circle(String color, double radius) {
        super(color);
        this.radius = radius;
    }

    @Override
    public double area() {
        return Math.PI * radius * radius;
    }

    @Override
    public double perimeter() {
        return 2 * Math.PI * radius;
    }

    @Override
    public String describe() {
        return String.format("Circle %s: radius=%.1f, Area=%.2f, Perimeter=%.2f",
                getColor(), radius, area(), perimeter());
    }
}

// Rectangle — width × height geometry
class Rectangle extends Shape {
    private double width;
    private double height;

    public Rectangle(String color, double width, double height) {
        super(color);
        this.width = width;
        this.height = height;
    }

    @Override
    public double area() {
        return width * height;
    }

    @Override
    public double perimeter() {
        return 2 * (width + height);
    }

    @Override
    public String describe() {
        return String.format("Rectangle %s: %.1fx%.1f, Area=%.2f, Perimeter=%.2f",
                getColor(), width, height, area(), perimeter());
    }
}

// Triangle — Heron's formula for area (works for any triangle given three side lengths)
class Triangle extends Shape {
    private double sideA, sideB, sideC;

    public Triangle(String color, double sideA, double sideB, double sideC) {
        super(color);
        this.sideA = sideA;
        this.sideB = sideB;
        this.sideC = sideC;
    }

    @Override
    public double perimeter() {
        return sideA + sideB + sideC;
    }

    @Override
    public double area() {
        // Heron's formula: s is the semi-perimeter
        double s = perimeter() / 2;
        return Math.sqrt(s * (s - sideA) * (s - sideB) * (s - sideC));
    }

    @Override
    public String describe() {
        return String.format("Triangle %s: sides=%.1f,%.1f,%.1f, Area=%.2f, Perimeter=%.2f",
                getColor(), sideA, sideB, sideC, area(), perimeter());
    }
}

public class ShapeDemo {
    public static void main(String[] args) {
        System.out.println("=== Shape Hierarchy - Runtime Polymorphism ===\n");

        // Shape[] holds different runtime types — Java will dispatch the correct override
        Shape[] shapes = new Shape[] {
            new Circle("red", 5.0),
            new Rectangle("blue", 4.0, 6.0),
            new Triangle("green", 3.0, 4.0, 5.0)
        };

        // Each call to describe() dynamically dispatches to the correct subclass method
        for (Shape s : shapes) {
            System.out.println(s.describe());
        }

        // Find the shape with the largest area
        Shape largest = shapes[0];
        for (int i = 1; i < shapes.length; i++) {
            if (shapes[i].area() > largest.area()) {
                largest = shapes[i];
            }
        }
        System.out.println("\nLargest area: " + largest.describe());
    }
}
