package com.fci.seminar.model;

import com.fci.seminar.model.enums.Role;
import java.util.Objects;

/**
 * Abstract base class for all user types in the system.
 * Contains common fields shared by Student, Evaluator, and Coordinator.
 */
public abstract class User {
    protected Long id;
    protected String username;
    protected String password;
    protected String name;
    protected String email;
    protected Role role;

    public User() {
    }

    public User(Long id, String username, String password, String name, String email, Role role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) &&
               Objects.equals(username, user.username) &&
               Objects.equals(password, user.password) &&
               Objects.equals(name, user.name) &&
               Objects.equals(email, user.email) &&
               role == user.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, password, name, email, role);
    }

    @Override
    public String toString() {
        return "User{" +
               "id=" + id +
               ", username='" + username + '\'' +
               ", name='" + name + '\'' +
               ", email='" + email + '\'' +
               ", role=" + role +
               '}';
    }
}
