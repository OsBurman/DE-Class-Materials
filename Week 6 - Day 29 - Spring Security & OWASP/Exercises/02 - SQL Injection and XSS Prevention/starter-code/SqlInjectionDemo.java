package com.security;

import java.sql.*;
import java.util.Map;

/**
 * Exercise 02 – SQL Injection Prevention
 *
 * The static USERS map simulates a database table.
 * Use it inside your PreparedStatement simulation below.
 */
public class SqlInjectionDemo {

    /** Simulated in-memory "database" of users */
    private static final Map<String, String> USERS = Map.of(
            "alice", "alice",
            "bob",   "bob",
            "carol", "carol"
    );

    /**
     * VULNERABLE version — shown for comparison only. Do NOT call this in tests.
     */
    public static String findUserVulnerable(String username) {
        // Simulates: "SELECT username FROM users WHERE username = '" + username + "'"
        // Attack: username = "' OR '1'='1"  → matches everything
        if (username.contains("'")) {
            return "all users";   // simulates the injection succeeding
        }
        return USERS.getOrDefault(username, "not found");
    }

    /**
     * TODO: Rewrite this method to use a simulated PreparedStatement.
     *
     * Because we have no real JDBC connection here, simulate parameterisation by:
     *   1. Checking that `username` is a plain string (no single quotes or SQL meta-chars).
     *      Use username.matches("[a-zA-Z0-9_]+") — if it fails return "not found".
     *   2. Look up the username in the USERS map and return the value, or "not found".
     *
     * This models the behaviour of a real PreparedStatement where the driver
     * escapes the parameter before embedding it in the query.
     */
    public static String findUserByUsername(String username) {
        // TODO: Validate username with username.matches("[a-zA-Z0-9_]+")
        //       Return "not found" if validation fails
        // TODO: Return USERS.getOrDefault(username, "not found")
        return null;
    }

    /**
     * TODO: Rewrite this method to use a simulated PreparedStatement.
     *
     * Validate that username contains only safe characters [a-zA-Z0-9_].
     * If validation fails, throw IllegalArgumentException("Unsafe username").
     * Otherwise print:
     *   "PreparedStatement: DELETE FROM users WHERE username = ?" + username
     */
    public static void deleteUserByUsername(String username) {
        // TODO: Validate username
        // TODO: Print the safe simulated query
    }

    public static void main(String[] args) {
        System.out.println("=== SQL Injection Demo ===");
        System.out.println("findUser(\"alice\")             → " + findUserByUsername("alice"));
        System.out.println("findUser(\"' OR '1'='1\")       → " + findUserByUsername("' OR '1'='1"));
        deleteUserByUsername("alice");
    }
}
