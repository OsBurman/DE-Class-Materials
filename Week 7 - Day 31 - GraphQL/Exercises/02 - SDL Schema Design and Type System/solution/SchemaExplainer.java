public class SchemaExplainer {

    public static void main(String[] args) {
        System.out.println("=== PRODUCT SCHEMA EXPLAINED ===\n");
        printSchemaAnnotated();
    }

    static void printSchemaAnnotated() {
        // ── Scalar explanations ─────────────────────────────────────────────
        System.out.println("# ID scalar: unique identifier, serialized as String");
        System.out.println("# String scalar: UTF-8 text");
        System.out.println("# Int scalar: 32-bit integer");
        System.out.println("# Float scalar: double-precision number");
        System.out.println("# Boolean scalar: true / false");

        // ── Product type ────────────────────────────────────────────────────
        System.out.println("type Product {");
        System.out.println("  id: ID!           # non-null unique identifier");
        System.out.println("  name: String!     # non-null product name");
        System.out.println("  price: Float!     # non-null price (e.g. 29.99)");
        System.out.println("  quantity: Int!    # non-null stock count");
        System.out.println("  inStock: Boolean! # non-null availability flag");
        System.out.println("  description: String  # nullable – not all products have a description");
        System.out.println("  reviews: [Review!]!  # non-null list of non-null Review objects");
        System.out.println("}");
        System.out.println();

        // ── Review type ─────────────────────────────────────────────────────
        System.out.println("type Review {");
        System.out.println("  id: ID!");
        System.out.println("  comment: String!  # non-null reviewer comment");
        System.out.println("  rating: Float!    # non-null rating e.g. 4.5");
        System.out.println("}");
        System.out.println();

        // ── Query type ──────────────────────────────────────────────────────
        System.out.println("# Query is the read-only operation type");
        System.out.println("type Query {");
        System.out.println("  products: [Product!]!       # fetch all products");
        System.out.println("  product(id: ID!): Product   # fetch one by ID; null if not found");
        System.out.println("}");
        System.out.println();

        // ── Mutation type ───────────────────────────────────────────────────
        System.out.println("# Mutation is the write operation type");
        System.out.println("type Mutation {");
        System.out.println("  addProduct(name: String!, price: Float!, inStock: Boolean!): Product!");
        System.out.println("  deleteProduct(id: ID!): Boolean!");
        System.out.println("}");
        System.out.println();

        // ── Subscription type ───────────────────────────────────────────────
        System.out.println("# Subscription delivers real-time push events to connected clients");
        System.out.println("type Subscription {");
        System.out.println("  productAdded: Product!");
        System.out.println("}");
    }
}
