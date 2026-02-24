package com.graphql.tasks;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.graphql.test.tester.GraphQlTester;

@GraphQlTest(TaskController.class)
class ArgumentsTest {

    @Autowired
    private GraphQlTester graphQlTester;

    // Helper – create a task and return its id
    private String createTask(String title) {
        return graphQlTester
                .document("mutation { createTask(title: \"" + title + "\") { id } }")
                .execute()
                .path("createTask.id").entity(String.class).get();
    }

    // TODO 1: Create a task titled "Inline arg task".
    //         Query it back using an inline argument: task(id: "<id>") { id title }
    //         Assert the returned title equals "Inline arg task".
    @Test
    void queryWithInlineArgument_returnsTask() {
        // TODO 1
    }

    // TODO 2: Create a task titled "Variable task".
    //         Use a query document with a variable: query GetTask($taskId: ID!) { task(id: $taskId) { id title } }
    //         Pass the variable with .variable("taskId", id).
    //         Assert title equals "Variable task".
    @Test
    void queryWithVariable_returnsTask() {
        // TODO 2
    }

    // TODO 3: Write a parameterized mutation:
    //         mutation CreateTask($t: String!) { createTask(title: $t) { id title done } }
    //         Pass variable t = "Variable mutation task" using .variable("t", "Variable mutation task").
    //         Assert returned title and done == false.
    @Test
    void mutationWithVariables_createsTask() {
        // TODO 3
    }

    // TODO 4: Create two tasks: "First task" and "Second task", capturing both IDs.
    //         Issue a single query using aliases:
    //           query { firstTask: task(id: "<id1>") { title }  secondTask: task(id: "<id2>") { title } }
    //         Assert path("firstTask.title") == "First task" and path("secondTask.title") == "Second task".
    @Test
    void aliases_queryTwoTasksSameField() {
        // TODO 4
    }

    // TODO 5: Use a variable $id = "does-not-exist" in a query.
    //         Assert path("task").valueIsNull().
    @Test
    void variable_withUnknownId_returnsNull() {
        // TODO 5
    }
}
