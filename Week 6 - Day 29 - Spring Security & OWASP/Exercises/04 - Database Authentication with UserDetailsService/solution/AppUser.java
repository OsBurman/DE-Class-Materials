package com.security.dbauth.model;

import jakarta.persistence.*;

/**
 * JPA entity that represents an application user stored in the database.
 */
@Entity
@Table(name = "app_users")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role;

    public AppUser() {}

    public AppUser(Long id, String username, String password, String role) {
        this.id       = id;
        this.username = username;
        this.password = password;
        this.role     = role;
    }

    public Long getId()                   { return id; }
    public void setId(Long id)            { this.id = id; }

    public String getUsername()           { return username; }
    public void setUsername(String u)     { this.username = u; }

    public String getPassword()           { return password; }
    public void setPassword(String p)     { this.password = p; }

    public String getRole()               { return role; }
    public void setRole(String r)         { this.role = r; }
}
