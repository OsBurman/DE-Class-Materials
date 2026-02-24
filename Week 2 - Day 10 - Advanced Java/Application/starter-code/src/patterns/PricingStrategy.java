/**
 * Strategy interface for pricing.
 * TODO Task 5: Define the interface and 3 implementations in this file
 * (or split into separate files if you prefer).
 */
public interface PricingStrategy {
    double applyDiscount(double price);
}

// TODO: class RegularPricing implements PricingStrategy — no discount (return price)
// TODO: class HappyHourPricing implements PricingStrategy — 20% off (return price * 0.80)
// TODO: class LoyaltyPricing implements PricingStrategy — 10% off (return price * 0.90)
