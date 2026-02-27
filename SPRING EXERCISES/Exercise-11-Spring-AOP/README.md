# Exercise 11 — Spring AOP (Aspect-Oriented Programming)

## 🎯 Learning Objectives
By the end of this exercise you will be able to:
- Explain the AOP concepts: **aspect**, **advice**, **pointcut**, **join point**, **weaving**
- Use `@Aspect` and `@Component` to create an aspect class
- Implement the five advice types: `@Before`, `@After`, `@AfterReturning`, `@AfterThrowing`, `@Around`
- Write pointcut expressions to target specific methods
- Measure method execution time using `@Around` with `ProceedingJoinPoint`
- Create a custom annotation to trigger AOP advice
- Understand how Spring AOP uses proxies under the hood

---

## 📋 What You're Building
A **Banking API** — accounts and transactions, with AOP for logging, auditing, and performance monitoring.

### Endpoints
| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/accounts` | Create account |
| `GET` | `/api/accounts/{id}` | Get account |
| `GET` | `/api/accounts` | List all accounts |
| `POST` | `/api/accounts/{id}/deposit` | Deposit funds |
| `POST` | `/api/accounts/{id}/withdraw` | Withdraw funds |
| `POST` | `/api/accounts/transfer` | Transfer between accounts |
| `GET` | `/api/accounts/{id}/transactions` | Get transaction history |

### What the Aspects Do
| Aspect | Advice | Trigger | Effect |
|--------|--------|---------|--------|
| `LoggingAspect` | `@Before` | Any service method | Logs method name + args |
| `LoggingAspect` | `@AfterReturning` | Any service method | Logs return value |
| `LoggingAspect` | `@AfterThrowing` | Any service method | Logs exception details |
| `PerformanceAspect` | `@Around` | Any service method | Measures execution time |
| `AuditAspect` | `@Around` | `@Audited` methods | Records who did what |

---

## 🏗️ Project Setup
```bash
cd Exercise-11-Spring-AOP/starter-code
./mvnw spring-boot:run
```

---

## 📁 File Structure
```
src/main/java/com/exercise/banking/
├── BankingApplication.java
├── annotation/
│   └── Audited.java                    ← ⭐ Custom annotation for auditing
├── aspect/
│   ├── LoggingAspect.java              ← ⭐ @Before, @AfterReturning, @AfterThrowing
│   ├── PerformanceAspect.java          ← ⭐ @Around — execution time
│   └── AuditAspect.java                ← ⭐ @Around with custom @Audited annotation
├── entity/
│   ├── Account.java
│   └── Transaction.java
├── dto/
│   ├── AccountRequest.java
│   ├── AccountResponse.java
│   ├── TransactionRequest.java
│   └── TransactionResponse.java
├── exception/
│   ├── ErrorResponse.java
│   ├── ResourceNotFoundException.java
│   ├── InsufficientFundsException.java
│   └── GlobalExceptionHandler.java
├── repository/
│   ├── AccountRepository.java
│   └── TransactionRepository.java
├── service/
│   └── BankingService.java
└── controller/
    └── BankingController.java
```

---

## ✅ TODOs

### `annotation/Audited.java`
- [ ] **TODO 1**: Create a custom `@Audited` annotation:
  - `@Target(ElementType.METHOD)`
  - `@Retention(RetentionPolicy.RUNTIME)`
  - One `String action()` default `""` attribute

### `aspect/LoggingAspect.java`
- [ ] **TODO 2**: Add `@Aspect` and `@Component` to the class
- [ ] **TODO 3**: Create a `@Before` advice that runs before any method in `com.exercise.banking.service`:
  - Pointcut: `"execution(* com.exercise.banking.service.*.*(..))"` 
  - Log: method name + arguments using `joinPoint.getSignature().getName()` and `joinPoint.getArgs()`
- [ ] **TODO 4**: Create an `@AfterReturning(pointcut="...", returning="result")` advice:
  - Log the method name + return value
- [ ] **TODO 5**: Create an `@AfterThrowing(pointcut="...", throwing="ex")` advice:
  - Log the method name + exception message

### `aspect/PerformanceAspect.java`
- [ ] **TODO 6**: Add `@Aspect` and `@Component`
- [ ] **TODO 7**: Create an `@Around` advice targeting all service methods:
  - Record start time with `System.currentTimeMillis()`
  - Call `joinPoint.proceed()` to execute the actual method
  - Calculate elapsed time
  - Log: `"[PERF] methodName completed in X ms"`
  - Return the result of `proceed()`

### `aspect/AuditAspect.java`
- [ ] **TODO 8**: Add `@Aspect` and `@Component`
- [ ] **TODO 9**: Create an `@Around` advice targeting methods annotated with `@Audited`:
  - Pointcut: `"@annotation(audited)"` with parameter `Audited audited`
  - Log: `"[AUDIT] Action=<audited.action()> Method=<name> executed at <timestamp>"`
  - Proceed and return result

### `service/BankingService.java`
- [ ] **TODO 10**: Add `@Audited(action = "DEPOSIT")` on `deposit()` method
- [ ] **TODO 11**: Add `@Audited(action = "WITHDRAW")` on `withdraw()` method
- [ ] **TODO 12**: Add `@Audited(action = "TRANSFER")` on `transfer()` method

---

## 💡 Key Concepts

### AOP Terminology
| Term | Meaning |
|------|---------|
| **Aspect** | A class containing cross-cutting logic (`@Aspect`) |
| **Advice** | The action taken at a join point (`@Before`, `@Around`, etc.) |
| **Pointcut** | Expression defining WHICH methods to intercept |
| **Join Point** | A specific method execution (in Spring AOP) |
| **Weaving** | Process of applying aspects to target objects (Spring does this at runtime via proxies) |

### Advice Types
```java
@Before("pointcut()")          // runs BEFORE method
@After("pointcut()")           // runs AFTER method (always, like finally)
@AfterReturning(pointcut="...", returning="result")  // runs on SUCCESS
@AfterThrowing(pointcut="...", throwing="ex")        // runs on EXCEPTION
@Around("pointcut()")          // wraps the method — most powerful
```

### Pointcut Expressions
```java
// All methods in a package:
execution(* com.example.service.*.*(..))

// Specific method:
execution(public String com.example.MyService.findById(Long))

// Any method named "save":
execution(* *.save(..))

// Methods with a specific annotation:
@annotation(com.example.annotation.MyAnnotation)
```

### @Around Advice Pattern
```java
@Around("execution(* com.example.service.*.*(..))")
public Object aroundAdvice(ProceedingJoinPoint pjp) throws Throwable {
    // Before
    Object result = pjp.proceed(); // actually run the method
    // After
    return result;
}
```
