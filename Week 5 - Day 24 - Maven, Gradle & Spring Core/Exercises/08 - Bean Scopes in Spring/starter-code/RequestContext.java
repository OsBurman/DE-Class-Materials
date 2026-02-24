package com.library;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.UUID;

// TODO: Add @Component
// TODO: Add @Scope("prototype") — a new instance is created every time this bean is requested
public class RequestContext {

    private final String requestId;

    public RequestContext() {
        // TODO: Assign requestId to the first 8 characters of a random UUID
        //       Hint: UUID.randomUUID().toString().substring(0, 8)
        this.requestId = "TODO";
    }

    /**
     * Returns the unique ID assigned to this request context instance.
     *
     * @return the request ID string
     */
    public String getRequestId() {
        // TODO: Return requestId
        return null;
    }
}
