package com.fci.seminar.ui.panels.student;

import com.fci.seminar.model.Registration;
import com.fci.seminar.model.User;
import com.fci.seminar.model.enums.RegistrationStatus;
import com.fci.seminar.service.AuthService;
import com.fci.seminar.service.RegistrationService;
import com.fci.seminar.ui.MainFrame;
import com.fci.seminar.ui.components.CardPanel;
import com.fci.seminar.ui.components.RoundedPanel;
import com.fci.seminar.ui.components.StyledButton;
import com.fci.seminar.ui.dialogs.ConfirmDialog;
import com.fci.seminar.ui.dialogs.MessageDialog;
import com.fci.seminar.util.FileUtils;
import com.fci.seminar.util.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.io.File;
import java.util.List;

/**
 * Panel for uploading presentation materials.
 * Provides drag-drop interface and file validation.
 * Requirements: 4.1, 4.2, 4.3, 4.4, 4.5
 */
public class UploadPanel extends JPanel implements MainFrame.Refreshable {
    
    private final AuthService authService;
    private final RegistrationService registrationService;
    
    // Current registration
    private Registration currentRegistration;
    
    // UI Components
    private JLabel registrationInfoLabel;
    private JPanel dropZonePanel;
    private JLabel dropZoneLabel;
    private JPanel uploadedFilesPanel;
    private StyledButton selectFileButton;
    
    /**
     * Creates the upload panel.
     */
    public UploadPanel() {
        this.authService = AuthService.getInstance();
        this.registrationService = RegistrationService.getInstance();
        initComponents();
        loadRegistration();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(UIConstants.SPACING_MD, UIConstants.SPACING_MD));
        setBackground(UIConstants.BG_MAIN);
        setOpaque(false);
        
        // Header
        add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Main content
        add(createMainContent(), BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Upload Presentation Materials");
        titleLabel.setFont(UIConstants.TITLE_MEDIUM);
        titleLabel.setForeground(UIConstants.TEXT_PRIMARY);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        return headerPanel;
    }
    
    private JPanel createMainContent() {
        JPanel mainPanel = new JPanel(new BorderLayout(UIConstants.SPACING_MD, UIConstants.SPACING_MD));
        mainPanel.setOpaque(false);
        
        // Registration info card
        mainPanel.add(createRegistrationInfoCard(), BorderLayout.NORTH);
        
        // Upload area
        mainPanel.add(createUploadCard(), BorderLayout.CENTER);
        
        // Uploaded files list
        mainPanel.add(createUploadedFilesCard(), BorderLayout.SOUTH);
        
        return mainPanel;
    }
    
    private CardPanel createRegistrationInfoCard() {
        CardPanel card = new CardPanel("Current Registration");
        card.setPreferredSize(new Dimension(0, 80));
        
        registrationInfoLabel = new JLabel("Loading registration information...");
        registrationInfoLabel.setFont(UIConstants.BODY);
        registrationInfoLabel.setForeground(UIConstants.TEXT_SECONDARY);
        
        card.getContentPanel().setLayout(new BorderLayout());
        card.getContentPanel().add(registrationInfoLabel, BorderLayout.CENTER);
        
        return card;
    }

    
    private CardPanel createUploadCard() {
        CardPanel card = new CardPanel("Upload File");
        card.setContentLayout(new BorderLayout(UIConstants.SPACING_MD, UIConstants.SPACING_MD));
        
        // Requirements info
        JPanel requirementsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, UIConstants.SPACING_MD, 0));
        requirementsPanel.setOpaque(false);
        
        JLabel typeLabel = new JLabel("Allowed types: PPT, PPTX, PDF, PNG, JPG");
        typeLabel.setFont(UIConstants.SMALL);
        typeLabel.setForeground(UIConstants.TEXT_SECONDARY);
        requirementsPanel.add(typeLabel);
        
        JLabel sizeLabel = new JLabel("  |  Max size: 50MB");
        sizeLabel.setFont(UIConstants.SMALL);
        sizeLabel.setForeground(UIConstants.TEXT_SECONDARY);
        requirementsPanel.add(sizeLabel);
        
        card.getContentPanel().add(requirementsPanel, BorderLayout.NORTH);
        
        // Drop zone
        dropZonePanel = createDropZone();
        card.getContentPanel().add(dropZonePanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createDropZone() {
        RoundedPanel dropZone = new RoundedPanel(UIConstants.RADIUS_MD, false);
        dropZone.setLayout(new GridBagLayout());
        dropZone.setBackground(new Color(248, 250, 252));
        dropZone.setBorder(BorderFactory.createDashedBorder(
            UIConstants.BORDER, 2, 5, 5, true
        ));
        dropZone.setPreferredSize(new Dimension(0, 200));
        
        JPanel centerContent = new JPanel();
        centerContent.setLayout(new BoxLayout(centerContent, BoxLayout.Y_AXIS));
        centerContent.setOpaque(false);
        
        // Upload icon
        JLabel iconLabel = new JLabel("\uD83D\uDCC4"); // Document emoji
        iconLabel.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 48));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerContent.add(iconLabel);
        
        centerContent.add(Box.createVerticalStrut(UIConstants.SPACING_MD));
        
        // Drop zone text
        dropZoneLabel = new JLabel("Drag and drop file here or click to select");
        dropZoneLabel.setFont(UIConstants.BODY);
        dropZoneLabel.setForeground(UIConstants.TEXT_SECONDARY);
        dropZoneLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerContent.add(dropZoneLabel);
        
        centerContent.add(Box.createVerticalStrut(UIConstants.SPACING_MD));
        
        // Select file button
        selectFileButton = StyledButton.secondary("Select File");
        selectFileButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        selectFileButton.addActionListener(e -> selectFile());
        centerContent.add(selectFileButton);
        
        dropZone.add(centerContent);
        
        // Setup drag and drop
        setupDragAndDrop(dropZone);
        
        // Click to select
        dropZone.setCursor(new Cursor(Cursor.HAND_CURSOR));
        dropZone.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                selectFile();
            }
        });
        
        return dropZone;
    }
    
    private void setupDragAndDrop(JPanel dropZone) {
        new DropTarget(dropZone, new DropTargetListener() {
            @Override
            public void dragEnter(DropTargetDragEvent dtde) {
                dropZone.setBackground(new Color(230, 240, 255));
                dropZone.repaint();
            }
            
            @Override
            public void dragOver(DropTargetDragEvent dtde) {
                // Accept the drag
                dtde.acceptDrag(DnDConstants.ACTION_COPY);
            }
            
            @Override
            public void dropActionChanged(DropTargetDragEvent dtde) {}
            
            @Override
            public void dragExit(DropTargetEvent dte) {
                dropZone.setBackground(new Color(248, 250, 252));
                dropZone.repaint();
            }
            
            @Override
            public void drop(DropTargetDropEvent dtde) {
                dropZone.setBackground(new Color(248, 250, 252));
                dropZone.repaint();
                
                try {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY);
                    Transferable transferable = dtde.getTransferable();
                    
                    if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                        @SuppressWarnings("unchecked")
                        List<File> files = (List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);
                        
                        if (!files.isEmpty()) {
                            handleFileUpload(files.get(0));
                        }
                    }
                    
                    dtde.dropComplete(true);
                } catch (Exception e) {
                    dtde.dropComplete(false);
                    showError("Failed to process dropped file: " + e.getMessage());
                }
            }
        });
    }
    
    private CardPanel createUploadedFilesCard() {
        CardPanel card = new CardPanel("Uploaded Files");
        card.setPreferredSize(new Dimension(0, 150));
        card.setContentLayout(new BorderLayout());
        
        uploadedFilesPanel = new JPanel();
        uploadedFilesPanel.setLayout(new BoxLayout(uploadedFilesPanel, BoxLayout.Y_AXIS));
        uploadedFilesPanel.setOpaque(false);
        
        JScrollPane scrollPane = new JScrollPane(uploadedFilesPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        
        card.getContentPanel().add(scrollPane, BorderLayout.CENTER);
        
        return card;
    }

    
    private void loadRegistration() {
        User currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            showNoRegistration("Please log in to upload files");
            return;
        }
        
        List<Registration> registrations = registrationService.getRegistrationsByStudent(currentUser.getId());
        
        if (registrations.isEmpty()) {
            showNoRegistration("No registration found. Please register first.");
            return;
        }
        
        // Get the most recent registration
        currentRegistration = registrations.get(registrations.size() - 1);
        
        // Check if registration is approved
        if (currentRegistration.getStatus() != RegistrationStatus.APPROVED) {
            showNoRegistration("Your registration is not yet approved. Status: " + 
                    currentRegistration.getStatus().name());
            disableUpload();
            return;
        }
        
        // Show registration info
        String info = String.format("<html><b>%s</b><br>Type: %s | Status: %s</html>",
                currentRegistration.getResearchTitle(),
                currentRegistration.getPresentationType().name(),
                currentRegistration.getStatus().name());
        registrationInfoLabel.setText(info);
        
        // Enable upload
        enableUpload();
        
        // Load uploaded files
        loadUploadedFiles();
    }
    
    private void showNoRegistration(String message) {
        registrationInfoLabel.setText(message);
        currentRegistration = null;
        disableUpload();
        uploadedFilesPanel.removeAll();
        
        JLabel emptyLabel = new JLabel("No files uploaded");
        emptyLabel.setFont(UIConstants.BODY);
        emptyLabel.setForeground(UIConstants.TEXT_SECONDARY);
        uploadedFilesPanel.add(emptyLabel);
        uploadedFilesPanel.revalidate();
        uploadedFilesPanel.repaint();
    }
    
    private void enableUpload() {
        selectFileButton.setEnabled(true);
        dropZonePanel.setEnabled(true);
        dropZoneLabel.setText("Drag and drop file here or click to select");
    }
    
    private void disableUpload() {
        selectFileButton.setEnabled(false);
        dropZoneLabel.setText("Upload disabled - registration required");
    }
    
    private void loadUploadedFiles() {
        uploadedFilesPanel.removeAll();
        
        if (currentRegistration == null || currentRegistration.getFilePath() == null || 
                currentRegistration.getFilePath().isEmpty()) {
            JLabel emptyLabel = new JLabel("No files uploaded yet");
            emptyLabel.setFont(UIConstants.BODY);
            emptyLabel.setForeground(UIConstants.TEXT_SECONDARY);
            uploadedFilesPanel.add(emptyLabel);
        } else {
            // Show uploaded file
            uploadedFilesPanel.add(createFileRow(currentRegistration.getFilePath()));
        }
        
        uploadedFilesPanel.revalidate();
        uploadedFilesPanel.repaint();
    }
    
    private JPanel createFileRow(String filePath) {
        JPanel row = new JPanel(new BorderLayout(UIConstants.SPACING_MD, 0));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(UIConstants.SPACING_SM, 0, UIConstants.SPACING_SM, 0));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        
        // File icon and name
        JPanel fileInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, UIConstants.SPACING_SM, 0));
        fileInfo.setOpaque(false);
        
        JLabel iconLabel = new JLabel("\uD83D\uDCC4"); // Document emoji
        iconLabel.setFont(UIConstants.BODY);
        fileInfo.add(iconLabel);
        
        // Extract filename from path
        String fileName = new File(filePath).getName();
        JLabel nameLabel = new JLabel(fileName);
        nameLabel.setFont(UIConstants.BODY);
        nameLabel.setForeground(UIConstants.TEXT_PRIMARY);
        fileInfo.add(nameLabel);
        
        // File size if exists
        File file = new File(filePath);
        if (file.exists()) {
            JLabel sizeLabel = new JLabel("(" + FileUtils.formatFileSize(file.length()) + ")");
            sizeLabel.setFont(UIConstants.SMALL);
            sizeLabel.setForeground(UIConstants.TEXT_SECONDARY);
            fileInfo.add(sizeLabel);
        }
        
        row.add(fileInfo, BorderLayout.CENTER);
        
        // Delete button
        StyledButton deleteButton = StyledButton.danger("Delete");
        deleteButton.setPreferredSize(new Dimension(80, 30));
        deleteButton.addActionListener(e -> deleteFile(filePath));
        row.add(deleteButton, BorderLayout.EAST);
        
        return row;
    }
    
    private void selectFile() {
        if (currentRegistration == null) {
            showError("Please register first before uploading files");
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Presentation File");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) return true;
                String ext = FileUtils.getFileExtension(f.getName()).toLowerCase();
                return FileUtils.isAllowedExtension(ext);
            }
            
            @Override
            public String getDescription() {
                return "Presentation Files (PPT, PPTX, PDF, PNG, JPG)";
            }
        });
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            handleFileUpload(fileChooser.getSelectedFile());
        }
    }
    
    private void handleFileUpload(File file) {
        if (currentRegistration == null) {
            showError("Please register first before uploading files");
            return;
        }
        
        // Validate file
        FileUtils.ValidationResult validation = FileUtils.validateFile(file);
        if (!validation.isValid()) {
            showError(validation.getMessage());
            return;
        }
        
        try {
            // Store file
            String targetFileName = "presentation_" + currentRegistration.getId() + 
                    FileUtils.getFileExtension(file.getName());
            String storedPath = FileUtils.storeFile(file, targetFileName);
            
            // Update registration with file path
            registrationService.updateFilePath(currentRegistration.getId(), storedPath);
            currentRegistration.setFilePath(storedPath);
            
            showSuccess("File uploaded successfully!");
            loadUploadedFiles();
            
        } catch (Exception e) {
            showError("Failed to upload file: " + e.getMessage());
        }
    }
    
    private void deleteFile(String filePath) {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        boolean confirmed = ConfirmDialog.showDialog(parent, "Delete File", 
                "Are you sure you want to delete this file?");
        
        if (confirmed) {
            // Delete physical file
            FileUtils.deleteFile(filePath);
            
            // Update registration
            if (currentRegistration != null) {
                registrationService.updateFilePath(currentRegistration.getId(), null);
                currentRegistration.setFilePath(null);
            }
            
            showSuccess("File deleted successfully");
            loadUploadedFiles();
        }
    }
    
    private void showSuccess(String message) {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        MessageDialog.showSuccess(parent, "Success", message);
    }
    
    private void showError(String message) {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        MessageDialog.showError(parent, "Error", message);
    }
    
    @Override
    public void refresh() {
        loadRegistration();
    }
}
