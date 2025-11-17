\c manager;

CREATE TABLE IF NOT EXISTS manager (
    id SERIAL PRIMARY KEY,
    cpf VARCHAR(11) NOT NULL,
    email VARCHAR(100) NOT NULL,
    name VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    account_count INTEGER NOT NULL DEFAULT 0
);

INSERT INTO manager (id, cpf, email, name, password, type) VALUES
    (1, '98574307084', 'ger1@bantads.com.br', 'Geniéve', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'GERENTE'),
    (2, '64065268052', 'ger2@bantads.com.br', 'Godophredo', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'GERENTE'),
    (3, '23862179060', 'ger3@bantads.com.br', 'Gyândula', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'GERENTE'),
    (4, '40501740066', 'adm1@bantads.com.br', 'Adamântio', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMINISTRADOR')
ON CONFLICT (id) DO NOTHING;
