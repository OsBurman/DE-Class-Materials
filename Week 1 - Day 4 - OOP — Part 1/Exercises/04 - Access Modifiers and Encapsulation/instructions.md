# Exercise 04: Access Modifiers and Encapsulation

## Objective
Apply `public`, `private`, `protected`, and default (package-private) access modifiers to class members and enforce encapsulation through controlled getter/setter access.

---

## Background
**Access modifiers** control which code can see a class member:

| Modifier | Same class | Same package | Subclass | Everywhere |
|---|---|---|---|---|
| `private` | ✅ | ❌ | ❌ | ❌ |
| *(default)* | ✅ | ✅ | ❌ | ❌ |
| `protected` | ✅ | ✅ | ✅ | ❌ |
| `public` | ✅ | ✅ | ✅ | ✅ |

**Encapsulation** means hiding fields with `private` and exposing controlled access through `public` getters and setters. This protects the object's internal state from invalid data.

---

## Requirements

1. Create a class `Person` with **all fields `private`**:
   - `private String name`
   - `private int    age`
   - `private String email`

2. Write a `public` parameterized constructor that accepts all three values and assigns them using `this`.

3. Write `public` getters for all three fields.

4. Write `public` setters with validation:
   - `setAge(int age)` — if `age < 0` or `age > 150`, print `"Invalid age: [age]"` and do not update.
   - `setEmail(String email)` — if the email does not contain `"@"`, print `"Invalid email: [email]"` and do not update.
   - `setName(String name)` — if name is null or blank, print `"Name cannot be blank"` and do not update.

5. Add a **package-private** (no modifier) helper method `String formatForLog()` that returns `"[name]|[age]|[email]"`. This method is intentionally not `public` — only classes in the same package can call it.

6. Add a `public` `toString()` that returns `"Person{name='[name]', age=[age], email='[email]'}"`.

7. In `main`, demonstrate:
   - Valid construction and print.
   - Successful setter updates.
   - Three invalid setter attempts (bad age, bad email, blank name) — each prints an error.
   - Print the object after failed updates to show fields are unchanged.
   - Call `formatForLog()` (accessible because `main` is in the same file/package) and print the result.

---

## Hints
- `private` fields cannot be accessed as `obj.name` from outside the class — you will get a compile error.
- `String.contains("@")` returns `true` if the string contains the `@` character.
- `name == null || name.isBlank()` covers both null and whitespace-only strings (Java 11+).
- The default (package-private) modifier means you simply write nothing: `String formatForLog() { ... }`.

---

## Expected Output
```
Person{name='Carol', age=30, email='carol@example.com'}
Person{name='Carlos', age=31, email='carlos@work.com'}
Invalid age: -5
Invalid email: not-an-email
Name cannot be blank
Person{name='Carlos', age=31, email='carlos@work.com'}
Log: Carlos|31|carlos@work.com
```
