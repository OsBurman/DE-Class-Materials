/**
 * DAY 5 — OOP Part 2 | Part 1, File 1
 * TOPIC: Inheritance
 *
 * Topics covered:
 *  - extends keyword: declaring an inheritance relationship
 *  - What is inherited (fields, methods) and what is not (constructors, private members)
 *  - super() — calling the parent constructor
 *  - super.method() — calling a parent method from the child
 *  - Inheritance hierarchy (multi-level: grandparent → parent → child)
 *  - The Object class: root of every Java class
 *  - Method inheritance and specialization
 *
 * Key vocabulary:
 *  - superclass  : the parent class (also called "base class")
 *  - subclass    : the child class that extends the superclass (also "derived class")
 *  - super       : keyword to refer to the immediate parent class
 *  - IS-A        : the fundamental relationship inheritance represents
 */
public class Inheritance {

    // ==========================================================
    // SECTION A: Base class — Vehicle
    // ==========================================================
    // Every vehicle has some common properties and behaviors.
    // We model those once here.

    static class Vehicle {

        // Fields common to ALL vehicles
        private String make;
        private String model;
        private int    year;
        protected double fuelLevel;   // protected: subclasses can read/write directly

        // public constructor — subclasses will call this via super()
        public Vehicle(String make, String model, int year) {
            this.make      = make;
            this.model     = model;
            this.year      = year;
            this.fuelLevel = 100.0;
            System.out.println("  [Vehicle constructor] Created: " + year + " " + make + " " + model);
        }

        // Public getters (private fields still require getters in subclasses)
        public String getMake()  { return make; }
        public String getModel() { return model; }
        public int    getYear()  { return year; }

        // Behavior available to all vehicles
        public void refuel(double amount) {
            fuelLevel = Math.min(100.0, fuelLevel + amount);
            System.out.printf("  Refuelled %s %s. Fuel level: %.0f%%%n", make, model, fuelLevel);
        }

        public void displayInfo() {
            System.out.printf("  Vehicle: %d %s %s | Fuel: %.0f%%%n", year, make, model, fuelLevel);
        }

        @Override
        public String toString() {
            return String.format("%d %s %s (fuel=%.0f%%)", year, make, model, fuelLevel);
        }
    }

    // ==========================================================
    // SECTION B: First-level subclass — Car extends Vehicle
    // ==========================================================
    // A Car IS-A Vehicle. It inherits everything from Vehicle
    // and adds car-specific fields and behavior.

    static class Car extends Vehicle {

        // Fields specific to Car — not shared with other Vehicle subtypes
        private int    numDoors;
        private String fuelType;     // "Gasoline", "Diesel", "Hybrid"

        // Constructor must call super() FIRST to initialize the Vehicle part
        public Car(String make, String model, int year, int numDoors, String fuelType) {
            super(make, model, year);   // ← calls Vehicle(String, String, int)
            this.numDoors = numDoors;
            this.fuelType = fuelType;
            System.out.println("  [Car constructor]     Added: " + numDoors + " doors, " + fuelType);
        }

        public int    getNumDoors() { return numDoors; }
        public String getFuelType() { return fuelType; }

        // Car-specific behavior
        public void honk() {
            System.out.println("  " + getMake() + " " + getModel() + ": Beep beep! 🚗");
        }

        // Specialized version of displayInfo — adds car-specific detail
        @Override
        public void displayInfo() {
            super.displayInfo();   // ← calls Vehicle.displayInfo() first
            System.out.printf("  ↳ Car details: %d doors | %s%n", numDoors, fuelType);
        }

        @Override
        public String toString() {
            return super.toString() + String.format(" [Car: %d-door, %s]", numDoors, fuelType);
        }
    }

    // ==========================================================
    // SECTION C: Another first-level subclass — Truck extends Vehicle
    // ==========================================================

    static class Truck extends Vehicle {

        private double payloadCapacityTons;
        private boolean hasTowHitch;

        public Truck(String make, String model, int year,
                     double payloadCapacityTons, boolean hasTowHitch) {
            super(make, model, year);
            this.payloadCapacityTons = payloadCapacityTons;
            this.hasTowHitch         = hasTowHitch;
            System.out.printf("  [Truck constructor]   Added: %.1f ton payload, towHitch=%b%n",
                    payloadCapacityTons, hasTowHitch);
        }

        public void loadCargo(double tons) {
            if (tons > payloadCapacityTons) {
                System.out.printf("  [WARNING] %s %s: %.1f tons exceeds %.1f ton capacity!%n",
                        getMake(), getModel(), tons, payloadCapacityTons);
            } else {
                System.out.printf("  %s %s loaded with %.1f tons. ✅%n",
                        getMake(), getModel(), tons);
            }
        }

        @Override
        public void displayInfo() {
            super.displayInfo();
            System.out.printf("  ↳ Truck details: %.1f ton payload | tow hitch: %b%n",
                    payloadCapacityTons, hasTowHitch);
        }

        @Override
        public String toString() {
            return super.toString() + String.format(" [Truck: %.1ft payload]", payloadCapacityTons);
        }
    }

    // ==========================================================
    // SECTION D: Second-level subclass — ElectricCar extends Car
    // ==========================================================
    // Multi-level inheritance: ElectricCar IS-A Car, and Car IS-A Vehicle.
    // The chain: Vehicle → Car → ElectricCar

    static class ElectricCar extends Car {

        private double batteryCapacityKwh;
        private int    rangeKm;

        public ElectricCar(String make, String model, int year,
                           double batteryCapacityKwh, int rangeKm) {
            // Calls Car(String, String, int, int, String)
            super(make, model, year, 4, "Electric");
            this.batteryCapacityKwh = batteryCapacityKwh;
            this.rangeKm            = rangeKm;
            System.out.printf("  [ElectricCar ctor]    Added: %.1f kWh, %d km range%n",
                    batteryCapacityKwh, rangeKm);
        }

        public void chargeBattery() {
            fuelLevel = 100.0;   // protected field from Vehicle — accessible here
            System.out.printf("  %s %s: Battery fully charged! ⚡%n", getMake(), getModel());
        }

        @Override
        public void displayInfo() {
            super.displayInfo();   // calls Car.displayInfo() → which calls Vehicle.displayInfo()
            System.out.printf("  ↳ EV details: %.1f kWh battery | %d km range%n",
                    batteryCapacityKwh, rangeKm);
        }

        @Override
        public String toString() {
            return super.toString() + String.format(" [EV: %.1f kWh, %d km]",
                    batteryCapacityKwh, rangeKm);
        }
    }

    // ==========================================================
    // SECTION E: The Object class
    // ==========================================================
    // Every class in Java implicitly extends Object.
    // Object defines: toString(), equals(), hashCode(), getClass(), etc.
    // When you write a class with no 'extends', Java inserts 'extends Object' silently.

    static class SimpleBox {
        private String contents;

        public SimpleBox(String contents) { this.contents = contents; }

        // Without overriding toString(), printing this object shows something like:
        //   Inheritance$SimpleBox@6d06d69c  (class name + memory hash)

        // Overriding toString() makes it useful:
        @Override
        public String toString() { return "Box{" + contents + "}"; }

        // Override equals() to compare by content, not reference identity:
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;                      // same reference
            if (!(obj instanceof SimpleBox)) return false;     // wrong type
            SimpleBox other = (SimpleBox) obj;
            return this.contents.equals(other.contents);
        }

        @Override
        public int hashCode() { return contents.hashCode(); }
    }

    // ==========================================================
    // MAIN — DEMONSTRATIONS
    // ==========================================================
    public static void main(String[] args) {

        System.out.println("============================================================");
        System.out.println("SECTION 1 — extends and super(): building the hierarchy");
        System.out.println("============================================================");

        System.out.println("\nCreating a Car:");
        Car sedan = new Car("Toyota", "Camry", 2022, 4, "Gasoline");
        System.out.println();

        System.out.println("Creating a Truck:");
        Truck pickup = new Truck("Ford", "F-150", 2023, 1.5, true);
        System.out.println();

        System.out.println("Creating an ElectricCar (3-level chain):");
        ElectricCar ev = new ElectricCar("Tesla", "Model 3", 2024, 75.0, 500);
        System.out.println();

        System.out.println("============================================================");
        System.out.println("SECTION 2 — Accessing inherited methods and fields");
        System.out.println("============================================================");

        // Car inherits refuel() and displayInfo() from Vehicle
        sedan.refuel(20.0);            // Vehicle method — works on Car
        pickup.refuel(50.0);           // Vehicle method — works on Truck

        System.out.println();
        sedan.honk();                  // Car-only method
        sedan.displayInfo();           // Overridden Car version calls super.displayInfo() first
        System.out.println();
        pickup.loadCargo(1.2);
        pickup.loadCargo(2.5);         // over capacity warning
        pickup.displayInfo();
        System.out.println();

        ev.chargeBattery();            // ElectricCar method
        ev.displayInfo();              // 3-level chain: ElectricCar → Car → Vehicle
        System.out.println();

        System.out.println("============================================================");
        System.out.println("SECTION 3 — super.method(): calling a parent's version");
        System.out.println("============================================================");

        System.out.println("Notice displayInfo() output for ElectricCar:");
        System.out.println("  Line 1 comes from Vehicle.displayInfo()");
        System.out.println("  Line 2 comes from Car.displayInfo() adding car details");
        System.out.println("  Line 3 comes from ElectricCar.displayInfo() adding EV details");
        System.out.println("Each level calls super.displayInfo() before adding its own output.");
        System.out.println();

        System.out.println("============================================================");
        System.out.println("SECTION 4 — toString() chain via super.toString()");
        System.out.println("============================================================");

        System.out.println("sedan:  " + sedan);
        System.out.println("pickup: " + pickup);
        System.out.println("ev:     " + ev);
        System.out.println();
        System.out.println("ev.toString() calls Car.toString() which calls Vehicle.toString()");
        System.out.println("Each adds its own detail to the string built by its parent.");
        System.out.println();

        System.out.println("============================================================");
        System.out.println("SECTION 5 — The Object class: root of everything");
        System.out.println("============================================================");

        SimpleBox box1 = new SimpleBox("Chocolates");
        SimpleBox box2 = new SimpleBox("Chocolates");
        SimpleBox box3 = new SimpleBox("Books");

        System.out.println("box1: " + box1);          // calls overridden toString()
        System.out.println("box2: " + box2);
        System.out.println();

        // == checks reference (same object in memory?)
        System.out.println("box1 == box2 (reference) : " + (box1 == box2));        // false
        // .equals() checks content (because we overrode it)
        System.out.println("box1.equals(box2) (content): " + box1.equals(box2));   // true
        System.out.println("box1.equals(box3) (content): " + box1.equals(box3));   // false
        System.out.println();

        // getClass() — inherited from Object, cannot be overridden
        System.out.println("sedan.getClass().getName() : " + sedan.getClass().getName());
        System.out.println("ev.getClass().getName()    : " + ev.getClass().getName());
        System.out.println("ev.getClass().getSuperclass().getName() : "
                + ev.getClass().getSuperclass().getName());

        System.out.println();
        System.out.println("============================================================");
        System.out.println("INHERITANCE SUMMARY");
        System.out.println("============================================================");
        System.out.println("  extends    → declares IS-A relationship");
        System.out.println("  super()    → calls parent constructor (must be line 1)");
        System.out.println("  super.x()  → calls parent version of method x()");
        System.out.println("  Subclass inherits: public + protected fields and methods");
        System.out.println("  Subclass does NOT inherit: constructors, private members");
        System.out.println("  All classes ultimately extend Object");
    }
}
