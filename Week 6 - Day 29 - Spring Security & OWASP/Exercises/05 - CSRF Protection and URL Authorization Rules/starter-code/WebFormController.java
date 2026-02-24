package com.security.csrfauth.controller;

import org.springframework.web.bind.annotation.*;

/**
 * Controller that simulates a form-based web application.
 *
 * The /form paths are covered by the form security filter chain
 * (CSRF enabled, form login).
 *
 * In a real Thymeleaf app the form HTML would contain:
 *   <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
 * MockMvc simulates this with the csrf() post-processor.
 *
 * This class is already fully implemented – no changes needed.
 */
@RestController
@RequestMapping("/form")
public class WebFormController {

    @GetMapping("/dashboard")
    public String dashboard() {
        return "Welcome to the dashboard";
    }

    @PostMapping("/submit")
    public String submitForm(@RequestBody String data) {
        return "Form submitted: " + data;
    }
}
