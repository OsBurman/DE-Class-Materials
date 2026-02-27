public class InsufficientFundsException extends Exception {
    private double shortfall;

    public InsufficientFundsException(double shortfall) {
        super("Insufficient funds. Short by $" + String.format("%.2f", shortfall));
        this.shortfall = shortfall;
    }

    public double getShortfall() {
        return shortfall;
    }
}
