package com.security.dbauth.config;

import com.security.dbauth.model.AppUser;
import com.security.dbauth.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Seeds the H2 database with two test users on application startup.
 *
 * Credentials seeded:
 *   username=user   password=password  role=USER
 *   username=admin  password=admin123  role=ADMIN
 *
 * Passwords are BCrypt-encoded via the PasswordEncoder bean declared in
 * SecurityConfig, which is why it is injected here.
 *
 * This class already works – no changes needed.
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
