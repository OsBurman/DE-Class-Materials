import exceptions.InvalidGradeException;
import exceptions.StudentNotFoundException;

import java.io.*;
import java.util.*;

/**
 * Reads and writes student record files.
 * Complete all TODOs using BufferedReader, BufferedWriter, and
 * try-with-resources.
 */
public class StudentFileManager {

    // TODO Task 3: readStudents(String filePath)
    // - Use try-with-resources: try (BufferedReader br = new BufferedReader(new
    // FileReader(filePath)))
    // - Skip the header line
    // - For each data line: split by ",", parse gpa
    // - If gpa < 0.0 or > 4.0: throw new InvalidGradeException(...)
    // - If gpa can't be parsed: catch NumberFormatException and print warning, skip
    // row
    // - Return List<String[]> of valid rows
    public List<String[]> readStudents(String filePath) throws InvalidGradeException, IOException {
        List<String[]> students = new ArrayList<>();
        // Your implementation here
        return students;
    }

    // TODO Task 4: writeReport(String filePath, List<String[]> students)
    // - Use try-with-resources: try (BufferedWriter bw = new BufferedWriter(new
    // FileWriter(filePath)))
    // - Write a header line, then one formatted line per student
    // - In a finally block: print "File operation complete"
    public void writeReport(String filePath, List<String[]> students) throws IOException {
        // Your implementation here
    }

    // TODO Task 5: findStudent(List<String[]> students, String name)
    // - Loop through students, compare name field (index 1)
    // - If found: return the String[] row
    // - If not found: throw new StudentNotFoundException(name)
    public String[] findStudent(List<String[]> students, String name) {
        return null; // replace with your implementation
    }

    // TODO Task 6: processAll(String inputPath, String outputPath)
    // 1. Call readStudents()
    // 2. Call writeReport()
    // 3. Call findStudent() with a name that does NOT exist in the file
    // (to demonstrate the exception propagating up)
    public void processAll(String inputPath, String outputPath) throws InvalidGradeException, IOException {
        // Your implementation here
    }
}
