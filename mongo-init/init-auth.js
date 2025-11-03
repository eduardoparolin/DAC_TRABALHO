db = db.getSiblingDB('users'); // Nome do banco

db.createCollection('User');

db.usuarios.insertMany([
  // Clientes
  {
    email: "cli1@bantads.com.br",
    password: "$2a$10$M/l5HsgddsSw5QiDGWXzJOSaS8cYwTUkEm3ftBbwWpbpA0zXA8exu",
    role: "CLIENT"
  },
  {
    email: "cli2@bantads.com.br",
    password: "$2a$10$M/l5HsgddsSw5QiDGWXzJOSaS8cYwTUkEm3ftBbwWpbpA0zXA8exu",
    role: "CLIENT"
  },
  {
    email: "cli3@bantads.com.br",
    password: "$2a$10$M/l5HsgddsSw5QiDGWXzJOSaS8cYwTUkEm3ftBbwWpbpA0zXA8exu",
    role: "CLIENT"
  },
  {
    email: "cli4@bantads.com.br",
    password: "$2a$10$M/l5HsgddsSw5QiDGWXzJOSaS8cYwTUkEm3ftBbwWpbpA0zXA8exu",
    role: "CLIENT"
  },
  {
    email: "cli5@bantads.com.br",
    password: "$2a$10$M/l5HsgddsSw5QiDGWXzJOSaS8cYwTUkEm3ftBbwWpbpA0zXA8exu",
    role: "CLIENT"
  },

  // MANAGERs
  {
    email: "ger1@bantads.com.br",
    password: "$2a$10$M/l5HsgddsSw5QiDGWXzJOSaS8cYwTUkEm3ftBbwWpbpA0zXA8exu",
    role: "MANAGER"
  },
  {
    email: "ger2@bantads.com.br",
    password: "$2a$10$M/l5HsgddsSw5QiDGWXzJOSaS8cYwTUkEm3ftBbwWpbpA0zXA8exu",
    role: "MANAGER"
  },
  {
    email: "ger3@bantads.com.br",
    password: "$2a$10$M/l5HsgddsSw5QiDGWXzJOSaS8cYwTUkEm3ftBbwWpbpA0zXA8exu",
    role: "MANAGER"
  },

  // Administrador
  {
    email: "adm1@bantads.com.br",
    password: "$2a$10$M/l5HsgddsSw5QiDGWXzJOSaS8cYwTUkEm3ftBbwWpbpA0zXA8exu",
    role: "ADMIN"
  }
]);
