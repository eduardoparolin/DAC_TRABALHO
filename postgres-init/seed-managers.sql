-- Seed initial managers for the bank account service
\c manager;

INSERT INTO manager (id, cpf, email, name, password, type) VALUES
(1, '11111111111', 'manager1@bank.com', 'João Silva', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'MANAGER'),
(2, '22222222222', 'manager2@bank.com', 'Maria Santos', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'MANAGER'),
(3, '33333333333', 'admin@bank.com', 'Admin User', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMINISTRATOR')
ON CONFLICT (id) DO NOTHING;

SELECT setval('manager_id_seq', (SELECT MAX(id) FROM manager), true);
