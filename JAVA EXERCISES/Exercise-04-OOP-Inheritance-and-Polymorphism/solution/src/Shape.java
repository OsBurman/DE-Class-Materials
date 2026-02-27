public abstract class Shape {
    private String color;

    public Shape(String color) { this.color = color; }

    public String getColor() { return color; }

    public abstract double area();
    public abstract double perimeter();

    public void describe() {
        System.out.printf("Shape:     %s%n",   getClass().getSimpleName());
        System.out.printf("Color:     %s%n",   color);
        System.out.printf("Area:      %.2f%n", area());
        System.out.printf("Perimeter: %.2f%n", perimeter());
    }
}
