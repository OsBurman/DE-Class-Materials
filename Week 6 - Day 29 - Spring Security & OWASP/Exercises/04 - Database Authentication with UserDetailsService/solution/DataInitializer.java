package com.security.dbauth.config;

import com.security.dbauth.model.AppUser;
import com.security.dbauth.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Seeds the H2 database with two test users on application startup.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        userRepository.save(
                new AppUser(null, "user",  passwordEncoder.encode("password"), "USER"));
        userRepository.save(
                new AppUser(null, "admin", passwordEncoder.encode("admin123"),  "ADMIN"));

        System.out.println("Seeded " + userRepository.count() + " users.");
    }
}
