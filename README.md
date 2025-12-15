# Seminar Management System

FCI Postgraduate Academic Research Seminar Management System - A Java Swing desktop application.

## Overview

This system manages the complete workflow of academic seminars, including session scheduling, student registration, evaluation scoring, and award calculation.

### Role System

| Role | Login Required | Permissions |
|------|----------------|-------------|
| Guest | No | View seminar schedule and session availability |
| Student | Yes | Register for seminars, upload presentation materials, view status |
| Evaluator | Yes | View assigned presentations, submit evaluations |
| Coordinator | Yes | Full management (users, sessions, assignments, awards, reports) |

## Tech Stack

### Core Technologies
- **Java 17** - Programming language
- **Java Swing** - GUI framework
- **Maven** - Build and dependency management

### Dependencies

| Library | Version | Purpose |
|---------|---------|---------|
| [Gson](https://github.com/google/gson) | 2.10.1 | JSON serialization/deserialization for data persistence |

### Maven Plugins

| Plugin | Version | Purpose |
|--------|---------|---------|
| maven-compiler-plugin | 3.11.0 | Java compilation configuration |
| maven-jar-plugin | 3.3.0 | JAR packaging with main class entry point |

## Project Structure

```
src/
├── main/
│   ├── java/com/fci/seminar/
│   │   ├── Main.java                 # Application entry point
│   │   ├── model/                    # Data models
│   │   │   ├── User.java, Student.java, Evaluator.java, Coordinator.java
│   │   │   ├── Session.java, Registration.java, Evaluation.java, Award.java
│   │   │   └── enums/                # Enum types
│   │   ├── repository/               # Data access layer
│   │   │   ├── UserRepository.java
│   │   │   ├── SessionRepository.java
│   │   │   ├── RegistrationRepository.java
│   │   │   └── EvaluationRepository.java
│   │   ├── service/                  # Business logic layer
│   │   │   ├── AuthService.java      # Authentication service
│   │   │   ├── SessionService.java   # Session management
│   │   │   ├── RegistrationService.java  # Registration management
│   │   │   ├── EvaluationService.java    # Evaluation management
│   │   │   ├── AwardService.java     # Award calculation
│   │   │   └── ReportService.java    # Report generation
│   │   ├── ui/                       # View layer
│   │   │   ├── MainFrame.java        # Main window
│   │   │   ├── components/           # Common UI components
│   │   │   ├── dialogs/              # Dialog windows
│   │   │   └── panels/               # Feature panels
│   │   │       ├── guest/            # Guest panels
│   │   │       ├── student/          # Student panels
│   │   │       ├── evaluator/        # Evaluator panels
│   │   │       └── coordinator/      # Coordinator panels
│   │   └── util/                     # Utility classes
│   │       ├── DataManager.java      # JSON data management
│   │       ├── FileUtils.java        # File operations
│   │       ├── UIConstants.java      # UI constants
│   │       └── UIUtils.java          # UI utilities
│   └── resources/
│       └── data/                     # JSON data files
│           ├── users.json
│           ├── sessions.json
│           ├── registrations.json
│           ├── evaluations.json
│           └── awards.json
```

## Quick Start

### Requirements
- JDK 17 or higher
- Maven 3.6+

### Build and Run

```bash
# Clone the project
git clone <repository-url>
cd seminar-management-system

# Compile the project
mvn clean compile

# Run the application
mvn exec:java -Dexec.mainClass="com.fci.seminar.Main"

# Or package and run
mvn clean package
java -jar target/seminar-management-system-1.0-SNAPSHOT.jar
```

## Demo Accounts

| Role | Username | Password |
|------|----------|----------|
| Coordinator | admin | admin123 |
| Evaluator | evaluator1 | password123 |
| Student | student1 | password123 |

## Core Features

### 1. User Authentication
- Multi-role login (Student/Evaluator/Coordinator)
- Role-based access control (RBAC)
- Session management

### 2. Seminar Schedule
- Calendar/list view display
- Filter by date/type
- Session status display (OPEN/FULL/CLOSED)

### 3. Student Registration
- Submit research information (title, abstract, supervisor)
- Select presentation type (Oral/Poster)
- Registration status tracking

### 4. Material Upload
- Supported formats: PPT, PPTX, PDF, PNG, JPG
- File size limit: 50MB
- File validation

### 5. Evaluation System
- Four-dimensional scoring criteria (Problem Clarity, Methodology, Results, Presentation Quality)
- Each criterion: 0-25 points, total: 100 points
- Draft saving and final submission

### 6. Award Management
- Best Oral Presentation
- Best Poster Presentation
- People's Choice Award
- Automatic calculation with tie handling

### 7. Report Center
- Registration statistics report
- Evaluation summary report
- Session attendance report
- CSV data export

## UI Design

### Color Scheme
- Primary: Academic Blue (#19376D)
- Accent: #0AC2EF
- Background: #F8F9FC

### Custom Components
- `StyledButton` - Button with hover effects
- `StyledTextField` - Input field with placeholder
- `StyledTable` - Zebra-striped table
- `CardPanel` - Rounded shadow card
- `StatusLabel` - Status indicator label

## Data Storage

The system uses JSON files for data persistence via Gson library:

- Auto-load data on startup
- Auto-save data on shutdown
- Supports Java 8 date/time types (LocalDate, LocalTime, LocalDateTime)

## Architecture

- **MVC Pattern** - Model/View/Controller separation
- **Singleton Pattern** - Service layer uses singletons
- **Repository Pattern** - Data access abstraction

## License

MIT License

## Contributors

FCI Seminar Team
