\c client;

CREATE TABLE IF NOT EXISTS client (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    email VARCHAR(150) NOT NULL,
    cpf VARCHAR(11) NOT NULL UNIQUE,
    phone VARCHAR(20),
    salary NUMERIC(14,2) NOT NULL,
    status VARCHAR(50),
    rejection_reason VARCHAR(500),
    street VARCHAR(200) NOT NULL,
    number VARCHAR(10) NOT NULL,
    complement VARCHAR(100),
    zip_code VARCHAR(8) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(2) NOT NULL,
    account_id BIGINT UNIQUE,
    manager_id BIGINT,
    creation_date TIMESTAMPTZ,
    approval_date TIMESTAMPTZ
);

INSERT INTO client (
    name, email, cpf, phone, salary, status, street, number, zip_code, city, state, creation_date, approval_date, account_id, manager_id
) VALUES
('Catharyna', 'cli1@bantads.com.br', '12912861012', '11988880001', 10000.00, 'APROVADO',
 'Rua das Orquídeas', '128', '04001001', 'São Paulo', 'SP', NOW(), NOW(), 1, 1),
('Cleuddônio', 'cli2@bantads.com.br', '09506382000', '21977770002', 20000.00, 'APROVADO',
 'Avenida Rio Branco', '500', '20040002', 'Rio de Janeiro', 'RJ', NOW(), NOW(), 2, 2),
('Catianna', 'cli3@bantads.com.br', '85733854057', '31966660003', 3000.00, 'APROVADO',
 'Rua das Palmeiras', '45', '30120010', 'Belo Horizonte', 'MG', NOW(), NOW(), 3, 3),
('Cutardo', 'cli4@bantads.com.br', '58872160006', '41955550004', 500.00, 'APROVADO',
 'Travessa do Sol', '9', '80020020', 'Curitiba', 'PR', NOW(), NOW(), 4, 4),
('Coândrya', 'cli5@bantads.com.br', '76179646090', '41944440005', 1500.00, 'APROVADO',
 'Rua das Laranjeiras', '210', '80030030', 'Curitiba', 'PR', NOW(), NOW(), 5, 4);


