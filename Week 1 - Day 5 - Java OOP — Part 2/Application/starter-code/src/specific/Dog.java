package specific;

import animals.Mammal;
import behaviors.Swimmable;
import behaviors.Trainable;

/**
 * A Dog is a Mammal that can Swim and be Trained.
 * TODO Task 4: Extend Mammal, implement Swimmable and Trainable.
 * - Override makeSound() → "Woof!"
 * - Override move() → "Runs on four legs"
 * - Implement swim() → "Splashes through water"
 * - Implement train(String command) → "Dog performs: [command]"
 */
public class Dog extends Mammal implements Swimmable, Trainable {

    public Dog(String name, int age, String habitat, String furColor) {
        super(name, age, habitat, furColor);
    }

    // TODO: Override makeSound()
    // TODO: Override move()
    // TODO: Implement swim()
    // TODO: Implement train(String command)
}
