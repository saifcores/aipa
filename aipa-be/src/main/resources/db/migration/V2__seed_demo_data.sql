-- Demo seed data for AIPA (Senegal / XOF payment ops scenarios)

INSERT INTO customers (id, firstname, lastname, phone, email, created_at, updated_at) VALUES
('11111111-1111-1111-1111-111111111111', 'Awa', 'Diop', '+221771000001', 'awa.diop@example.sn', NOW() - INTERVAL '30 days', NOW()),
('22222222-2222-2222-2222-222222222222', 'Moussa', 'Ndiaye', '+221771000002', 'moussa.ndiaye@example.sn', NOW() - INTERVAL '25 days', NOW()),
('33333333-3333-3333-3333-333333333333', 'Fatou', 'Ba', '+221771000003', 'fatou.ba@example.sn', NOW() - INTERVAL '20 days', NOW()),
('44444444-4444-4444-4444-444444444444', 'Ibrahima', 'Sarr', '+221771000004', 'ibrahima.sarr@example.sn', NOW() - INTERVAL '15 days', NOW()),
('55555555-5555-5555-5555-555555555555', 'Khady', 'Fall', '+221771000005', 'khady.fall@example.sn', NOW() - INTERVAL '10 days', NOW());

-- Historical successes
INSERT INTO transactions (id, reference, amount, currency, provider, status, error_code, customer_id, created_at, updated_at) VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1', 'TX45890', 15000.00, 'XOF', 'WAVE', 'SUCCESS', NULL,
 '11111111-1111-1111-1111-111111111111', NOW() - INTERVAL '3 days', NOW() - INTERVAL '3 days' + INTERVAL '1 minute'),
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2', 'TX45891', 75000.00, 'XOF', 'ORANGE_MONEY', 'SUCCESS', NULL,
 '22222222-2222-2222-2222-222222222222', NOW() - INTERVAL '2 days', NOW() - INTERVAL '2 days' + INTERVAL '2 minutes');

-- CDC Case 1: TX45892 SUCCESS Wave 25000
INSERT INTO transactions (id, reference, amount, currency, provider, status, error_code, customer_id, created_at, updated_at) VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3', 'TX45892', 25000.00, 'XOF', 'WAVE', 'SUCCESS', NULL,
 '11111111-1111-1111-1111-111111111111',
 date_trunc('day', NOW()) + INTERVAL '14 hours 10 minutes',
 date_trunc('day', NOW()) + INTERVAL '14 hours 11 minutes');

-- Failed: insufficient balance (ERROR_105)
INSERT INTO transactions (id, reference, amount, currency, provider, status, error_code, customer_id, created_at, updated_at) VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa4', 'TX45893', 120000.00, 'XOF', 'WAVE', 'FAILED', 'ERROR_105',
 '33333333-3333-3333-3333-333333333333', NOW() - INTERVAL '5 hours', NOW() - INTERVAL '5 hours' + INTERVAL '30 seconds');

-- Pending for > 2 hours
INSERT INTO transactions (id, reference, amount, currency, provider, status, error_code, customer_id, created_at, updated_at) VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa5', 'TX45894', 45000.00, 'XOF', 'ORANGE_MONEY', 'PENDING', NULL,
 '44444444-4444-4444-4444-444444444444', NOW() - INTERVAL '3 hours', NOW() - INTERVAL '3 hours');

-- Today failures (Wave / Orange / Free)
INSERT INTO transactions (id, reference, amount, currency, provider, status, error_code, customer_id, created_at, updated_at) VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa6', 'TX45901', 8000.00, 'XOF', 'WAVE', 'FAILED', 'ERROR_105',
 '11111111-1111-1111-1111-111111111111', NOW() - INTERVAL '2 hours', NOW() - INTERVAL '2 hours'),
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa7', 'TX45902', 55000.00, 'XOF', 'ORANGE_MONEY', 'FAILED', 'ERROR_302',
 '22222222-2222-2222-2222-222222222222', NOW() - INTERVAL '90 minutes', NOW() - INTERVAL '90 minutes'),
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa8', 'TX45903', 99000.00, 'XOF', 'ORANGE_MONEY', 'FAILED', 'ERROR_503',
 '22222222-2222-2222-2222-222222222222', NOW() - INTERVAL '60 minutes', NOW() - INTERVAL '60 minutes'),
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa9', 'TX45904', 12500.00, 'XOF', 'FREE_MONEY', 'FAILED', 'ERROR_401',
 '55555555-5555-5555-5555-555555555555', NOW() - INTERVAL '45 minutes', NOW() - INTERVAL '45 minutes'),
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaa10', 'TX45905', 30000.00, 'XOF', 'WAVE', 'FAILED', 'ERROR_803',
 '33333333-3333-3333-3333-333333333333', NOW() - INTERVAL '30 minutes', NOW() - INTERVAL '30 minutes');

-- Orange Money > 50000 (mix success/failed)
INSERT INTO transactions (id, reference, amount, currency, provider, status, error_code, customer_id, created_at, updated_at) VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaa11', 'TX45910', 65000.00, 'XOF', 'ORANGE_MONEY', 'SUCCESS', NULL,
 '44444444-4444-4444-4444-444444444444', NOW() - INTERVAL '4 hours', NOW() - INTERVAL '4 hours' + INTERVAL '1 minute'),
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaa12', 'TX45911', 82000.00, 'XOF', 'ORANGE_MONEY', 'SUCCESS', NULL,
 '55555555-5555-5555-5555-555555555555', NOW() - INTERVAL '6 hours', NOW() - INTERVAL '6 hours' + INTERVAL '40 seconds');

-- Card rails
INSERT INTO transactions (id, reference, amount, currency, provider, status, error_code, customer_id, created_at, updated_at) VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaa13', 'TX45920', 150000.00, 'XOF', 'VISA', 'FAILED', 'ERROR_601',
 '11111111-1111-1111-1111-111111111111', NOW() - INTERVAL '1 day', NOW() - INTERVAL '1 day'),
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaa14', 'TX45921', 42000.00, 'XOF', 'MASTERCARD', 'SUCCESS', NULL,
 '22222222-2222-2222-2222-222222222222', NOW() - INTERVAL '8 hours', NOW() - INTERVAL '8 hours' + INTERVAL '20 seconds');

-- This week: customer with multiple failures (Moussa)
INSERT INTO transactions (id, reference, amount, currency, provider, status, error_code, customer_id, created_at, updated_at) VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaa15', 'TX45930', 18000.00, 'XOF', 'WAVE', 'FAILED', 'ERROR_105',
 '22222222-2222-2222-2222-222222222222', NOW() - INTERVAL '3 days', NOW() - INTERVAL '3 days'),
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaa16', 'TX45931', 22000.00, 'XOF', 'FREE_MONEY', 'FAILED', 'ERROR_702',
 '22222222-2222-2222-2222-222222222222', NOW() - INTERVAL '2 days', NOW() - INTERVAL '2 days'),
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaa17', 'TX45932', 5000.00, 'XOF', 'WAVE', 'CANCELLED', NULL,
 '33333333-3333-3333-3333-333333333333', NOW() - INTERVAL '12 hours', NOW() - INTERVAL '12 hours'),
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaa18', 'TX45933', 9000.00, 'XOF', 'ORANGE_MONEY', 'EXPIRED', NULL,
 '44444444-4444-4444-4444-444444444444', NOW() - INTERVAL '20 hours', NOW() - INTERVAL '18 hours');
