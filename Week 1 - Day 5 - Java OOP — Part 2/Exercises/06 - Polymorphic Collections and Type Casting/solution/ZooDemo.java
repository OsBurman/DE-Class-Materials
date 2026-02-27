import java.util.ArrayList;

// Abstract base — every zoo animal has a name and must define its sound
abstract class ZooAnimal {
    protected String name;

    public ZooAnimal(String name) { this.name = name; }

    public String getName() { return name; }

    public abstract String makeSound();

    // Concrete method — calls the abstract makeSound() via runtime polymorphism
    public String describe() {
        return name + " says: " + makeSound();
    }
}

// Lion — adds pride group membership and a roar
class Lion extends ZooAnimal {
    private String pride;

    public Lion(String name, String pride) {
        super(name);
        this.pride = pride;
    }

    @Override
    public String makeSound() { return "ROAR"; }

    public String getPride() { return pride; }
}

// Penguin — can swim but cannot fly
class Penguin extends ZooAnimal {
    private boolean canFly;

    public Penguin(String name) {
        super(name);
        this.canFly = false;  // penguins never fly
    }

    @Override
    public String makeSound() { return "Squawk"; }

    public boolean canFly() { return canFly; }

    // Subclass-specific behavior — only accessible via Penguin reference or downcast
    public String swim() { return name + " is swimming!"; }
}

// Parrot — repeats a learned phrase
class Parrot extends ZooAnimal {
    private String phrase;

    public Parrot(String name, String phrase) {
        super(name);
        this.phrase = phrase;
    }

    @Override
    public String makeSound() { return "Squawk"; }

    // Subclass-specific behavior
    public String repeat() { return name + " says: " + phrase; }
}

public class ZooDemo {
    public static void main(String[] args) {
        System.out.println("=== Zoo Animal Registry ===\n");

        // A single list holds multiple concrete types — all share the ZooAnimal supertype
        ArrayList<ZooAnimal> animals = new ArrayList<>();
        animals.add(new Lion("Simba", "Pridelands"));
        animals.add(new Lion("Nala", "Pridelands"));
        animals.add(new Penguin("Tux"));
        animals.add(new Penguin("Pebble"));
        animals.add(new Parrot("Polly", "Polly wants a cracker"));

        // --- Polymorphic loop: call describe() without knowing the concrete type ---
        System.out.println("--- All Animals (polymorphic describe) ---");
        for (ZooAnimal animal : animals) {
            System.out.println(animal.describe());  // runtime dispatch to each subclass's makeSound()
        }

        System.out.println();

        // --- instanceof + downcast to access subclass-specific methods ---
        System.out.println("--- Species-Specific Actions ---");
        for (ZooAnimal animal : animals) {
            if (animal instanceof Lion) {
                Lion lion = (Lion) animal;  // safe: we checked instanceof first
                System.out.println(lion.getName() + " belongs to the " + lion.getPride() + " pride");
            } else if (animal instanceof Penguin) {
                Penguin penguin = (Penguin) animal;
                System.out.println(penguin.swim());
            } else if (animal instanceof Parrot) {
                Parrot parrot = (Parrot) animal;
                System.out.println(parrot.repeat());
            }
        }

        System.out.println();

        // --- Count each type using instanceof ---
        System.out.println("--- Animal Count ---");
        int lionCount = 0, penguinCount = 0, parrotCount = 0;
        for (ZooAnimal animal : animals) {
            if (animal instanceof Lion)   lionCount++;
            if (animal instanceof Penguin) penguinCount++;
            if (animal instanceof Parrot) parrotCount++;
        }
        System.out.println("Lions:   " + lionCount);
        System.out.println("Penguins: " + penguinCount);
        System.out.println("Parrots: " + parrotCount);
        System.out.println("Total:   " + animals.size());
    }
}
