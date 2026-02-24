import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileReadWriteDemo {

    public static void main(String[] args) {

        // ============================================================
        // PART 1: Write to file using FileWriter + BufferedWriter
        // ============================================================
        System.out.println("=== Part 1: Write to file ===");

        // TODO: Declare FileWriter fw = null; and BufferedWriter bw = null; HERE (before try)
        //       so they are accessible in the finally block


        // TODO: try {
        //         fw = new FileWriter("grocery_list.txt");
        //         bw = new BufferedWriter(fw);
        //         Write these 5 items, each followed by bw.newLine():
        //           "Apples", "Bananas", "Carrots", "Dates", "Eggplant"
        //         Print "File written: grocery_list.txt"
        //       } catch (IOException e) {
        //         System.out.println("Write error: " + e.getMessage());
        //       } finally {
        //         Close bw (if not null), then fw (if not null)
        //         Wrap each close in its own try-catch to handle close exceptions
        //       }

        System.out.println();

        // ============================================================
        // PART 2: Read from file using FileReader + BufferedReader
        // ============================================================
        System.out.println("=== Part 2: Read from file ===");

        // TODO: Declare FileReader fr = null; and BufferedReader br = null; HERE

        // TODO: try {
        //         fr = new FileReader("grocery_list.txt");
        //         br = new BufferedReader(fr);
        //         String line;
        //         while ((line = br.readLine()) != null) {
        //           System.out.println("Read: " + line);
        //         }
        //         System.out.println("Done reading grocery_list.txt");
        //       } catch (IOException e) {
        //         System.out.println("Read error: " + e.getMessage());
        //       } finally {
        //         Close br (if not null), then fr (if not null)
        //       }

        System.out.println();

        // ============================================================
        // PART 3: Handle missing file
        // ============================================================
        System.out.println("=== Part 3: Handle missing file ===");

        // TODO: Declare FileReader fr2 = null; BufferedReader br2 = null; HERE

        // TODO: try {
        //         fr2 = new FileReader("missing_file.txt");  // throws FileNotFoundException
        //         ... (read attempt — won't be reached)
        //       } catch (FileNotFoundException e) {
        //         System.out.println("File not found: missing_file.txt");
        //       } catch (IOException e) {
        //         System.out.println("Read error: " + e.getMessage());
        //       } finally {
        //         Close br2 and fr2 if not null
        //       }
    }
}
