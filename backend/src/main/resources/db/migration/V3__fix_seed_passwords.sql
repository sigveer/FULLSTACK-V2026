-- Fix seeded user passwords: 'password' hashed with BCrypt strength 12
UPDATE users
SET password = '$2a$12$tBdYa9T7INK1Mxc3x6RKt.X0u/GFD80lTk0n/LsNIjpQQDkBzSn7q'
WHERE email IN ('admin@iksystem.local', 'manager@iksystem.local', 'employee@iksystem.local');

-- Give admin user membership in Demo Organization too
INSERT INTO memberships (user_id, organization_id, role)
VALUES (1, 2, 'ADMIN');
