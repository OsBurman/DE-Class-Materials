package com.graphql.tasks;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.graphql.test.tester.GraphQlTester;

import static org.assertj.core.api.Assertions.assertThat;

@GraphQlTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private GraphQlTester graphQlTester;

    // TODO 11: Test that querying all tasks returns an empty list before any mutations.
    //   Hint: graphQlTester.document("{ tasks { id title done } }")
    //              .execute()
    //              .path("tasks")
    //              .entityList(Task.class)
    //              .hasSize(0);
    @Test
    void queryAllTasks_returnsEmptyList() {
        // TODO 11
    }

    // TODO 12: Test that createTask returns a Task with the correct title and done=false.
    //   Use a mutation document string:
    //   "mutation { createTask(title: \"Buy milk\") { id title done } }"
    //   Assert title == "Buy milk" and done == false.
    @Test
    void createTask_addsTaskAndReturnsIt() {
        // TODO 12
    }

    // TODO 13: Test that task(id) returns the correct task.
    //   1. Create a task and capture the returned id.
    //   2. Query task(id: "<id>") and assert the title matches.
    //   Hint: .path("createTask.id").entity(String.class).get()
    @Test
    void queryTask_byId_returnsCorrectTask() {
        // TODO 13
    }

    // TODO 14: Test that completeTask sets done=true.
    //   1. Create a task and capture the id.
    //   2. Run mutation completeTask(id: "<id>") { id done }
    //   3. Assert done == true.
    @Test
    void completeTask_setsDoneTrue() {
        // TODO 14
    }

    // TODO 15: Test that querying with an unknown id returns null.
    //   Use query: "{ task(id: \"999\") { id title } }"
    //   Assert the path "task" is null.
    //   Hint: .path("task").valueIsNull()
    @Test
    void queryTask_unknownId_returnsNull() {
        // TODO 15
    }
}
