package com.graphql.tasks;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.graphql.test.tester.GraphQlTester;

@GraphQlTest(TaskController.class)
class FragmentsTest {

    @Autowired
    private GraphQlTester graphQlTester;

    private String createTask(String title) {
        return graphQlTester
                .document("mutation { createTask(title: \"" + title + "\") { id } }")
                .execute()
                .path("createTask.id").entity(String.class).get();
    }

    /**
     * TODO 1: Define a named fragment "TaskFields on Task { id title done }".
     *         Use aliases and spread the fragment to query two tasks in one request.
     *         Assert taskA.title == "Fragment task A" and taskB.title == "Fragment task B".
     *
     *         The document string should look like:
     *           fragment TaskFields on Task { id title done }
     *           query { taskA: task(id: "<id1>") { ...TaskFields } taskB: task(id: "<id2>") { ...TaskFields } }
     */
    @Test
    void namedFragment_reusedInTwoQueries() {
        // TODO 1: create two tasks, then build and execute the document with the fragment
    }

    /**
     * TODO 2: Create three tasks.
     *         Query "tasks { ...TaskFields }" with the TaskFields fragment in the same document.
     *         Assert the list has at least 3 items.
     *         Hint: .path("tasks").entityList(Task.class).hasSizeGreaterThanOrEqualTo(3)
     */
    @Test
    void namedFragment_inListQuery() {
        // TODO 2
    }

    /**
     * TODO 3: Query taskOrPriority(id: "p-1") using an inline fragment:
     *           { taskOrPriority(id: "p-1") { id title ... on PriorityTask { priority } } }
     *         Assert path("taskOrPriority.priority") is not null.
     *         Hint: .path("taskOrPriority.priority").hasValue()
     */
    @Test
    void inlineFragment_selectsTypeSpecificField() {
        // TODO 3
    }

    /**
     * TODO 4 (bonus): Query taskOrPriority with a regular id (not starting with "p-").
     *                 Assert that path("taskOrPriority.priority") is absent (not a PriorityTask).
     *                 Hint: .path("taskOrPriority.priority").pathDoesNotExist()
     */
    @Test
    void inlineFragment_regularTask_priorityAbsent() {
        // TODO 4
    }
}
