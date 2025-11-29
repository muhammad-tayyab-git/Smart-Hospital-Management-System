
# Smart Hospital Management System (SHMS) - Starter (Spring Boot + Thymeleaf + MySQL)

## What's included
- Basic login (session-based), CRUD for Patients, Doctors, Appointments and Bills.
- Thymeleaf templates with Bootstrap UI.
- MySQL configuration placeholders (application.properties).
- Sample users inserted via data.sql.

## Setup
1. Create MySQL database named `shms_db`:
   - In MySQL Workbench: `CREATE DATABASE shms_db;`
2. Update `src/main/resources/application.properties` with your DB credentials.
3. INSERT INTO users (id, username, password, role) VALUES (1, 'admin', 'admin123', 'ADMIN');

Run this query for the admin enrollment and after that you can add doctors,patients etc and manage the things.
And set JDK version to 17.
4. Run:
   ```bash
   mvn spring-boot:run
   ```
5. Open `http://localhost:8080` and login (sample users in data.sql).

Notes: Passwords are plain text for starter only. Swap to Spring Security + hashing before production.

