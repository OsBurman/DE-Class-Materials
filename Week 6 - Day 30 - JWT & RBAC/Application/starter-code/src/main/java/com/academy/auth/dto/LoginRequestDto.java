package com.academy.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request body for POST /auth/login.
 * This class is COMPLETE.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {
    private String username;
    private String password;
}
