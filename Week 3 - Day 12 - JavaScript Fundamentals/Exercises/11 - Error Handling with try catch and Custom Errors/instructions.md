# Exercise 11: Error Handling with try, catch, and Custom Errors

## Objective
Handle runtime errors gracefully using `try/catch/finally`, create and throw custom `Error` subclasses, and understand how errors propagate up the call stack.

## Background
Unhandled errors crash programs and give users poor experiences. JavaScript provides the `try/catch/finally` block for wrapping code that might fail, allowing you to handle or log the error and continue execution. You can also `throw` your own errors — including custom classes that extend `Error` — to signal specific failure conditions in your application.

## Requirements

1. **Basic `try/catch`:**
   - Write code that intentionally causes a `TypeError` by calling `.toUpperCase()` on `null`.
   - Wrap it in `try/catch`, catch the error, and log:
     - The error's `name` (e.g., `TypeError`)
     - The error's `message`
     - The fact that execution continued after the catch block.

2. **`finally` block:**
   - Write a function `readData(shouldFail)` that:
     - In its `try` block: if `shouldFail === true`, throws `new Error("Data read failed")`, otherwise logs `"Data read successfully"`.
     - In its `catch` block: logs `"Error caught: [message]"`.
     - In its `finally` block: **always** logs `"Cleaning up resources"` (simulating closing a file/DB connection).
   - Call it with `false` and then with `true`.

3. **Rethrowing errors:**
   - Write a function `parseAge(input)` that:
     - Tries to parse `input` as a number.
     - If the result is `NaN`, throws a `TypeError("Age must be a number")`.
     - If the result is less than 0 or greater than 150, throws a `RangeError("Age must be between 0 and 150")`.
     - Otherwise returns the number.
   - In a caller function `processAge(input)`, call `parseAge` inside a `try/catch`:
     - If the error is a `TypeError`, log `"Type problem: [message]"`.
     - If the error is a `RangeError`, log `"Range problem: [message]"`.
     - Otherwise rethrow the error.
   - Call `processAge("abc")`, `processAge(-5)`, and `processAge("25")`.

4. **Custom Error classes:**
   - Create a custom error class `ValidationError` that extends `Error`:
     - Constructor takes `message` and `field` (the field that failed validation).
     - Sets `this.name = "ValidationError"`.
     - Stores `this.field`.
   - Create a custom error class `NotFoundError` that extends `Error`:
     - Constructor takes `resource` (e.g., `"User"`).
     - Sets `this.name = "NotFoundError"` and `this.message = \`${resource} not found\``.
   - Write a function `validateUser(user)` that:
     - Throws `ValidationError("Name is required", "name")` if `user.name` is falsy.
     - Throws `ValidationError("Email must contain @", "email")` if `user.email` doesn't include `"@"`.
     - Returns `"User is valid"` otherwise.
   - Demonstrate both errors and the valid path.

5. **Error propagation:**
   - Write three functions `a()`, `b()`, `c()` where `c()` throws `new Error("thrown in c")`, `b()` calls `c()`, and `a()` calls `b()`.
   - Wrap the call to `a()` in a `try/catch` and log the caught error's message.
   - Explain in a comment that the error bubbles up through `b` and `a` until it is caught.

6. **`instanceof` type checking:**
   - Show that the custom errors pass the right `instanceof` checks:
     - `new ValidationError("test", "field") instanceof ValidationError` → true
     - `new ValidationError("test", "field") instanceof Error` → true
     - `new ValidationError("test", "field") instanceof NotFoundError` → false

## Hints
- `finally` always runs — even if `try` returns early or `catch` throws another error.
- Use `instanceof` to check which specific error type you caught before deciding how to handle or rethrow it.
- Custom error classes need `super(message)` in the constructor to set up the `Error` base properly (including `stack`).
- `error.stack` contains the full stack trace as a string — useful for debugging.

## Expected Output

```
--- basic try/catch ---
Caught TypeError: Cannot read properties of null (reading 'toUpperCase')
Execution continued after catch

--- finally ---
Data read successfully
Cleaning up resources
---
Error caught: Data read failed
Cleaning up resources

--- rethrowing ---
Type problem: Age must be a number
Range problem: Age must be between 0 and 150
processAge("25") → 25

--- custom errors ---
ValidationError on field "name": Name is required
ValidationError on field "email": Email must contain @
validateUser valid: User is valid

--- error propagation ---
Caught from a(): thrown in c

--- instanceof checks ---
validationErr instanceof ValidationError → true
validationErr instanceof Error → true
validationErr instanceof NotFoundError → false
```
