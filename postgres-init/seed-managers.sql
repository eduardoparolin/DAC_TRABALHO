\c manager;

CREATE TABLE IF NOT EXISTS manager (
    id SERIAL PRIMARY KEY,
    cpf VARCHAR(11) NOT NULL,
    email VARCHAR(100) NOT NULL,
    name VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    account_count INTEGER NOT NULL DEFAULT 0,
);

INSERT INTO manager (id, cpf, email, name, password, type) VALUES
    (1, '11111111111', 'manager1@bank.com', 'João Silva', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'MANAGER'),
    (2, '22222222222', 'manager2@bank.com', 'Maria Santos', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'MANAGER'),
    (3, '33333333333', 'admin@bank.com', 'Admin User', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMINISTRATOR')
ON CONFLICT (id) DO NOTHING;
