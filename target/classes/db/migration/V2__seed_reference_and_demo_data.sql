-- Smart Hospital Management System - Flyway V2 reference/demo data
-- Idempotent seed for roles, departments and local demo records.

SET FOREIGN_KEY_CHECKS = 0;

-- Roles
INSERT IGNORE INTO roles (id, name, description) VALUES
(1, 'ADMIN', 'Hospital administrator'),
(2, 'DOCTOR', 'Medical doctor'),
(3, 'RECEPTIONIST', 'Reception and appointment staff'),
(4, 'NURSE', 'Nursing staff'),
(5, 'PHARMACIST', 'Pharmacy staff'),
(6, 'LAB_TECHNICIAN', 'Laboratory technician'),
(7, 'PATIENT', 'Hospital patient');

-- Departments
INSERT IGNORE INTO departments
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

INSERT IGNORE INTO users
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
INSERT IGNORE INTO user_roles (user_id, role_id) VALUES
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
INSERT IGNORE INTO patients
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
INSERT IGNORE INTO doctors
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
INSERT IGNORE INTO doctor_schedules
(doctor_id, day_of_week, start_time, end_time, slot_duration_minutes)
VALUES
(1, 'MONDAY', '09:00:00', '13:00:00', 30),
(1, 'WEDNESDAY', '09:00:00', '13:00:00', 30),
(1, 'FRIDAY', '10:00:00', '14:00:00', 30),
(2, 'TUESDAY', '09:00:00', '13:00:00', 30),
(2, 'THURSDAY', '09:00:00', '13:00:00', 30),
(2, 'FRIDAY', '14:00:00', '18:00:00', 30);

-- Appointments
INSERT IGNORE INTO appointments
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
INSERT IGNORE INTO medical_records
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
INSERT IGNORE INTO diagnoses
(id, medical_record_id, diagnosis_code, diagnosis_name, description, severity)
VALUES
(1, 1, 'R07.89', 'Other chest pain',
 'Non-specific chest discomfort requiring monitoring.',
 'MILD');

-- Prescription
INSERT IGNORE INTO prescriptions
(id, medical_record_id, doctor_id, patient_id, prescription_date, instructions, status)
VALUES
(1, 1, 1, 1, '2026-09-20 12:00:00',
 'Take medication according to the prescribed schedule.',
 'ACTIVE');

INSERT IGNORE INTO prescription_items
(prescription_id, medicine_name, dosage, frequency, duration, route, instructions)
VALUES
(1, 'Example Medication', '10 mg', 'Once daily', '14 days', 'Oral',
 'Take after breakfast.');

-- Lab orders
INSERT IGNORE INTO lab_orders
(id, patient_id, doctor_id, appointment_id, test_name, priority, status)
VALUES
(1, 1, 1, 3, 'Complete Blood Count (CBC)', 'NORMAL', 'COMPLETED'),
(2, 2, 2, 2, 'Basic Metabolic Panel', 'NORMAL', 'ORDERED');

INSERT IGNORE INTO lab_results
(id, lab_order_id, result_value, unit, reference_range, result_text, verified_by, verified_at)
VALUES
(1, 1, 'Normal', NULL, 'See laboratory reference range',
 'No significant abnormalities detected.',
 6, '2026-09-20 15:00:00');

-- Wards
INSERT IGNORE INTO wards
(id, department_id, name, floor, capacity)
VALUES
(1, 5, 'Emergency Ward', 'Ground Floor', 20),
(2, 1, 'Cardiology Ward', 'Floor 2', 12),
(3, 2, 'Neurology Ward', 'Floor 3', 10);

-- Beds
INSERT IGNORE INTO beds (id, ward_id, bed_number, bed_type, status) VALUES
(1, 1, 'ER-01', 'EMERGENCY', 'AVAILABLE'),
(2, 1, 'ER-02', 'EMERGENCY', 'OCCUPIED'),
(3, 2, 'CARD-01', 'STANDARD', 'AVAILABLE'),
(4, 2, 'CARD-02', 'PRIVATE', 'AVAILABLE'),
(5, 3, 'NEUR-01', 'STANDARD', 'AVAILABLE'),
(6, 3, 'NEUR-02', 'ICU', 'MAINTENANCE');

-- Admission
INSERT IGNORE INTO admissions
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
INSERT IGNORE INTO invoices
(id, invoice_number, patient_id, appointment_id,
 issue_date, due_date, subtotal, discount, tax, total_amount, status)
VALUES
(1, 'INV-000001', 1, 3,
 '2026-09-20', '2026-10-20',
 75.00, 0.00, 0.00, 75.00, 'PAID'),

(2, 'INV-000002', 2, 2,
 '2026-10-01', '2026-11-01',
 70.00, 0.00, 0.00, 70.00, 'PENDING');

INSERT IGNORE INTO payments
(id, invoice_id, amount, payment_method, transaction_reference, status)
VALUES
(1, 1, 75.00, 'CARD', 'DEMO-TXN-000001', 'COMPLETED');

-- Notifications
INSERT IGNORE INTO notifications
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
INSERT IGNORE INTO activities
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
