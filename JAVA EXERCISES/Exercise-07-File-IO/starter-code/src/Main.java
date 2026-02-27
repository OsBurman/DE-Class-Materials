import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Exercise 07 — File I/O (STARTER)
 * Todo List Manager with file persistence.
 *
 * Fill in each TODO. The application compiles as-is.
 */
public class Main {

    // ── TodoItem ────────────────────────────────────────────────────
    static class TodoItem implements Serializable {
        private static final long serialVersionUID = 1L;
        int id;
        String title;
        boolean done;
        String priority; // HIGH / MEDIUM / LOW
        String createdAt;

        TodoItem(int id, String title, String priority) {
            this.id = id;
            this.title = title;
            this.priority = priority;
            this.done = false;
            this.createdAt = java.time.LocalDate.now().toString();
        }

        // Serialize to CSV line: id,title,done,priority,createdAt
        String toCsv() {
            return id + "," + title.replace(",", "；") + "," + done + "," + priority + "," + createdAt;
        }

        // TODO 1: Implement static fromCsv(String line)
        // Split on "," (limit 5), parse each field, return a new TodoItem.
        // Set item.done = Boolean.parseBoolean(parts[2])
        static TodoItem fromCsv(String line) {
            return null; // your code here
        }

        @Override
        public String toString() {
            return String.format("[%s] #%d %-30s (%s) %s",
                    done ? "✓" : " ", id, title, priority, done ? "" : createdAt);
        }
    }

    // ── TODO 2: Implement saveToCsv(List<TodoItem> items, String filename) ──
    // Use BufferedWriter / FileWriter.
    // Write a header line: "id,title,done,priority,createdAt"
    // Then write each item's toCsv() on its own line.
    static void saveToCsv(List<TodoItem> items, String filename) throws IOException {
        // your code here
    }

    // ── TODO 3: Implement loadFromCsv(String filename) ──────────────
    // Use BufferedReader / FileReader.
    // Skip the header line.
    // For each remaining line, call TodoItem.fromCsv(line) and add to list.
    // Return an empty list if the file does not exist.
    static List<TodoItem> loadFromCsv(String filename) throws IOException {
        return new ArrayList<>(); // your code here
    }

    // ── TODO 4: Implement saveToTextReport(List<TodoItem>, String filename) ─
    // Use the NIO Files.writeString() or Files.write() API.
    // Format a human-readable report with sections:
    // "=== Todo Report ==="
    // "Pending:" (list undone items)
    // "Completed:" (list done items)
    // "Total: X pending, Y done"
    static void saveToTextReport(List<TodoItem> items, String filename) throws IOException {
        // your code here (use Path.of(filename), Files.writeString(), or Files.write())
    }

    // ── TODO 5: Implement serializeItems / deserializeItems ─────────
    // Use ObjectOutputStream / ObjectInputStream.
    static void serializeItems(List<TodoItem> items, String filename) throws IOException {
        // your code here
    }

    @SuppressWarnings("unchecked")
    static List<TodoItem> deserializeItems(String filename) throws IOException, ClassNotFoundException {
        return new ArrayList<>(); // your code here
    }

    // ── TODO 6: Implement appendToLog(String message, String logFile) ─
    // Use FileWriter(filename, true) (append mode) wrapped in BufferedWriter.
    // Write: "[timestamp] message\n"
    static void appendToLog(String message, String logFile) throws IOException {
        // your code here
    }

    // ── Main ─────────────────────────────────────────────────────────
    public static void main(String[] args) {
        List<TodoItem> todos = new ArrayList<>(List.of(
                new TodoItem(1, "Buy groceries", "HIGH"),
                new TodoItem(2, "Finish Java exercise", "HIGH"),
                new TodoItem(3, "Read a book", "LOW"),
                new TodoItem(4, "Call the dentist", "MEDIUM"),
                new TodoItem(5, "Go for a run", "MEDIUM")));
        todos.get(0).done = true;
        todos.get(1).done = true;

        String csvFile = "todos.csv";
        String reportFile = "todos_report.txt";
        String binFile = "todos.ser";
        String logFile = "todos.log";

        try {
            // Save and reload CSV
            saveToCsv(todos, csvFile);
            System.out.println("✓ Saved CSV: " + csvFile);

            List<TodoItem> loaded = loadFromCsv(csvFile);
            System.out.println("✓ Loaded " + loaded.size() + " items from CSV:");
            loaded.forEach(System.out::println);

            // Text report
            saveToTextReport(loaded, reportFile);
            System.out.println("\n✓ Report saved: " + reportFile);
            System.out.println(Files.readString(Path.of(reportFile)));

            // Serialization
            serializeItems(todos, binFile);
            System.out.println("✓ Serialized to: " + binFile);
            List<TodoItem> deserialized = deserializeItems(binFile);
            System.out.println("✓ Deserialized " + deserialized.size() + " items");

            // Log
            appendToLog("App started", logFile);
            appendToLog("Saved " + todos.size() + " todos", logFile);
            System.out.println("✓ Log appended: " + logFile);
            System.out.println(Files.readString(Path.of(logFile)));

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
