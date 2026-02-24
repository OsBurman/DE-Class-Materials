import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * DAY 10 — PART 2 | Serialization and Deserialization
 * ─────────────────────────────────────────────────────────────────────────────
 * Serialization: converting a Java object graph into a byte stream
 * Deserialization: reconstructing the object from that byte stream
 *
 * Use cases:
 *   - Persist objects to disk (session state, caching)
 *   - Send objects over a network (legacy RMI, messaging)
 *   - Deep-copy objects (clone via serialization)
 *
 * Java built-in (java.io.Serializable):
 *   ✅ No external library needed
 *   ❌ Java-only format (not interoperable)
 *   ❌ Security risks: deserialization gadget chains (CVE history)
 *   ❌ Sensitive to class changes (serialVersionUID)
 *
 * Modern alternative: JSON (Jackson/Gson) — preferred for most use cases
 *   ✅ Human-readable, language-agnostic, safe
 *   ✅ Trivially supported by Spring Boot (Jackson auto-configured)
 */
public class Serialization {

    static final Path DEMO_DIR = Path.of(System.getProperty("java.io.tmpdir"), "java-serialization-demo");

    public static void main(String[] args) throws Exception {
        Files.createDirectories(DEMO_DIR);

        demonstrateBasicSerialization();
        demonstrateSerialVersionUID();
        demonstrateTransientFields();
        demonstrateCustomSerialization();
        demonstrateDeepCopyViaSerialization();
        demonstrateJsonAlternative();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 1 — Basic Serialization / Deserialization
    // A class must implement Serializable to be serialized.
    // ObjectOutputStream writes; ObjectInputStream reads.
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateBasicSerialization() throws Exception {
        System.out.println("=== Basic Serialization ===");

        Path file = DEMO_DIR.resolve("order.ser");

        // Create an object
        CustomerOrder order = new CustomerOrder("ORD-2025-001", "Alice Smith",
                Arrays.asList("Laptop", "Mouse Pad"), 1049.98);
        System.out.println("Original: " + order);

        // SERIALIZE: write object graph to file
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(file.toFile())))) {
            oos.writeObject(order);
        }
        System.out.println("Serialized to: " + file + " (" + Files.size(file) + " bytes)");

        // DESERIALIZE: read object back from file
        CustomerOrder restored;
        try (ObjectInputStream ois = new ObjectInputStream(
                new BufferedInputStream(new FileInputStream(file.toFile())))) {
            restored = (CustomerOrder) ois.readObject();
        }
        System.out.println("Deserialized:  " + restored);
        System.out.println("Same object?   " + (order == restored));       // false — new object
        System.out.println("Equal content? " + order.equals(restored) + "\n");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 2 — serialVersionUID
    // The JVM uses serialVersionUID to verify that a deserialized class matches
    // the class definition in memory. If they differ → InvalidClassException.
    // ALWAYS declare it explicitly — auto-generated IDs change if you add a field.
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateSerialVersionUID() {
        System.out.println("=== serialVersionUID ===");

        System.out.println("Without explicit serialVersionUID:");
        System.out.println("  1. Serialize object with class version A");
        System.out.println("  2. Add a field to the class (version B)");
        System.out.println("  3. Try to deserialize old data → InvalidClassException!");
        System.out.println("     (JVM detects class structure changed)");
        System.out.println();
        System.out.println("With explicit: private static final long serialVersionUID = 1L;");
        System.out.println("  Adding new fields (with defaults) won't break deserialization");
        System.out.println("  Change the UID intentionally when you make breaking changes");
        System.out.println("  IDEs can auto-generate: 'Add serialVersionUID' quick fix\n");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 3 — transient: exclude fields from serialization
    // Use for: passwords, computed/derived values, non-serializable objects,
    //          cache fields that should be re-created on load
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateTransientFields() throws Exception {
        System.out.println("=== transient Fields ===");

        Path file = DEMO_DIR.resolve("user-session.ser");

        UserSession session = new UserSession("alice", "s3cr3t-p4ssw0rd", "JWT_TOKEN_XYZ", 3600);
        System.out.println("Before serialization:");
        System.out.println("  username: " + session.username);
        System.out.println("  password: " + session.password);         // should NOT be persisted
        System.out.println("  token:    " + session.authToken);        // transient — not saved
        System.out.println("  ttl:      " + session.timeToLiveSeconds);

        try (ObjectOutputStream oos = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(file.toFile())))) {
            oos.writeObject(session);
        }

        UserSession loaded;
        try (ObjectInputStream ois = new ObjectInputStream(
                new BufferedInputStream(new FileInputStream(file.toFile())))) {
            loaded = (UserSession) ois.readObject();
        }

        System.out.println("\nAfter deserialization:");
        System.out.println("  username: " + loaded.username);          // restored
        System.out.println("  password: " + loaded.password);          // null (transient)
        System.out.println("  token:    " + loaded.authToken);          // null (transient)
        System.out.println("  ttl:      " + loaded.timeToLiveSeconds + "\n");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 4 — Custom serialization with writeObject / readObject
    // Override these private methods to control exactly what gets written/read.
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateCustomSerialization() throws Exception {
        System.out.println("=== Custom Serialization (writeObject/readObject) ===");

        Path file = DEMO_DIR.resolve("secure-creds.ser");

        SecureCredentials creds = new SecureCredentials("alice", "hunter2");
        System.out.println("Original password: " + creds.rawPassword);

        try (ObjectOutputStream oos = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(file.toFile())))) {
            oos.writeObject(creds);
        }

        SecureCredentials loaded;
        try (ObjectInputStream ois = new ObjectInputStream(
                new BufferedInputStream(new FileInputStream(file.toFile())))) {
            loaded = (SecureCredentials) ois.readObject();
        }

        System.out.println("Loaded password: " + loaded.rawPassword);
        System.out.println("(custom writeObject encrypted; custom readObject decrypted)\n");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 5 — Deep Copy via Serialization
    // By round-tripping through bytes, you get a completely independent copy.
    // Not recommended for production (slow, risky) — use copy constructors or
    // dedicated mapping libraries (MapStruct, ModelMapper) instead.
    // ─────────────────────────────────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    static void demonstrateDeepCopyViaSerialization() throws Exception {
        System.out.println("=== Deep Copy via Serialization ===");

        CustomerOrder original = new CustomerOrder("ORD-ORIG", "Bob",
                new ArrayList<>(Arrays.asList("Book", "Pen")), 29.99);

        // Deep copy: serialize to byte array, then deserialize
        CustomerOrder deepCopy = deepCopy(original);
        deepCopy.items.add("Ruler");   // modify the copy

        System.out.println("Original items: " + original.items);  // unaffected
        System.out.println("Copy items:     " + deepCopy.items);

        System.out.println("\n⚠️  Prefer copy constructors or builders over serialization for copying.");
        System.out.println("     Serialization deep copy is slow and requires Serializable everywhere.\n");
    }

    static <T extends Serializable> T deepCopy(T object) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(object);
        }
        byte[] bytes = baos.toByteArray();
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            return (T) ois.readObject();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 6 — JSON Alternative (simulated)
    // In production Java, prefer JSON serialization (Jackson/Gson).
    // Spring Boot auto-configures Jackson — @RequestBody and @ResponseBody
    // use it transparently.
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateJsonAlternative() throws Exception {
        System.out.println("=== JSON Serialization (conceptual / manual example) ===");

        // Manual JSON serialization (normally Jackson does this automatically)
        CustomerOrder order = new CustomerOrder("ORD-JSON", "Charlie",
                Arrays.asList("Headphones", "Cable"), 149.99);

        String json = toJsonManual(order);
        System.out.println("Manual JSON output:\n  " + json);

        System.out.println("\nWith Jackson (Spring Boot auto-configured):");
        System.out.println("  ObjectMapper mapper = new ObjectMapper();");
        System.out.println("  String json   = mapper.writeValueAsString(order);   // serialize");
        System.out.println("  Order restored = mapper.readValue(json, Order.class); // deserialize");
        System.out.println();
        System.out.println("Why prefer JSON over Java serialization?");
        System.out.println("  ✅ Human-readable — debug with your eyes");
        System.out.println("  ✅ Language-agnostic — JS/Python/Go can all read it");
        System.out.println("  ✅ No deserialization gadget vulnerabilities");
        System.out.println("  ✅ Easy versioning — missing fields get defaults");
        System.out.println("  ❌ Slightly larger than binary formats (use Protobuf/Avro for extreme perf)\n");
    }

    // A minimal manual JSON builder for demonstration purposes
    static String toJsonManual(CustomerOrder o) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"orderId\":\"").append(o.orderId).append("\",");
        sb.append("\"customer\":\"").append(o.customer).append("\",");
        sb.append("\"items\":[");
        for (int i = 0; i < o.items.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append("\"").append(o.items.get(i)).append("\"");
        }
        sb.append("],");
        sb.append("\"total\":").append(o.total);
        sb.append("}");
        return sb.toString();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Model classes
    // ─────────────────────────────────────────────────────────────────────────

    static class CustomerOrder implements Serializable {
        private static final long serialVersionUID = 1L;   // ← always declare this

        String orderId;
        String customer;
        List<String> items;
        double total;

        CustomerOrder(String orderId, String customer, List<String> items, double total) {
            this.orderId  = orderId;
            this.customer = customer;
            this.items    = items;
            this.total    = total;
        }

        @Override public String toString() {
            return "CustomerOrder{orderId='" + orderId + "', customer='" + customer +
                    "', items=" + items + ", total=" + total + "}";
        }

        @Override public boolean equals(Object o) {
            if (!(o instanceof CustomerOrder)) return false;
            CustomerOrder other = (CustomerOrder) o;
            return Objects.equals(orderId, other.orderId) &&
                   Objects.equals(customer, other.customer) &&
                   Objects.equals(items, other.items) &&
                   Double.compare(total, other.total) == 0;
        }
    }

    static class UserSession implements Serializable {
        private static final long serialVersionUID = 1L;

        String username;
        transient String password;     // transient — excluded from serialization
        transient String authToken;    // transient — excluded from serialization
        int timeToLiveSeconds;

        UserSession(String username, String password, String authToken, int ttl) {
            this.username         = username;
            this.password         = password;
            this.authToken        = authToken;
            this.timeToLiveSeconds = ttl;
        }
    }

    static class SecureCredentials implements Serializable {
        private static final long serialVersionUID = 1L;

        String username;
        transient String rawPassword;   // don't auto-serialize
        private String storedEncrypted; // we manage this ourselves

        SecureCredentials(String username, String password) {
            this.username    = username;
            this.rawPassword = password;
        }

        private void writeObject(ObjectOutputStream oos) throws IOException {
            oos.defaultWriteObject();
            // "Encrypt" before writing (ROT13 for demo — use AES in production!)
            oos.writeObject(rot13(rawPassword));
        }

        private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
            ois.defaultReadObject();
            // "Decrypt" after reading
            rawPassword = rot13((String) ois.readObject());
        }

        static String rot13(String s) {
            StringBuilder sb = new StringBuilder();
            for (char c : s.toCharArray()) {
                if (c >= 'a' && c <= 'z') sb.append((char) ('a' + (c - 'a' + 13) % 26));
                else if (c >= 'A' && c <= 'Z') sb.append((char) ('A' + (c - 'A' + 13) % 26));
                else sb.append(c);
            }
            return sb.toString();
        }
    }
}
