package com.security.csrfauth.controller;

import org.springframework.web.bind.annotation.*;

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
