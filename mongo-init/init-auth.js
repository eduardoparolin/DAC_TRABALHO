db = db.getSiblingDB('users'); // Nome do banco

db.createCollection('user');

db.user.insertMany([
  // Clientes
  {
    email: "cli1@bantads.com.br",
    password: "$2a$10$M/l5HsgddsSw5QiDGWXzJOSaS8cYwTUkEm3ftBbwWpbpA0zXA8exu",
    cpf: "12912861012",
    role: "CLIENT"
  },
  {
    email: "cli2@bantads.com.br",
    password: "$2a$10$M/l5HsgddsSw5QiDGWXzJOSaS8cYwTUkEm3ftBbwWpbpA0zXA8exu",
    cpf: "09506382000",
    role: "CLIENT"
  },
  {
    email: "cli3@bantads.com.br",
    password: "$2a$10$M/l5HsgddsSw5QiDGWXzJOSaS8cYwTUkEm3ftBbwWpbpA0zXA8exu",
    cpf: "85733854057",
    role: "CLIENT"
  },
  {
    email: "cli4@bantads.com.br",
    password: "$2a$10$M/l5HsgddsSw5QiDGWXzJOSaS8cYwTUkEm3ftBbwWpbpA0zXA8exu",
    cpf: "58872160006",
    role: "CLIENT"
  },
  {
    email: "cli5@bantads.com.br",
    password: "$2a$10$M/l5HsgddsSw5QiDGWXzJOSaS8cYwTUkEm3ftBbwWpbpA0zXA8exu",
    cpf: "76179646090",
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
