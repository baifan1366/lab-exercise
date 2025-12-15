package com.fci.seminar.model;

import com.fci.seminar.model.enums.Role;
import java.util.Objects;

/**
 * Coordinator user with full control over the system.
 */
public class Coordinator extends User {
    private String staffId;

    public Coordinator() {
        this.role = Role.COORDINATOR;
    }

    public Coordinator(Long id, String username, String password, String name, String email,
                       String staffId) {
        super(id, username, password, name, email, Role.COORDINATOR);
        this.staffId = staffId;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Coordinator that = (Coordinator) o;
        return Objects.equals(staffId, that.staffId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), staffId);
    }

    @Override
    public String toString() {
        return "Coordinator{" +
               "id=" + id +
               ", username='" + username + '\'' +
               ", name='" + name + '\'' +
               ", staffId='" + staffId + '\'' +
               '}';
    }
}
