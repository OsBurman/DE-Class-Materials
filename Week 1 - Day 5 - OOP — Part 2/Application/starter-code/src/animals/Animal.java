package animals;

/**
 * Abstract base class for all animals.
 * Complete all TODOs.
 */
public abstract class Animal {

    // TODO Task 1: Declare private fields: name (String), age (int), habitat
    // (String)

    // TODO: Parameterized constructor
    public Animal(String name, int age, String habitat) {

    }

    // Getters
    public String getName() {
        return "";
    } // TODO: return name

    public int getAge() {
        return 0;
    } // TODO: return age

    public String getHabitat() {
        return "";
    } // TODO: return habitat

    // TODO Task 1 (cont): Abstract methods — subclasses MUST implement these
    public abstract String makeSound();

    public abstract String move();

    // TODO: Implement describe() — concrete method
    // Return: "[Name] is a [age]-year-old [ClassName] from [habitat]"
    // Use this.getClass().getSimpleName() for the class name
    public String describe() {
        return "";
    }

    // TODO: Overloaded describe(boolean verbose)
    // If verbose, append ": " + makeSound() + " | " + move() to the base describe()
    // result
    public String describe(boolean verbose) {
        return "";
    }
}
