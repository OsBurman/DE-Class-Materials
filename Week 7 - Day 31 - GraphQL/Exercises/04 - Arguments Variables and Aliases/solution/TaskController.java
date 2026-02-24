package com.graphql.tasks;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
public class TaskController {

    // Simple in-memory store – no database needed for this exercise
    private final List<Task> tasks = new ArrayList<>();

    // Hot multicast sink: every subscriber receives every new task emitted
    private final Sinks.Many<Task> taskSink =
            Sinks.many().multicast().onBackpressureBuffer();

    // ─── Queries ──────────────────────────────────────────────────────────

    /** Returns all tasks. Maps to GraphQL Query.tasks */
    @QueryMapping
    public List<Task> tasks() {
        return List.copyOf(tasks);
    }

    /** Returns a single task by ID, or null if not found. Maps to Query.task */
    @QueryMapping
    public Task task(@Argument String id) {
        return tasks.stream()
                    .filter(t -> t.id().equals(id))
                    .findFirst()
                    .orElse(null);
    }

    // ─── Mutations ────────────────────────────────────────────────────────

    /** Creates a new task, stores it, notifies subscribers, returns it. */
    @MutationMapping
    public Task createTask(@Argument String title) {
        Task newTask = new Task(UUID.randomUUID().toString(), title, false);
        tasks.add(newTask);
        // Emit to the sink so subscription clients receive this task
        taskSink.tryEmitNext(newTask);
        return newTask;
    }

    /**
     * Marks a task done.  Records are immutable so we replace the element in the list.
     * Returns null if the id is not found.
     */
    @MutationMapping
    public Task completeTask(@Argument String id) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).id().equals(id)) {
                Task updated = tasks.get(i).withDone(true);
                tasks.set(i, updated);
                return updated;
            }
        }
        return null; // id not found – schema allows nullable return
    }

    // ─── Subscriptions ────────────────────────────────────────────────────

    /**
     * Returns a Flux that emits a Task every time createTask is called.
     * Spring for GraphQL wraps this in a WebSocket or SSE subscription transport.
     */
    @SubscriptionMapping
    public Flux<Task> taskCreated() {
        return taskSink.asFlux();
    }
}
