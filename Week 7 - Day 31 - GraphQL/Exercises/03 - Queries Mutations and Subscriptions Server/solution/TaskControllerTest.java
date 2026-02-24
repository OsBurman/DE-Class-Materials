package com.graphql.tasks;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.graphql.test.tester.GraphQlTester;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @GraphQlTest loads only the GraphQL slice: the controller, schema, and GraphQlTester.
 * No HTTP server is started — fast and focused.
 */
@GraphQlTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @Test
    void queryAllTasks_returnsEmptyList() {
        graphQlTester
                .document("{ tasks { id title done } }")
                .execute()
                .path("tasks")
                .entityList(Task.class)
                .hasSize(0);
    }

    @Test
    void createTask_addsTaskAndReturnsIt() {
        graphQlTester
                .document("mutation { createTask(title: \"Buy milk\") { id title done } }")
                .execute()
                .path("createTask.title").entity(String.class).isEqualTo("Buy milk")
                .path("createTask.done").entity(Boolean.class).isEqualTo(false);
    }

    @Test
    void queryTask_byId_returnsCorrectTask() {
        // Step 1: create a task and capture its generated id
        String id = graphQlTester
                .document("mutation { createTask(title: \"Go running\") { id } }")
                .execute()
                .path("createTask.id").entity(String.class).get();

        // Step 2: query by that id and assert the title comes back correctly
        graphQlTester
                .document(String.format("{ task(id: \"%s\") { id title } }", id))
                .execute()
                .path("task.title").entity(String.class).isEqualTo("Go running");
    }

    @Test
    void completeTask_setsDoneTrue() {
        // Create a task first, then complete it
        String id = graphQlTester
                .document("mutation { createTask(title: \"Read book\") { id } }")
                .execute()
                .path("createTask.id").entity(String.class).get();

        graphQlTester
                .document(String.format(
                        "mutation { completeTask(id: \"%s\") { id done } }", id))
                .execute()
                .path("completeTask.done").entity(Boolean.class).isEqualTo(true);
    }

    @Test
    void queryTask_unknownId_returnsNull() {
        graphQlTester
                .document("{ task(id: \"999\") { id title } }")
                .execute()
                .path("task").valueIsNull();
    }
}
