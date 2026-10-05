-- ============================================================
-- SMART HOSPITAL MANAGEMENT SYSTEM - DATABASE V2
-- MySQL 8.x / MySQL Workbench
-- ============================================================

DROP DATABASE IF EXISTS smart_hospital;
CREATE DATABASE smart_hospital
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE smart_hospital;

-- Application database user (local/development only)
CREATE USER IF NOT EXISTS 'shms_user'@'%' IDENTIFIED BY 'ChangeThisPassword123!';
GRANT ALL PRIVILEGES ON smart_hospital.* TO 'shms_user'@'%';
FLUSH PRIVILEGES;

SET FOREIGN_KEY_CHECKS = 0;

-- ============================================================
-- 1. USERS
-- ============================================================

CREATE TABLE users (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(30),
    profile_image_url VARCHAR(500),
    status ENUM('ACTIVE','INACTIVE','SUSPENDED','PENDING') NOT NULL DEFAULT 'ACTIVE',
    last_login_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_users_status (status)
) ENGINE=InnoDB;

-- ============================================================
-- 2. ROLES
-- ============================================================

CREATE TABLE roles (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE user_roles (
    user_id BIGINT UNSIGNED NOT NULL,
    role_id BIGINT UNSIGNED NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role
        FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ============================================================
-- 3. DEPARTMENTS
-- ============================================================

CREATE TABLE departments (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    code VARCHAR(20) NOT NULL UNIQUE,
    description TEXT,
    location VARCHAR(255),
    phone VARCHAR(30),
    status ENUM('ACTIVE','INACTIVE') NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ============================================================
-- 4. PATIENTS
-- ============================================================

CREATE TABLE patients (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL UNIQUE,
    patient_number VARCHAR(30) NOT NULL UNIQUE,
    date_of_birth DATE,
    gender ENUM('MALE','FEMALE','OTHER','PREFER_NOT_TO_SAY'),
    blood_group ENUM(
        'A_POSITIVE','A_NEGATIVE',
        'B_POSITIVE','B_NEGATIVE',
        'AB_POSITIVE','AB_NEGATIVE',
        'O_POSITIVE','O_NEGATIVE'
    ),
    address TEXT,
    emergency_contact_name VARCHAR(200),
    emergency_contact_phone VARCHAR(30),
    allergies TEXT,
    status ENUM('ACTIVE','INACTIVE') NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_patients_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ============================================================
-- 5. DOCTORS
-- ============================================================

CREATE TABLE doctors (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL UNIQUE,
    doctor_number VARCHAR(30) NOT NULL UNIQUE,
    department_id BIGINT UNSIGNED NOT NULL,
    specialization VARCHAR(150),
    qualification VARCHAR(255),
    license_number VARCHAR(100) UNIQUE,
    experience_years INT NOT NULL DEFAULT 0,
    consultation_fee DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    bio TEXT,
    status ENUM('ACTIVE','INACTIVE','ON_LEAVE') NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_doctors_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_doctors_department
        FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE RESTRICT,
    INDEX idx_doctors_department (department_id),
    INDEX idx_doctors_status (status)
) ENGINE=InnoDB;

-- ============================================================
-- 6. DOCTOR SCHEDULES
-- ============================================================

CREATE TABLE doctor_schedules (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    doctor_id BIGINT UNSIGNED NOT NULL,
    day_of_week ENUM(
        'MONDAY','TUESDAY','WEDNESDAY','THURSDAY',
        'FRIDAY','SATURDAY','SUNDAY'
    ) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    slot_duration_minutes INT NOT NULL DEFAULT 30,
    status ENUM('AVAILABLE','UNAVAILABLE') NOT NULL DEFAULT 'AVAILABLE',
    CONSTRAINT fk_schedule_doctor
        FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE,
    INDEX idx_schedule_doctor_day (doctor_id, day_of_week)
) ENGINE=InnoDB;

-- ============================================================
-- 7. APPOINTMENTS
-- ============================================================

CREATE TABLE appointments (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    appointment_number VARCHAR(30) NOT NULL UNIQUE,
    patient_id BIGINT UNSIGNED NOT NULL,
    doctor_id BIGINT UNSIGNED NOT NULL,
    department_id BIGINT UNSIGNED NOT NULL,
    appointment_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    appointment_type ENUM(
        'CONSULTATION','FOLLOW_UP','EMERGENCY','CHECKUP'
    ) NOT NULL DEFAULT 'CONSULTATION',
    reason TEXT,
    status ENUM(
        'REQUESTED','CONFIRMED','IN_PROGRESS',
        'COMPLETED','CANCELLED','NO_SHOW'
    ) NOT NULL DEFAULT 'REQUESTED',
    notes TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_appointment_patient
        FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE RESTRICT,
    CONSTRAINT fk_appointment_doctor
        FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE RESTRICT,
    CONSTRAINT fk_appointment_department
        FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE RESTRICT,
    INDEX idx_appointment_patient (patient_id),
    INDEX idx_appointment_doctor_date (doctor_id, appointment_date),
    INDEX idx_appointment_date_status (appointment_date, status)
) ENGINE=InnoDB;

-- ============================================================
-- 8. MEDICAL RECORDS
-- ============================================================

CREATE TABLE medical_records (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT UNSIGNED NOT NULL,
    doctor_id BIGINT UNSIGNED NOT NULL,
    appointment_id BIGINT UNSIGNED UNIQUE NULL,
    visit_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    chief_complaint TEXT,
    symptoms TEXT,
    clinical_notes TEXT,
    diagnosis_summary TEXT,
    treatment_plan TEXT,
    follow_up_date DATE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_medical_patient
        FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE RESTRICT,
    CONSTRAINT fk_medical_doctor
        FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE RESTRICT,
    CONSTRAINT fk_medical_appointment
        FOREIGN KEY (appointment_id) REFERENCES appointments(id) ON DELETE SET NULL,
    INDEX idx_medical_patient_date (patient_id, visit_date)
) ENGINE=InnoDB;

CREATE TABLE diagnoses (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    medical_record_id BIGINT UNSIGNED NOT NULL,
    diagnosis_code VARCHAR(50),
    diagnosis_name VARCHAR(255) NOT NULL,
    description TEXT,
    severity ENUM('MILD','MODERATE','SEVERE','CRITICAL'),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_diagnosis_record
        FOREIGN KEY (medical_record_id) REFERENCES medical_records(id) ON DELETE CASCADE,
    INDEX idx_diagnosis_record (medical_record_id)
) ENGINE=InnoDB;

-- ============================================================
-- 9. PRESCRIPTIONS
-- ============================================================

CREATE TABLE prescriptions (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    medical_record_id BIGINT UNSIGNED NOT NULL,
    doctor_id BIGINT UNSIGNED NOT NULL,
    patient_id BIGINT UNSIGNED NOT NULL,
    prescription_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    instructions TEXT,
    status ENUM('ACTIVE','COMPLETED','CANCELLED') NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_prescription_record
        FOREIGN KEY (medical_record_id) REFERENCES medical_records(id) ON DELETE CASCADE,
    CONSTRAINT fk_prescription_doctor
        FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE RESTRICT,
    CONSTRAINT fk_prescription_patient
        FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE RESTRICT,
    INDEX idx_prescription_patient (patient_id)
) ENGINE=InnoDB;

CREATE TABLE prescription_items (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    prescription_id BIGINT UNSIGNED NOT NULL,
    medicine_name VARCHAR(255) NOT NULL,
    dosage VARCHAR(100),
    frequency VARCHAR(100),
    duration VARCHAR(100),
    route VARCHAR(100),
    instructions TEXT,
    CONSTRAINT fk_prescription_item
        FOREIGN KEY (prescription_id) REFERENCES prescriptions(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ============================================================
-- 10. LABORATORY
-- ============================================================

CREATE TABLE lab_orders (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT UNSIGNED NOT NULL,
    doctor_id BIGINT UNSIGNED NOT NULL,
    appointment_id BIGINT UNSIGNED NULL,
    test_name VARCHAR(255) NOT NULL,
    priority ENUM('NORMAL','URGENT','STAT') NOT NULL DEFAULT 'NORMAL',
    status ENUM(
        'ORDERED','SAMPLE_COLLECTED','PROCESSING',
        'COMPLETED','CANCELLED'
    ) NOT NULL DEFAULT 'ORDERED',
    ordered_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_lab_order_patient
        FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE RESTRICT,
    CONSTRAINT fk_lab_order_doctor
        FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE RESTRICT,
    CONSTRAINT fk_lab_order_appointment
        FOREIGN KEY (appointment_id) REFERENCES appointments(id) ON DELETE SET NULL,
    INDEX idx_lab_order_patient (patient_id),
    INDEX idx_lab_order_status (status)
) ENGINE=InnoDB;

CREATE TABLE lab_results (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    lab_order_id BIGINT UNSIGNED NOT NULL,
    result_value VARCHAR(255),
    unit VARCHAR(50),
    reference_range VARCHAR(255),
    result_text TEXT,
    attachment_url VARCHAR(500),
    verified_by BIGINT UNSIGNED NULL,
    verified_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_lab_result_order
        FOREIGN KEY (lab_order_id) REFERENCES lab_orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_lab_result_verifier
        FOREIGN KEY (verified_by) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ============================================================
-- 11. WARDS / BEDS / ADMISSIONS
-- ============================================================

CREATE TABLE wards (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    department_id BIGINT UNSIGNED NOT NULL,
    name VARCHAR(100) NOT NULL,
    floor VARCHAR(50),
    capacity INT NOT NULL DEFAULT 0,
    status ENUM('ACTIVE','INACTIVE') NOT NULL DEFAULT 'ACTIVE',
    CONSTRAINT fk_ward_department
        FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE RESTRICT,
    INDEX idx_ward_department (department_id)
) ENGINE=InnoDB;

CREATE TABLE beds (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    ward_id BIGINT UNSIGNED NOT NULL,
    bed_number VARCHAR(30) NOT NULL,
    bed_type ENUM('STANDARD','ICU','PRIVATE','EMERGENCY') NOT NULL DEFAULT 'STANDARD',
    status ENUM('AVAILABLE','OCCUPIED','RESERVED','MAINTENANCE')
        NOT NULL DEFAULT 'AVAILABLE',
    CONSTRAINT fk_bed_ward
        FOREIGN KEY (ward_id) REFERENCES wards(id) ON DELETE RESTRICT,
    UNIQUE KEY uq_ward_bed (ward_id, bed_number),
    INDEX idx_bed_status (status)
) ENGINE=InnoDB;

CREATE TABLE admissions (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    admission_number VARCHAR(30) NOT NULL UNIQUE,
    patient_id BIGINT UNSIGNED NOT NULL,
    doctor_id BIGINT UNSIGNED NOT NULL,
    bed_id BIGINT UNSIGNED NOT NULL,
    admission_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    discharge_date DATETIME NULL,
    reason TEXT,
    diagnosis TEXT,
    notes TEXT,
    status ENUM('ADMITTED','DISCHARGED','TRANSFERRED','CANCELLED')
        NOT NULL DEFAULT 'ADMITTED',
    CONSTRAINT fk_admission_patient
        FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE RESTRICT,
    CONSTRAINT fk_admission_doctor
        FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE RESTRICT,
    CONSTRAINT fk_admission_bed
        FOREIGN KEY (bed_id) REFERENCES beds(id) ON DELETE RESTRICT,
    INDEX idx_admission_patient (patient_id),
    INDEX idx_admission_status (status)
) ENGINE=InnoDB;

-- ============================================================
-- 12. BILLING
-- ============================================================

CREATE TABLE invoices (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    invoice_number VARCHAR(30) NOT NULL UNIQUE,
    patient_id BIGINT UNSIGNED NOT NULL,
    appointment_id BIGINT UNSIGNED NULL,
    issue_date DATE NOT NULL,
    due_date DATE,
    subtotal DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    discount DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    tax DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    total_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    status ENUM(
        'DRAFT','PENDING','PARTIALLY_PAID',
        'PAID','CANCELLED','OVERDUE'
    ) NOT NULL DEFAULT 'DRAFT',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_invoice_patient
        FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE RESTRICT,
    CONSTRAINT fk_invoice_appointment
        FOREIGN KEY (appointment_id) REFERENCES appointments(id) ON DELETE SET NULL,
    INDEX idx_invoice_patient (patient_id),
    INDEX idx_invoice_status (status)
) ENGINE=InnoDB;

CREATE TABLE payments (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    invoice_id BIGINT UNSIGNED NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    payment_method ENUM('CASH','CARD','BANK_TRANSFER','ONLINE') NOT NULL,
    transaction_reference VARCHAR(255),
    payment_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status ENUM('PENDING','COMPLETED','FAILED','REFUNDED')
        NOT NULL DEFAULT 'PENDING',
    CONSTRAINT fk_payment_invoice
        FOREIGN KEY (invoice_id) REFERENCES invoices(id) ON DELETE RESTRICT,
    INDEX idx_payment_invoice (invoice_id)
) ENGINE=InnoDB;

-- ============================================================
-- 13. NOTIFICATIONS / ACTIVITIES
-- ============================================================

CREATE TABLE notifications (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    type ENUM(
        'INFO','SUCCESS','WARNING','APPOINTMENT',
        'BILLING','LAB_RESULT','SYSTEM'
    ) NOT NULL DEFAULT 'INFO',
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notification_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_notification_user_read (user_id, is_read)
) ENGINE=InnoDB;

CREATE TABLE activities (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NULL,
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(100),
    entity_id BIGINT UNSIGNED,
    description TEXT,
    ip_address VARCHAR(45),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_activity_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_activity_user_date (user_id, created_at),
    INDEX idx_activity_entity (entity_type, entity_id)
) ENGINE=InnoDB;

-- ============================================================
-- SEED DATA
-- ============================================================

-- Roles
INSERT INTO roles (id, name, description) VALUES
(1, 'ADMIN', 'Hospital administrator'),
(2, 'DOCTOR', 'Medical doctor'),
(3, 'RECEPTIONIST', 'Reception and appointment staff'),
(4, 'NURSE', 'Nursing staff'),
(5, 'PHARMACIST', 'Pharmacy staff'),
(6, 'LAB_TECHNICIAN', 'Laboratory technician'),
(7, 'PATIENT', 'Hospital patient');

-- Departments
INSERT INTO departments
(id, name, code, description, location, phone)
VALUES
(1, 'Cardiology', 'CARD',
 'Diagnosis and treatment of heart-related conditions',
 'Building A - Floor 2', '+36 1 555 1001'),
(2, 'Neurology', 'NEUR',
 'Diagnosis and treatment of neurological conditions',
 'Building A - Floor 3', '+36 1 555 1002'),
(3, 'Pediatrics', 'PED',
 'Medical care for children',
 'Building B - Floor 1', '+36 1 555 1003'),
(4, 'Orthopedics', 'ORTH',
 'Musculoskeletal diagnosis and treatment',
 'Building B - Floor 2', '+36 1 555 1004'),
(5, 'Emergency Medicine', 'ER',
 'Emergency and urgent medical care',
 'Building C - Ground Floor', '+36 1 555 1005'),
(6, 'Radiology', 'RAD',
 'Medical imaging and diagnostic services',
 'Building C - Floor 1', '+36 1 555 1006'),
(7, 'Laboratory', 'LAB',
 'Clinical laboratory services',
 'Building C - Floor 1', '+36 1 555 1007');

-- ============================================================
-- DEMO USERS
--
-- IMPORTANT:
-- These are BCrypt-compatible hashes for development/demo use.
-- Demo password for all accounts: Password123!
-- Change/remove these accounts before production.
-- ============================================================

INSERT INTO users
(id, email, password_hash, first_name, last_name, phone, status)
VALUES
(1, 'admin@smarthospital.local',
 '$2a$10$7EqJtq98hPqEX7fNZaFWoO8Qm7WwZrH8l6x0qGfGvQ3X6M3hQwW5K',
 'System', 'Administrator', '+36 30 100 0001', 'ACTIVE'),

(2, 'doctor.sarah@smarthospital.local',
 '$2a$10$7EqJtq98hPqEX7fNZaFWoO8Qm7WwZrH8l6x0qGfGvQ3X6M3hQwW5K',
 'Sarah', 'Ahmed', '+36 30 100 0002', 'ACTIVE'),

(3, 'doctor.david@smarthospital.local',
 '$2a$10$7EqJtq98hPqEX7fNZaFWoO8Qm7WwZrH8l6x0qGfGvQ3X6M3hQwW5K',
 'David', 'Khan', '+36 30 100 0003', 'ACTIVE'),

(4, 'reception@smarthospital.local',
 '$2a$10$7EqJtq98hPqEX7fNZaFWoO8Qm7WwZrH8l6x0qGfGvQ3X6M3hQwW5K',
 'Emma', 'Wilson', '+36 30 100 0004', 'ACTIVE'),

(5, 'nurse@smarthospital.local',
 '$2a$10$7EqJtq98hPqEX7fNZaFWoO8Qm7WwZrH8l6x0qGfGvQ3X6M3hQwW5K',
 'Olivia', 'Martin', '+36 30 100 0005', 'ACTIVE'),

(6, 'lab@smarthospital.local',
 '$2a$10$7EqJtq98hPqEX7fNZaFWoO8Qm7WwZrH8l6x0qGfGvQ3X6M3hQwW5K',
 'James', 'Taylor', '+36 30 100 0006', 'ACTIVE'),

(7, 'patient.john@example.com',
 '$2a$10$7EqJtq98hPqEX7fNZaFWoO8Qm7WwZrH8l6x0qGfGvQ3X6M3hQwW5K',
 'John', 'Smith', '+36 30 100 0007', 'ACTIVE'),

(8, 'patient.maria@example.com',
 '$2a$10$7EqJtq98hPqEX7fNZaFWoO8Qm7WwZrH8l6x0qGfGvQ3X6M3hQwW5K',
 'Maria', 'Garcia', '+36 30 100 0008', 'ACTIVE'),

(9, 'patient.ali@example.com',
 '$2a$10$7EqJtq98hPqEX7fNZaFWoO8Qm7WwZrH8l6x0qGfGvQ3X6M3hQwW5K',
 'Ali', 'Hassan', '+36 30 100 0009', 'ACTIVE');

-- User roles
INSERT INTO user_roles (user_id, role_id) VALUES
(1, 1),
(2, 2),
(3, 2),
(4, 3),
(5, 4),
(6, 6),
(7, 7),
(8, 7),
(9, 7);

-- Patients
INSERT INTO patients
(id, user_id, patient_number, date_of_birth, gender, blood_group,
 address, emergency_contact_name, emergency_contact_phone, allergies)
VALUES
(1, 7, 'PAT-000001', '1998-04-15', 'MALE', 'O_POSITIVE',
 'Budapest, Hungary', 'Anna Smith', '+36 30 200 0001', 'None known'),

(2, 8, 'PAT-000002', '1995-09-22', 'FEMALE', 'A_POSITIVE',
 'Budapest, Hungary', 'Carlos Garcia', '+36 30 200 0002', 'Penicillin'),

(3, 9, 'PAT-000003', '2001-01-10', 'MALE', 'B_POSITIVE',
 'Debrecen, Hungary', 'Fatima Hassan', '+36 30 200 0003', 'None known');

-- Doctors
INSERT INTO doctors
(id, user_id, doctor_number, department_id, specialization,
 qualification, license_number, experience_years, consultation_fee, bio)
VALUES
(1, 2, 'DOC-000001', 1, 'Cardiology',
 'MD, Cardiology Specialist', 'HU-CARD-10001', 12, 75.00,
 'Experienced cardiologist specializing in preventive and clinical cardiology.'),

(2, 3, 'DOC-000002', 2, 'Neurology',
 'MD, Neurology Specialist', 'HU-NEUR-10002', 9, 70.00,
 'Neurologist specializing in headache, epilepsy and neurological diagnostics.');

-- Doctor schedules
INSERT INTO doctor_schedules
(doctor_id, day_of_week, start_time, end_time, slot_duration_minutes)
VALUES
(1, 'MONDAY', '09:00:00', '13:00:00', 30),
(1, 'WEDNESDAY', '09:00:00', '13:00:00', 30),
(1, 'FRIDAY', '10:00:00', '14:00:00', 30),
(2, 'TUESDAY', '09:00:00', '13:00:00', 30),
(2, 'THURSDAY', '09:00:00', '13:00:00', 30),
(2, 'FRIDAY', '14:00:00', '18:00:00', 30);

-- Appointments
INSERT INTO appointments
(id, appointment_number, patient_id, doctor_id, department_id,
 appointment_date, start_time, end_time, appointment_type,
 reason, status, notes)
VALUES
(1, 'APT-000001', 1, 1, 1,
 '2026-10-08', '09:00:00', '09:30:00', 'CONSULTATION',
 'Routine cardiac consultation', 'CONFIRMED',
 'Patient requested a routine cardiovascular check.'),

(2, 'APT-000002', 2, 2, 2,
 '2026-10-09', '10:00:00', '10:30:00', 'CHECKUP',
 'Recurring headaches', 'REQUESTED',
 NULL),

(3, 'APT-000003', 3, 1, 1,
 '2026-10-10', '11:00:00', '11:30:00', 'FOLLOW_UP',
 'Follow-up consultation', 'COMPLETED',
 'Follow-up after previous consultation.');

-- Medical records
INSERT INTO medical_records
(id, patient_id, doctor_id, appointment_id, visit_date,
 chief_complaint, symptoms, clinical_notes, diagnosis_summary, treatment_plan, follow_up_date)
VALUES
(1, 1, 1, 3, '2026-09-20 11:30:00',
 'Occasional chest discomfort',
 'Mild intermittent chest discomfort during exercise',
 'Physical examination completed. ECG recommended.',
 'Non-specific chest discomfort; further observation recommended.',
 'Lifestyle modification and ECG follow-up.',
 '2026-10-20');

-- Diagnoses
INSERT INTO diagnoses
(id, medical_record_id, diagnosis_code, diagnosis_name, description, severity)
VALUES
(1, 1, 'R07.89', 'Other chest pain',
 'Non-specific chest discomfort requiring monitoring.',
 'MILD');

-- Prescription
INSERT INTO prescriptions
(id, medical_record_id, doctor_id, patient_id, prescription_date, instructions, status)
VALUES
(1, 1, 1, 1, '2026-09-20 12:00:00',
 'Take medication according to the prescribed schedule.',
 'ACTIVE');

INSERT INTO prescription_items
(prescription_id, medicine_name, dosage, frequency, duration, route, instructions)
VALUES
(1, 'Example Medication', '10 mg', 'Once daily', '14 days', 'Oral',
 'Take after breakfast.');

-- Lab orders
INSERT INTO lab_orders
(id, patient_id, doctor_id, appointment_id, test_name, priority, status)
VALUES
(1, 1, 1, 3, 'Complete Blood Count (CBC)', 'NORMAL', 'COMPLETED'),
(2, 2, 2, 2, 'Basic Metabolic Panel', 'NORMAL', 'ORDERED');

INSERT INTO lab_results
(id, lab_order_id, result_value, unit, reference_range, result_text, verified_by, verified_at)
VALUES
(1, 1, 'Normal', NULL, 'See laboratory reference range',
 'No significant abnormalities detected.',
 6, '2026-09-20 15:00:00');

-- Wards
INSERT INTO wards
(id, department_id, name, floor, capacity)
VALUES
(1, 5, 'Emergency Ward', 'Ground Floor', 20),
(2, 1, 'Cardiology Ward', 'Floor 2', 12),
(3, 2, 'Neurology Ward', 'Floor 3', 10);

-- Beds
INSERT INTO beds (id, ward_id, bed_number, bed_type, status) VALUES
(1, 1, 'ER-01', 'EMERGENCY', 'AVAILABLE'),
(2, 1, 'ER-02', 'EMERGENCY', 'OCCUPIED'),
(3, 2, 'CARD-01', 'STANDARD', 'AVAILABLE'),
(4, 2, 'CARD-02', 'PRIVATE', 'AVAILABLE'),
(5, 3, 'NEUR-01', 'STANDARD', 'AVAILABLE'),
(6, 3, 'NEUR-02', 'ICU', 'MAINTENANCE');

-- Admission
INSERT INTO admissions
(id, admission_number, patient_id, doctor_id, bed_id,
 admission_date, reason, diagnosis, notes, status)
VALUES
(1, 'ADM-000001', 3, 1, 2,
 '2026-10-01 09:30:00',
 'Emergency observation',
 'Requires short-term observation.',
 'Patient is stable and under observation.',
 'ADMITTED');

-- Invoices
INSERT INTO invoices
(id, invoice_number, patient_id, appointment_id,
 issue_date, due_date, subtotal, discount, tax, total_amount, status)
VALUES
(1, 'INV-000001', 1, 3,
 '2026-09-20', '2026-10-20',
 75.00, 0.00, 0.00, 75.00, 'PAID'),

(2, 'INV-000002', 2, 2,
 '2026-10-01', '2026-11-01',
 70.00, 0.00, 0.00, 70.00, 'PENDING');

INSERT INTO payments
(id, invoice_id, amount, payment_method, transaction_reference, status)
VALUES
(1, 1, 75.00, 'CARD', 'DEMO-TXN-000001', 'COMPLETED');

-- Notifications
INSERT INTO notifications
(user_id, title, message, type, is_read)
VALUES
(7, 'Appointment Confirmed',
 'Your appointment APT-000001 has been confirmed.',
 'APPOINTMENT', FALSE),

(8, 'Appointment Request',
 'Your appointment request is waiting for confirmation.',
 'APPOINTMENT', FALSE),

(1, 'System Ready',
 'Smart Hospital Management System database is ready.',
 'SYSTEM', TRUE);

-- Activities
INSERT INTO activities
(user_id, action, entity_type, entity_id, description, ip_address)
VALUES
(1, 'SYSTEM_INITIALIZED', 'SYSTEM', 1,
 'Hospital management database initialized with demo data.',
 '127.0.0.1'),

(1, 'USER_CREATED', 'USER', 7,
 'Demo patient account created.',
 '127.0.0.1'),

(2, 'MEDICAL_RECORD_CREATED', 'MEDICAL_RECORD', 1,
 'Medical record created for patient PAT-000001.',
 '127.0.0.1');

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- VERIFICATION
-- ============================================================

SELECT 'Database created successfully' AS message;

SHOW TABLES;

SELECT COUNT(*) AS total_users FROM users;
SELECT COUNT(*) AS total_patients FROM patients;
SELECT COUNT(*) AS total_doctors FROM doctors;
SELECT COUNT(*) AS total_appointments FROM appointments;
SELECT COUNT(*) AS total_medical_records FROM medical_records;
SELECT COUNT(*) AS total_prescriptions FROM prescriptions;
SELECT COUNT(*) AS total_lab_orders FROM lab_orders;
SELECT COUNT(*) AS total_invoices FROM invoices;

-- Demo login accounts:
-- admin@smarthospital.local       / Password123!
-- doctor.sarah@smarthospital.local / Password123!
-- doctor.david@smarthospital.local / Password123!
-- reception@smarthospital.local   / Password123!
-- nurse@smarthospital.local       / Password123!
-- lab@smarthospital.local         / Password123!
-- patient.john@example.com        / Password123!
-- patient.maria@example.com       / Password123!
-- patient.ali@example.com         / Password123!
--
-- IMPORTANT:
-- Replace demo accounts/passwords before production.
-- ============================================================
