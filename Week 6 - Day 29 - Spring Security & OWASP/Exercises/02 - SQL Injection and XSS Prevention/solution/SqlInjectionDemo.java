package com.security;

import java.util.Map;

/**
 * Solution – Exercise 02 SQL Injection Prevention
 */
public class SqlInjectionDemo {

    private static final Map<String, String> USERS = Map.of(
            "alice", "alice",
            "bob",   "bob",
            "carol", "carol"
    );

    public static String findUserByUsername(String username) {
        // Whitelist validation simulates PreparedStatement parameterisation.
        // A real PreparedStatement would escape the value at the driver level.
        if (!username.matches("[a-zA-Z0-9_]+")) {
            return "not found";   // injection attempt rejected
        }
        return USERS.getOrDefault(username, "not found");
    }

    public static void deleteUserByUsername(String username) {
        if (!username.matches("[a-zA-Z0-9_]+")) {
            throw new IllegalArgumentException("Unsafe username");
        }
        // Simulates: PreparedStatement ps = conn.prepareStatement(
        //   "DELETE FROM users WHERE username = ?"); ps.setString(1, username);
        System.out.println("PreparedStatement: DELETE FROM users WHERE username = " + username);
    }

    public static void main(String[] args) {
        System.out.println("=== SQL Injection Demo ===");
        System.out.println("findUser(\"alice\")             → " + findUserByUsername("alice"));
        System.out.println("findUser(\"' OR '1'='1\")       → " + findUserByUsername("' OR '1'='1"));
        deleteUserByUsername("alice");
    }
}
