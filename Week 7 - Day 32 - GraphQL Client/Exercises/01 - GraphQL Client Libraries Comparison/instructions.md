# Exercise 01: GraphQL Client Libraries Comparison

## Objective
Understand why dedicated GraphQL client libraries exist and compare the major options available for React and Angular applications.

## Background
When consuming a GraphQL API from a frontend application, you could technically use `fetch` or any HTTP client — GraphQL is just HTTP POST under the hood. However, dedicated GraphQL client libraries provide powerful extras: normalized caching, automatic re-fetching, declarative data binding, and DevTools. Choosing the right client depends on the framework, project size, and caching needs.

## Requirements

1. In `printClientLibrariesOverview()`, print a formatted table comparing **four** GraphQL client libraries: **Apollo Client**, **urql**, **Relay**, and **Angular Apollo**. For each library, show: Name, Primary Framework, Caching Strategy, and Complexity level (Low / Medium / High).

2. In `printVsFetchComparison()`, print a side-by-side comparison of using raw `fetch` vs a GraphQL client library. Show at least **five** dimensions of comparison: Normalized caching, Automatic re-fetching, DevTools, Boilerplate required, and Real-time subscriptions.

3. In `printWhenToUseWhich()`, print a decision guide with at least **three** rules of thumb (e.g., "Small app, no caching needs → use fetch", "React app with complex caching → use Apollo Client").

4. The `main` method must call all three methods in order with a blank line between each section.

5. All output must be neatly column-aligned using `printf` formatting.

## Hints
- Use `%-30s` style format specifiers in `printf` to pad columns to a fixed width.
- Think of each method as printing a "section" with a header line, a separator (`---`), and then rows of data.
- For the decision guide, a simple numbered list format works well.
- There is no user input — this is a pure print exercise.

## Expected Output

```
=== GraphQL Client Libraries ===
Library              Framework    Caching              Complexity
----------------------------------------------------------------------
Apollo Client        React        Normalized InMemory  High
urql                 React        Document/Custom      Medium
Relay                React        Normalized           High
Apollo Angular       Angular      Normalized InMemory  High

=== Raw fetch() vs GraphQL Client Library ===
Feature                     fetch()      GraphQL Client
-------------------------------------------------------
Normalized caching          No           Yes
Automatic re-fetching       No           Yes
DevTools support            No           Yes
Boilerplate required        High         Low
Real-time subscriptions     Manual       Built-in

=== When to Use Which ===
1. Small / one-off GraphQL call in any app       → use fetch or axios
2. React app with moderate data requirements     → use urql (lighter weight)
3. React app with complex caching and team size  → use Apollo Client
4. Angular app consuming GraphQL                 → use Apollo Angular
5. Large-scale React app needing relay-style     → use Relay (Meta standard)
```
