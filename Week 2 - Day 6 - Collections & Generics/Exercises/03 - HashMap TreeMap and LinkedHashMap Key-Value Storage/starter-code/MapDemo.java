import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.TreeMap;
import java.util.Map;

public class MapDemo {
    public static void main(String[] args) {

        // ===================== PART 1: HashMap =====================
        System.out.println("=== HashMap (stock) ===");

        // TODO: Create HashMap<String, Integer> named stockMap
        // TODO: put "Clean Code" → 5, "Effective Java" → 3, "Design Patterns" → 8, "Refactoring" → 2

        // TODO: Print "Initial map: " + stockMap
        // TODO: Print "Stock of Effective Java: " + stockMap.get("Effective Java")
        // TODO: Print "Unknown Book stock (default): " + stockMap.getOrDefault("Unknown Book", 0)

        // TODO: Update "Clean Code" stock to 7 using put()
        // TODO: Remove "Refactoring" using remove()
        // TODO: Print "After updating Clean Code to 7 and removing Refactoring:"
        // TODO: Print stockMap

        // TODO: Print "Contains key Design Patterns: " + stockMap.containsKey("Design Patterns")
        // TODO: Print "Contains value 3: " + stockMap.containsValue(3)
        // TODO: Print "Size: " + stockMap.size()

        System.out.println();

        // ===================== PART 2: LinkedHashMap =====================
        System.out.println("=== LinkedHashMap (prices, insertion order) ===");

        // TODO: Create LinkedHashMap<String, Double> named priceMap
        // TODO: put "Clean Code" → 39.99, "Effective Java" → 49.99,
        //            "Design Patterns" → 44.99, "Refactoring" → 34.99 (same order)
        // TODO: Print priceMap (insertion order should be preserved)

        System.out.println();

        // ===================== PART 3: TreeMap =====================
        System.out.println("=== TreeMap (sorted by key) ===");

        // TODO: Create TreeMap<String, Integer> named sortedStock
        // TODO: put all 4 books with their original stock values (5, 3, 8, 2)
        // TODO: Print sortedStock (keys will be alphabetically sorted)
        // TODO: Print "First key: " + sortedStock.firstKey()
        // TODO: Print "Last key: " + sortedStock.lastKey()

        System.out.println();

        // ===================== PART 4: Iterating stockMap =====================
        System.out.println("=== Iterating stockMap ===");

        // TODO: Iterate stockMap.keySet() — print "Keys: " then each key separated by space
        System.out.print("Keys: ");
        // TODO: for (String key : stockMap.keySet()) { System.out.print(key + " "); }
        System.out.println();

        // TODO: Iterate stockMap.values() — print "Values: " then each value separated by space
        System.out.print("Values: ");
        // TODO: for (int val : stockMap.values()) { System.out.print(val + " "); }
        System.out.println();

        // TODO: Iterate stockMap.entrySet() — print "Entries:" then each on its own line
        //       Format: "  [entry.getKey()] = [entry.getValue()]"
        System.out.println("Entries:");
        // TODO: for (Map.Entry<String, Integer> entry : stockMap.entrySet()) { ... }
    }
}
