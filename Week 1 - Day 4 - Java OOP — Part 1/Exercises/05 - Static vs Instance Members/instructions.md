# Exercise 05: Static vs Instance Members

## Objective
Differentiate between `static` (class-level) and instance members by building a class that tracks how many objects have been created using a `static` counter field, and provides both static utility methods and instance methods.

---

## Background
A **static** member belongs to the **class itself**, not to any particular object. All instances share the same static field, and you call static methods on the class name: `Counter.getCount()`. An **instance** member belongs to one specific object and is called on a reference: `obj.describe()`. You cannot reference instance fields inside a static method — there is no object context.

---

## Requirements

1. Create a class `Vehicle` with:
   - A `private static int count = 0` field to track total vehicles created.
   - Private instance fields: `String make`, `String model`, `int year`
   - A public parameterized constructor that sets the three instance fields **and** increments `count` by 1.

2. Write `public static int getCount()` — returns the current value of `count`.

3. Write `public static String classify(int year)` — a pure static utility method that returns:
   - `"Classic"` if year < 1980
   - `"Vintage"` if year < 2000
   - `"Modern"` otherwise

4. Write a `public` instance method `String describe()` that returns:
   `"[year] [make] [model] ([classify result])"`
   — call the static `classify(this.year)` from inside the instance method.

5. In `main`:
   - Print `"Vehicles created: " + Vehicle.getCount()` before any objects are made (expect 0).
   - Create four `Vehicle` objects with different makes, models, and years.
   - After each creation, print `"Vehicles created: " + Vehicle.getCount()`.
   - Print `describe()` for all four vehicles.
   - Call `Vehicle.classify(1965)`, `Vehicle.classify(1995)`, and `Vehicle.classify(2021)` directly (without creating objects) and print each result.

---

## Hints
- `static` fields are initialized once when the class is loaded — before any constructor runs.
- Inside the constructor, `count++` increments the shared class-level counter.
- `classify()` is `static` because it doesn't need any object state — it only depends on the `year` argument.
- Calling a static method from an instance method is fine: `classify(this.year)` or just `classify(year)`.

---

## Expected Output
```
Vehicles created: 0
Vehicles created: 1
Vehicles created: 2
Vehicles created: 3
Vehicles created: 4
1965 Ford Mustang (Classic)
1998 Toyota Camry (Vintage)
2015 Honda Civic (Modern)
2023 Tesla Model 3 (Modern)
1965 → Classic
1995 → Vintage
2021 → Modern
```
