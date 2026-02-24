import java.util.Arrays;

// ── Sorting Strategy ──────────────────────────────────────────────────────────

interface SortStrategy {
    void sort(int[] arr);
}

class BubbleSortStrategy implements SortStrategy {
    @Override
    public void sort(int[] arr) {
        // TODO: implement bubble sort
        // Hint: nested loops; if arr[j] > arr[j+1], swap them
    }
}

class SelectionSortStrategy implements SortStrategy {
    @Override
    public void sort(int[] arr) {
        // TODO: implement selection sort
        // Hint: for each i, find the index of the minimum in arr[i..n-1]
        //       then swap arr[i] with arr[minIndex]
    }
}

class InsertionSortStrategy implements SortStrategy {
    @Override
    public void sort(int[] arr) {
        // TODO: implement insertion sort
        // Hint: for each i starting at 1, pick arr[i] as 'key';
        //       shift elements to the right while they are greater than key
    }
}

// Context class for sorting
class Sorter {
    private SortStrategy strategy;

    public void setStrategy(SortStrategy s) {
        // TODO: assign the strategy
    }

    public void sort(int[] arr) {
        // TODO: delegate to strategy.sort(arr)
    }
}

// ── Discount Strategy ─────────────────────────────────────────────────────────

interface DiscountStrategy {
    double apply(double price);
}

class NoDiscountStrategy implements DiscountStrategy {
    @Override
    public double apply(double price) {
        // TODO: return price unchanged
        return 0;
    }
}

class PercentageDiscountStrategy implements DiscountStrategy {
    private final double percent;

    public PercentageDiscountStrategy(double percent) {
        this.percent = percent;
    }

    @Override
    public double apply(double price) {
        // TODO: return price reduced by percent %
        // Formula: price * (1 - percent / 100)
        return 0;
    }
}

class FlatDiscountStrategy implements DiscountStrategy {
    private final double amount;

    public FlatDiscountStrategy(double amount) {
        this.amount = amount;
    }

    @Override
    public double apply(double price) {
        // TODO: return price minus amount, but never below 0
        // Hint: Math.max(0, price - amount)
        return 0;
    }
}

// Context class for pricing
class PriceCalculator {
    private DiscountStrategy strategy;

    public void setStrategy(DiscountStrategy s) {
        // TODO: assign the strategy
    }

    public double calculate(double price) {
        // TODO: delegate to strategy.apply(price)
        return 0;
    }
}

// ── Main ──────────────────────────────────────────────────────────────────────

public class StrategyDemo {
    public static void main(String[] args) {

        // ── Part 1: Sorting ──
        System.out.println("=== Strategy: Sorting ===");

        int[] original = {5, 2, 9, 1, 7};
        Sorter sorter = new Sorter();

        // TODO: Sort with BubbleSortStrategy, print result
        // TODO: Sort with SelectionSortStrategy, print result
        // TODO: Sort with InsertionSortStrategy, print result
        // Remember to reset/copy the array before each sort!

        // ── Part 2: Discount ──
        System.out.println("\n=== Strategy: Discount ===");
        double price = 120.00;
        PriceCalculator calc = new PriceCalculator();

        // TODO: apply NoDiscountStrategy, print formatted result
        // TODO: apply PercentageDiscountStrategy(20), print result
        // TODO: apply FlatDiscountStrategy(30), print result
    }
}
