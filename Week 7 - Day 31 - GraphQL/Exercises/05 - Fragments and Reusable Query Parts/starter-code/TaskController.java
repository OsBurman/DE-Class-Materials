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

    private final List<Task> tasks = new ArrayList<>();
    private final Sinks.Many<Task> taskSink =
            Sinks.many().multicast().onBackpressureBuffer();

    @QueryMapping
    public List<Task> tasks() {
        return List.copyOf(tasks);
    }

    @QueryMapping
    public Task task(@Argument String id) {
        return tasks.stream().filter(t -> t.id().equals(id)).findFirst().orElse(null);
    }

    /**
     * TODO 7: Add a @QueryMapping method taskOrPriority(@Argument String id)
     *         that returns a TaskInterface.
     *         - If id starts with "p-" return new PriorityTask(id, "Priority: " + id, false, 1)
     *         - Otherwise return a Task from the list, or a new Task(id, "Regular: " + id, false)
     */
    // TODO 7: taskOrPriority method here

    @MutationMapping
    public Task createTask(@Argument String title) {
        Task newTask = new Task(UUID.randomUUID().toString(), title, false);
        tasks.add(newTask);
        taskSink.tryEmitNext(newTask);
        return newTask;
    }

    @MutationMapping
    public Task completeTask(@Argument String id) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).id().equals(id)) {
                Task updated = tasks.get(i).withDone(true);
                tasks.set(i, updated);
                return updated;
            }
        }
        return null;
    }

    @SubscriptionMapping
    public Flux<Task> taskCreated() {
        return taskSink.asFlux();
    }
}
