package com.academy.service;

import org.springframework.stereotype.Service;

/**
 * Second implementation of GreetingService.
 * Use @Qualifier("spanishGreetingService") to inject specifically this bean.
 */
@Service("spanishGreetingService")
public class SpanishGreetingService implements GreetingService {
    @Override
    public String greet(String name) {
        return "¡Hola, " + name + "! Bienvenido a la Academia.";
    }
}
