class Vehicle {

    // TODO: Declare a PRIVATE STATIC int field 'count' initialized to 0
    //       This field is shared by ALL Vehicle objects

    // TODO: Declare three private INSTANCE fields: String make, String model, int year

    // TODO: Write a PUBLIC parameterized constructor Vehicle(String make, String model, int year)
    //       Assign the three instance fields
    //       Increment 'count' by 1 (this.count++ or just count++)

    // TODO: Write PUBLIC STATIC method  int getCount()
    //       Return the value of count

    // TODO: Write PUBLIC STATIC method  String classify(int year)
    //       Return "Classic"  if year < 1980
    //       Return "Vintage"  if year < 2000
    //       Return "Modern"   otherwise

    // TODO: Write PUBLIC INSTANCE method  String describe()
    //       Return "[year] [make] [model] ([classify(year)])"
    //       Call classify(this.year) to get the category string
}

public class VehicleDemo {

    public static void main(String[] args) {

        // TODO: Print "Vehicles created: " + Vehicle.getCount()  (expect 0)

        // TODO: Create v1: make="Ford",   model="Mustang",  year=1965 — then print count
        // TODO: Create v2: make="Toyota", model="Camry",    year=1998 — then print count
        // TODO: Create v3: make="Honda",  model="Civic",    year=2015 — then print count
        // TODO: Create v4: make="Tesla",  model="Model 3",  year=2023 — then print count

        // TODO: Print describe() for v1, v2, v3, v4

        // TODO: Call Vehicle.classify(1965), Vehicle.classify(1995), Vehicle.classify(2021)
        //       Print each result in the format "[year] → [category]"
    }
}
