export const getManagersMock = [
  {
    gerente: {
      cpf: "40501740066",
      nome: "Geniéve",
      email: "ger1@bantads.com.br",
      tipo: "ADMINISTRADOR",
    },
    clientes: [
      {
        cliente: "40501740066",
        numero: "7811",
        saldo: 1099.21,
        limite: 5000,
        gerente: "23862179060",
        criacao: "2025-08-01T16:27:48-03:00",
      },
    ],
    saldo_positivo: 89123.22,
    saldo_negativo: -982.35,
  },
];

export const createManagerMock = {
  cpf: "40501740066",
  nome: "Geniéve",
  email: "ger1@bantads.com.br",
  tipo: "ADMINISTRADOR",
  senha: "tads",
};

export const getManagerByCPFMock = {
  cpf: "40501740066",
  nome: "Geniéve",
  email: "ger1@bantads.com.br",
  tipo: "ADMINISTRADOR",
};
