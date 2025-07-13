-- 회원
CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       nickname VARCHAR(50),
                       allow_notification BOOLEAN,
                       created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                       updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                       deleted_at DATETIME NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 회원 인증 (수정됨)
CREATE TABLE user_auth (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           user_id BIGINT NOT NULL,
                           provider_type VARCHAR(50) NOT NULL, -- KAKAO, BOS 등
                           provider_id VARCHAR(100),
                           email VARCHAR(255) NOT NULL,
                           password_hash VARCHAR(255),
                           last_login_at DATETIME,
                           CONSTRAINT fk_user_auth_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 약관
CREATE TABLE terms (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       code VARCHAR(50) NOT NULL UNIQUE,
                       title VARCHAR(255) NOT NULL,
                       content TEXT NOT NULL,
                       is_required BOOLEAN NOT NULL DEFAULT TRUE,
                       version VARCHAR(20) NOT NULL,
                       created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 유저 약관 동의 내역
CREATE TABLE user_terms_agreement (
                                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      user_id BIGINT NOT NULL,
                                      terms_id BIGINT NOT NULL,
                                      agreed_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                      revoked_at DATETIME NULL,
                                      CONSTRAINT fk_user_terms_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                                      CONSTRAINT fk_user_terms_terms FOREIGN KEY (terms_id) REFERENCES terms(id) ON DELETE CASCADE,
                                      UNIQUE KEY uq_user_terms (user_id, terms_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
