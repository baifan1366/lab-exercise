# Requirements Document

## Introduction

FCI学院研究生学术研讨会管理系统是一个基于 Java Swing 的桌面应用程序，用于管理年度研究生学术研讨会。系统支持研讨会日程安排、演示者注册、评估、奖项计算和报告生成，同时通过默认的 Guest 访问模式确保透明度。系统采用基于角色的访问控制 (RBAC) 模型，包含四个角色：Guest、Student、Evaluator 和 Coordinator。

## Glossary

- **Seminar Management System (SMS)**: 研讨会管理系统，本项目的核心应用
- **Guest**: 访客角色，无需登录，只读访问
- **Student**: 学生角色，可注册演示和上传材料
- **Evaluator**: 评估员角色，可评估分配的演示
- **Coordinator**: 协调员角色，拥有完全控制权限
- **Session**: 研讨会场次，包含日期、时间、地点和容量信息
- **Registration**: 学生注册记录，包含研究标题、摘要等信息
- **Evaluation**: 评估记录，包含评分和评语
- **Rubric**: 评分标准，包含问题清晰度、研究方法、研究结果、演示质量四个维度
- **Award**: 奖项，包括最佳口头报告、最佳海报展示、最受欢迎奖

## Requirements

### Requirement 1: User Authentication and Role Management

**User Story:** As a system user, I want to authenticate with my credentials and access features based on my role, so that I can perform my designated tasks securely.

#### Acceptance Criteria

1. WHEN the system starts THEN the Seminar Management System SHALL display the main interface in Guest mode by default
2. WHEN a user clicks the login button THEN the Seminar Management System SHALL display a login dialog with role selection (Student/Evaluator/Coordinator), username, and password fields
3. WHEN a user submits valid credentials THEN the Seminar Management System SHALL authenticate the user and update the interface to show role-specific menus and features
4. WHEN a user submits invalid credentials THEN the Seminar Management System SHALL display an error message and maintain the current session state
5. WHEN an authenticated user clicks logout THEN the Seminar Management System SHALL clear the session and return to Guest mode

### Requirement 2: Seminar Schedule Viewing (Guest/All Roles)

**User Story:** As a guest or authenticated user, I want to view the seminar schedule and session availability, so that I can understand when and where seminars are happening.

#### Acceptance Criteria

1. WHEN a user navigates to the schedule view THEN the Seminar Management System SHALL display all seminar sessions with date, time, venue, and type information
2. WHEN displaying session information THEN the Seminar Management System SHALL show the availability status (Open/Full/Closed/Requires Approval) for each session
3. WHEN a user applies date or type filters THEN the Seminar Management System SHALL display only sessions matching the filter criteria
4. WHEN a user selects a session THEN the Seminar Management System SHALL display detailed session information including capacity and current registration count

### Requirement 3: Student Registration

**User Story:** As a student, I want to register for the seminar by providing my research details, so that I can present my research at the seminar.

#### Acceptance Criteria

1. WHEN a student accesses the registration form THEN the Seminar Management System SHALL display fields for research title (max 200 characters), abstract (max 1000 characters), supervisor name, and presentation type selection
2. WHEN a student selects presentation type THEN the Seminar Management System SHALL offer Oral or Poster options
3. WHEN a student views available sessions THEN the Seminar Management System SHALL display only sessions with Open status and matching presentation type
4. WHEN a student submits a registration with all required fields completed THEN the Seminar Management System SHALL create a registration record with Pending status
5. WHEN a student submits a registration with missing required fields THEN the Seminar Management System SHALL display validation errors and prevent submission
6. WHEN a student views their registration status THEN the Seminar Management System SHALL display the current status (Pending/Approved/Rejected/Cancelled) and assigned session details

### Requirement 4: Presentation Material Upload

**User Story:** As a student, I want to upload my presentation materials, so that evaluators can review my work.

#### Acceptance Criteria

1. WHEN a student with an approved registration accesses the upload panel THEN the Seminar Management System SHALL display a file upload interface
2. WHEN a student selects a file for upload THEN the Seminar Management System SHALL validate the file type (PPT, PPTX, PDF, PNG, JPG only) and size (max 50MB)
3. WHEN a student uploads a valid file THEN the Seminar Management System SHALL store the file and associate it with the registration record
4. WHEN a student uploads an invalid file type or oversized file THEN the Seminar Management System SHALL display an error message and reject the upload
5. WHEN a student views uploaded files THEN the Seminar Management System SHALL display the file name with an option to delete

### Requirement 5: Evaluation by Evaluators

**User Story:** As an evaluator, I want to evaluate assigned presentations using a standardized rubric, so that I can provide fair and consistent assessments.

#### Acceptance Criteria

1. WHEN an evaluator logs in THEN the Seminar Management System SHALL display only presentations assigned to that evaluator
2. WHEN an evaluator opens an evaluation form THEN the Seminar Management System SHALL display four scoring criteria: Problem Clarity (0-25), Methodology (0-25), Results (0-25), Presentation Quality (0-25)
3. WHEN an evaluator adjusts a score slider THEN the Seminar Management System SHALL update the displayed score and recalculate the total score (0-100)
4. WHEN an evaluator enters comments THEN the Seminar Management System SHALL store the qualitative feedback with the evaluation
5. WHEN an evaluator saves a draft THEN the Seminar Management System SHALL persist the evaluation without marking it as submitted
6. WHEN an evaluator submits an evaluation THEN the Seminar Management System SHALL mark the evaluation as final and record the submission timestamp

### Requirement 6: Session Management by Coordinator

**User Story:** As a coordinator, I want to create and manage seminar sessions, so that I can organize the seminar schedule effectively.

#### Acceptance Criteria

1. WHEN a coordinator accesses session management THEN the Seminar Management System SHALL display a list of all sessions with options to create, edit, and delete
2. WHEN a coordinator creates a new session THEN the Seminar Management System SHALL require date, start time, end time, venue, type (Oral/Poster), and capacity fields
3. WHEN a coordinator edits a session THEN the Seminar Management System SHALL allow modification of all session fields
4. WHEN a coordinator deletes a session with no registrations THEN the Seminar Management System SHALL remove the session from the system
5. WHEN a coordinator attempts to delete a session with existing registrations THEN the Seminar Management System SHALL display a warning and require confirmation
6. WHEN a coordinator changes session status THEN the Seminar Management System SHALL update the status to Open, Full, Closed, or Requires Approval

### Requirement 7: Assignment Management by Coordinator

**User Story:** As a coordinator, I want to assign students to sessions and evaluators to presentations, so that the seminar runs smoothly.

#### Acceptance Criteria

1. WHEN a coordinator accesses assignment management THEN the Seminar Management System SHALL display unassigned students and available sessions
2. WHEN a coordinator assigns a student to a session THEN the Seminar Management System SHALL update the registration record and increment the session's registered count
3. WHEN a coordinator assigns an evaluator to a presentation THEN the Seminar Management System SHALL create an evaluation assignment record
4. WHEN a session reaches capacity after an assignment THEN the Seminar Management System SHALL automatically update the session status to Full
5. WHEN a coordinator views the assignment matrix THEN the Seminar Management System SHALL display evaluator-presentation assignments in a grid format

### Requirement 8: Award Management by Coordinator

**User Story:** As a coordinator, I want to compute and manage awards, so that outstanding presenters can be recognized.

#### Acceptance Criteria

1. WHEN a coordinator accesses award management THEN the Seminar Management System SHALL display award categories: Best Oral Presentation, Best Poster Presentation, People's Choice Award
2. WHEN a coordinator triggers award calculation THEN the Seminar Management System SHALL compute winners based on average evaluation scores for Oral and Poster categories
3. WHEN displaying award results THEN the Seminar Management System SHALL show winner name, research title, and score for each category
4. WHEN multiple presentations have the same highest score THEN the Seminar Management System SHALL list all tied presentations as co-winners

### Requirement 9: Reports and Analytics by Coordinator

**User Story:** As a coordinator, I want to generate reports and export data, so that I can analyze seminar outcomes and share results.

#### Acceptance Criteria

1. WHEN a coordinator accesses the report center THEN the Seminar Management System SHALL display available report types: Registration Statistics, Evaluation Summary, Session Attendance, Award Results
2. WHEN a coordinator generates a report THEN the Seminar Management System SHALL display the report data in a formatted view
3. WHEN a coordinator exports data THEN the Seminar Management System SHALL generate a CSV file with the selected data
4. WHEN a coordinator requests a print preview THEN the Seminar Management System SHALL display a printable version of the report

### Requirement 10: User Interface Design

**User Story:** As a system user, I want a professional and intuitive interface, so that I can navigate and use the system efficiently.

#### Acceptance Criteria

1. WHEN the system displays the main window THEN the Seminar Management System SHALL show a header (60px), sidebar (220px), main content area, and status bar (30px)
2. WHEN displaying the sidebar THEN the Seminar Management System SHALL show role-appropriate menu items with icons
3. WHEN a user interacts with buttons THEN the Seminar Management System SHALL provide visual feedback (hover, press states) using the defined color scheme
4. WHEN displaying data tables THEN the Seminar Management System SHALL use zebra striping, hover highlighting, and consistent row heights (44px)
5. WHEN displaying status information THEN the Seminar Management System SHALL use color-coded labels (Success-green, Warning-yellow, Danger-red, Info-blue)

### Requirement 11: Data Persistence

**User Story:** As a system administrator, I want data to be persisted reliably, so that information is not lost between sessions.

#### Acceptance Criteria

1. WHEN the system saves data THEN the Seminar Management System SHALL serialize objects to JSON files in the data directory
2. WHEN the system starts THEN the Seminar Management System SHALL load existing data from JSON files
3. WHEN data is modified THEN the Seminar Management System SHALL persist changes immediately to prevent data loss
4. WHEN loading data fails THEN the Seminar Management System SHALL display an error message and initialize with empty data sets
