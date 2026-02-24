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

        // Declare outside try so finally can close them
        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            fw = new FileWriter("grocery_list.txt");
            bw = new BufferedWriter(fw);  // BufferedWriter wraps FileWriter for performance

            String[] items = {"Apples", "Bananas", "Carrots", "Dates", "Eggplant"};
            for (String item : items) {
                bw.write(item);
                bw.newLine();  // platform-safe: \r\n on Windows, \n on Unix
            }
            System.out.println("File written: grocery_list.txt");
        } catch (IOException e) {
            System.out.println("Write error: " + e.getMessage());
        } finally {
            // Close in reverse order of opening — buffered wrapper first, then underlying stream
            // Each close is in its own try to prevent one close failure from skipping the other
            if (bw != null) {
                try { bw.close(); } catch (IOException e) { /* ignore close failure */ }
            }
            if (fw != null) {
                try { fw.close(); } catch (IOException e) { /* ignore close failure */ }
            }
        }
        System.out.println();

        // ============================================================
        // PART 2: Read from file using FileReader + BufferedReader
        // ============================================================
        System.out.println("=== Part 2: Read from file ===");

        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader("grocery_list.txt");
            br = new BufferedReader(fr);

            String line;
            // readLine() returns null at EOF — this is the standard read-loop pattern
            while ((line = br.readLine()) != null) {
                System.out.println("Read: " + line);
            }
            System.out.println("Done reading grocery_list.txt");
        } catch (IOException e) {
            System.out.println("Read error: " + e.getMessage());
        } finally {
            if (br != null) {
                try { br.close(); } catch (IOException e) { /* ignore */ }
            }
            if (fr != null) {
                try { fr.close(); } catch (IOException e) { /* ignore */ }
            }
        }
        System.out.println();

        // ============================================================
        // PART 3: Handle missing file — FileNotFoundException
        // ============================================================
        System.out.println("=== Part 3: Handle missing file ===");

        FileReader fr2 = null;
        BufferedReader br2 = null;
        try {
            // FileNotFoundException extends IOException — catch it first (more specific)
            fr2 = new FileReader("missing_file.txt");
            br2 = new BufferedReader(fr2);
        } catch (FileNotFoundException e) {
            System.out.println("File not found: missing_file.txt");
        } catch (IOException e) {
            System.out.println("Read error: " + e.getMessage());
        } finally {
            if (br2 != null) {
                try { br2.close(); } catch (IOException e) { /* ignore */ }
            }
            if (fr2 != null) {
                try { fr2.close(); } catch (IOException e) { /* ignore */ }
            }
        }
    }
}
