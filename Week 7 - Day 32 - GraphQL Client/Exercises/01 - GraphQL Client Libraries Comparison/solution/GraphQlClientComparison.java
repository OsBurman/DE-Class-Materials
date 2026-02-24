public class GraphQlClientComparison {

    public static void main(String[] args) {
        printClientLibrariesOverview();
        System.out.println();
        printVsFetchComparison();
        System.out.println();
        printWhenToUseWhich();
    }

    // Prints a comparison table of major GraphQL client libraries
    static void printClientLibrariesOverview() {
        System.out.println("=== GraphQL Client Libraries ===");
        // Header row — fixed-width columns for alignment
        System.out.printf("%-20s %-12s %-20s %-10s%n",
                "Library", "Framework", "Caching", "Complexity");
        System.out.println("-".repeat(70));
        // Each row: library name, primary framework, caching approach, learning curve
        System.out.printf("%-20s %-12s %-20s %-10s%n",
                "Apollo Client", "React", "Normalized InMemory", "High");
        System.out.printf("%-20s %-12s %-20s %-10s%n",
                "urql", "React", "Document/Custom", "Medium");
        System.out.printf("%-20s %-12s %-20s %-10s%n",
                "Relay", "React", "Normalized", "High");
        System.out.printf("%-20s %-12s %-20s %-10s%n",
                "Apollo Angular", "Angular", "Normalized InMemory", "High");
    }

    // Compares raw fetch() with a dedicated GraphQL client library
    static void printVsFetchComparison() {
        System.out.println("=== Raw fetch() vs GraphQL Client Library ===");
        // Header
        System.out.printf("%-28s %-12s %-15s%n", "Feature", "fetch()", "GraphQL Client");
        System.out.println("-".repeat(55));
        // Five comparison dimensions
        System.out.printf("%-28s %-12s %-15s%n", "Normalized caching", "No", "Yes");
        System.out.printf("%-28s %-12s %-15s%n", "Automatic re-fetching", "No", "Yes");
        System.out.printf("%-28s %-12s %-15s%n", "DevTools support", "No", "Yes");
        System.out.printf("%-28s %-12s %-15s%n", "Boilerplate required", "High", "Low");
        System.out.printf("%-28s %-12s %-15s%n", "Real-time subscriptions", "Manual", "Built-in");
    }

    // Provides a numbered decision guide for choosing the right client
    static void printWhenToUseWhich() {
        System.out.println("=== When to Use Which ===");
        System.out.println("1. Small / one-off GraphQL call in any app       → use fetch or axios");
        System.out.println("2. React app with moderate data requirements     → use urql (lighter weight)");
        System.out.println("3. React app with complex caching and team size  → use Apollo Client");
        System.out.println("4. Angular app consuming GraphQL                 → use Apollo Angular");
        System.out.println("5. Large-scale React app needing relay-style     → use Relay (Meta standard)");
    }
}
