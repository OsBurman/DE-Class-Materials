package com.academy;

/**
 * Day 5 Part 1 — Inheritance, Method Overriding, Polymorphism
 *
 * Theme: Animal Kingdom
 * Run: mvn compile exec:java
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║  Day 5 Part 1 — Inheritance & Polymorphism Demo     ║");
        System.out.println("╚══════════════════════════════════════════════════════╝\n");

        System.out.println("=== 1. Creating Objects (Inheritance Hierarchy) ===");
        Animal[] zoo = {
            new Dog("Rex",    "Labrador"),
            new Cat("Whiskers"),
            new Bird("Tweety", true),
            new Dog("Buddy",  "Poodle"),
            new Fish("Nemo")
        };

        System.out.println("\n=== 2. Polymorphism — calling makeSound() on each Animal ===");
        for (Animal animal : zoo) {
            animal.makeSound();   // runtime polymorphism — correct subclass method called
        }

        System.out.println("\n=== 3. instanceof Check & Downcasting ===");
        for (Animal animal : zoo) {
            if (animal instanceof Dog dog) {           // Java 16+ pattern matching
                System.out.println("  Dog breed: " + dog.getBreed());
            } else if (animal instanceof Bird bird) {
                System.out.println("  Bird can fly: " + bird.canFly());
            }
        }

        System.out.println("\n=== 4. Method Overloading vs Overriding ===");
        Dog d = new Dog("Max", "Husky");
        d.fetch();                   // overloaded — no args
        d.fetch("tennis ball");      // overloaded — with arg
        d.makeSound();               // overridden from Animal

        System.out.println("\n=== 5. super Keyword ===");
        Cat cat = new Cat("Luna");
        cat.describe();   // calls super.describe() then adds its own info

        System.out.println("\n✓ Inheritance & Polymorphism demo complete.");
    }
}

// ─────────────────────────────────────────────────────────────
// Base class — defines common behavior for all animals
// ─────────────────────────────────────────────────────────────
abstract class Animal {
    protected String name;
    protected int    age;

    public Animal(String name) {
        this.name = name;
        this.age  = 1;
    }

    // Abstract method — MUST be overridden by subclasses
    public abstract void makeSound();

    // Concrete method — inherited as-is (can be overridden)
    public void eat() {
        System.out.println("  " + name + " is eating.");
    }

    public void describe() {
        System.out.println("  Animal: " + name + ", Age: " + age);
    }

    public String getName() { return name; }
}

// ─────────────────────────────────────────────────────────────
// Subclasses — inherit from Animal
// ─────────────────────────────────────────────────────────────
class Dog extends Animal {
    private String breed;

    public Dog(String name, String breed) {
        super(name);          // call Animal constructor
        this.breed = breed;
    }

    @Override
    public void makeSound() {
        System.out.println("  " + name + " (Dog): Woof! Woof!");
    }

    // Method overloading — same name, different parameters
    public void fetch()             { System.out.println("  " + name + " fetches the ball!"); }
    public void fetch(String item)  { System.out.println("  " + name + " fetches the " + item + "!"); }

    public String getBreed() { return breed; }
}

class Cat extends Animal {
    public Cat(String name) { super(name); }

    @Override
    public void makeSound() {
        System.out.println("  " + name + " (Cat): Meow~");
    }

    @Override
    public void describe() {
        super.describe();    // call parent's describe() first
        System.out.println("    ↳ Cat specialty: loves knocking things off shelves");
    }
}

class Bird extends Animal {
    private boolean flightCapable;

    public Bird(String name, boolean canFly) {
        super(name);
        this.flightCapable = canFly;
    }

    @Override
    public void makeSound() {
        System.out.println("  " + name + " (Bird): Tweet! Tweet!");
    }

    public boolean canFly() { return flightCapable; }
}

class Fish extends Animal {
    public Fish(String name) { super(name); }

    @Override
    public void makeSound() {
        System.out.println("  " + name + " (Fish): ...blub...");
    }
}
