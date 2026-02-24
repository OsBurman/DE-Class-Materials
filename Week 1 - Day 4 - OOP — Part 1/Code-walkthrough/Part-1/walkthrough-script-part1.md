# Walkthrough Script — Part 1
## Day 4: OOP — Part 1
### Files: `01-classes-and-objects.java` · `02-constructors.java` · `03-class-members.java`

---

## OVERVIEW (Before Opening Any File)

**[ACTION]** Have the three Part-1 files visible in the file explorer. Don't open any yet.

"Good morning! Welcome to Day 4 — Object-Oriented Programming. This is the most important week of the entire course. Everything you build for the rest of the program — Spring services, REST APIs, database entities — is built on the foundation we're laying today.

So far, you've been writing procedural code: one main method, a sequence of statements, arrays of primitives. Today we shift perspective entirely. We're going to start modeling the real world as objects — things that have both data and behavior bundled together.

By end of day you'll be defining your own types, creating objects from them, and controlling exactly who can see what inside those objects. Let's go."

---

## FILE 1: `01-classes-and-objects.java`

**[ACTION]** Open `01-classes-and-objects.java`.

"The first file establishes the core vocabulary: what is a class, what is an object, and how do they relate."

---

### The Blueprint Analogy

**[ACTION]** Scroll to the class definition of `BankAccount`.

"Think of a class as a blueprint — like an architect's floor plan for a house. The blueprint describes how many rooms there are, where the walls go, where the doors are. But the blueprint *itself* is not a house. You use the blueprint to build houses.

That's exactly what a class is: a blueprint for objects. Here's the `BankAccount` class."

"Look at the anatomy. At the top — **fields**. These are the data slots every bank account will have: `ownerName`, `accountNumber`, `balance`, `isActive`. Below that — the **constructor**, which is how we build an actual account. Below that — **methods**, which are what an account can do: deposit, withdraw, get balance."

**[ASK]** "Right now, with just this class definition, how many bank accounts exist in memory?"

"Zero. We've described what a bank account looks like — but we haven't created any. The class is just the blueprint."

---

### SECTION 1: Creating Objects with `new`

**[ACTION]** Scroll to Section 1 in `main`.

"Here's where the magic happens:"

```java
BankAccount account1 = new BankAccount("Alice Johnson", "ACC-1001", 1500.00);
BankAccount account2 = new BankAccount("Bob Martinez", "ACC-1002", 500.00);
```

"The `new` keyword allocates memory on the **heap** — an area of RAM dedicated to object storage — and calls the constructor to set up the initial state. The variable `account1` doesn't *hold* the object — it holds a **reference**, like a mailing address that tells Java where in memory the object lives."

**[ASK]** "What happens when we print `account1` directly?"

"It calls `toString()`. Without overriding `toString()`, you'd get something like `BankAccount@7852e922` — a memory address. Not useful. Always override `toString()` when you want readable output."

**[ACTION]** Show the `toString()` override:

```java
@Override
public String toString() {
    return String.format("BankAccount{owner='%s', acct='%s', balance=$%.2f, active=%b}", ...);
}
```

"The `@Override` annotation tells Java — and your IDE — that this method is replacing one from a parent class. Every Java class automatically inherits from `Object`, and `Object` has a `toString()`. We're replacing it with our own version."

---

### SECTION 2: Dot Notation

**[ACTION]** Scroll to Section 2.

"To access anything inside an object, you use the dot: `object.field` or `object.method()`."

```java
System.out.println("Owner: " + account1.ownerName);
account1.deposit(250.00);
account2.withdraw(2000.00);  // insufficient funds
```

**[ASK]** "What does `account2.withdraw(2000.00)` print? Bob only has $500."

"It prints the insufficient funds message and returns `false`. The method has validation logic — it doesn't just blindly deduct."

---

### SECTION 3: Independent Objects

**[ACTION]** Scroll to Section 3 (Students).

"Three Student objects from the same blueprint — each completely independent:"

```java
student1.updateGpa(3.8);
student2.updateGpa(2.7);
student3.updateGpa(1.9);
```

"And then:"

```java
student1.updateGpa(3.9);
// student2.gpa is unchanged
```

**[ASK]** "Why doesn't changing student1's GPA affect student2?"

"Because they are separate objects in separate memory locations. Each has its own copy of the `gpa` field. Changing one doesn't touch the others."

---

### SECTION 4: Object References

**[ACTION]** Scroll to Section 4.

"This is one of the most important concepts in Java — and one of the most misunderstood."

```java
BankAccount accountRef = account1;
accountRef.deposit(500.00);
```

"I have NOT made a copy. `accountRef` is a second variable pointing at the SAME object in memory. So when I deposit through `accountRef`, Alice's real balance changes."

**[ASK]** "Before I show the output — what does `account1.balance` print after this deposit?"

"It went up by $500 — because `account1` and `accountRef` are the same object."

```java
System.out.println("Same object? " + (account1 == accountRef));  // true
```

"The `==` operator on objects checks if two references point to the SAME memory address — not whether the contents are equal. We'll come back to `==` vs `.equals()` in depth later."

⚠️ **WATCH OUT:** "This is where students get caught. If someone gives you an object and says 'here, store this', and you modify it, the caller's object is also modified. There's no automatic cloning. We'll see later how to protect against this with copy constructors."

---

### SECTION 5: null

**[ACTION]** Scroll to Section 5.

"Every object reference can also be `null` — meaning 'no object here yet'."

```java
BankAccount emptyAccount = null;
emptyAccount.deposit(100.00);  // NullPointerException!
```

**[ASK]** "What exception does this throw?"

"`NullPointerException`. It's the most common exception in Java. Always check for null before calling methods on a reference you didn't create yourself."

---

### SECTION 6: State Change

**[ACTION]** Scroll to Section 6.

"One last important idea: objects persist their state. Watch what happens when we close account2 and then try to withdraw:"

"The `closeAccount()` method sets `isActive = false`. The `withdraw()` method checks `isActive` — if false, it refuses. This is state-dependent behavior — the same method call produces different results depending on the object's current state."

→ **TRANSITION:** "You now have the core vocabulary. Let's go deeper on one specific part of the class anatomy: constructors."

---

## FILE 2: `02-constructors.java`

**[ACTION]** Open `02-constructors.java`.

"A constructor is a special method that runs exactly once — right when you create an object with `new`. Its job is to give the object a valid initial state."

---

### No-arg Constructor (Section 1)

**[ACTION]** Scroll to the `Product()` no-arg constructor.

"The no-arg constructor takes no parameters and sets sensible defaults:"

```java
Product() {
    this.name = "Unnamed Product";
    this.category = "General";
    this.price = 0.0;
    ...
}
```

"If you write NO constructors at all in your class, Java silently provides a no-arg constructor for you — it just calls `Object()`'s constructor and that's it. The moment you write ANY constructor yourself, that automatic one is gone."

⚠️ **WATCH OUT:** "This is a gotcha. You write one parameterized constructor. You later try `new Product()` — compile error! Java no longer provides the no-arg automatically. If you want it, write it explicitly."

---

### Parameterized Constructors (Sections 2 & 3)

**[ACTION]** Scroll to the 2-param and 4-param constructors.

"Java lets you have multiple constructors — called **constructor overloading** — as long as each one has a different parameter list:"

```java
Product(String name, double price) { ... }           // 2 params
Product(String name, String category, double price, int stockQuantity) { ... }  // 4 params
```

"Java picks the right constructor at runtime based on what arguments you pass."

**[ASK]** "What's the difference between overloading and just having one constructor with more parameters?"

"Overloading is about convenience — letting callers provide only the information they have, with defaults for the rest."

---

### Constructor Chaining with `this()` (Section 4)

**[ACTION]** Scroll to the 3-param constructor.

"Here's the most important pattern in constructor design:"

```java
Product(String name, String category, double price) {
    this(name, category, price, 0);  // ← delegates to 4-param
}
```

"`this()` calls another constructor in the SAME class. The 3-param constructor doesn't duplicate logic — it just fills in the missing `stockQuantity` as 0 and hands off to the full constructor."

⚠️ **WATCH OUT:** "`this()` MUST be the FIRST statement in the constructor. You can't do any work before calling `this()`. If you try, you'll get a compile error: 'call to this must be first statement in constructor'."

"This is a good pattern to follow: write ONE full constructor with all the logic, then have all shorter constructors chain to it. Keep the complexity in one place."

---

### Copy Constructor (Section 5)

**[ACTION]** Scroll to the copy constructor.

```java
Product(Product other) {
    this(other.name, other.category, other.price, other.stockQuantity);
}
```

"A copy constructor takes another object of the same type and creates a new independent copy. Watch what happens when we modify the copy:"

```java
Product copy = new Product(original);
copy.applyDiscount(10);
copy.name = "Mechanical Keyboard (Refurb)";
```

**[ASK]** "What does `original` print after we modify `copy`?"

"Unchanged — because `new Product(original)` created a brand new object with copied values. The two are now independent."

"This is the alternative to `BankAccount accountRef = account1` — that shares. `new Product(original)` does not share."

---

### Constructor Cascade (Section 6)

**[ACTION]** Scroll to the Employee section.

"The `Employee` class shows a cascade: the 2-param constructor → chains to 3-param → chains to 5-param."

```java
Employee(String firstName, String lastName) {
    this(firstName, lastName, "Unassigned");   // → 3-param
}
Employee(String firstName, String lastName, String department) {
    this(firstName, lastName, department, 50000.0, 0);  // → 5-param
}
```

"One call to `new Employee("Chris", "Taylor")` sets up 6 fields correctly. The caller only knows two things. The class handles the rest."

→ **TRANSITION:** "Perfect. Now let's really drill into what lives inside a class — the full anatomy of fields and methods."

---

## FILE 3: `03-class-members.java`

**[ACTION]** Open `03-class-members.java`.

"Class members are the building blocks of every class: fields hold state, methods implement behavior. This file goes through all the varieties."

---

### Instance Fields

**[ACTION]** Scroll to the `Person` fields section.

"These are declared at the class level — NOT inside any method. Every Person object gets its own independent copy of all these fields."

"Contrast with local variables — declared inside a method. A local variable exists only while that method is executing. An instance field exists for the entire lifetime of the object."

⚠️ **WATCH OUT:** "Beginners sometimes declare variables inside a method and wonder why they can't access them elsewhere, or they declare them as class fields when they should be local. Rule of thumb: if it's part of the object's long-term state, it's a field. If it's just scratch space for a calculation, it's a local variable."

---

### Getters

**[ACTION]** Scroll to the getters.

"Getters provide read access to fields. Convention: `getFieldName()`, returns the field's type. For booleans, the convention is `is`: `isAdult()`, `isActive()`."

```java
String getFirstName()   { return firstName; }
boolean isAdult()       { return age >= 18; }
```

"Notice `isAdult()` — it doesn't store `isAdult` as a field. It computes the answer from `age` every time. That's a computed getter."

---

### Setters with Validation

**[ACTION]** Scroll to the setters.

"Setters provide write access — and this is where you put validation:"

```java
void setEmail(String email) {
    if (email != null && email.contains("@")) {
        this.email = email;
    } else {
        System.out.println("Invalid email format: " + email);
    }
}
```

"The caller can only set a value that passes our rules. This is **encapsulation** — the object controls its own data. We'll make this airtight with `private` in Part 2."

**[ACTION]** Scroll to Section 2 in main to demonstrate:

```java
person1.setEmail("not-an-email");  // invalid — prints error, field unchanged
person1.setAge(200);               // invalid — prints error
```

---

### Void Methods

**[ACTION]** Scroll to Section 3.

"Void methods perform actions with side effects — they change state or produce output:"

```java
void celebrateBirthday() {
    age++;
    System.out.println("Happy Birthday! You are now " + age);
}
```

"After calling this, Bob's `age` field is permanently incremented. The state of the object changed."

---

### Method Overloading

**[ACTION]** Scroll to the three `sendNotification` methods and Section 4 in main.

"Same name, different parameter lists — Java calls the right version based on the arguments:"

```java
person1.sendNotification();                                         // no-arg
person1.sendNotification("Welcome!");                              // one arg
person1.sendNotification("Order Shipped", "Your order is on the way.");  // two args
```

⚠️ **WATCH OUT:** "Overloading is resolved at COMPILE TIME based on the argument types and count. It is NOT polymorphism (that's tomorrow). If you pass an `int` where a `String` is expected, it won't match — compile error."

---

### Methods Calling Methods

**[ACTION]** Scroll to `getSummary()`.

"Methods can call other methods in the same class, and can call methods on field objects:"

```java
return String.format("[%s | Age: %d (%s) | Email: %s | Address: %s]",
        getFullName(),                   // our own method
        age,
        getAgeGroup(),                   // our own method
        email,
        homeAddress.getFormatted());     // method on a field object
```

"This is composition at work — a `Person` contains an `Address`, and we call `Address.getFormatted()` through the person's field. Clean, reusable."

---

### Computed Methods (Rectangle)

**[ACTION]** Scroll to the Rectangle class and Section 6 in main.

"A key design decision: what should be a field and what should be a computed method?"

"I store `width` and `height`. From those two facts I can calculate area, perimeter, diagonal — so I don't store those. They're methods:"

```java
double getArea()      { return width * height; }
double getPerimeter() { return 2 * (width + height); }
boolean isSquare()    { return width == height; }
```

**[ASK]** "What's the advantage of computing these rather than storing them?"

"If I change the width, the area and perimeter automatically reflect the new value. If I stored them, I'd have to remember to update all three every time width changed. Fewer bugs, less duplication."

---

### Fields vs Local Variables Summary

**[ACTION]** Scroll to Section 7.

"A quick visual comparison — instance fields like `person1.firstName` live on the heap, tied to the object for as long as the object exists. Local variables like `addr1` in this main method live on the stack and disappear when the method returns."

→ **TRANSITION:** "That's Part 1 done. You know what a class is, how constructors work, and how fields and methods are defined. After the break, Part 2 takes it further — access modifiers, static members, and the `this` keyword."

---

## SELF-CHECK ✅

- [x] Classes and objects: blueprint vs instance, class anatomy, creating with `new`, dot notation
- [x] Multiple independent objects
- [x] Object references (shared reference vs copy)
- [x] `null` and NullPointerException
- [x] State change persistence
- [x] `toString()` override
- [x] Default/no-arg constructor, parameterized constructors, constructor overloading
- [x] Constructor chaining with `this()`
- [x] Copy constructor
- [x] Constructor cascade (2 → 3 → 5 param chain)
- [x] Instance fields vs local variables
- [x] Getter methods (simple and computed)
- [x] Setter methods with validation
- [x] Void methods, methods with return values
- [x] Method overloading
- [x] Methods calling other methods, object composition
- [x] Computed/derived methods (Rectangle)
- [x] Learning Objectives: create objects ✓, invoke methods ✓, design classes ✓ (Part 2 covers encapsulation and static)
