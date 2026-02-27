class Dog {

    // Instance fields — each object gets its own copy
    String name;
    String breed;
    int age;

    void bark() {
        System.out.println(name + " says: Woof!");
    }

    String describe() {
        // Build and return the description string; caller decides what to do with it
        return name + " is a " + age + "-year-old " + breed + ".";
    }
}

public class Main {

    public static void main(String[] args) {

        // Create three independent Dog objects
        Dog dog1 = new Dog();
        dog1.name  = "Rex";
        dog1.breed = "German Shepherd";
        dog1.age   = 3;

        Dog dog2 = new Dog();
        dog2.name  = "Bella";
        dog2.breed = "Labrador";
        dog2.age   = 2;

        Dog dog3 = new Dog();
        dog3.name  = "Max";
        dog3.breed = "Poodle";
        dog3.age   = 5;

        // Call bark() on each object
        dog1.bark();
        dog2.bark();
        dog3.bark();

        // Call describe() and print each result
        System.out.println(dog1.describe());
        System.out.println(dog2.describe());
        System.out.println(dog3.describe());

        // Array gives us a convenient length count
        Dog[] dogs = {dog1, dog2, dog3};
        System.out.println("Total dogs: " + dogs.length);

        // Mutating one object does NOT affect the others
        dog1.age = 4;
        System.out.println("Rex's age updated to 4.");
        System.out.println(dog1.describe());                    // shows age = 4
        System.out.println("Bella is still " + dog2.age + "."); // dog2 unchanged
    }
}
