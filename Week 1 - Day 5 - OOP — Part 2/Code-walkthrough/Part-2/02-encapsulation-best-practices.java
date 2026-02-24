/**
 * DAY 5 — OOP Part 2 | Part 2, File 2
 * TOPIC: Encapsulation Best Practices
 *
 * Topics covered:
 *  - Why private fields + controlled access is not enough on its own
 *  - Immutable classes (final class, final fields, no setters, defensive copies in constructors)
 *  - Defensive copies: return copies instead of references to mutable internal objects
 *  - Tell, Don't Ask principle: push behavior into the class that has the data
 *  - Minimal surface area: only expose what external code genuinely needs
 *  - Validating state at the boundary (constructor + setters)
 *  - The Law of Demeter: don't chain deep into other objects
 *
 * Key vocabulary:
 *  - immutable       : once created, an object's state cannot change
 *  - defensive copy  : return a new copy of a mutable object rather than the original
 *  - Tell Don't Ask  : tell the object what to do; don't ask for its data and act on it externally
 *  - Law of Demeter  : a method should only call methods on its own fields, not on values returned by those fields
 */
public class EncapsulationBestPractices {

    // ==========================================================
    // SECTION A: Why simple private fields aren't always enough
    // ==========================================================

    // This class has private fields and getters — looks encapsulated.
    // But there's a subtle hole.

    static class BadOrder {
        private String  customerId;
        private java.util.List<String> items;  // mutable list!
        private double total;

        public BadOrder(String customerId, java.util.List<String> items, double total) {
            this.customerId = customerId;
            this.items      = items;      // stores THE SAME reference — not a copy
            this.total      = total;
        }

        public java.util.List<String> getItems() {
            return items;     // returns the INTERNAL list — caller can modify it!
        }

        public double getTotal() { return total; }

        @Override
        public String toString() {
            return "BadOrder{customer='" + customerId + "', items=" + items + ", total=$" + total + "}";
        }
    }

    // ==========================================================
    // SECTION B: Proper encapsulation with defensive copies
    // ==========================================================

    static class GoodOrder {
        private final String customerId;
        private final java.util.List<String> items;
        private double total;

        public GoodOrder(String customerId, java.util.List<String> items, double total) {
            if (customerId == null || customerId.isBlank()) {
                throw new IllegalArgumentException("Customer ID cannot be blank.");
            }
            if (total < 0) {
                throw new IllegalArgumentException("Total cannot be negative.");
            }
            this.customerId = customerId;
            // Defensive copy in constructor: don't trust the caller's list
            this.items      = new java.util.ArrayList<>(items);
            this.total      = total;
        }

        // Returns an UNMODIFIABLE VIEW — caller cannot add/remove
        public java.util.List<String> getItems() {
            return java.util.Collections.unmodifiableList(items);
        }

        public String getCustomerId() { return customerId; }
        public double getTotal()      { return total; }
        public int    getItemCount()  { return items.size(); }

        // Tell, Don't Ask: expose behavior, not raw data
        public boolean hasItem(String item) {
            return items.contains(item);
        }

        public double getAverageItemCost() {
            return items.isEmpty() ? 0 : total / items.size();
        }

        public void applyDiscount(double percent) {
            if (percent <= 0 || percent >= 100) {
                throw new IllegalArgumentException("Discount percent must be between 0 and 100.");
            }
            total = total * (1 - percent / 100);
        }

        @Override
        public String toString() {
            return String.format("GoodOrder{customer='%s', items=%d, total=$%.2f}",
                    customerId, items.size(), total);
        }
    }

    // ==========================================================
    // SECTION C: Immutable class
    // ==========================================================
    // An immutable class: once constructed, state never changes.
    // Requirements:
    //   1. final class (prevent subclassing)
    //   2. All fields private final
    //   3. No setters
    //   4. Defensive copies in constructor AND in getters for mutable fields
    //   5. Constructor validates all input

    static final class Money {
        private final double amount;
        private final String currency;

        public Money(double amount, String currency) {
            if (amount < 0) throw new IllegalArgumentException("Amount cannot be negative.");
            if (currency == null || currency.isBlank())
                throw new IllegalArgumentException("Currency cannot be blank.");
            this.amount   = amount;
            this.currency = currency.toUpperCase().trim();
        }

        public double getAmount()   { return amount; }
        public String getCurrency() { return currency; }

        // Instead of setters, return NEW Money objects (immutable pattern)
        public Money add(Money other) {
            if (!this.currency.equals(other.currency)) {
                throw new IllegalArgumentException(
                    "Cannot add different currencies: " + this.currency + " + " + other.currency);
            }
            return new Money(this.amount + other.amount, this.currency);
        }

        public Money subtract(Money other) {
            if (!this.currency.equals(other.currency)) {
                throw new IllegalArgumentException("Currency mismatch.");
            }
            if (other.amount > this.amount) {
                throw new IllegalArgumentException("Result would be negative.");
            }
            return new Money(this.amount - other.amount, this.currency);
        }

        public Money multiply(double factor) {
            if (factor < 0) throw new IllegalArgumentException("Factor cannot be negative.");
            return new Money(this.amount * factor, this.currency);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof Money)) return false;
            Money other = (Money) obj;
            return Double.compare(this.amount, other.amount) == 0
                    && this.currency.equals(other.currency);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(amount, currency);
        }

        @Override
        public String toString() {
            return String.format("%s %.2f", currency, amount);
        }
    }

    // ==========================================================
    // SECTION D: Tell, Don't Ask
    // ==========================================================

    // ── BAD: Ask style ────────────────────────────────────────
    // External code reaches in, checks data, and acts on it.
    // The logic that SHOULD be in ShoppingCart is scattered everywhere.

    static class ShoppingCart {
        private java.util.List<Double> itemPrices = new java.util.ArrayList<>();
        private String membershipTier;

        public ShoppingCart(String membershipTier) {
            this.membershipTier = membershipTier;
        }

        public void addItem(double price) { itemPrices.add(price); }

        // TELL style methods — push the decision INTO the cart
        public double calculateTotal() {
            double subtotal = itemPrices.stream().mapToDouble(Double::doubleValue).sum();
            // Logic lives HERE in the class that has the data
            if ("GOLD".equals(membershipTier))       return subtotal * 0.85;  // 15% off
            if ("SILVER".equals(membershipTier))     return subtotal * 0.90;  // 10% off
            return subtotal;
        }

        public boolean isEligibleForFreeShipping() {
            return calculateTotal() >= 50.0;
        }

        public String getOrderSummary() {
            return String.format("Cart: %d items | Tier: %s | Total: $%.2f | Free shipping: %b",
                    itemPrices.size(), membershipTier, calculateTotal(), isEligibleForFreeShipping());
        }

        // Minimal surface area: these internal details are NOT exposed
        // getItemPrices() ← no getter for this; callers don't need the raw list
        // getMembershipTier() ← callers don't need this either (only affects price internally)
    }

    // ==========================================================
    // SECTION E: Law of Demeter (Don't talk to strangers)
    // ==========================================================
    // A method should only call methods on:
    //   1. Itself (this.method())
    //   2. Its own fields (field.method())
    //   3. Parameters passed to it
    //   4. Objects it creates locally
    // It should NOT chain deep: a.getB().getC().doSomething()

    static class Address {
        private String city;
        private String country;

        public Address(String city, String country) {
            this.city    = city;
            this.country = country;
        }

        public String getCity()    { return city; }
        public String getCountry() { return country; }

        // Convenience method — the Law of Demeter way
        public boolean isSameCountry(Address other) {
            return this.country.equals(other.country);
        }

        @Override
        public String toString() { return city + ", " + country; }
    }

    static class Customer {
        private String  name;
        private Address address;

        public Customer(String name, Address address) {
            this.name    = name;
            this.address = address;
        }

        // ❌ BAD: exposes internal Address object
        // public Address getAddress() { return address; }
        // Callers then write: customer.getAddress().getCity()  ← violation of Law of Demeter

        // ✅ GOOD: expose the information, not the object
        public String  getName()       { return name; }
        public String  getCity()       { return address.getCity(); }
        public String  getCountry()    { return address.getCountry(); }
        public boolean isInCountry(String country) {
            return address.getCountry().equals(country);
        }

        @Override
        public String toString() {
            return String.format("Customer{name='%s', address=%s}", name, address);
        }
    }

    // ==========================================================
    // MAIN — DEMONSTRATIONS
    // ==========================================================
    public static void main(String[] args) {

        System.out.println("============================================================");
        System.out.println("SECTION 1 — The mutable reference leak problem");
        System.out.println("============================================================");

        java.util.List<String> myItems = new java.util.ArrayList<>(java.util.Arrays.asList("Laptop", "Mouse"));
        BadOrder badOrder = new BadOrder("CUST-001", myItems, 350.00);
        System.out.println("Before: " + badOrder);

        // The caller modifies the original list AFTER the order was created:
        myItems.add("Keyboard");   // PROBLEM: modifies the order's internal state!
        System.out.println("After modifying original list: " + badOrder);

        // Even worse — modify via the getter:
        badOrder.getItems().add("Monitor");  // PROBLEM: getter returned the internal list!
        System.out.println("After modifying via getter   : " + badOrder);
        System.out.println();

        System.out.println("============================================================");
        System.out.println("SECTION 2 — Defensive copies fix the leak");
        System.out.println("============================================================");

        java.util.List<String> startItems = new java.util.ArrayList<>(java.util.Arrays.asList("Laptop", "Mouse"));
        GoodOrder goodOrder = new GoodOrder("CUST-002", startItems, 350.00);
        System.out.println("Before: " + goodOrder);

        // Modifying the original list no longer affects the order:
        startItems.add("Keyboard");
        System.out.println("After modifying original list: " + goodOrder + " (UNCHANGED ✅)");

        // Trying to modify via getter:
        try {
            goodOrder.getItems().add("Monitor");
        } catch (UnsupportedOperationException e) {
            System.out.println("Getter is unmodifiable — add() threw UnsupportedOperationException ✅");
        }
        System.out.println();

        System.out.println("Tell, Don't Ask methods on GoodOrder:");
        System.out.println("  Has 'Laptop'?     " + goodOrder.hasItem("Laptop"));
        System.out.println("  Avg item cost: $" + String.format("%.2f", goodOrder.getAverageItemCost()));
        goodOrder.applyDiscount(10);
        System.out.println("  After 10% discount: " + goodOrder);
        System.out.println();

        System.out.println("============================================================");
        System.out.println("SECTION 3 — Immutable class: Money");
        System.out.println("============================================================");

        Money price   = new Money(1299.99, "USD");
        Money tax     = new Money(115.00, "USD");
        Money shipping = new Money(25.00, "USD");

        // Each operation returns a NEW Money — originals are unchanged
        Money subtotal = price.add(tax);
        Money total    = subtotal.add(shipping);
        Money discounted = total.multiply(0.9);  // 10% off

        System.out.println("Price:     " + price);
        System.out.println("Tax:       " + tax);
        System.out.println("Shipping:  " + shipping);
        System.out.println("Subtotal:  " + subtotal);
        System.out.println("Total:     " + total);
        System.out.println("Discounted:" + discounted);
        System.out.println();

        // price is still $1299.99 — immutable objects cannot be changed
        System.out.println("Original price unchanged: " + price);
        System.out.println();

        // Currency mismatch
        Money euros = new Money(50.00, "EUR");
        try {
            price.add(euros);
        } catch (IllegalArgumentException e) {
            System.out.println("Currency mismatch caught: " + e.getMessage());
        }
        System.out.println();

        System.out.println("============================================================");
        System.out.println("SECTION 4 — Tell, Don't Ask (ShoppingCart)");
        System.out.println("============================================================");

        ShoppingCart goldCart = new ShoppingCart("GOLD");
        goldCart.addItem(29.99);
        goldCart.addItem(49.99);
        goldCart.addItem(14.99);
        System.out.println(goldCart.getOrderSummary());

        ShoppingCart silverCart = new ShoppingCart("SILVER");
        silverCart.addItem(19.99);
        silverCart.addItem(22.00);
        System.out.println(silverCart.getOrderSummary());

        ShoppingCart basicCart = new ShoppingCart("BASIC");
        basicCart.addItem(40.00);
        System.out.println(basicCart.getOrderSummary());
        System.out.println();

        System.out.println("Compare Tell, Don't Ask:");
        System.out.println("  BAD  (Ask):  if (cart.getMembershipTier().equals(\"GOLD\")) { total *= 0.85; }");
        System.out.println("  GOOD (Tell): cart.calculateTotal()  // logic lives inside the class");
        System.out.println();

        System.out.println("============================================================");
        System.out.println("SECTION 5 — Law of Demeter");
        System.out.println("============================================================");

        Address addr1 = new Address("New York", "USA");
        Address addr2 = new Address("Los Angeles", "USA");
        Address addr3 = new Address("London", "UK");

        Customer c1 = new Customer("Alice", addr1);
        Customer c2 = new Customer("Bob", addr2);
        Customer c3 = new Customer("Charlie", addr3);

        // ✅ GOOD: ask the Customer for the info, not the Address
        System.out.println("  " + c1.getName() + " is in: " + c1.getCity());
        System.out.println("  Same country as c2? " + c1.isInCountry(c2.getCountry()));
        System.out.println("  " + c3.getName() + " in USA? " + c3.isInCountry("USA"));

        // ❌ BAD (commented out — would require exposing getAddress()):
        // String city = customer.getAddress().getCity();   // violation
        // customer.getAddress().getCountry().toUpperCase() // violation (two hops)
        System.out.println();

        System.out.println("============================================================");
        System.out.println("ENCAPSULATION BEST PRACTICES SUMMARY");
        System.out.println("============================================================");
        System.out.println("  1. private fields + public methods (basics — you already know this)");
        System.out.println("  2. Defensive copies in constructors (don't store caller's references)");
        System.out.println("  3. Return unmodifiable views or copies from getters");
        System.out.println("  4. Validate at the boundary — constructor and setters");
        System.out.println("  5. Prefer immutable classes when state never needs to change");
        System.out.println("  6. Tell, Don't Ask — push behavior into the class with the data");
        System.out.println("  7. Law of Demeter — one dot is usually enough");
        System.out.println("  8. Minimal surface area — expose only what external code needs");
    }
}
