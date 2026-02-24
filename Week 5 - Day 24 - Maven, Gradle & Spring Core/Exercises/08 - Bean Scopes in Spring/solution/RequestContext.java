package com.library;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Scope("prototype")  // new instance created on every getBean() call
public class RequestContext {

    private final String requestId;

    public RequestContext() {
        // Generate a short unique ID to identify this instance
        this.requestId = UUID.randomUUID().toString().substring(0, 8);
    }

    public String getRequestId() {
        return requestId;
    }
}
