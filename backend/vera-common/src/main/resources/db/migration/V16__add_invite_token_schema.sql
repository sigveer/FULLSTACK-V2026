CREATE TABLE invitations (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    organization_id BIGINT NOT NULL,
    email VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    token VARCHAR(100) NOT NULL UNIQUE,
    invited_by_id BIGINT NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    accepted_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_inv_org FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE CASCADE,
    CONSTRAINT fk_inv_user FOREIGN KEY (invited_by_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_inv_token ON invitations(token);