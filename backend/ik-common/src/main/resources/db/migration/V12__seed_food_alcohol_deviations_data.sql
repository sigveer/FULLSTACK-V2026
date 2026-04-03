-- Seed food deviations
INSERT INTO food_deviations (
    organization_id, reported_at, reported_by_user_id, deviation_type, severity,
    description, immediate_action, immediate_action_by_user_id, immediate_action_at,
    cause, preventive_measures, preventive_responsible_user_id, preventive_deadline, status
)
VALUES
    (
        (SELECT id FROM organizations WHERE name = 'IK System'),
        DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 2 HOUR),
        (SELECT id FROM users WHERE email = 'employee@iksystem.local'),
        'TEMPERATUR', 'HIGH',
        'Kjøleskap 2 målt til 6.1°C kl. 08:30. Grenseverdi er 4°C. Ferske fiskeprodukter lagret her.',
        'Flyttet alle varer til kjøleskap 1. Tekniker varslet.',
        (SELECT id FROM users WHERE email = 'employee@iksystem.local'),
        DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 1 HOUR),
        'Termostat sviktet etter strømbrudd natt til tirsdag.',
        'Installere temperaturalarm med SMS-varsling på alle kjøleskap.',
        (SELECT id FROM users WHERE email = 'manager@iksystem.local'),
        DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 7 DAY),
        'OPEN'
    ),
    (
        (SELECT id FROM organizations WHERE name = 'IK System'),
        DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 3 DAY),
        (SELECT id FROM users WHERE email = 'employee@iksystem.local'),
        'RENHOLD', 'LOW',
        'Ukentlig dyprengjøring av kjøkken ble utsatt grunnet sykdom.',
        'Gjennomført onsdag som kompensasjon. Rutine oppdatert.',
        (SELECT id FROM users WHERE email = 'employee@iksystem.local'),
        DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 2 DAY),
        'Sykdom blant rengjøringspersonale.',
        'Oppdatere bemanningsplan med backup-personell for renhold.',
        (SELECT id FROM users WHERE email = 'manager@iksystem.local'),
        DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 14 DAY),
        'CLOSED'
    ),
    (
        (SELECT id FROM organizations WHERE name = 'IK System'),
        DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 1 DAY),
        (SELECT id FROM users WHERE email = 'manager@iksystem.local'),
        'ALLERGEN', 'MEDIUM',
        'Allergenliste ikke oppdatert etter menyendring. Kunde med nøtteallergi fikk feil informasjon.',
        'Rettet allergenlisten umiddelbart. Informerte gjesten og tilbød ny rett.',
        (SELECT id FROM users WHERE email = 'manager@iksystem.local'),
        DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 1 DAY),
        'Rutine for oppdatering av allergenliste ved menyendring ble ikke fulgt.',
        'Innføre sjekkliste-punkt ved hver menyendring som krever signering.',
        (SELECT id FROM users WHERE email = 'manager@iksystem.local'),
        DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 3 DAY),
        'UNDER_TREATMENT'
    );

-- Seed alcohol deviations
INSERT INTO alcohol_deviations (
    organization_id, reported_at, reported_by_user_id, report_source, deviation_type,
    description, immediate_action, causal_analysis, causal_explanation,
    preventive_measures, preventive_deadline, preventive_responsible_user_id, status
)
VALUES
    (
        (SELECT id FROM organizations WHERE name = 'IK System'),
        DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 1 DAY),
        (SELECT id FROM users WHERE email = 'manager@iksystem.local'),
        'EGENRAPPORT', 'SKJENKING_APENBART_BERUSET',
        'Ny servitør gjennomførte ikke alderskontroll av gruppe med unge gjester.',
        'Gjestene ble bedt om legitimasjon. Oppfølgingssamtale planlagt.',
        'MANGLENDE_OPPLAERING',
        'Servitøren hadde ikke fått tilstrekkelig opplæring i rutiner for alderskontroll.',
        'Gjennomføre obligatorisk opplæring for alle nye ansatte innen første arbeidsuke.',
        DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 7 DAY),
        (SELECT id FROM users WHERE email = 'manager@iksystem.local'),
        'UNDER_TREATMENT'
    ),
    (
        (SELECT id FROM organizations WHERE name = 'IK System'),
        DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 5 DAY),
        (SELECT id FROM users WHERE email = 'admin@iksystem.local'),
        'SJENKEKONTROLL', 'BERUSET_PERSON_I_LOKALET',
        'Kommunal kontroll avdekket at en tydelig beruset gjest befant seg i lokalet.',
        'Gjesten ble bedt om å forlate lokalet umiddelbart.',
        'RUTINE_IKKE_FULGT',
        'Ansvarlig bartender overvåket ikke gjestene tilstrekkelig ved høy aktivitet.',
        'Innføre faste runder i lokalet hver halvtime ved kveldsarrangementer.',
        DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 14 DAY),
        (SELECT id FROM users WHERE email = 'admin@iksystem.local'),
        'OPEN'
    );

-- Auto-add penalty points for the sjenkekontroll deviation above
INSERT INTO penalty_points (organization_id, alcohol_deviation_id, points, violation_type, description)
VALUES (
    (SELECT id FROM organizations WHERE name = 'IK System'),
    (SELECT id FROM alcohol_deviations WHERE description LIKE 'Kommunal kontroll avdekket%' LIMIT 1),
    2,
    'BERUSET_PERSON_I_LOKALET',
    'Auto: sjenkekontroll – BERUSET_PERSON_I_LOKALET'
);
