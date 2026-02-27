# Exercise 01: Class and Object Basics

## Objective
Practice defining a Java class with fields and methods, then instantiate and use multiple objects.

---

## Background
A **class** is a blueprint; an **object** is a concrete instance created from that blueprint. Every object has its own copy of the instance fields declared in the class. You create an object with the `new` keyword, and you invoke its methods using dot notation: `object.method()`.

---

## Requirements

1. In a file named `Dog.java`, define a class `Dog` with:
   - Three instance fields: `String name`, `String breed`, `int age`
   - A method `void bark()` that prints `"[name] says: Woof!"`
   - A method `String describe()` that returns `"[name] is a [age]-year-old [breed]."`

2. In a file named `Main.java`, write a `main` method that:
   - Creates **three** `Dog` objects using different field values (assigned directly after construction, e.g. `dog.name = "Rex"`).
   - Calls `bark()` on each dog.
   - Calls `describe()` on each dog and prints the result.
   - Prints the total number of dogs created (`3`) by using `dogs.length` on an array of the three objects.

3. Demonstrate that objects are independent: change `dog1.age` after creation and show the updated value without affecting other objects.

---

## Hints
- Fields are declared inside the class body but outside any method: `String name;`
- To access a field or call a method on an object, use dot notation: `myDog.name = "Max"`
- `describe()` should build a `String` using concatenation and `return` it — don't print inside the method.
- Arrays of objects: `Dog[] dogs = {dog1, dog2, dog3};` — then `dogs.length` gives the count.

---

## Expected Output
```
Rex says: Woof!
Bella says: Woof!
Max says: Woof!
Rex is a 3-year-old German Shepherd.
Bella is a 2-year-old Labrador.
Max is a 5-year-old Poodle.
Total dogs: 3
Rex's age updated to 4.
Rex is a 4-year-old German Shepherd.
Bella is still 2.
```
