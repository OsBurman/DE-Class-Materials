public class JvmArchitecture {

    public static void main(String[] args) {

        System.out.println("=== JVM Runtime Information ===");

        // Print the version of the Java runtime currently executing this program
        System.out.println("Java Version : " + System.getProperty("java.version"));

        // Print the name of the organization that produced this JVM implementation
        System.out.println("Java Vendor  : " + System.getProperty("java.vendor"));

        // Print the directory where the JRE is installed on this machine
        System.out.println("Java Home    : " + System.getProperty("java.home"));

        // Print the name of the host operating system
        System.out.println("OS Name      : " + System.getProperty("os.name"));

        // Print the directory from which the JVM was launched (where you ran 'java')
        System.out.println("Working Dir  : " + System.getProperty("user.dir"));

        // Separator between general info and architecture info
        System.out.println("---");

        // sun.arch.data.model returns "32" or "64" depending on the JVM bit-width
        System.out.println("JVM Architecture: " + System.getProperty("sun.arch.data.model") + "-bit");
    }
}
