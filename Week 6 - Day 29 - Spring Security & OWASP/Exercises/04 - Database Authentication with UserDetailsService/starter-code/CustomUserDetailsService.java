package com.security.dbauth.service;

import com.security.dbauth.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Exercise 04 – Task 1: Implement a database-backed UserDetailsService.
 *
 * Spring Security calls {@code loadUserByUsername()} during HTTP Basic / form-login
 * authentication.  Your job is to look the user up in the database and return
 * a {@link UserDetails} object that Spring Security can verify the password against.
 *
 * Steps:
 *   1. Query {@code userRepository.findByUsername(username)}.
 *   2. If the Optional is empty, throw {@code new UsernameNotFoundException(...)}.
 *   3. Use {@code org.springframework.security.core.userdetails.User.withUsername()}
 *      to build and return a {@link UserDetails} object.
 *      - Set the encoded password from the AppUser entity.
 *      - Set the role via {@code .roles(appUser.getRole())}.
 *        (Spring Security will automatically prepend "ROLE_" for you.)
 *
 * Hint – builder pattern:
 *   return User.withUsername(appUser.getUsername())
 *              .password(appUser.getPassword())
 *              .roles(appUser.getRole())
 *              .build();
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private AppUserRepository userRepository;

    /**
     * TODO: Implement this method.
     *
     * @param username the username supplied by the client
     * @return a populated UserDetails object
     * @throws UsernameNotFoundException if the user does not exist in the database
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // TODO: Find the user in the database.
        // TODO: Throw UsernameNotFoundException if not found.
        // TODO: Build and return a UserDetails object.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
