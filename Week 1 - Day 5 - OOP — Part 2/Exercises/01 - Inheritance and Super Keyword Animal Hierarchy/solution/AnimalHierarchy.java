// Base class — defines the common contract for all animals
class Animal {
    private String name;
    private int age;

    // Constructor initializes shared fields
    public Animal(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() { return name; }
    public int getAge()     { return age; }

    // Base describe — subclasses can call super.describe() or use getters
    public String describe() {
        return name + " (age " + age + ")";
    }

    // Default sound — overridden by each subclass
    public String sound() {
        return "...";
    }
}

// Dog inherits Animal and adds breed-specific data and behaviors
class Dog extends Animal {
    private String breed;

    public Dog(String name, int age, String breed) {
        super(name, age);   // must be first — initializes name and age in Animal
        this.breed = breed;
    }

    public String getBreed() { return breed; }

    @Override
    public String sound() {
        return "Woof!";
    }

    @Override
    public String describe() {
        // Using parent getters to access private fields of Animal
        return getName() + " the " + breed + " (age " + getAge() + ")";
    }
}

// Cat inherits Animal and adds indoor/outdoor distinction
class Cat extends Animal {
    private boolean isIndoor;

    public Cat(String name, int age, boolean isIndoor) {
        super(name, age);
        this.isIndoor = isIndoor;
    }

    public boolean isIndoor() { return isIndoor; }

    @Override
    public String sound() {
        return "Meow!";
    }

    @Override
    public String describe() {
        String type = isIndoor ? "Indoor" : "Outdoor";
        return getName() + " (age " + getAge() + ") - " + type + " cat";
    }
}

// Bird inherits Animal and adds species classification
class Bird extends Animal {
    private String species;

    public Bird(String name, int age, String species) {
        super(name, age);
        this.species = species;
    }

    @Override
    public String sound() {
        return "Tweet!";
    }

    @Override
    public String describe() {
        return getName() + " the " + species + " (age " + getAge() + ")";
    }
}

public class AnimalHierarchy {
    public static void main(String[] args) {
        System.out.println("=== Animal Hierarchy Demo ===");

        // Create a Dog instance
        Dog rex = new Dog("Rex", 3, "Labrador");
        System.out.println("\nDog:");
        System.out.println(rex.describe());
        System.out.println("Rex says: " + rex.sound());

        // Create a Cat instance
        Cat whiskers = new Cat("Whiskers", 7, true);
        System.out.println("\nCat:");
        System.out.println(whiskers.describe());
        System.out.println("Whiskers says: " + whiskers.sound());

        // Create a Bird instance
        Bird tweety = new Bird("Tweety", 2, "Canary");
        System.out.println("\nBird:");
        System.out.println(tweety.describe());
        System.out.println("Tweety says: " + tweety.sound());

        // Assign Dog to an Animal reference — runtime polymorphism in action:
        // the JVM calls Dog's describe(), not Animal's, because the actual object is a Dog
        Animal animalRef = rex;
        System.out.println("\nAnimal reference pointing to Dog:");
        System.out.println(animalRef.describe());
    }
}
