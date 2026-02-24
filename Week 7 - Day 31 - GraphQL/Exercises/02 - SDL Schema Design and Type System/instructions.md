# Exercise 02: SDL Schema Design and Type System

## Objective
Practice writing GraphQL Schema Definition Language (SDL) by designing a complete schema that uses all built-in scalar types, object types with fields, and all three operation types (Query, Mutation, Subscription).

## Background
Every GraphQL API is described by a schema written in SDL. The schema is the contract between server and client — it defines every type, every field, and every operation that the API supports. Before building a server, experienced GraphQL developers design the schema first ("schema-first development"). This exercise focuses entirely on schema authoring.

## Requirements

1. In `schema.graphqls`, define a `Product` object type with **at least 6 fields** that together use all five built-in scalar types: `String`, `Int`, `Float`, `Boolean`, and `ID`.
2. Define a `Review` object type with at least 3 fields, including a non-null `String` for `comment` and a `Float` for `rating`.
3. Make `Product` contain a list field `reviews: [Review!]!` linking the two types.
4. Define a `Query` type with:
   - `products: [Product!]!` — returns all products
   - `product(id: ID!): Product` — returns one product by ID (nullable — returns null if not found)
5. Define a `Mutation` type with:
   - `addProduct(name: String!, price: Float!, inStock: Boolean!): Product!`
   - `deleteProduct(id: ID!): Boolean!`
6. Define a `Subscription` type with:
   - `productAdded: Product!` — clients subscribe to be notified whenever a new product is added
7. In `SchemaExplainer.java`, implement `printSchemaAnnotated()` which reads and prints each type from the schema with a one-line comment explaining what it does.

## Hints
- Non-null fields use `!` — e.g., `name: String!` means the server guarantees a non-null value.
- `[Review!]!` means: the list itself is non-null AND each item in the list is non-null.
- The `ID` scalar is serialized as a `String` but semantically represents a unique identifier.
- `SchemaExplainer` does not need to parse the file — just `println` each line with explanatory comments interspersed.

## Expected Output

```
=== PRODUCT SCHEMA EXPLAINED ===

# ID scalar: unique identifier, serialized as String
# String scalar: UTF-8 text
# Int scalar: 32-bit integer
# Float scalar: double-precision number
# Boolean scalar: true / false
type Product {
  id: ID!           # non-null unique identifier
  name: String!     # non-null product name
  price: Float!     # non-null price (e.g. 29.99)
  quantity: Int!    # non-null stock count
  inStock: Boolean! # non-null availability flag
  description: String  # nullable – not all products have a description
  reviews: [Review!]!  # non-null list of non-null Review objects
}

type Review {
  id: ID!
  comment: String!  # non-null reviewer comment
  rating: Float!    # non-null rating e.g. 4.5
}

# Query is the read-only operation type
type Query {
  products: [Product!]!       # fetch all products
  product(id: ID!): Product   # fetch one by ID; null if not found
}

# Mutation is the write operation type
type Mutation {
  addProduct(name: String!, price: Float!, inStock: Boolean!): Product!
  deleteProduct(id: ID!): Boolean!
}

# Subscription delivers real-time push events to connected clients
type Subscription {
  productAdded: Product!
}
```
