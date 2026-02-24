package com.graphql.tasks;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.graphql.test.tester.GraphQlTester;

@GraphQlTest(TaskController.class)
class ArgumentsTest {

    @Autowired
    private GraphQlTester graphQlTester;

    private String createTask(String title) {
        return graphQlTester
                .document("mutation { createTask(title: \"" + title + "\") { id } }")
                .execute()
                .path("createTask.id").entity(String.class).get();
    }

    @Test
    void queryWithInlineArgument_returnsTask() {
        String id = createTask("Inline arg task");

        // Inline argument: the value is embedded directly in the query string
        graphQlTester
                .document(String.format("{ task(id: \"%s\") { id title } }", id))
                .execute()
                .path("task.title").entity(String.class).isEqualTo("Inline arg task");
    }

    @Test
    void queryWithVariable_returnsTask() {
        String id = createTask("Variable task");

        // Variable: $taskId is declared in the operation signature, passed separately
        graphQlTester
                .document("query GetTask($taskId: ID!) { task(id: $taskId) { id title } }")
                .variable("taskId", id)
                .execute()
                .path("task.title").entity(String.class).isEqualTo("Variable task");
    }

    @Test
    void mutationWithVariables_createsTask() {
        // Variables keep the mutation document static and reusable
        graphQlTester
                .document("mutation CreateTask($t: String!) { createTask(title: $t) { id title done } }")
                .variable("t", "Variable mutation task")
                .execute()
                .path("createTask.title").entity(String.class).isEqualTo("Variable mutation task")
                .path("createTask.done").entity(Boolean.class).isEqualTo(false);
    }

    @Test
    void aliases_queryTwoTasksSameField() {
        String id1 = createTask("First task");
        String id2 = createTask("Second task");

        // Aliases allow querying the same field twice in one request with different args
        String doc = String.format(
                "query { firstTask: task(id: \"%s\") { title } secondTask: task(id: \"%s\") { title } }",
                id1, id2);

        graphQlTester
                .document(doc)
                .execute()
                .path("firstTask.title").entity(String.class).isEqualTo("First task")
                .path("secondTask.title").entity(String.class).isEqualTo("Second task");
    }

    @Test
    void variable_withUnknownId_returnsNull() {
        graphQlTester
                .document("query GetTask($id: ID) { task(id: $id) { id title } }")
                .variable("id", "does-not-exist")
                .execute()
                .path("task").valueIsNull();
    }
}
