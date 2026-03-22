INSERT INTO roles (name, description)
VALUES
    ('ROLE_ADMIN', 'System administrator with full access'),
    ('ROLE_MANAGER', 'Can manage checklists, users and reports'),
    ('ROLE_STAFF', 'Can complete daily routines and logs');

INSERT INTO users (username, email, password_hash, enabled)
VALUES
    ('admin', 'admin@iksystem.local', '$2a$10$exampleAdminHashReplaceLater', TRUE),
    ('manager', 'manager@iksystem.local', '$2a$10$exampleManagerHashReplaceLater', TRUE),
    ('staff', 'staff@iksystem.local', '$2a$10$exampleStaffHashReplaceLater', TRUE);

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
JOIN roles r ON (
    (u.username = 'admin' AND r.name IN ('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_STAFF')) OR
    (u.username = 'manager' AND r.name IN ('ROLE_MANAGER', 'ROLE_STAFF')) OR
    (u.username = 'staff' AND r.name = 'ROLE_STAFF')
);
