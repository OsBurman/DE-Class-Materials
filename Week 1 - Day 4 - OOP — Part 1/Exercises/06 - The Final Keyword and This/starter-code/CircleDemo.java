class Circle {

    // TODO: Declare a PUBLIC STATIC FINAL double constant named PI = 3.14159

    // TODO: Declare a PRIVATE FINAL double field 'radius'  (assigned once in constructor)
    // TODO: Declare a PRIVATE String field 'color'          (mutable)

    // TODO: Write the main constructor  Circle(double radius, String color)
    //       Use 'this.radius = radius' and 'this.color = color'
    //       (both parameter names match field names, so 'this.' is required)

    // TODO: Write a COPY constructor  Circle(Circle other)
    //       Chain to the main constructor using: this(other.radius, other.color)
    //       Body should be empty (all work is done by the chained call)

    // TODO: Write  double area()            → return PI * radius * radius
    // TODO: Write  double circumference()   → return 2 * PI * radius
    // TODO: Write  void setColor(String color)  → use 'this.color = color'
    // TODO: Write  String getColor()        → return color
    // TODO: Write  double getRadius()       → return radius

    // TODO: Override toString()
    //       Return "Circle{radius=[radius], color='[color]', area=[area formatted to 2dp]}"
    //       Use String.format("%.2f", area()) for the area part
}

public class CircleDemo {

    public static void main(String[] args) {

        // TODO: Print "PI constant: " + Circle.PI

        // TODO: Create c1 = new Circle(5.0, "red")
        //       Print c1

        // TODO: Create c2 using the copy constructor from c1
        //       Print c2  (should match c1)
        //       Change c2's color to "blue" using setColor()
        //       Print c1 again (should still say red — objects are independent)
        //       Print c2 (should now say blue)

        // TODO: Declare  final double scale = 2.0
        //       Print "Scaled radius: " + (c1.getRadius() * scale)

        // TODO: Add a comment here explaining why  c1.radius = 10.0  would not compile
    }
}
