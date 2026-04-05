CREATE TABLE temperature_appliances (
    id BIGINT NOT NULL AUTO_INCREMENT,
    organization_id BIGINT NOT NULL,
    name VARCHAR(120) NOT NULL,
    appliance_type VARCHAR(20) NOT NULL,
    min_temperature DECIMAL(6,2) NOT NULL,
    max_temperature DECIMAL(6,2) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_temp_appliances_org (organization_id),
    CONSTRAINT fk_temp_appliances_org FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE CASCADE
);

CREATE TABLE temperature_measurements (
    id BIGINT NOT NULL AUTO_INCREMENT,
    organization_id BIGINT NOT NULL,
    appliance_id BIGINT NOT NULL,
    measured_by_user_id BIGINT NOT NULL,
    measured_at TIMESTAMP NOT NULL,
    temperature DECIMAL(6,2) NOT NULL,
    note TEXT,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_temp_measurements_org (organization_id),
    KEY idx_temp_measurements_appliance (appliance_id),
    KEY idx_temp_measurements_measured_at (measured_at),
    CONSTRAINT fk_temp_measurements_org FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE CASCADE,
    CONSTRAINT fk_temp_measurements_appliance FOREIGN KEY (appliance_id) REFERENCES temperature_appliances(id) ON DELETE CASCADE,
    CONSTRAINT fk_temp_measurements_user FOREIGN KEY (measured_by_user_id) REFERENCES users(id)
);
