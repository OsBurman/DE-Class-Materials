# Exercise 04: Abstract Classes and the Template Method Pattern

## Objective
Practice defining and using abstract classes — classes that declare abstract methods subclasses must implement, while also providing shared concrete behavior.

## Background
A payment processing system handles multiple payment methods: credit cards, PayPal, and bank transfers. All payments follow the same general process (validate → process → confirm), but the specific steps differ by payment type. You'll model this using an abstract class that enforces the structure while letting subclasses fill in the details.

## Requirements

1. Create an abstract class `Payment` with:
   - A protected field `double amount`
   - A protected field `String currency`
   - A constructor that takes and assigns `amount` and `currency`
   - Getters `getAmount()` and `getCurrency()`
   - Abstract method `boolean validate()` — subclasses must implement their own validation logic
   - Abstract method `void processPayment()` — subclasses must implement how they actually process
   - Abstract method `String getPaymentType()` — returns a string like `"Credit Card"`
   - A **concrete** method `void execute()` that:
     1. Prints `"Initiating [type] payment of [amount] [currency]"`
     2. Calls `validate()` — if it returns `false`, prints `"Payment validation failed."` and returns
     3. Calls `processPayment()`
     4. Prints `"Payment complete."`

2. Create class `CreditCardPayment` extending `Payment`:
   - Additional fields: `String cardNumber`, `String cardHolder`, `int cvv`
   - Constructor takes `amount`, `currency`, `cardNumber`, `cardHolder`, `cvv`
   - Implement `getPaymentType()` → return `"Credit Card"`
   - Implement `validate()`: return `true` if `cardNumber.length() == 16` AND `cvv >= 100 && cvv <= 999`; print `"Validating credit card..."` first
   - Implement `processPayment()`: print `"Charging [amount] [currency] to card ending in [last 4 digits]"`

3. Create class `PayPalPayment` extending `Payment`:
   - Additional field: `String email`
   - Constructor takes `amount`, `currency`, `email`
   - Implement `getPaymentType()` → return `"PayPal"`
   - Implement `validate()`: return `true` if `email.contains("@")`; print `"Validating PayPal account..."` first
   - Implement `processPayment()`: print `"Sending [amount] [currency] to PayPal account [email]"`

4. Create class `BankTransferPayment` extending `Payment`:
   - Additional fields: `String bankName`, `String accountNumber`
   - Constructor takes `amount`, `currency`, `bankName`, `accountNumber`
   - Implement `getPaymentType()` → return `"Bank Transfer"`
   - Implement `validate()`: return `true` if `accountNumber.length() >= 8`; print `"Validating bank account..."` first
   - Implement `processPayment()`: print `"Transferring [amount] [currency] to [bankName] account [accountNumber]"`

5. In `main`:
   - Create one of each payment type (at least one valid, one with invalid data to trigger the failure path)
   - Call `execute()` on each and observe the template method controlling the flow
   - Demonstrate that you **cannot** instantiate `Payment` directly (add this as a comment)

## Hints
- An abstract class cannot be instantiated with `new Payment(...)` — only its concrete subclasses can
- Abstract methods have no body: `abstract boolean validate();`
- The `execute()` method in the abstract class calls `validate()` and `processPayment()` without knowing which subclass will run them — this is the Template Method pattern
- To get the last 4 characters of a string: `cardNumber.substring(cardNumber.length() - 4)`

## Expected Output

```
=== Payment Processing System ===

Initiating Credit Card payment of 150.0 USD
Validating credit card...
Charging 150.0 USD to card ending in 4242
Payment complete.

Initiating PayPal payment of 75.5 EUR
Validating PayPal account...
Sending 75.5 EUR to PayPal account user@example.com
Payment complete.

Initiating Bank Transfer payment of 500.0 GBP
Validating bank account...
Transferring 500.0 GBP to NatWest account 12345678
Payment complete.

--- Invalid payment ---
Initiating Credit Card payment of 20.0 USD
Validating credit card...
Payment validation failed.
```
