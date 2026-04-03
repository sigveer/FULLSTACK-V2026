-- ============================================================
-- Food deviations table
-- ============================================================
CREATE TABLE food_deviations (
    id                              BIGINT NOT NULL AUTO_INCREMENT,
    organization_id                 BIGINT NOT NULL,
    reported_at                     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    reported_by_user_id             BIGINT NOT NULL,
    deviation_type                  VARCHAR(30) NOT NULL,
    severity                        VARCHAR(10) NOT NULL,
    description                     TEXT NOT NULL,
    immediate_action                TEXT,
    immediate_action_by_user_id     BIGINT NULL,
    immediate_action_at             TIMESTAMP NULL,
    cause                           TEXT,
    preventive_measures             TEXT,
    preventive_responsible_user_id  BIGINT NULL,
    preventive_deadline             TIMESTAMP NULL,
    status                          VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    created_at                      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_food_dev_org FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE CASCADE,
    CONSTRAINT fk_food_dev_reported_by FOREIGN KEY (reported_by_user_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT fk_food_dev_action_by FOREIGN KEY (immediate_action_by_user_id) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_food_dev_preventive_responsible FOREIGN KEY (preventive_responsible_user_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE INDEX idx_food_dev_org ON food_deviations(organization_id);
CREATE INDEX idx_food_dev_status ON food_deviations(status);
CREATE INDEX idx_food_dev_reported_at ON food_deviations(reported_at);

-- ============================================================
-- Alcohol deviations table
-- ============================================================
CREATE TABLE alcohol_deviations (
    id                              BIGINT NOT NULL AUTO_INCREMENT,
    organization_id                 BIGINT NOT NULL,
    reported_at                     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    reported_by_user_id             BIGINT NOT NULL,
    report_source                   VARCHAR(30) NOT NULL,
    deviation_type                  VARCHAR(50) NOT NULL,
    description                     TEXT NOT NULL,
    immediate_action                TEXT,
    causal_analysis                 VARCHAR(30),
    causal_explanation              TEXT,
    preventive_measures             TEXT,
    preventive_deadline             TIMESTAMP NULL,
    preventive_responsible_user_id  BIGINT NULL,
    status                          VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    created_at                      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_alcohol_dev_org FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE CASCADE,
    CONSTRAINT fk_alcohol_dev_reported_by FOREIGN KEY (reported_by_user_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT fk_alcohol_dev_preventive_responsible FOREIGN KEY (preventive_responsible_user_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE INDEX idx_alcohol_dev_org ON alcohol_deviations(organization_id);
CREATE INDEX idx_alcohol_dev_status ON alcohol_deviations(status);
CREATE INDEX idx_alcohol_dev_reported_at ON alcohol_deviations(reported_at);

-- ============================================================
-- Penalty points ledger for alcohol deviations
-- ============================================================
CREATE TABLE penalty_points (
    id                      BIGINT NOT NULL AUTO_INCREMENT,
    organization_id         BIGINT NOT NULL,
    alcohol_deviation_id    BIGINT NULL,
    points                  INT NOT NULL,
    violation_type          VARCHAR(50) NOT NULL,
    description             VARCHAR(500),
    created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_penalty_org FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE CASCADE,
    CONSTRAINT fk_penalty_alcohol_dev FOREIGN KEY (alcohol_deviation_id) REFERENCES alcohol_deviations(id) ON DELETE SET NULL
);

CREATE INDEX idx_penalty_org ON penalty_points(organization_id);
