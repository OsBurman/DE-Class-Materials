# Exercise 04: Arguments, Variables and Aliases

## Objective
Practice writing GraphQL query documents that use **arguments** to filter data, **variables** to parameterize queries safely, and **aliases** to request the same field multiple times with different names.

## Background
Hard-coding values like `task(id: "42")` directly in a query string is called an "inline argument." For production clients it is safer and cleaner to use **variables** — the query document contains a placeholder (`$id`) and the actual value is passed in a separate JSON object. **Aliases** solve a different problem: when you want to query the same field twice with different arguments, GraphQL needs a name for each result; aliases provide those names.

## Requirements

1. Reuse the `TaskController`, `Task`, and `schema.graphqls` from Exercise 03 (files provided — no changes needed).
2. In `ArgumentsTest.java`, write a test `queryWithInlineArgument_returnsTask` that:
   - Creates a task titled `"Inline arg task"`
   - Queries `task(id: "<id>") { id title }` using an inline argument
   - Asserts the returned title equals `"Inline arg task"`
3. Write a test `queryWithVariable_returnsTask` that:
   - Creates a task titled `"Variable task"`
   - Uses `graphQlTester.document("query GetTask($taskId: ID!) { task(id: $taskId) { id title } }")` with `.variable("taskId", id)`
   - Asserts title equals `"Variable task"`
4. Write a test `mutationWithVariables_createsTask` that:
   - Uses a parameterized mutation document: `mutation CreateTask($t: String!) { createTask(title: $t) { id title done } }`
   - Passes variable `t = "Variable mutation task"` using `.variable("t", "Variable mutation task")`
   - Asserts returned title and `done == false`
5. Write a test `aliases_queryTwoTasksSameField` that:
   - Creates two tasks: `"First task"` and `"Second task"`, capturing their IDs
   - Issues a single query using aliases:
     ```graphql
     query {
       firstTask: task(id: "<id1>") { title }
       secondTask: task(id: "<id2>") { title }
     }
     ```
   - Asserts `path("firstTask.title")` equals `"First task"` and `path("secondTask.title")` equals `"Second task"`
6. Write a test `variable_withUnknownId_returnsNull` using a variable `$id = "does-not-exist"` and asserting `path("task").valueIsNull()`.

## Hints
- Pass variables using `.variable("variableName", value)` on the `GraphQlTester.Request`.
- Aliases have the form `aliasName: fieldName(args) { ... }` in the query document.
- The variable type in the query declaration must match the schema: `ID!` for non-null, `ID` for nullable.
- You can chain multiple `.variable()` calls on the same request.

## Expected Output
```
All 5 tests PASS
```
