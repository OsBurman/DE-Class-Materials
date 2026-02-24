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

    // In-memory store – fine for this exercise
    private final List<Task> tasks = new ArrayList<>();

    // Hot sink: new tasks published here are pushed to all subscribers
    // Sinks.many().multicast().onBackpressureBuffer() allows multiple subscribers
    private final Sinks.Many<Task> taskSink =
            Sinks.many().multicast().onBackpressureBuffer();

    // ─── Queries ──────────────────────────────────────────────────────────

    /**
     * TODO 6: Annotate this method with @QueryMapping
     *         Return all tasks from the in-memory list.
     */
    public List<Task> tasks() {
        // TODO 6: return the tasks list
        return null;
    }

    /**
     * TODO 7: Annotate this method with @QueryMapping
     *         Use @Argument on the id parameter.
     *         Find and return the Task whose id equals the argument, or null.
     */
    public Task task(String id) {
        // TODO 7: stream tasks, filter by id, return first match or null
        return null;
    }

    // ─── Mutations ────────────────────────────────────────────────────────

    /**
     * TODO 8: Annotate this method with @MutationMapping
     *         Use @Argument on the title parameter.
     *         1. Create a new Task with UUID id, given title, done=false
     *         2. Add it to the tasks list
     *         3. Emit it on taskSink with taskSink.tryEmitNext(newTask)
     *         4. Return the new task
     */
    public Task createTask(String title) {
        // TODO 8: build Task, add to list, emit to sink, return task
        return null;
    }

    /**
     * TODO 9: Annotate with @MutationMapping
     *         Use @Argument on id.
     *         Find the task by id. If found, replace it with a copy where done=true.
     *         Return the updated task, or null if not found.
     *         Hint: records are immutable – use new Task(task.id(), task.title(), true)
     */
    public Task completeTask(String id) {
        // TODO 9: find task, mark done=true, replace in list, return updated task
        return null;
    }

    // ─── Subscriptions ────────────────────────────────────────────────────

    /**
     * TODO 10: Annotate with @SubscriptionMapping
     *          Return a Flux<Task> from the taskSink.
     *          Hint: taskSink.asFlux()
     */
    public Flux<Task> taskCreated() {
        // TODO 10: return the Flux that delivers new tasks to subscribers
        return null;
    }
}
