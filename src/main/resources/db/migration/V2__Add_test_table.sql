-- Test migration to verify flyway is working properly

CREATE TABLE flyway_test (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id)
);

-- Insert test data
INSERT INTO flyway_test (name) VALUES ('Flyway is working!');