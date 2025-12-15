package com.fci.seminar.repository;

import com.fci.seminar.model.*;
import com.fci.seminar.model.enums.Role;
import com.fci.seminar.util.DataManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository for User entity operations.
 * Handles CRUD operations for Students, Evaluators, and Coordinators.
 * Requirements: 1.3, 1.4
 */
public class UserRepository {
    private final DataManager dataManager;
    
    public UserRepository() {
        this.dataManager = DataManager.getInstance();
    }
    
    /**
     * Constructor for testing with custom DataManager.
     */
    public UserRepository(DataManager dataManager) {
        this.dataManager = dataManager;
    }
    
    // ==================== Find Operations ====================
    
    /**
     * Finds a user by ID across all user types.
     */
    public User findById(Long id) {
        if (id == null) return null;
        
        // Search in students
        for (Student student : dataManager.getStudents()) {
            if (id.equals(student.getId())) {
                return student;
            }
        }
        
        // Search in evaluators
        for (Evaluator evaluator : dataManager.getEvaluators()) {
            if (id.equals(evaluator.getId())) {
                return evaluator;
            }
        }
        
        // Search in coordinators
        for (Coordinator coordinator : dataManager.getCoordinators()) {
            if (id.equals(coordinator.getId())) {
                return coordinator;
            }
        }
        
        return null;
    }

    
    /**
     * Finds all users across all types.
     */
    public List<User> findAll() {
        return dataManager.getAllUsers();
    }
    
    /**
     * Finds a user by username across all user types.
     * Requirements: 1.3
     */
    public User findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) return null;
        
        // Search in students
        for (Student student : dataManager.getStudents()) {
            if (username.equals(student.getUsername())) {
                return student;
            }
        }
        
        // Search in evaluators
        for (Evaluator evaluator : dataManager.getEvaluators()) {
            if (username.equals(evaluator.getUsername())) {
                return evaluator;
            }
        }
        
        // Search in coordinators
        for (Coordinator coordinator : dataManager.getCoordinators()) {
            if (username.equals(coordinator.getUsername())) {
                return coordinator;
            }
        }
        
        return null;
    }
    
    /**
     * Finds a user by username and role.
     * Requirements: 1.3, 1.4
     */
    public User findByUsernameAndRole(String username, Role role) {
        if (username == null || role == null) return null;
        
        switch (role) {
            case STUDENT:
                for (Student student : dataManager.getStudents()) {
                    if (username.equals(student.getUsername())) {
                        return student;
                    }
                }
                break;
            case EVALUATOR:
                for (Evaluator evaluator : dataManager.getEvaluators()) {
                    if (username.equals(evaluator.getUsername())) {
                        return evaluator;
                    }
                }
                break;
            case COORDINATOR:
                for (Coordinator coordinator : dataManager.getCoordinators()) {
                    if (username.equals(coordinator.getUsername())) {
                        return coordinator;
                    }
                }
                break;
            default:
                return null;
        }
        
        return null;
    }
    
    /**
     * Finds all users with a specific role.
     * Requirements: 1.3
     */
    public List<User> findByRole(Role role) {
        if (role == null) return new ArrayList<>();
        
        switch (role) {
            case STUDENT:
                return new ArrayList<>(dataManager.getStudents());
            case EVALUATOR:
                return new ArrayList<>(dataManager.getEvaluators());
            case COORDINATOR:
                return new ArrayList<>(dataManager.getCoordinators());
            default:
                return new ArrayList<>();
        }
    }
    
    /**
     * Finds all students.
     */
    public List<Student> findAllStudents() {
        return dataManager.getStudents();
    }
    
    /**
     * Finds all evaluators.
     */
    public List<Evaluator> findAllEvaluators() {
        return dataManager.getEvaluators();
    }
    
    /**
     * Finds all coordinators.
     */
    public List<Coordinator> findAllCoordinators() {
        return dataManager.getCoordinators();
    }

    
    // ==================== Save Operations ====================
    
    /**
     * Saves a user (creates new or updates existing).
     */
    public User save(User user) {
        if (user == null) return null;
        
        if (user instanceof Student) {
            return saveStudent((Student) user);
        } else if (user instanceof Evaluator) {
            return saveEvaluator((Evaluator) user);
        } else if (user instanceof Coordinator) {
            return saveCoordinator((Coordinator) user);
        }
        
        return null;
    }
    
    /**
     * Saves a student.
     */
    public Student saveStudent(Student student) {
        if (student == null) return null;
        
        List<Student> students = dataManager.getStudents();
        
        if (student.getId() == null) {
            // New student - assign ID
            student.setId(dataManager.getNextId(Student.class));
            students.add(student);
        } else {
            // Update existing student
            boolean found = false;
            for (int i = 0; i < students.size(); i++) {
                if (student.getId().equals(students.get(i).getId())) {
                    students.set(i, student);
                    found = true;
                    break;
                }
            }
            if (!found) {
                students.add(student);
            }
        }
        
        dataManager.setStudents(students);
        dataManager.saveUsers();
        return student;
    }
    
    /**
     * Saves an evaluator.
     */
    public Evaluator saveEvaluator(Evaluator evaluator) {
        if (evaluator == null) return null;
        
        List<Evaluator> evaluators = dataManager.getEvaluators();
        
        if (evaluator.getId() == null) {
            // New evaluator - assign ID
            evaluator.setId(dataManager.getNextId(Evaluator.class));
            evaluators.add(evaluator);
        } else {
            // Update existing evaluator
            boolean found = false;
            for (int i = 0; i < evaluators.size(); i++) {
                if (evaluator.getId().equals(evaluators.get(i).getId())) {
                    evaluators.set(i, evaluator);
                    found = true;
                    break;
                }
            }
            if (!found) {
                evaluators.add(evaluator);
            }
        }
        
        dataManager.setEvaluators(evaluators);
        dataManager.saveUsers();
        return evaluator;
    }
    
    /**
     * Saves a coordinator.
     */
    public Coordinator saveCoordinator(Coordinator coordinator) {
        if (coordinator == null) return null;
        
        List<Coordinator> coordinators = dataManager.getCoordinators();
        
        if (coordinator.getId() == null) {
            // New coordinator - assign ID
            coordinator.setId(dataManager.getNextId(Coordinator.class));
            coordinators.add(coordinator);
        } else {
            // Update existing coordinator
            boolean found = false;
            for (int i = 0; i < coordinators.size(); i++) {
                if (coordinator.getId().equals(coordinators.get(i).getId())) {
                    coordinators.set(i, coordinator);
                    found = true;
                    break;
                }
            }
            if (!found) {
                coordinators.add(coordinator);
            }
        }
        
        dataManager.setCoordinators(coordinators);
        dataManager.saveUsers();
        return coordinator;
    }

    
    // ==================== Delete Operations ====================
    
    /**
     * Deletes a user by ID.
     */
    public void delete(Long id) {
        if (id == null) return;
        
        // Try to delete from students
        List<Student> students = dataManager.getStudents();
        boolean removed = students.removeIf(s -> id.equals(s.getId()));
        if (removed) {
            dataManager.setStudents(students);
            dataManager.saveUsers();
            return;
        }
        
        // Try to delete from evaluators
        List<Evaluator> evaluators = dataManager.getEvaluators();
        removed = evaluators.removeIf(e -> id.equals(e.getId()));
        if (removed) {
            dataManager.setEvaluators(evaluators);
            dataManager.saveUsers();
            return;
        }
        
        // Try to delete from coordinators
        List<Coordinator> coordinators = dataManager.getCoordinators();
        removed = coordinators.removeIf(c -> id.equals(c.getId()));
        if (removed) {
            dataManager.setCoordinators(coordinators);
            dataManager.saveUsers();
        }
    }
    
    /**
     * Deletes a student by ID.
     */
    public void deleteStudent(Long id) {
        if (id == null) return;
        
        List<Student> students = dataManager.getStudents();
        if (students.removeIf(s -> id.equals(s.getId()))) {
            dataManager.setStudents(students);
            dataManager.saveUsers();
        }
    }
    
    /**
     * Deletes an evaluator by ID.
     */
    public void deleteEvaluator(Long id) {
        if (id == null) return;
        
        List<Evaluator> evaluators = dataManager.getEvaluators();
        if (evaluators.removeIf(e -> id.equals(e.getId()))) {
            dataManager.setEvaluators(evaluators);
            dataManager.saveUsers();
        }
    }
    
    /**
     * Deletes a coordinator by ID.
     */
    public void deleteCoordinator(Long id) {
        if (id == null) return;
        
        List<Coordinator> coordinators = dataManager.getCoordinators();
        if (coordinators.removeIf(c -> id.equals(c.getId()))) {
            dataManager.setCoordinators(coordinators);
            dataManager.saveUsers();
        }
    }
    
    // ==================== Utility Methods ====================
    
    /**
     * Checks if a username already exists.
     */
    public boolean existsByUsername(String username) {
        return findByUsername(username) != null;
    }
    
    /**
     * Counts total users.
     */
    public long count() {
        return dataManager.getStudents().size() + 
               dataManager.getEvaluators().size() + 
               dataManager.getCoordinators().size();
    }
    
    /**
     * Counts users by role.
     */
    public long countByRole(Role role) {
        if (role == null) return 0;
        
        switch (role) {
            case STUDENT:
                return dataManager.getStudents().size();
            case EVALUATOR:
                return dataManager.getEvaluators().size();
            case COORDINATOR:
                return dataManager.getCoordinators().size();
            default:
                return 0;
        }
    }
}
