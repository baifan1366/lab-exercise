package com.fci.seminar.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Utility class for file validation and storage operations.
 * Requirements: 4.2, 4.3, 4.4
 */
public class FileUtils {
    
    // Allowed file extensions (lowercase)
    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(Arrays.asList(
            ".ppt", ".pptx", ".pdf", ".png", ".jpg"
    ));
    
    // Maximum file size: 50MB in bytes
    public static final long MAX_FILE_SIZE = 50L * 1024 * 1024;
    
    // Default upload directory
    private static final String DEFAULT_UPLOAD_DIR = "uploads";
    
    private FileUtils() {
        // Utility class - prevent instantiation
    }
    
    // ==================== File Validation ====================
    
    /**
     * Validates a file for upload.
     * Requirements: 4.2, 4.4
     * @param file the file to validate
     * @return ValidationResult containing success status and error message
     */
    public static ValidationResult validateFile(File file) {
        if (file == null) {
            return new ValidationResult(false, "File cannot be null");
        }
        
        if (!file.exists()) {
            return new ValidationResult(false, "File does not exist");
        }
        
        // Validate file type
        ValidationResult typeResult = validateFileType(file.getName());
        if (!typeResult.isValid()) {
            return typeResult;
        }
        
        // Validate file size
        ValidationResult sizeResult = validateFileSize(file.length());
        if (!sizeResult.isValid()) {
            return sizeResult;
        }
        
        return new ValidationResult(true, "File is valid");
    }

    
    /**
     * Validates file type by extension.
     * Requirements: 4.2
     * @param fileName the file name to validate
     * @return ValidationResult containing success status and error message
     */
    public static ValidationResult validateFileType(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return new ValidationResult(false, "File name cannot be empty");
        }
        
        String extension = getFileExtension(fileName);
        if (extension.isEmpty()) {
            return new ValidationResult(false, "File must have an extension");
        }
        
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            return new ValidationResult(false, 
                    "Invalid file type. Allowed types: PPT, PPTX, PDF, PNG, JPG");
        }
        
        return new ValidationResult(true, "File type is valid");
    }
    
    /**
     * Validates file size.
     * Requirements: 4.4
     * @param fileSize the file size in bytes
     * @return ValidationResult containing success status and error message
     */
    public static ValidationResult validateFileSize(long fileSize) {
        if (fileSize <= 0) {
            return new ValidationResult(false, "File is empty");
        }
        
        if (fileSize > MAX_FILE_SIZE) {
            return new ValidationResult(false, 
                    String.format("File size exceeds maximum limit of %d MB", 
                            MAX_FILE_SIZE / (1024 * 1024)));
        }
        
        return new ValidationResult(true, "File size is valid");
    }
    
    /**
     * Gets the file extension from a file name.
     * @param fileName the file name
     * @return the extension including the dot, or empty string if none
     */
    public static String getFileExtension(String fileName) {
        if (fileName == null) return "";
        
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot == -1 || lastDot == fileName.length() - 1) {
            return "";
        }
        
        return fileName.substring(lastDot).toLowerCase();
    }
    
    /**
     * Checks if a file extension is allowed.
     * @param extension the extension to check (with or without dot)
     * @return true if allowed
     */
    public static boolean isAllowedExtension(String extension) {
        if (extension == null) return false;
        
        String ext = extension.toLowerCase();
        if (!ext.startsWith(".")) {
            ext = "." + ext;
        }
        
        return ALLOWED_EXTENSIONS.contains(ext);
    }
    
    /**
     * Gets the set of allowed extensions.
     * @return set of allowed extensions
     */
    public static Set<String> getAllowedExtensions() {
        return new HashSet<>(ALLOWED_EXTENSIONS);
    }

    
    // ==================== File Storage Operations ====================
    
    /**
     * Stores a file in the upload directory.
     * Requirements: 4.3
     * @param sourceFile the source file to store
     * @param targetFileName the target file name
     * @return the path to the stored file
     * @throws IOException if storage fails
     */
    public static String storeFile(File sourceFile, String targetFileName) throws IOException {
        return storeFile(sourceFile, targetFileName, DEFAULT_UPLOAD_DIR);
    }
    
    /**
     * Stores a file in a specified directory.
     * Requirements: 4.3
     * @param sourceFile the source file to store
     * @param targetFileName the target file name
     * @param uploadDir the upload directory
     * @return the path to the stored file
     * @throws IOException if storage fails
     */
    public static String storeFile(File sourceFile, String targetFileName, String uploadDir) 
            throws IOException {
        // Validate file first
        ValidationResult validation = validateFile(sourceFile);
        if (!validation.isValid()) {
            throw new IOException("File validation failed: " + validation.getMessage());
        }
        
        // Ensure upload directory exists
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // Generate unique file name if needed
        String finalFileName = generateUniqueFileName(uploadDir, targetFileName);
        Path targetPath = uploadPath.resolve(finalFileName);
        
        // Copy file
        Files.copy(sourceFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        
        return targetPath.toString();
    }
    
    /**
     * Generates a unique file name to avoid conflicts.
     * @param uploadDir the upload directory
     * @param fileName the original file name
     * @return a unique file name
     */
    private static String generateUniqueFileName(String uploadDir, String fileName) {
        Path targetPath = Paths.get(uploadDir, fileName);
        
        if (!Files.exists(targetPath)) {
            return fileName;
        }
        
        // Add timestamp to make unique
        String extension = getFileExtension(fileName);
        String baseName = fileName.substring(0, fileName.length() - extension.length());
        String uniqueName = baseName + "_" + System.currentTimeMillis() + extension;
        
        return uniqueName;
    }
    
    /**
     * Deletes a file.
     * @param filePath the path to the file
     * @return true if deleted successfully
     */
    public static boolean deleteFile(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            return false;
        }
        
        try {
            Path path = Paths.get(filePath);
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            return false;
        }
    }
    
    /**
     * Checks if a file exists.
     * @param filePath the path to the file
     * @return true if file exists
     */
    public static boolean fileExists(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            return false;
        }
        return Files.exists(Paths.get(filePath));
    }
    
    /**
     * Gets the file size in a human-readable format.
     * @param bytes the size in bytes
     * @return formatted size string
     */
    public static String formatFileSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2f KB", bytes / 1024.0);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", bytes / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", bytes / (1024.0 * 1024 * 1024));
        }
    }
    
    // ==================== Validation Result Class ====================
    
    /**
     * Result of a file validation operation.
     */
    public static class ValidationResult {
        private final boolean valid;
        private final String message;
        
        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public String getMessage() {
            return message;
        }
        
        @Override
        public String toString() {
            return "ValidationResult{valid=" + valid + ", message='" + message + "'}";
        }
    }
}
