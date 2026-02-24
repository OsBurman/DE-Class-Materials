import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.TreeMap;
import java.util.Map;

public class MapDemo {
    public static void main(String[] args) {

        // ===================== PART 1: HashMap =====================
        System.out.println("=== HashMap (stock) ===");

        HashMap<String, Integer> stockMap = new HashMap<>();
        stockMap.put("Clean Code",      5);
        stockMap.put("Effective Java",  3);
        stockMap.put("Design Patterns", 8);
        stockMap.put("Refactoring",     2);

        System.out.println("Initial map: " + stockMap);
        System.out.println("Stock of Effective Java: " + stockMap.get("Effective Java"));
        // getOrDefault avoids a null return when the key is missing
        System.out.println("Unknown Book stock (default): " + stockMap.getOrDefault("Unknown Book", 0));

        // put() overwrites if key already exists — no separate "update" method needed
        stockMap.put("Clean Code", 7);
        stockMap.remove("Refactoring");
        System.out.println("After updating Clean Code to 7 and removing Refactoring:");
        System.out.println(stockMap);
        System.out.println("Contains key Design Patterns: " + stockMap.containsKey("Design Patterns"));
        System.out.println("Contains value 3: " + stockMap.containsValue(3));
        System.out.println("Size: " + stockMap.size());

        System.out.println();

        // ===================== PART 2: LinkedHashMap =====================
        System.out.println("=== LinkedHashMap (prices, insertion order) ===");

        LinkedHashMap<String, Double> priceMap = new LinkedHashMap<>();
        priceMap.put("Clean Code",      39.99);
        priceMap.put("Effective Java",  49.99);
        priceMap.put("Design Patterns", 44.99);
        priceMap.put("Refactoring",     34.99);
        System.out.println(priceMap);   // insertion order guaranteed

        System.out.println();

        // ===================== PART 3: TreeMap =====================
        System.out.println("=== TreeMap (sorted by key) ===");

        TreeMap<String, Integer> sortedStock = new TreeMap<>();
        sortedStock.put("Clean Code",      5);
        sortedStock.put("Effective Java",  3);
        sortedStock.put("Design Patterns", 8);
        sortedStock.put("Refactoring",     2);
        System.out.println(sortedStock);           // keys sorted alphabetically
        System.out.println("First key: " + sortedStock.firstKey());
        System.out.println("Last key: "  + sortedStock.lastKey());

        System.out.println();

        // ===================== PART 4: Iterating stockMap =====================
        System.out.println("=== Iterating stockMap ===");

        // keySet() — iterate over keys only
        System.out.print("Keys: ");
        for (String key : stockMap.keySet()) {
            System.out.print(key + " ");
        }
        System.out.println();

        // values() — iterate over values only
        System.out.print("Values: ");
        for (int val : stockMap.values()) {
            System.out.print(val + " ");
        }
        System.out.println();

        // entrySet() — most efficient way to iterate both key and value together
        System.out.println("Entries:");
        for (Map.Entry<String, Integer> entry : stockMap.entrySet()) {
            System.out.println("  " + entry.getKey() + " = " + entry.getValue());
        }
    }
}
