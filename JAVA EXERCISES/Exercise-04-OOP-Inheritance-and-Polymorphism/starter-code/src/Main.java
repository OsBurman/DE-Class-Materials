public class Main {
    public static void main(String[] args) {
        Shape[] shapes = {
                new Circle(5.0, "Red"),
                new Rectangle(4.0, 6.0, "Blue"),
                new Triangle(3.0, 4.0, 5.0, "Green"),
                new Circle(2.5, "Yellow"),
                new Rectangle(8.0, 3.0, "Purple")
        };

        System.out.println("=== Shape Gallery ===");
        ShapeCalculator.printAll(shapes);

        System.out.printf("%nTotal area of all shapes: %.2f%n", ShapeCalculator.totalArea(shapes));

        Shape largest = ShapeCalculator.largestShape(shapes);
        System.out.println("Largest shape: " + largest);

        System.out.println("\n=== Drawing Shapes ===");
        for (Shape s : shapes) {
            if (s instanceof Drawable d) {
                d.draw();
            }
        }
    }
}
