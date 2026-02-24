import java.util.List;

public class Main {
    public static void main(String[] args) {

        StudentFileManager manager = new StudentFileManager();
        String inputPath = "data/students.csv";
        String outputPath = "data/report.txt";

        // TODO: Wrap processAll in a try-catch that handles:
        //   - InvalidGradeException (checked)
        //   - StudentNotFoundException (unchecked/runtime)
        //   - General Exception
        // Add a finally block that prints "Application shutting down."

        // manager.processAll(inputPath, outputPath);

        System.out.println("Check data/report.txt for output.");
    }
}
