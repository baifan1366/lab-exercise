package com.fci.seminar.ui.panels.coordinator;

import com.fci.seminar.model.Evaluator;
import com.fci.seminar.model.Student;
import com.fci.seminar.model.User;
import com.fci.seminar.repository.UserRepository;
import com.fci.seminar.ui.MainFrame;
import com.fci.seminar.ui.components.CardPanel;
import com.fci.seminar.ui.components.StyledButton;
import com.fci.seminar.ui.components.StyledTable;
import com.fci.seminar.ui.components.StyledTextField;
import com.fci.seminar.util.UIConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel for managing users (students and evaluators).
 * Provides CRUD operations and search/filtering capabilities.
 * Requirements: 1.2
 */
public class UserManagementPanel extends JPanel implements MainFrame.Refreshable {
    
    private final UserRepository userRepository;
    
    // Tab pane for students and evaluators
    private JTabbedPane tabbedPane;
    
    // Students tab components
    private StyledTable studentsTable;
    private DefaultTableModel studentsTableModel;
    private StyledTextField studentSearchField;
    
    // Evaluators tab components
    private StyledTable evaluatorsTable;
    private DefaultTableModel evaluatorsTableModel;
    private StyledTextField evaluatorSearchField;
    
    /**
     * Creates the user management panel.
     */
    public UserManagementPanel() {
        this.userRepository = new UserRepository();
        initComponents();
        loadData();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(UIConstants.SPACING_MD, UIConstants.SPACING_MD));
        setBackground(UIConstants.BG_MAIN);
        setOpaque(false);
        
        // Header
        add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Tabbed pane for students and evaluators
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIConstants.BODY_BOLD);
        tabbedPane.addTab("Students", createStudentsPanel());
        tabbedPane.addTab("Evaluators", createEvaluatorsPanel());
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("User Management");
        titleLabel.setFont(UIConstants.TITLE_MEDIUM);
        titleLabel.setForeground(UIConstants.TEXT_PRIMARY);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        return headerPanel;
    }

    
    private JPanel createStudentsPanel() {
        CardPanel card = new CardPanel();
        JPanel content = card.getContentPanel();
        content.setLayout(new BorderLayout(UIConstants.SPACING_MD, UIConstants.SPACING_MD));
        
        // Toolbar with search and buttons
        JPanel toolbar = new JPanel(new BorderLayout(UIConstants.SPACING_MD, 0));
        toolbar.setOpaque(false);
        
        // Search field
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, UIConstants.SPACING_SM, 0));
        searchPanel.setOpaque(false);
        
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(UIConstants.BODY);
        searchLabel.setForeground(UIConstants.TEXT_SECONDARY);
        searchPanel.add(searchLabel);
        
        studentSearchField = new StyledTextField("Search by name or ID...");
        studentSearchField.setPreferredSize(new Dimension(200, UIConstants.INPUT_HEIGHT));
        studentSearchField.addActionListener(e -> filterStudents());
        searchPanel.add(studentSearchField);
        
        StyledButton searchBtn = StyledButton.secondary("Search");
        searchBtn.addActionListener(e -> filterStudents());
        searchPanel.add(searchBtn);
        
        toolbar.add(searchPanel, BorderLayout.WEST);
        
        // Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, UIConstants.SPACING_SM, 0));
        buttonPanel.setOpaque(false);
        
        StyledButton addBtn = StyledButton.primary("Add Student");
        addBtn.addActionListener(e -> showAddStudentDialog());
        buttonPanel.add(addBtn);
        
        StyledButton editBtn = StyledButton.secondary("Edit");
        editBtn.addActionListener(e -> editSelectedStudent());
        buttonPanel.add(editBtn);
        
        StyledButton deleteBtn = StyledButton.danger("Delete");
        deleteBtn.addActionListener(e -> deleteSelectedStudent());
        buttonPanel.add(deleteBtn);
        
        toolbar.add(buttonPanel, BorderLayout.EAST);
        
        content.add(toolbar, BorderLayout.NORTH);
        
        // Students table
        String[] columns = {"ID", "Username", "Name", "Email", "Student ID", "Program", "Supervisor"};
        studentsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        studentsTable = new StyledTable(studentsTableModel);
        studentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(studentsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER));
        
        content.add(scrollPane, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createEvaluatorsPanel() {
        CardPanel card = new CardPanel();
        JPanel content = card.getContentPanel();
        content.setLayout(new BorderLayout(UIConstants.SPACING_MD, UIConstants.SPACING_MD));
        
        // Toolbar with search and buttons
        JPanel toolbar = new JPanel(new BorderLayout(UIConstants.SPACING_MD, 0));
        toolbar.setOpaque(false);
        
        // Search field
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, UIConstants.SPACING_SM, 0));
        searchPanel.setOpaque(false);
        
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(UIConstants.BODY);
        searchLabel.setForeground(UIConstants.TEXT_SECONDARY);
        searchPanel.add(searchLabel);
        
        evaluatorSearchField = new StyledTextField("Search by name or department...");
        evaluatorSearchField.setPreferredSize(new Dimension(200, UIConstants.INPUT_HEIGHT));
        evaluatorSearchField.addActionListener(e -> filterEvaluators());
        searchPanel.add(evaluatorSearchField);
        
        StyledButton searchBtn = StyledButton.secondary("Search");
        searchBtn.addActionListener(e -> filterEvaluators());
        searchPanel.add(searchBtn);
        
        toolbar.add(searchPanel, BorderLayout.WEST);
        
        // Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, UIConstants.SPACING_SM, 0));
        buttonPanel.setOpaque(false);
        
        StyledButton addBtn = StyledButton.primary("Add Evaluator");
        addBtn.addActionListener(e -> showAddEvaluatorDialog());
        buttonPanel.add(addBtn);
        
        StyledButton editBtn = StyledButton.secondary("Edit");
        editBtn.addActionListener(e -> editSelectedEvaluator());
        buttonPanel.add(editBtn);
        
        StyledButton deleteBtn = StyledButton.danger("Delete");
        deleteBtn.addActionListener(e -> deleteSelectedEvaluator());
        buttonPanel.add(deleteBtn);
        
        toolbar.add(buttonPanel, BorderLayout.EAST);
        
        content.add(toolbar, BorderLayout.NORTH);
        
        // Evaluators table
        String[] columns = {"ID", "Username", "Name", "Email", "Department", "Expertise"};
        evaluatorsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        evaluatorsTable = new StyledTable(evaluatorsTableModel);
        evaluatorsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(evaluatorsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER));
        
        content.add(scrollPane, BorderLayout.CENTER);
        
        return card;
    }
    
    private void loadData() {
        loadStudents();
        loadEvaluators();
    }
    
    private void loadStudents() {
        studentsTableModel.setRowCount(0);
        List<Student> students = userRepository.findAllStudents();
        for (Student student : students) {
            studentsTableModel.addRow(new Object[]{
                student.getId(),
                student.getUsername(),
                student.getName(),
                student.getEmail(),
                student.getStudentId(),
                student.getProgram(),
                student.getSupervisor()
            });
        }
    }
    
    private void loadEvaluators() {
        evaluatorsTableModel.setRowCount(0);
        List<Evaluator> evaluators = userRepository.findAllEvaluators();
        for (Evaluator evaluator : evaluators) {
            evaluatorsTableModel.addRow(new Object[]{
                evaluator.getId(),
                evaluator.getUsername(),
                evaluator.getName(),
                evaluator.getEmail(),
                evaluator.getDepartment(),
                evaluator.getExpertise()
            });
        }
    }

    
    private void filterStudents() {
        String searchText = studentSearchField.getText().toLowerCase().trim();
        studentsTableModel.setRowCount(0);
        
        List<Student> students = userRepository.findAllStudents();
        for (Student student : students) {
            if (searchText.isEmpty() ||
                (student.getName() != null && student.getName().toLowerCase().contains(searchText)) ||
                (student.getStudentId() != null && student.getStudentId().toLowerCase().contains(searchText)) ||
                (student.getUsername() != null && student.getUsername().toLowerCase().contains(searchText))) {
                studentsTableModel.addRow(new Object[]{
                    student.getId(),
                    student.getUsername(),
                    student.getName(),
                    student.getEmail(),
                    student.getStudentId(),
                    student.getProgram(),
                    student.getSupervisor()
                });
            }
        }
    }
    
    private void filterEvaluators() {
        String searchText = evaluatorSearchField.getText().toLowerCase().trim();
        evaluatorsTableModel.setRowCount(0);
        
        List<Evaluator> evaluators = userRepository.findAllEvaluators();
        for (Evaluator evaluator : evaluators) {
            if (searchText.isEmpty() ||
                (evaluator.getName() != null && evaluator.getName().toLowerCase().contains(searchText)) ||
                (evaluator.getDepartment() != null && evaluator.getDepartment().toLowerCase().contains(searchText)) ||
                (evaluator.getUsername() != null && evaluator.getUsername().toLowerCase().contains(searchText))) {
                evaluatorsTableModel.addRow(new Object[]{
                    evaluator.getId(),
                    evaluator.getUsername(),
                    evaluator.getName(),
                    evaluator.getEmail(),
                    evaluator.getDepartment(),
                    evaluator.getExpertise()
                });
            }
        }
    }
    
    private void showAddStudentDialog() {
        JDialog dialog = createStudentDialog(null, "Add Student");
        dialog.setVisible(true);
    }
    
    private void editSelectedStudent() {
        int selectedRow = studentsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a student to edit.", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Long id = (Long) studentsTableModel.getValueAt(selectedRow, 0);
        User user = userRepository.findById(id);
        if (user instanceof Student) {
            JDialog dialog = createStudentDialog((Student) user, "Edit Student");
            dialog.setVisible(true);
        }
    }
    
    private void deleteSelectedStudent() {
        int selectedRow = studentsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a student to delete.", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this student?", 
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            Long id = (Long) studentsTableModel.getValueAt(selectedRow, 0);
            userRepository.deleteStudent(id);
            loadStudents();
        }
    }
    
    private JDialog createStudentDialog(Student student, String title) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), title, true);
        dialog.setSize(400, 450);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(UIConstants.SPACING_LG, UIConstants.SPACING_LG, 
                                                        UIConstants.SPACING_LG, UIConstants.SPACING_LG));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(UIConstants.SPACING_XS, UIConstants.SPACING_XS, 
                                UIConstants.SPACING_XS, UIConstants.SPACING_XS);
        
        // Username
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Username:"), gbc);
        JTextField usernameField = new JTextField(student != null ? student.getUsername() : "");
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(usernameField, gbc);
        
        // Password
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panel.add(new JLabel("Password:"), gbc);
        JPasswordField passwordField = new JPasswordField(student != null ? student.getPassword() : "");
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(passwordField, gbc);
        
        // Name
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        panel.add(new JLabel("Name:"), gbc);
        JTextField nameField = new JTextField(student != null ? student.getName() : "");
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(nameField, gbc);
        
        // Email
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        panel.add(new JLabel("Email:"), gbc);
        JTextField emailField = new JTextField(student != null ? student.getEmail() : "");
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(emailField, gbc);
        
        // Student ID
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        panel.add(new JLabel("Student ID:"), gbc);
        JTextField studentIdField = new JTextField(student != null ? student.getStudentId() : "");
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(studentIdField, gbc);
        
        // Program
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0;
        panel.add(new JLabel("Program:"), gbc);
        JTextField programField = new JTextField(student != null ? student.getProgram() : "");
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(programField, gbc);
        
        // Supervisor
        gbc.gridx = 0; gbc.gridy = 6; gbc.weightx = 0;
        panel.add(new JLabel("Supervisor:"), gbc);
        JTextField supervisorField = new JTextField(student != null ? student.getSupervisor() : "");
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(supervisorField, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        StyledButton cancelBtn = StyledButton.secondary("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());
        buttonPanel.add(cancelBtn);
        
        StyledButton saveBtn = StyledButton.primary("Save");
        saveBtn.addActionListener(e -> {
            Student s = student != null ? student : new Student();
            s.setUsername(usernameField.getText().trim());
            s.setPassword(new String(passwordField.getPassword()));
            s.setName(nameField.getText().trim());
            s.setEmail(emailField.getText().trim());
            s.setStudentId(studentIdField.getText().trim());
            s.setProgram(programField.getText().trim());
            s.setSupervisor(supervisorField.getText().trim());
            
            userRepository.saveStudent(s);
            loadStudents();
            dialog.dispose();
        });
        buttonPanel.add(saveBtn);
        
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);
        
        dialog.add(panel);
        return dialog;
    }

    
    private void showAddEvaluatorDialog() {
        JDialog dialog = createEvaluatorDialog(null, "Add Evaluator");
        dialog.setVisible(true);
    }
    
    private void editSelectedEvaluator() {
        int selectedRow = evaluatorsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an evaluator to edit.", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Long id = (Long) evaluatorsTableModel.getValueAt(selectedRow, 0);
        User user = userRepository.findById(id);
        if (user instanceof Evaluator) {
            JDialog dialog = createEvaluatorDialog((Evaluator) user, "Edit Evaluator");
            dialog.setVisible(true);
        }
    }
    
    private void deleteSelectedEvaluator() {
        int selectedRow = evaluatorsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an evaluator to delete.", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this evaluator?", 
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            Long id = (Long) evaluatorsTableModel.getValueAt(selectedRow, 0);
            userRepository.deleteEvaluator(id);
            loadEvaluators();
        }
    }
    
    private JDialog createEvaluatorDialog(Evaluator evaluator, String title) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), title, true);
        dialog.setSize(400, 380);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(UIConstants.SPACING_LG, UIConstants.SPACING_LG, 
                                                        UIConstants.SPACING_LG, UIConstants.SPACING_LG));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(UIConstants.SPACING_XS, UIConstants.SPACING_XS, 
                                UIConstants.SPACING_XS, UIConstants.SPACING_XS);
        
        // Username
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Username:"), gbc);
        JTextField usernameField = new JTextField(evaluator != null ? evaluator.getUsername() : "");
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(usernameField, gbc);
        
        // Password
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panel.add(new JLabel("Password:"), gbc);
        JPasswordField passwordField = new JPasswordField(evaluator != null ? evaluator.getPassword() : "");
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(passwordField, gbc);
        
        // Name
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        panel.add(new JLabel("Name:"), gbc);
        JTextField nameField = new JTextField(evaluator != null ? evaluator.getName() : "");
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(nameField, gbc);
        
        // Email
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        panel.add(new JLabel("Email:"), gbc);
        JTextField emailField = new JTextField(evaluator != null ? evaluator.getEmail() : "");
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(emailField, gbc);
        
        // Department
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        panel.add(new JLabel("Department:"), gbc);
        JTextField departmentField = new JTextField(evaluator != null ? evaluator.getDepartment() : "");
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(departmentField, gbc);
        
        // Expertise
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0;
        panel.add(new JLabel("Expertise:"), gbc);
        JTextField expertiseField = new JTextField(evaluator != null ? evaluator.getExpertise() : "");
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(expertiseField, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        StyledButton cancelBtn = StyledButton.secondary("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());
        buttonPanel.add(cancelBtn);
        
        StyledButton saveBtn = StyledButton.primary("Save");
        saveBtn.addActionListener(e -> {
            Evaluator ev = evaluator != null ? evaluator : new Evaluator();
            ev.setUsername(usernameField.getText().trim());
            ev.setPassword(new String(passwordField.getPassword()));
            ev.setName(nameField.getText().trim());
            ev.setEmail(emailField.getText().trim());
            ev.setDepartment(departmentField.getText().trim());
            ev.setExpertise(expertiseField.getText().trim());
            
            userRepository.saveEvaluator(ev);
            loadEvaluators();
            dialog.dispose();
        });
        buttonPanel.add(saveBtn);
        
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);
        
        dialog.add(panel);
        return dialog;
    }
    
    @Override
    public void refresh() {
        loadData();
    }
}
