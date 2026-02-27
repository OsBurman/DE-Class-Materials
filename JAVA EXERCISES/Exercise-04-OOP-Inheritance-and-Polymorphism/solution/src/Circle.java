public class Circle extends Shape implements Drawable {
    private double radius;
    public Circle(double radius, String color) { super(color); this.radius = radius; }
    public double getRadius() { return radius; }
    @Override public double area()      { return Math.PI * radius * radius; }
    @Override public double perimeter() { return 2 * Math.PI * radius; }
    @Override public void draw() { System.out.printf("  ( o )  radius=%.1f%n", radius); }
    @Override public String toString() { return String.format("Circle[radius=%.1f, color=%s]", radius, getColor()); }
}
