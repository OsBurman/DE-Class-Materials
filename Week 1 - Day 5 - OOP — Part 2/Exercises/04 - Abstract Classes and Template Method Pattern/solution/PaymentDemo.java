// Abstract class — defines the payment contract and template execution flow
// Cannot be instantiated directly; subclasses must implement the three abstract methods
abstract class Payment {
    protected double amount;
    protected String currency;

    public Payment(double amount, String currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public double getAmount()   { return amount; }
    public String getCurrency() { return currency; }

    // Abstract methods — no body; every subclass MUST provide an implementation
    public abstract boolean validate();
    public abstract void processPayment();
    public abstract String getPaymentType();

    // Template method — defines the algorithm skeleton; calls abstract methods at the right points
    // Subclasses never override execute() itself; they just fill in the abstract steps
    public void execute() {
        System.out.println("Initiating " + getPaymentType() + " payment of " + amount + " " + currency);
        if (!validate()) {
            System.out.println("Payment validation failed.");
            return;  // stop here — do not process
        }
        processPayment();
        System.out.println("Payment complete.");
    }
}

// Credit card — validates card number length and CVV range
class CreditCardPayment extends Payment {
    private String cardNumber;
    private String cardHolder;
    private int cvv;

    public CreditCardPayment(double amount, String currency,
                              String cardNumber, String cardHolder, int cvv) {
        super(amount, currency);
        this.cardNumber = cardNumber;
        this.cardHolder = cardHolder;
        this.cvv = cvv;
    }

    @Override
    public String getPaymentType() { return "Credit Card"; }

    @Override
    public boolean validate() {
        System.out.println("Validating credit card...");
        // Card must be exactly 16 digits and CVV must be a 3-digit number
        return cardNumber.length() == 16 && cvv >= 100 && cvv <= 999;
    }

    @Override
    public void processPayment() {
        // Only show last 4 digits for security
        String lastFour = cardNumber.substring(cardNumber.length() - 4);
        System.out.println("Charging " + amount + " " + currency + " to card ending in " + lastFour);
    }
}

// PayPal — minimal validation: just check the email looks valid
class PayPalPayment extends Payment {
    private String email;

    public PayPalPayment(double amount, String currency, String email) {
        super(amount, currency);
        this.email = email;
    }

    @Override
    public String getPaymentType() { return "PayPal"; }

    @Override
    public boolean validate() {
        System.out.println("Validating PayPal account...");
        return email.contains("@");
    }

    @Override
    public void processPayment() {
        System.out.println("Sending " + amount + " " + currency + " to PayPal account " + email);
    }
}

// Bank transfer — account number must be at least 8 characters
class BankTransferPayment extends Payment {
    private String bankName;
    private String accountNumber;

    public BankTransferPayment(double amount, String currency,
                                String bankName, String accountNumber) {
        super(amount, currency);
        this.bankName = bankName;
        this.accountNumber = accountNumber;
    }

    @Override
    public String getPaymentType() { return "Bank Transfer"; }

    @Override
    public boolean validate() {
        System.out.println("Validating bank account...");
        return accountNumber.length() >= 8;
    }

    @Override
    public void processPayment() {
        System.out.println("Transferring " + amount + " " + currency
                + " to " + bankName + " account " + accountNumber);
    }
}

public class PaymentDemo {
    public static void main(String[] args) {
        System.out.println("=== Payment Processing System ===\n");

        // Valid credit card (16-digit number, valid CVV)
        Payment card = new CreditCardPayment(150.0, "USD", "1234567890124242", "Alice Smith", 456);
        card.execute();

        System.out.println();

        // Valid PayPal
        Payment paypal = new PayPalPayment(75.5, "EUR", "user@example.com");
        paypal.execute();

        System.out.println();

        // Valid bank transfer
        Payment bank = new BankTransferPayment(500.0, "GBP", "NatWest", "12345678");
        bank.execute();

        System.out.println();
        System.out.println("--- Invalid payment ---");

        // Invalid credit card — cardNumber is only 4 digits, so validate() returns false
        Payment badCard = new CreditCardPayment(20.0, "USD", "1234", "Bob", 999);
        badCard.execute();

        // This would NOT compile — Payment is abstract:
        // Payment p = new Payment(100.0, "USD");
    }
}
