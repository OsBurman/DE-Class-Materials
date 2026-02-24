import java.util.Arrays;

// ── Sorting Strategy ──────────────────────────────────────────────────────────

interface SortStrategy {
    void sort(int[] arr);
}

class BubbleSortStrategy implements SortStrategy {
    @Override
    public void sort(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - 1 - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    int tmp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = tmp;
                }
            }
        }
    }
}

class SelectionSortStrategy implements SortStrategy {
    @Override
    public void sort(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < n; j++) {
                if (arr[j] < arr[minIdx]) minIdx = j;
            }
            int tmp = arr[minIdx];
            arr[minIdx] = arr[i];
            arr[i] = tmp;
        }
    }
}

class InsertionSortStrategy implements SortStrategy {
    @Override
    public void sort(int[] arr) {
        int n = arr.length;
        for (int i = 1; i < n; i++) {
            int key = arr[i];
            int j = i - 1;
            while (j >= 0 && arr[j] > key) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }
}

// Context
class Sorter {
    private SortStrategy strategy;

    public void setStrategy(SortStrategy s) {
        this.strategy = s;
    }

    public void sort(int[] arr) {
        strategy.sort(arr);
    }
}

// ── Discount Strategy ─────────────────────────────────────────────────────────

interface DiscountStrategy {
    double apply(double price);
}

class NoDiscountStrategy implements DiscountStrategy {
    @Override
    public double apply(double price) {
        return price;
    }
}

class PercentageDiscountStrategy implements DiscountStrategy {
    private final double percent;

    public PercentageDiscountStrategy(double percent) {
        this.percent = percent;
    }

    @Override
    public double apply(double price) {
        return price * (1 - percent / 100);
    }
}

class FlatDiscountStrategy implements DiscountStrategy {
    private final double amount;

    public FlatDiscountStrategy(double amount) {
        this.amount = amount;
    }

    @Override
    public double apply(double price) {
        return Math.max(0, price - amount);
    }
}

// Context
class PriceCalculator {
    private DiscountStrategy strategy;

    public void setStrategy(DiscountStrategy s) {
        this.strategy = s;
    }

    public double calculate(double price) {
        return strategy.apply(price);
    }
}

// ── Main ──────────────────────────────────────────────────────────────────────

public class StrategyDemo {
    public static void main(String[] args) {

        // ── Part 1: Sorting ──
        System.out.println("=== Strategy: Sorting ===");
        int[] original = {5, 2, 9, 1, 7};
        Sorter sorter = new Sorter();

        int[] copy1 = Arrays.copyOf(original, original.length);
        sorter.setStrategy(new BubbleSortStrategy());
        sorter.sort(copy1);
        System.out.printf("BubbleSortStrategy:    %s%n", Arrays.toString(copy1));

        int[] copy2 = Arrays.copyOf(original, original.length);
        sorter.setStrategy(new SelectionSortStrategy());
        sorter.sort(copy2);
        System.out.printf("SelectionSortStrategy: %s%n", Arrays.toString(copy2));

        int[] copy3 = Arrays.copyOf(original, original.length);
        sorter.setStrategy(new InsertionSortStrategy());
        sorter.sort(copy3);
        System.out.printf("InsertionSortStrategy: %s%n", Arrays.toString(copy3));

        // ── Part 2: Discount ──
        System.out.println("\n=== Strategy: Discount ===");
        double price = 120.00;
        PriceCalculator calc = new PriceCalculator();

        calc.setStrategy(new NoDiscountStrategy());
        System.out.printf("No discount:     $%.2f%n", calc.calculate(price));

        calc.setStrategy(new PercentageDiscountStrategy(20));
        System.out.printf("20%% off:         $%.2f%n", calc.calculate(price));

        calc.setStrategy(new FlatDiscountStrategy(30));
        System.out.printf("$30 flat off:    $%.2f%n", calc.calculate(price));
    }
}
