import { Hono } from "hono";
import { Pool } from "pg";
import { MongoClient } from "mongodb";

const rebootRoutes = new Hono();

const postgresConfig = {
  host: process.env.POSTGRES_HOST || "localhost",
  port: Number(process.env.POSTGRES_PORT) || 5444,
  user: process.env.POSTGRES_USER || "postgres",
  password: process.env.POSTGRES_PASSWORD || "1234",
};

const mongoConfig = {
  url:
    process.env.MONGO_URL ||
    "mongodb://admin:1234@localhost:27017/users?authSource=admin",
};

rebootRoutes.get("/", async (c) => {
  try {
    const databases = [
      "client",
      "account_command",
      "account_query",
      "manager",
      "orchestrator",
    ];

    for (const dbName of databases) {
      const pool = new Pool({
        ...postgresConfig,
        database: dbName,
      });

      try {
        const tablesResult = await pool.query(`
          SELECT tablename FROM pg_tables
          WHERE schemaname = 'public'
        `);

        for (const row of tablesResult.rows) {
          const tableName = row.tablename;
          await pool.query(
            `TRUNCATE TABLE ${tableName} RESTART IDENTITY CASCADE`
          );
        }
      } catch (error) {
        throw error;
      } finally {
        await pool.end();
      }
    }

    // Senha hasheada para "tads"
    const passwordHash =
      "$2a$10$sR6De6BRV5FxSPS9tNm6wuGyOb9Snk0owmMgMA7mM2yTW4C6c/seq";

    const managerPool = new Pool({ ...postgresConfig, database: "manager" });
    try {
      await managerPool.query(`
        INSERT INTO manager (account_count, id, cpf, name, email, password, type) VALUES
        (2, 98574307084, '98574307084', 'Geniéve', 'ger1@bantads.com.br', '${passwordHash}', 'GERENTE'),
        (2, 64065268052, '64065268052', 'Godophredo', 'ger2@bantads.com.br', '${passwordHash}', 'GERENTE'),
        (1, 23862179060, '23862179060', 'Gyândula', 'ger3@bantads.com.br', '${passwordHash}', 'GERENTE')
      `);
    } finally {
      await managerPool.end();
    }

    try {
      const mongoClient = new MongoClient(mongoConfig.url);
      await mongoClient.connect();

      const db = mongoClient.db("users");

      const collections = await db.listCollections().toArray();
      for (const collection of collections) {
        await db.collection(collection.name).drop();
      }

      await db.collection("user").insertMany([
        {
          clientId: null,
          managerId: 40501740066,
          email: "adm1@bantads.com.br",
          password: passwordHash,
          role: "ADMINISTRADOR",
          cpf: "40501740066",
          name: "Adamântio",
        },
        {
          clientId: null,
          managerId: 23862179060,
          email: "ger3@bantads.com.br",
          password: passwordHash,
          role: "GERENTE",
          cpf: "23862179060",
          name: "Gyândula",
        },
        {
          clientId: null,
          managerId: 98574307084,
          email: "ger1@bantads.com.br",
          password: passwordHash,
          role: "GERENTE",
          cpf: "98574307084",
          name: "Geniéve",
        },
        {
          clientId: null,
          managerId: 64065268052,
          email: "ger2@bantads.com.br",
          password: passwordHash,
          role: "GERENTE",
          cpf: "64065268052",
          name: "Godophredo",
        },
      ]);
      await mongoClient.close();
    } catch (error) {
      throw error;
    }

    const clientPool = new Pool({ ...postgresConfig, database: "client" });
    try {
      await clientPool.query(`
        INSERT INTO client (name, email, cpf, phone, salary, status, street, complement, zip_code, city, state, manager_id, creation_date, approval_date) VALUES
        ('Catharyna', 'cli1@bantads.com.br', '12912861012', '41999999001', 10000.00, 'APROVADO', 'Rua das Flores', 'Apt 101', '80000000', 'Curitiba', 'PR', 98574307084, '2000-01-01', '2000-01-01'),
        ('Cleuddônio', 'cli2@bantads.com.br', '09506382000', '41999999002', 20000.00, 'APROVADO', 'Av. Principal', NULL, '80000001', 'Curitiba', 'PR', 64065268052, '1990-10-10', '1990-10-10'),
        ('Catianna', 'cli3@bantads.com.br', '85733854057', '41999999003', 3000.00, 'APROVADO', 'Rua Central', 'Casa', '80000002', 'Curitiba', 'PR', 23862179060, '2012-12-12', '2012-12-12'),
        ('Cutardo', 'cli4@bantads.com.br', '58872160006', '41999999004', 500.00, 'APROVADO', 'Rua Lateral', NULL, '80000003', 'Curitiba', 'PR', 98574307084, '2022-02-22', '2022-02-22'),
        ('Coândrya', 'cli5@bantads.com.br', '76179646090', '41999999005', 1500.00, 'APROVADO', 'Av. Secundária', 'Bloco A', '80000004', 'Curitiba', 'PR', 64065268052, '2025-01-01', '2025-01-01')
      `);
    } finally {
      await clientPool.end();
    }

    const accountCommandPool = new Pool({
      ...postgresConfig,
      database: "account_command",
    });
    try {
      await accountCommandPool.query(`
        INSERT INTO account (clientid, accountnumber, creationdate, balance, limitamount, managerid, status)
        VALUES
          (1, '1291', '2000-01-01', 800.00, 5000.00, 98574307084, 'ATIVA'),
          (2, '0950', '1990-10-10', -10000.00, 10000.00, 64065268052, 'ATIVA'),
          (3, '8573', '2012-12-12', -1000.00, 1500.00, 23862179060, 'ATIVA'),
          (4, '5887', '2022-02-22', 150000.00, 0.00, 98574307084, 'ATIVA'),
          (5, '7617', '2025-01-01', 1500.00, 0.00, 64065268052, 'ATIVA')
      `);
    } finally {
      await accountCommandPool.end();
    }

    const accountQueryPool = new Pool({
      ...postgresConfig,
      database: "account_query",
    });
    try {
      await accountQueryPool.query(`
        INSERT INTO account (id, accountnumber, balance, clientid, creationdate, limitamount, managerid, status)
        VALUES
          (1, '1291', 800.00, 1, '2000-01-01', 5000.00, 98574307084, 'ATIVA'),
          (2, '0950', -10000.00, 2, '1990-10-10', 10000.00, 64065268052, 'ATIVA'),
          (3, '8573', -1000.00, 3, '2012-12-12', 1500.00, 23862179060, 'ATIVA'),
          (4, '5887', 150000.00, 4, '2022-02-22', 0.00, 98574307084, 'ATIVA'),
          (5, '7617', 1500.00, 5, '2025-01-01', 0.00, 64065268052, 'ATIVA')
      `);
    } finally {
      await accountQueryPool.end();
    }

    const clientUpdatePool = new Pool({
      ...postgresConfig,
      database: "client",
    });
    try {
      await clientUpdatePool.query(`
        UPDATE client SET account_id = '1291' WHERE cpf = '12912861012';
        UPDATE client SET account_id = '0950' WHERE cpf = '09506382000';
        UPDATE client SET account_id = '8573' WHERE cpf = '85733854057';
        UPDATE client SET account_id = '5887' WHERE cpf = '58872160006';
        UPDATE client SET account_id = '7617' WHERE cpf = '76179646090';
      `);
    } finally {
      await clientUpdatePool.end();
    }

    const mongoClientFinal = new MongoClient(mongoConfig.url);
    try {
      await mongoClientFinal.connect();
      const db = mongoClientFinal.db("users");

      await db.collection("user").insertMany([
        {
          clientId: 1,
          managerId: null,
          email: "cli1@bantads.com.br",
          password: passwordHash,
          role: "CLIENTE",
          cpf: "12912861012",
          name: "Catharyna",
        },
        {
          clientId: 2,
          managerId: null,
          email: "cli2@bantads.com.br",
          password: passwordHash,
          role: "CLIENTE",
          cpf: "09506382000",
          name: "Cleuddônio",
        },
        {
          clientId: 3,
          managerId: null,
          email: "cli3@bantads.com.br",
          password: passwordHash,
          role: "CLIENTE",
          cpf: "85733854057",
          name: "Catianna",
        },
        {
          clientId: 4,
          managerId: null,
          email: "cli4@bantads.com.br",
          password: passwordHash,
          role: "CLIENTE",
          cpf: "58872160006",
          name: "Cutardo",
        },
        {
          clientId: 5,
          managerId: null,
          email: "cli5@bantads.com.br",
          password: passwordHash,
          role: "CLIENTE",
          cpf: "76179646090",
          name: "Coândrya",
        },
      ]);
      await mongoClientFinal.close();
    } catch (error) {
      throw error;
    }

    const transactionCommandPool = new Pool({
      ...postgresConfig,
      database: "account_command",
    });
    try {
      await transactionCommandPool.query(`
        INSERT INTO transaction (dateTime, type, source_account_number, target_account_number, amount)
        VALUES
          -- Transações de Catharyna
          ('2020-01-01 10:00:00+00', 'DEPOSITO', '1291', NULL, 1000.00),
          ('2020-01-01 11:00:00+00', 'DEPOSITO', '1291', NULL, 900.00),
          ('2020-01-01 12:00:00+00', 'SAQUE', '1291', NULL, 550.00),
          ('2020-01-01 13:00:00+00', 'SAQUE', '1291', NULL, 350.00),
          ('2020-01-10 15:00:00+00', 'DEPOSITO', '1291', NULL, 2000.00),
          ('2020-01-15 08:00:00+00', 'SAQUE', '1291', NULL, 500.00),
          ('2020-01-20 12:00:00+00', 'TRANSFERENCIA', '1291', '0950', 1700.00),

          -- Transações de Cleuddônio
          ('2025-01-01 12:00:00+00', 'DEPOSITO', '0950', NULL, 1000.00),
          ('2025-01-02 10:00:00+00', 'DEPOSITO', '0950', NULL, 5000.00),
          ('2025-01-10 10:00:00+00', 'SAQUE', '0950', NULL, 200.00),
          ('2025-02-05 10:00:00+00', 'DEPOSITO', '0950', NULL, 7000.00),

          -- Transações de Catianna
          ('2025-05-05 10:00:00+00', 'DEPOSITO', '8573', NULL, 1000.00),
          ('2025-05-06 10:00:00+00', 'SAQUE', '8573', NULL, 2000.00),

          -- Transações de Cutardo
          ('2025-06-01 10:00:00+00', 'DEPOSITO', '5887', NULL, 150000.00),

          -- Transações de Coândrya
          ('2025-07-01 10:00:00+00', 'DEPOSITO', '7617', NULL, 1500.00)
      `);
    } finally {
      await transactionCommandPool.end();
    }

    const transactionQueryPool = new Pool({
      ...postgresConfig,
      database: "account_query",
    });
    try {
      await transactionQueryPool.query(`
        INSERT INTO transaction (dateTime, type, source_account_number, target_account_number, amount)
        VALUES
          -- Transações de Catharyna
          ('2020-01-01 10:00:00+00', 'DEPOSITO', '1291', NULL, 1000.00),
          ('2020-01-01 11:00:00+00', 'DEPOSITO', '1291', NULL, 900.00),
          ('2020-01-01 12:00:00+00', 'SAQUE', '1291', NULL, 550.00),
          ('2020-01-01 13:00:00+00', 'SAQUE', '1291', NULL, 350.00),
          ('2020-01-10 15:00:00+00', 'DEPOSITO', '1291', NULL, 2000.00),
          ('2020-01-15 08:00:00+00', 'SAQUE', '1291', NULL, 500.00),
          ('2020-01-20 12:00:00+00', 'TRANSFERENCIA', '1291', '0950', 1700.00),

          -- Transações de Cleuddônio
          ('2025-01-01 12:00:00+00', 'DEPOSITO', '0950', NULL, 1000.00),
          ('2025-01-02 10:00:00+00', 'DEPOSITO', '0950', NULL, 5000.00),
          ('2025-01-10 10:00:00+00', 'SAQUE', '0950', NULL, 200.00),
          ('2025-02-05 10:00:00+00', 'DEPOSITO', '0950', NULL, 7000.00),

          -- Transações de Catianna
          ('2025-05-05 10:00:00+00', 'DEPOSITO', '8573', NULL, 1000.00),
          ('2025-05-06 10:00:00+00', 'SAQUE', '8573', NULL, 2000.00),

          -- Transações de Cutardo
          ('2025-06-01 10:00:00+00', 'DEPOSITO', '5887', NULL, 150000.00),

          -- Transações de Coândrya
          ('2025-07-01 10:00:00+00', 'DEPOSITO', '7617', NULL, 1500.00)
      `);
    } finally {
      await transactionQueryPool.end();
    }

    return c.json(
      {
        success: true,
        message: "Reboot Concluido",
      },
      200
    );
  } catch (error) {
    console.error("Reboot Falhou", error);
    return c.json(
      {
        success: false,
        error: "Reboot Falhou",
      },
      500
    );
  }
});

export { rebootRoutes };
