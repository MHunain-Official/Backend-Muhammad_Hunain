-- V1__create_dealers_table.sql
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE dealers (
    id               UUID         NOT NULL DEFAULT gen_random_uuid(),
    tenant_id        VARCHAR(100) NOT NULL,
    name             VARCHAR(255) NOT NULL,
    email            VARCHAR(255) NOT NULL,
    subscription_type VARCHAR(20) NOT NULL CHECK (subscription_type IN ('BASIC', 'PREMIUM')),

    CONSTRAINT pk_dealers PRIMARY KEY (id),
    CONSTRAINT uq_dealers_email UNIQUE (email)
);

CREATE INDEX idx_dealer_tenant_id ON dealers (tenant_id);
CREATE INDEX idx_dealer_email     ON dealers (email);
