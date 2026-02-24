public class SingletonFactoryDemo {

    // ── Singleton: enum style ────────────────────────────────────────────────
    // Enum-based Singleton: JVM guarantees one instance; serialization & reflection safe
    enum AppConfig {
        INSTANCE;
        final String appName = "MyApp";
        final String version = "1.0.0";

        String getInfo() { return "App: " + appName + " v " + version; }
    }

    // ── Singleton: double-checked locking ────────────────────────────────────
    // volatile prevents CPU instruction reordering during construction
    static class DatabaseConnection {
        private static volatile DatabaseConnection instance;
        private final String url;

        private DatabaseConnection() { url = "jdbc:mysql://localhost/mydb"; }

        static DatabaseConnection getInstance() {
            if (instance == null) {                          // first check — no lock overhead after init
                synchronized (DatabaseConnection.class) {
                    if (instance == null) {                  // second check — inside the lock
                        instance = new DatabaseConnection();
                    }
                }
            }
            return instance;
        }

        void connect() { System.out.println("Connected to: " + url); }
    }

    // ── Factory Method: Shape interface and implementations ──────────────────
    interface Shape { double area(); }

    static class Circle implements Shape {
        private final double radius;
        Circle(double radius) { this.radius = radius; }
        @Override public double area() { return Math.PI * radius * radius; }
    }

    static class Rectangle implements Shape {
        private final double width, height;
        Rectangle(double width, double height) { this.width = width; this.height = height; }
        @Override public double area() { return width * height; }
    }

    static class Triangle implements Shape {
        private final double base, height;
        Triangle(double base, double height) { this.base = base; this.height = height; }
        @Override public double area() { return 0.5 * base * height; }
    }

    // Factory centralises construction — callers never reference concrete classes directly
    static class ShapeFactory {
        static Shape create(String type, double... dims) {
            return switch (type.toLowerCase()) {
                case "circle"    -> new Circle(dims[0]);
                case "rectangle" -> new Rectangle(dims[0], dims[1]);
                case "triangle"  -> new Triangle(dims[0], dims[1]);
                default          -> throw new IllegalArgumentException("Unknown shape: " + type);
            };
        }
    }

    public static void main(String[] args) {

        // ── Enum Singleton ──────────────────────────────────────────────────
        System.out.println("=== Singleton: enum AppConfig ===");
        AppConfig c1 = AppConfig.INSTANCE;
        AppConfig c2 = AppConfig.INSTANCE;
        System.out.println(c1.getInfo());
        System.out.println("c1 == c2: " + (c1 == c2));

        // ── Double-checked locking Singleton ────────────────────────────────
        System.out.println("\n=== Singleton: DatabaseConnection (double-checked locking) ===");
        DatabaseConnection.getInstance().connect();
        DatabaseConnection.getInstance().connect();
        System.out.println("Same instance: " +
                (DatabaseConnection.getInstance() == DatabaseConnection.getInstance()));

        // ── Factory Method ──────────────────────────────────────────────────
        System.out.println("\n=== Factory Method: ShapeFactory ===");
        Shape circle    = ShapeFactory.create("circle",    5.0);
        Shape rectangle = ShapeFactory.create("rectangle", 6.0, 4.0);
        Shape triangle  = ShapeFactory.create("triangle",  6.0, 5.0);

        System.out.printf("Circle area: %.2f%n",    circle.area());
        System.out.printf("Rectangle area: %.2f%n", rectangle.area());
        System.out.printf("Triangle area: %.2f%n",  triangle.area());
    }
}
