package com.fci.seminar.model;

import com.fci.seminar.model.enums.Role;
import java.util.Objects;

/**
 * Student user who can register for seminars and upload presentation materials.
 */
public class Student extends User {
    private String studentId;
    private String program;
    private String supervisor;

    public Student() {
        this.role = Role.STUDENT;
    }

    public Student(Long id, String username, String password, String name, String email,
                   String studentId, String program, String supervisor) {
        super(id, username, password, name, email, Role.STUDENT);
        this.studentId = studentId;
        this.program = program;
        this.supervisor = supervisor;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(String supervisor) {
        this.supervisor = supervisor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Student student = (Student) o;
        return Objects.equals(studentId, student.studentId) &&
               Objects.equals(program, student.program) &&
               Objects.equals(supervisor, student.supervisor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), studentId, program, supervisor);
    }

    @Override
    public String toString() {
        return "Student{" +
               "id=" + id +
               ", username='" + username + '\'' +
               ", name='" + name + '\'' +
               ", studentId='" + studentId + '\'' +
               ", program='" + program + '\'' +
               ", supervisor='" + supervisor + '\'' +
               '}';
    }
}
