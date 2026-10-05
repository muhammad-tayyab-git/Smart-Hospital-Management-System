# Smart Hospital Management System V2

## Database
The application expects the MySQL database `smart_hospital`.

Local defaults:
- DB URL: `jdbc:mysql://localhost:3306/smart_hospital`
- DB user: `shms_user`
- DB password: `ChangeThisPassword123!`

You can override these with `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` environment variables.

## Demo accounts
Password for the seeded local demo accounts is normalized by the application to:
`Password123!`

Examples:
- `admin@smarthospital.local`
- `doctor.sarah@smarthospital.local`
- `doctor.david@smarthospital.local`
- `reception@smarthospital.local`
- `patient.john@example.com`
- `patient.maria@example.com`
- `patient.ali@example.com`

## Registration
Public registration creates a PATIENT account only. Staff roles are assigned by administrators.

## Important
Do not use the demo password or database password in production. Put production secrets in environment variables or your deployment secret manager.

## Docker
`docker compose up --build` starts MySQL and the Spring Boot application. The SQL file is mounted as an initialization script for a fresh MySQL volume.
