# Packages and Imports in Java

## What This File Covers

- What a package is and why it exists
- Package naming conventions
- The `package` declaration at the top of a file
- `import` statements — specific and wildcard
- Fully qualified class names (when you don't import)
- Static imports (`import static`)
- The `java.lang` package (automatically imported)
- How packages map to folder structure on disk
- Access modifier recap: how packages affect visibility

---

## 1. What Is a Package?

A **package** is a namespace — a named grouping for related classes and interfaces.

Think of it like folders on your computer. You don't put all your files in one giant folder. You organize them: `Documents/Work/2026/Reports/`. Packages do the same for Java classes.

**Why packages matter:**
1. **Organization** — group related classes together
2. **Avoiding name collisions** — two classes can both be named `Date` if they live in different packages (`java.util.Date` vs `java.sql.Date`)
3. **Access control** — `protected` and package-private (default) visibility is package-scoped

---

## 2. Package Declaration

Every `.java` file can declare its package on the **very first line** (before imports, before the class):

```java
package com.example.banking.accounts;

public class SavingsAccount {
    // ...
}
```

**Rules:**
- The `package` declaration must be the **first non-comment statement** in the file
- A file can belong to only ONE package
- If there is no `package` declaration, the class is in the **default package** — avoid this for real projects

### Package Naming Conventions

| Convention | Example |
|---|---|
| All lowercase | `com.example.banking` |
| Reverse domain name (prevents global collisions) | `com.acme.payroll.models` |
| Subpackages with dots | `com.example.app.services` |
| Don't start with `java` or `javax` — reserved | ❌ `java.myapp` |

**Typical real-world project layout:**

```
com.example.ecommerce
├── com.example.ecommerce.model        → entity/domain classes
├── com.example.ecommerce.service      → business logic
├── com.example.ecommerce.repository   → database access
├── com.example.ecommerce.controller   → HTTP endpoints (Spring MVC)
├── com.example.ecommerce.dto          → Data Transfer Objects
└── com.example.ecommerce.config       → configuration classes
```

---

## 3. Import Statements

If a class you want to use is in a **different package**, you must either:
1. **Import it** — then use the simple name
2. **Use the fully qualified name** — no import needed

### Specific imports (preferred)

```java
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
```

```java
// Now you can write:
List<String> names = new ArrayList<>();
```

### Wildcard imports (use with caution)

```java
import java.util.*;
```

"Import everything from `java.util`." This works but:
- Does NOT import sub-packages (e.g., `import java.util.*` does NOT import `java.util.concurrent.*`)
- Clutters the namespace — IDE auto-imports are always specific

### Fully qualified class name (no import required)

```java
java.util.List<String> names = new java.util.ArrayList<>();
java.time.LocalDate today = java.time.LocalDate.now();
```

Useful when two packages have a class with the same name:

```java
import java.util.Date;

// Now java.util.Date is imported, but you also need java.sql.Date:
java.sql.Date sqlDate = new java.sql.Date(System.currentTimeMillis());  // fully qualified
```

---

## 4. Static Imports

Static imports let you use static members (methods and constants) without the class name prefix:

```java
// Without static import:
System.out.println(Math.sqrt(16));
System.out.println(Math.PI);

// With static import:
import static java.lang.Math.sqrt;
import static java.lang.Math.PI;

System.out.println(sqrt(16));  // no "Math." prefix
System.out.println(PI);        // no "Math." prefix
```

Useful with constants and assertion methods in tests:

```java
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// In a test:
assertEquals(42, result);   // instead of Assertions.assertEquals(42, result)
assertTrue(list.isEmpty());
```

**Caution:** Don't overuse static imports — they can make code harder to read because you can't tell where the method came from.

---

## 5. `java.lang` — The Auto-Import

You never need to import `java.lang.*` — Java imports it automatically for every file. This package contains the most fundamental classes:

| Class | What it is |
|---|---|
| `String` | Text |
| `Integer`, `Double`, `Boolean`, etc. | Wrapper classes for primitives |
| `Object` | Root of all classes |
| `System` | `System.out.println(...)`, `System.exit(...)` |
| `Math` | `Math.sqrt()`, `Math.random()`, `Math.PI` |
| `StringBuilder` | Mutable string building |
| `Thread` | Multi-threading (Week 2) |
| `Exception`, `RuntimeException` | Exception hierarchy (Week 2) |

---

## 6. How Packages Map to Folders on Disk

**This is not optional — the folder structure must match the package declaration exactly.**

If your class declares `package com.example.banking.accounts;`, then the `.java` file MUST be at:

```
src/
└── main/
    └── java/
        └── com/
            └── example/
                └── banking/
                    └── accounts/
                        └── SavingsAccount.java   ← matches package declaration
```

This is enforced by the Java compiler. If the path doesn't match the package, you'll get a compile error.

Modern build tools (Maven, Gradle — Week 5) manage this automatically with the `src/main/java/` convention.

---

## 7. Access Modifiers and Packages — Recap

| Modifier | Same class | Same package | Subclass | Everywhere |
|---|---|---|---|---|
| `public` | ✅ | ✅ | ✅ | ✅ |
| `protected` | ✅ | ✅ | ✅ | ❌ |
| *(default)* | ✅ | ✅ | ❌ | ❌ |
| `private` | ✅ | ❌ | ❌ | ❌ |

The **default (package-private)** access level exists specifically because of packages. A class member with no modifier is visible to everything in the same package — making packages an access boundary as well as an organizational one.

---

## 8. Practical Example — Multi-Package Application Structure

Here's what a simple Spring Boot e-commerce app looks like organized into packages:

```
src/main/java/com/example/store/
│
├── model/
│   ├── Product.java          → package com.example.store.model;
│   ├── Order.java            → package com.example.store.model;
│   └── Customer.java         → package com.example.store.model;
│
├── service/
│   ├── ProductService.java   → package com.example.store.service;
│   └── OrderService.java     → package com.example.store.service;
│
├── repository/
│   ├── ProductRepository.java → package com.example.store.repository;
│   └── OrderRepository.java   → package com.example.store.repository;
│
├── controller/
│   └── ProductController.java → package com.example.store.controller;
│
└── StoreApplication.java     → package com.example.store;
```

`ProductService.java` — example of what the top of the file looks like:

```java
package com.example.store.service;

import com.example.store.model.Product;
import com.example.store.repository.ProductRepository;
import java.util.List;
import java.util.Optional;

public class ProductService {

    private ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public List<Product> getAllProducts() {
        return repository.findAll();
    }

    public Optional<Product> findById(String id) {
        return repository.findById(id);
    }
}
```

Notice the imports: `com.example.store.model.Product` is in a sibling package — you must import it. `java.util.List` and `java.util.Optional` are from the standard library.

---

## 9. Common Import Pitfalls

### Pitfall 1: Forgetting to import

```
Error: cannot find symbol
       symbol: class ArrayList
```
Cause: You used `ArrayList` but didn't import `java.util.ArrayList`.

### Pitfall 2: Wildcard + name collision

```java
import java.util.*;
import java.sql.*;

Date d = new Date();   // compile error: reference to Date is ambiguous
```
Fix: Use specific imports or fully qualified names.

### Pitfall 3: Wrong package declared

```
error: class Product is public, should be declared in a file named Product.java
```
Each public class must be in a file matching its name. And the package must match the folder path.

### Pitfall 4: Importing a class that's in `java.lang`

```java
import java.lang.String;   // unnecessary — String is always available
import java.lang.Math;     // unnecessary
```
Not harmful, just noise. Most IDEs warn and auto-clean this.

---

## 10. Quick Reference

```java
// File: src/main/java/com/example/app/service/UserService.java

package com.example.app.service;           // 1. Package declaration (line 1)

import com.example.app.model.User;          // 2. Specific import from sibling package
import java.util.List;                      // 3. Standard library import
import java.util.Optional;
import static java.util.Collections.unmodifiableList;  // 4. Static import

public class UserService {
    // ...
}
```

**Order matters (by convention):**
1. `package` declaration
2. `import` for standard Java (`java.*`, `javax.*`)
3. `import` for third-party libraries
4. `import` for your own project's classes
5. Class declaration

Modern IDEs (IntelliJ IDEA, VS Code with Java extensions) auto-manage imports for you. Understanding the rules helps you debug when the IDE gets confused.
