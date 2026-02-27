public class ShapeCalculator {
    public static double totalArea(Shape[] shapes) {
        double total = 0;
        for (Shape s : shapes)
            total += s.area();
        return total;
    }

    public static Shape largestShape(Shape[] shapes) {
        Shape best = null;
        for (Shape s : shapes)
            if (best == null || s.area() > best.area())
                best = s;
        return best;
    }

    public static void printAll(Shape[] shapes) {
        for (Shape s : shapes) {
            System.out.println("-".repeat(30));
            s.describe();
        }
        System.out.println("-".repeat(30));
    }
}
