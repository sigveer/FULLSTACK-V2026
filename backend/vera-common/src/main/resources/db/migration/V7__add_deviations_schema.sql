CREATE TABLE deviations (
    id BIGINT NOT NULL AUTO_INCREMENT,
    organization_id BIGINT NOT NULL,
    module VARCHAR(20) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    immediate_action TEXT,
    severity VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    reported_by_user_id BIGINT NOT NULL,
    assigned_to_user_id BIGINT NULL,
    reported_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP NULL,
    closed_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_deviations_organization FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE CASCADE,
    CONSTRAINT fk_deviations_reported_by_user FOREIGN KEY (reported_by_user_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT fk_deviations_assigned_to_user FOREIGN KEY (assigned_to_user_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE INDEX idx_deviations_organization_id ON deviations(organization_id);
CREATE INDEX idx_deviations_module ON deviations(module);
CREATE INDEX idx_deviations_status ON deviations(status);
CREATE INDEX idx_deviations_severity ON deviations(severity);
CREATE INDEX idx_deviations_reported_at ON deviations(reported_at);
