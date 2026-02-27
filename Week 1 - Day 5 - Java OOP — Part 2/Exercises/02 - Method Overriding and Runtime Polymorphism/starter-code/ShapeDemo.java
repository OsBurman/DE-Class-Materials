// Base Shape class
class Shape {
    // TODO: Declare a private field String color

    // TODO: Write a constructor that takes color and assigns it

    // TODO: Write a getter getColor()

    // TODO: Write method double area() that returns 0.0

    // TODO: Write method double perimeter() that returns 0.0

    // TODO: Write method String describe() that returns:
    //       "Shape: [color], Area: [area], Perimeter: [perimeter]"
    //       Format area and perimeter to 2 decimal places: String.format("%.2f", area())
}

// Circle subclass
class Circle extends Shape {
    // TODO: Declare private field double radius

    // TODO: Constructor takes color and radius; call super(color) first

    // TODO: Override area() — return Math.PI * radius * radius

    // TODO: Override perimeter() — return 2 * Math.PI * radius

    // TODO: Override describe() — return:
    //       "Circle [color]: radius=[radius], Area=[area], Perimeter=[perimeter]"
    //       (area and perimeter formatted to 2 decimal places)
}

// Rectangle subclass
class Rectangle extends Shape {
    // TODO: Declare private fields double width, double height

    // TODO: Constructor takes color, width, height; call super(color) first

    // TODO: Override area() — return width * height

    // TODO: Override perimeter() — return 2 * (width + height)

    // TODO: Override describe() — return:
    //       "Rectangle [color]: [width]x[height], Area=[area], Perimeter=[perimeter]"
}

// Triangle subclass
class Triangle extends Shape {
    // TODO: Declare private fields double sideA, double sideB, double sideC

    // TODO: Constructor takes color, sideA, sideB, sideC; call super(color) first

    // TODO: Override perimeter() — return sideA + sideB + sideC

    // TODO: Override area() using Heron's formula:
    //       double s = perimeter() / 2;
    //       return Math.sqrt(s * (s - sideA) * (s - sideB) * (s - sideC));

    // TODO: Override describe() — return:
    //       "Triangle [color]: sides=[a],[b],[c], Area=[area], Perimeter=[perimeter]"
}

public class ShapeDemo {
    public static void main(String[] args) {
        System.out.println("=== Shape Hierarchy - Runtime Polymorphism ===\n");

        // TODO: Create a Shape array with 3 elements:
        //       - Circle, color "red", radius 5.0
        //       - Rectangle, color "blue", width 4.0, height 6.0
        //       - Triangle, color "green", sides 3.0, 4.0, 5.0
        Shape[] shapes = new Shape[3];
        // shapes[0] = ...
        // shapes[1] = ...
        // shapes[2] = ...

        // TODO: Loop through the shapes array and print shape.describe() for each

        // TODO: Find the shape with the largest area:
        //       - Initialize a variable 'largest' to shapes[0]
        //       - Loop through the array; if the current shape's area() > largest.area(), update largest
        //       - After the loop, print "Largest area: " + largest.describe()
    }
}
