# Walkthrough Script — Day 5, Part 2
## OOP Part 2: Abstraction, Encapsulation Best Practices & Packages

**Files covered (in order):**
1. `01-abstract-classes-and-interfaces.java`
2. `02-encapsulation-best-practices.java`
3. `03-packages-and-imports.md`

**Estimated time:** 90 minutes

---

## File 1: Abstract Classes and Interfaces
**File:** `Part-2/01-abstract-classes-and-interfaces.java`
**Time estimate:** ~35 minutes

---

### Opening — The Problem That Abstraction Solves (3 min)

[ACTION] Open the file. Scroll to the top. Don't run anything yet.

"Part 1 this morning gave us inheritance, overriding, and polymorphism. We can have a `Vehicle` variable pointing to a `Car` object. The JVM calls the right `displayInfo()` at runtime.

But inheritance alone has a problem: what should `Vehicle.displayInfo()` actually do? It printed some generic defaults. There's nothing wrong with that for a demo — but in real systems, you often have a concept that **should never be instantiated on its own**. You can't own a 'Vehicle'. You own a Honda Civic. You own a Ford F-150.

Abstract classes and interfaces let us enforce that contract. 'If you're a type of Employee, you MUST tell me how to calculate your bonus. I won't let you be instantiated if you don't.'"

---

### Section 1: The Abstract Employee Class (8 min)

[ACTION] Scroll to the `Employee` abstract class.

"Let's read the signature line: `abstract class Employee`. Two things to point out right away."

[ASK] "What do you think `abstract` on the class means? What does it prevent?"

Wait for: you can't use `new Employee(...)`.

"Exactly. And the methods marked `abstract` — look at them. No body. Just a signature and a semicolon. `abstract double calculateBonus();`. The compiler is enforcing a contract: any non-abstract class that extends Employee MUST implement these three methods. If it doesn't, the code won't compile."

[ACTION] Point to `printPayslip()` and `calculateTotalCompensation()`.

"Now look at these. They have bodies. Abstract classes can have concrete methods too — that's the key difference from interfaces. `printPayslip()` is useful to every employee type. We write it once here and everyone inherits it. It internally calls `calculateBonus()` — which it doesn't know how to compute — and Java will dynamically dispatch that call to the right subclass."

⚠️ WATCH OUT: "Notice that `printPayslip()` calls `this.calculateBonus()`. `this` here is determined at runtime — whichever concrete object this method is called on. That's the same dynamic dispatch from Part 1, working silently inside the abstract class itself."

[ACTION] Scroll to `FullTimeEmployee`.

"Here's the first concrete subclass. It must implement the three abstract methods or the file won't compile. Look at `calculateBonus()` — it's performance-rating dependent. `Contractor` calculates zero bonus — they don't get bonuses by contract. `Manager` adds a team-size component."

[ASK] "Why is it valuable to call `employee.printPayslip()` on all three types the same way, even though the bonus calculation is different for each?"

Wait for: one method, works for all types, polymorphism.

"Exactly — write the printing logic once. Define the bonus contract once. Let each type fill in the rule for itself. That's abstraction at work."

[ACTION] Scroll to the `// CANNOT instantiate` commented line in main.

"If we tried `new Employee(...)`, the compiler stops us immediately. This isn't a runtime crash — it's a compile-time guarantee. The type system is protecting us."

---

### Section 2: Interfaces — A Pure Contract (10 min)

[ACTION] Scroll to the `JsonSerializable` interface.

"Abstract classes have one limitation: a class can only extend ONE abstract class. Java doesn't allow multiple inheritance of classes. Interfaces solve that. A class can implement as many interfaces as it wants."

[ASK] "What's the difference in intent between an abstract class and an interface? How would you explain it to someone?"

Wait for answers. Guide toward: abstract class = 'is a type of'; interface = 'can do this' or 'behaves like this'.

"A good rule of thumb: abstract classes represent a **family** of related types. Interfaces represent a **capability** or **role** a type plays."

[ACTION] Point to the interface constants, abstract method, default method, and static method.

"Interfaces can now have four things: constants (implicitly `public static final`), abstract methods (no body — the contract), `default` methods (have a body — shared behavior), and `static` methods. This was added in Java 8 and Java 9 respectively."

[ACTION] Point to `default toJsonWithVersion()`.

"The default method calls `this.toJson()` — but `toJson()` is abstract. Same dynamic dispatch trick. The default method can define a behavior that uses the abstract method underneath it."

[ACTION] Point to `static wrap()`.

"Static interface methods belong to the interface itself — not to any implementing class. You call them as `JsonSerializable.wrap(...)`. They can't be overridden."

[ACTION] Scroll to `Product implements JsonSerializable, Reportable, Auditable`.

"Product plays three roles: it's serializable to JSON, it can generate reports, and it's auditable. None of these require the same base class. They're separate contracts. Multiple interface implementation lets us compose behavior horizontally."

⚠️ WATCH OUT: "If two interfaces both declare a `default` method with the same signature, the class MUST override it to resolve the ambiguity. The compiler won't let you leave it unresolved."

---

### Section 3: Polymorphism Through Interfaces (7 min)

[ACTION] Scroll to the `Discountable` interface and its three implementations.

"This is the most important pattern in the file. `Discountable` has one method: `apply(double price)`. Three completely different discount strategies implement it. None of them share a common base class. They're not even 'related' in a family sense."

[ACTION] Scroll to the array in main that holds all three discount types.

"But we can put all three in a `Discountable[]` array. Loop over them. Call `apply(100.0)` on each. The right logic runs for each one. This is the power of programming to an interface — your client code doesn't care which discount type it has. It just calls `apply()`."

[ASK] "If the business adds a 'Free Shipping' discount type next month, what changes in this code?"

Wait for: only add a new class that implements Discountable — no changes to the loop, no changes to the calling code.

"That's the Open/Closed Principle — open for extension, closed for modification. You've already seen it working in practice."

[ACTION] Scroll to the comparison table at the bottom of main.

"Let's read through this table together." [READ TABLE ALOUD] "The short version: if you have shared state and shared behavior among a family of types, use abstract class. If you're defining a capability that unrelated types might share, use interface. When in doubt, lean toward interfaces — they're more flexible."

---

→ TRANSITION: "Abstract classes and interfaces are how we define what things should do. Now let's talk about how we protect what they contain — encapsulation best practices."

---

## File 2: Encapsulation Best Practices
**File:** `Part-2/02-encapsulation-best-practices.java`
**Time estimate:** ~35 minutes

---

### Opening (2 min)

"Week 1, Day 4 introduced encapsulation — private fields, getters, setters. Today we go deeper. We'll see that simple private fields with public getters are sometimes not enough. Real encapsulation is about maintaining invariants — guarantees about your object's state that can never be violated."

---

### Section 1: The Mutable Reference Leak (10 min)

[ACTION] Scroll to `BadOrder`. Read the class carefully.

"What's the list of items in this order? It's a `List<String>`, stored as a field. Let's see the constructor."

[ACTION] Point to the constructor: `this.items = items;`

"The constructor takes an `items` list from the caller — and stores the **reference**. Not a copy. The same object in memory."

[ACTION] Scroll to the first main demo showing `BadOrder`.

"Here's the attack. We create a list, pass it to `BadOrder`. The order is created. Now we do `items.add('TAMPERED_ITEM')` on the original list variable. We never called any method on `badOrder`. We just mutated the list we already had — and the order's internal list changed too, because they point to the same object."

[ASK] "Is this a Java bug? Or a design mistake?"

Wait for: design mistake. The class didn't protect itself.

"Design mistake entirely. And there's a second attack — look at the getter: `getItems()` returns `this.items` directly. The caller can grab that reference and modify the list through the getter."

[ACTION] Scroll to `GoodOrder`.

"Two fixes. Fix one: in the constructor, `this.items = new ArrayList<>(items)`. Defensive copy. We copy the caller's data into our own fresh list. The caller can mutate their original all day — ours is untouched."

"Fix two: the getter returns `Collections.unmodifiableList(this.items)`. The caller gets a view of the list they can read but not write. If they try to call `.add()` on it, they'll get an `UnsupportedOperationException` at runtime."

⚠️ WATCH OUT: "The unmodifiable wrapper is a view, not a copy. If the internal list changes, the view reflects that change. For full defense you can return `new ArrayList<>(this.items)` from the getter instead. Use unmodifiable list when you want callers to see updates; use a copy when you want to fully isolate."

[ACTION] Scroll to the Tell-Don't-Ask methods: `hasItem()`, `getAverageItemCost()`, `applyDiscount()`.

"These three methods embody a principle called **Tell, Don't Ask**. Instead of letting the caller get the list and compute things themselves, `GoodOrder` provides the operations. The object knows its own data best — let it do the work."

[ASK] "What's the problem with exposing the raw list and having callers compute averages, check membership, apply discounts externally?"

Wait for: logic scattered everywhere, class can't enforce business rules, harder to change internals.

"Exactly. If you later switch from `ArrayList` to a `TreeSet`, every caller that iterated the list has to change. If you keep the logic inside the class, only the class changes."

---

### Section 2: Immutable Classes (10 min)

[ACTION] Scroll to the `Money` class.

"Here's a step further: not just encapsulated, but truly **immutable**. Once created, a `Money` object cannot change. Ever."

[ACTION] Point to: `final class`, `final double amount`, `final String currency`, no setters.

"Four things make this immutable: `final class` prevents subclassing (a subclass could add setters). `final` fields prevent reassignment after construction. There are no setter methods. And look at the operations — `add()`, `subtract()`, `multiply()`. They don't modify `this`. They create and return a **new** `Money` object."

[ACTION] Point to `add()`: `return new Money(this.amount + other.amount, this.currency);`

"The original money objects are untouched. This pattern is called 'value semantics' — just like how `int + int` gives you a new int, not a mutation of either operand."

[ASK] "Where have you seen immutable objects already in Java?"

Wait for: `String`. Maybe `LocalDate` if they remember it.

"`String` is the classic example. `"hello".toUpperCase()` returns a new String. `"hello"` is still `"hello"`. `LocalDate.plusDays(1)` returns a new date."

[ACTION] Point to the currency check in `add()`.

"Can't add dollars to euros. The method validates this and throws `IllegalArgumentException`. Immutable classes can still enforce business rules at construction and in their methods — they just refuse to be changed after the fact."

[ACTION] Scroll to the Money demo in main.

"Chaining: `price.add(tax).multiply(quantity)`. Each call returns a new object. The chain reads naturally. Original values are safe."

---

### Section 3: Tell-Don't-Ask and Law of Demeter (8 min)

[ACTION] Scroll to `ShoppingCart`.

"Private list of item prices. Private membership tier. Public methods: `addItem()`, `calculateTotal()`, `printReceipt()`. Notice what's NOT here: no `getItemPrices()`, no `getMembershipTier()`. This cart does not expose its internal data."

[ACTION] Point to `calculateTotal()`.

"The discount logic is inside the class. The caller just calls `cart.calculateTotal()`. They don't ask for the membership tier, check what it is, apply a discount themselves. That logic lives here, where it belongs."

[ACTION] Scroll to `Address` and `Customer`.

"Law of Demeter — 'Don't talk to strangers.' The idea: a method should only call methods on its own object, its own fields, and objects passed as parameters. It should NOT chain through multiple objects to reach data."

[ACTION] Point to the commented-out bad pattern: `customer.getAddress().getCity()`.

"This chains through two objects. Now `CustomerReportGenerator` knows about `Address`. If `Address` changes its method name, the report generator breaks. Three things coupled."

[ACTION] Point to `Customer.getCity()`.

"The fix: `Customer` delegates. `getCity()` calls `this.address.getCity()` internally. The caller just says `customer.getCity()` — one level deep. `Customer` knows about `Address`; nobody else needs to."

[ASK] "What's the real cost of violating Law of Demeter in a large codebase?"

Wait for: ripple changes, harder to refactor, tight coupling.

"The further the reach, the larger the blast radius when internals change. Keep your dependencies shallow."

[ACTION] Scroll to the best practices summary at the bottom of main.

"Let's read these eight practices." [READ THE LIST ALOUD]

"These aren't rules to memorize. They're instincts to develop. Every time you write a getter that returns a collection, ask yourself: should I be returning a defensive copy or an unmodifiable view? Every time you write a method that calls `.get()` twice in a row, ask yourself: is this Tell-Don't-Ask?"

---

→ TRANSITION: "You now know how to design individual classes well. The final topic for today gives those classes an address — how Java organizes them in a project using packages."

---

## File 3: Packages and Imports
**File:** `Part-2/03-packages-and-imports.md`
**Time estimate:** ~20 minutes

---

### Opening (2 min)

[ACTION] Open the markdown file. Scroll through it briefly so students can see the structure.

"This is a reference document, not a code demo. Packages are a file-system-level concept — you can't really show them in a single-file walkthrough. But you need to understand them before Week 5 when we start building multi-file Maven and Spring projects. Let's walk through the key ideas."

---

### Section 1: What Is a Package? (4 min)

[ACTION] Read aloud the opening paragraph and 'Why packages matter' list.

"Think of every Java project you'll build in this course. By Week 5 you'll have dozens of class files. Without packages, they all go in one folder and every name must be globally unique across your entire project. That doesn't scale."

[ASK] "Can you think of a name collision that would be a real problem? Two classes with the same name in the same project?"

Wait for examples. Guide toward: `Date` from `java.util` vs `java.sql`, `List` from `java.util` vs something custom.

"Java's standard library alone has a `Date` in `java.util` AND a `Date` in `java.sql`. Different things, same name. Packages are how they coexist."

---

### Section 2: Package Declaration and Naming (4 min)

[ACTION] Point to Section 2 in the doc. Read the code example.

"`package com.example.banking.accounts;` — that's the first line of the file. One package per file. And notice the naming: reverse domain. If your company is `acme.com`, your packages start with `com.acme`. This guarantees global uniqueness — no other company with a different domain can have the same package root."

[ACTION] Point to the folder structure table.

"The subpackages represent layers of your application. `model` holds your data classes. `service` holds business logic. `repository` handles database access. `controller` handles HTTP. You'll see this pattern every single day in Spring Boot."

⚠️ WATCH OUT: "The folder path on disk must exactly match the package declaration. `package com.example.banking.accounts;` means the file lives at `com/example/banking/accounts/` relative to your source root. The compiler enforces this."

---

### Section 3: Import Statements (5 min)

[ACTION] Point to Section 3. Read through specific imports vs wildcard.

"Specific imports are preferred in professional code. When you see `import java.util.ArrayList;` you know exactly where `ArrayList` comes from. IDE shortcuts (Cmd+Shift+O in IntelliJ, or triggered automatically in VS Code with the Java extension) handle imports for you — you'll rarely type them manually."

[ACTION] Point to the wildcard pitfall — two packages with `Date`.

"If you do `import java.util.*` AND `import java.sql.*`, now `Date` is ambiguous. The compiler can't resolve it. You have to use the fully qualified name: `java.sql.Date` or `java.util.Date` inline. Specific imports would have caught this."

[ACTION] Point to Section 4 — Static imports.

"Static imports are used heavily in tests. `assertEquals(expected, actual)` is much cleaner than `Assertions.assertEquals(expected, actual)` in every test method. You'll use these when we reach the Testing week."

---

### Section 4: `java.lang` and the Real-World Structure (4 min)

[ACTION] Point to Section 5 — `java.lang` table.

"String, System, Math, Object — you've been using all of these without an import. That's because `java.lang` is automatically imported into every Java file. Now you know why."

[ACTION] Point to Section 8 — the Spring Boot project example.

"This is what your file system will look like in Week 5. `UserService.java` imports from its own project's packages and from the standard library. The top of every file will look like this: package declaration, then imports grouped by origin."

[ACTION] Read the 'Order matters' bullet list at the bottom of the quick reference.

"IDEs enforce this order automatically. Knowing the rule helps when your IDE flags an import order warning."

---

### Packages — Closing Points (1 min)

[ASK] "What is the difference between a package-private member and a `protected` member?"

Wait for: package-private = same package only, no subclasses outside; protected = same package AND subclasses anywhere.

"Good. Table in Section 7 captures that. Save this doc — you'll refer back to it in Week 5 when your first Maven project has six packages."

---

## Part 2 Self-Check

Use these questions to verify learning before closing Day 5.

**Abstraction:**
- [ ] What's the difference between an abstract class and a concrete class?
- [ ] What does it mean for a method to be abstract?
- [ ] Can you instantiate an abstract class? Why not?
- [ ] Can an abstract class have concrete methods? Can it have constructors?
- [ ] What is the difference in intent: abstract class vs interface?
- [ ] How do you implement multiple interfaces?
- [ ] What are default methods on interfaces? Why were they added in Java 8?
- [ ] What are static interface methods?

**Encapsulation Best Practices:**
- [ ] What is a mutable reference leak? How does it happen?
- [ ] What is a defensive copy? When do you use it?
- [ ] How does `Collections.unmodifiableList()` differ from a defensive copy?
- [ ] What makes a class immutable? Name the four requirements.
- [ ] What is the Tell-Don't-Ask principle?
- [ ] What is the Law of Demeter? How does it reduce coupling?

**Packages and Imports:**
- [ ] Where in a Java file does the package declaration go?
- [ ] What is the naming convention for packages?
- [ ] What is the difference between `import java.util.List` and `import java.util.*`?
- [ ] What is a static import? When would you use it?
- [ ] Why don't you need to import `String` or `System`?
- [ ] How does the package name relate to the folder structure on disk?

---

## Day 5 Wrap-Up (5 min)

[ACTION] Write on the board or display the four OOP pillars with Day 5 completed:

```
1. Encapsulation    ✅ Day 4 (basics) + Day 5 (best practices)
2. Inheritance      ✅ Day 5 Part 1
3. Polymorphism     ✅ Day 5 Part 1
4. Abstraction      ✅ Day 5 Part 2
```

"You started the week not knowing what a class was. You end it with all four OOP pillars covered — and not just the basics. You understand defensive copies, immutability, Tell-Don't-Ask, polymorphism through interfaces, and abstract contracts.

Week 2 builds on this foundation: Collections and Generics (Day 6) will show you how the Java standard library uses every one of these patterns. Pay attention to how `List`, `Set`, and `Map` are interfaces with multiple implementations — that's the Discountable pattern scaled up."

[ASK] Final question: "Name one real-world class you could design today using what you've learned this week. What fields would it have? What abstract class or interface would it implement?"

Let 2–3 students answer. Let them talk. This solidifies the week.

---

*End of Day 5 Part 2 walkthrough script.*
