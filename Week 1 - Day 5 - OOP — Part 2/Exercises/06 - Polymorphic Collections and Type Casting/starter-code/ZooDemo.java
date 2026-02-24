import java.util.ArrayList;

// Abstract base class for all zoo animals
abstract class ZooAnimal {
    // TODO: Declare protected field String name

    // TODO: Write a constructor that takes name and assigns it

    // TODO: Write getter getName()

    // TODO: Declare abstract method String makeSound()

    // TODO: Write concrete method String describe()
    //       Returns: "[name] says: [makeSound()]"
}

// Lion subclass
class Lion extends ZooAnimal {
    // TODO: Declare private field String pride (the pride group name)

    // TODO: Constructor takes name and pride; call super(name)

    // TODO: Override makeSound() to return "ROAR"

    // TODO: Add method String getPride() that returns the pride name
}

// Penguin subclass
class Penguin extends ZooAnimal {
    // TODO: Declare private field boolean canFly (always false)

    // TODO: Constructor takes name only; call super(name), set canFly = false

    // TODO: Override makeSound() to return "Squawk"

    // TODO: Add method boolean canFly() that returns false

    // TODO: Add method String swim() that returns "[name] is swimming!"
}

// Parrot subclass
class Parrot extends ZooAnimal {
    // TODO: Declare private field String phrase

    // TODO: Constructor takes name and phrase; call super(name)

    // TODO: Override makeSound() to return "Squawk"

    // TODO: Add method String repeat() that returns "[name] says: [phrase]"
}

public class ZooDemo {
    public static void main(String[] args) {
        System.out.println("=== Zoo Animal Registry ===\n");

        // TODO: Create an ArrayList<ZooAnimal>

        // TODO: Add to the list:
        //       - new Lion("Simba", "Pridelands")
        //       - new Lion("Nala", "Pridelands")
        //       - new Penguin("Tux")
        //       - new Penguin("Pebble")
        //       - new Parrot("Polly", "Polly wants a cracker")

        // --- Polymorphic describe loop ---
        System.out.println("--- All Animals (polymorphic describe) ---");
        // TODO: Loop through the list and print animal.describe() for each
        //       (No casting needed — describe() is defined on ZooAnimal)

        System.out.println();

        // --- Species-specific actions with instanceof + downcast ---
        System.out.println("--- Species-Specific Actions ---");
        // TODO: Loop through the list again.
        //       For each animal, check instanceof:
        //       - If Lion:    Lion lion = (Lion) animal;  print "[lion.getName()] belongs to the [lion.getPride()] pride"
        //       - If Penguin: downcast and print penguin.swim()
        //       - If Parrot:  downcast and print parrot.repeat()

        System.out.println();

        // --- Count by type ---
        System.out.println("--- Animal Count ---");
        // TODO: Declare int lionCount = 0, penguinCount = 0, parrotCount = 0
        // TODO: Loop through the list; use instanceof to increment the right counter
        // TODO: Print each count and the total
    }
}
