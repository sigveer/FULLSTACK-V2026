-- Checklist templates for demo/testing
INSERT INTO checklists (organization_id, name, description, frequency, active)
VALUES
    (1, 'Hygiene og renhold - Morgen', 'Daglig sjekk av renhold før åpning', 'DAILY', TRUE),
    (1, 'Skjenkekontroll - Kveldsvakt', 'Kontroll av ID og skjenkerutiner', 'DAILY', TRUE),
    (1, 'Månedlig temperaturkalibrering', 'Verifisering av termometre', 'MONTHLY', TRUE),
    (2, 'Bar-rutiner demo', 'Demo-sjekkliste for organisasjon 2', 'WEEKLY', TRUE);

-- Items for organization 1 checklist 1 (fully completed)
INSERT INTO checklist_items (checklist_id, title, description, completed, completed_at)
SELECT c.id, 'Vask alle arbeidsflater', 'Benk, skjærebrett og stasjoner', TRUE, CURRENT_TIMESTAMP
FROM checklists c
WHERE c.organization_id = 1 AND c.name = 'Hygiene og renhold - Morgen';

INSERT INTO checklist_items (checklist_id, title, description, completed, completed_at)
SELECT c.id, 'Sjekk håndvaskstasjoner', 'Såpe og papir på plass', TRUE, CURRENT_TIMESTAMP
FROM checklists c
WHERE c.organization_id = 1 AND c.name = 'Hygiene og renhold - Morgen';

INSERT INTO checklist_items (checklist_id, title, description, completed, completed_at)
SELECT c.id, 'Desinfiser kontaktflater', 'Dørhåndtak, skjermer og terminaler', TRUE, CURRENT_TIMESTAMP
FROM checklists c
WHERE c.organization_id = 1 AND c.name = 'Hygiene og renhold - Morgen';

-- Items for organization 1 checklist 2 (in progress)
INSERT INTO checklist_items (checklist_id, title, description, completed, completed_at)
SELECT c.id, 'Verifiser ID ved bestilling', 'Alle ansatte følger rutinen', TRUE, CURRENT_TIMESTAMP
FROM checklists c
WHERE c.organization_id = 1 AND c.name = 'Skjenkekontroll - Kveldsvakt';

INSERT INTO checklist_items (checklist_id, title, description, completed)
SELECT c.id, 'Loggfør avvik', 'Mangler eller brudd logges umiddelbart', FALSE
FROM checklists c
WHERE c.organization_id = 1 AND c.name = 'Skjenkekontroll - Kveldsvakt';

INSERT INTO checklist_items (checklist_id, title, description, completed)
SELECT c.id, 'Stikkprøvekontroll ved stengetid', 'Siste kontroll før stenging', FALSE
FROM checklists c
WHERE c.organization_id = 1 AND c.name = 'Skjenkekontroll - Kveldsvakt';

-- Items for organization 1 checklist 3 (not started)
INSERT INTO checklist_items (checklist_id, title, description, completed)
SELECT c.id, 'Kalibrer kjøleskapstermometer', NULL, FALSE
FROM checklists c
WHERE c.organization_id = 1 AND c.name = 'Månedlig temperaturkalibrering';

INSERT INTO checklist_items (checklist_id, title, description, completed)
SELECT c.id, 'Kalibrer frysertermometer', NULL, FALSE
FROM checklists c
WHERE c.organization_id = 1 AND c.name = 'Månedlig temperaturkalibrering';

-- Items for organization 2 checklist (fully completed)
INSERT INTO checklist_items (checklist_id, title, description, completed, completed_at)
SELECT c.id, 'Kontroller serveringsområde', NULL, TRUE, CURRENT_TIMESTAMP
FROM checklists c
WHERE c.organization_id = 2 AND c.name = 'Bar-rutiner demo';
