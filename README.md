# Smart Hospital Management System

A portfolio-grade **Hospital Management System** built with **Spring Boot, Java 17, Thymeleaf, Bootstrap 5, Spring Data JPA/Hibernate and MySQL**. The project has evolved from a basic CRUD application into a domain-oriented hospital workflow platform covering authentication, role-based access, appointments, clinical records, prescriptions, laboratory workflows, admissions, beds, billing, payments, notifications, audit activity and operational dashboards.

> **Portfolio / educational project:** This system is not intended for real patient data or clinical use without professional security, privacy, compliance, infrastructure and clinical-safety review.

---

## Table of Contents

1. [Project Overview](#project-overview)
2. [Goals](#goals)
3. [Technology Stack](#technology-stack)
4. [Architecture](#architecture)
5. [Roles and Access Model](#roles-and-access-model)
6. [Authentication and Account Model](#authentication-and-account-model)
7. [Major Features](#major-features)
8. [End-to-End Hospital Workflows](#end-to-end-hospital-workflows)
9. [Database Design](#database-design)
10. [Local Development Setup](#local-development-setup)
11. [Environment Variables](#environment-variables)
12. [Flyway Database Migrations](#flyway-database-migrations)
13. [Running with Docker](#running-with-docker)
14. [Important Application Routes](#important-application-routes)
15. [Security and Production Hardening](#security-and-production-hardening)
16. [Testing](#testing)
17. [Continuous Integration and Delivery](#continuous-integration-and-delivery)
18. [Project Structure](#project-structure)
19. [Development History](#development-history)
20. [Deployment Checklist](#deployment-checklist)
21. [Limitations and Future Work](#limitations-and-future-work)
22. [Portfolio Talking Points](#portfolio-talking-points)
23. [License / Usage](#license--usage)

---

# Project Overview

The Smart Hospital Management System is a server-rendered healthcare application designed to demonstrate practical full-stack software engineering.

The system provides separate experiences for patients, doctors and hospital staff while keeping authorization and ownership checks on the backend.

The current architecture is centered around these domains:

```text
Identity & Access
      |
      +---- Patients
      +---- Doctors
      +---- Staff

Hospital Operations
      |
      +---- Departments
      +---- Appointments
      +---- Admissions
      +---- Wards / Beds

Clinical Care
      |
      +---- Medical Records
      +---- Diagnoses
      +---- Prescriptions
      +---- Laboratory Orders / Results

Financial Operations
      |
      +---- Invoices
      +---- Payments

Communication & Governance
      |
      +---- Notifications
      +---- Activity / Audit Trail
```

---

# Goals

The project is designed to demonstrate:

- Full-stack Java development
- Spring Boot application design
- Layered architecture
- Domain-oriented data modeling
- Role-based authorization
- Secure authentication practices
- Server-side validation
- Transactional business workflows
- Database migrations with Flyway
- Responsive web UI development
- Docker-based local deployment
- Production-oriented configuration
- Automated testing foundations
- Operational health checks

---

# Technology Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Backend | Spring Boot 3.3.2 |
| Persistence | Spring Data JPA / Hibernate 6 |
| Database | MySQL 8+ |
| Database migrations | Flyway |
| Frontend | Thymeleaf + Bootstrap 5 |
| Validation | Jakarta Bean Validation / Spring Validation |
| Password security | BCrypt |
| Real-time foundation | WebSocket / Spring Messaging |
| Monitoring | Spring Boot Actuator |
| Build | Maven |
| Containerization | Docker / Docker Compose |
| Server | Embedded Tomcat |

---

# Architecture

The application follows a layered architecture:

```text
Browser
  |
  v
Thymeleaf Templates / Bootstrap UI
  |
  v
Controllers
  |
  v
Application / Domain Services
  |
  v
Spring Data Repositories
  |
  v
JPA / Hibernate
  |
  v
MySQL
```

Cross-cutting concerns include:

- Authentication
- Role authorization
- Validation
- Exception handling
- Notifications
- Activity logging
- Configuration through environment variables
- Database migration management
- Health monitoring

The application uses `spring.jpa.hibernate.ddl-auto=validate`, meaning Hibernate checks that the database matches the entity model but does not silently modify the production schema.

---

# Roles and Access Model

The application supports seven roles:

### ADMIN

- Hospital command center
- Staff account management
- User/role administration
- Doctors and patient management
- Billing oversight
- Operational metrics
- Activity/audit visibility

### DOCTOR

- Appointment queue
- Start consultation
- Complete consultation
- Clinical notes
- Diagnoses
- Treatment plans
- Prescriptions
- Laboratory orders
- Follow-up dates

### RECEPTIONIST

- Patient coordination
- Appointment coordination
- Billing-related workflows
- Hospital front-desk operations

### NURSE

- Patient/admission support
- Ward and bed operations
- Clinical support workflows

### PHARMACIST

- Prescription fulfilment workflows
- Medication-related operations

### LAB_TECHNICIAN

- Laboratory work queue
- Sample processing
- Laboratory result entry

### PATIENT

- Own profile
- Doctor directory
- Appointment booking
- Appointment cancellation
- Medical history
- Prescriptions
- Laboratory results
- Admissions information
- Invoices
- Payments
- Notifications

Authorization is deny-by-default for protected application routes. The backend, not the browser, is responsible for enforcing access rules.

---

# Authentication and Account Model

## Login identifier

**Email is the unique login identifier.** There is no separate username field.

## Patient registration

Public registration creates **PATIENT accounts only**.

Required fields:

- Email
- Password
- First name
- Last name

Other patient information can be completed later.

## Staff accounts

Patients cannot register themselves as doctors, administrators or other staff members.

Staff accounts are created through the admin staff-management workflow.

Supported staff roles:

- ADMIN
- DOCTOR
- RECEPTIONIST
- NURSE
- PHARMACIST
- LAB_TECHNICIAN

## Passwords

Passwords are stored using BCrypt hashing. Plain-text passwords should never be committed to the repository.

---

# Major Features

## 1. Responsive healthcare UI

- Healthcare-focused visual design
- Responsive navigation
- Mobile-friendly tables and cards
- Role-aware navigation
- Empty states
- Status badges
- Responsive forms
- Dashboard cards
- Consistent buttons and actions

## 2. Admin Command Center

Provides operational visibility into areas such as:

- Doctors
- Patients
- Appointments
- Admissions
- Bed inventory
- Lab orders
- Invoices
- Revenue
- Payments
- Patient growth
- Billing analytics
- Recent activity

## 3. Doctor Clinical Workspace

A doctor can move an appointment through a clinical workflow:

```text
Booked Appointment
       |
       v
Start Consultation
       |
       v
Clinical Visit
       |
       +--> Diagnosis
       +--> Treatment Plan
       +--> Prescription
       +--> Laboratory Order
       +--> Follow-up Date
       |
       v
Complete Visit
       |
       v
Invoice
```

## 4. Laboratory Workflow

Laboratory technicians receive a work queue and can process orders through statuses such as:

```text
ORDERED
   |
   v
SAMPLE_COLLECTED
   |
   v
PROCESSING
   |
   v
COMPLETED
```

Results can contain:

- Result value
- Unit
- Reference range
- Result text
- Optional attachment URL
- Verification information

## 5. Patient My Health

Patients can view their own healthcare information including:

- Medical records
- Clinical history
- Diagnoses
- Prescriptions
- Laboratory orders/results
- Admissions
- Doctor information
- Follow-up information

Patient ownership is checked server-side.

## 6. Appointments

Patients can:

- Select a doctor
- Select a date
- Select an available time slot
- Add a reason
- Submit an appointment
- View their appointments
- Cancel eligible appointments

The backend validates appointment ownership, dates, times and overlapping bookings.

## 7. Admissions and Bed Management

Hospital operations include:

```text
Department
   |
   v
Ward
   |
   v
Bed
   |
   v
Admission
   |
   v
Discharge
```

Bed state is updated as admissions are created and discharged.

## 8. Billing and Payments

The application supports:

- Invoice generation
- Invoice detail pages
- Patient invoice ownership checks
- Payment recording
- Payment methods
- Payment status
- Balance calculation
- Overpayment prevention
- Already-paid protection
- Cancelled invoice protection

## 9. Notifications

Notifications support categories such as:

- Appointment
- Billing
- Laboratory result
- System information
- Warning
- Success

Read/unread state is tracked per user.

## 10. Activity / Audit Foundation

The `activities` table provides a foundation for recording important application actions with:

- User
- Action
- Entity type
- Entity ID
- Description
- IP address
- Timestamp

---

# End-to-End Hospital Workflows

## Patient journey

```text
Register
   |
   v
Login
   |
   v
Find Doctor
   |
   v
Book Appointment
   |
   v
Doctor Consultation
   |
   +--> Diagnosis
   +--> Prescription
   +--> Lab Order
   |
   v
Visit Completed
   |
   v
Invoice
   |
   v
Payment
   |
   v
My Health / History
```

## Doctor journey

```text
Login
  |
  v
Doctor Dashboard
  |
  v
Appointment Queue
  |
  v
Start Consultation
  |
  v
Clinical Record
  |
  +--> Diagnosis
  +--> Prescription
  +--> Lab Order
  |
  v
Complete Visit
```

## Laboratory journey

```text
Doctor creates Lab Order
          |
          v
Lab Technician Queue
          |
          v
Sample Collected
          |
          v
Processing
          |
          v
Result Entered
          |
          v
Result Verified
          |
          v
Patient can view result
```

## Admission journey

```text
Patient
  |
  v
Admission
  |
  v
Ward
  |
  v
Bed assigned
  |
  v
Patient admitted
  |
  v
Discharge
  |
  v
Bed available again
```

---

# Database Design

The V2 model contains **21 tables**.

| # | Table | Purpose |
|---:|---|---|
| 1 | `users` | Authentication and shared user identity |
| 2 | `roles` | Available application roles |
| 3 | `user_roles` | User-to-role mapping |
| 4 | `departments` | Hospital departments |
| 5 | `patients` | Patient-specific information |
| 6 | `doctors` | Doctor-specific information |
| 7 | `doctor_schedules` | Doctor availability |
| 8 | `appointments` | Patient/doctor appointments |
| 9 | `medical_records` | Clinical visit records |
| 10 | `diagnoses` | Diagnoses attached to medical records |
| 11 | `prescriptions` | Prescription headers |
| 12 | `prescription_items` | Medicines in prescriptions |
| 13 | `lab_orders` | Laboratory requests |
| 14 | `lab_results` | Laboratory results |
| 15 | `wards` | Hospital wards |
| 16 | `beds` | Bed inventory |
| 17 | `admissions` | Patient admissions |
| 18 | `invoices` | Patient billing documents |
| 19 | `payments` | Invoice payments |
| 20 | `notifications` | User notifications |
| 21 | `activities` | Activity/audit foundation |

### Relationship overview

```text
users ----< user_roles >---- roles
  |
  +---- patients
  |
  +---- doctors ----> departments
  |
  +---- notifications
  |
  +---- activities

patients ----< appointments >---- doctors
                    |
                    +---- department

appointments ---- medical_records
medical_records ----< diagnoses
medical_records ----< prescriptions ----< prescription_items
appointments ----< lab_orders ----< lab_results

patients ----< admissions >---- doctors
admissions ---- beds ---- wards ---- departments

patients ----< invoices ----< payments
```

---

# Local Development Setup

## Prerequisites

Install:

- Java 17
- Maven
- MySQL 8+
- Git
- Docker / Docker Compose if using containers

Verify:

```bash
java -version
mvn -version
mysql --version
git --version
```

## 1. Clone the repository

```bash
git clone <YOUR-GITHUB-REPOSITORY-URL>
cd Smart-Hospital-Management-System
```

## 2. Create the database

Create an empty database:

```sql
CREATE DATABASE smart_hospital;
```

Create/grant the application database user according to your local MySQL setup.

Example:

```sql
GRANT ALL PRIVILEGES ON smart_hospital.* TO 'shms_user'@'localhost';
FLUSH PRIVILEGES;
```

Do **not** paste real credentials into Git or into this README.

## 3. Configure environment variables

```bash
export DB_URL='jdbc:mysql://127.0.0.1:3306/smart_hospital?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC'
export DB_USERNAME='shms_user'
export DB_PASSWORD='your-local-password'
export FLYWAY_ENABLED=true
export PORT=8080
export COOKIE_SECURE=false
```

## 4. Build and test

```bash
mvn clean test
```

## 5. Start the application

```bash
mvn spring-boot:run
```

Open:

```text
http://localhost:8080
```

Health endpoint:

```text
http://localhost:8080/actuator/health
```

---

# Environment Variables

| Variable | Purpose | Example |
|---|---|---|
| `DB_URL` | JDBC database connection | `jdbc:mysql://127.0.0.1:3306/smart_hospital?...` |
| `DB_USERNAME` | Application DB user | `shms_user` |
| `DB_PASSWORD` | Application DB password | local secret |
| `PORT` | HTTP server port | `8080` |
| `FLYWAY_ENABLED` | Enables migrations | `true` |
| `COOKIE_SECURE` | Secure cookies over HTTPS | `true` in production |
| `HEALTH_SHOW_DETAILS` | Health detail visibility | `never` / controlled value |

`.env.example` is a template only. Never commit real credentials.

---

# Flyway Database Migrations

Flyway is now the source of truth for the application schema.

Migration directory:

```text
src/main/resources/db/migration/
```

Current migrations:

```text
V1__baseline_schema.sql
V2__seed_reference_and_demo_data.sql
```

## V1 — baseline schema

Creates the complete 21-table V2 domain schema.

## V2 — seed data

Adds reference/demo data in an idempotent way.

## Hibernate behavior

Hibernate is configured as:

```properties
spring.jpa.hibernate.ddl-auto=validate
```

This is intentional. Hibernate validates the model against the database but does not create, alter or delete production tables.

## Existing local V2 database

If your existing database already contains the V2 schema, Flyway can baseline it and then apply later migrations.

**Do not delete the working database just to start Flyway.**

## Fresh database

For a new empty database:

1. Create `smart_hospital`.
2. Configure DB environment variables.
3. Start the application.
4. Flyway creates the schema.
5. Flyway applies seed/reference data.
6. Hibernate validates the schema.
7. Spring Boot starts normally.

The historical file `database/smart_hospital_v2.sql` is retained for reference. It should not be the normal startup mechanism anymore.

---

# Running with Docker

The Compose stack starts MySQL first and waits for its health check before starting the application.

Example:

```bash
export MYSQL_ROOT_PASSWORD='local-root-password'
export DB_USERNAME='shms_user'
export DB_PASSWORD='local-app-password'

docker compose up --build
```

The application receives its database configuration through environment variables.

For production, use the hosting provider's secret management instead of shell history or committed `.env` files.

---

# Important Application Routes

The exact route set can evolve with the application, but the current feature areas include:

| Area | Example route |
|---|---|
| Home | `/` or `/home` |
| Login | `/login` |
| Registration | `/register` |
| Profile | `/profile` |
| Doctor directory | `/doctors` |
| Patient dashboard | patient dashboard route |
| Appointments | appointment routes |
| Doctor workspace | doctor dashboard/workspace routes |
| Admin dashboard | admin dashboard route |
| Staff management | `/admin/staff` |
| Billing | invoice/billing routes |
| Health check | `/actuator/health` |

Use the controller mappings in `src/main/java` as the authoritative route reference if routes change.

---

# Security and Production Hardening

## Implemented baseline

### Authentication

- Email-based login
- BCrypt password hashing
- Fresh session after successful login
- Session invalidation on logout
- Patient-only public registration
- Staff creation controlled by admin

### Authorization

- Role-aware navigation
- Deny-by-default route authorization
- Server-side ownership checks
- Patient appointment ownership enforcement
- Patient invoice ownership enforcement
- Destructive operations moved away from unsafe GET links where applicable

### HTTP/session security

- HttpOnly session cookies
- SameSite=Lax session cookies
- Optional Secure cookie setting for HTTPS
- `X-Content-Type-Options: nosniff`
- `X-Frame-Options: SAMEORIGIN`
- Strict referrer policy
- Permissions policy restricting camera, microphone and geolocation by default

### Error handling

Friendly error pages are provided for:

- 404 Not Found
- 403 Forbidden
- 500 Internal Server Error

Server-side exceptions are logged without exposing implementation details to users.

### Database security

- No database password committed in source code
- Environment-based credentials
- Hibernate validation mode
- Flyway versioned migrations
- Database application account separated from root access

### Health monitoring

Spring Boot Actuator exposes a health endpoint suitable for deployment health checks:

```text
/actuator/health
```

Health details should remain restricted in production.

---

# Testing

The project includes a testing foundation covering authentication-related behavior and deterministic role/email handling.

Run:

```bash
mvn clean test
```

Recommended future test layers:

```text
Unit Tests
   |
   +--> Service logic
   +--> Validation
   +--> Authorization decisions

Integration Tests
   |
   +--> Controllers
   +--> JPA repositories
   +--> Flyway migrations

End-to-End Tests
   |
   +--> Registration
   +--> Login
   +--> Appointment booking
   +--> Doctor consultation
   +--> Lab workflow
   +--> Billing/payment
```

---

# Continuous Integration and Delivery

GitHub Actions provides an automated quality gate for the `main` branch and pull requests. The workflow is defined in `.github/workflows/ci.yml`.

The CI pipeline performs:

1. Checkout of the repository
2. Java 17 setup with Maven dependency caching
3. Unit tests
4. Containerized MySQL integration tests using Testcontainers
5. Production JAR packaging
6. Docker image build validation

The integration test is intentionally opt-in locally because it requires Docker:

```bash
mvn test
```

For the full test suite, including the real MySQL/Flyway integration test:

```bash
mvn verify -Dintegration=true
```

The integration test verifies that Flyway can create the expected hospital schema and seed the seven reference roles. It uses an isolated MySQL 8.4 container, so it does not modify the developer's local `smart_hospital` database.

### CI workflow

```text
Pull Request / Push
        |
        v
   GitHub Actions
        |
        +--> Java 17 setup
        |
        +--> Unit tests
        |
        +--> Testcontainers + MySQL
        |       |
        |       +--> Flyway migrations
        |       +--> Hibernate validation
        |       +--> Schema/seed assertions
        |
        +--> Maven package
        |
        +--> Docker build
        v
    Quality Gate
```

The workflow does not deploy to a cloud provider yet. Keeping deployment provider-neutral makes it possible to add Render, Railway, AWS, Azure, GCP or another platform later without coupling the core CI pipeline to a specific vendor.

# Project Structure

A simplified structure:

```text
Smart-Hospital-Management-System/
├── src/
│   ├── main/
│   │   ├── java/com/shms/
│   │   │   ├── config/
│   │   │   ├── controller/
│   │   │   ├── model/
│   │   │   ├── repository/
│   │   │   ├── service/
│   │   │   └── SmartHospitalApplication.java
│   │   └── resources/
│   │       ├── db/migration/
│   │       ├── static/
│   │       ├── templates/
│   │       └── application.properties
│   └── test/
├── database/
│   └── smart_hospital_v2.sql
├── Dockerfile
├── docker-compose.yml
├── .dockerignore
├── .env.example
├── .gitignore
├── pom.xml
└── README.md
```

---

# Development History

## V2 domain upgrade

The original CRUD-oriented application was expanded into a domain-oriented hospital platform.

Major backend changes included:

- Replaced legacy `AppointmentSlot` with `Appointment`.
- Replaced legacy `BillRepository` with `InvoiceRepository`.
- Added medical records.
- Added diagnoses.
- Added prescriptions and prescription items.
- Added laboratory orders and results.
- Added wards and beds.
- Added admissions.
- Added payments.
- Added notifications.
- Added activity/audit foundation.
- Added centralized staff management.
- Added role-aware authorization.
- Added server-side appointment ownership validation.

## UI/UX upgrade

The UI was redesigned around a healthcare SaaS visual system with:

- Responsive navigation
- Role-specific workspaces
- Admin command center
- Doctor dashboard
- Patient health area
- Appointment booking
- Billing views
- Staff management
- Responsive tables/cards/forms
- Mobile layouts
- Consistent status indicators

## Feature workflow upgrade

The system was then expanded into complete workflows:

- Patient appointment booking
- Doctor consultation
- Clinical records
- Diagnoses
- Prescriptions
- Laboratory workflow
- Admissions
- Ward/bed management
- Invoice generation
- Payment recording
- Notifications
- Operational dashboards

## Step 4 — Production Hardening

The current release adds:

- Flyway database migrations
- Validation-only Hibernate schema behavior
- Environment-based production configuration
- Session-cookie hardening
- HTTP security headers
- Friendly error pages
- Global exception handling
- Actuator health endpoint
- Automated test foundations
- Docker health checks
- Docker build-context cleanup
- Consolidated project documentation

---

# Deployment Checklist

Before deploying a real hosted instance of this portfolio application:

### Database

- [ ] Use a managed/external MySQL database.
- [ ] Do not use a development database in production.
- [ ] Restrict database network access.
- [ ] Configure backups.
- [ ] Test database restoration.
- [ ] Keep Flyway enabled.

### Secrets

- [ ] Set database credentials through deployment secrets.
- [ ] Remove demo credentials.
- [ ] Rotate development passwords.
- [ ] Never commit `.env` files.
- [ ] Never commit API keys or tokens.

### Application

- [ ] Set `PORT` from the hosting provider.
- [ ] Use HTTPS.
- [ ] Set `COOKIE_SECURE=true`.
- [ ] Keep `ddl-auto=validate`.
- [ ] Confirm `/actuator/health` works.
- [ ] Restrict actuator exposure.

### Files

Uploaded medical documents should not depend on container-local storage. Use object storage or another persistent, access-controlled storage service for a real deployment.

### Security

- [ ] Add CSRF protection where appropriate.
- [ ] Add MFA for privileged accounts.
- [ ] Centralize secrets.
- [ ] Add rate limiting / brute-force protection.
- [ ] Add security scanning.
- [ ] Perform penetration testing.
- [ ] Review authorization rules.
- [ ] Implement strong audit controls.

### Healthcare compliance

Before any real clinical deployment, perform formal privacy, security, legal and regulatory review appropriate to the target country and healthcare environment.

---

# Limitations and Future Work

This is a strong portfolio project, but it is not a production hospital information system.

Recommended future improvements include:

1. Comprehensive integration and end-to-end testing.
2. CI/CD with GitHub Actions.
3. Testcontainers for MySQL integration tests.
4. Strong CSRF protection and security review.
5. MFA for administrators and staff.
6. Password reset and account recovery flows.
7. Rate limiting and login abuse protection.
8. Object storage for medical attachments.
9. Centralized logging and monitoring.
10. Metrics and alerting.
11. Backup and disaster-recovery procedures.
12. Immutable audit logging.
13. Advanced reporting and analytics.
14. API layer for mobile applications.
15. OpenAPI/Swagger documentation.
16. Container image vulnerability scanning.
17. Automated dependency/security updates.
18. More granular permissions than role-only access.
19. Appointment reminders.
20. Email/SMS/push notification integrations.
21. More advanced pharmacy inventory management.
22. Insurance and claims workflows.
23. Multi-hospital / multi-tenant support.

---

# Portfolio Talking Points

This project can demonstrate the following skills during interviews:

### Backend engineering

> "I designed the system around domain-specific services instead of putting all hospital logic directly in controllers. This made appointment, clinical, laboratory, admission and billing workflows easier to isolate and evolve."

### Security

> "I made email the unique authentication identifier, restricted public registration to patients, and moved staff account creation behind admin authorization. I also enforced patient ownership on the server rather than trusting a patient ID submitted by the browser."

### Database engineering

> "The application uses a normalized 21-table domain model and Flyway migrations so the database schema is version-controlled and reproducible across environments. Hibernate is validation-only rather than automatically changing production tables."

### Production readiness

> "I moved database configuration to environment variables, added health checks, Docker readiness checks, error handling, secure session settings and an Actuator health endpoint."

### Full-stack development

> "I built role-specific dashboards and workflows for patients, doctors and hospital staff using Spring Boot, Thymeleaf, Bootstrap, JPA and MySQL."

### Healthcare workflow design

> "The application models a complete journey from appointment to consultation, diagnosis, prescription/lab order, invoice and payment, while also supporting admissions and bed management."

---

# Version Milestones

### `v2.0.0`

Working V2 baseline with the expanded hospital domain and database model.

### Phase 2 Portfolio Upgrade

Expanded UI/UX, role-based workflows and clinical/operational features.

### Step 4 — Production Hardening

Database migrations, environment-driven configuration, security hardening, error handling, testing foundations, health checks and Docker improvements.

### Next milestone — Step 5

Recommended focus:

```text
Automated Integration Testing
          |
          v
GitHub Actions CI/CD
          |
          v
Container Image Build
          |
          v
Deployment Pipeline
          |
          v
Production Observability
```

---

# License / Usage

This repository is intended as a personal portfolio and educational software project.

Do not use it with real patient information or as a clinical system without appropriate professional review, security controls, privacy protections, compliance validation and operational safeguards.
