# Exercise 03: Queries, Mutations and Subscriptions with Spring for GraphQL

## Objective
Build a working Spring for GraphQL server that handles all three operation types — Query, Mutation, and Subscription — by implementing `@QueryMapping`, `@MutationMapping`, and `@SubscriptionMapping` controller methods backed by in-memory data.

## Background
Spring for GraphQL (part of Spring Boot 3) provides first-class support for GraphQL through auto-configuration and annotation-driven controllers. The framework maps GraphQL operations to Java methods using `@QueryMapping`, `@MutationMapping`, and `@SubscriptionMapping` — similar to how `@GetMapping` maps HTTP GET requests. Subscriptions use Reactor's `Flux` to push events to connected clients.

## Requirements

1. Add the `pom.xml` dependency for `spring-boot-starter-graphql` (already provided — review it).
2. Create `src/main/resources/graphql/schema.graphqls` defining:
   - `type Task { id: ID!, title: String!, done: Boolean! }`
   - `type Query { tasks: [Task!]!, task(id: ID!): Task }`
   - `type Mutation { createTask(title: String!): Task!, completeTask(id: ID!): Task }`
   - `type Subscription { taskCreated: Task! }`
3. Implement `TaskController` with:
   - `@QueryMapping` method `tasks()` returning all tasks from the in-memory list
   - `@QueryMapping` method `task(@Argument String id)` returning the matching task or `null`
   - `@MutationMapping` method `createTask(@Argument String title)` that creates a new `Task`, adds it to the list, publishes it to the `Sinks.Many` sink, and returns the new task
   - `@MutationMapping` method `completeTask(@Argument String id)` that finds the task, marks `done = true`, and returns it (or `null`)
   - `@SubscriptionMapping` method `taskCreated()` returning `Flux<Task>` from the sink
4. Implement the `Task` record/class with fields `id`, `title`, `done`.
5. Write `TaskControllerTest` with **5 tests** using `@GraphQlTest`:
   - `queryAllTasks_returnsEmptyList` — empty list before any mutations
   - `createTask_addsTaskAndReturnsIt` — create then assert returned title
   - `queryTask_byId_returnsCorrectTask` — create then query by the returned ID
   - `completeTask_setsDoneTrue` — create then complete, assert `done == true`
   - `queryTask_unknownId_returnsNull` — query with `"999"`, assert null

## Hints
- `@GraphQlTest(TaskController.class)` sets up a lightweight test slice — no HTTP server needed.
- Use `graphQlTester.document("{ tasks { id title done } }").execute()` and chain `.path("tasks").entityList(Task.class)`.
- `Sinks.many().multicast().onBackpressureBuffer()` creates a hot sink that all subscription subscribers share.
- For the mutation test: `graphQlTester.document("mutation { createTask(title: \"Buy milk\") { id title done } }").execute()`.
- Auto-generate task IDs with `UUID.randomUUID().toString()`.

## Expected Output (from tests)
```
All 5 tests PASS
```

When you `mvn spring-boot:run` and POST to `http://localhost:8080/graphql`:
```json
POST body:  { "query": "{ tasks { id title done } }" }
Response:   { "data": { "tasks": [] } }

POST body:  { "query": "mutation { createTask(title: \"Buy milk\") { id title done } }" }
Response:   { "data": { "createTask": { "id": "<uuid>", "title": "Buy milk", "done": false } } }
```
