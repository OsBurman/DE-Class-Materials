package com.security.dbauth.service;

import com.security.dbauth.model.AppUser;
import com.security.dbauth.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Database-backed implementation of {@link UserDetailsService}.
 *
 * Spring Security calls {@code loadUserByUsername()} each time a request
 * needs to be authenticated.  We look the user up in H2 via JPA and convert
 * the {@link AppUser} entity into a Spring Security {@link UserDetails} object.
 *
 * Key points:
 *  • The password stored in AppUser is a BCrypt hash – we hand it straight to
 *    the builder; Spring Security compares it with the incoming plaintext
 *    password using the BCryptPasswordEncoder declared in SecurityConfig.
 *  • {@code .roles(appUser.getRole())} automatically prepends "ROLE_" so the
 *    stored value "ADMIN" becomes the authority "ROLE_ADMIN".
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private AppUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found: " + username));

        return User.withUsername(appUser.getUsername())
                   .password(appUser.getPassword())
                   .roles(appUser.getRole())   // stores "USER" → authority "ROLE_USER"
                   .build();
    }
}
