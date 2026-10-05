package com.shms.integration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * End-to-end database verification.
 *
 * Disabled during ordinary local unit-test runs so developers do not need
 * Docker just to execute `mvn test`. CI enables it with -Dintegration=true.
 */
@EnabledIfSystemProperty(named = "integration", matches = "true")
@Testcontainers
@SpringBootTest
class DatabaseIntegrationTest {

    @Container
    static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.4")
            .withDatabaseName("smart_hospital")
            .withUsername("shms_test")
            .withPassword("shms_test_password");

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL::getUsername);
        registry.add("spring.datasource.password", MYSQL::getPassword);
        registry.add("spring.flyway.enabled", () -> "true");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
        registry.add("app.demo-data.enabled", () -> "false");
    }

    @Autowired
    private JdbcTemplate jdbc;

    @Test
    void flywayCreatesExpectedHospitalSchema() {
        Integer tables = jdbc.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables " +
                "WHERE table_schema = DATABASE() AND table_type = 'BASE TABLE'",
                Integer.class);

        assertEquals(22, tables, "21 domain tables plus Flyway history are expected");
        assertTrue(tableExists("users"));
        assertTrue(tableExists("appointments"));
        assertTrue(tableExists("medical_records"));
        assertTrue(tableExists("lab_results"));
        assertTrue(tableExists("invoices"));
        assertTrue(tableExists("activities"));
    }

    @Test
    void flywaySeedContainsReferenceRoles() {
        Integer roleCount = jdbc.queryForObject("SELECT COUNT(*) FROM roles", Integer.class);
        assertEquals(7, roleCount);

        Integer patientRole = jdbc.queryForObject(
                "SELECT COUNT(*) FROM roles WHERE name = 'PATIENT'", Integer.class);
        assertEquals(1, patientRole);
    }

    private boolean tableExists(String table) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables " +
                "WHERE table_schema = DATABASE() AND table_name = ?",
                Integer.class,
                table);
        return count != null && count == 1;
    }
}
