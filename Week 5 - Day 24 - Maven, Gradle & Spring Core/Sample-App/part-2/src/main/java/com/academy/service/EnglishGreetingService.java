package com.academy.service;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * @Service — stereotype for business-logic beans (specialisation of @Component).
 * @Primary  — this bean is chosen automatically when multiple GreetingService beans exist
 *             and no @Qualifier is specified.
 */
@Service("englishGreetingService")
@Primary
public class EnglishGreetingService implements GreetingService {
    @Override
    public String greet(String name) {
        return "Hello, " + name + "! Welcome to the Academy.";
    }
}
