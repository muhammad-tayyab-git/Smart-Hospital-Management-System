package com.shms.config;

import org.springframework.context.annotation.Configuration;

/**
 * Database seed data is managed by Flyway migrations.
 * Keeping startup initialization out of the application prevents production
 * startup from mutating user/role records unexpectedly.
 */
@Configuration
public class DataInitializer {
    // Intentionally empty. See db/migration/V2__seed_reference_and_demo_data.sql.
}
