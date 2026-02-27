public class Rectangle extends Shape implements Drawable {
    private double width, height;

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

    @Override
    public double area() {
        return width * height;
    }

    @Override
    public double perimeter() {
        return 2 * (width + height);
    }

    @Override
    public void draw() {
        System.out.printf("  [ %.1f x %.1f ]%n", width, height);
    }

    @Override
    public String toString() {
        return String.format("Rectangle[%.1fx%.1f, color=%s]", width, height, getColor());
    }
}
