package com.exercise.socialmedia.service;

import com.exercise.socialmedia.dto.AuthResponse;
import com.exercise.socialmedia.dto.LoginRequest;
import com.exercise.socialmedia.dto.RegisterRequest;
import com.exercise.socialmedia.entity.User;
import com.exercise.socialmedia.exception.DuplicateResourceException;
import com.exercise.socialmedia.jwt.JwtUtil;
import com.exercise.socialmedia.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
        this.userRepository = userRepository; this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil; this.authenticationManager = authenticationManager;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername()))
            throw new DuplicateResourceException("Username already taken: " + request.getUsername());
        if (userRepository.existsByEmail(request.getEmail()))
            throw new DuplicateResourceException("Email already in use: " + request.getEmail());
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setBio(request.getBio());
        userRepository.save(user);
        return new AuthResponse(jwtUtil.generateToken(user.getUsername(), user.getRole()), user.getUsername(), user.getRole());
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow();
        return new AuthResponse(jwtUtil.generateToken(user.getUsername(), user.getRole()), user.getUsername(), user.getRole());
    }
}
