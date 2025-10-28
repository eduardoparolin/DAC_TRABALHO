
CREATE TABLE manager.manager (
     id BIGSERIAL PRIMARY KEY,
     cpf VARCHAR(255) NOT NULL,
     name VARCHAR(255) NOT NULL,
     email VARCHAR(255) NOT NULL UNIQUE,
     password VARCHAR(255) NOT NULL,
     type VARCHAR(50) NOT NULL
);
