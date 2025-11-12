\c account_query;

CREATE TABLE IF NOT EXISTS account_query (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL,
    account_number VARCHAR(50) NOT NULL UNIQUE,
    creation_date TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    balance NUMERIC(14,2) NOT NULL DEFAULT 0,
    limit_amount NUMERIC(14,2) NOT NULL DEFAULT 0,
    manager_id BIGINT,
    status VARCHAR(50 ) NOT NULL
);

-- Inserts na tabela account
INSERT INTO account_query (client_id, account_number, creation_date, balance, limit_amount, manager_id, status)
VALUES
(12912861012, '1291', '2000-01-01T00:00:00Z', 800.00, 5000.00, 1, 'ATIVA'),

(09506382000, '0950', '1990-10-10T00:00:00Z', -10000.00, 10000.00, 2, 'ATIVA'),

(85733854057, '8573', '2012-12-12T00:00:00Z', -1000.00, 1500.00, 3, 'ATIVA'),

(58872160006, '5887', '2022-02-22T00:00:00Z', 150000.00, 0.00, 1, 'ATIVA'),

(76179646090, '7617', '2025-01-01T00:00:00Z', 1500.00, 0.00, 4, 'ATIVA');

CREATE TABLE IF NOT EXISTS transaction (
    id BIGSERIAL PRIMARY KEY,
    dateTime TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    type VARCHAR(50) NOT NULL,
    source_account_number VARCHAR(50) NOT NULL,
    target_account_number VARCHAR(50),
    amount NUMERIC(14,2) NOT NULL
);

-- =============================
-- Transações de Catharyna
-- =============================
INSERT INTO transaction (dateTime, type, source_account_number, target_account_number, amount)
VALUES
('2020-01-01 10:00:00+00', 'DEPÓSITO', '1291', NULL, 1000.00),
('2020-01-01 11:00:00+00', 'DEPÓSITO', '1291', NULL, 900.00),
('2020-01-01 12:00:00+00', 'SAQUE', '1291', NULL, 550.00),
('2020-01-01 13:00:00+00', 'SAQUE', '1291', NULL, 350.00),
('2020-01-10 15:00:00+00', 'DEPÓSITO', '1291', NULL, 2000.00),
('2020-01-15 08:00:00+00', 'SAQUE', '1291', NULL, 500.00),
('2020-01-20 12:00:00+00', 'TRANSFERÊNCIA', '1291', '0950', 1700.00);

-- =============================
-- Transações de Cleuddônio
-- =============================
INSERT INTO transaction (dateTime, type, source_account_number, target_account_number, amount)
VALUES
('2025-01-01 12:00:00+00', 'DEPÓSITO', '0950', NULL, 1000.00),
('2025-01-02 10:00:00+00', 'DEPÓSITO', '0950', NULL, 5000.00),
('2025-01-10 10:00:00+00', 'SAQUE', '0950', NULL, 200.00),
('2025-02-05 10:00:00+00', 'DEPÓSITO', '0950', NULL, 7000.00);

-- =============================
-- Transações de Catianna
-- =============================
INSERT INTO transaction (dateTime, type, source_account_number, target_account_number, amount)
VALUES
('2025-05-05 10:00:00+00', 'DEPÓSITO', '8573', NULL, 1000.00),
('2025-06-06 10:00:00+00', 'SAQUE', '8573', NULL, 2000.00);

-- =============================
-- Transações de Cutardo
-- =============================
INSERT INTO transaction (dateTime, type, source_account_number, target_account_number, amount)
VALUES
('2025-06-01 10:00:00+00', 'DEPÓSITO', '5887', NULL, 150000.00);

-- =============================
-- Transações de Coândrya
-- =============================
INSERT INTO transaction (dateTime, type, source_account_number, target_account_number, amount)
VALUES
('2025-07-01 10:00:00+00', 'DEPÓSITO', '7617', NULL, 1500.00);
