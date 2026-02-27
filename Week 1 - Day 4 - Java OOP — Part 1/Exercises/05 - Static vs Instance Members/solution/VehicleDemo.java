class Vehicle {

    // Static field — shared across ALL instances; incremented by each constructor call
    private static int count = 0;

    // Instance fields — each object gets its own copy
    private String make;
    private String model;
    private int    year;

    public Vehicle(String make, String model, int year) {
        this.make  = make;
        this.model = model;
        this.year  = year;
        count++;                                    // increment class-level counter
    }

    // Static method — accessed via class name; cannot reference instance fields
    public static int getCount() {
        return count;
    }

    // Static utility — pure function, no object state needed
    public static String classify(int year) {
        if      (year < 1980) return "Classic";
        else if (year < 2000) return "Vintage";
        else                  return "Modern";
    }

    // Instance method — can access both instance fields AND static members
    public String describe() {
        return year + " " + make + " " + model + " (" + classify(year) + ")";
    }
}

public class VehicleDemo {

    public static void main(String[] args) {

        // Before any object is created, count is 0
        System.out.println("Vehicles created: " + Vehicle.getCount());

        Vehicle v1 = new Vehicle("Ford",   "Mustang",  1965);
        System.out.println("Vehicles created: " + Vehicle.getCount()); // 1

        Vehicle v2 = new Vehicle("Toyota", "Camry",    1998);
        System.out.println("Vehicles created: " + Vehicle.getCount()); // 2

        Vehicle v3 = new Vehicle("Honda",  "Civic",    2015);
        System.out.println("Vehicles created: " + Vehicle.getCount()); // 3

        Vehicle v4 = new Vehicle("Tesla",  "Model 3",  2023);
        System.out.println("Vehicles created: " + Vehicle.getCount()); // 4

        // Instance method calls
        System.out.println(v1.describe());
        System.out.println(v2.describe());
        System.out.println(v3.describe());
        System.out.println(v4.describe());

        // Static method called directly on the class — no object required
        System.out.println("1965 → " + Vehicle.classify(1965));
        System.out.println("1995 → " + Vehicle.classify(1995));
        System.out.println("2021 → " + Vehicle.classify(2021));
    }
}
