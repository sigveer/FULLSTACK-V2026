INSERT INTO organizations (name, org_number)
VALUES
    ('IK System', '123456789'),
    ('Demo Organization', '987654321');

-- Password is 'password' hashed with BCrypt (strength 12)
INSERT INTO users (email, password, full_name, phone_number)
VALUES
    ('admin@iksystem.local', '$2a$12$LJ3m4ys3Gzl0Ih2UjKLBYeFP3kNMbWR6V0VQ5xNdPBM5DqHO/nIVu', 'Admin User', '+4712345678'),
    ('manager@iksystem.local', '$2a$12$LJ3m4ys3Gzl0Ih2UjKLBYeFP3kNMbWR6V0VQ5xNdPBM5DqHO/nIVu', 'Manager User', '+4712345679'),
    ('employee@iksystem.local', '$2a$12$LJ3m4ys3Gzl0Ih2UjKLBYeFP3kNMbWR6V0VQ5xNdPBM5DqHO/nIVu', 'Employee User', '+4712345680');

-- Memberships: admin + manager in IK System, employee in both orgs
INSERT INTO memberships (user_id, organization_id, role)
VALUES
    (1, 1, 'ADMIN'),
    (2, 1, 'MANAGER'),
    (3, 1, 'EMPLOYEE'),
    (3, 2, 'ADMIN');
