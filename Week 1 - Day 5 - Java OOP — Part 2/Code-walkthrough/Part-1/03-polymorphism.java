/**
 * DAY 5 — OOP Part 2 | Part 1, File 3
 * TOPIC: Polymorphism — Compile-time and Runtime
 *
 * Topics covered:
 *  - Compile-time polymorphism (static dispatch): method overloading — resolved by compiler
 *  - Runtime polymorphism (dynamic dispatch): method overriding — resolved by JVM at runtime
 *  - Upcasting: treating a subclass object as its superclass type
 *  - Downcasting: casting back to the original subclass type
 *  - instanceof operator: safe type checking before downcast
 *  - Polymorphic collections: List<Animal> holding Dogs, Cats, etc.
 *  - Practical use: writing code that works for any subtype
 *
 * Key vocabulary:
 *  - polymorphism    : one interface, many implementations ("many forms")
 *  - upcasting       : assigning a subclass reference to a superclass variable (always safe)
 *  - downcasting     : casting a superclass reference back to a subclass (must check with instanceof)
 *  - dynamic dispatch: the JVM calls the method on the actual runtime type, not the declared type
 */
public class Polymorphism {

    // ==========================================================
    // SECTION A: The class hierarchy we'll use for demos
    // ==========================================================

    static abstract class Animal {
        protected String name;
        protected int    age;

        public Animal(String name, int age) {
            this.name = name;
            this.age  = age;
        }

        // Every animal CAN speak — but each in its own way
        public abstract String speak();

        // Common concrete method — shared by all
        public void sleep() {
            System.out.println("  " + name + " is sleeping... 💤");
        }

        public String eat(String food) {
            return name + " eats " + food;
        }

        public String getName() { return name; }
        public int    getAge()  { return age; }

        @Override
        public String toString() {
            return String.format("%s(name='%s', age=%d)", getClass().getSimpleName(), name, age);
        }
    }

    static class Dog extends Animal {
        private String breed;

        public Dog(String name, int age, String breed) {
            super(name, age);
            this.breed = breed;
        }

        @Override
        public String speak() { return name + " says: Woof! 🐶"; }

        public void fetch(String item) {
            System.out.println("  " + name + " fetches the " + item + "!");
        }

        public String getBreed() { return breed; }

        @Override
        public String toString() {
            return String.format("Dog{name='%s', age=%d, breed='%s'}", name, age, breed);
        }
    }

    static class Cat extends Animal {
        private boolean isIndoor;

        public Cat(String name, int age, boolean isIndoor) {
            super(name, age);
            this.isIndoor = isIndoor;
        }

        @Override
        public String speak() { return name + " says: Meow! 🐱"; }

        public void purr() {
            System.out.println("  " + name + " purrs contentedly... prrr 😸");
        }

        public boolean isIndoor() { return isIndoor; }
    }

    static class Parrot extends Animal {
        private String[] vocabulary;

        public Parrot(String name, int age, String... vocabulary) {
            super(name, age);
            this.vocabulary = vocabulary;
        }

        @Override
        public String speak() {
            // Parrots repeat their vocabulary
            return name + " says: \"" + vocabulary[(int)(Math.random() * vocabulary.length)] + "\" 🦜";
        }

        public void mimic(String phrase) {
            System.out.println("  " + name + " mimics: \"" + phrase + "\"");
        }

        public int getVocabularySize() { return vocabulary.length; }
    }

    // ==========================================================
    // SECTION B: Compile-time polymorphism (overloading)
    // ==========================================================
    // Already shown in file 2 — here we add one more illustration:
    // the compiler resolves the call based purely on argument types.

    static class Formatter {

        public String format(int value) {
            return String.format("[INT: %d]", value);
        }

        public String format(double value) {
            return String.format("[DOUBLE: %.2f]", value);
        }

        public String format(String value) {
            return String.format("[STRING: \"%s\"]", value);
        }

        public String format(int value, String label) {
            return String.format("[%s: %d]", label.toUpperCase(), value);
        }
    }

    // ==========================================================
    // SECTION C: Runtime polymorphism (dynamic dispatch)
    // ==========================================================
    // The DECLARED type of the variable is Animal.
    // The ACTUAL runtime type is Dog, Cat, or Parrot.
    // When speak() is called, Java looks at the actual runtime type — not the declared type.

    // ==========================================================
    // SECTION D: instanceof and safe downcasting
    // ==========================================================

    // ==========================================================
    // MAIN — DEMONSTRATIONS
    // ==========================================================
    public static void main(String[] args) {

        System.out.println("============================================================");
        System.out.println("SECTION 1 — Compile-time polymorphism (overloading)");
        System.out.println("============================================================");

        Formatter fmt = new Formatter();
        System.out.println(fmt.format(42));
        System.out.println(fmt.format(3.14159));
        System.out.println(fmt.format("Hello"));
        System.out.println(fmt.format(100, "score"));
        System.out.println();
        System.out.println("The COMPILER chose which format() to call.");
        System.out.println("Decision is made at COMPILE TIME based on argument types.");
        System.out.println();

        System.out.println("============================================================");
        System.out.println("SECTION 2 — Upcasting: subclass → superclass reference");
        System.out.println("============================================================");

        // DECLARED type is Animal, ACTUAL type is Dog
        Animal animal1 = new Dog("Rex", 3, "German Shepherd");  // upcast — always safe
        Animal animal2 = new Cat("Luna", 2, true);
        Animal animal3 = new Parrot("Polly", 5, "Hello!", "Cracker?", "Polly wants a cracker");

        System.out.println("animal1: " + animal1);
        System.out.println("animal2: " + animal2);
        System.out.println("animal3: " + animal3);
        System.out.println();

        // Through the Animal reference, we can only call Animal methods:
        System.out.println(animal1.speak());   // ← Dog.speak() called — NOT Animal.speak()
        System.out.println(animal2.speak());   // ← Cat.speak()
        System.out.println(animal3.speak());   // ← Parrot.speak()
        System.out.println();
        System.out.println("The DECLARED type is Animal — but Java calls the ACTUAL type's method.");
        System.out.println("This is RUNTIME polymorphism / dynamic dispatch.");
        System.out.println();

        // animal1.fetch("ball");  ← compile error: fetch() not on Animal — only on Dog
        // The compiler sees Animal and only allows Animal API.

        System.out.println("============================================================");
        System.out.println("SECTION 3 — Polymorphic collections");
        System.out.println("============================================================");

        // One list — many types
        java.util.List<Animal> animals = new java.util.ArrayList<>();
        animals.add(new Dog("Buddy",  4, "Labrador"));
        animals.add(new Cat("Whiskers", 1, false));
        animals.add(new Parrot("Rio", 3, "Squawk!", "Pretty bird", "Hello!"));
        animals.add(new Dog("Max",   6, "Bulldog"));
        animals.add(new Cat("Shadow", 5, true));

        System.out.println("All animals speak:");
        for (Animal a : animals) {
            System.out.println("  " + a.speak());   // dynamic dispatch — correct method every time
        }
        System.out.println();

        System.out.println("All animals eat:");
        for (Animal a : animals) {
            System.out.println("  " + a.eat("their food"));
        }
        System.out.println();

        // Polymorphic method — doesn't care what type of Animal
        System.out.println("Running all animals through a vet check:");
        for (Animal a : animals) {
            vetCheck(a);
        }
        System.out.println();

        System.out.println("============================================================");
        System.out.println("SECTION 4 — instanceof and safe downcasting");
        System.out.println("============================================================");

        System.out.println("Processing each animal with type-specific behavior:");
        for (Animal a : animals) {
            processAnimal(a);
        }
        System.out.println();

        System.out.println("============================================================");
        System.out.println("SECTION 5 — Unsafe downcast (ClassCastException)");
        System.out.println("============================================================");

        Animal myCat = new Cat("Felix", 3, true);

        // ✅ Safe — check first
        if (myCat instanceof Dog) {
            Dog d = (Dog) myCat;
            d.fetch("stick");
        } else {
            System.out.println("  myCat is NOT a Dog — skipping fetch()");
        }

        // ❌ Unsafe — no check, throws ClassCastException at runtime
        try {
            Dog wrongCast = (Dog) myCat;   // Cat is NOT a Dog
            wrongCast.fetch("ball");
        } catch (ClassCastException e) {
            System.out.println("  ClassCastException caught: " + e.getMessage());
            System.out.println("  Always use instanceof before downcasting!");
        }
        System.out.println();

        System.out.println("============================================================");
        System.out.println("SECTION 6 — Pattern matching instanceof (Java 16+)");
        System.out.println("============================================================");

        Animal[] sample = {
            new Dog("Rufus", 2, "Beagle"),
            new Cat("Mittens", 4, true),
            new Parrot("Coco", 2, "Ciao!", "Volare!")
        };

        for (Animal a : sample) {
            // Old style: if (a instanceof Dog) { Dog d = (Dog) a; d.fetch(...); }
            // New style (Java 16+): combines check + cast in one:
            if (a instanceof Dog d) {
                System.out.printf("  %s is a %s — fetch time!%n", d.getName(), d.getBreed());
                d.fetch("frisbee");
            } else if (a instanceof Cat c) {
                System.out.printf("  %s is %s — purring...%n",
                        c.getName(), c.isIndoor() ? "indoor" : "outdoor");
                c.purr();
            } else if (a instanceof Parrot p) {
                System.out.printf("  %s knows %d words%n", p.getName(), p.getVocabularySize());
                p.mimic("Polly wants a cracker");
            }
        }
        System.out.println();

        System.out.println("============================================================");
        System.out.println("POLYMORPHISM SUMMARY");
        System.out.println("============================================================");
        System.out.println("  Compile-time (static dispatch):");
        System.out.println("    → Method OVERLOADING — different parameter lists");
        System.out.println("    → Compiler picks the right version at compile time");
        System.out.println();
        System.out.println("  Runtime (dynamic dispatch):");
        System.out.println("    → Method OVERRIDING — subclass replaces parent method");
        System.out.println("    → JVM calls the actual runtime type's version");
        System.out.println("    → Enables polymorphic code: write once, works for many types");
        System.out.println();
        System.out.println("  Upcasting   : always safe (subclass ref → superclass var)");
        System.out.println("  Downcasting : requires instanceof check → ClassCastException risk");
    }

    // ── Helper methods demonstrating polymorphic design ───────────────────────

    // This method works for ANY Animal — it doesn't know or care about the subtype
    static void vetCheck(Animal animal) {
        System.out.printf("  Vet check: %s | age=%d | says: %s%n",
                animal.getName(), animal.getAge(), animal.speak());
    }

    // When you DO need subtype-specific behavior: use instanceof
    static void processAnimal(Animal animal) {
        System.out.print("  " + animal.getName() + ": ");
        if (animal instanceof Dog dog) {
            System.out.println("Dog (" + dog.getBreed() + ") — fetch!");
            dog.fetch("ball");
        } else if (animal instanceof Cat cat) {
            System.out.println("Cat — purr time!");
            cat.purr();
        } else if (animal instanceof Parrot parrot) {
            System.out.println("Parrot — vocabulary size: " + parrot.getVocabularySize());
        } else {
            System.out.println("Unknown animal type");
        }
    }
}
