package com.security.dbauth.repository;

import com.security.dbauth.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for {@link AppUser}.
 *
 * Spring generates the implementation automatically at runtime.
 * The {@code findByUsername} derived query is the key method used by
 * {@code CustomUserDetailsService} during authentication.
 */
@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    /**
     * Look up a user by their login name.
     *
     * @param username the username to search for
     * @return an Optional containing the user, or empty if not found
     */
    Optional<AppUser> findByUsername(String username);
}
