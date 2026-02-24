class Rectangle {

    double width;
    double height;

    // Default constructor — delegates to the two-arg constructor
    Rectangle() {
        this(1.0, 1.0);                         // constructor chaining — must be first statement
    }

    // Single-arg constructor creates a square — delegates to two-arg
    Rectangle(double side) {
        this(side, side);                        // only this call; no duplication of assignment logic
    }

    // Two-arg constructor — the canonical initializer
    Rectangle(double width, double height) {
        this.width  = width;                    // 'this.width' = field; 'width' = parameter
        this.height = height;
    }

    double area() {
        return width * height;
    }

    double perimeter() {
        return 2 * (width + height);
    }

    String describe() {
        return String.format(
            "Rectangle [width=%.1f, height=%.1f, area=%.1f, perimeter=%.1f]",
            width, height, area(), perimeter()
        );
    }
}

public class RectangleDemo {

    public static void main(String[] args) {

        Rectangle r1 = new Rectangle();            // default → 1×1
        Rectangle r2 = new Rectangle(5.0);         // square  → 5×5
        Rectangle r3 = new Rectangle(4.0, 6.0);
        Rectangle r4 = new Rectangle(3.0, 8.0);

        System.out.println(r1.describe());
        System.out.println(r2.describe());
        System.out.println(r3.describe());
        System.out.println(r4.describe());
    }
}
