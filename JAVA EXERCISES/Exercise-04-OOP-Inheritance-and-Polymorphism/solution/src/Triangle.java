public class Triangle extends Shape implements Drawable {
    private double a, b, c;

    public Triangle(double a, double b, double c, String color) {
        super(color);
        this.a = a;
        this.b = b;
        this.c = c;
    }

    @Override
    public double area() {
        double s = (a + b + c) / 2;
        return Math.sqrt(s * (s - a) * (s - b) * (s - c));
    }

    @Override
    public double perimeter() {
        return a + b + c;
    }

    @Override
    public void draw() {
        System.out.printf("   /\\   a=%.1f b=%.1f c=%.1f%n", a, b, c);
    }

    @Override
    public String toString() {
        return String.format("Triangle[%.1f, %.1f, %.1f, color=%s]", a, b, c, getColor());
    }
}
