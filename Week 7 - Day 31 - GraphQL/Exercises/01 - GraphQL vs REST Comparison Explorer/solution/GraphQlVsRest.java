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

    static void printComparisonTable() {
        // printf with three fixed-width columns for alignment
        String fmt = "%-35s %-35s %-35s%n";
        System.out.printf(fmt, "Feature", "REST", "GraphQL");
        System.out.println("-".repeat(107));
        System.out.printf(fmt, "Endpoint model",
                "Multiple endpoints (/users, /posts)", "Single endpoint (/graphql)");
        System.out.printf(fmt, "Data fetching",
                "Fixed response shape", "Client specifies exact fields");
        System.out.printf(fmt, "Type system",
                "No enforced schema", "Strongly-typed SDL schema");
        System.out.printf(fmt, "Versioning",
                "/v1, /v2 URL versioning", "Schema evolution (no versions)");
        System.out.printf(fmt, "Real-time support",
                "Polling or WebSocket add-ons", "Built-in Subscriptions");
        System.out.printf(fmt, "Tooling / Introspection",
                "OpenAPI/Swagger (manual)", "Introspection + GraphiQL auto");
    }

    static void demonstrateOverFetching() {
        // REST returns every field defined on the server — client has no choice
        System.out.println("REST GET /users/42 returns 8 fields:");
        System.out.println("  { id, username, email, bio, createdAt, lastLogin, avatarUrl, role }");
        System.out.println();

        // GraphQL lets the client declare exactly which fields it needs
        System.out.println("GraphQL query { user(id: \"42\") { id username email } } returns 3 fields:");
        System.out.println("  { id, username, email }");
        System.out.println();

        int restFields = 8;
        int graphqlFields = 3;
        System.out.printf("Over-fetching eliminated: %d unnecessary fields not transmitted.%n",
                restFields - graphqlFields);
    }

    static void demonstrateUnderFetching() {
        // With REST, a single call does not return nested related resources
        System.out.println("REST requires 2 HTTP calls:");
        System.out.println("  Call 1: GET /users/42        → user object");
        System.out.println("  Call 2: GET /users/42/posts  → posts array");
        System.out.println();

        // GraphQL resolves nested types in a single round-trip
        System.out.println("GraphQL satisfies both in 1 request:");
        System.out.println("  query { user(id: \"42\") { username posts { title } } }");
    }

    static void printWhenToUseEach() {
        System.out.println("Prefer REST when:");
        System.out.println("  1. Public API consumed by many unknown clients");
        System.out.println("  2. Simple CRUD with well-defined, stable resources");
        System.out.println("  3. Team is already proficient with REST/OpenAPI tooling");
        System.out.println();

        System.out.println("Prefer GraphQL when:");
        System.out.println("  1. Multiple client types (mobile, web, TV) need different data shapes");
        System.out.println("  2. Rapid frontend iteration requires frequent field additions");
        System.out.println("  3. Aggregating data from multiple backend services into one request");
    }
}
