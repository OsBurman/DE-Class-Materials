package com.academy.auth.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Simple user model.
 * In a real app this would be a JPA entity.
 * This class is COMPLETE.
 */
@Data
@AllArgsConstructor
public class User {
    private String username;
    private String encodedPassword;
    private Role role;
}
