# Exercise 06: Polymorphic Collections and Type Casting

## Objective
Practice storing mixed object types in a single collection using a supertype reference, iterating polymorphically, and safely downcasting with `instanceof` to access subclass-specific behavior.

## Background
A zoo management system tracks many different animals in a single list. The zoo staff needs to process all animals uniformly (feed them, describe them) but also needs to perform species-specific actions on certain animals. This is where polymorphic collections and safe type casting become essential real-world skills.

## Requirements

1. Reuse (or redefine in this file) the following class hierarchy:
   - Abstract class `ZooAnimal` with:
     - Protected field `String name`
     - Constructor taking `name`
     - Getter `getName()`
     - Abstract method `String makeSound()`
     - Concrete method `String describe()` that returns `"[name] says: [makeSound()]"`
   - Class `Lion` extending `ZooAnimal`:
     - Additional field `String pride` (the name of the lion's pride group)
     - Constructor takes `name`, `pride`
     - Override `makeSound()` → `"ROAR"`
     - Add method `String getPride()` → returns the pride name
   - Class `Penguin` extending `ZooAnimal`:
     - Additional field `boolean canFly` (always `false` for penguins — but model it anyway)
     - Constructor takes `name`; `canFly` is always `false`
     - Override `makeSound()` → `"Squawk"`
     - Add method `boolean canFly()` → returns `false`
     - Add method `String swim()` → returns `"[name] is swimming!"`
   - Class `Parrot` extending `ZooAnimal`:
     - Additional field `String phrase` (a phrase the parrot repeats)
     - Constructor takes `name`, `phrase`
     - Override `makeSound()` → `"Squawk"`
     - Add method `String repeat()` → returns `"[name] says: [phrase]"`

2. In `main`:
   - Create an `ArrayList<ZooAnimal>` and add at least:
     - 2 `Lion` objects
     - 2 `Penguin` objects
     - 1 `Parrot` object
   - **Polymorphic loop**: iterate the list and call `describe()` on every animal — this works because `describe()` is defined on `ZooAnimal`
   - **instanceof + downcast**: iterate the list again and:
     - If the animal `instanceof Lion`, downcast and print `"[name] belongs to the [pride] pride"`
     - If the animal `instanceof Penguin`, downcast and call `swim()` and print the result
     - If the animal `instanceof Parrot`, downcast and call `repeat()` and print the result
   - **Count by type**: count how many Lions, Penguins, and Parrots are in the list and print the counts

## Hints
- `ArrayList<ZooAnimal>` stores any object that IS-A `ZooAnimal` — you can add `Lion`, `Penguin`, and `Parrot` to the same list
- After `instanceof` confirms the type, you must **downcast** to access subclass-specific methods: `Lion lion = (Lion) animal;`
- Without the `instanceof` check, a downcast to the wrong type throws a `ClassCastException` at runtime
- Java 16+ pattern matching syntax (`if (animal instanceof Lion lion)`) combines the check and cast in one step — either approach is fine

## Expected Output

```
=== Zoo Animal Registry ===

--- All Animals (polymorphic describe) ---
Simba says: ROAR
Nala says: ROAR
Tux says: Squawk
Pebble says: Squawk
Polly says: Squawk

--- Species-Specific Actions ---
Simba belongs to the Pridelands pride
Nala belongs to the Pridelands pride
Tux is swimming!
Pebble is swimming!
Polly says: Polly wants a cracker

--- Animal Count ---
Lions:   2
Penguins: 2
Parrots: 1
Total:   5
```
