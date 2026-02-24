import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileProcessingCapstone {

    // ============================================================
    // Checked custom exception — forces callers to handle bad readings
    // ============================================================
    static class InvalidReadingException extends Exception {
        public InvalidReadingException(String raw) {
            super("Invalid temperature reading: '" + raw + "'");
        }
    }

    // ============================================================
    // Translate NumberFormatException → domain-specific checked exception
    // This keeps I/O and parsing concerns separated
    // ============================================================
    static double parseTemperature(String raw) throws InvalidReadingException {
        try {
            return Double.parseDouble(raw);
        } catch (NumberFormatException e) {
            // Wrap the low-level exception in our domain exception
            throw new InvalidReadingException(raw);
        }
    }

    public static void main(String[] args) {

        // ============================================================
        // PART 1: Write input file with try-with-resources
        // ============================================================
        System.out.println("=== Part 1: Write input file ===");

        // The resource (bw) is automatically closed at the end of the try block
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("temperatures.txt"))) {
            String[] readings = {"23.5", "INVALID", "18.2", "-5.0", "CORRUPT", "31.7"};
            for (String r : readings) {
                bw.write(r);
                bw.newLine();
            }
            System.out.println("Input file written: temperatures.txt");
        } catch (IOException e) {
            System.out.println("Write error: " + e.getMessage());
        }
        System.out.println();

        // ============================================================
        // PART 2: Process — two resources in one try-with-resources header
        // ============================================================
        System.out.println("=== Part 2: Process temperatures ===");

        // Resources are closed in reverse declaration order: bw closed before br
        try (BufferedReader br = new BufferedReader(new FileReader("temperatures.txt"));
             BufferedWriter bw = new BufferedWriter(new FileWriter("valid_temperatures.txt"))) {

            String line;
            while ((line = br.readLine()) != null) {
                try {
                    double temp = parseTemperature(line);
                    // Write the numeric value — parseDouble normalises it (e.g., "-5.0" stays "-5.0")
                    bw.write(String.valueOf(temp));
                    bw.newLine();
                    System.out.println("Valid reading: " + temp + "°C");
                } catch (InvalidReadingException e) {
                    // Catching here lets the loop continue — we don't abort on one bad line
                    System.out.println("Skipped invalid: " + line);
                }
            }
            System.out.println("Processing complete.");

        } catch (IOException e) {
            System.out.println("Processing error: " + e.getMessage());
        }
        System.out.println();

        // ============================================================
        // PART 3: Read results file
        // ============================================================
        System.out.println("=== Part 3: Read results file ===");

        try (BufferedReader br = new BufferedReader(new FileReader("valid_temperatures.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println("Stored: " + line);
            }
        } catch (IOException e) {
            System.out.println("Read error: " + e.getMessage());
        }
    }
}
