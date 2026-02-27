# Walkthrough Script — Part 1
## Day 5: OOP — Part 2
### Files: `01-inheritance.java` · `02-method-overriding-vs-overloading.java` · `03-polymorphism.java`

---

## OVERVIEW (Opening Part 1)

**[ACTION]** Have all three files visible in the explorer. Don't open any yet.

"Good morning. We're on the last day of Week 1 — OOP Part 2. Everything from this week clicks together today. Yesterday we covered classes, constructors, access modifiers, and the `this` keyword. Today we're adding the three big ideas that make OOP genuinely powerful: inheritance, overriding and overloading, and polymorphism.

By end of morning you'll understand how to build class hierarchies where child classes build on parent classes, how Java decides at runtime which version of a method to actually call, and how one variable of type `Animal` can hold a `Dog`, a `Cat`, or a `Parrot` — and call the right behavior on each.

Let's go."

---

## FILE 1: `01-inheritance.java`

**[ACTION]** Open `01-inheritance.java`.

"Inheritance is the mechanism that lets one class build on another. The child class — called a **subclass** — gets everything the parent has, plus adds its own stuff."

---

### The Blueprint Hierarchy (Before the Code)

"Let me give you the mental model first. We have vehicles. All vehicles share some things: they have a make, a model, a year, a fuel level. But cars have doors and fuel type. Trucks have payload capacity. Electric cars have battery size and range.

Instead of copying the shared stuff into every class, we model it once in `Vehicle`, then each specific type **extends** it and adds only what's new."

**[ACTION]** Draw on the board (or show on screen):
```
Vehicle
├── Car
│   └── ElectricCar
└── Truck
```

"This is an IS-A relationship. A Car IS-A Vehicle. An ElectricCar IS-A Car (and therefore also IS-A Vehicle). This is the test: if the sentence 'X is a Y' makes sense in the real world, inheritance probably makes sense in code."

---

### SECTION A: Vehicle (the base class)

**[ACTION]** Scroll to `Vehicle`.

"Look at the fields: `make`, `model`, `year` — all `private`. And `fuelLevel` — this one is `protected`. Remember protected from yesterday: subclasses can access it directly."

"The constructor takes three parameters and sets all the fields. It also prints a trace message — we'll use that to watch the constructor chain fire."

"Methods: `refuel()`, `displayInfo()`, `toString()`. These will be available to every Vehicle subclass automatically — inherited for free."

---

### SECTION B: Car extends Vehicle

**[ACTION]** Scroll to `Car`.

"The `extends` keyword on line 1 of the class declaration is all you need to declare the relationship:"

```java
static class Car extends Vehicle { ... }
```

"Car adds two fields: `numDoors` and `fuelType`. And here's the most important part — the constructor:"

```java
public Car(String make, String model, int year, int numDoors, String fuelType) {
    super(make, model, year);   // ← calls Vehicle's constructor
    this.numDoors = numDoors;
    this.fuelType = fuelType;
}
```

"The very first thing the Car constructor does is call `super(make, model, year)`. This invokes the Vehicle constructor, which sets up the shared fields. THEN Car sets up its own fields."

⚠️ **WATCH OUT:** "`super()` must be the FIRST statement in a subclass constructor — exactly like `this()` must be first for constructor chaining. If you don't call `super()` explicitly, Java inserts `super()` automatically — but only if the parent has a no-arg constructor. Since Vehicle only has a 3-param constructor, forgetting `super()` here would be a compile error."

---

### SECTION C: Truck extends Vehicle

**[ACTION]** Scroll quickly over Truck.

"Same pattern — `extends Vehicle`, calls `super(make, model, year)`, adds its own fields. Truck has `payloadCapacityTons` and `hasTowHitch`, and its own `loadCargo()` method."

---

### SECTION D: ElectricCar extends Car (multi-level)

**[ACTION]** Scroll to `ElectricCar`.

"Now we go three levels deep. ElectricCar extends Car, which extends Vehicle. The chain is: Vehicle → Car → ElectricCar."

```java
public ElectricCar(String make, String model, int year, ...) {
    super(make, model, year, 4, "Electric");  // calls Car's constructor
    ...
}
```

"This calls `Car`'s constructor, which in turn calls `Vehicle`'s constructor. Three constructor levels fire in sequence."

---

### SECTION 1 in main: Watch the constructor chain

**[ACTION]** Scroll to Section 1 in main.

**[ASK]** "When I create an ElectricCar, how many constructor calls do you think will print?"

"Three. Watch:"

```java
ElectricCar ev = new ElectricCar("Tesla", "Model 3", 2024, 75.0, 500);
```

"Output:
- `[Vehicle constructor] Created: 2024 Tesla Model 3`
- `[Car constructor] Added: 4 doors, Electric`
- `[ElectricCar ctor] Added: 75.0 kWh, 500 km range`"

"The Vehicle part fires first, then Car, then ElectricCar. Parent before child — always."

---

### SECTION 2 in main: Inherited methods

**[ACTION]** Scroll to Section 2.

"Car inherits `refuel()` from Vehicle. We never wrote `refuel()` in the Car class — it just works:"

```java
sedan.refuel(20.0);   // Vehicle.refuel() — works on Car
pickup.refuel(50.0);  // Vehicle.refuel() — works on Truck
```

"Then `displayInfo()` — which we DID override in Car. It calls `super.displayInfo()` first and then adds car-specific lines. We can see the exact same pattern in the ElectricCar version:"

```java
ev.displayInfo();
// → Vehicle's line
// → Car's line
// → ElectricCar's line
```

---

### SECTION 3: super.method()

"That three-line output for `ev.displayInfo()` shows the `super.method()` pattern. Each class calls its parent's version first, then adds its own output. The chain flows upward before coming back down."

---

### SECTION 4: toString() chain

**[ACTION]** Scroll to Section 4.

"Same pattern with `toString()`. Each level calls `super.toString()` and appends its own detail:"

```java
System.out.println("ev: " + ev);
// → "2024 Tesla Model 3 (fuel=100%) [Car: 4-door, Electric] [EV: 75.0 kWh, 500 km]"
```

"One `println` call, three classes contributing to the string."

---

### SECTION 5: The Object class

**[ACTION]** Scroll to `SimpleBox` and Section 5.

"Every class in Java implicitly extends `Object` — even `Vehicle`. `Object` is the root of the entire class hierarchy."

"Three things you inherit from Object that you'll use constantly: `toString()`, `equals()`, `hashCode()`."

"Without overriding `toString()`, printing an object gives you `ClassName@hashcode` — memory gibberish. Without overriding `equals()`, the `==` operator is the default — it compares references, not content."

**[ASK]** "Two boxes, both containing 'Chocolates'. Are they equal?"

"With `==`: no — they're different objects. With `.equals()` after we override it: yes — same content."

```java
System.out.println(box1 == box2);           // false
System.out.println(box1.equals(box2));      // true
```

→ **TRANSITION:** "Inheritance gives us reuse and hierarchy. But there's an important question: when a subclass and a superclass both define a method with the same name — what happens? Let's go to File 2."

---

## FILE 2: `02-method-overriding-vs-overloading.java`

**[ACTION]** Open `02-method-overriding-vs-overloading.java`.

"Two concepts that sound similar but are completely different: **overriding** and **overloading**. Overriding is a runtime decision. Overloading is a compile-time decision."

---

### SECTION A: Method Overriding

**[ACTION]** Scroll to `Shape`, then `Circle.area()`.

"Shape defines `area()` and returns `0.0`. Circle overrides it:"

```java
@Override
public double area() {
    return Math.PI * radius * radius;
}
```

"Same name. Same parameter list (none). Same return type. That's a valid override. The `@Override` annotation tells the compiler: 'I intend this to replace the parent's version. Verify it.'"

"The `@Override` annotation also appears on `perimeter()` and `draw()`. All three override the Shape versions."

**[ACTION]** Scroll to `describe()` in Shape:

```java
public String describe() {
    return String.format("... area=%.2f | perimeter=%.2f", area(), perimeter());
}
```

"Notice `describe()` calls `area()` and `perimeter()`. When a Circle calls `describe()`, which version of `area()` runs?"

**[ASK]** "It's the describe() method defined in Shape — but the object is a Circle. Which area() runs?"

"Circle's version. Because the method call is resolved at runtime based on the actual object type. That's dynamic dispatch — we'll formalize this in File 3."

---

### SECTION 2: @Override catches typos

**[ACTION]** Scroll to `Dog extends Animal`.

"Here's why `@Override` matters every single time:"

```java
// ✅ Correct override
@Override
public String speak() { return "Woof!"; }

// ❌ This would silently create a NEW method:
// public String Speak() { return "Woof!"; }  ← capital S typo
```

"Without `@Override`, Java doesn't complain. You think you overrode `speak()`. You didn't. The parent's version still runs. This is a real-world bug that's surprisingly hard to track down."

"With `@Override`, the compiler immediately says: 'method does not override or implement a method from a supertype.' Bug caught at compile time."

⚠️ **WATCH OUT:** "Always use `@Override`. There is essentially no situation where omitting it is better. Make it a habit."

---

### SECTION 3: Rules for valid overriding

**[ACTION]** Scroll to `Printer` and `LaserPrinter`, then the printed rules summary in main.

"A few rules that make or break whether an override is valid:"

"**1. Same name + same parameters** — if you change the parameter list, it becomes an overload, not an override."

"**2. Return type** — must be the same, or a subtype (covariant). If parent returns `Animal`, child can return `Dog`."

"**3. Access modifier** — same or WIDER. Parent is `public` → child must be `public`. Parent is `protected` → child can be `protected` OR `public`. You can NEVER narrow visibility."

"In `LaserPrinter`, `getStatus()` was `protected` in `Printer` and we widened it to `public` — that's legal."

"**4. final methods** — already covered yesterday — cannot be overridden. The compiler blocks it."

"**5. static methods** — cannot be overridden. They're 'hidden', not overridden. Different topic — don't confuse the two."

---

### SECTION D: Method Overloading

**[ACTION]** Scroll to `Calculator` and Section 4 in main.

"Overloading is a completely different thing. Same class, same method name, DIFFERENT parameter list:"

```java
public int    add(int a, int b)           { ... }  // 2 ints
public int    add(int a, int b, int c)    { ... }  // 3 ints
public double add(double a, double b)     { ... }  // 2 doubles
public String add(String a, String b)     { ... }  // 2 Strings
```

"Java picks the right version based purely on what arguments you pass — at compile time. By the time the program runs, the decision is already made."

**[ASK]** "What happens if I call `calc.add(3, 4)`?"

"`add(int, int)` — two ints."

**[ASK]** "What about `calc.add(3.14, 2.71)`?"

"`add(double, double)` — two doubles."

⚠️ **WATCH OUT:** "You cannot overload by return type alone. `int getValue()` and `long getValue()` with the same parameters would be a compile error — the signature is name + parameter list only, not return type."

---

### SECTION E: Override AND Overload in the same subclass

**[ACTION]** Scroll to `EmailNotification` and Section 5 in main.

"A subclass can both override an inherited method AND add overloaded versions:"

"- `send(String message)` → **override** — replaces parent's version"
"- `send(String message, String recipient)` → **overload** — new method"
"- `send(String message, String recipient, boolean highPriority)` → another **overload**"

**[ACTION]** Show the comparison table at the bottom of main.

"Print this table out. Test yourselves on it:"

```
Feature          │ Overriding               │ Overloading
─────────────────┼──────────────────────────┼──────────────────────────
Where?           │ Subclass                 │ Same class (or subclass)
Signature        │ SAME                     │ DIFFERENT parameters
Resolved         │ Runtime (dynamic)        │ Compile time (static)
@Override?       │ Yes, always              │ N/A
```

→ **TRANSITION:** "Overriding is the engine behind polymorphism. Now let's see what polymorphism actually looks like in practice — and why it's the most powerful feature in object-oriented programming."

---

## FILE 3: `03-polymorphism.java`

**[ACTION]** Open `03-polymorphism.java`.

"Polymorphism literally means 'many forms'. In Java, it means one variable or one method call can produce different behavior depending on the actual type of the object involved."

"There are two flavors: **compile-time** (overloading — already covered) and **runtime** (the big one — dynamic dispatch)."

---

### SECTION A: The Animal hierarchy

**[ACTION]** Scroll to `Animal`, `Dog`, `Cat`, `Parrot`.

"The abstract class `Animal` defines `speak()` as abstract — no body. Dog, Cat, and Parrot each provide their own implementation."

"We also have subclass-specific methods: `Dog.fetch()`, `Cat.purr()`, `Parrot.mimic()`. These exist ONLY on their respective subclasses."

---

### SECTION 1: Compile-time polymorphism recap

**[ACTION]** Scroll to `Formatter` and Section 1 in main.

"Quick reminder from File 2 — the compiler resolves `format()` calls based on argument types. Static, compile-time, decided before the program runs."

---

### SECTION 2: Upcasting

**[ACTION]** Scroll to Section 2 in main.

"Here's where runtime polymorphism begins:"

```java
Animal animal1 = new Dog("Rex", 3, "German Shepherd");
Animal animal2 = new Cat("Luna", 2, true);
Animal animal3 = new Parrot("Polly", 5, "Hello!", "Cracker?", "Polly wants a cracker");
```

"The declared type on the LEFT is `Animal`. The actual object on the RIGHT is `Dog`, `Cat`, or `Parrot`. Assigning a subclass to a superclass variable is called **upcasting** — and it's always safe."

**[ASK]** "When I call `animal1.speak()` — the variable is declared as `Animal`. `Animal.speak()` is abstract — it has no body. What actually runs?"

"Dog's `speak()`. Java looks at the actual runtime type of `animal1` — which is `Dog` — and calls Dog's version. The declared type on the left doesn't matter for this decision."

"This is **dynamic dispatch** — the JVM resolves the method call at runtime based on the actual object type, not the variable type."

**[ACTION]** Show the commented-out line:

```java
// animal1.fetch("ball");  ← compile error: fetch() not on Animal
```

"Through the `Animal` variable, you can only call methods that `Animal` declares. The compiler sees `Animal` and limits you to the `Animal` API. Even though the object IS a Dog, the compiler only knows you declared `Animal`."

---

### SECTION 3: Polymorphic collections

**[ACTION]** Scroll to Section 3.

"This is the practical payoff. One list holding many types:"

```java
List<Animal> animals = new ArrayList<>();
animals.add(new Dog(...));
animals.add(new Cat(...));
animals.add(new Parrot(...));
animals.add(new Dog(...));
animals.add(new Cat(...));
```

"And then one loop:"

```java
for (Animal a : animals) {
    System.out.println(a.speak());
}
```

"Five different animals, five different outputs, one line of code. The loop doesn't know what type each animal is. It doesn't need to. It just calls `speak()` and the right version runs."

**[ACTION]** Scroll to `vetCheck()`:

```java
static void vetCheck(Animal animal) {
    System.out.printf("Vet check: %s | age=%d | says: %s%n",
        animal.getName(), animal.getAge(), animal.speak());
}
```

"This method accepts ANY Animal. Call it with a Dog, a Cat, a Parrot — it works for all of them. Write once, works for every Animal subtype that will ever exist, including ones not written yet. THAT is polymorphism's power."

---

### SECTION 4: instanceof and downcasting

**[ACTION]** Scroll to `processAnimal()` and Section 4.

"Sometimes you DO need type-specific behavior. `Dog.fetch()` doesn't exist on `Animal`. To call it, you need a Dog reference. That requires a **downcast**."

"But before you downcast, you MUST check with `instanceof`:"

```java
if (animal instanceof Dog dog) {
    dog.fetch("ball");
} else if (animal instanceof Cat cat) {
    cat.purr();
}
```

"This is Java 16+ pattern-matching syntax — it checks AND casts in one step. The variable `dog` is only in scope inside the `if` block, and it's already the right type."

---

### SECTION 5: ClassCastException

**[ACTION]** Scroll to Section 5.

"What happens without the check:"

```java
Animal myCat = new Cat("Felix", 3, true);
Dog wrongCast = (Dog) myCat;   // ClassCastException!
```

"Cat is NOT a Dog. Java throws `ClassCastException` at runtime — not a compile error. You won't know until the program crashes. Always use `instanceof`."

---

### Closing the Polymorphism file

**[ACTION]** Scroll to the summary at the bottom of main.

"Two flavors:"

"**Compile-time (static dispatch):** Overloading. The compiler picks the method based on argument types. Decided before the program runs."

"**Runtime (dynamic dispatch):** Overriding. The JVM calls the actual runtime type's version. The most powerful OOP feature."

"Key terms to remember: **upcasting** is always safe. **Downcasting** requires `instanceof`. **Dynamic dispatch** is why polymorphism works."

→ **TRANSITION:** "After the break, Part 2 covers the other big OOP concepts: abstract classes vs interfaces, encapsulation best practices, and how to organize your code into packages. The polymorphism foundation we just built is exactly what makes interfaces so useful."

---

## SELF-CHECK ✅

- [x] `extends` keyword: declaring IS-A relationship
- [x] `super()`: calling parent constructor from child
- [x] `super.method()`: calling parent method from child override
- [x] Multi-level inheritance (Vehicle → Car → ElectricCar)
- [x] What IS inherited (public/protected fields and methods)
- [x] What is NOT inherited (constructors, private members)
- [x] The Object class as the root of all Java classes
- [x] `toString()`, `equals()`, `hashCode()` from Object
- [x] Method overriding: same name, same params, correct access
- [x] `@Override` annotation: safety net against typos
- [x] Overriding rules: access widening, covariant return type, no final override
- [x] Method overloading: same name, different parameters
- [x] Overloading resolved at compile time
- [x] Override vs overload comparison table
- [x] Compile-time polymorphism (overloading demo)
- [x] Runtime polymorphism / dynamic dispatch
- [x] Upcasting: subclass → superclass reference
- [x] Polymorphic collections and single-loop dispatch
- [x] `instanceof` and downcasting
- [x] `ClassCastException` and how to avoid it
- [x] Pattern-matching `instanceof` (Java 16+)
- [x] Learning Objectives: implement inheritance ✓, override/overload appropriately ✓, polymorphism ✓ (interfaces in Part 2)
