import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileProcessingCapstone {

    // ============================================================
    // TODO 1: Define InvalidReadingException (extends Exception — CHECKED)
    //   Constructor: InvalidReadingException(String raw)
    //   Message: "Invalid temperature reading: '[raw]'"
    // ============================================================


    // ============================================================
    // TODO 2: Define parseTemperature(String raw) throws InvalidReadingException
    //   Try Double.parseDouble(raw)
    //   If NumberFormatException is thrown, throw new InvalidReadingException(raw)
    //   Return the parsed double if successful
    // ============================================================
    static double parseTemperature(String raw) throws InvalidReadingException {
        // TODO: implement
        return 0;
    }

    public static void main(String[] args) {

        // ============================================================
        // PART 1: Write input file with try-with-resources
        // ============================================================
        System.out.println("=== Part 1: Write input file ===");

        // TODO: Use try-with-resources to open a BufferedWriter wrapping FileWriter("temperatures.txt")
        //       try (BufferedWriter bw = new BufferedWriter(new FileWriter("temperatures.txt"))) { ... }
        //       Write these 6 lines, each with bw.write() + bw.newLine():
        //         "23.5", "INVALID", "18.2", "-5.0", "CORRUPT", "31.7"
        //       Print "Input file written: temperatures.txt"
        //       Catch IOException outside the try-with-resources block

        System.out.println();

        // ============================================================
        // PART 2: Process temperatures — read + write in one try-with-resources
        // ============================================================
        System.out.println("=== Part 2: Process temperatures ===");

        // TODO: Use try-with-resources with TWO resources in the header (separated by ;):
        //   BufferedReader br = new BufferedReader(new FileReader("temperatures.txt"))
        //   BufferedWriter bw = new BufferedWriter(new FileWriter("valid_temperatures.txt"))
        //
        // TODO: Inside the try block:
        //   String line;
        //   while ((line = br.readLine()) != null) {
        //       try {
        //           double temp = parseTemperature(line);
        //           bw.write(String.valueOf(temp));  bw.newLine();
        //           System.out.println("Valid reading: " + temp + "°C");
        //       } catch (InvalidReadingException e) {
        //           System.out.println("Skipped invalid: " + line);
        //       }
        //   }
        //   System.out.println("Processing complete.");
        //
        // TODO: Catch IOException after the try-with-resources

        System.out.println();

        // ============================================================
        // PART 3: Read results file
        // ============================================================
        System.out.println("=== Part 3: Read results file ===");

        // TODO: Use try-with-resources to read "valid_temperatures.txt" line by line
        //       Print "Stored: " + line for each line
        //       Catch IOException
    }
}
