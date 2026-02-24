# Walkthrough Script — Part 2
## Day 4: OOP — Part 1
### Files: `01-access-modifiers.java` · `02-non-access-modifiers.java` · `03-this-keyword.java`

---

## OVERVIEW (Opening Part 2)

**[ACTION]** Have all three Part-2 files visible in the explorer. Don't open any yet.

"Welcome back. In Part 1 we built classes with real fields and methods, and we understand how objects work. In Part 2 we put walls and locks on those objects.

Three topics:
1. **Access modifiers** — who can see what
2. **Non-access modifiers** — `static`, `final`, and a preview of `abstract`
3. **The `this` keyword** — what it is, and four ways to use it

Let's go."

---

## FILE 1: `01-access-modifiers.java`

**[ACTION]** Open `01-access-modifiers.java`.

"Access modifiers are the keywords — `public`, `private`, `protected`, and nothing at all — that control visibility. They answer the question: who is allowed to read or write this piece of data?"

---

### The Visibility Table

**[ACTION]** Scroll to the visibility table in the file header comments.

"Four levels, four columns. Read it left to right — each modifier is MORE restrictive than the one above it:"

```
Modifier    │ Same Class │ Same Package │ Subclass │ Everywhere
────────────┼────────────┼──────────────┼──────────┼───────────
public      │     ✅     │      ✅      │    ✅    │    ✅
protected   │     ✅     │      ✅      │    ✅    │    ❌
(default)   │     ✅     │      ✅      │    ❌    │    ❌
private     │     ✅     │      ❌      │    ❌    │    ❌
```

"Commit this to memory. You WILL need it."

**[ASK]** "Which modifier do you think most instance fields should be?"

"Private. Almost always private. That's encapsulation."

---

### SECTION 1: The Anti-Pattern — Public Fields

**[ACTION]** Scroll to `UnsafeBankAccount` and then to Section 1 in main.

"Here's a class with ALL fields public. Let me show you the disaster this enables:"

```java
hackedAccount.balance = 999999.99;   // direct write — no validation
hackedAccount.isActive = false;      // lock the account from outside
hackedAccount.ownerName = "";        // invalid name — accepted silently
```

"Three lines. The entire integrity of the account object is destroyed. No constructor, no method, no rule was violated. Java happily allowed it because the fields are public."

"This is why the rule exists: if external code can write directly to your fields, you have zero control over the state of your objects."

→ **TRANSITION:** "Now let's look at the fix."

---

### SECTION 2: Encapsulation with Private Fields

**[ACTION]** Scroll to `SafeBankAccount`.

"Same concept — a bank account — but now every field is `private`:"

```java
private String ownerName;
private String accountNumber;
private double balance;
private boolean isActive;
private int failedWithdrawalAttempts;
```

"There is no way — zero — for external code to touch `balance` directly. The only paths in are `deposit()` and `withdraw()`, both of which have validation."

**[ACTION]** Scroll to Section 2 in main. Point to the commented-out lines:

```java
// alice.balance = 999999.99;   // ← compile error: balance has private access in SafeBankAccount
// alice.isActive = false;      // ← compile error: isActive has private access in SafeBankAccount
```

"This is a compile error — not a runtime error. The mistake is caught before the program even runs."

**[ACTION]** Now point to the auto-lock feature in `withdraw()`:

```java
failedWithdrawalAttempts++;
if (failedWithdrawalAttempts >= 3) {
    isActive = false;
}
```

"Notice `failedWithdrawalAttempts` — this field has NO getter. External code can't even read it. It's purely internal bookkeeping. The account locked itself after 3 failed attempts and nobody from outside could have prevented it — or exploited it."

⚠️ **WATCH OUT:** "Some students make all setters public as a reflex. Ask yourself: does this field NEED to be settable from outside? `accountNumber` — should it ever change? No. So there's no setter. `balance` — should external code set it directly? No. It goes through `deposit`/`withdraw`. Only expose what external code genuinely needs."

---

### SECTION 3: Default (Package-Private)

**[ACTION]** Scroll to `PackageHelper`.

"The `formatCurrency` method has NO access modifier — so it's package-private. Any class in the same package can use it, but nothing outside the package can."

"This is great for utility helpers used internally across a module, but that you don't want to expose as public API."

```java
static String formatCurrency(double amount) { ... }    // package-private
private static String maskAccountNumber(...) { ... }   // private — only PackageHelper itself
public  static String getSafeDisplay(...)   { ... }    // public — available everywhere
```

"Three levels of visibility in one helper class. `getSafeDisplay` is public — callers use it. Internally, it calls `maskAccountNumber` (private) and `formatCurrency` (package-private)."

---

### SECTION 4: Protected — Inheritance Preview

**[ACTION]** Scroll to `Vehicle` and `ElectricCar`.

"Protected is the one that trips people up. It means: same class + same package + any subclass, even in a different package."

"Look at `Vehicle`:"

```java
private String make;           // Vehicle only — even ElectricCar can't touch this
protected int year;            // ElectricCar CAN access this directly
public String color;           // everyone
```

"Now in `ElectricCar.getSpec()`:"

```java
return String.format("... %s %s (%d) ...", getMake(), getModel(), year, ...);
```

"`year` — protected — used directly. `make` and `model` — private — must go through getters. That compile error is shown in comments in the file."

"We'll cover inheritance deeply tomorrow. For today, just know protected exists and what column it fills in the table."

→ **TRANSITION:** "Let's move to non-access modifiers: `static`, `final`, and `abstract`."

---

## FILE 2: `02-non-access-modifiers.java`

**[ACTION]** Open `02-non-access-modifiers.java`.

"Non-access modifiers don't control visibility — they control behaviour. `static` means 'belongs to the class'. `final` means 'cannot change'. `abstract` means 'must be completed by a subclass'."

---

### SECTION 1: static fields

**[ACTION]** Scroll to `BankAccount` fields.

"Look at the fields:"

```java
private static int    totalAccounts    = 0;    // ONE copy, shared by ALL instances
private static double totalDepositsAll = 0.0;  // ONE copy
public  static final double INTEREST_RATE = 0.025;  // constant

private final String accountNumber;    // EACH instance has its own
private String ownerName;              // EACH instance has its own
```

"Static fields live at the class level — not inside any object. Think of them as data that belongs to ALL bank accounts collectively."

**[ACTION]** Scroll to Section 1 in main.

"Before any accounts exist, `getTotalAccounts()` returns 0. Watch the counter as we create three accounts:"

```java
BankAccount alice = new BankAccount("Alice Johnson", 1500.00);
// totalAccounts → 1

BankAccount bob   = new BankAccount("Bob Martinez", 800.00);
// totalAccounts → 2

BankAccount carol = new BankAccount("Carol Williams", 2200.00);
// totalAccounts → 3
```

"Every constructor call increments the SAME shared counter. There is only ONE `totalAccounts` in memory, no matter how many BankAccount objects exist."

**[ASK]** "If I call `alice.getTotalAccounts()` vs `BankAccount.getTotalAccounts()`, what's the difference?"

"The result is the same — but the `ClassName.method()` form is preferred for static methods because it makes clear you're calling a class-level, not instance-level, operation."

---

### The static vs instance member Table

**[ACTION]** Scroll to the comments table around Section 3 in main.

"Here's the mental model — commit this:"

```
               │  STATIC member           │  INSTANCE member
───────────────┼──────────────────────────┼───────────────────────────
Belongs to     │ The CLASS itself         │ A specific OBJECT
Copies         │ One (shared by all)      │ One per object
How to call    │ ClassName.method()       │ object.method()
Has 'this'?    │ No                       │ Yes
Can access     │ Only other static        │ Both static and instance
```

"The big rule: from inside a `static` method, you CANNOT access instance fields. There's no `this`. Show the compile error comment:"

```java
// public static void badMethod() {
//     System.out.println(balance);  // compile error: non-static variable balance
// }
```

---

### final fields

**[ACTION]** Scroll back up to the constructor.

"The `accountNumber` field is `final`:"

```java
private final String accountNumber;
```

"It's set in the constructor and can NEVER be changed after that. If you try: compile error. This is perfect for things that are assigned once and should be immutable for the life of the object: IDs, serial numbers, timestamps."

⚠️ **WATCH OUT:** "Don't confuse `static final` (class-level constant, ALL_CAPS by convention) with plain `final` (instance-level, set once per object). `INTEREST_RATE` is `static final` — one copy, never changes. `accountNumber` is just `final` — each object has its own, but theirs doesn't change."

---

### SECTION 3: final methods and final classes

**[ACTION]** Scroll to `Shape` and then to `ImmutablePoint`.

"A `final` method cannot be overridden by a subclass. In `Shape`:"

```java
public final String getShapeType() { return shapeType; }
```

"If `ElectricCar` tried to override this in `Circle`, compile error. Use `final` on methods when the behavior must be guaranteed — when it would be dangerous or nonsensical for a subclass to replace it."

"A `final` class cannot be extended AT ALL. `ImmutablePoint` is `final` — nobody can subclass it. Java's own `String` class is final. It was a deliberate security decision: you can't create a 'special' String that secretly leaks characters."

---

### SECTION 4: Utility Class Pattern

**[ACTION]** Scroll to `MathUtils` and Section 5 in main.

"A utility class contains only static methods and constants. You never create an instance of it — you call methods directly on the class name, just like `Math.sqrt()`."

```java
MathUtils.circleArea(5)
MathUtils.isPrime(17)
MathUtils.clamp(150, 0, 100)
```

"The class is `final` (can't be subclassed) and the constructor is `private` (throws an exception if called). Belt and suspenders — you can't accidentally instantiate it."

---

### SECTION 5: abstract — Preview

**[ACTION]** Scroll to `Animal`, `Dog`, `Cat`, and Section 6 in main.

"Quick preview — we go deep on this tomorrow. An `abstract` class cannot be instantiated directly:"

```java
// Animal animal = new Animal("Generic");  // compile error: Animal is abstract
```

"An `abstract` method has no body — it's a promise that subclasses must fulfill:"

```java
public abstract String makeSound();
```

"`Dog` must implement `makeSound()`. `Cat` must implement `makeSound()`. If a subclass doesn't, it also becomes abstract."

"But `sleep()` — that's a concrete method in `Animal`. All subclasses inherit it for free. Abstract classes let you share common behavior while forcing subclasses to fill in the unique parts. More tomorrow."

→ **TRANSITION:** "One more topic — the `this` keyword. You've seen `this.field` a hundred times today. Let's be precise about what `this` actually is and all the things you can do with it."

---

## FILE 3: `03-this-keyword.java`

**[ACTION]** Open `03-this-keyword.java`.

"`this` is a reference to the **current object** — the object on which the method or constructor is currently executing. It's automatically provided by Java inside every non-static method and constructor."

---

### SECTION 1: this.field — Name Disambiguation

**[ACTION]** Scroll to the `Employee` constructor.

"Look at the parameters: `firstName`, `lastName`, `salary`... and the fields have the exact same names. Inside the constructor, which `firstName` is which?"

"Java's scoping rules say: the local scope (parameter) takes priority. So `firstName` alone refers to the PARAMETER. To refer to the FIELD, you must say `this.firstName`."

```java
this.firstName = firstName;   // field ← parameter
this.lastName  = lastName;
this.salary    = salary;
```

**[ASK]** "What would happen if we accidentally wrote `firstName = firstName`?"

"The parameter is assigned to itself — a no-op. The field is never updated. The object would have whatever the default value is for a String: `null`. This is a real bug people write."

"Same pattern in setters. Whenever the parameter name matches the field name, you need `this.`."

**[ACTION]** Scroll to `calculateAnnualBonus()`:

```java
double baseBonus = salary * 0.10;
```

"Here the local variable is named `baseBonus` — different from any field. So `salary` unambiguously refers to the instance field. No `this.` needed — but adding it wouldn't be wrong, just redundant."

---

### SECTION 2: this() — Constructor Chaining

**[ACTION]** Scroll to the `Product` constructors.

"You saw this in Part 1's `02-constructors.java`. Here's the same pattern with a 5-parameter master constructor and four shorter ones all delegating to it with `this()`."

**[ACTION]** Run the Section 2 demo — watch all five constructors print their trace.

"Every single constructor — regardless of how many arguments — ends up at the 5-param master. The logic lives in ONE place. If you need to change initialization behavior, you change it once."

"The rule bears repeating: `this()` MUST be the absolute first statement in a constructor. Show the compile-error comment:"

```java
// public Product(String name, double price) {
//     System.out.println("Creating...");   // NOPE — statement before this()
//     this(name, "General", price, 0, false);  // compile error
// }
```

---

### SECTION 3: this as Method Argument

**[ACTION]** Scroll to `SavingsAccount.transfer()`.

"Sometimes you need to pass the current object to another method. You do that by passing `this` as an argument."

```java
public void transfer(SavingsAccount destination, double amount) {
    ...
    logTransfer(this, destination, amount);
    //          ^^^^ the SavingsAccount we're currently executing on
}

private static void logTransfer(SavingsAccount from, SavingsAccount to, double amount) {
    from.logger.record(String.format("Transfer: %s → %s ...", from.ownerName, to.ownerName, ...));
}
```

"The static `logTransfer` helper needs to know both the sender and the recipient. We pass `this` to tell it 'the sender is me — the current object'."

**[ASK]** "Why is `logTransfer` static here?"

"Because it doesn't need to be called on any one account. It's a helper that takes two accounts as parameters. Making it static also prevents it from accidentally accessing `this.balance` or other instance state of the class it lives in."

**[ACTION]** Run Section 3 and show the event log output.

---

### SECTION 4: this as Return Value — Fluent Builder

**[ACTION]** Scroll to `SavingsAccountBuilder`.

"The most sophisticated use of `this`. Every 'configuration' method sets a field and then returns `this` — the same builder object:"

```java
public SavingsAccountBuilder ownerName(String ownerName) {
    this.ownerName = ownerName;
    return this;              // ← same object, now with ownerName set
}
```

"Because each method returns the same object, you can chain the calls:"

```java
BuiltAccount premium = new SavingsAccountBuilder()
    .ownerName("Frank Wilson")
    .initialBalance(10000.0)
    .interestRate(0.045)
    .minBalance(1000.0)
    .overdraftProtection(true)
    .premiumTier()
    .build();
```

"Read it top-to-bottom — it's almost English. This is the **Builder Pattern** and it's everywhere in real Java code."

"Compare to the alternative:"

```java
new SavingsAccount("Frank Wilson", 10000.0, 0.045, 1000.0, true, "Premium")
```

⚠️ **WATCH OUT:** "Six positional arguments — which slot is the interest rate? Which is the min balance? You have to count. Swap two doubles and you have a silent bug that passes compilation. The builder makes every assignment self-documenting."

"The chain ends with `.build()` — a terminal method that actually constructs the target object and returns it. The builder accumulates configuration; `build()` commits."

---

### Closing the `this` Keyword

**[ACTION]** Scroll to the summary at the bottom of main.

"Four uses — print these out:"

```
this.field    → refers to instance field (disambiguates from parameter)
this()        → calls another constructor in same class (must be line 1)
method(this)  → passes current object as argument
return this;  → enables method chaining (fluent/builder pattern)
```

---

## DAY 4 WRAP-UP

**[ACTION]** Close all code files. Turn to face the class.

"Let's close out Day 4 with a mental inventory. You now know:

**From Part 1:**
- A class is a blueprint; an object is an instance created with `new`
- Variables hold references, not objects — and two references can point to the same object
- Constructors initialize state; `this()` lets constructors delegate to each other
- Every instance field is independent per object; methods can call other methods; object composition (`Person` has an `Address`) is how you build complex models

**From Part 2:**
- `private` fields are the default for encapsulation — direct write access from outside is dangerous
- `static` members belong to the class, not any instance — one shared copy
- `final` fields are set once and never changed; `final` methods can't be overridden; `final` classes can't be extended
- `this` is always the current object — you can use it to disambiguate fields, chain constructors, pass yourself as an argument, or enable fluent method chaining

Tomorrow is Day 5 — Inheritance and Polymorphism. That `abstract` preview will become the main event. We'll also cover `extends`, `super`, method overriding, and how Java decides at runtime which version of a method to call. Good work today."

---

## SELF-CHECK ✅

- [x] Visibility table: public / protected / default / private
- [x] Why public fields are dangerous (direct write, no validation)
- [x] Encapsulation: private fields + public getters/setters + validation
- [x] Default (package-private) visibility and when to use it
- [x] protected: same package + subclasses
- [x] static fields: class-level, one shared copy
- [x] static methods: called on class name, no `this`, can't access instance members
- [x] static final: named constants, ALL_CAPS convention
- [x] final instance field: set once in constructor, immutable
- [x] final method: cannot be overridden
- [x] final class: cannot be extended
- [x] Utility class: all-static, private constructor
- [x] abstract: preview (full coverage Day 5)
- [x] this.field: name disambiguation
- [x] this(): constructor chaining (must be first statement)
- [x] this as argument: passing current object
- [x] return this: fluent/builder pattern
- [x] Learning Objectives: design classes with encapsulation ✓, apply access modifiers ✓, differentiate static vs instance ✓, create and invoke objects ✓
