import java.util.*;
import java.util.stream.*;

/**
 * Exercise 12 — Capstone
 * Analytics — stream-based reporting
 *
 * TODO 1: totalDeposited(List<Transaction> txns)
 * Filter by type==DEPOSIT, sum amounts with mapToDouble().sum()
 *
 * TODO 2: topSpenders(List<Transaction> txns, int n)
 * Group WITHDRAW transactions by accountNumber, summing amounts.
 * Return top N account numbers sorted by total withdrawn (descending).
 * Hint: Collectors.groupingBy + Collectors.summingDouble, then sort + limit
 *
 * TODO 3: transactionsByType(List<Transaction> txns)
 * Return Map<String, Long> where key=type.toString(), value=count.
 * Hint: Collectors.groupingBy(t -> t.getType().toString(),
 * Collectors.counting())
 *
 * TODO 4: getAccountReport(Collection<Account> accounts)
 * Return a String with one line per account, sorted by ownerName.
 * Format: "%-8s %-12s %-10s %10s%n", accountNumber, ownerName, type, "$balance"
 */
public class Analytics {

    // TODO 1
    public static double totalDeposited(List<Transaction> txns) {
        return 0.0; // TODO
    }

    // TODO 2
    public static List<String> topSpenders(List<Transaction> txns, int n) {
        return new ArrayList<>(); // TODO
    }

    // TODO 3
    public static Map<String, Long> transactionsByType(List<Transaction> txns) {
        return new HashMap<>(); // TODO
    }

    // TODO 4
    public static String getAccountReport(Collection<Account> accounts) {
        return ""; // TODO
    }
}
