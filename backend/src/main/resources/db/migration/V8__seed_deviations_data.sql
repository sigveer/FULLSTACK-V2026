INSERT INTO deviations (
    organization_id,
    module,
    title,
    description,
    immediate_action,
    severity,
    status,
    reported_by_user_id,
    assigned_to_user_id,
    reported_at,
    resolved_at
)
VALUES
    (
        (SELECT id FROM organizations WHERE name = 'IK System'),
        'IK_MAT',
        'Kjøleskap 2 over grenseverdi',
        'Temperatur malt til 6.1 C kl. 08:30. Grenseverdi er 4 C. Ferske fiskeprodukter lagret her.',
        'Flyttet alle varer til kjøleskap 1. Tekniker varslet for inspeksjon.',
        'CRITICAL',
        'OPEN',
        (SELECT id FROM users WHERE email = 'employee@iksystem.local'),
        (SELECT id FROM users WHERE email = 'manager@iksystem.local'),
        DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 2 HOUR),
        NULL
    ),
    (
        (SELECT id FROM organizations WHERE name = 'IK System'),
        'IK_ALKOHOL',
        'Manglende ID-kontroll observert pa kveldsvakt',
        'Ny servitor gjennomførte ikke alderskontroll av gruppe med unge gjester.',
        'Gjestene ble bedt om legitimasjon. Oppfølgingssamtale planlagt.',
        'MEDIUM',
        'IN_PROGRESS',
        (SELECT id FROM users WHERE email = 'manager@iksystem.local'),
        (SELECT id FROM users WHERE email = 'admin@iksystem.local'),
        DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 1 DAY),
        NULL
    ),
    (
        (SELECT id FROM organizations WHERE name = 'IK System'),
        'IK_MAT',
        'Varmholding under 60C',
        'Varmholdingsutstyret holdt ikke tilstrekkelig temperatur ved kveldsskift.',
        'Mat som sto i varmhholding ble kassert. Ny temperaturkontroll bestilt.',
        'HIGH',
        'OPEN',
        (SELECT id FROM users WHERE email = 'employee@iksystem.local'),
        (SELECT id FROM users WHERE email = 'manager@iksystem.local'),
        DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 2 DAY),
        NULL
    ),
    (
        (SELECT id FROM organizations WHERE name = 'IK System'),
        'IK_MAT',
        'Renholdsplan ikke fullført tirsdag',
        'Ukentlig dyprengjøring av kjøkken ble utsatt grunnet sykdom.',
        'Gjennomført onsdag som kompensasjon. Rutine oppdatert.',
        'LOW',
        'RESOLVED',
        (SELECT id FROM users WHERE email = 'employee@iksystem.local'),
        (SELECT id FROM users WHERE email = 'manager@iksystem.local'),
        DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 3 DAY),
        DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 2 DAY)
    ),
    (
        (SELECT id FROM organizations WHERE name = 'Demo Organization'),
        'IK_ALKOHOL',
        'Opplæringsbevis mangler for nyansatt',
        'Nyansatt har ikke fullført dokumentert opplaering i ansvarlig skjenking.',
        'Midlertidig omplassering til oppgaver uten alkoholservering.',
        'MEDIUM',
        'RESOLVED',
        (SELECT id FROM users WHERE email = 'admin@iksystem.local'),
        (SELECT id FROM users WHERE email = 'admin@iksystem.local'),
        DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 5 DAY),
        DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 4 DAY)
    );
