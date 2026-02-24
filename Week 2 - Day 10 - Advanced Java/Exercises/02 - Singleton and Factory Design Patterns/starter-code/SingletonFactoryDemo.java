public class SingletonFactoryDemo {

    // ── Singleton: enum style ────────────────────────────────────────────────
    // TODO: Create an enum AppConfig with a single value INSTANCE.
    //       Give it fields: String appName = "MyApp" and String version = "1.0.0"
    //       Add method getInfo() returning "App: " + appName + " v " + version


    // ── Singleton: double-checked locking ────────────────────────────────────
    // TODO: Create a class DatabaseConnection with:
    //       - private static volatile DatabaseConnection instance
    //       - private String url, set to "jdbc:mysql://localhost/mydb" in the constructor
    //       - public static DatabaseConnection getInstance() using double-checked locking:
    //           if (instance == null) { synchronized(...) { if (instance == null) { instance = new...; } } }
    //       - public void connect() that prints "Connected to: " + url


    // ── Factory Method: Shape ────────────────────────────────────────────────
    // TODO: Create an interface Shape with method double area()

    // TODO: Create class Circle(double radius) implementing Shape
    //       area() = Math.PI * radius * radius

    // TODO: Create class Rectangle(double width, double height) implementing Shape
    //       area() = width * height

    // TODO: Create class Triangle(double base, double height) implementing Shape
    //       area() = 0.5 * base * height

    // TODO: Create class ShapeFactory with static method create(String type, double... dims)
    //       "circle"    → new Circle(dims[0])
    //       "rectangle" → new Rectangle(dims[0], dims[1])
    //       "triangle"  → new Triangle(dims[0], dims[1])
    //       otherwise   → throw new IllegalArgumentException("Unknown shape: " + type)


    public static void main(String[] args) {

        // ── Enum Singleton ──────────────────────────────────────────────────
        System.out.println("=== Singleton: enum AppConfig ===");
        // TODO: Get two references to AppConfig.INSTANCE
        //       Print c1.getInfo() and "c1 == c2: " + (c1 == c2)


        // ── Double-checked locking Singleton ────────────────────────────────
        System.out.println("\n=== Singleton: DatabaseConnection (double-checked locking) ===");
        // TODO: Call DatabaseConnection.getInstance().connect() twice
        //       Print "Same instance: " + (getInstance() == getInstance())


        // ── Factory Method ──────────────────────────────────────────────────
        System.out.println("\n=== Factory Method: ShapeFactory ===");
        // TODO: Create a circle with radius 5, rectangle 6x4, triangle base=6 height=5
        //       Print each area formatted to 2 decimal places
    }
}
