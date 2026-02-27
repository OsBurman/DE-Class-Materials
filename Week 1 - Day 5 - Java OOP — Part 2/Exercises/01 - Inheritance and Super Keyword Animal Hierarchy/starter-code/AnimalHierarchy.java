// Animal base class
class Animal {
    // TODO: Declare private fields: String name, int age
    
    // TODO: Write a constructor that takes name and age and assigns them to the fields

    // TODO: Write a getter for name: getName()

    // TODO: Write a getter for age: getAge()

    // TODO: Write a method String describe() that returns "[name] (age [age])"
    //       Example: "Leo (age 5)"

    // TODO: Write a method String sound() that returns "..."
}

// Dog subclass
class Dog extends Animal {
    // TODO: Declare a private field: String breed

    // TODO: Write a constructor that takes name, age, and breed.
    //       Call super(name, age) as the first line, then assign breed.

    // TODO: Write a getter getBreed()

    // TODO: Override sound() to return "Woof!"

    // TODO: Override describe() to return "[name] the [breed] (age [age])"
    //       Tip: you can use getters from the parent class or call super.describe()
    //       to include the parent portion and build on it
}

// Cat subclass
class Cat extends Animal {
    // TODO: Declare a private field: boolean isIndoor

    // TODO: Write a constructor that takes name, age, and isIndoor.
    //       Call super(name, age) as the first line.

    // TODO: Write a getter isIndoor() that returns the boolean field

    // TODO: Override sound() to return "Meow!"

    // TODO: Override describe() to return "[name] (age [age]) - Indoor cat"
    //       OR "[name] (age [age]) - Outdoor cat" depending on isIndoor
}

// Bird subclass
class Bird extends Animal {
    // TODO: Declare a private field: String species

    // TODO: Write a constructor that takes name, age, and species.
    //       Call super(name, age) as the first line.

    // TODO: Override sound() to return "Tweet!"

    // TODO: Override describe() to return "[name] the [species] (age [age])"
}

public class AnimalHierarchy {
    public static void main(String[] args) {
        System.out.println("=== Animal Hierarchy Demo ===");

        // TODO: Create a Dog named "Rex", age 3, breed "Labrador"
        // TODO: Print "\nDog:" then rex.describe() then "Rex says: " + rex.sound()

        // TODO: Create a Cat named "Whiskers", age 7, isIndoor true
        // TODO: Print "\nCat:" then whiskers.describe() then "Whiskers says: " + whiskers.sound()

        // TODO: Create a Bird named "Tweety", age 2, species "Canary"
        // TODO: Print "\nBird:" then tweety.describe() then "Tweety says: " + tweety.sound()

        // TODO: Assign your Dog object to an Animal variable (Animal animalRef = rex)
        //       Print "\nAnimal reference pointing to Dog:" then animalRef.describe()
        //       Notice: it still calls Dog's describe() — that's runtime polymorphism!
    }
}
