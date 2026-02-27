import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Exercise 07 — File I/O (SOLUTION)
 */
public class Main {

    static class TodoItem implements Serializable {
        private static final long serialVersionUID = 1L;
        int id;
        String title;
        boolean done;
        String priority;
        String createdAt;

        TodoItem(int id, String title, String priority) {
            this.id = id;
            this.title = title;
            this.priority = priority;
            this.done = false;
            this.createdAt = java.time.LocalDate.now().toString();
        }

        String toCsv() {
            return id + "," + title.replace(",", "；") + "," + done + "," + priority + "," + createdAt;
        }

        static TodoItem fromCsv(String line) {
            String[] p = line.split(",", 5);
            TodoItem item = new TodoItem(Integer.parseInt(p[0].trim()), p[1].trim(), p[3].trim());
            item.done = Boolean.parseBoolean(p[2].trim());
            item.createdAt = p[4].trim();
            return item;
        }

        @Override
        public String toString() {
            return String.format("[%s] #%d %-30s (%s) %s",
                    done ? "✓" : " ", id, title, priority, done ? "" : createdAt);
        }
    }

    static void saveToCsv(List<TodoItem> items, String filename) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            bw.write("id,title,done,priority,createdAt");
            bw.newLine();
            for (TodoItem t : items) {
                bw.write(t.toCsv());
                bw.newLine();
            }
        }
    }

    static List<TodoItem> loadFromCsv(String filename) throws IOException {
        List<TodoItem> list = new ArrayList<>();
        File file = new File(filename);
        if (!file.exists())
            return list;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.isBlank())
                    list.add(TodoItem.fromCsv(line));
            }
        }
        return list;
    }

    static void saveToTextReport(List<TodoItem> items, String filename) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Todo Report ===\n\n");
        sb.append("Pending:\n");
        items.stream().filter(t -> !t.done).forEach(t -> sb.append("  ").append(t).append("\n"));
        sb.append("\nCompleted:\n");
        items.stream().filter(t -> t.done).forEach(t -> sb.append("  ").append(t).append("\n"));
        long pending = items.stream().filter(t -> !t.done).count();
        sb.append("\nTotal: ").append(pending).append(" pending, ")
                .append(items.size() - pending).append(" done\n");
        Files.writeString(Path.of(filename), sb.toString());
    }

    static void serializeItems(List<TodoItem> items, String filename) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(items);
        }
    }

    @SuppressWarnings("unchecked")
    static List<TodoItem> deserializeItems(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return (List<TodoItem>) ois.readObject();
        }
    }

    static void appendToLog(String message, String logFile) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(logFile, true))) {
            bw.write("[" + java.time.LocalDateTime.now() + "] " + message);
            bw.newLine();
        }
    }

    public static void main(String[] args) {
        List<TodoItem> todos = new ArrayList<>(List.of(
                new TodoItem(1, "Buy groceries", "HIGH"),
                new TodoItem(2, "Finish Java exercise", "HIGH"),
                new TodoItem(3, "Read a book", "LOW"),
                new TodoItem(4, "Call the dentist", "MEDIUM"),
                new TodoItem(5, "Go for a run", "MEDIUM")));
        todos.get(0).done = true;
        todos.get(1).done = true;

        try {
            saveToCsv(todos, "todos.csv");
            System.out.println("✓ Saved CSV");
            List<TodoItem> loaded = loadFromCsv("todos.csv");
            System.out.println("✓ Loaded " + loaded.size() + " items:");
            loaded.forEach(System.out::println);

            saveToTextReport(loaded, "todos_report.txt");
            System.out.println("\n✓ Report:\n" + Files.readString(Path.of("todos_report.txt")));

            serializeItems(todos, "todos.ser");
            System.out.println("✓ Serialized. Deserialized count: " + deserializeItems("todos.ser").size());

            appendToLog("App started", "todos.log");
            appendToLog("Saved " + todos.size() + " todos", "todos.log");
            System.out.println("✓ Log:\n" + Files.readString(Path.of("todos.log")));
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
