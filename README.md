# Smart Hospital Management System

A portfolio-grade hospital operations web application built with **Java 17, Spring Boot 3.3, Thymeleaf, Bootstrap 5 and MySQL**.

## Product scope

The system is designed around role-based hospital workflows:

- Patient registration and email-based login
- Role-aware dashboards
- Doctor directory and appointment booking
- Patient and doctor management
- Appointment status workflows
- Billing and invoice views
- Activity/audit tracking
- Responsive Bootstrap UI
- Environment-based database configuration
- MySQL schema validation with `ddl-auto=validate`

### Roles

`ADMIN`, `DOCTOR`, `RECEPTIONIST`, `NURSE`, `PHARMACIST`, `LAB_TECHNICIAN`, `PATIENT`

Public registration creates **patients only**. Staff accounts should be created/assigned by an administrator.

## Local setup

1. Create/import the `smart_hospital` MySQL database and grant the application user access.
2. Copy `.env.example` to `.env` for your own reference, but export the variables in the shell because Spring Boot does not automatically load a `.env` file.
3. Set:

```bash
export DB_URL='jdbc:mysql://127.0.0.1:3306/smart_hospital?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC'
export DB_USERNAME='shms_user'
export DB_PASSWORD='your-local-password'
export PORT=8080
```

4. Build:

```bash
mvn clean package
```

5. Run:

```bash
mvn spring-boot:run
```

6. Open `http://localhost:8080`.

## Architecture

The application follows a conventional Spring MVC structure:

```text
controller -> service -> repository -> entity
                    \-> DTOs / view models
templates + static assets -> Thymeleaf UI
MySQL -> relational persistence
```

## Production notes

- Never commit real database credentials.
- Use managed MySQL/PostgreSQL for production.
- Add versioned Flyway migrations before enabling Flyway in production.
- Store patient uploads in object storage rather than the application filesystem.
- Use HTTPS and secure session/cookie settings in production.
- Replace demo credentials before deployment.

## Portfolio highlights

This project demonstrates full-stack Java development, relational database design, MVC architecture, role-based workflows, server-rendered responsive UI, validation, authentication, and production-oriented configuration.
