# Exercise 05: Fragments and Reusable Query Parts

## Objective
Practice writing GraphQL **named fragments** and **inline fragments** to eliminate field duplication across queries and conditionally select fields based on type.

## Background
When multiple queries need the same set of fields from a type (e.g., every query that touches a `Task` needs `id`, `title`, and `done`), copying those fields into every query creates maintenance burden. GraphQL **fragments** solve this by defining a reusable set of fields once and spreading it with `...FragmentName` wherever needed. **Inline fragments** serve a related purpose: when a field can return one of several types (a union or interface), inline fragments let the client request type-specific fields.

## Requirements

1. Reuse the provided `TaskController`, `Task`, `schema.graphqls`, and `pom.xml` — no changes needed.
2. In `FragmentsTest.java`, write a test `namedFragment_reusedInTwoQueries` that:
   - Creates two tasks: `"Fragment task A"` and `"Fragment task B"`, capturing both IDs
   - Defines a **named fragment** `fragment TaskFields on Task { id title done }`
   - Issues a single query document that uses `...TaskFields` to fetch both tasks via aliases:
     ```graphql
     fragment TaskFields on Task { id title done }
     query {
       taskA: task(id: "<id1>") { ...TaskFields }
       taskB: task(id: "<id2>") { ...TaskFields }
     }
     ```
   - Asserts `path("taskA.title")` equals `"Fragment task A"` and `path("taskB.title")` equals `"Fragment task B"`
3. Write a test `namedFragment_inListQuery` that:
   - Creates three tasks
   - Queries `tasks { ...TaskFields }` using the same `TaskFields` fragment
   - Asserts `path("tasks").entityList(Task.class).hasSizeGreaterThanOrEqualTo(3)`
4. Extend `schema.graphqls` (in starter-code and solution) to add a `PriorityTask` type that implements a `TaskInterface`:
   ```graphql
   interface TaskInterface { id: ID!  title: String!  done: Boolean! }
   type Task implements TaskInterface { id: ID!  title: String!  done: Boolean! }
   type PriorityTask implements TaskInterface { id: ID!  title: String!  done: Boolean!  priority: Int! }
   type Query { ... taskOrPriority(id: ID!): TaskInterface }
   ```
   Add `taskOrPriority(id: ID!): TaskInterface` to the `Query` type.
5. Add a `taskOrPriority(@Argument String id)` method to `TaskController` that returns a `PriorityTask` (a new simple record with `id`, `title`, `done`, `priority`) when the id starts with `"p-"`, otherwise returns a normal `Task`.
6. Write a test `inlineFragment_selectsTypeSpecificField` that:
   - Queries `taskOrPriority(id: "p-1")` using an inline fragment:
     ```graphql
     { taskOrPriority(id: "p-1") { id title ... on PriorityTask { priority } } }
     ```
   - Asserts `path("taskOrPriority.priority")` is not null

## Hints
- The fragment declaration (`fragment X on Type { ... }`) and the query that uses it (`...X`) must be in the **same document string**.
- For inline fragments, the syntax is `... on TypeName { fields }` inside the selection set.
- To return an interface type from a resolver, Spring for GraphQL needs the concrete class to be mapped to the interface; just returning the implementing record is sufficient.
- For the `PriorityTask`, a simple `record PriorityTask(String id, String title, boolean done, int priority) implements TaskInterface {}` works.

## Expected Output
```
All 4 tests PASS
```
