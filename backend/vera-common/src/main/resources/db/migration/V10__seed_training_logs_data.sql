INSERT INTO training_logs (
    organization_id,
    employee_user_id,
    logged_by_user_id,
    title,
    description,
    completed_at,
    expires_at,
    status
)
VALUES
    (
        (SELECT id FROM organizations WHERE name = 'Everest Sushi & Fusion AS'),
        (SELECT id FROM users WHERE email = 'employee@everest.local'),
        (SELECT id FROM users WHERE email = 'admin@everest.local'),
        'Brannvernkurs',
        'Årlig brannvernopplæring for alle ansatte.',
        DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 3 MONTH),
        DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 9 MONTH),
        'COMPLETED'
    ),
    (
        (SELECT id FROM organizations WHERE name = 'Everest Sushi & Fusion AS'),
        (SELECT id FROM users WHERE email = 'manager@everest.local'),
        (SELECT id FROM users WHERE email = 'admin@everest.local'),
        'HMS-kurs',
        'Grunnleggende HMS-opplæring.',
        DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 11 MONTH),
        DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 20 DAY),
        'EXPIRES_SOON'
    ),
    (
        (SELECT id FROM organizations WHERE name = 'Everest Sushi & Fusion AS'),
        (SELECT id FROM users WHERE email = 'employee@everest.local'),
        (SELECT id FROM users WHERE email = 'manager@everest.local'),
        'Allergenopplæring',
        'Opplæring i allergenhåndtering for kjøkkenpersonell.',
        DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 14 MONTH),
        DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 2 MONTH),
        'EXPIRED'
    ),
    (
        (SELECT id FROM organizations WHERE name = 'Everest Sushi & Fusion AS'),
        (SELECT id FROM users WHERE email = 'manager@everest.local'),
        (SELECT id FROM users WHERE email = 'admin@everest.local'),
        'Ansvarlig vertskap',
        'Kurs i ansvarlig alkoholservering.',
        NULL,
        NULL,
        'NOT_COMPLETED'
    ),
    (
        (SELECT id FROM organizations WHERE name = 'Demo Organization'),
        (SELECT id FROM users WHERE email = 'employee@everest.local'),
        (SELECT id FROM users WHERE email = 'employee@everest.local'),
        'Førstehjelp',
        'Grunnkurs i førstehjelp.',
        DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 1 MONTH),
        DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 11 MONTH),
        'COMPLETED'
    );
