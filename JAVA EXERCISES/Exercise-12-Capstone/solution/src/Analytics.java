import java.util.*;
import java.util.stream.*;

/** Analytics — Solution */
public class Analytics {

    public static double totalDeposited(List<Transaction> txns) {
        return txns.stream()
                .filter(t -> t.getType() == Transaction.Type.DEPOSIT)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public static List<String> topSpenders(List<Transaction> txns, int n) {
        return txns.stream()
                .filter(t -> t.getType() == Transaction.Type.WITHDRAW)
                .collect(Collectors.groupingBy(Transaction::getAccountNumber,
                        Collectors.summingDouble(Transaction::getAmount)))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(n)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public static Map<String, Long> transactionsByType(List<Transaction> txns) {
        return txns.stream()
                .collect(Collectors.groupingBy(t -> t.getType().toString(), Collectors.counting()));
    }

    public static String getAccountReport(Collection<Account> accounts) {
        return accounts.stream()
                .sorted(Comparator.comparing(Account::getOwnerName))
                .map(Account::toString)
                .collect(Collectors.joining("\n"));
    }
}
