-- Seed temperature tracking data for organization 1 (IK System)
-- 3 fridges + 2 freezers, and 5 measurements without deviations.

INSERT INTO temperature_appliances (organization_id, name, appliance_type, min_temperature, max_temperature, is_active)
VALUES
    (1, 'Kjøleskap 1', 'FRIDGE', 0.00, 4.00, TRUE),
    (1, 'Kjøleskap 2', 'FRIDGE', 0.00, 4.00, TRUE),
    (1, 'Kjøleskap 3', 'FRIDGE', 0.00, 4.00, TRUE),
    (1, 'Fryser 1', 'FREEZER', -25.00, -18.00, TRUE),
    (1, 'Fryser 2', 'FREEZER', -25.00, -18.00, TRUE);

-- Use user_id=2 (Manager User) from seed data as registrar.
INSERT INTO temperature_measurements (organization_id, appliance_id, measured_by_user_id, measured_at, temperature, note, status)
VALUES
    (1, (SELECT id FROM temperature_appliances WHERE organization_id = 1 AND name = 'Kjøleskap 1' LIMIT 1), 2, '2026-04-04 08:00:00', 2.8, 'Morgenkontroll', 'OK'),
    (1, (SELECT id FROM temperature_appliances WHERE organization_id = 1 AND name = 'Kjøleskap 2' LIMIT 1), 2, '2026-04-04 08:05:00', 3.1, 'Morgenkontroll', 'OK'),
    (1, (SELECT id FROM temperature_appliances WHERE organization_id = 1 AND name = 'Kjøleskap 3' LIMIT 1), 2, '2026-04-04 08:10:00', 1.9, 'Morgenkontroll', 'OK'),
    (1, (SELECT id FROM temperature_appliances WHERE organization_id = 1 AND name = 'Fryser 1' LIMIT 1), 2, '2026-04-04 08:15:00', -20.2, 'Morgenkontroll', 'OK'),
    (1, (SELECT id FROM temperature_appliances WHERE organization_id = 1 AND name = 'Fryser 2' LIMIT 1), 2, '2026-04-04 08:20:00', -21.0, 'Morgenkontroll', 'OK');
