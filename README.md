Seminar Management System – Complete Role-Based Design
1. System Overview

The Faculty of Computing and Informatics (FCI) requires a standalone Java Swing–based Seminar Management System to manage its annual Postgraduate Academic Research Seminar.
The system supports seminar scheduling, presenter registration, evaluation, award computation, and reporting, while ensuring transparency through a default Guest access mode.

The system follows a role-based access control (RBAC) model with four roles:

Guest (default, no login)

Student (Presenter)

Evaluator (Panel Member)

Coordinator (Faculty Staff)

2. Role Overview (Summary Table)
Role	Login Required	Access Level	Main Purpose
Guest	No	Read-only	View seminar schedule & slot availability
Student	Yes	Limited write	Register & present research
Evaluator	Yes	Controlled write	Evaluate assigned presenters
Coordinator	Yes	Full control	Manage seminar & generate reports
3. Guest Role (Default Mode)
3.1 Description

Guest is the default role when the system is launched.
It allows public users to view seminar-related information without authentication, improving transparency and accessibility.

3.2 Permissions

Guest users can:

View upcoming seminar sessions

View session details (date, venue, session type)

View slot availability (Open / Full / Closed / Requires Application)

Search or filter sessions by date or type

Guest users cannot:

Register for a seminar

Upload files

View presenter evaluations or marks

Access internal reports or awards deliberation

3.3 Modules Available

Public Seminar Schedule Module

Session Availability Viewer

4. Student Role (Presenter)
4.1 Description

The Student role represents postgraduate students who present their research during the seminar.

4.2 Permissions

Students can:

Log in as Student

Register for the seminar by providing:

Research title

Abstract

Supervisor name

Preferred presentation type (Oral / Poster)

Select available seminar sessions (based on slot availability)

Upload presentation materials (slides or poster file path)

View their own registration status and assigned session

Students cannot:

Evaluate other students

View evaluators’ marks or comments

Manage sessions or assign evaluators

4.3 Modules Available

Student Registration Module

Presentation Upload Module

Personal Registration Status Viewer

5. Evaluator Role (Panel Member)
5.1 Description

Evaluators are academic staff assigned to assess student presentations.

5.2 Permissions

Evaluators can:

Log in as Evaluator

View assigned presentations only

Evaluate students using predefined rubrics:

Problem Clarity

Methodology

Results

Presentation Quality

Assign marks for each criterion

Add qualitative comments for presenters

Submit and update evaluations before the deadline

Evaluators cannot:

View evaluations by other evaluators

Modify seminar schedules

Assign themselves to sessions

View award results before finalization

5.3 Modules Available

Evaluation Module

Assigned Presentation Viewer

Rubric-Based Scoring Interface

6. Coordinator Role (Faculty Staff)
6.1 Description

The Coordinator role has the highest authority and manages the overall seminar operation.

6.2 Permissions

Coordinators can:

Log in as Coordinator

Create and manage seminar sessions:

Date

Venue

Session type (Oral / Poster)

Time slots and capacity

Assign students to sessions

Assign evaluators to presentations

Manage poster boards (board IDs and criteria)

Generate seminar schedules

Generate final evaluation reports

Compute and manage awards:

Best Oral Presentation

Best Poster Presentation

People’s Choice Award

Approve or reject registrations if required

6.3 Modules Available

User Management Module

Session Management Module

Assignment Module (Student–Evaluator–Session)

Award & Ceremony Module

Reports & Summary Module

7. System Modules Mapping to Roles
Module	Guest	Student	Evaluator	Coordinator
User Management	✗	✗	✗	✓
Seminar Schedule View	✓	✓	✓	✓
Session Management	✗	✗	✗	✓
Registration	✗	✓	✗	✓
File Upload	✗	✓	✗	✗
Evaluation	✗	✗	✓	✓ (view only)
Poster Board Management	✗	✗	✗	✓
Award Management	✗	✗	✗	✓
Reports & Analytics	✗	✗	✗	✓
8. System Design Decision (Justification)

Guest-first entry improves usability and transparency

Role separation ensures:

Data privacy

Academic integrity

Clear responsibility boundaries

Read-only Guest access minimizes security risk

Design aligns with real-world academic conference systems

9. Conclusion

The proposed Seminar Management System adopts a clear, scalable, and role-based design that supports academic presentation workflows while maintaining transparency and control.
By introducing a default Guest role and enforcing strict permissions for authenticated users, the system ensures both accessibility and data security.