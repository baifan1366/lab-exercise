# Implementation Plan

- [x] 1. Set up project structure and core utilities





  - [x] 1.1 Create Maven/Gradle project structure with required directories


    - Create src/main/java/com/fci/seminar directory structure
    - Create model, service, repository, ui, util packages
    - Create src/main/resources/data and icons directories
    - Create src/test/java and src/test/resources directories
    - _Requirements: 11.1_

  - [x] 1.2 Implement UIConstants class with all design tokens

    - Define color constants (PRIMARY, PRIMARY_LIGHT, ACCENT, etc.)
    - Define font constants (TITLE_LARGE, BODY, etc.)
    - Define spacing and dimension constants
    - _Requirements: 10.1, 10.3, 10.4_

  - [x] 1.3 Implement UIUtils helper class

    - Create utility methods for common UI operations
    - Implement color manipulation helpers
    - _Requirements: 10.1_

- [x] 2. Implement Model layer (Enums and Entities)






  - [x] 2.1 Create enum classes

    - Implement Role enum (GUEST, STUDENT, EVALUATOR, COORDINATOR)
    - Implement SessionStatus enum (OPEN, FULL, CLOSED, REQUIRES_APPROVAL)
    - Implement SessionType enum (ORAL, POSTER)
    - Implement RegistrationStatus enum (PENDING, APPROVED, REJECTED, CANCELLED)
    - Implement AwardType enum (BEST_ORAL, BEST_POSTER, PEOPLES_CHOICE)
    - _Requirements: 1.1, 2.2, 3.2, 3.6_

  - [x] 2.2 Implement User class hierarchy

    - Create abstract User base class with common fields
    - Implement Student class extending User
    - Implement Evaluator class extending User
    - Implement Coordinator class extending User
    - _Requirements: 1.2, 1.3_

  - [x] 2.3 Implement Session entity

    - Create Session class with all fields
    - Implement getters, setters, and validation
    - _Requirements: 2.1, 6.2_

  - [x] 2.4 Implement Registration entity

    - Create Registration class with all fields
    - Implement validation for title (max 200) and abstract (max 1000)
    - _Requirements: 3.1, 3.4_
  - [x] 2.5 Implement Evaluation entity


    - Create Evaluation class with scoring fields
    - Implement getTotalScore() method
    - _Requirements: 5.2, 5.3_
  - [ ]* 2.6 Write property test for Evaluation score invariant
    - **Property 11: Evaluation score invariant**
    - **Validates: Requirements 5.3**

  - [x] 2.7 Implement Award entity

    - Create Award class with type, registration reference, and score
    - _Requirements: 8.1, 8.3_


- [x] 3. Implement Data Persistence layer





  - [x] 3.1 Implement DataManager class

    - Create singleton DataManager for JSON file operations
    - Implement JSON serialization using Gson library
    - Implement JSON deserialization with error handling
    - _Requirements: 11.1, 11.2, 11.4_
  - [ ]* 3.2 Write property test for JSON round-trip
    - **Property 23: JSON serialization round-trip**
    - **Validates: Requirements 11.1, 11.2**

  - [x] 3.3 Implement UserRepository

    - Implement findById, findAll, save, delete operations
    - Implement findByUsername and findByRole methods
    - _Requirements: 1.3, 1.4_

  - [x] 3.4 Implement SessionRepository

    - Implement CRUD operations for sessions
    - Implement findByDate, findByType, findByStatus methods
    - _Requirements: 2.1, 2.3, 6.1_

  - [x] 3.5 Implement RegistrationRepository

    - Implement CRUD operations for registrations
    - Implement findByStudentId, findBySessionId, findByStatus methods
    - _Requirements: 3.4, 3.6_

  - [x] 3.6 Implement EvaluationRepository

    - Implement CRUD operations for evaluations
    - Implement findByEvaluatorId, findByRegistrationId methods
    - _Requirements: 5.1, 5.4_

- [x] 4. Checkpoint - Ensure all tests pass





  - Ensure all tests pass, ask the user if questions arise.

- [x] 5. Implement Service layer





  - [x] 5.1 Implement AuthService


    - Create singleton AuthService
    - Implement login method with credential validation
    - Implement logout method to reset to Guest mode
    - Implement getCurrentUser and getCurrentRole methods
    - _Requirements: 1.3, 1.4, 1.5_
  - [ ]* 5.2 Write property tests for authentication
    - **Property 1: Valid credentials authentication**
    - **Property 2: Invalid credentials rejection**
    - **Property 3: Logout role reset**
    - **Validates: Requirements 1.3, 1.4, 1.5**

  - [x] 5.3 Implement SessionService

    - Implement getAllSessions and getAvailableSessions methods
    - Implement createSession with validation
    - Implement updateSession and deleteSession methods
    - Implement updateSessionStatus method
    - _Requirements: 2.1, 6.2, 6.3, 6.4, 6.5, 6.6_
  - [ ]* 5.4 Write property tests for session operations
    - **Property 4: Complete session display**
    - **Property 5: Session filter correctness**
    - **Property 14: Session creation validation**
    - **Property 15: Empty session deletion**
    - **Property 16: Non-empty session deletion protection**
    - **Validates: Requirements 2.1, 2.2, 2.3, 3.3, 6.2, 6.4, 6.5**

  - [x] 5.5 Implement RegistrationService

    - Implement register method with validation
    - Implement getRegistrationsByStudent and getRegistrationsBySession
    - Implement approveRegistration and rejectRegistration methods
    - Implement assignToSession method
    - _Requirements: 3.4, 3.5, 3.6, 7.2_
  - [ ]* 5.6 Write property tests for registration
    - **Property 6: Valid registration creates pending record**
    - **Property 7: Invalid registration rejection**
    - **Property 17: Assignment count increment**
    - **Property 18: Capacity status invariant**
    - **Validates: Requirements 3.4, 3.5, 7.2, 7.4**


  - [x] 5.7 Implement EvaluationService
    - Implement getEvaluationsByEvaluator method
    - Implement saveEvaluation and submitEvaluation methods
    - Implement assignEvaluator method
    - Implement getAverageScore method
    - _Requirements: 5.1, 5.4, 5.5, 5.6_
  - [ ]* 5.8 Write property tests for evaluation
    - **Property 10: Evaluator assignment visibility**
    - **Property 12: Draft save state preservation**
    - **Property 13: Evaluation submission state**

    - **Validates: Requirements 5.1, 5.5, 5.6**
  - [x] 5.9 Implement AwardService

    - Implement calculateAwards method
    - Implement getBestOral, getBestPoster, getPeoplesChoice methods
    - Handle tie-breaking logic
    - _Requirements: 8.2, 8.3, 8.4_
  - [ ]* 5.10 Write property tests for awards
    - **Property 19: Award winner selection**
    - **Property 20: Tie handling**
    - **Validates: Requirements 8.2, 8.4**

  - [x] 5.11 Implement ReportService

    - Implement getRegistrationStatistics method
    - Implement getEvaluationSummary method
    - Implement getSessionAttendance method
    - Implement exportToCsv method
    - _Requirements: 9.1, 9.2, 9.3_

  - [x] 5.12 Implement FileUtils for file validation

    - Implement validateFileType method
    - Implement validateFileSize method
    - Implement file storage operations
    - _Requirements: 4.2, 4.3, 4.4_
  - [ ]* 5.13 Write property tests for file validation
    - **Property 8: File validation correctness**
    - **Property 9: Valid file association**
    - **Validates: Requirements 4.2, 4.3, 4.4**


- [x] 6. Checkpoint - Ensure all tests pass




  - Ensure all tests pass, ask the user if questions arise.



- [x] 7. Implement UI Components

  - [x] 7.1 Implement RoundedPanel component


    - Create custom JPanel with rounded corners
    - Implement shadow effect option
    - _Requirements: 10.1_


  - [x] 7.2 Implement CardPanel component
    - Extend RoundedPanel with title support
    - Implement content panel accessor

    - _Requirements: 10.1_
  - [x] 7.3 Implement StyledButton component

    - Create button with PRIMARY, SECONDARY, DANGER, SUCCESS types
    - Implement hover and press state effects
    - _Requirements: 10.3_
  - [x] 7.4 Implement StyledTextField component


    - Create text field with placeholder support
    - Implement focus state styling
    - _Requirements: 10.3_
  - [x] 7.5 Implement StyledTable component


    - Create table with zebra striping
    - Implement hover highlighting
    - Configure header styling
    - _Requirements: 10.4_


  - [x] 7.6 Implement StatusLabel component

    - Create label with colored background
    - Implement factory methods for status types
    - _Requirements: 10.5_
  - [ ]* 7.7 Write property test for status color mapping
    - **Property 22: Status color mapping**
    - **Validates: Requirements 10.5**

  - [x] 7.8 Implement RoundedBorder utility

    - Create custom border with rounded corners
    - Support configurable color and radius
    - _Requirements: 10.1_


- [x] 8. Implement Main Frame and Navigation




  - [x] 8.1 Implement HeaderPanel


    - Create header with logo and title
    - Add user info display area
    - Add login/logout button
    - _Requirements: 10.1_
  - [x] 8.2 Implement SidebarPanel


    - Create sidebar with menu items
    - Implement role-based menu filtering
    - Add menu item click handling
    - _Requirements: 10.2_
  - [ ]* 8.3 Write property test for role-based menu
    - **Property 21: Role-based menu visibility**
    - **Validates: Requirements 10.2**
  - [x] 8.4 Implement StatusBar


    - Create status bar with role display
    - Add copyright text
    - _Requirements: 10.1_
  - [x] 8.5 Implement MainFrame


    - Create main window with BorderLayout
    - Integrate header, sidebar, content area, status bar
    - Implement panel switching logic
    - Implement role change handling
    - _Requirements: 10.1, 1.1_


- [x] 9. Implement Dialogs




  - [x] 9.1 Implement LoginDialog


    - Create modal dialog with role selection
    - Add username and password fields
    - Implement login button with validation
    - Handle authentication result
    - _Requirements: 1.2, 1.3, 1.4_


  - [x] 9.2 Implement ConfirmDialog
    - Create reusable confirmation dialog
    - Support custom title and message
    - Return user choice

    - _Requirements: 6.5_
  - [x] 9.3 Implement MessageDialog

    - Create dialog for info, warning, error messages
    - Support different icon types
    - _Requirements: 1.4, 4.4_


- [x] 10. Implement Guest Panels






  - [x] 10.1 Implement ScheduleViewPanel


    - Create calendar/list view for sessions
    - Display session cards with status
    - Implement date and type filters
    - Show session details on selection
    - _Requirements: 2.1, 2.2, 2.3, 2.4_


- [x] 11. Implement Student Panels



  - [x] 11.1 Implement RegistrationPanel


    - Create registration form with all fields
    - Implement presentation type selection
    - Show available sessions based on type
    - Implement form validation
    - Handle registration submission
    - _Requirements: 3.1, 3.2, 3.3, 3.4, 3.5_


  - [x] 11.2 Implement UploadPanel





    - Create file upload interface with drag-drop area
    - Display file type and size requirements
    - Implement file validation
    - Show uploaded files list with delete option


    - _Requirements: 4.1, 4.2, 4.3, 4.4, 4.5_

  - [x] 11.3 Implement StatusPanel




    - Display registration status
    - Show assigned session details
    - Display uploaded file information
    - _Requirements: 3.6_



- [x] 12. Implement Evaluator Panels



  - [x] 12.1 Implement AssignedListPanel


    - Display list of assigned presentations
    - Show evaluation status for each
    - Enable navigation to evaluation form
    - _Requirements: 5.1_
  - [x] 12.2 Implement EvaluationPanel


    - Create scoring interface with sliders (0-25 each)
    - Display real-time total score calculation
    - Add comments text area
    - Implement save draft and submit buttons
    - _Requirements: 5.2, 5.3, 5.4, 5.5, 5.6_

- [x] 13. Implement Coordinator Panels



  - [x] 13.1 Implement DashboardPanel


    - Display summary statistics cards
    - Show recent activities
    - Quick access to common actions
    - _Requirements: 6.1_

  - [x] 13.2 Implement UserManagementPanel

    - Display user tables (students, evaluators)
    - Implement add/edit/delete user functionality
    - Support user search and filtering
    - _Requirements: 1.2_


  - [x] 13.3 Implement SessionManagementPanel
    - Display session list with CRUD operations
    - Create session form dialog
    - Implement status change functionality
    - Handle deletion with registration check

    - _Requirements: 6.1, 6.2, 6.3, 6.4, 6.5, 6.6_

  - [x] 13.4 Implement AssignmentPanel
    - Create student-session assignment interface
    - Create evaluator-presentation assignment matrix
    - Display unassigned items
    - Handle drag-drop or selection-based assignment

    - _Requirements: 7.1, 7.2, 7.3, 7.4, 7.5_
  - [x] 13.5 Implement AwardPanel

    - Display award categories
    - Implement calculate awards button
    - Show winners with details
    - Handle tie display
    - _Requirements: 8.1, 8.2, 8.3, 8.4_

  - [x] 13.6 Implement ReportPanel


    - Display report type selection
    - Show generated report data
    - Implement CSV export functionality
    - Add print preview option
    - _Requirements: 9.1, 9.2, 9.3, 9.4_

- [x] 14. Implement Main Application Entry





  - [x] 14.1 Create Main class


    - Set up look and feel
    - Initialize DataManager and load data
    - Create and display MainFrame
    - Handle application shutdown
    - _Requirements: 1.1, 11.2_

  - [x] 14.2 Create sample data files

    - Create users.json with sample users for each role
    - Create sessions.json with sample sessions
    - Create registrations.json with sample registrations
    - Create evaluations.json with sample evaluations
    - _Requirements: 11.1, 11.2_






- [ ] 15. Final Checkpoint - Ensure all tests pass
  - Ensure all tests pass, ask the user if questions arise.
