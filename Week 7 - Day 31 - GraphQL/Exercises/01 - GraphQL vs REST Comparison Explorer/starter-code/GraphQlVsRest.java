public class GraphQlVsRest {

    public static void main(String[] args) {
        System.out.println("=== GRAPHQL VS REST COMPARISON TABLE ===");
        printComparisonTable();

        System.out.println("\n=== OVER-FETCHING DEMO ===");
        demonstrateOverFetching();

        System.out.println("\n=== UNDER-FETCHING DEMO ===");
        demonstrateUnderFetching();

        System.out.println("\n=== WHEN TO USE REST vs GRAPHQL ===");
        printWhenToUseEach();
    }

    /**
     * TODO 1: Print a formatted two-column comparison table.
     *   - Column headers: "Feature", "REST", "GraphQL"
     *   - Print a separator line of dashes after the header
     *   - Include at least 6 rows covering:
     *       endpoint model, data fetching, type system,
     *       versioning, real-time support, tooling/introspection
     *   - Hint: use System.out.printf("%-35s %-35s %-35s%n", col1, col2, col3)
     */
    static void printComparisonTable() {
        // TODO 1: Print the header row with three columns: Feature | REST | GraphQL

        // TODO 1: Print a separator line (e.g., "-".repeat(89))

        // TODO 1: Print at least 6 feature comparison rows
    }

    /**
     * TODO 2: Simulate an over-fetching scenario.
     *   - Print what a REST GET /users/42 returns (8 fields listed inline)
     *   - Print what the equivalent GraphQL query returns (3 fields)
     *   - Print a one-line summary: "Over-fetching eliminated: X unnecessary fields not transmitted."
     */
    static void demonstrateOverFetching() {
        // TODO 2: Print the REST response (8 fields)

        // TODO 2: Print the GraphQL query and its 3-field response

        // TODO 2: Print the summary line
    }

    /**
     * TODO 3: Simulate an under-fetching scenario.
     *   - Print that REST requires 2 HTTP calls (Call 1 and Call 2 labelled)
     *   - Print that GraphQL satisfies both in 1 request (show the query string)
     */
    static void demonstrateUnderFetching() {
        // TODO 3: Print the two REST calls needed

        // TODO 3: Print the single GraphQL query that replaces both
    }

    /**
     * TODO 4: Print 3 scenarios where REST is preferred and 3 where GraphQL is preferred.
     *   - Use "Prefer REST when:" and "Prefer GraphQL when:" as sub-headers
     *   - Number each scenario 1, 2, 3
     */
    static void printWhenToUseEach() {
        // TODO 4: Print "Prefer REST when:" and list 3 numbered scenarios

        // TODO 4: Print "Prefer GraphQL when:" and list 3 numbered scenarios
    }
}
