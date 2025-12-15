package com.fci.seminar.model;

import com.fci.seminar.model.enums.Role;
import java.util.Objects;

/**
 * Evaluator user who can evaluate assigned presentations.
 */
public class Evaluator extends User {
    private String department;
    private String expertise;

    public Evaluator() {
        this.role = Role.EVALUATOR;
    }

    public Evaluator(Long id, String username, String password, String name, String email,
                     String department, String expertise) {
        super(id, username, password, name, email, Role.EVALUATOR);
        this.department = department;
        this.expertise = expertise;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getExpertise() {
        return expertise;
    }

    public void setExpertise(String expertise) {
        this.expertise = expertise;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Evaluator evaluator = (Evaluator) o;
        return Objects.equals(department, evaluator.department) &&
               Objects.equals(expertise, evaluator.expertise);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), department, expertise);
    }

    @Override
    public String toString() {
        return "Evaluator{" +
               "id=" + id +
               ", username='" + username + '\'' +
               ", name='" + name + '\'' +
               ", department='" + department + '\'' +
               ", expertise='" + expertise + '\'' +
               '}';
    }
}
