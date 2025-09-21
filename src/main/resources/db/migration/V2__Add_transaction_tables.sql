-- Add transaction and repayment management tables

-- Transactions table
CREATE TABLE IF NOT EXISTS transactions (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    transaction_type VARCHAR(10) NOT NULL COMMENT 'LEND, BORROW',
    counterpart_name VARCHAR(12) NOT NULL,
    counterpart_character LONGTEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'JSON format character data',
    relationship VARCHAR(20) NOT NULL COMMENT 'PARENT, SIBLING, FRIEND, LOVER, OTHER',
    custom_relationship VARCHAR(50) COMMENT 'Custom relationship when relationship is OTHER',
    transaction_date DATE NOT NULL,
    total_amount DECIMAL(19,2) NOT NULL,
    completed_amount DECIMAL(19,2) NOT NULL DEFAULT 0,
    memo TEXT,
    repayment_type VARCHAR(20) NOT NULL COMMENT 'DIVIDED_BY_PERIOD, FIXED_MONTHLY, FLEXIBLE',
    target_date DATE COMMENT 'Target completion date',
    monthly_amount DECIMAL(19,2) COMMENT 'Monthly payment amount for FIXED_MONTHLY type',
    payment_day INT COMMENT 'Payment day of month (1-31) for FIXED_MONTHLY type',
    has_target_date BOOLEAN COMMENT 'Whether target date is set',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_transaction_date (transaction_date),
    KEY idx_repayment_type (repayment_type),
    CONSTRAINT fk_transactions_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Repayment schedules table for schedule-based repayment management
CREATE TABLE IF NOT EXISTS repayment_schedules (
    id BIGINT NOT NULL AUTO_INCREMENT,
    transaction_id BIGINT NOT NULL,
    scheduled_date DATE NOT NULL COMMENT 'Originally scheduled payment date',
    scheduled_amount DECIMAL(19,2) NOT NULL COMMENT 'Originally scheduled payment amount',
    actual_date DATE COMMENT 'Actual payment date (null if not paid yet)',
    actual_amount DECIMAL(19,2) COMMENT 'Actual payment amount (null if not paid yet)',
    status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED' COMMENT 'SCHEDULED, IN_PROGRESS, OVERDUE, COMPLETED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_transaction_id (transaction_id),
    KEY idx_scheduled_date (scheduled_date),
    KEY idx_status (status),
    KEY idx_transaction_status (transaction_id, status),
    CONSTRAINT fk_repayment_schedules_transaction FOREIGN KEY (transaction_id) REFERENCES transactions(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
