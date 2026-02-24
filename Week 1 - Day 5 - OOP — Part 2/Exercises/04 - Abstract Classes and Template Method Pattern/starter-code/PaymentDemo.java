// Abstract base class — defines the payment contract and execution template
abstract class Payment {
    // TODO: Declare protected fields: double amount, String currency

    // TODO: Write a constructor that takes amount and currency and assigns them

    // TODO: Write getters getAmount() and getCurrency()

    // TODO: Declare abstract method: boolean validate()
    //       (abstract methods have no body — just the signature ending with a semicolon)

    // TODO: Declare abstract method: void processPayment()

    // TODO: Declare abstract method: String getPaymentType()

    // TODO: Write a CONCRETE method: void execute()
    //       It should:
    //       1. Print "Initiating [getPaymentType()] payment of [amount] [currency]"
    //       2. Call validate() — if it returns false, print "Payment validation failed." and return
    //       3. Call processPayment()
    //       4. Print "Payment complete."
}

// Credit card implementation
class CreditCardPayment extends Payment {
    // TODO: Declare private fields: String cardNumber, String cardHolder, int cvv

    // TODO: Constructor takes amount, currency, cardNumber, cardHolder, cvv
    //       Call super(amount, currency) first

    // TODO: Implement getPaymentType() — return "Credit Card"

    // TODO: Implement validate():
    //       Print "Validating credit card..."
    //       Return true if cardNumber.length() == 16 AND cvv is between 100 and 999 (inclusive)

    // TODO: Implement processPayment():
    //       Print "Charging [amount] [currency] to card ending in [last 4 digits of cardNumber]"
    //       Tip: cardNumber.substring(cardNumber.length() - 4)
}

// PayPal implementation
class PayPalPayment extends Payment {
    // TODO: Declare private field: String email

    // TODO: Constructor takes amount, currency, email
    //       Call super(amount, currency) first

    // TODO: Implement getPaymentType() — return "PayPal"

    // TODO: Implement validate():
    //       Print "Validating PayPal account..."
    //       Return true if email.contains("@")

    // TODO: Implement processPayment():
    //       Print "Sending [amount] [currency] to PayPal account [email]"
}

// Bank transfer implementation
class BankTransferPayment extends Payment {
    // TODO: Declare private fields: String bankName, String accountNumber

    // TODO: Constructor takes amount, currency, bankName, accountNumber
    //       Call super(amount, currency) first

    // TODO: Implement getPaymentType() — return "Bank Transfer"

    // TODO: Implement validate():
    //       Print "Validating bank account..."
    //       Return true if accountNumber.length() >= 8

    // TODO: Implement processPayment():
    //       Print "Transferring [amount] [currency] to [bankName] account [accountNumber]"
}

public class PaymentDemo {
    public static void main(String[] args) {
        System.out.println("=== Payment Processing System ===\n");

        // TODO: Create a CreditCardPayment: 150.0, "USD", "1234567890124242", "Alice Smith", 456
        //       Call execute() on it

        System.out.println();

        // TODO: Create a PayPalPayment: 75.5, "EUR", "user@example.com"
        //       Call execute() on it

        System.out.println();

        // TODO: Create a BankTransferPayment: 500.0, "GBP", "NatWest", "12345678"
        //       Call execute() on it

        System.out.println();
        System.out.println("--- Invalid payment ---");

        // TODO: Create a CreditCardPayment with an invalid card (cardNumber too short, e.g., "1234"):
        //       amount 20.0, "USD", "1234", "Bob", 999
        //       Call execute() — watch validation fail

        // NOTE: You CANNOT write: Payment p = new Payment(...) — Payment is abstract!
        // Uncomment the line below to see the compile error:
        // Payment p = new Payment(100.0, "USD");
    }
}
