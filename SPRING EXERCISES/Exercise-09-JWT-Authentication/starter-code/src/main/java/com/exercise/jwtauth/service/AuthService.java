package com.exercise.jwtauth.service;

import com.exercise.jwtauth.dto.AuthResponse;
import com.exercise.jwtauth.dto.LoginRequest;
import com.exercise.jwtauth.dto.RegisterRequest;
import com.exercise.jwtauth.entity.User;
import com.exercise.jwtauth.jwt.JwtUtil;
import com.exercise.jwtauth.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    // TODO 8: Implement register:
    //         1. Check for duplicate username → throw RuntimeException("Username taken")
    //         2. Check for duplicate email → throw RuntimeException("Email in use")
    //         3. Create User, set username/email, set password = passwordEncoder.encode(request.getPassword())
    //         4. Save user
    //         5. Return new AuthResponse(jwtUtil.generateToken(user.getUsername(), user.getRole()),
    //                                    user.getUsername(), user.getRole())
    public AuthResponse register(RegisterRequest request) {
        // TODO 8: implement this
        throw new UnsupportedOperationException("TODO 8: implement register");
    }

    // TODO 9: Implement login:
    //         1. Authenticate: authenticationManager.authenticate(
    //                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()))
    //            (If credentials wrong, Spring throws AuthenticationException → 401)
    //         2. Load the user: userRepository.findByUsername(request.getUsername()).orElseThrow(...)
    //         3. Return new AuthResponse(jwtUtil.generateToken(user.getUsername(), user.getRole()),
    //                                    user.getUsername(), user.getRole())
    public AuthResponse login(LoginRequest request) {
        // TODO 9: implement this
        throw new UnsupportedOperationException("TODO 9: implement login");
    }
}
