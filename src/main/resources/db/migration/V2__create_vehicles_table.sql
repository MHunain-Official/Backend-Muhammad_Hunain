-- V2__create_vehicles_table.sql
CREATE TABLE vehicles (
    id        UUID           NOT NULL DEFAULT gen_random_uuid(),
    tenant_id VARCHAR(100)   NOT NULL,
    dealer_id UUID           NOT NULL,
    model     VARCHAR(255)   NOT NULL,
    price     NUMERIC(12, 2) NOT NULL CHECK (price > 0),
    status    VARCHAR(20)    NOT NULL CHECK (status IN ('AVAILABLE', 'SOLD')),

    CONSTRAINT pk_vehicles   PRIMARY KEY (id),
    CONSTRAINT fk_vehicles_dealer
        FOREIGN KEY (dealer_id) REFERENCES dealers (id)
        ON DELETE CASCADE
);

CREATE INDEX idx_vehicle_tenant_id ON vehicles (tenant_id);
CREATE INDEX idx_vehicle_dealer_id ON vehicles (dealer_id);
CREATE INDEX idx_vehicle_status    ON vehicles (status);
