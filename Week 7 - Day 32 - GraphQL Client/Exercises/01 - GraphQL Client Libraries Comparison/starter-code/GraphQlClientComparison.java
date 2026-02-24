public class GraphQlClientComparison {

    public static void main(String[] args) {
        printClientLibrariesOverview();
        System.out.println();
        printVsFetchComparison();
        System.out.println();
        printWhenToUseWhich();
    }

    /**
     * TODO 1: Print a formatted table comparing 4 GraphQL client libraries.
     * Columns: Library, Framework, Caching Strategy, Complexity
     * Libraries to include: Apollo Client, urql, Relay, Apollo Angular
     *
     * Use printf with fixed-width columns, e.g.:
     *   System.out.printf("%-20s %-12s %-20s %-10s%n", name, framework, caching, complexity);
     *
     * Print a header row, a separator line (dashes), then one row per library.
     * Start with: System.out.println("=== GraphQL Client Libraries ===");
     */
    static void printClientLibrariesOverview() {
        // TODO 1: Implement the comparison table described above
    }

    /**
     * TODO 2: Print a side-by-side comparison of raw fetch() vs a GraphQL client library.
     * Include at least 5 dimensions:
     *   - Normalized caching
     *   - Automatic re-fetching
     *   - DevTools support
     *   - Boilerplate required
     *   - Real-time subscriptions
     *
     * Format as a table with columns: Feature, fetch(), GraphQL Client
     * Start with: System.out.println("=== Raw fetch() vs GraphQL Client Library ===");
     */
    static void printVsFetchComparison() {
        // TODO 2: Implement the fetch() vs client comparison table
    }

    /**
     * TODO 3: Print a decision guide with at least 3 rules of thumb.
     * Each rule should follow the pattern: "Scenario  →  recommendation"
     * Start with: System.out.println("=== When to Use Which ===");
     * Number the rules starting from 1.
     */
    static void printWhenToUseWhich() {
        // TODO 3: Implement the numbered decision guide
    }
}
