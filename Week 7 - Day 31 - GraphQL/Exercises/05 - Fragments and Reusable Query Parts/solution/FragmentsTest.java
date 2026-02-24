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

    @Test
    void namedFragment_reusedInTwoQueries() {
        String id1 = createTask("Fragment task A");
        String id2 = createTask("Fragment task B");

        // Fragment declared once, spread in two alias fields — avoids duplicating field lists
        String doc = """
                fragment TaskFields on Task { id title done }
                query {
                  taskA: task(id: "%s") { ...TaskFields }
                  taskB: task(id: "%s") { ...TaskFields }
                }
                """.formatted(id1, id2);

        graphQlTester.document(doc).execute()
                .path("taskA.title").entity(String.class).isEqualTo("Fragment task A")
                .path("taskB.title").entity(String.class).isEqualTo("Fragment task B");
    }

    @Test
    void namedFragment_inListQuery() {
        createTask("Task alpha");
        createTask("Task beta");
        createTask("Task gamma");

        String doc = """
                fragment TaskFields on Task { id title done }
                query { tasks { ...TaskFields } }
                """;

        graphQlTester.document(doc).execute()
                .path("tasks").entityList(Task.class)
                .hasSizeGreaterThanOrEqualTo(3);
    }

    @Test
    void inlineFragment_selectsTypeSpecificField() {
        // id starts with "p-" → controller returns a PriorityTask
        // ... on PriorityTask { priority } selects the type-specific field
        graphQlTester
                .document("{ taskOrPriority(id: \"p-1\") { id title ... on PriorityTask { priority } } }")
                .execute()
                .path("taskOrPriority.priority").hasValue();
    }

    @Test
    void inlineFragment_regularTask_priorityAbsent() {
        // id does NOT start with "p-" → controller returns a Task; priority field absent
        graphQlTester
                .document("{ taskOrPriority(id: \"regular-1\") { id title ... on PriorityTask { priority } } }")
                .execute()
                .path("taskOrPriority.priority").pathDoesNotExist();
    }
}
