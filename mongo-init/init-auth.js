db = db.getSiblingDB('users'); // Nome do banco

db.createCollection('user');

db.user.insertMany([
  // Clientes
  {
    clientId: 1,
    email: "cli1@bantads.com.br",
    password: "$2a$10$M/l5HsgddsSw5QiDGWXzJOSaS8cYwTUkEm3ftBbwWpbpA0zXA8exu",
    cpf: "12912861012",
    name: "Catharyna",
    role: "CLIENT"
  },
  {
    clientId: 2,
    email: "cli2@bantads.com.br",
    password: "$2a$10$M/l5HsgddsSw5QiDGWXzJOSaS8cYwTUkEm3ftBbwWpbpA0zXA8exu",
    cpf: "09506382000",
    name: "Cleuddônio",
    role: "CLIENT"
  },
  {
    clientId: 3,
    email: "cli3@bantads.com.br",
    password: "$2a$10$M/l5HsgddsSw5QiDGWXzJOSaS8cYwTUkEm3ftBbwWpbpA0zXA8exu",
    cpf: "85733854057",
    name: "Catianna",
    role: "CLIENT"
  },
  {
    clientId: 4,
    email: "cli4@bantads.com.br",
    password: "$2a$10$M/l5HsgddsSw5QiDGWXzJOSaS8cYwTUkEm3ftBbwWpbpA0zXA8exu",
    cpf: "58872160006",
    name: "Cutardo",
    role: "CLIENT"
  },
  {
    clientId: 5,
    email: "cli5@bantads.com.br",
    password: "$2a$10$M/l5HsgddsSw5QiDGWXzJOSaS8cYwTUkEm3ftBbwWpbpA0zXA8exu",
    cpf: "76179646090",
    name: "Coândrya",
    role: "CLIENT"
  },

  // MANAGERs
  {
    managerId: 1,
    email: "ger1@bantads.com.br",
    password: "$2a$10$M/l5HsgddsSw5QiDGWXzJOSaS8cYwTUkEm3ftBbwWpbpA0zXA8exu",
    cpf: "98574307084",
    name: "Geniéve",
    role: "MANAGER"
  },
  {
    managerId: 2,
    email: "ger2@bantads.com.br",
    password: "$2a$10$M/l5HsgddsSw5QiDGWXzJOSaS8cYwTUkEm3ftBbwWpbpA0zXA8exu",
    cpf: "64065268052",
    name: "Godophredo",
    role: "MANAGER"
  },
  {
    managerId: 3,
    email: "ger3@bantads.com.br",
    password: "$2a$10$M/l5HsgddsSw5QiDGWXzJOSaS8cYwTUkEm3ftBbwWpbpA0zXA8exu",
    cpf: "23862179060",
    name: "Gyândula",
    role: "MANAGER"
  },

  // Administrador
  {
    managerId: 4,
    email: "adm1@bantads.com.br",
    password: "$2a$10$M/l5HsgddsSw5QiDGWXzJOSaS8cYwTUkEm3ftBbwWpbpA0zXA8exu",
    cpf: "40501740066",
    name: "Adamântio",
    role: "ADMIN"
  }
]);
