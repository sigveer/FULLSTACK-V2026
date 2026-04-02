CREATE TABLE training_logs (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    organization_id BIGINT       NOT NULL,
    employee_user_id BIGINT      NOT NULL,
    logged_by_user_id BIGINT     NOT NULL,
    title           VARCHAR(255) NOT NULL,
    description     TEXT,
    completed_at    TIMESTAMP    NULL,
    expires_at      TIMESTAMP    NULL,
    status          VARCHAR(20)  NOT NULL DEFAULT 'NOT_COMPLETED',
    created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    CONSTRAINT fk_training_logs_organization  FOREIGN KEY (organization_id)   REFERENCES organizations(id) ON DELETE CASCADE,
    CONSTRAINT fk_training_logs_employee      FOREIGN KEY (employee_user_id)  REFERENCES users(id)         ON DELETE RESTRICT,
    CONSTRAINT fk_training_logs_logged_by     FOREIGN KEY (logged_by_user_id) REFERENCES users(id)         ON DELETE RESTRICT
);

CREATE INDEX idx_training_logs_organization_id  ON training_logs(organization_id);
CREATE INDEX idx_training_logs_employee_user_id ON training_logs(employee_user_id);
CREATE INDEX idx_training_logs_status           ON training_logs(status);
CREATE INDEX idx_training_logs_expires_at       ON training_logs(expires_at);
