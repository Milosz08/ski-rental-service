-- liquibase formatted sql
-- changeset milosz08:aq1

CREATE TABLE IF NOT EXISTS roles
(
    id BIGINT NOT NULL AUTO_INCREMENT,

    role_name VARCHAR(30) NOT NULL,
    alias CHAR(1) NOT NULL,
    role_eng VARCHAR(6) NOT NULL,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    INDEX (role_name)
)
ENGINE=InnoDB COLLATE=utf16_polish_ci;
