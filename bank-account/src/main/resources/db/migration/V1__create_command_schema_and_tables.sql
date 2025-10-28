

CREATE TABLE IF NOT EXISTS command_account.account (
    id            BIGSERIAL PRIMARY KEY,
    client_id     BIGINT,
    account_number VARCHAR(255),
    creation_date TIMESTAMP WITH TIME ZONE,
    balance       DECIMAL(19, 2),
    limit_amount  DECIMAL(19, 2),
    manager_id    BIGINT,
    status        VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS query_account.account (
    id            BIGINT PRIMARY KEY,
    client_id     BIGINT,
    account_number VARCHAR(255),
    balance       DECIMAL(19, 2),
    limit_amount  DECIMAL(19, 2),
    manager_id    BIGINT,
    creation_date TIMESTAMP WITH TIME ZONE,
    status        VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS command_account.transaction (
    id                      BIGSERIAL PRIMARY KEY,
    date_time               TIMESTAMP WITH TIME ZONE,
    type                    VARCHAR(50),
    source_account_number   VARCHAR(255) NOT NULL,
    target_account_number   VARCHAR(255),
    amount                  DECIMAL(19, 2)
);

CREATE TABLE IF NOT EXISTS query_account.transaction (
    id                      BIGINT PRIMARY KEY,
    date_time               TIMESTAMP WITH TIME ZONE,
    type                    VARCHAR(50),
    source_account_number   VARCHAR(255) NOT NULL,
    target_account_number   VARCHAR(255),
    amount                  DECIMAL(19, 2)
);