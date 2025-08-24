-- Initial schema migration for 내꿈은 건물주 project

-- Users table
CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nickname VARCHAR(50) NOT NULL,
    notification_allowed BOOLEAN NOT NULL DEFAULT FALSE,
    marketing_agreed BOOLEAN NOT NULL DEFAULT FALSE,
    character_components JSON,
    home_type VARCHAR(50),
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    deleted_at TIMESTAMP(6) NULL,
    PRIMARY KEY (id),
    INDEX idx_users_deleted_at (deleted_at),
    INDEX idx_users_nickname (nickname)
);

-- User authentication table
CREATE TABLE user_auths (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    provider_type VARCHAR(20) NOT NULL,
    provider_id VARCHAR(255),
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255),
    last_login_at TIMESTAMP(6),
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_auths_email (email),
    UNIQUE KEY uk_user_auths_provider (provider_type, provider_id),
    INDEX idx_user_auths_user_id (user_id)
);

-- Terms table
CREATE TABLE terms (
    id BIGINT NOT NULL AUTO_INCREMENT,
    code VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    is_required BOOLEAN NOT NULL DEFAULT FALSE,
    version VARCHAR(20) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    UNIQUE KEY uk_terms_code_version (code, version),
    INDEX idx_terms_code (code)
);

-- User terms agreement table
CREATE TABLE user_terms_agreement (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    terms_id BIGINT NOT NULL,
    agreed_at TIMESTAMP(6),
    revoked_at TIMESTAMP(6),
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (terms_id) REFERENCES terms(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_terms_agreement (user_id, terms_id),
    INDEX idx_user_terms_agreement_user_id (user_id)
);