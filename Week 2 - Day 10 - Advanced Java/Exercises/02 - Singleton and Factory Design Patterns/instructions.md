# Exercise 02: Singleton and Factory Design Patterns

## Objective
Implement a thread-safe Singleton and a Factory Method pattern to control object creation and centralise instantiation logic.

## Background
**Creational design patterns** solve the problem of *how* objects are created. The **Singleton** pattern ensures that a class has exactly one instance and provides a global access point to it — useful for shared resources like a configuration manager or logger. The **Factory Method** pattern defines an interface for creating an object but lets subclasses or a factory class decide which concrete class to instantiate — decoupling the caller from the implementation.

## Requirements

1. **Singleton — enum style** (preferred, thread-safe):
   - Create an `enum AppConfig` with one value `INSTANCE`
   - Give it two fields: `String appName = "MyApp"` and `String version = "1.0.0"`
   - Add a method `getInfo()` that returns `"App: appName v version"`
   - In `main`, get two references `AppConfig c1 = AppConfig.INSTANCE` and `AppConfig c2 = AppConfig.INSTANCE`
   - Print `c1.getInfo()` and `"c1 == c2: " + (c1 == c2)` (should be `true`)

2. **Singleton — double-checked locking** (for comparison / awareness):
   - Create a class `DatabaseConnection` with a `private static volatile DatabaseConnection instance`
   - Implement `public static DatabaseConnection getInstance()` using a double-checked lock (`synchronized (DatabaseConnection.class)` inside the null check)
   - Add a `String url` field set in the private constructor to `"jdbc:mysql://localhost/mydb"`
   - Add a `connect()` method that prints `"Connected to: " + url`
   - In `main`, call `DatabaseConnection.getInstance().connect()` twice and print `"Same instance: " + (DatabaseConnection.getInstance() == DatabaseConnection.getInstance())`

3. **Factory Method**:
   - Create an interface `Shape` with a method `double area()`
   - Create three classes: `Circle(double radius)`, `Rectangle(double width, double height)`, `Triangle(double base, double height)` — each implements `Shape` and computes its area
   - Create a `ShapeFactory` class with a static method `create(String type, double... dims)` that returns the correct `Shape` based on `type` (`"circle"`, `"rectangle"`, `"triangle"`)
   - In `main`, create one of each shape via the factory and print `"Circle area: " + area`, `"Rectangle area: " + area`, `"Triangle area: " + area` (round to 2 decimal places with `String.format("%.2f", ...)`)

## Hints
- The enum Singleton is considered the best Java Singleton — it handles thread safety, serialization, and reflection attacks automatically
- Double-checked locking requires `volatile` on the field to prevent instruction reordering on multi-core processors
- The Factory's `dims` varargs let you pass different numbers of doubles for each shape — `dims[0]` for circle radius, `dims[0]` and `dims[1]` for rectangle/triangle
- Throw an `IllegalArgumentException("Unknown shape: " + type)` for unrecognized types

## Expected Output

```
=== Singleton: enum AppConfig ===
App: MyApp v 1.0.0
c1 == c2: true

=== Singleton: DatabaseConnection (double-checked locking) ===
Connected to: jdbc:mysql://localhost/mydb
Connected to: jdbc:mysql://localhost/mydb
Same instance: true

=== Factory Method: ShapeFactory ===
Circle area: 78.54
Rectangle area: 24.00
Triangle area: 15.00
```
